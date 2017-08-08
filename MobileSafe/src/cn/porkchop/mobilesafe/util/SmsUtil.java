package cn.porkchop.mobilesafe.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;

import cn.porkchop.mobilesafe.activity.AdvancedToolsActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.widget.Toast;

public class SmsUtil {
	private static char SPECIALCHAR = 'ē';

	public interface SmsBackupRestoreListener {
		void onShow();

		void onDismiss();

		void setMax(int max);

		void setProgress(int currentProgress);
	}

	private class SmsData {
		public List<SmsBackup> smsbackup;

		public class SmsBackup {
			public String address;
			public long date;
			public String body;
			public long type;
		}
	}

	/**
	 * 把引号转换成特殊字符,放置破坏json的格式
	 * 
	 * @author nanamiporkchop
	 * @time 2017-7-28 下午7:43:57
	 * @param string
	 * @return
	 */
	private static String convertQuotationToSpecial(String string) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			if (string.charAt(i) == '"') {
				sb.append(SPECIALCHAR);
			} else {
				sb.append(string.charAt(i));
			}
		}
		return sb.toString();
	}

	/**
	 * 把特殊字符转换成引号
	 * 
	 * @author nanamiporkchop
	 * @time 2017-7-28 下午8:03:28
	 * @param string
	 * @return
	 */
	private static String convertSpecialToQuotation(String string) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			if (string.charAt(i) == SPECIALCHAR) {
				sb.append('"');
			} else {
				sb.append(string.charAt(i));
			}
		}
		return sb.toString();
	}

	/**
	 * 备份短信,
	 * 
	 * @author nanamiporkchop
	 * @time 2017-7-28 下午8:03:49
	 * @param context
	 *            是activity类型,为了调用runOnUiThread方法
	 * @param listener
	 * @throws Exception
	 */
	public static void smsBackup(final Activity context,
			final SmsBackupRestoreListener listener) throws Exception {
		class Progress {
			int p;
		}
		class TempFile {
			File file;
		}
		final Progress progress = new Progress();
		final TempFile tempFile = new TempFile();
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// 检查是否挂载了sd卡
			if (Environment.getExternalStorageDirectory().getFreeSpace() < 5 * 1024 * 1024) {
				// 如果sd卡容量小于5m
				throw new Exception("sd卡容量不够!");
			} else {
				tempFile.file = new File(
						Environment.getExternalStorageDirectory(),
						"smsbackup.json");
			}
		} else {
			throw new Exception("sd卡未挂载成功,或者没有插入sd卡,请检查");
		}
		// 获得这个文件的输出流
		final PrintWriter out = new PrintWriter(tempFile.file);
		out.write("{\"smsbackup\":[");
		out.flush();
		Uri uri = Uri.parse("content://sms");
		final Cursor cursor = context.getContentResolver().query(uri,
				new String[] { "address", "date", "body", "type" }, null, null,
				null);
		listener.setMax(cursor.getCount());
		listener.onShow();
		// 开启另一个线程吧短信写入文件
		new Thread() {
			public void run() {
				StringBuilder sb = new StringBuilder();
				if (cursor.getCount() == 0) {
					// 关闭进度条
					context.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							listener.onDismiss();
						}
					});
					// 没有短信来备份
					DataUtil.showToast(context, "没有可备份的短信!");
					// 关闭
					cursor.close();
					out.close();
					// 删除创建的文件
					tempFile.file.delete();
					return;
				}
				while (cursor.moveToNext()) {
					sb.append("{");
					sb.append("\"address\":\"" + cursor.getString(0) + "\",");
					sb.append("\"date\":\"" + cursor.getString(1) + "\",");
					//先加密再转换特殊字符
					sb.append("\"body\":\""
							+ convertQuotationToSpecial(EncryptUtil.encrypt(cursor.getString(2), SettingConstant.SEED))
							+ "\",");
					sb.append("\"type\":\"" + cursor.getString(3) + "\"}");
					if (cursor.isLast()) {
						sb.append("]}");
					} else {
						sb.append(",");
					}
					// 更新进度
					SystemClock.sleep(50);
					listener.setProgress(++progress.p);
				}
				// 关闭进度条
				context.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						listener.onDismiss();
						Toast.makeText(context, "短信备份成功", 0).show();
					}
				});
				// 写入到文件
				out.write(sb.toString());
				// 关闭
				cursor.close();
				out.close();
			}
		}.start();
	}
	/**
	 * 短信恢复,安卓5.0以后只有系统短信app才能写入到数据库
	 * @author nanamiporkchop
	 * @time 2017-7-28 下午10:27:19
	 * @param context
	 * @param listener
	 * @throws Exception
	 */
	public static void smsRestore(final Activity context,
			final SmsBackupRestoreListener listener) throws Exception {
		// 判断sd卡是否挂载
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			throw new Exception("sd卡未挂载成功,或者没有插入sd卡,请检查");
		}
		File file = new File(Environment.getExternalStorageDirectory(),
				"smsbackup.json");
		// 判断文件是否存在
		if (!file.exists()) {
			throw new Exception("请先备份短信");
		}
		String json = streamToString(new FileInputStream(file));
		Gson gson = new Gson();
		final SmsData smsData = gson.fromJson(json, SmsData.class);
		// 设置dialog的大小
		listener.setMax(smsData.smsbackup.size());
		// 显示progressbar
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				listener.onShow();
			}
		});
		final Uri uri = Uri.parse("content://sms");
		new Thread() {
			public void run() {
				for (int i = 0; i < smsData.smsbackup.size(); i++) {
					cn.porkchop.mobilesafe.util.SmsUtil.SmsData.SmsBackup smsBackup = smsData.smsbackup.get(i);
					ContentValues values = new ContentValues();
					values.put("address", smsBackup.address);
					values.put("date", smsBackup.date);
					values.put("body",EncryptUtil.decrypt(convertSpecialToQuotation(smsBackup.body), SettingConstant.SEED));
					System.out.println(EncryptUtil.decrypt(convertSpecialToQuotation(smsBackup.body), SettingConstant.SEED));
					values.put("type", smsBackup.type);
					listener.setProgress(i + 1);
					context.getContentResolver().insert(uri, values);
				}
				// 关闭进度条对话框
				context.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						listener.onDismiss();
						Toast.makeText(context, "短信恢复完成", 0).show();
					}
				});
			}
		}.start();
	}

	private static String streamToString(InputStream is) {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		try {
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}

package cn.porkchop.mobilesafe.activity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAKey;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import cn.porkchop.mobilesafe.R;
import cn.porkchop.mobilesafe.R.id;
import cn.porkchop.mobilesafe.R.layout;
import cn.porkchop.mobilesafe.R.menu;
import cn.porkchop.mobilesafe.util.DataUtil;
import cn.porkchop.mobilesafe.util.SettingConstant;
import cn.porkchop.mobilesafe.util.SPUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SplashActivity extends Activity {

	protected static final int STARTHOMEACTIVITY = 0;
	protected static final int EXCEPTION = 1;
	private static final int DOWNLOADFAIL = 2;
	protected static final int NEEDUPDATE = 3;
	private TextView tv_versioncode;
	private TextView tv_versionname;
	private RelativeLayout linearLayout;
	private int mVersionCode;
	private UpdateInfo ui;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initView();
		initData();
		initAnimation();
		
		//********测试代码
		SPUtil.putBoolean(this, SettingConstant.FILENAME,
				SettingConstant.ISAUTOUPDATE, false);
		//********测试代码
	}

	private void initView() {
		setContentView(R.layout.activity_splash);
		tv_versioncode = (TextView) findViewById(R.id.tv_splash_versioncode);
		tv_versionname = (TextView) findViewById(R.id.tv_splash_versionname);
		linearLayout = (RelativeLayout) findViewById(R.id.linearLayout);
	}

	/**
	 * 获得版本信息
	 * 
	 * @author nanamiporkchop
	 * @time 2017-6-18 下午12:42:05
	 */
	private void initData() {
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), 0);
			mVersionCode = packageInfo.versionCode;
			String versionName = packageInfo.versionName;
			// 显示版本信息到ui
			tv_versionname.setText(versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 集合动画
	 * 
	 * @author nanamiporkchop
	 * @time 2017-6-17 下午11:20:26
	 */
	private void initAnimation() {

		// 旋转动画
		RotateAnimation ra = new RotateAnimation(0.0f, 360.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ra.setDuration(2000);
		ra.setFillAfter(true);

		// 缩放动画
		ScaleAnimation sa = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		sa.setDuration(2000);
		sa.setFillAfter(true);

		// 透明度动画
		AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
		aa.setDuration(2000);
		aa.setFillAfter(true);

		// 添加到集合
		AnimationSet as = new AnimationSet(false);
		as.addAnimation(aa);
		as.addAnimation(sa);
		as.addAnimation(ra);

		// 目标view设置动画
		linearLayout.setAnimation(as);

		// 绑定动画事件
		as.setAnimationListener(new AnimationListener() {
			long mStartTime;
			long mEndTime;

			/**
			 * 如果自动更新,就在动画开始的时候联网更新,更新完后打开home界面
			 */
			@Override
			public void onAnimationStart(Animation animation) {
				mStartTime = System.currentTimeMillis();
				if (SPUtil.getBoolean(SplashActivity.this,
						SettingConstant.FILENAME, SettingConstant.ISAUTOUPDATE,
						false)) {

					new Thread() {
						public void run() {
							checkUpdate();
						}
					}.start();
				}
			}

			/**
			 * 检查是否有更新
			 * 
			 * @author nanamiporkchop
			 * @time 2017-6-18 下午1:47:02
			 */
			private void checkUpdate() {
				Message message = Message.obtain();
				try {
					URL url = new URL(getResources().getString(
							R.string.updateInfoUrl));
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setConnectTimeout(5000);
					conn.setRequestMethod("GET");
					int code = conn.getResponseCode();
					if (code == 200) {
						InputStream is = conn.getInputStream();
						UpdateInfo ui = parseResponseSteam(is);
						// 判断当前版本号是否是最新
						if (mVersionCode == ui.versionCode) {
							// 相同直接进入home界面
							message.what = STARTHOMEACTIVITY;
						} else {
							message.what = NEEDUPDATE;
						}
					}
				} catch (MalformedURLException e) {
					message.what = EXCEPTION;
					message.obj = e;
				} catch (NotFoundException e) {
					message.what = EXCEPTION;
					message.obj = e;
				} catch (IOException e) {
					message.what = EXCEPTION;
					message.obj = e;
				} catch (JSONException e) {
					message.what = EXCEPTION;
					message.obj = e;
				}
				mEndTime = System.currentTimeMillis();
				// 如果动画还没播放完,就等动画放完
				if (mEndTime - mStartTime < 2000) {
					try {
						Thread.sleep(2000 - (mEndTime - mStartTime));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				handler.sendMessage(message);
			}

			private UpdateInfo parseResponseSteam(InputStream is)
					throws IOException, JSONException {
				StringBuilder sb = new StringBuilder();
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				String line = br.readLine();
				while (line != null) {
					sb.append(line);
					line = br.readLine();
				}

				return parseJSON(sb.toString());
			}

			/**
			 * 解析json并封装到javabean中
			 * 
			 * @author nanamiporkchop
			 * @time 2017-6-18 下午2:37:53
			 * @param string
			 * @return
			 * @throws JSONException
			 */
			private UpdateInfo parseJSON(String string) throws JSONException {
				ui = new UpdateInfo();
				JSONObject jo = new JSONObject(string);
				ui.description = jo.getString("description");
				ui.downloadUrl = jo.getString("downloadUrl");
				ui.versionCode = jo.getInt("versioncode");
				return ui;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			/**
			 * 如果不自动更新,就在动画结束的时候打开home界面
			 */
			@Override
			public void onAnimationEnd(Animation animation) {
				if (!SPUtil.getBoolean(SplashActivity.this,
						SettingConstant.FILENAME, SettingConstant.ISAUTOUPDATE,
						false)) {
					startHomeActivity();
				}
			}
		});
	}

	class UpdateInfo {
		String downloadUrl;
		String description;
		int versionCode;

		@Override
		public String toString() {
			return "UpdateInfo [downloadUrl=" + downloadUrl + ", description="
					+ description + ", versionCode=" + versionCode + "]";
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case STARTHOMEACTIVITY:
				startHomeActivity();
				break;
			case EXCEPTION:
				Exception e = (Exception) msg.obj;
				DataUtil.showDebugInfo(SplashActivity.this, e.getMessage());
				startHomeActivity();
				break;
			case DOWNLOADFAIL:
				DataUtil.showDebugInfo(SplashActivity.this, "下载失败");
				startHomeActivity();
				break;
			case NEEDUPDATE:
				showIsUpdateDialog();
				break;
			}
		}
	};

	/**
	 * 打开主界面
	 * 
	 * @author nanamiporkchop
	 * @time 2017-6-18 下午4:11:59
	 */
	private void startHomeActivity() {
		startActivity(new Intent(this, HomeActivity.class));
		finish();
	}

	/**
	 * 检查到有最新版后,提示是否更新,更新则下载,不更新则进入home界面
	 * 
	 * @author nanamiporkchop
	 * @time 2017-6-18 下午4:26:17
	 */
	protected void showIsUpdateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提醒");
		builder.setMessage("有新版本,是否下载?\n更新内容:" + ui.description);
		builder.setPositiveButton("下载", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				downloadLastVersionApk();
			}
		}).setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				startHomeActivity();
			}
		}).setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				startHomeActivity();
			}
		});
		builder.show();
	}

	/**
	 * 下载服务器上的apk,并提示用户是否安装
	 * 
	 * @author nanamiporkchop
	 * @time 2017-6-18 下午4:04:42
	 * @param ui
	 */
	private void downloadLastVersionApk() {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.download(ui.downloadUrl, Environment
				.getExternalStorageDirectory().getAbsolutePath()
				+ "/mobilesafe.apk", new RequestCallBack<File>() {

			@Override
			public void onSuccess(ResponseInfo<File> arg0) {
				showInstallDialog();
			}

			private void showInstallDialog() {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				intent.addCategory("android.intent.category.DEFAULT");
				File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"mobilesafe.apk");
				intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
				startActivityForResult(intent,1);
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Message msg = Message.obtain();
				msg.what = DOWNLOADFAIL;
				handler.sendMessage(msg);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		startHomeActivity();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

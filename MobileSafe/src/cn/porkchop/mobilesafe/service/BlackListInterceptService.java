package cn.porkchop.mobilesafe.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import cn.porkchop.mobilesafe.dao.BlackListDao;
import cn.porkchop.mobilesafe.dao.CallLogDao;
import cn.porkchop.mobilesafe.db.BlackListDB;
import cn.porkchop.mobilesafe.util.DataUtil;
import android.R.integer;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

public class BlackListInterceptService extends Service {
	BlackListDao blackListDao = new BlackListDao(this);
	CallLogDao callLogDao = new CallLogDao();
	private SmSReceiver smSReceiver;
	private TelephonyManager mTelephoneManager;
	private PhoneStateListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		registerSmsIntercept();
		registerTelephoneIntercept();
	}

	private void registerTelephoneIntercept() {
		mTelephoneManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		listener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				// 电话铃声响的时候,如果来电是需要拦截的号码,就挂断
				if (state == TelephonyManager.CALL_STATE_RINGING
						&& (blackListDao.findModeByPhone(incomingNumber) & BlackListDB.PHONE_MODE) != 0) {
					// 用内容观察者删除来点记录
					registerCallLogContentObserver(incomingNumber);
					// 挂断电话
					try {
						Class<?> clazz = Class
								.forName("android.os.ServiceManager");
						Method method = clazz.getDeclaredMethod("getService",
								String.class);
						IBinder iBinder = (IBinder) method.invoke(null,
								TELEPHONY_SERVICE);
						ITelephony iTelephony = ITelephony.Stub
								.asInterface(iBinder);
						iTelephony.endCall();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			/**
			 * 用内容观察者删除来电记录
			 * 
			 * @author nanamiporkchop
			 * @time 2017-7-19 下午1:47:49
			 * @param incomingNumber
			 */
			private void registerCallLogContentObserver(
					final String incomingNumber) {
				Uri uri = Uri.parse("content://call_log/calls");
				getContentResolver().registerContentObserver(uri, true,
						new ContentObserver(new Handler()) {
							@Override
							public void onChange(boolean selfChange) {
								callLogDao.deleteCallLogByPhone(
										BlackListInterceptService.this,
										incomingNumber);
								// 解绑内容观察者
								getContentResolver().unregisterContentObserver(
										this);
							}
						});
			}

		};
		mTelephoneManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	/**
	 * 注册短信接收者,用于拦截广播
	 * 
	 * @author nanamiporkchop
	 * @time 2017-7-19 上午11:27:27
	 */
	private void registerSmsIntercept() {
		smSReceiver = new SmSReceiver();
		IntentFilter filter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(smSReceiver, filter);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(smSReceiver);
		mTelephoneManager.listen(listener, PhoneStateListener.LISTEN_NONE);
	}

	private class SmSReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Object[] smsDatas = (Object[]) intent.getExtras().get("pdus");
			for (Object data : smsDatas) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) data);
				String phone = smsMessage.getDisplayOriginatingAddress();
				if ((blackListDao.findModeByPhone(phone) & BlackListDB.SMS_MODE) != 0) {
					abortBroadcast();
					System.out.println("拦截来自" + phone + "的短信");
				}
			}
		}
	}
}

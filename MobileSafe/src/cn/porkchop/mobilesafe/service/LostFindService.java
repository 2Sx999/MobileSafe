package cn.porkchop.mobilesafe.service;

import java.util.List;

import cn.porkchop.mobilesafe.activity.LostFindStep4Activity;
import cn.porkchop.mobilesafe.receiver.MyDeviceAdminReceiver;
import cn.porkchop.mobilesafe.util.SPUtil;
import cn.porkchop.mobilesafe.util.SettingConstant;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

public class LostFindService extends Service {
	DevicePolicyManager mDPM;
	ComponentName mDeviceAdminSample;
	private SmsReceiver smsReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// 注册广播拦截的广播接收者
		smsReceiver = new SmsReceiver();
		IntentFilter filter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		// 设置优先级
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(smsReceiver, filter);
		super.onCreate();

		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mDeviceAdminSample = new ComponentName(this,
				MyDeviceAdminReceiver.class);
	}

	private class SmsReceiver extends BroadcastReceiver {
		MediaPlayer player = new MediaPlayer();

		@Override
		public void onReceive(Context context, Intent intent) {
			Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
			for (Object message : messages) {
				SmsMessage sms = SmsMessage.createFromPdu((byte[]) message);
				// 获取短信内容
				String body = sms.getMessageBody();
				if ("#*gps*#".equals(body)) {
					getLocation();
					abortBroadcast();
				} else if ("#*lockscreen*#".equals(body)) {
					System.out.println("#*lockscreen*#");
					mDPM.lockNow();
					mDPM.resetPassword("1234", DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
					abortBroadcast();
				} else if ("#*wipedata*#".equals(body)) {
					System.out.println("#*wipedata*#");
					mDPM.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
					abortBroadcast();
				} else if ("#*music*#".equals(body)) {
					abortBroadcast();
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(smsReceiver);
		super.onDestroy();
	}

	private void getLocation() {
		final LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		List<String> allProviders = lm.getAllProviders();
		lm.requestLocationUpdates("gps", 0, 0, new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLocationChanged(Location location) {
				// 获得位置信息
				float accuracy = location.getAccuracy();
				double latitude = location.getLatitude();
				double longitude = location.getLongitude();
				double altitude = location.getAltitude();

				// 给安全号码发送短信
				String safeNumber = SPUtil.getString(getApplicationContext(),
						SettingConstant.FILENAME, SettingConstant.SAFENUMBER,
						"110");
				SmsManager manager = SmsManager.getDefault();
				manager.sendTextMessage(safeNumber, null, "accuracy : "
						+ accuracy + "\nlatitude : " + latitude
						+ "\nlongitutde : " + longitude + "\naltitude : "
						+ altitude, null, null);
				lm.removeUpdates(this);
			}
		});
	}
}

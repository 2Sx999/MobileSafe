package cn.porkchop.mobilesafe.service;

import java.util.List;

import cn.porkchop.mobilesafe.model.AppInfo;
import cn.porkchop.mobilesafe.util.ProcessInfoUtil;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class LockScreenClearProcessService extends Service {

	private ScreenLockReceiver screenLockReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		screenLockReceiver = new ScreenLockReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(screenLockReceiver, filter);
	}

	private class ScreenLockReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			List<AppInfo> allRunningAppInfo = ProcessInfoUtil
					.getAllRunningAppInfo(getApplicationContext());
			for (AppInfo appInfo : allRunningAppInfo) {
				if (appInfo.getPackName().equals(getPackageName())) {
					continue;
				}
				activityManager.killBackgroundProcesses(appInfo.getPackName());
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(screenLockReceiver);
	}
}

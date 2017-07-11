package cn.porkchop.mobilesafe.util;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtil {
	public static boolean isServiceRunning(Context context, String serviceName) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> list = am.getRunningServices(100);
		for (RunningServiceInfo rsi : list) {
			if (rsi.service.getClassName().equals(serviceName)) {
				return true;
			}
		}
		return false;
	}
}

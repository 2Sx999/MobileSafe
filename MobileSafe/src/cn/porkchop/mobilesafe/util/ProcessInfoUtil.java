package cn.porkchop.mobilesafe.util;

import java.util.ArrayList;
import java.util.List;

import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import cn.porkchop.mobilesafe.model.AppInfo;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class ProcessInfoUtil {
	/**
	 * 获得MemoryInfo,从而获取内存信息
	 * 
	 * @author nanamiporkchop
	 * @time 2017-8-6 下午12:08:15
	 * @param context
	 * @return
	 */
	public static MemoryInfo getMemoryInfo(Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo memoryInfo = new MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);
		return memoryInfo;
	}

	/**
	 * 得到所有运行的进程的信息
	 * 
	 * @author nanamiporkchop
	 * @time 2017-8-6 下午12:08:45
	 * @return
	 */
	public static List<AppInfo> getAllRunningAppInfo(Context context) {
		List<AppInfo> list = new ArrayList<AppInfo>();
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<AndroidAppProcess> processInfos = ProcessManager
				.getRunningAppProcesses();
		for (AndroidAppProcess androidAppProcess : processInfos) {
			AppInfo appInfo = new AppInfo();
			// 获得进程名
			appInfo.setPackName(androidAppProcess.name);
			// 获得内存信息
			android.os.Debug.MemoryInfo[] memoryInfos = activityManager
					.getProcessMemoryInfo(new int[] { androidAppProcess.pid });
			long memsize = memoryInfos[0].getTotalPrivateDirty() * 1024L;
			appInfo.setMemSize(memsize);
			try {
				appInfo = AppInfoUtil.getAppInfoByPackName(context, appInfo);
			} catch (NameNotFoundException e) {
				continue;
			}
			if (appInfo.getIcon() == null) {
				continue;
			}
			list.add(appInfo);
		}
		return list;
	}
}

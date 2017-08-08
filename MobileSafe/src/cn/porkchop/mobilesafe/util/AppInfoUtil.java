package cn.porkchop.mobilesafe.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.porkchop.mobilesafe.model.AppInfo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

public class AppInfoUtil {
	/**
	 * @return 手机总共内存
	 */
	public static long getPhoneTotalMem() {
		File dataDirectory = Environment.getDataDirectory();
		return dataDirectory.getTotalSpace();
	}

	/**
	 * @return 手机可用内存
	 */
	public static long getPhoneAvailMem() {
		File dataDirectory = Environment.getDataDirectory();
		return dataDirectory.getFreeSpace();
	}

	/**
	 * @return sd总共内存
	 */
	public static long getSDTotalMem() {
		File dataDirectory = Environment.getExternalStorageDirectory();
		return dataDirectory.getTotalSpace();
	}

	/**
	 * @return sd可用内存
	 */
	public static long getSDAvailMem() {
		File dataDirectory = Environment.getExternalStorageDirectory();
		return dataDirectory.getFreeSpace();
	}

	/**
	 * 得到所有安装的app信息,封装到bean中
	 * 
	 * @author nanamiporkchop
	 * @time 2017-8-1 下午2:52:09
	 * @param context
	 * @return
	 */
	public static List<AppInfo> getAllInstalledAppInfo(Context context) {
		List<AppInfo> list = new ArrayList<AppInfo>();
		PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> installedPackages = packageManager
				.getInstalledPackages(0);
		for (PackageInfo packageInfo : installedPackages) {
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			AppInfo appInfo = new AppInfo();
			// 包名
			appInfo.setPackName(applicationInfo.packageName);
			// 图标
			appInfo.setIcon(applicationInfo.loadIcon(packageManager));
			// 名字
			appInfo.setAppName(applicationInfo.loadLabel(packageManager) + "");
			if ((applicationInfo.flags & applicationInfo.FLAG_SYSTEM) != 0) {
				// 是系统app
				appInfo.setSystem(true);
			}
			if ((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
				// 安装在sd卡中
				appInfo.setSD(true);
			}
			// 路径
			appInfo.setSourceDir(applicationInfo.sourceDir);
			// 安装文件大小
			appInfo.setSize(new File(applicationInfo.sourceDir).length());
			appInfo.setUid(applicationInfo.uid);
			list.add(appInfo);
		}
		return list;
	}

	public static AppInfo getAppInfoByPackName(Context context, AppInfo appInfo) throws NameNotFoundException {
		PackageManager packageManager = context.getPackageManager();
		ApplicationInfo applicationInfo = packageManager.getApplicationInfo(
				appInfo.getPackName(), 0);
		// 图标
		appInfo.setIcon(applicationInfo.loadIcon(packageManager));
		// 名字
		appInfo.setAppName(applicationInfo.loadLabel(packageManager) + "");
		if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
			// 系统app
			appInfo.setSystem(true);
		}
		if ((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
			// 安装在sd卡中
			appInfo.setSD(true);
		}
		// 路径
		appInfo.setSourceDir(applicationInfo.sourceDir);
		// 安装文件的大小
		appInfo.setSize(new File(applicationInfo.sourceDir).length());
		appInfo.setUid(applicationInfo.uid);
		return appInfo;
	}
}

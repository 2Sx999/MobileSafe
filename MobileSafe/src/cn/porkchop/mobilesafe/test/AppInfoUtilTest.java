package cn.porkchop.mobilesafe.test;

import cn.porkchop.mobilesafe.util.AppInfoUtil;
import android.test.AndroidTestCase;

public class AppInfoUtilTest extends AndroidTestCase {
	public void test() {
		System.out.println(AppInfoUtil.getAllInstalledAppInfo(getContext()));
	}
}

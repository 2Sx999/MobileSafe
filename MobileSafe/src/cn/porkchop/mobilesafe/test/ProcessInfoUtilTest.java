package cn.porkchop.mobilesafe.test;

import cn.porkchop.mobilesafe.util.ProcessInfoUtil;
import android.app.ActivityManager.MemoryInfo;
import android.test.AndroidTestCase;

public class ProcessInfoUtilTest extends AndroidTestCase {
	public void test() {
		System.out.println(ProcessInfoUtil.getAllRunningAppInfo(getContext()));
	}
}

package cn.porkchop.mobilesafe.test;

import cn.porkchop.mobilesafe.util.EncryptUtil;
import cn.porkchop.mobilesafe.util.SettingConstant;
import android.test.AndroidTestCase;

public class EncryptUtilTest extends AndroidTestCase {
	public void test() throws Exception {
		System.out.println(EncryptUtil.decrypt(
				EncryptUtil.encrypt("なぬ", SettingConstant.SEED),
				SettingConstant.SEED));
	}
}

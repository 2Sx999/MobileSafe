package cn.porkchop.mobilesafe.test;

import java.util.List;

import cn.porkchop.mobilesafe.dao.BlackListDao;
import cn.porkchop.mobilesafe.dao.CallLogDao;
import cn.porkchop.mobilesafe.dao.ContactDao;
import cn.porkchop.mobilesafe.dao.SmsDao;
import cn.porkchop.mobilesafe.db.BlackListDB;
import cn.porkchop.mobilesafe.model.BlackListItem;
import android.test.AndroidTestCase;

public class SmsDaoTest extends AndroidTestCase {
	public void testFind() {
		SmsDao dao = new SmsDao();
		System.out.println(dao.findAll(getContext()));
	}
	
	public void test() {
		System.out.println(new Integer(999)==999);
	}
}

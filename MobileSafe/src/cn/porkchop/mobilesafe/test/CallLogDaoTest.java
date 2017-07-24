package cn.porkchop.mobilesafe.test;

import java.util.List;

import cn.porkchop.mobilesafe.dao.BlackListDao;
import cn.porkchop.mobilesafe.dao.CallLogDao;
import cn.porkchop.mobilesafe.dao.ContactDao;
import cn.porkchop.mobilesafe.db.BlackListDB;
import cn.porkchop.mobilesafe.model.BlackListItem;
import android.test.AndroidTestCase;

public class CallLogDaoTest extends AndroidTestCase {
	public void testFind() {
		CallLogDao dao = new CallLogDao();
		System.out.println(dao.findAll(getContext()));
	}
}

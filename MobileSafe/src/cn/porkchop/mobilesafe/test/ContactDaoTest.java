package cn.porkchop.mobilesafe.test;

import cn.porkchop.mobilesafe.dao.ContactDao;
import android.test.AndroidTestCase;

public class ContactDaoTest extends AndroidTestCase {
	public void test1() {
		System.out.println(new ContactDao().getAllContacts(getContext()));

	}
}

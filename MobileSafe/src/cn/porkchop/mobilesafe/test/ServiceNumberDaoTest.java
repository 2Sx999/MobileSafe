package cn.porkchop.mobilesafe.test;

import android.test.AndroidTestCase;
import cn.porkchop.mobilesafe.dao.AddressDao;
import cn.porkchop.mobilesafe.dao.ServiceNumberDao;

public class ServiceNumberDaoTest extends AndroidTestCase {
	public void test(){
		ServiceNumberDao serviceNumberDao = new ServiceNumberDao(getContext());
		serviceNumberDao.getAllServicePhoneType();
	}
}

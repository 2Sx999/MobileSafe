package cn.porkchop.mobilesafe.test;

import android.test.AndroidTestCase;
import cn.porkchop.mobilesafe.dao.AddressDao;

public class AddressDaoTest extends AndroidTestCase {
	public void test(){
		AddressDao addressDao = new AddressDao(getContext());
		System.out.println(addressDao.getLocation("13812630871"));
	}
}

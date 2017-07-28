package cn.porkchop.mobilesafe.test;

import android.test.AndroidTestCase;
import cn.porkchop.mobilesafe.dao.AddressDao;

public class AddressDaoTest extends AndroidTestCase {
	public void test(){
		AddressDao addressDao = new AddressDao(getContext());
		System.out.println(addressDao.getLocation("13812630871"));
	}
	
	public void test1() {
		fu o = new son();
		System.out.println(o.x);
	}
}

class fu{
	int x=1;
	public void print() {
		System.out.println("fu"+x);
	}
}

class son extends fu{
	int x=2;
	public void print() {
		System.out.println("zi"+this.x);
	}
}

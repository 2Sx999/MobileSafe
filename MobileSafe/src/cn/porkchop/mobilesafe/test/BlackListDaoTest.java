package cn.porkchop.mobilesafe.test;

import java.util.List;

import cn.porkchop.mobilesafe.dao.BlackListDao;
import cn.porkchop.mobilesafe.dao.ContactDao;
import cn.porkchop.mobilesafe.db.BlackListDB;
import cn.porkchop.mobilesafe.model.BlackListItem;
import android.test.AndroidTestCase;

public class BlackListDaoTest extends AndroidTestCase {
	public void testInsert() {
		BlackListDao dao = new BlackListDao(getContext());
		for(int i =0;i<30;i++){
			new BlackListDao(getContext()).insertOrUpdate("110"+i, BlackListDB.ALL_MODE);
		}
	}
	public void testFind(){
		BlackListDao dao = new BlackListDao(getContext());
		List<BlackListItem> list = dao.findMore(0, 10);
		System.out.println(list);
	}
}

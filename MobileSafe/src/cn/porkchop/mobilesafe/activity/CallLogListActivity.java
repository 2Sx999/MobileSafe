package cn.porkchop.mobilesafe.activity;

import java.util.List;

import cn.porkchop.mobilesafe.dao.CallLogDao;
import cn.porkchop.mobilesafe.dao.ContactDao;
import cn.porkchop.mobilesafe.model.Contact;

public class CallLogListActivity extends BaseListActivity {

	@Override
	public List<Contact> getListData() {
		return new CallLogDao().findAll(this);
	}

}

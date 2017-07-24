package cn.porkchop.mobilesafe.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import cn.porkchop.mobilesafe.model.Contact;

public class CallLogDao {
	public List<Contact> findAll(Context context) {
		List<Contact> list = new ArrayList<Contact>();
		Uri smsUri = Uri.parse("content://call_log/calls");
		Cursor cursor = context.getContentResolver().query(smsUri,
				new String[] { "name", "number" }, null, null, null);
		while (cursor.moveToNext()) {
			Contact contact = new Contact();
			contact.setName(cursor.getString(0));
			if (cursor.getString(0) == null) {
				contact.setName("陌生号码(通话记录)");
			}
			contact.setPhone(cursor.getString(1));
			list.add(contact);
		}
		cursor.close();
		return list;
	}

	public void deleteCallLogByPhone(Context context, String phone) {
		Uri uri = Uri.parse("content://call_log/calls");
		context.getContentResolver().delete(uri, "number=?",
				new String[] { phone });
	}
}

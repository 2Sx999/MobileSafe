package cn.porkchop.mobilesafe.dao;

import java.util.ArrayList;
import java.util.List;

import cn.porkchop.mobilesafe.model.Contact;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class SmsDao {
	public List<Contact> findAll(Context context) {
		List<Contact> list = new ArrayList<Contact>();
		Uri smsUri = Uri.parse("content://sms");
		Cursor cursor = context.getContentResolver().query(smsUri,
				new String[] { "person", "address" }, null, null, null);
		while (cursor.moveToNext()) {
			Contact contact = new Contact();
			if (cursor.getString(0) == null) {
				contact.setName("陌生号码(短信记录)");
			} else {
				Uri dataUrl = Uri.parse("content://com.android.contacts/data");
				Cursor dataCoursor = context.getContentResolver().query(
						dataUrl, new String[] { "data1", "mimetype" },
						"raw_contact_id=?",
						new String[] { cursor.getString(0) }, null);
				while (dataCoursor.moveToNext()) {
					String data = dataCoursor.getString(0);
					String mimetype = dataCoursor.getString(1);
					if("vnd.android.cursor.item/name".equals(mimetype)){
						contact.setName(data);
					}
				}
				dataCoursor.close();
			}
			contact.setPhone(cursor.getString(1));
			list.add(contact);
		}
		cursor.close();
		return list;
	}
}

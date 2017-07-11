package cn.porkchop.mobilesafe.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import cn.porkchop.mobilesafe.model.Contact;


public class ContactDao {
	public List<Contact> getAllContacts(Context context) {
		List<Contact> list = new ArrayList<Contact>();
		ContentResolver contentResolver = context.getContentResolver();
		//raw_contacts表
		Uri contactUri = Uri.parse("content://com.android.contacts/raw_contacts");
		//data表
		Uri dataUri = Uri.parse("content://com.android.contacts/data");
		Cursor contactCursor = contentResolver.query(contactUri, new String[]{"contact_id"}, null, null, null);
		while(contactCursor.moveToNext()){
			Contact contact = new Contact();
			String contactId = contactCursor.getString(0);
			if(contactId == null){
				continue;
			}
			Cursor dataCursor = contentResolver.query(dataUri, new String[]{"data1","mimetype"}, "raw_contact_id=?", new String[]{contactId}, null);
			while(dataCursor.moveToNext()){
				String mimeType = dataCursor.getString(1);
				String data1 = dataCursor.getString(0);
				if("vnd.android.cursor.item/phone_v2".equals(mimeType)){
					contact.setPhone(data1);
				}else if("vnd.android.cursor.item/name".equals(mimeType)){
					contact.setName(data1);
				}
			}
			list.add(contact);
		}
		return list;
	}
}

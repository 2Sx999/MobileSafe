package cn.porkchop.mobilesafe.dao;

import java.util.ArrayList;
import java.util.List;

import cn.porkchop.mobilesafe.model.ServicePhoneDetail;
import cn.porkchop.mobilesafe.model.ServicePhoneType;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ServiceNumberDao {
	private String SERVICE_DBPATH;
	private Context mContext;

	public ServiceNumberDao(Context context) {
		mContext = context;
		SERVICE_DBPATH = mContext.getFilesDir() + "/commonnum.db";
	}

	public List<ServicePhoneType> getAllServicePhoneType() {
		List<ServicePhoneType> typeList = new ArrayList<ServicePhoneType>();
		SQLiteDatabase database = SQLiteDatabase.openDatabase(SERVICE_DBPATH,
				null, SQLiteDatabase.OPEN_READONLY);
		Cursor typeCursor = database.rawQuery("SELECT * from classlist", null);
		while (typeCursor.moveToNext()) {
			ServicePhoneType type = new ServicePhoneType();
			type.setId(typeCursor.getInt(typeCursor.getColumnIndex("idx")));
			type.setType(typeCursor.getString(typeCursor.getColumnIndex("name")));
			List<ServicePhoneDetail> detailList = new ArrayList<ServicePhoneDetail>();
			type.setList(detailList);

			Cursor detailCursor = database.rawQuery("SELECT * from table"
					+ type.getId(), null);
			while (detailCursor.moveToNext()) {
				ServicePhoneDetail detail = new ServicePhoneDetail();
				detail.setId(detailCursor.getInt(detailCursor
						.getColumnIndex("_id")));
				detail.setNumber(detailCursor.getString(detailCursor
						.getColumnIndex("number")));
				detail.setName(detailCursor.getString(detailCursor
						.getColumnIndex("name")));
				System.out.println(detail);
				detailList.add(detail);
			}
			typeList.add(type);
		}
		typeCursor.close();
		database.close();
		return typeList;
	}
}

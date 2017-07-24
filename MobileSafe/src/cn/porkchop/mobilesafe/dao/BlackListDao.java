package cn.porkchop.mobilesafe.dao;

import java.util.ArrayList;
import java.util.List;

import cn.porkchop.mobilesafe.db.BlackListDB;
import cn.porkchop.mobilesafe.model.BlackListItem;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BlackListDao {
	private BlackListDB mBlackListDB;

	public BlackListDao(Context context) {
		mBlackListDB = new BlackListDB(context);
	}

	/**
	 * 先删除数据,再插入黑名单数据
	 * 
	 * @author nanamiporkchop
	 * @time 2017-7-15 下午9:51:03
	 * @param phone
	 * @param mode
	 */
	public void insertOrUpdate(String phone, int mode) {
		// 先删除重复的数据
		deleteByPhone(phone);
		// 在添加数据
		SQLiteDatabase db = mBlackListDB.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(BlackListDB.PHONE, phone);
		contentValues.put(BlackListDB.MODE, mode);
		db.insert(BlackListDB.TABLENAME, null, contentValues);
		db.close();
	}

	/**
	 * 分批获取数据
	 * 
	 * @author nanamiporkchop
	 * @time 2017-7-16 上午10:03:56
	 * @param startPosition
	 * @param count
	 * @return
	 */
	public List<BlackListItem> findMore(int startPosition, int count) {
		SQLiteDatabase db = mBlackListDB.getReadableDatabase();
		List<BlackListItem> list = new ArrayList<BlackListItem>();
		Cursor cursor = db.rawQuery("select * from " + BlackListDB.TABLENAME
				+ " order by " + BlackListDB.ID + " desc limit ?,?",
				new String[] { startPosition + "", count + "" });
		while (cursor.moveToNext()) {
			BlackListItem item = new BlackListItem();
			item.setId(cursor.getInt(cursor.getColumnIndex(BlackListDB.ID)));
			item.setMode(cursor.getInt(cursor.getColumnIndex(BlackListDB.MODE)));
			item.setPhone(cursor.getString(cursor
					.getColumnIndex(BlackListDB.PHONE)));
			list.add(item);
		}
		cursor.close();
		db.close();
		return list;
	}

	public int deleteByPhone(String phone) {
		SQLiteDatabase db = mBlackListDB.getWritableDatabase();
		int count = db.delete(BlackListDB.TABLENAME, BlackListDB.PHONE + "=?",
				new String[] { phone });
		db.close();
		return count;
	}

	public int findModeByPhone(String phone) {
		SQLiteDatabase db = mBlackListDB.getReadableDatabase();
		Cursor cursor = db.rawQuery("select " + BlackListDB.MODE + " from "
				+ BlackListDB.TABLENAME + " where phone=?",
				new String[] { phone });
		int name = 0;
		if (cursor.moveToNext()) {
			name = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return name;
	}
}

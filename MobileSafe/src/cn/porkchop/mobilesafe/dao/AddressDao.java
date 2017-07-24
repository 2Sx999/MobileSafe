package cn.porkchop.mobilesafe.dao;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AddressDao {
	private Context mContext;
	private String PHONE_DBPATH;


	public AddressDao(Context context) {
		mContext = context;
		PHONE_DBPATH = mContext.getFilesDir() + "/address.db";
	}

	/**
	 * @param number
	 *            手机号如：18899997777或者固定电话如：075512344321
	 * @return 归属地信息
	 */
	public String getLocation(String number) {
		String location = "";
		// 是否是手机号
		Pattern p = Pattern.compile("1[34578]{1}[0-9]{9}");
		Matcher m = p.matcher(number);
		boolean b = m.matches();
		if (b) {
			location = getMobileLocation(number.substring(0, 7));
		} else {
			if (number.charAt(1) == '1' || number.charAt(1) == '2') {
				// 2位区号
				location = getTelephoneLocation(number.substring(1, 3));
			} else {
				// 3位区号
				location = getTelephoneLocation(number.substring(1, 4));
			}
		}
		return location; //
	}

	/**
	 * @param mobileNumber
	 *            手机号的前七位
	 * @return 归属地信息
	 */
	private String getMobileLocation(String mobileNumber) {
		String location = "未知";
		// 获取数据库
		SQLiteDatabase database = SQLiteDatabase.openDatabase(PHONE_DBPATH,
				null, SQLiteDatabase.OPEN_READONLY);
		// 查询
		Cursor cursor = database
				.rawQuery(
						"select location from data2 where id=(select outkey from data1 where id=?)",
						new String[] { mobileNumber });
		if (cursor.moveToNext()) {
			// 获取归属地信息
			location = cursor.getString(0);
		}
		cursor.close();
		return location;
	}

	/**
	 * @param phoneNumber
	 *            输入的是固话的区号
	 * @return 位置信息
	 */
	private String getTelephoneLocation(String phoneNumber) {
		String location = "未知截掉";
		// 获取数据库
		SQLiteDatabase database = SQLiteDatabase.openDatabase(PHONE_DBPATH,
				null, SQLiteDatabase.OPEN_READONLY);
		// 查询
		Cursor cursor = database.rawQuery(
				"select location from data2 where area=?",
				new String[] { phoneNumber });
		if (cursor.moveToNext()) {
			// 获取归属地信息
			location = cursor.getString(0);
		}
		cursor.close();
		return location.substring(0, location.length() - 2);
	}
}

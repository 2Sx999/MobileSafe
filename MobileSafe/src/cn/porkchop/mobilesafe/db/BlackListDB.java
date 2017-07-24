package cn.porkchop.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackListDB extends SQLiteOpenHelper {
	public static final String TABLENAME = "t_blacklist";
	public static final String PHONE = "phone";
	public static final String ID = "_id";
	public static final String MODE = "mode";
	public static final int SMS_MODE = 1;
	public static final int PHONE_MODE = 1 << 1;
	public static final int ALL_MODE = SMS_MODE | PHONE_MODE;
	private static final int VERSION = 1;

	public BlackListDB(Context context) {
		super(context, "blacklist.db", null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table t_blacklist(_id integer primary key autoincrement,phone varchar(15),mode integer)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table t_blacklist");
		onCreate(db);
	}



}

package com.example.bluetoothcaller.Service;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataOpenHelper extends SQLiteOpenHelper {
	private final static String DATABASE_NAME = "pim.db";
	private final static int DATABASE_VERSION = 1;
	public final static String TABLE_NAME_CONTACTS = "contacts_table";
	public final static String TABLE_NAME_CALLLOGS = "calllogs_table";
	public final static String CONTACTS_ID = "contacts_id";
	public final static String CONTACTS_NAME = "contacts_name";
	public final static String CONTACTS_NUM = "contacts_number";
	public final static String CALLLOGS_ID = "calllogs_id";
	public final static String CALLLOGS_NAME = "calllogs_name";
	public final static String CALLLOGS_NUM = "calllogs_number";
	
	public DataOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	public DataOpenHelper(Context context, String name, CursorFactory factory,
			int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		String sqlContacts = "CREATE TABLE " + TABLE_NAME_CONTACTS + "(" + CONTACTS_ID
				+ " INTEGER primary key autoincrement, " + CONTACTS_NAME + " text "
				+ CONTACTS_NUM + "text);";
		String sqlCalllogs = "CREATE TABLE " + TABLE_NAME_CALLLOGS + "(" + CALLLOGS_ID
				+ " INTEGER primary key autoincrement, " + CALLLOGS_NAME + " text "
				+ CALLLOGS_NUM + "text);";
		arg0.execSQL(sqlContacts);
		arg0.execSQL(sqlCalllogs);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		String sqlContacts = "DROP TABLE IF EXISTS" + TABLE_NAME_CONTACTS;
		String sqlCalllogs = "DROP TABLE IF EXISTS" + TABLE_NAME_CALLLOGS;
		arg0.execSQL(sqlContacts);
		arg0.execSQL(sqlCalllogs);
		onCreate(arg0);
	}

	public long insert(String dbName, Object[] args) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		//cv.put(CONTACTS_NAME, contactsName);
		//cv.put(CONTACTS_NUM, contactsNum);
		long row = db.insert(TABLE_NAME_CONTACTS, null, cv);
		return row;
	}

	public void delete(String dbName, String contactsName) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = CONTACTS_NAME + "=?";
		String[] whereValue = { contactsName };
		db.delete(TABLE_NAME_CONTACTS, where, whereValue);
	}
}

package com.project.keepingrunning.frame;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	private final static String DATABASENAME = "Activity.db";
	private static final int DATABASE_VERSION = 1; 

	public DBHelper(Context context) {  
        //CursorFactory设置为null,使用默认值  
        super(context, DATABASENAME, null, DATABASE_VERSION);  
    }  
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS " + Constant.TABLE_ACTIVITY +  " " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, distance DOUBLE, start_time TEXT, end_time TEXT, speed DOUBLE)");
		db.execSQL("CREATE TABLE IF NOT EXISTS " + Constant.TABLE_ACTIVITYPATH +  " " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, activity_id INTEGER, latitude DOUBLE, longitude DOUBLE, speed DOUBLE, record_time TEXT," + " " +
				"FOREIGN KEY(activity_id) REFERENCES " +Constant.TABLE_ACTIVITY +"(id)" + ")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db); 
		if(!db.isReadOnly()) { 
			// Enable foreign key constraints 
			db.execSQL("PRAGMA foreign_keys=ON;"); 
		}
	}

}

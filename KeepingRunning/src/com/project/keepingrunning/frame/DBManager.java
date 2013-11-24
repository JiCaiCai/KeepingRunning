package com.project.keepingrunning.frame;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.project.keeprunning.object.ActivityPath;
import com.project.keeprunning.object.RunActivity;

public class DBManager {
	private DBHelper helper;  
    private SQLiteDatabase db; 
	
    public DBManager(Context context) {  
        helper = new DBHelper(context);  
        db = helper.getWritableDatabase();  
    }
    
    public void addActivityPaths(List<ActivityPath> activityPaths) {  
        db.beginTransaction();  // start transaction 
        try {  
            for (ActivityPath activityPath : activityPaths) {  
                db.execSQL("INSERT INTO " + Constant.TABLE_ACTIVITYPATH + " VALUES(null, ?, ?, ?, ?, ?)",
                		new Object[]{activityPath.getActivityID(), activityPath.getLatitude(), activityPath.getLongitude(), activityPath.getSpeed(), activityPath.getRecordTime()});  
            }  
            db.setTransactionSuccessful();  // finish transaction 
        } finally {  
            db.endTransaction();    // end transaction  
        }  
    }  
    
    public void addRunActivity(RunActivity runActivity) {
    	db.beginTransaction();  // start transaction 
        try {  
        	db.execSQL("INSERT INTO " + Constant.TABLE_ACTIVITY + " VALUES(?, ?, ?, ?, ?)",
            		new Object[]{runActivity.getId(), runActivity.getDistance(), runActivity.getStartTime(), runActivity.getEndTime(), runActivity.getSpeed()});  

        	db.setTransactionSuccessful();  // finish transaction 
        } finally {  
            db.endTransaction();    // end transaction  
        } 
    }
    
    public int getRunActivityCount() {
    	int count = 0;
    	
    	Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + Constant.TABLE_ACTIVITY, null);
    	
    	if (c.moveToNext()) {  
    		count = c.getInt(0);
        }  
        c.close();
    	
    	return count;
    }
    
    /** 
     * close database 
     */  
    public void closeDB() {  
        db.close();  
    }
    
}

package com.project.keepingrunning.frame;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.project.keepingrunning.object.ActivityPath;
import com.project.keepingrunning.object.RunActivity;

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
    
    public int getMaxRunActivityID() {
    	int maxId = 0;
    	
    	Cursor c = db.rawQuery("SELECT MAX("+Constant.ID+") FROM " + Constant.TABLE_ACTIVITY, null);
    	
    	if (c.moveToNext()) {  
    		maxId = c.getInt(0);
        }  
        c.close();
    	
    	return maxId;
    }
    
    public List<RunActivity> getRunActivities() {
    	List<RunActivity> result = new ArrayList<RunActivity>();
    	
    	Cursor c = db.rawQuery("SELECT * FROM " + Constant.TABLE_ACTIVITY + " ORDER BY " + Constant.START_TIME + " desc", null);
    	
    	while (c.moveToNext()) {  
    		RunActivity ra = new RunActivity();
    		int columnIndex = c.getColumnIndex(Constant.ID);
    		ra.setId(c.getInt(columnIndex));
    		columnIndex = c.getColumnIndex(Constant.DISTANCE);
    		ra.setDistance(c.getDouble(columnIndex));
    		columnIndex = c.getColumnIndex(Constant.START_TIME);
    		ra.setStartTime(c.getString(columnIndex));
    		columnIndex = c.getColumnIndex(Constant.END_TIME);
    		ra.setEndTime(c.getString(columnIndex));
    		columnIndex = c.getColumnIndex(Constant.SPEED);
    		ra.setSpeed(c.getDouble(columnIndex));
    		result.add(ra);
        }  
        c.close();
    	
    	return result;
    }
    
    public List<RunActivity> getRunPathActivities() {
    	List<RunActivity> result = new ArrayList<RunActivity>();
    	
    	Cursor c = db.rawQuery("SELECT * FROM " + Constant.TABLE_ACTIVITY + " ORDER BY " + Constant.START_TIME + " desc", null);
    	
    	while (c.moveToNext()) {  
    		RunActivity ra = new RunActivity();
    		int columnIndex = c.getColumnIndex(Constant.ID);
    		ra.setId(c.getInt(columnIndex));
    		columnIndex = c.getColumnIndex(Constant.DISTANCE);
    		ra.setDistance(c.getDouble(columnIndex));
    		columnIndex = c.getColumnIndex(Constant.START_TIME);
    		ra.setStartTime(c.getString(columnIndex));
    		columnIndex = c.getColumnIndex(Constant.END_TIME);
    		ra.setEndTime(c.getString(columnIndex));
    		columnIndex = c.getColumnIndex(Constant.SPEED);
    		ra.setSpeed(c.getDouble(columnIndex));
    		result.add(ra);
        }  
        c.close();
    	
    	return result;
    }
    
    public List<ActivityPath> getPathActivities(int id) {
    	List<ActivityPath> result = new ArrayList<ActivityPath>();
    	
    	Cursor c = db.rawQuery("SELECT * FROM " + Constant.TABLE_ACTIVITYPATH +
    			" WHERE " + Constant.ACTIVITY_ID + "=? " +
    			" ORDER BY " + Constant.RECORD_TIME + " asc", new String[]{String.valueOf(id)});
    	
    	while (c.moveToNext()) {  
    		ActivityPath pma = new ActivityPath();
    		int columnIndex = c.getColumnIndex(Constant.ID);
    		pma.setId(c.getInt(columnIndex));
    		columnIndex = c.getColumnIndex(Constant.ACTIVITY_ID);
    		pma.setActivityID(c.getInt(columnIndex));
    		columnIndex = c.getColumnIndex(Constant.LATITUDE);
    		pma.setLatitude(c.getDouble(columnIndex));
    		columnIndex = c.getColumnIndex(Constant.LONGITUDE);
    		pma.setLongitude(c.getDouble(columnIndex));
    		columnIndex = c.getColumnIndex(Constant.SPEED);
    		pma.setSpeed(c.getDouble(columnIndex));
    		columnIndex = c.getColumnIndex(Constant.RECORD_TIME);
    		pma.setRecordTime(c.getString(columnIndex));
    		result.add(pma);
        }  
        c.close();
    	
    	return result;
    }
    
    public void deleteRunActivity(int id) {
    	db.beginTransaction();  // start transaction 
        try {  
        	db.execSQL("DELETE FROM " + Constant.TABLE_ACTIVITYPATH + " WHERE " + Constant.ACTIVITY_ID + "=?",
            		new Object[]{id}); 
        	db.execSQL("DELETE FROM " + Constant.TABLE_ACTIVITY + " WHERE " + Constant.ID + "=?",
            		new Object[]{id});

        	db.setTransactionSuccessful();  // finish transaction 
        } finally {  
            db.endTransaction();    // end transaction  
        }
    }
    
    /** 
     * close database 
     */  
    public void closeDB() {  
        db.close();  
    }
    
}

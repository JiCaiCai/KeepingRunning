package com.project.keepingrunning;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.project.keepingrunning.frame.Constant;
import com.project.keepingrunning.frame.DBManager;
import com.project.keepingrunning.object.RunActivity;

public class RecordActivity extends SherlockActivity{

    private ListView listView;
    private SimpleAdapter mySimpleAdapter = null;
    private ActionBar mActionBar;
    private DBManager mDBManager = null;
    private ArrayList<HashMap<String,String>> runActivityList = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        listView = (ListView) findViewById(R.id.list);
        
        mDBManager = new DBManager(this);
        List<RunActivity> activityList = mDBManager.getRunActivities();
        
      //create ArrayList and add data into it       
        runActivityList = getRunActivityList(activityList); 
        
        mySimpleAdapter = new SimpleAdapter(this, 
        		runActivityList,//data source               
        		R.layout.list_item,               
        		new String[]{Constant.START_TIME, Constant.DISTANCE,Constant.SPEED, Constant.SPEND_TIME},                
        		new int[]{R.id.start_time,R.id.distance,R.id.speed,R.id.spend_time});
        
        initListView(mySimpleAdapter); 

        // use getSupportActionBar to get the ActionBar instance  
        mActionBar = getSupportActionBar(); 
        
        // hide Title  
        mActionBar.setDisplayShowTitleEnabled(true);  
        // hide Home LOGO  
        mActionBar.setDisplayShowHomeEnabled(true); 
        // show arrow
        mActionBar.setDisplayHomeAsUpEnabled(true);
        
    }
    
    private void initListView(SimpleAdapter mySimpleAdapter) {
    	listView.setAdapter(mySimpleAdapter);
    	//add click event
        listView.setOnItemClickListener(new OnItemClickListener(){             
        	@Override            
        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {                 
        		//obtain HashMap object                 
        		HashMap<String,String> map=(HashMap<String,String>)listView.getItemAtPosition(arg2);                 
        		String id=map.get(Constant.ID);                 
        		String start_time=map.get(Constant.START_TIME);                 
        		Toast.makeText(getApplicationContext(), 
        			"你选择了第"+arg2+"个Item，id的值是："+id+"start_time的值是:"+start_time, 
        			Toast.LENGTH_SHORT).show();             
        	}                      
        });
        
        //add long click event for deleting record
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
        	 @Override  
             public boolean onItemLongClick(AdapterView<?> arg0, View arg1,  
                     int arg2, long arg3) {  
                 // When clicked, confirm delete record   
        		 HashMap<String,String> map=(HashMap<String,String>)listView.getItemAtPosition(arg2);                 
         		 Integer id= Integer.valueOf(map.get(Constant.ID));
         		 String startTime=map.get(Constant.START_TIME); 
         		 showCautious(id, startTime, arg2);
         		 
                 return false;  
             } 
        }); 
		
    }
    
   private void showCautious(final Integer id,String startTime, final int pos) {
		new AlertDialog.Builder(RecordActivity.this).setTitle("Cautious")
        .setMessage("Delete this record(start time : "+startTime+")?")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	mDBManager.deleteRunActivity(id);
            	runActivityList.remove(pos);
            	mySimpleAdapter.notifyDataSetChanged();
            }})
        .setNegativeButton("No", null)
        .create().show();
	}
    
    private ArrayList<HashMap<String,String>> getRunActivityList(List<RunActivity> activityList) {
    	ArrayList<HashMap<String,String>> runActivityList=new ArrayList<HashMap<String,String>>(); 
    	NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumIntegerDigits(3);
        numberFormat.setMaximumFractionDigits(3);
        numberFormat.setMinimumIntegerDigits(1);
        for (RunActivity runActivity:activityList) {
        	HashMap<String, String> map = new HashMap<String, String>();
        	long duration = getDuration(runActivity);
        	map.put(Constant.DISTANCE, numberFormat.format(runActivity.getDistance()/1000)+"km");             
        	map.put(Constant.SPEED, numberFormat.format(runActivity.getDistance()/(duration/1000))+"m/s");             
        	map.put(Constant.START_TIME, runActivity.getStartTime());             
        	map.put(Constant.SPEND_TIME, getDurationStr(duration));             
        	map.put(Constant.ID, String.valueOf(runActivity.getId()));             
        	runActivityList.add(map); 
        }
        return runActivityList; 
    }
    
    private long getDuration (RunActivity ra) {
    	SimpleDateFormat df = new SimpleDateFormat(Constant.DATE_FORMAT);
    	try {
			Date startDate = df.parse(ra.getStartTime());
			Date endDate = df.parse(ra.getEndTime());
			Calendar startDateC = Calendar.getInstance();
			Calendar endDateC = Calendar.getInstance();
			startDateC.setTime(startDate);
			endDateC.setTime(endDate);
			long duration = endDateC.getTimeInMillis() - startDateC.getTimeInMillis();
			return duration;
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return 1;
    }
    
    private String getDurationStr (long duration) {
		long min = (duration/1000)/60;
		String strMin = min<10? "0"+min:min+"";
		long sec = (duration/1000)%60;
		String strSec = sec<10? "0"+sec:sec+"";
		return strMin + "' " + strSec +"\"" ;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {  
        case android.R.id.home:  
        	finish();
            break;  
        default:  
            break;  
        }  
        return super.onOptionsItemSelected(item);  
	}

	

}


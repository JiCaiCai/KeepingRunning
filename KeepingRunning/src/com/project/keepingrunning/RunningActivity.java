package com.project.keepingrunning;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.location.Location;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.project.keepingrunning.frame.Constant;
import com.project.keepingrunning.frame.DBManager;
import com.project.keeprunning.object.ActivityPath;
import com.project.keeprunning.object.RunActivity;

public class RunningActivity extends SherlockActivity {

	// member
	private LocationClient mLocClient;
	private double mDistance;
	private int mTime;
	private Time mRecordTime;
	private DecimalFormat mDF = null;
	private DBManager mDBManager = null;
	private int mActivityID = 0;
	
	// data
	private RunActivity runActivity = null;
	private ArrayList<ActivityPath> activityPaths = null;
	
	// controls
	private TextView tvUsedTime = null;
	private TextView tvRunSpeed = null;
	private TextView tvRunDistance = null;
	private ProgressBar pbLimitation = null;
	private Button btnStop = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_running);
		
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		mDistance = 0;
		mTime = 0;
		mRecordTime = new Time("GMT+8");
		mDF = new DecimalFormat("0.0");
		mDBManager = new DBManager(this);
		mActivityID = mDBManager.getRunActivityCount() + 1;
		runActivity = new RunActivity();
		activityPaths = new ArrayList<ActivityPath>();
		
		// controls
		tvUsedTime = (TextView) findViewById(R.id.used_time);
		tvRunSpeed = (TextView) findViewById(R.id.run_speed);
		tvRunDistance = (TextView) findViewById(R.id.run_distance);
		pbLimitation = (ProgressBar) findViewById(R.id.limit_bar);
		btnStop = (Button) findViewById(R.id.run_stop);
		
		btnStop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveDataToDB();
				finish();
			}
		});
		
		// initialize the locator
		initLocator();
	}

	/**
	 * save the data
	 */
	protected void saveDataToDB() {
		// TODO Auto-generated method stub
		// the parameter of this activity
		runActivity.setId(mActivityID);
		runActivity.setDistance(mDistance);
		runActivity.setSpeed(mDistance/mTime);
		if (activityPaths.size() > 0) {
			runActivity.setEndTime(activityPaths.get(activityPaths.size()-1).getRecordTime());
		}
		
		// add this run activity
		mDBManager.addRunActivity(runActivity);
		
		// add the path of this activity
		mDBManager.addActivityPaths(activityPaths);
		
		// close the manager
		mDBManager.closeDB();
	}

	private void initLocator() {
		// TODO Auto-generated method stub
		mLocClient = new LocationClient(getApplicationContext());  
		mLocClient.setAK(Constant.BAIDUKEY);
        mLocClient.registerLocationListener(new BDLocationListenerImpl()); // register location listener interface  
          
        // set type of location
        LocationClientOption option = new LocationClientOption();  
        option.setOpenGps(true); // open GPS
        option.setAddrType("all");
        option.setCoorType("GCJ02"); 
        option.setPriority(LocationClientOption.GpsFirst); // GPS has the highest priority 
        option.setScanSpan(5000); // time interval 5000ms  
        option.disableCache(false);         
        mLocClient.setLocOption(option);  // set location parameter
        
        mLocClient.start();
        
    	mLocClient.requestLocation();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.running, menu);
		return true;
	}

	public class BDLocationListenerImpl implements BDLocationListener {
		private BDLocation mLastLoc = null;
		
		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			if(location == null) {
				return;
			}
			if (!(location.getLocType() == 61 || location.getLocType() == 161)) {
				return;
			}
			
			// test
			StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");  
            sb.append(location.getTime());  
            sb.append("\nerror code : ");  
            sb.append(location.getLocType());  
            sb.append("\nlatitude : ");  
            sb.append(location.getLatitude());  
            sb.append("\nlontitude : ");  
            sb.append(location.getLongitude());  
            sb.append("\nradius : ");  
            sb.append(location.getRadius());  
            sb.append("\ntype : ");
            sb.append(location.getLocType());
            sb.append("\nspeed : ");  
            sb.append(location.getSpeed());  
            sb.append("\nsatellite : ");  
            sb.append(location.getSatelliteNumber());  
            if (location.getLocType() == BDLocation.TypeGpsLocation){  
                 sb.append("\nspeed : ");  
                 sb.append(location.getSpeed());  
                 sb.append("\nsatellite : ");  
                 sb.append(location.getSatelliteNumber());  
             } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){  
                 sb.append("\naddr : ");  
                 sb.append(location.getAddrStr());  
             }   
         
            Log.e("log", sb.toString()); 
            
            float[] distance = new float[1];
            if (mLastLoc != null) {
            	// if the type of location is based on network, then use fake data
            	if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
            		location.setLongitude(mLastLoc.getLongitude()+0.0003);
                    location.setLatitude(mLastLoc.getLatitude()+0.0003);
            	}
                
            	Location.distanceBetween(mLastLoc.getLatitude(), mLastLoc.getLongitude(), 
                		location.getLatitude(), location.getLongitude(), distance);
            }
            
            // record the start time
            if (runActivity.getStartTime() == null) {
            	runActivity.setStartTime(location.getTime());
            }
            
            // update the member
            mDistance +=  distance[0];
            
            // update the interface
            tvRunDistance.setText("Distance: " + keepTwoDigits(mDistance/1000) + " km");
            tvRunSpeed.setText("Speed: " + keepTwoDigits(location.getSpeed()) + " m/s");
            tvUsedTime.setText("Time: "+ "1" + " s");
            
            // save those path
            ActivityPath path = new ActivityPath();
            path.setActivityID(mActivityID);
            path.setLatitude(location.getLatitude());
            path.setLongitude(location.getLongitude());
            path.setRecordTime(getCurrentTime());
            path.setSpeed(location.getSpeed());
            activityPaths.add(path);
            
            // update the last location
            mLastLoc = location;
		}

		@Override
		public void onReceivePoi(BDLocation poiLocation) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private String keepTwoDigits(double value) {
		// TODO Auto-generated method stub
		return mDF.format(value);
	}

	public String getCurrentTime() {
		// TODO Auto-generated method stub
		mRecordTime.setToNow();   
        int year = mRecordTime.year;   
        int month = mRecordTime.month + 1;   
        int day = mRecordTime.monthDay;   
        int minute = mRecordTime.minute;   
        int hour = mRecordTime.hour;   
        int sec = mRecordTime.second;
		return year+"-"+month+"-"+day+" "+hour+":"+minute+":"+sec;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mLocClient.stop();
		
		super.onDestroy();
	}
	
}

package com.project.keepingrunning;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.project.keepingrunning.frame.Constant;
import com.project.keepingrunning.frame.DBManager;
import com.project.keepingrunning.object.ActivityPath;
import com.project.keepingrunning.object.RunActivity;

public class RunningActivity extends SherlockActivity {

	// member
	private LocationClient mLocClient;
	private double mDistance;
	private int mTime;
	private DecimalFormat mDF = null;
	private DBManager mDBManager = null;
	private int mActivityID = 0;
	private Time mRecordTime;
	private int mTag = 0;
	private NotificationManager mManager = null;
	private boolean mLocating = true;
	private Notification mNotification = null;
	
	// Timer
	private static Handler mHandler = null;
	private Timer mTimer= null;
	
	// data
	private RunActivity runActivity = null;
	private ArrayList<ActivityPath> activityPaths = null;
	
	// controls
	private TextView tvUsedTime = null;
	private TextView tvRunSpeed = null;
	private TextView tvRunDistance = null;
	private TextView tvNotification = null;
	private LinearLayout llDashBoard = null;
	private ProgressBar pbLimitation = null;
	private Button btnStop = null;
	private RemoteViews rvNotificationProgress = null;
	
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
		mRecordTime = new Time();
		mDF = new DecimalFormat("0.0");
		mDBManager = new DBManager(this);
		mActivityID = mDBManager.getMaxRunActivityID() + 1;
		runActivity = new RunActivity();
		activityPaths = new ArrayList<ActivityPath>();
		mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		// controls
		tvUsedTime = (TextView) findViewById(R.id.used_time);
		tvRunSpeed = (TextView) findViewById(R.id.run_speed);
		tvRunDistance = (TextView) findViewById(R.id.run_distance);
		tvNotification = (TextView) findViewById(R.id.task_notification);
		llDashBoard = (LinearLayout) findViewById(R.id.dash_board);
		pbLimitation = (ProgressBar) findViewById(R.id.limit_bar);
		btnStop = (Button) findViewById(R.id.run_stop);
		rvNotificationProgress = new RemoteViews(getPackageName(), R.layout.notification_view);
		
		btnStop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showCautious();
			}
		});
		// get data from previous activity
		mTag = getIntent().getIntExtra(Constant.SOURCE, 0);
		switch (mTag) {
		case 1:
			break;
		case 2:
			pbLimitation.setMax(getIntent().getIntExtra(Constant.TIME, 0));
			break;
		case 3:
			pbLimitation.setMax(getIntent().getIntExtra(Constant.DISTANCE, 0));
			break;

		default:
			break;
		}
		
		// initialize the locator
		initLocator();
		// initialize the timer 
		initialTimer();
	}

	/**
	 * show the notification bar
	 */
	@SuppressLint("NewApi")
	private void showNotification() {
		// TODO Auto-generated method stub
		Notification.Builder mBuilder = new Notification.Builder(RunningActivity.this)
		.setSmallIcon(R.drawable.icon)
		.setContent(rvNotificationProgress)
		.setOngoing(true);
		mBuilder.setTicker("Start your running!");
		
		// construct Intent
		Intent resultIntent = new Intent(this, RunningActivity.class);
		// Encapsulate Intent
		PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		
		mNotification = mBuilder.build();
		mManager.notify(Constant.NOTICE_ID, mNotification);
	}

	private void initialTimer() {
		// TODO Auto-generated method stub
		mHandler = new Handler() {
			
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				
				mTime = msg.what;
				int min = mTime/60;
				String strMin = min<10? "0"+min:min+"";
				int sec = mTime%60;
				String strSec = sec<10? "0"+sec:sec+"";
				tvUsedTime.setText("Time: "+ strMin + "' " + strSec +"\"" );
				if (mTag == 2 && mTime <= pbLimitation.getMax()) {
					pbLimitation.setProgress(mTime);
					// notification progress bar and text
	            	rvNotificationProgress.setProgressBar(R.id.notification_progressbar, pbLimitation.getMax(), mTime, false);
	            	rvNotificationProgress.setTextViewText(R.id.notification_text,  (int)((double)mTime/pbLimitation.getMax()*100) + "%");
	            	mManager.notify(Constant.NOTICE_ID, mNotification);
					if (mTime == pbLimitation.getMax()) {
						tvNotification.setText(getString(R.string.task_notification));
					}
				}
			}
		};
		
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			int second = 0;
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!mLocating) {
					Message msg = new Message();
					msg.what = second++;
					mHandler.sendMessage(msg);
				}
			}
			
		}, 1000, 1000 );
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
            	runActivity.setStartTime(getCurrentTime());
            	// locate successful
            	mLocating = false;
            	// notice disappear
            	tvNotification.setText("");
            	// show dash board
            	llDashBoard.setVisibility(View.VISIBLE);
            	// show notification bar
        		showNotification();
            }
            
            // update the member
            mDistance +=  distance[0];
            
            // update the interface
            tvRunDistance.setText("Distance: " + keepTwoDigits(mDistance/1000) + " km");
            tvRunSpeed.setText("Speed: " + keepTwoDigits(location.getSpeed()) + " m/s");
            if (mTag == 3 && mDistance <= pbLimitation.getMax()) {
            	pbLimitation.setProgress((int) mDistance);
            	// notification progress bar and text
            	rvNotificationProgress.setProgressBar(R.id.notification_progressbar, pbLimitation.getMax(), (int) mDistance, false);
            	rvNotificationProgress.setTextViewText(R.id.notification_text, (int)(mDistance/pbLimitation.getMax()*100) + "%");
            	mManager.notify(Constant.NOTICE_ID, mNotification);
            	
            	if (mDistance > pbLimitation.getMax()) {
            		tvNotification.setText(getString(R.string.task_notification));
            	}
            }
            
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
		mTimer.cancel();
		
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK) {
	    	if (mLocating) {
	    		finish();
	    	} else {
	    		showCautious();
	    	}
	        return false;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	void showCautious() {
		new AlertDialog.Builder(RunningActivity.this).setTitle("Cautious")
        .setMessage("Finish the activity?")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	saveDataToDB();
            	mManager.cancel(Constant.NOTICE_ID);
            	finish();
            	//Intent intent = new Intent(RunningActivity.this, RecordActivity.class);
            }})
        .setNegativeButton("No", null)
        .create().show();
	}
}

package com.project.keepingrunning;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.project.keepingrunning.frame.Constant;
import com.project.keepingrunning.frame.DBManager;
import com.project.keepingrunning.object.ActivityPath;
import com.project.keepingrunning.object.RunActivity;

public class SetDestination extends SherlockActivity {
	//declare the relevant object of baidu map
	private MapView mMapView = null;
	private BMapManager mBMapManager;
	private Toast mToast;
	private MapController mMapController = null;
	private boolean mLocating=false;
	private Timer mTimer=null;
	//UPDATE like a signal of the handler,when the message equal to UPDATE 
	private static final int UPDATE = 0;


	//declare the relevant object of the baidu location
	private LocationClient mLocClient=null;
	private LocationData BeginMarker=new LocationData();
	private LocationData EndMarker=new LocationData();
	private Handler mLocationHandler=new Handler(){
		
	
	@Override
    public void handleMessage(Message msg) {
            // TODO 接收消息并且去更新UI线程上的控件内容
            if (msg.what ==UPDATE ) {
//            	//which means that 
            	GeoPoint CenterPoint =new GeoPoint((int)(BeginMarker.latitude*1E6),(int)(BeginMarker.longitude*1E6)); 
            	mMapController = mMapView.getController();
            	mMapController.animateTo(CenterPoint);
            	showToast("your locations is: "+Double.toString(BeginMarker.latitude)+" "+Double.toString(BeginMarker.longitude));
	            mTimer.cancel();
	            mLocClient.stop();
	            addBeginMarker(BeginMarker);
        		
            }
            super.handleMessage(msg);
        }
	};

	
	//declare relevant database object
	private DBManager mDBManager = null;
	private int mActivityID = 0;
	private ArrayList<ActivityPath> activityPaths = null;
	private RunActivity runActivity = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initLocator();
		initMapManager();
		setContentView(R.layout.set_destination);
		initMap();
		//the biggest question is that, the location need time.
		//so ,firslty ,it return null,but the map need it to locate our postion!!
		
		mTimer = new Timer();  
        mTimer.schedule(new MyTask(), 1000, 1000);  
	}  

	private class MyTask extends TimerTask{  
        @Override  
        public void run() {  
        	if(mLocating){
	            Message message = new Message();  
	            message.what = UPDATE;  
	            mLocationHandler.sendMessage(message);  
        	}
        }     
    }  
		//init();
		//

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getSupportMenuInflater().inflate(R.menu.menu_home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		boolean isOkToJump=false;
		double locationArray[]=new double[4];
		if((int)EndMarker.latitude!=0){
			isOkToJump=true;
			locationArray[0]=BeginMarker.latitude;
			locationArray[1]=BeginMarker.longitude;
			locationArray[2]=EndMarker.latitude;
			locationArray[3]=EndMarker.longitude;
		}
		Intent intent = null;
		switch (item.getItemId()) {  
        case R.id.activity_record : 
        	intent = new Intent(this, RecordActivity.class);
        	startActivity(intent);
//            Toast.makeText(this, "menu_record", Toast.LENGTH_SHORT).show(); 
            break;  
        case R.id.start_activity:
        	if(isOkToJump==true){
        		intent = new Intent(this, RunTypeActivity.class).putExtra("LocationArray", locationArray);
        		startActivity(intent);
        	}
        	else{
        		showToast("please set your destination");
        	}
        	break;
        case android.R.id.home:  
//            Intent intent = new Intent(this, MainActivity.class);  
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  
//                    | Intent.FLAG_ACTIVITY_NEW_TASK);  
//            startActivity(intent);  
//            Toast.makeText(getApplicationContext(), "android.R.id.home", 0)  
//                    .show();  
        	Toast.makeText(this, "menu", Toast.LENGTH_SHORT).show(); 
            break;  
        default:  
            break;  
        }  
        return super.onOptionsItemSelected(item);  
	}
	private void init(){
		BeginMarker=new LocationData();
		EndMarker=new LocationData();
		//activityPaths = new ArrayList<ActivityPath>();
	}
	private void initDB(){
		mActivityID = mDBManager.getMaxRunActivityID() + 1;
		mDBManager = new DBManager(this);
	}
	private void initMapManager(){
		/**
		 * initialize BMapManager
		 */
		mBMapManager = new BMapManager(this);
		
		//the first parameter is API key,
		//the second parameter is common event listener
		mBMapManager.init(Constant.BAIDUKEY, new MKGeneralListener() {
			
			//this method will be called when the authority is denied 
			@Override
			public void onGetPermissionState(int iError) {
				if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
					showToast("Wrong API KEY, please check！");
	            }
			}
			
			//this method will handle Network error
			@Override
			public void onGetNetworkState(int iError) {
				if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
					Toast.makeText(getApplication(), "Network error！", Toast.LENGTH_LONG).show();
	            }
			}
		});
		mBMapManager.start();
	}
	private void initMap(){

		mMapView = (MapView) findViewById(R.id.HomeMap);
		
		 /**
         * get map controller
         */
        mMapController = mMapView.getController();
        mMapController.enableClick(true);
        mMapController.setZoom(16);
        mMapView.setBuiltInZoomControls(true);
        //
        //showToast(String.valueOf(BeginMarker.latitude));
        //showToast("test");
    	//GeoPoint CenterPoint =new GeoPoint((int)(BeginMarker.latitude),(int)(BeginMarker.longitude)); 
      
    	//mMapController.setCenter(CenterPoint);
        GeoPoint CenterPoint =new GeoPoint((int)(39.915* 1E6),(int)(116.404* 1E6));     
        mMapController.setCenter(CenterPoint);
        
        mMapView.regMapViewListener(mBMapManager, new MKMapViewListener() {
        	
			@Override
			public void onMapMoveFinish() {
			}
			
			@Override
			public void onMapLoadFinish() {
				//showToast("Map Loaded！");
			}
			
			@Override
			public void onMapAnimationFinish() {
				
			}
			
			@Override
			public void onGetCurrentMap(Bitmap arg0) {
				
			}
			@Override
			public void onClickMapPoi(MapPoi arg0) {
				if (arg0 != null){
					showToast(arg0.strText);
				}
			}
		});
        //set the touch listener function
        MKMapTouchListener mapTouchListener = new MKMapTouchListener(){  
            @Override  
            public void onMapClick(GeoPoint point) {  
            }  
      
            @Override  
            public void onMapDoubleClick(GeoPoint point) {  
                
            }  
      
            @Override  
            public void onMapLongClick(GeoPoint point) {  
            	addEndMarker(point);
            }  
        };  
        mMapView.regMapTouchListner(mapTouchListener); 
        
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

    private void showToast(String msg){  
        if(mToast == null){  
            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);  
        }else{  
            mToast.setText(msg);  
            mToast.setDuration(Toast.LENGTH_SHORT);
        }  
        mToast.show();  
    } 
	private void addBeginMarker(LocationData location){
		MyLocationOverlay myLocationOverlay = new MyLocationOverlay(mMapView);
		myLocationOverlay.setData(location);  
		mMapView.getOverlays().add(myLocationOverlay);  
		mMapView.refresh(); 
	}
	private void addEndMarker(GeoPoint point){
		if((int)EndMarker.latitude!=0){
			showToast("you only can set one destination");
		}
		else{
			MyLocationOverlay myLocationOverlay = new MyLocationOverlay(mMapView);  
			LocationData location =new LocationData();
			location.latitude=point.getLatitudeE6()/1E6;
			location.longitude=point.getLongitudeE6()/1E6;
			myLocationOverlay.setData(location);  
			mMapView.getOverlays().add(myLocationOverlay);  
			mMapView.refresh(); 
			EndMarker.latitude=location.latitude;
			EndMarker.longitude=location.longitude;
		}
		
	}
	public class BDLocationListenerImpl implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			if(location == null) {
				return;
			}
			if (!(location.getLocType() == 61 || location.getLocType() == 161)) {
				return;
			}
			//if location !=null ,then it means that we successfully get the location
			mLocating=true;
			// save the lat and lon  to the BeginMarker
			BeginMarker.latitude=location.getLatitude();
			BeginMarker.longitude=location.getLongitude();
			//mLocationArray.add(BeginMarker);
		}
            
		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub
			
		}
            
	}
	protected void saveDataToDB() {
		// TODO Auto-generated method stub
		// the parameter of this activity

		
		// add the path of this activity
		mDBManager.addActivityPaths(activityPaths);
		
		// close the manager
		mDBManager.closeDB();
	}
	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}
	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}
	@Override
	protected void onDestroy() {
		mMapView.destroy();
		
		if(mBMapManager != null){
			mBMapManager.destroy();
			mBMapManager = null;
		}
		super.onDestroy();
	}
}

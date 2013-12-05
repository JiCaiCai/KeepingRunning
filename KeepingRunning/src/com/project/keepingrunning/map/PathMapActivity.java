package com.project.keepingrunning.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.project.keepingrunning.R;
import com.project.keepingrunning.frame.AcitvityPathComparator;
import com.project.keepingrunning.frame.Constant;
import com.project.keepingrunning.frame.DBManager;
import com.project.keepingrunning.object.ActivityPath;

public class PathMapActivity extends SherlockActivity {
	private Toast mToast;
	private BMapManager mBMapManager;
	/**
	 * MapView main map view 
	 */
	private MapView mMapView = null;
	/**
	 * use MapController to control the map
	 */
	private MapController mMapController = null;
	/**
	 * MKMapViewListener the handle the event
	 */
	MKMapViewListener mMapListener = null;
	
	private DBManager mDBManager = null;
	private ActionBar mActionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		
		
		setContentView(R.layout.activity_path_map);
		
		mMapView = (MapView) findViewById(R.id.bmapView);
		
		 /**
         * get map controller
         */
        mMapController = mMapView.getController();
        mMapController.enableClick(true);
        mMapController.setZoom(16);
        mMapView.setBuiltInZoomControls(true);
        
        mDBManager = new DBManager(this);
        
        List<GeoPoint> geoPointList = setRoute();
        
        //set start point at the center point
        mMapController.setCenter(geoPointList.get(0));
        
        mMapView.regMapViewListener(mBMapManager, new MKMapViewListener() {
        	
			@Override
			public void onMapMoveFinish() {
			}
			
			@Override
			public void onMapLoadFinish() {
				showToast("Map Loaded！");
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
        
        // use getSupportActionBar to get the ActionBar instance  
        mActionBar = getSupportActionBar(); 
        
        // hide Title  
        mActionBar.setDisplayShowTitleEnabled(true);  
        // hide Home LOGO  
        mActionBar.setDisplayShowHomeEnabled(true); 
        // show arrow
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(this.getIntent().getStringExtra(Constant.START_TIME));
	}

	private List<GeoPoint> setRoute () {
		int id = this.getIntent().getIntExtra(Constant.ID, 0);
		List<ActivityPath> result = mDBManager.getPathActivities(id);
		Collections.sort(result, new AcitvityPathComparator());
		
		List<GeoPoint> geoPointList = new ArrayList<GeoPoint>();
		/**
         * GeoPoint is for storing latitudes and longitude
         */
		for (ActivityPath ap : result) {
			GeoPoint p = new GeoPoint((int)(ap.getLatitude() * 1E6), (int)(ap.getLongitude()* 1E6));
			geoPointList.add(p);
		} 
		
		GeoPoint start = geoPointList.get(0);
		GeoPoint stop  = geoPointList.get(geoPointList.size()-1);
		
		GeoPoint[] step = geoPointList.toArray(new GeoPoint[geoPointList.size()]);
		
		GeoPoint [][] routeData = new GeoPoint[1][];
		routeData[0] = step;
		
		//use step to build a MKRoute
		MKRoute route = new MKRoute();
		route.customizeRoute(start, stop, routeData);
        
		RouteOverlay routeOverlay = new RouteOverlay(PathMapActivity.this, mMapView);
		routeOverlay.setData(route);
		
		mMapView.getOverlays().add(routeOverlay);
		mMapView.refresh();
		
		return geoPointList;
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

	
	
	 /** 
     * show Toast message 
     * @param msg 
     */  
    private void showToast(String msg){  
        if(mToast == null){  
            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);  
        }else{  
            mToast.setText(msg);  
            mToast.setDuration(Toast.LENGTH_SHORT);
        }  
        mToast.show();  
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


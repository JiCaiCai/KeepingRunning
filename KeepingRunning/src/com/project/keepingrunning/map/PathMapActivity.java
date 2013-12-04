package com.project.keepingrunning.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

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
import com.project.keepingrunning.frame.DBManager;
import com.project.keepingrunning.object.ActivityPath;

public class PathMapActivity extends Activity {
	private Toast mToast;
	private BMapManager mBMapManager;
	/**
	 * MapView 是地图主控件
	 */
	private MapView mMapView = null;
	/**
	 * 用MapController完成地图控制
	 */
	private MapController mMapController = null;
	/**
	 * MKMapViewListener 用于处理地图事件回调
	 */
	MKMapViewListener mMapListener = null;
	
	private DBManager mDBManager = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * 使用地图sdk前需先初始化BMapManager，这个必须在setContentView()先初始化
		 */
		mBMapManager = new BMapManager(this);
		
		//第一个参数是API key,
		//第二个参数是常用事件监听，用来处理通常的网络错误，授权验证错误等，你也可以不添加这个回调接口
		mBMapManager.init("170a1e97ef2248b6a5811482c5f4e2ac", new MKGeneralListener() {
			
			//授权错误的时候调用的回调函数
			@Override
			public void onGetPermissionState(int iError) {
				if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
					showToast("API KEY错误, 请检查！");
	            }
			}
			
			//一些网络状态的错误处理回调函数
			@Override
			public void onGetNetworkState(int iError) {
				if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
					Toast.makeText(getApplication(), "您的网络出错啦！", Toast.LENGTH_LONG).show();
	            }
			}
		});
		
		
		setContentView(R.layout.activity_path_map);
		
		mMapView = (MapView) findViewById(R.id.bmapView);
		
		  /**
         * 获取地图控制器
         */
        mMapController = mMapView.getController();
        /**
         *  设置地图是否响应点击事件  .
         */
        mMapController.enableClick(true);
        /**
         * 设置地图缩放级别
         */
        mMapController.setZoom(15);
        
        /**
         * 显示内置缩放控件
         */
        mMapView.setBuiltInZoomControls(true);
        
        mDBManager = new DBManager(this);
        
        List<GeoPoint> geoPointList = setRoute();
        
        //设置p地方为中心点
        mMapController.setCenter(geoPointList.get(0));
        
        mMapView.regMapViewListener(mBMapManager, new MKMapViewListener() {
			
        	
        	/**
        	 * 地图移动完成时会回调此接口 方法
        	 */
			@Override
			public void onMapMoveFinish() {
				showToast("地图移动完毕！");
			}
			
			/**
			 * 地图加载完毕回调此接口方法
			 */
			@Override
			public void onMapLoadFinish() {
				showToast("地图载入完毕！");
			}
			
			/**
			 *  地图完成带动画的操作（如: animationTo()）后，此回调被触发
			 */
			@Override
			public void onMapAnimationFinish() {
				
			}
			
			/**
			 *  当调用过 mMapView.getCurrentMap()后，此回调会被触发
			 *  可在此保存截图至存储设备
			 */
			@Override
			public void onGetCurrentMap(Bitmap arg0) {
				
			}
			
			/**
			 * 点击地图上被标记的点回调此方法
			 * 
			 */
			@Override
			public void onClickMapPoi(MapPoi arg0) {
				if (arg0 != null){
					showToast(arg0.strText);
				}
			}
		});
        
        
	}

	private List<GeoPoint> setRoute () {
		int id = this.getIntent().getIntExtra("id", 0);
		List<ActivityPath> result = mDBManager.getPathActivities(id);
		Collections.sort(result, new AcitvityPathComparator());
		
		List<GeoPoint> geoPointList = new ArrayList<GeoPoint>();
		/**
         * 保存精度和纬度的类,
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
		//用站点数据构建一个MKRoute
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
    	//MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		mMapView.onResume();
		super.onResume();
	}



	@Override
	protected void onPause() {
		//MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		//MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		mMapView.destroy();
		
		//退出应用调用BMapManager的destroy()方法
		if(mBMapManager != null){
			mBMapManager.destroy();
			mBMapManager = null;
		}
		
		super.onDestroy();
	}

	
	
	 /** 
     * 显示Toast消息 
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
	
	
}


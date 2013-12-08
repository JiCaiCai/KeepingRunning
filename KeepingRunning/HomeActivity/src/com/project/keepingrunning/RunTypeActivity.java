package com.project.keepingrunning;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.baidu.mapapi.map.LocationData;

public class RunTypeActivity extends SherlockFragmentActivity implements ActionBar.TabListener, OnPageChangeListener {
	
	// top tab titles
	private String [] mTabTitles; 
	
	private ViewPager mViewPager;
	
	private List<Fragment> mFragmentList; 
	
	private ActionBar mActionBar; 
	
	private double[] locationArray=new double[4];
	
	//this boolean is used to check is it from the SetDestination
	private boolean isFromSetDestination=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run_type);
		//this part is used to get the data from the SetDestination
		if(getIntent().getDoubleArrayExtra("LocationArray")!=null){
			locationArray=getIntent().getDoubleArrayExtra("LocationArray");
			isFromSetDestination=true;
		}
		else{
			isFromSetDestination=false;
		}
		mTabTitles = getResources().getStringArray(R.array.run_type);
		mFragmentList =  new ArrayList<Fragment>();
		
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		// set the Adapter  
        mViewPager.setAdapter(new TabPagerAdapter(getSupportFragmentManager(), mFragmentList));  
        // set listener 
        mViewPager.setOnPageChangeListener(this);
        
        // use getSupportActionBar to get the ActionBar instance  
        mActionBar = getSupportActionBar(); 
        
        // hide Title  
        mActionBar.setDisplayShowTitleEnabled(true);  
        // hide Home LOGO  
        mActionBar.setDisplayShowHomeEnabled(true); 
        // show arrow
        mActionBar.setDisplayHomeAsUpEnabled(true);
        // Tab navigation mode 
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        //add Tab to ActionBar and set the TabListener  
        for(int i=0; i<mTabTitles.length; i++){  
        	 if(isFromSetDestination==true&&i==2){
        		 break;
        	 }
             ActionBar.Tab tab = mActionBar.newTab();  
             tab.setText(mTabTitles[i]);  
             tab.setTabListener(this);  
             mActionBar.addTab(tab, i);  
        }
        
        
        // the first type of running
        Fragment typeOne = new ItemFragment();  
        Bundle args = new Bundle();  
        args.putString("run_type", getString(R.string.run_type_one));  
        args.putInt("layout_id", R.layout.fragment_run_type_one);
        if(isFromSetDestination){
        	args.putDoubleArray("locationArray", locationArray);
        }
        typeOne.setArguments(args);  
              
        mFragmentList.add(typeOne);  
        
        // the second type of running
        typeOne = new ItemFragment();  
        args = new Bundle();  
        args.putString("run_type", getString(R.string.run_type_two));  
        args.putInt("layout_id", R.layout.fragment_run_type_two);
        if(isFromSetDestination){
        	args.putDoubleArray("locationArray", locationArray);
        }
        typeOne.setArguments(args);  
              
        mFragmentList.add(typeOne);  
        
        // the third type of running
        typeOne = new ItemFragment();  
        args = new Bundle();  
        args.putString("run_type", getString(R.string.run_type_three));  
        args.putInt("layout_id", R.layout.fragment_run_type_three);
        typeOne.setArguments(args);  
        if(!isFromSetDestination){     
        	mFragmentList.add(typeOne); 
        }
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {  
        case android.R.id.home:  
        	finish();
            break;  
        default:  
            break;  
        }  
        return super.onOptionsItemSelected(item);  
	}


	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		mActionBar.setSelectedNavigationItem(arg0);
	}



	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		mViewPager.setCurrentItem(tab.getPosition());
	}



	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
	}

}

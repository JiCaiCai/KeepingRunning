package com.project.keepingrunning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.roy.sherloktest.R;

public class ItemFragment extends SherlockFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View contextView = inflater.inflate(R.layout.fragment_item, container, false);  
        TextView mTextView = (TextView) contextView.findViewById(R.id.textview);  
          
        //获取Activity传递过来的参数  
        Bundle mBundle = getArguments();  
        String title = mBundle.getString("arg");
          
        mTextView.setText(title);  
          
        return contextView;  
	}
	
}

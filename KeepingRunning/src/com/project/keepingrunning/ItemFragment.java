package com.project.keepingrunning;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.project.keepingrunning.map.PathMapActivity;

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
        Button mToMap = (Button) contextView.findViewById(R.id.tomap);
        
        mToMap.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(getActivity(), PathMapActivity.class);
				startActivity(intent);
			}
		});
          
        //获取Activity传递过来的参数  
        Bundle mBundle = getArguments();  
        String title = mBundle.getString("arg");
          
        mTextView.setText(title);  
          
        return contextView;  
	}
	
}

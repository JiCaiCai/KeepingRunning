package com.project.keepingrunning;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.project.keepingrunning.frame.Constant;

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
		// obtain the data
        Bundle mBundle = getArguments();  
		int layoutID = mBundle.getInt("layout_id");
		View contextView = inflater.inflate(layoutID, container, false);
		
		switch (layoutID) {
		case R.layout.fragment_run_type_one:
			initTypeOne(contextView);
			break;
		case R.layout.fragment_run_type_two:
			initTypeTwo(contextView);
			break;
		case R.layout.fragment_run_type_three:
			initTypeThree(contextView);
			break;

		default:
			break;
		}
		
        return contextView;  
	}

	private void initTypeThree(View view) {
		// TODO Auto-generated method stub
		SeekBar seekRunDuration = (SeekBar) view.findViewById(R.id.seek_run_length);
		final TextView runDuration = (TextView) view.findViewById(R.id.run_length);
		Button run = (Button) view.findViewById(R.id.start_run);
		
		run.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				double distance = Double.parseDouble(runDuration.getText().toString());
				if (distance == 0) {
					Toast.makeText(getActivity(), "Distance should not be 0", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(getActivity(), RunningActivity.class);
				intent.putExtra(Constant.SOURCE, 3);
				intent.putExtra(Constant.DISTANCE, (int)(distance*1000));
				startActivity(intent);
				getActivity().finish();
			}
		});
		
		//  set the initial value of seekbar  
		seekRunDuration.setMax(200);  
		seekRunDuration.setProgress(0);  
        runDuration.setText(formatDouble((double)seekRunDuration.getProgress()/10));  
		
        // add listener
        seekRunDuration.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				runDuration.setText(formatDouble((double)progress/10));  
			}

		});
	}

	private void initTypeTwo(View view) {
		// TODO Auto-generated method stub
		SeekBar seekRunDuration = (SeekBar) view.findViewById(R.id.seek_run_duration);
		final TextView runDuration = (TextView) view.findViewById(R.id.run_duration);
		Button run = (Button) view.findViewById(R.id.start_run);
		
		run.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int time = Integer.parseInt((runDuration.getText().toString()));
				if (time == 0) {
					Toast.makeText(getActivity(), "Time should not be 0", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(getActivity(), RunningActivity.class);
				intent.putExtra(Constant.SOURCE, 2);
				intent.putExtra(Constant.TIME, time*60);
				startActivity(intent);
				getActivity().finish();
			}
		});
		
		//  set the initial value of seekbar  
		seekRunDuration.setMax(90);  
		seekRunDuration.setProgress(0);  
        runDuration.setText(seekRunDuration.getProgress()+"");  
		
        // add listener
        seekRunDuration.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				runDuration.setText(progress+"");  
			}
		});
	}

	private void initTypeOne(View view) {
		// TODO Auto-generated method stub
		Button run = (Button) view.findViewById(R.id.start_run);
		
		run.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), RunningActivity.class);
				intent.putExtra(Constant.SOURCE, 1);
				startActivity(intent);
				getActivity().finish();
			}
		});
	}
	
	/**
	 * format double data to string
	 * @param value
	 * @return
	 */
	private String formatDouble(double value) {
		// TODO Auto-generated method stub
		String str = String.valueOf(value);
		if (str.indexOf(".") == -1) {
			str = str + ".0";
		}
		return str;
	}
	
}

package com.project.keepingrunning;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class HomeActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		final Button freeRunButton = (Button) findViewById(R.id.free_run);
        freeRunButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent intent=null;
                intent=new Intent(HomeActivity.this,RunTypeActivity.class);
                startActivity(intent);
            }
        });
        final Button setDestinationButton=(Button) findViewById(R.id.set_destination);
        setDestinationButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=null;
                intent=new Intent(HomeActivity.this,SetDestination.class);
                startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getSupportMenuInflater().inflate(R.menu.menu_home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (item.getItemId()) {  
        case R.id.activity_record : 
        	intent = new Intent(this, RecordActivity.class);
        	startActivity(intent);
//            Toast.makeText(this, "menu_record", Toast.LENGTH_SHORT).show(); 
            break;  
        case R.id.start_activity:
        	intent = new Intent(this, RunTypeActivity.class);
        	startActivity(intent);
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

}
//	private OnClickListener setDestinationListener = new OnClickListener() {
//	public void onClick(View local_view) {
//	
//				Intent setDestinationIntent = new Intent(HomeActivity.this,
//						SetDestination.class);
//				startActivity(setDestinationIntent);
//	
//	}
//};
//private OnClickListener setFreeRun = new OnClickListener() {
//	public void onClick(View local_view) {
//	
//				Intent freeRunIntent = new Intent(HomeActivity.this,
//						RunTypeActivity.class);
//				startActivity(freeRunIntent);
//	
//	}
//};

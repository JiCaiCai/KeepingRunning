package com.project.keepingrunning;

import android.content.Intent;
import android.os.Bundle;
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

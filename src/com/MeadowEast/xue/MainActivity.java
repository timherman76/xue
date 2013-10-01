package com.MeadowEast.xue;

import java.io.File;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	Button ecButton, ceButton, exitButton;
	public static File filesDir;
	public static String mode;
	static final String TAG = "XUE MainActivity";
	
	static final String DATA_FILE = "vocabUTF8.txt";
	static final String DATA_FILE_DIR_URL = "http://www.meadoweast.com/capstone/";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ecButton   = (Button) findViewById(R.id.ecButton);
        ceButton   = (Button) findViewById(R.id.ceButton);
        exitButton = (Button) findViewById(R.id.exitButton);
    	ecButton.setOnClickListener(this);
    	ceButton.setOnClickListener(this);
    	exitButton.setOnClickListener(this);
        File sdCard = Environment.getExternalStorageDirectory();
		filesDir = new File (sdCard.getAbsolutePath() + "/Android/data/com.MeadowEast.xue/files");
		Log.d(TAG, "xxx filesDir="+filesDir);
		
		//perform update check
		AllCards.updateCheck();
		
		File logfilehandle = new File(MainActivity.filesDir, "ChineseEnglish" + ".log.txt");
		ProgressLog log = ProgressLog.readFromFile(logfilehandle);
    }

    public void onClick(View v){
    	Intent i;
    	switch (v.getId()){
	    	case R.id.ecButton:
	    		mode = "ec";
	    		i = new Intent(this, LearnActivity.class);
	    		startActivity(i);
				break;
	    	case R.id.ceButton:
	    		mode = "ce";
	    		i = new Intent(this, LearnActivity.class);
	    		startActivity(i);
				break;
	    	case R.id.exitButton:
	    		finish();
				break;
    	}
    }
   
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	boolean result = false;
    	int itemId = item.getItemId();
    	
    	switch (itemId){
    	
    		case R.id.menu_settings:
    			// Launch Preference activity
    		    Intent i = new Intent(MainActivity.this, AppPreferenceActivity.class);
    		    startActivity(i);
    			result = true;
    			break;
    			
    		default:
    			result = super.onOptionsItemSelected(item);
    			break;
    	
    	}
    	
    	return result; 
    }
    
    
    
    
	    
    
    
}

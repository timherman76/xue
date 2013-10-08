package com.MeadowEast.xue;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
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
    
    public boolean onPrepareOptionsMenu(Menu menu)
    {
    	//hide progress menu option if no valid log file...
        MenuItem showProgress = menu.findItem(R.id.menu_show_progress);      
        
        File logFile = new File(MainActivity.filesDir, "EnglishChinese" + ".log.txt");
        
        boolean logFileValid = logFile.exists() && logFile.canRead(); 
        
        if(logFileValid) 
        {           
        	showProgress.setVisible(true);
        }
        else
        {
        	showProgress.setVisible(false);
        }
        return true;
    }
    
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	boolean result = false;
    	int itemId = item.getItemId();
    	Intent i = null;
    	switch (itemId){
    	
    		case R.id.menu_settings:
    			// Launch Preference activity
    		    i = new Intent(MainActivity.this, AppPreferenceActivity.class);
    		    result = true;
    			break;
    			
    		case R.id.menu_show_progress:
    			// Launch Progress activity
    		    i = new Intent(this, ProgressInfoActivity.class);
    		    result = true;
    			break;	
    		
    		default:
    			result = super.onOptionsItemSelected(item);
    			break;
    	
    	}
    	
    	if ( i != null )
    	{
    		startActivity(i);
    	}
    	
    	return result; 
    }
    
    
    
    
	    
    
    
}

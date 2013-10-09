package com.MeadowEast.xue;

import java.io.IOException;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LearnActivity extends Activity implements OnClickListener, OnLongClickListener {
	static final String TAG = "LearnActivity";
	static final int ECDECKSIZE_DEFAULT = 40;
	static final int CEDECKSIZE_DEFAULT = 60;
	static final int ECTARGET_DEFAULT = 750;
	static final int CETARGET_DEFAULT = 750;
	

	public static MediaPlayer correctSound = null; 
	public static MediaPlayer incorrectSound = null;
	
	LearningProject lp;
	int itemsShown;
	TextView prompt, answer, other, status, timer;
	Button advance, okay;
	
	ScheduledThreadPoolExecutor timerExecutor = null;
	
	SharedPreferences preferences;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        Log.d(TAG, "Entering onCreate");

        //init prefs
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        
		//set up audio
        try
        {
        	correctSound = MediaPlayer.create(this, R.raw.correct_sound);
        	incorrectSound = MediaPlayer.create(this, R.raw.incorrect_sound);
        }
        catch ( Exception ex)
        {
        	String msg = ex.getMessage();
        	Log.e(TAG, msg, ex);
        }
        
        itemsShown = 0;
        prompt  = (TextView) findViewById(R.id.promptTextView);
        status  = (TextView) findViewById(R.id.statusTextView);
        other   = (TextView) findViewById(R.id.otherTextView);
        answer  = (TextView) findViewById(R.id.answerTextView);
        advance  = (Button) findViewById(R.id.advanceButton);
        okay     = (Button) findViewById(R.id.okayButton);
        timer  = (TextView) findViewById(R.id.timerTextView);
    	   
    	findViewById(R.id.advanceButton).setOnClickListener(this);
    	findViewById(R.id.okayButton).setOnClickListener(this);
    	
    	findViewById(R.id.promptTextView).setOnLongClickListener(this);
    	findViewById(R.id.answerTextView).setOnLongClickListener(this);
    	findViewById(R.id.otherTextView).setOnLongClickListener(this);
    	
    	//construct project using deck size form prefs
    	if (MainActivity.mode.equals("ec")){
    		int ecDeckSize = ECDECKSIZE_DEFAULT;
    		int ecTarget = ECTARGET_DEFAULT;
    		try
    		{
    			ecDeckSize = Integer.parseInt(preferences.getString(getString(R.string.pref_key_deck_size_ec),
    											ECDECKSIZE_DEFAULT+""));
    		}
    		catch ( Exception ex){}
    		
    		try
    		{
    			ecTarget = Integer.parseInt(preferences.getString(getString(R.string.pref_key_learning_pool_size_ec),
						ECTARGET_DEFAULT+""));
    		}catch (Exception ex){}
    		
    		lp = new EnglishChineseProject(ecDeckSize, ecTarget);
    	} else {
    		String prefKey = getString(R.string.pref_key_deck_size_ce);
    		int ceDeckSize = Integer.parseInt(preferences.getString(prefKey, CEDECKSIZE_DEFAULT+""));
    		int ceTarget = Integer.parseInt(preferences.getString(getString(R.string.pref_key_learning_pool_size_ce),
					CETARGET_DEFAULT+""));
    		lp = new ChineseEnglishProject(ceDeckSize, ceTarget);
    	}
    	
    	
    	clearContent();
    	doAdvance();
    	
    	startTimer();
    }
    
    protected void startTimer(){

    	//schedule timer to update every sec
    	if ( timerExecutor != null ){
    		stopTimer();
    	}
    	
    	timerExecutor = new ScheduledThreadPoolExecutor(1);
    	timerExecutor.scheduleAtFixedRate(new TimerTask(this, 1000), 0, 1000, TimeUnit.MILLISECONDS);
    	
    }
    
    protected void stopTimer(){
    	if ( timerExecutor != null){
    		timerExecutor.shutdownNow();
    		timerExecutor = null;
    	}
    }
    
    protected void updateElapsedTime(int ms){
    	this.lp.incrementElapsedTime(ms);
    	
    	long elapsedTimeMS = lp.getElapsedTime();
    	long elapsedTimeSecs = (elapsedTimeMS/1000) % 60;
    	long elapsedTimeMins = (elapsedTimeMS/1000/60) % 60;
    	long elapsedTimeHours = (elapsedTimeMS/1000/60/60);
    	
    	StringBuilder sb = new StringBuilder("Elapsed time: ");
    	if ( elapsedTimeHours > 0){
    		sb.append(elapsedTimeHours + ":");
    	}

    	sb.append("" + elapsedTimeMins);
    	String secs = elapsedTimeSecs + "";
    	if ( secs.length() < 2){
    		secs = "0" + secs;
    	}
    	sb.append(":" + secs);
    	
    	timer.setText(sb.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
	private void doAdvance(){
		if (itemsShown == 0){
			if (lp.next()){
				prompt.setText(lp.prompt());
				status.setText(lp.deckStatus());

				itemsShown++;
			} else {
				Log.d(TAG, "Error: Deck starts empty");
				throw new IllegalStateException("Error: Deck starts empty.");
			}
		} else if (itemsShown == 1){
			answer.setText(lp.answer());

			itemsShown++;
		} else if (itemsShown == 2){
			other.setText(lp.other());
			advance.setText("next");

			itemsShown++;
		} else if (itemsShown == 3){
			// Got it wrong
			advance.setText("show");
			lp.wrong();
			lp.next();
			clearContent();
			prompt.setText(lp.prompt());

			itemsShown = 1;
			status.setText(lp.deckStatus());
		}


		setOkayButton();
	}
	
	
	public void setOkayButton(){
		switch (itemsShown){
		case 0:
			okay.setVisibility(View.INVISIBLE);
			break;
		case 1:
			okay.setText("undo");
			if ( lp.getNumPriorMoves() > 0 ){
				okay.setVisibility(View.VISIBLE);
			}else{
				okay.setVisibility(View.INVISIBLE);
			}
			break;
		case 2:
			okay.setVisibility(View.INVISIBLE);
			break;
		case 3:
			okay.setText("okay");
			okay.setVisibility(View.VISIBLE);
			break;
		}
	}
	
	private void doUndo(){
		advance.setText("show");
		lp.undo();
		setOkayButton();
		clearContent();
		prompt.setText(lp.prompt());
		itemsShown = 1;
		status.setText(lp.deckStatus());
	}
	
	private void clearContent(){
		prompt.setText("");
		answer.setText("");
		other.setText("");
	}
	
	
	private void doOkay(){
		if (okay.getText().equals("done")){
			try {
				lp.log(lp.queueStatus());
				lp.writeStatus();
				finish();
				return;
				//System.exit(0);
			} catch (IOException e) {
				Log.d(TAG, "couldn't write Status");
				return;
			}
		}
		
		
		// Do nothing unless answer has been seen
		if (itemsShown < 2) return;
		// Got it right
		lp.right();
		if (lp.next()){
			advance.setText("show");
			clearContent();
			prompt.setText(lp.prompt());
			itemsShown = 1;
			status.setText(lp.deckStatus());
			
			setOkayButton();
		} else {
			((ViewManager) advance.getParent()).removeView(advance);
			status.setText("");
			okay.setText("done");
			okay.setVisibility(View.VISIBLE);
			clearContent();
		}
	}
    
    public void onClick(View v){
    	switch (v.getId()){
    	case R.id.advanceButton:
    		doAdvance();
			break;
    	case R.id.okayButton:
    		if ( okay.getText() == "undo"){
    			doUndo();
    		}
    		else{
	    		doOkay();
    		}
			break;
//    	case R.id.promptTextView:
//    	case R.id.answerTextView:
//    	case R.id.otherTextView:
//    		Toast.makeText(this, "Item index: "+lp.currentIndex(), Toast.LENGTH_LONG).show();
//    		break;
    	}
    }

    public boolean onLongClick(View v){
    	switch (v.getId()){
    	case R.id.promptTextView:
    	case R.id.answerTextView:
    	case R.id.otherTextView:
    		Toast.makeText(this, "Item index: "+lp.currentIndex(), Toast.LENGTH_LONG).show();
    		break;
    	}
    	return true;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Log.d(TAG, "llkj");
            new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(R.string.quit)
            .setMessage(R.string.reallyQuit)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    LearnActivity.this.finish();    
                }
            })
            .setNegativeButton(R.string.no, null)
            .show();
            return true;
        } else {
        	return super.onKeyDown(keyCode, event);
        }
    }
    
    @Override
    protected void onPause() {
    	stopTimer();
    	super.onPause();
    }
    
    @Override
    protected void onResume() {
    	startTimer();
    	super.onResume();
    }
    
    @Override
    protected void onDestroy() {
    	stopTimer();
    	super.onDestroy();
    }
    
    
    protected class TimerTask implements Runnable{
    	
    	private LearnActivity learnActivity;
    	private Runnable action;
    	
    	public TimerTask(LearnActivity activity, final int intervalMS){
    		learnActivity = activity;
    		action = new Runnable(){
    			
                public void run() {
        			learnActivity.updateElapsedTime(intervalMS);
                }
    			
    		};
    	}
    	
    	public void run() {
    		try
    		{
    			runOnUiThread(action);
    		}catch (Throwable t){
    			Log.e(TAG, t.getMessage(), t);
    		}
    	}
    }
}

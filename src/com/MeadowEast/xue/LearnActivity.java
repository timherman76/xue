package com.MeadowEast.xue;

import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
	static final int ECDECKSIZE = 40;
	static final int CEDECKSIZE = 60;
	
	LearningProject lp;
	int itemsShown;
	TextView prompt, answer, other, status, timer;
	Button advance, okay;
	
	ScheduledThreadPoolExecutor timerExecutor = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        Log.d(TAG, "Entering onCreate");

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
    	
    	if (MainActivity.mode.equals("ec"))
    		lp = new EnglishChineseProject(ECDECKSIZE);	
    	else
    		lp = new ChineseEnglishProject(CEDECKSIZE);

    	
    	
    	
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
    	
    	StringBuilder sb = new StringBuilder("Elapsed time:");
    	if ( elapsedTimeHours > 0){
    		sb.append(" " + elapsedTimeHours + "h,");
    	}
    	if ( elapsedTimeHours > 0 || elapsedTimeMins > 0){
    		sb.append(" " + elapsedTimeMins + "m,");
    	}
    		
    	sb.append(" " + elapsedTimeSecs + "s");
    	
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

	}
	
	private void clearContent(){
		prompt.setText("");
		answer.setText("");
		other.setText("");
	}
	
	private void doOkay(){
		if (okay.getText().equals("done"))
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
		} else {
			((ViewManager) advance.getParent()).removeView(advance);
			status.setText("");
			okay.setText("done");
			clearContent();
		}
	}
    
    public void onClick(View v){
    	switch (v.getId()){
    	case R.id.advanceButton:
    		doAdvance();
			break;
    	case R.id.okayButton:
    		doOkay();
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

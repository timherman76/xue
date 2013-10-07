package com.MeadowEast.xue;

import java.io.File;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ProgressInfoActivity extends Activity {

	
	static final String TAG = "XUE ProgressInfoActivity";
	
	protected Date startDate = null;
	ProgressLog progressLogEC = null;
	ProgressLog progressLogCE = null;
	
	protected static final int NUM_DAYS = 60;
	public static final String NUM_DECKS = "NUM_DECKS";
	public static final String NUM_ITEMS_LEARNED = "NUM_ITEMS_LEARNED";
	public static final String AVG_ITEMS_PER_DAY = "AVG_ITEMS_PER_DAY";

	public static final String LAST_DECK_DATE = "LAST_DECK_DATE";
	
	public static final DateFormat df = new SimpleDateFormat("MMM d, yyyy");
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_info);

        setPeriod();
        readLogFiles();
    }
    
    
    protected void setPeriod(){
    	//start date is NUM_DAYS days ago, at the beginning of the day
    	Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.DATE, NUM_DAYS*-1);
    	cal.set(Calendar.HOUR_OF_DAY, 0);
    	cal.set(Calendar.MINUTE, 0);
    	cal.set(Calendar.SECOND, 0);
    	cal.set(Calendar.MILLISECOND, 0);
    	
    	startDate = cal.getTime();
    	
    	//set text on screen
    	TextView periodLabel = (TextView) findViewById(R.id.PeriodTextView);
    	periodLabel.setText("Since " + df.format(startDate));
    }
    
    protected void readLogFiles(){
    	File logfilehandle = new File(MainActivity.filesDir, "EnglishChinese" + ".log.txt");
    	progressLogEC = ProgressLog.readFromFile(logfilehandle);
    	List<ProgressLogEntry> entriesEC = progressLogEC.getEntriesFromDate(startDate);
    	Dictionary<String, String> statsEC = setStats(entriesEC);
    	
    	TextView numDecksEC = (TextView) findViewById(R.id.DecksCompECTextView);
    	numDecksEC.setText(statsEC.get(NUM_DECKS));
    	TextView numItemsEC = (TextView) findViewById(R.id.ItemsLearnedECTextView);
    	numItemsEC.setText(statsEC.get(NUM_ITEMS_LEARNED));
    	TextView avgItemsEC = (TextView) findViewById(R.id.AvgItemsPerDayECTextView);
    	avgItemsEC.setText(statsEC.get(AVG_ITEMS_PER_DAY));
    	TextView lastDeckDateEC = (TextView) findViewById(R.id.LastDeckDateECTextView);
    	lastDeckDateEC.setText(statsEC.get(LAST_DECK_DATE));
    	
    	
    	logfilehandle = new File(MainActivity.filesDir, "ChineseEnglish" + ".log.txt");
    	progressLogCE = ProgressLog.readFromFile(logfilehandle);
    	List<ProgressLogEntry> entriesCE = progressLogCE.getEntriesFromDate(startDate);
    	Dictionary<String, String> statsCE = setStats(entriesCE);
    	TextView numDecksCE = (TextView) findViewById(R.id.DecksCompCETextView);
    	numDecksCE.setText(statsCE.get(NUM_DECKS));
    	TextView numItemsCE = (TextView) findViewById(R.id.ItemsLearnedCETextView);
    	numItemsCE.setText(statsCE.get(NUM_ITEMS_LEARNED));
    	TextView avgItemsCE = (TextView) findViewById(R.id.AvgItemsPerDayCETextView);
    	avgItemsCE.setText(statsCE.get(AVG_ITEMS_PER_DAY));
    	
    	
    }

    protected Dictionary<String, String> setStats(List<ProgressLogEntry> entries){
    	Dictionary<String, String> statValues = new Hashtable<String, String>();
    	
    	String lastEntryDate = "";
    	int numDecks = entries.size();
    	int numItemsLearned = 0;
    	if ( entries.size() > 0 ){
    		numItemsLearned = ProgressLog.getNumItemsLearned(entries.get(0), entries.get(entries.size()-1));
    		ProgressLogEntry lastEntry = entries.get(entries.size()-1);
    		lastEntryDate = df.format(lastEntry.CreatedDate);
    		
    	}
    	
    	float avgItemsPerDay = ((float)numItemsLearned)/NUM_DAYS;
    	
    	NumberFormat nf = NumberFormat.getInstance();
    	nf.setMaximumFractionDigits(1);
    	statValues.put(NUM_DECKS, numDecks + "");
    	statValues.put(NUM_ITEMS_LEARNED, numItemsLearned + "");
    	statValues.put(AVG_ITEMS_PER_DAY, nf.format(avgItemsPerDay));
    	statValues.put(LAST_DECK_DATE, lastEntryDate);
    	
    	return statValues;
    }
    
}

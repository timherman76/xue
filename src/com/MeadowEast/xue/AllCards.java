package com.MeadowEast.xue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;

import android.util.Log;

public class AllCards {
	static final String TAG = "CC AllCards";
	
	private static AllCards db = null;
	private static Object dbLock = new Object();
	public static Card getCard(int i){
		return getDB().cardArray[i];
	}
	public static int length(){
		return getDB().cardArray.length;
	}
	
	private Card [] cardArray;
	
	private AllCards() {		
		File file =  new File(MainActivity.filesDir, MainActivity.DATA_FILE);
		ArrayList<Card> allCards = new ArrayList<Card>();
		Log.d(TAG, "File is "+ file);
		Log.d(TAG, "Path is "+ file.getAbsolutePath());

		try {
			FileReader fr = new FileReader ( file );
			Log.d(TAG, "FileReader okay");
			BufferedReader in = new BufferedReader( fr );
			Log.d(TAG, "BufferedReader okay");
			
			String line;
			while ((line = in.readLine(  )) != null){
				String fixedline = new String(line.getBytes(), "utf-8");
				String [] fields = fixedline.split("\\t");
				if (fields.length == 3){
					Card c = new Card(fields[0], fields[1], fields[2]);
					allCards.add(c);
				} else {
					Log.d(TAG, "Bad line: "+fields.length+" elements");
					Log.d(TAG, fixedline);
				}
			}
			in.close();
		}
		catch ( Exception e ) {
			Log.d(TAG, "Unable to get Chinese data from file" );
		}
		cardArray = allCards.toArray(new Card[0]);
	}
		
	
	
	private static Date lastVocabUpdateCheck = null;
	private static final int UPDATE_FREQ_HOURS = 4;
	
	
	private synchronized static AllCards getDB() {
		
		//if db not created, create it
		if ( db == null ){
			//generate new cards from file
			db = new AllCards();
		}
		
		updateCheck();
		
		return db;
	}
	
	
	protected static synchronized void updateCheck()
	{
		boolean needsUpdateCheck = false;
		
		if ( lastVocabUpdateCheck == null ){
			needsUpdateCheck = true;
		} else {
			int hoursSinceLastUpdateCheck = Utils.DateDiffHours(new Date(), lastVocabUpdateCheck);
			needsUpdateCheck = hoursSinceLastUpdateCheck > UPDATE_FREQ_HOURS;
		}

		//if update overdue, create file data file update
		if ( needsUpdateCheck ){
			new CheckForDiffVocabFileTask().execute(MainActivity.DATA_FILE, MainActivity.DATA_FILE_DIR_URL, MainActivity.filesDir.getAbsolutePath());
			lastVocabUpdateCheck = new Date();
		}
	}
	
	
	
	protected static class CheckForDiffVocabFileTask extends CheckForDifferentFileTask{
		
		@Override
		protected void onPostExecute(Boolean result) {
			
			if ( result ){
				//if we need an update queue up a file download in the background
				(new DownloadVocabFileTask()).execute( MainActivity.DATA_FILE, MainActivity.DATA_FILE_DIR_URL, MainActivity.filesDir.getAbsolutePath());
			}
		}
	}
	
	protected static class DownloadVocabFileTask extends DownloadFileTask{
		
		@Override
		protected void onPostExecute(File result) {
			//clear out db
			if ( result != null){
				synchronized(dbLock){
					db = null;
				}
			}
		}
	}
}
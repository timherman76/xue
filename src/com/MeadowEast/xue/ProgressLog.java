package com.MeadowEast.xue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.util.Log;

public class ProgressLog {
	
	final static String TAG = "ProgressLog";
	
	public List<ProgressLogEntry> logEntries = new ArrayList<ProgressLogEntry>();
	
	protected ProgressLog(){
	}
	
	protected static ProgressLog readFromFile(File logFilePath){
		ProgressLog result = null;
		
		try {
			result = new ProgressLog();
			BufferedReader reader = new BufferedReader(new FileReader(logFilePath));
			String line = reader.readLine();
			while ( line != null)
			{
				ProgressLogEntry entry = result.parseLogEntry(line);
				if ( entry != null){
					result.logEntries.add(entry);
				}
				
				line = reader.readLine();
			}
			
			reader.close();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		} 
		
		return result;
	}
	
	
	protected ProgressLogEntry parseLogEntry(String logEntry){
		ProgressLogEntry result = new ProgressLogEntry();
		try{
			//parse log entry line
			//ex: 
			//[08/09/13 12:29,  65   19 + 696 = 715, 2334 + 1256 = 3590, 4370]
			String[] parts = logEntry.split("    ");
			SimpleDateFormat formatter = new SimpleDateFormat("yy/MM/dd hh:mm");
			result.CreatedDate = formatter.parse(parts[0]);
			
			//levels 0  1 + 2
			String vals = parts[1].trim().replace("+", "");
			int level0End = vals.indexOf(" ");
			String level0 = vals.substring(0, level0End);
			result.LevelSizes[0] = Integer.parseInt(level0);
			vals = vals.substring(level0End).trim();
			
			int level1End = vals.indexOf(" ");
			String level1 = vals.substring(0, level1End);
			result.LevelSizes[1] = Integer.parseInt(level1);
			vals = vals.substring(level1End).trim();
			
			int level2End = vals.indexOf(" ");
			String level2 = vals.substring(0, level2End);
			result.LevelSizes[2] = Integer.parseInt(level2);
			vals = vals.substring(level2End).trim();
			
			//levels 3 + 4
			vals = parts[2].trim().replace("+", "");
			int level3End = vals.indexOf(" ");
			String level3 = vals.substring(0, level3End);
			result.LevelSizes[3] = Integer.parseInt(level3);
			vals = vals.substring(level3End).trim();
			
			int level4End = vals.indexOf(" ");
			String level4 = vals.substring(0, level4End);
			result.LevelSizes[4] = Integer.parseInt(level4);
			vals = vals.substring(level4End).trim();

			
		}catch(Exception ex){
			Log.e(TAG, ex.getMessage(), ex);
			result = null;
		}
		
		return result;
	}
	
	public static int getNumItemsLearned(ProgressLogEntry logEntryA, ProgressLogEntry logEntryB){
		int result = 0;
		
		//entries can't be null and can't be the same entry...
		if ( logEntryA != null && logEntryB != null && !logEntryA.CreatedDate.equals(logEntryB.CreatedDate)){
			ProgressLogEntry firstEntry = (logEntryA.CreatedDate.compareTo(logEntryB.CreatedDate) > 0) ? logEntryA : logEntryB;
			ProgressLogEntry lastEntry = (firstEntry == logEntryA) ? logEntryB : logEntryA;
						
			//go through each level from 1 upwards...
			for( int i=1; i < lastEntry.LevelSizes.length; i++){
				//get the difference in #of items...
				int diff = lastEntry.LevelSizes[i] - firstEntry.LevelSizes[i];
				result += diff;
			}
			
			result = (result < 0) ? 0 : result;
		}
		
		return result;
	}
	
	public List<ProgressLogEntry> getEntriesFromDate(Date startDate){
		List<ProgressLogEntry> result = new ArrayList<ProgressLogEntry>();
		
		//assume entries are sorted by date ascending
		for (int i = 0; i < logEntries.size(); i++){
			ProgressLogEntry entry = logEntries.get(i);
		
			if ( startDate.compareTo(entry.CreatedDate) <= 0 ){
				//this entry date is equal to or after the start date, take it and all following entries
				result = logEntries.subList(i, logEntries.size()-1);
				break;
			}
		}
		
		return result;
	}
	
}

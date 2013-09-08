package com.MeadowEast.xue;

import android.os.AsyncTask;

public class CheckForDifferentFileTask extends
		AsyncTask<String, Integer, Boolean> {

	@Override
	protected Boolean doInBackground(String... params) {
		Boolean result = false;
		
		String fileName = params[0];
		String sourceFileDir = params[1];
		String targetFileDir = params[2];
		

    	result = Utils.checkForDifferentFile(fileName, sourceFileDir, targetFileDir);
    	return result;
	}

}

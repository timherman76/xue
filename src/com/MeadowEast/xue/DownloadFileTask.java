package com.MeadowEast.xue;

import java.io.File;

import android.os.AsyncTask;

public class DownloadFileTask extends AsyncTask<String, Integer, File> {


	@Override
	protected File doInBackground(String... params) {
		File result = null;
		
		
		String remoteFileName = params[0];
		String remoteFileDirURL = params[1];
		String downloadDirPath = params[2];
		
		result = Utils.downloadFile(remoteFileDirURL, remoteFileName, downloadDirPath, false);
		return result;
		
	}
	
}

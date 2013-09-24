package com.MeadowEast.xue;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.UUID;

import android.util.Log;

public class Utils {
	

	static final String TAG = (new Utils()).getClass().getName();
	
	public static long DateDiff(Date source, Date target){
		long result = 0;
		if ( source != null && target != null){
			result = source.getTime() - target.getTime();
		}
		return result;
	}
	
	public static int DateDiffMins(Date source, Date target){
		int result = 0;
		long diff = DateDiff(source, target);
		diff /= 1000;  //secs
		diff /= 60;   //mins
		
		result = (int) diff;
		
		return result;
	}
	
	public static int DateDiffHours(Date source, Date target){
		int result = Math.round((float)DateDiffMins(source, target) / 60);
		return result;
	}
	
	/**
	 * Checks to see if the file exists at the source location is different than the one at the target location
	 * 
	 * @param fileName the name of the file 
	 * @param sourceDirPath the path of the source directory
	 * @param targetDirPath the path of the target directory
	 * 
	 * @return true if the source file exists and either the source file is not present or is different than the target file
	 */
	public static boolean checkForDifferentFile(String fileName, String sourceDirPath, String targetDirPath)
	{
		boolean result = false;
		try{
			File targetDir = new File(targetDirPath);
			File targetFile = new File(targetDir, fileName);
			long targetFileSize = targetFile.length();
			long targetFileModified = targetFile.lastModified();
			long sourceFileSize = 0;
			long sourceFileModified = 0;
			
			URI sourceDir = new URI(sourceDirPath);
			URI sourceFilePath = sourceDir.resolve(fileName);
			if ( sourceFilePath.getScheme().startsWith("file"))
			{
				//source file is file path
				File sourceFile = new File(sourceFilePath);
				
				if ( sourceFile.exists() ){
					sourceFileSize = sourceFile.length();
					sourceFileModified = sourceFile.lastModified();
				}
			}
			else if ( sourceFilePath.getScheme().startsWith("http")){
				//source file is http url
				HttpURLConnection urlConn = (HttpURLConnection) sourceFilePath.toURL().openConnection();
				urlConn.setRequestMethod("HEAD");
			    urlConn.connect();	
				sourceFileSize = urlConn.getContentLength();
				sourceFileModified = urlConn.getLastModified();
			}
			//the file is different if...
			
			result = (!targetFile.exists() //target file doesn't exist 
					|| targetFileSize != sourceFileSize //the files have different sizes 
					|| targetFileModified < sourceFileModified);	//the source file has been modified more recently than the target file
			
			
		
		} catch (Exception e){
			Log.e(TAG, e.getMessage(), e);
		}
		
		return result;
	}
	
	
	
	/**
	 * 
	 * @param remoteFileURL  the full URL of the remote file
	 * @param downloadDirPath the local directory to download the file to 
	 * @param overwriteExisting indicates whether to overwrite an existing local file with the new file
	 * @return  the file that was downloaded (or null if not downloaded)
	 */
	public static File downloadFile(String remoteFileDirURL, String remoteFileName, String downloadDirPath, boolean overwriteExisting)
	{
		File file = null;
		File existingFile = null;
		try {
	        //open HTTP connection to the remote file
			URL url = new URL(remoteFileDirURL + remoteFileName);
	        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
	        urlConn.setRequestMethod("GET");
	        urlConn.connect();	
	
	        //create file object for downloadDir and 
	        File downloadDir = new File(downloadDirPath);
	        
	        //create file object for the file to download
	        existingFile = new File(downloadDir,remoteFileName);
	        
	        
	        if ( existingFile.exists() )
	        {
	        	//there is already a local file with the same name..
	        	if ( !overwriteExisting )
	        	{
	        		//give download file unique random name
	        		String tmpDownloadFileName = "tmp_" + UUID.randomUUID();
	        		file = new File(downloadDir, tmpDownloadFileName);
	        	}
	        }
	        else
	        {
	        	//no file with name exists, just download it with same name
	        	file = new File(downloadDir,remoteFileName);
	        	existingFile = null;
	        }
	        
	        //if the file doesn't exist, or if OK to overwrite existing file, then download
	        if ( file != null ) {
	        	//create stream to write the downloaded data into the file we created
		        FileOutputStream fileOutputStream = new FileOutputStream(file);
		        
		        //get input stream from remote url
		        InputStream inputStream = urlConn.getInputStream();
		
		        int totalFileBytes = urlConn.getContentLength();
		        
		        byte[] buffer = new byte[1024];
		        int byteCount = 0; 
		        int bytesDownloaded = 0;
		
		      //read file data from remote stream into local file
		        while ( (byteCount = inputStream.read(buffer)) > 0 ) {
		        	fileOutputStream.write(buffer, 0, byteCount);
		        	bytesDownloaded += byteCount;
		        }
		        
		        //close the output stream when done
		        fileOutputStream.close();
		        
		        boolean result = (bytesDownloaded == totalFileBytes);
		        
		        if ( result && existingFile != null)
		        {	
		        	//need to replace existing file with downloaded file...
		        	
		        	//rename existing file
		        	File targetFile = new File(existingFile.getAbsolutePath());
		        	File existingFileTemp = new File(existingFile.getParentFile(), "tmp_" + UUID.randomUUID());
		        	existingFile.renameTo(existingFileTemp);
		        	
		        	//now rename new downloaded file to prev file name
		        	file.renameTo(targetFile);
		        	
		        	//now delete previous file
		        	existingFile.delete();
		        }
	        }
	
	}catch ( Exception e ){
		Log.e(TAG, e.getMessage(), e);
		e.printStackTrace();
	}
		
	return file;	
}
	

}

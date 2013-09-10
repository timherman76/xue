package com.MeadowEast.xue;

import java.io.*;
import java.net.*;
import java.util.UUID;

import android.os.Environment;
import android.util.Log;

public class Utils {
	
	
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
			URI targetDir = new URI(targetDirPath);
			URI targetFilePath = targetDir.resolve(fileName);
			File targetFile = new File(targetFilePath);
			long targetFileSize = targetFile.length();
			
			
			URI sourceDir = new URI(sourceDirPath);
			URI sourceFilePath = sourceDir.resolve(fileName);
			if ( sourceFilePath.getScheme().startsWith("file"))
			{
				File sourceFile = new File(sourceFilePath);
				
				if ( sourceFile.exists() ){
					long sourceFileSize = sourceFile.length();
					result = (!targetFile.exists() || targetFileSize != sourceFileSize);
				}
			}
			else if ( sourceFilePath.getScheme().startsWith("http")){
				HttpURLConnection urlConn = (HttpURLConnection) sourceFilePath.toURL().openConnection();
				urlConn.setRequestMethod("HEAD");
			    urlConn.connect();	
				int sourceFileSize = urlConn.getContentLength();
				result = (targetFileSize != sourceFileSize);
			}
		
			
			
			
		
		} catch (Exception e){
			Log.e("Utils", e.getMessage(), e);
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
	public static File downloadFile(String remoteFileURL, String downloadDirPath, boolean overwriteExisting)
	{
		File file = null;
		File existingFile = null;
		try {
	        //open HTTP connection to the remote file
			URL url = new URL(remoteFileURL);
	        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
	        urlConn.setRequestMethod("GET");
	        urlConn.setDoOutput(true);
	        urlConn.connect();	
	
	        //get the name of the file to download
	        String remoteFileName = url.getFile();
	        
	        //create file object for downloadDir and 
	        File downloadDir = new File(downloadDirPath);
	        
	        //create file object for the file to download
	        existingFile = new File(downloadDir,remoteFileName);
	        
	        
	        if ( existingFile.exists() )
	        {
	        	//there is already a local file with the same name..
	        	if ( overwriteExisting )
	        	{
	        		//give download file unique random name
	        		file = new File(downloadDir,"tmp_" + UUID.randomUUID());
	        	}
	        }
	        else
	        {
	        	//no file with name exists, just download it with same name
	        	file = new File(downloadDir,remoteFileName);
	        	existingFile = null;
	        }
	        
	        //if the file doesn't exist, or if OK to overwrite existing file, then download
	        if ( file != null )
	        {
	        	//this will be used to write the downloaded data into the file we created
		        FileOutputStream fileOutputStream = new FileOutputStream(file);
		        
		        //get remote stream from url
		        InputStream inputStream = urlConn.getInputStream();
		
		        int totalFileBytes = urlConn.getContentLength();
		        
		        byte[] buffer = new byte[1024];
		        int byteCount = 0; 
		        int bytesDownloaded = 0;
		
		      //read file data from url into local file
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
	
	}catch ( Exception e )
	{
		e.printStackTrace();
	}
		
	return file;	
}
	

}

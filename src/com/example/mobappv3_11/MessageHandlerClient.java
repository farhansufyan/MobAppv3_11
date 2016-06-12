package com.example.mobappv3_11;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MessageHandlerClient extends IntentService {

	String mes; 
	String server_res = "Compilation Done !" ;
	
	UploadFile3 upfn = new UploadFile3() ;
	
	RegMain3 r3 = new RegMain3();
	
	String downloadfilename ;//= "d942ddef4bd55c1b_mergesort.c.out" ;
    //String sdcardpath = "/sdcard/" + downloadfilename; 
//    String serverip = "http://192.168.12.97:80/android_connect/";
    String serverip = "http://54.69.110.134:80/android_connect/";
    //String dwnldFilePath = serverip + "uploads/" + downloadfilename;
    String delUri = serverip + "delfile.php";
	
    // Timing calculation
    long start, end, dt;
    String t1;
    
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
    
    // For notification
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    
    public static final String TAG = "GCM Message Handle";
    
    private Handler handler;
    
    public MessageHandlerClient() {
        super("MHClient");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }
    
    @Override
    public void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        
        // The getMessageType() intent parameter must be the intent you received in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        mes = extras.getString("m");
        
        // android mobile id of device 
     	String m_androidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        
     	// For splitting the path to get the filename. Change from here for use in device.
        String f1 = upfn.uploadFilePath ;
       
        //String f1 = upfn.uploadFilePath2 ;
        //showToast(f1);
        String[] tokens = f1.split("/") ;
        String f2 = tokens[3] ;
//        String f2 = tokens[7] ;  // change here
        String uploadFileName1 = f2 ;
        //showToast(uploadFileName1);
        
        downloadfilename = m_androidId + "_" + upfn.uploadFileName + ".out" ;
//        downloadfilename = m_androidId + "_" + uploadFileName1 + ".out" ;  // change here for device
        //downloadfilename = m_androidId + "_" + "mergesort.c" + ".out" ;  // change here for device
        String dwnldFilePath = serverip + "uploads/" + downloadfilename;
        //System.out.println(dwnldFilePath);
        System.out.println(f2);
        //showToast();
        
        if (mes.length() == server_res.length()) {
        	//System.out.println(mes);
        	
        	//start = SystemClock.uptimeMillis();
        	// download_output_file_from_Server.
        	new DownloadFileAsync().execute(dwnldFilePath);
        	/*
        	end = SystemClock.uptimeMillis();
        	dt = end - start;
        	t1 = String.valueOf(dt);*/
        	//showToast(t1);
        	        	
        } else if (mes == "Error: ") {
        	// use localbroadcast message... as in version-2 
        	//Toast.makeText(getApplicationContext(), mes, Toast.LENGTH_SHORT).show();
        }
        
        //sendMessage(mes) ;
        Log.i("GCM", "Received : (" + messageType + ")  "+ mes);
        
        BroadcastReceiverClient.completeWakefulIntent(intent);
    }

    
    class DownloadFileAsync extends AsyncTask<String, String, String> {
    	   
    	String sdcardpath = "/sdcard/" + downloadfilename;
    	
		@Override
		protected String doInBackground(String... aurl) {
			start = SystemClock.uptimeMillis();
			
			int count;
			try {
				URL url = new URL(aurl[0]);
			
				Log.i("Download Start", "File Downloading ....");
				Log.i("FILE_URLLINK", "File URL is "+url);
			
				URLConnection conexion = url.openConnection();
				conexion.connect();
		
				int lenghtOfFile = conexion.getContentLength();
				//Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);
		
				//String sdcardpath = "/sdcard/" + downloadfilename;
				//System.out.println(sdcardpath);
				
				InputStream input = new BufferedInputStream(url.openStream());
				OutputStream output = new FileOutputStream(sdcardpath);
				//System.out.println("read kar lia");
				byte data[] = new byte[1024];
		
				long total = 0;
		
				while ((count = input.read(data)) != -1) {
					total += count;
					publishProgress(""+(int)((total*100)/lenghtOfFile));
					output.write(data, 0, count);
				}
		
				output.flush();
				output.close();
				input.close();	
			
				Log.i("Download stop", "File Downloading Done.");
			
			} catch (Exception e) {
				e.printStackTrace();
	            Log.i("ERROR ON DOWNLOADING FILES", "ERROR IS" +e);
			}
		
			end = SystemClock.uptimeMillis();
        	dt = end - start;
        	t1 = String.valueOf(dt);
			
			return t1;
		}
	
		@Override
		protected void onPostExecute(String unused) {
			//dismissDialog(DIALOG_DOWNLOAD_PROGRESS);	
			Toast.makeText(getBaseContext(), "Download Time: " + unused, Toast.LENGTH_SHORT).show();
			textshow(sdcardpath) ;
			//deloutfilefromserver() ;
		}
	}
    
    void textshow (String sdcardpath) {
		
		try {
			File myFile = new File(sdcardpath);
			//System.out.println("Path : " + myFile.getAbsolutePath());
			FileInputStream fIn = new FileInputStream(myFile);
			BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
			String aDataRow = "";
			String aBuffer = "";
			while ((aDataRow = myReader.readLine()) != null) {
				aBuffer += aDataRow + "\n";
			}
			
			Log.i("Textview", "Showing output of app.");
			//dnldfile.setText(aBuffer);
			sendMessage(aBuffer) ;
			myReader.close();
			//Toast.makeText(getBaseContext(), "Done reading SD.", Toast.LENGTH_SHORT).show();
			deleteFile();
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void deleteFile() {
		File folder = Environment.getExternalStorageDirectory();
		String fileName = folder.getPath() + "/" + downloadfilename;
		//System.out.println(fileName);
		File myFile = new File(fileName);
		Log.i("SDCard Delete", "Delete from SDCard");
		if(myFile.exists())
			myFile.delete();
	}

    /* Send an Intent with an action named "gcm-result". 
     * The Intent sent should be received by the ReceiverActivity.
     */
	private void sendMessage(String message) {
		Log.d("sender", "Broadcasting message");
		Intent intent = new Intent("gcm-result");
		// You can also include some extra data.
		intent.putExtra("message", message);
		
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	 
	}
    
    public void showToast(final String f){
        handler.post(new Runnable() {
            public void run() {
            	//Toast.makeText(getApplicationContext(),mes , Toast.LENGTH_SHORT).show();
            	Toast.makeText(getApplicationContext(), "Download Tidme: " + f , Toast.LENGTH_LONG).show();
            }
         }); 
    }
}
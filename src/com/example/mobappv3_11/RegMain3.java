package com.example.mobappv3_11;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class RegMain3 extends Activity implements OnClickListener{

	private static final int MY_INTENT_CLICK = 302;
	
	TextView textpath;
	EditText fileView;
	Button btn_brws, load, save, run;
	String selpath;
	
	// For GoogleCloudMessaging service
    GoogleCloudMessaging gcm;
    String regid;
    String PROJECT_NUMBER = "1012497861508";
    
    // for mobile id of device
    String m_androidId;
    
    Context context;
    
    int outfile = 0 ;
    
    // Timing calculation
    long start, end, tt;
    
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reg_main3);
		
		// android mobile id of device 
     	m_androidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		
		context = getApplicationContext();
		
		new Thread(new Runnable() {
            public void run() {
                 runOnUiThread(new Runnable() {
                        public void run() {
                            //messageText.setText("uploading started.....");
                        }
                    });
                 
            }
          }).start();  
		
		 getRegId() ;
		
		//text to get the link of the selected file
		textpath = (TextView) findViewById(R.id.tvpath);
		
		fileView = (EditText) findViewById(R.id.etData);
		
		//button to browse the file from sdcard
		btn_brws = (Button) findViewById(R.id.brwsbtn);
		btn_brws.setOnClickListener(this);
		
		load = (Button) findViewById(R.id.loadbtn);
		load.setOnClickListener(this);
		
		save = (Button) findViewById(R.id.savebtn);
		save.setOnClickListener(this);
		
		run = (Button) findViewById(R.id.runbtn);
		run.setOnClickListener(this);
		
	}
	
//	String serverip = "http://192.168.12.97:80/android_connect/";
	String serverip = "http://54.69.110.134:80/android_connect/";
	String postReceiverUrl = serverip + "farhan.php";

	public void getRegId() {
		new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + regid;
                    Log.i("GCM",  msg);  
                    
                    // new code start here
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        			nameValuePairs.add(new BasicNameValuePair("gcm_regid", regid));
        			nameValuePairs.add(new BasicNameValuePair("mid", m_androidId));
        			
        			HttpClient httpclient = new DefaultHttpClient();
        		    HttpPost httppost = new HttpPost(postReceiverUrl);
        		    HttpParams httpParameters = new BasicHttpParams();
        		    httpclient = new DefaultHttpClient(httpParameters); 
        			
        			try {
        			    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        			    HttpResponse response;
        			    response = httpclient.execute(httppost);
        			    StatusLine statusLine = response.getStatusLine();
        			    
        			    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
        			    	Log.e("Google", "Server Responded OK");
        			    } else {
        			    	response.getEntity().getContent().close();
        			        throw new IOException(statusLine.getReasonPhrase());
        			    }
        			} catch (Exception e) {
        			    e.printStackTrace();
        			}
                    //new code end here
                    
        			// Send mobile_id, user_id, regid to store in MySql 
                    //storeregId (context, regid);
                    /*
                    File sdCard = Environment.getExternalStorageDirectory();
	    			
	    			File file = new File(sdCard, "regid.txt");

	    			FileOutputStream fos = new FileOutputStream(file);
	    			OutputStreamWriter osw = new OutputStreamWriter(fos);

	    			String dataToSave = regid ;
	    			
	    			// write the string to the file
	    			osw.write(dataToSave);
	    			osw.flush();
	    			osw.close();
                    */
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                } catch (Exception e) {
                	msg = "Error :" + e.getMessage();
                }
                //System.out.println(msg) ;
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                //tvoutput.setText(msg + "\n");
            }
        }.execute(null, null, null);
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.brwsbtn:
			//browsefile();
			// Reading data from external storage
	        if (isSDCardReadable()) {
	        	browsefile();
	        	
	        } else {
	           	// SD Card Not Available
				Toast.makeText(getBaseContext(), "SD Card Not Available", Toast.LENGTH_SHORT).show();
	        }
			break;
			
		case R.id.loadbtn:
			//readFromsdCard(sel_file_path);
			// Reading data from external storage
	        if (isSDCardReadable()) {
	        	readFromsdCard(selpath);
	        	
	        } else {
	           	// SD Card Not Available
				Toast.makeText(getBaseContext(), "SD Card Not Available", Toast.LENGTH_SHORT).show();
	        }
			break;
			
		case R.id.savebtn:
			//svfl.save();
			// Writing data to external storage
			if (isSDCardWritable()) {
				writeTosdCard();
			} else {
				// SD Card Not Available
				Toast.makeText(getBaseContext(),"SD Card Not Available", Toast.LENGTH_SHORT).show();
				}
			break;
		case R.id.runbtn:
			Intent intent = new Intent (RegMain3.this, UploadFile3.class);
			
			// Change start here for any file path
			// Pass the file path to another activity
//			Bundle bundle = new Bundle();
			// Add path to bundle
//			bundle.putString("key", selpath);
			// Add the bundle to intent
//			intent.putExtras(bundle);
			// Change end here
			
			startActivity(intent);
			break;
		}	
	}
	
	// Checks if SD Card is available to read 
		public boolean isSDCardReadable() {
			String status = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(status)|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(status)) {
				return true;
			}
			return false;
		}

		private void browsefile() {
			Intent intent = new Intent();
			intent.setType("text/*"); //open only for text file
			intent.setAction(Intent.ACTION_GET_CONTENT);
			
			// broadcast the supported file or image and the choose file to open 
			startActivityForResult(Intent.createChooser(intent, "Select file"), MY_INTENT_CLICK);
		}
		
		// Get the content of the activity after clicking the text file
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			if (resultCode == RESULT_OK) {
				if (requestCode == MY_INTENT_CLICK) {
					if (data == null) return;
					
					//String sel_file_path;
					Uri sel_file_uri = data.getData();
								
					selpath = FileTextPath.getPath (getApplicationContext(), sel_file_uri);
					//textpath.setText("File path: \n" + selpath);
					textpath.setText(selpath);
					
					//readFromsdCard();	
				}
			}
		}

		private void readFromsdCard(String sel_file_path) {
			
			try {
				// SD Card Storage
				File file = new File(sel_file_path);
				FileInputStream fis = new FileInputStream(file);
				InputStreamReader isr = new InputStreamReader(fis);

				char[] inputBuffer = new char[100];
				String dataToRead = "";
				int charRead;

				while ((charRead = isr.read(inputBuffer)) > 0) {				
					String readString = String.copyValueOf(inputBuffer,0, charRead);
					dataToRead += readString;
					inputBuffer = new char[100];
				}

				//Log.d("MainInTry",  "BuffContent:" +dataToRead);
				
				// set the EditText to the text that has been raed
				fileView.setText(dataToRead);

				// success message
				Toast.makeText(getBaseContext(), "File loaded successfilly",Toast.LENGTH_SHORT).show();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Checks if SD Card is available for write
		public boolean isSDCardWritable() {
			String status = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(status)) {
				return true;
			}
			return false;
		}
		
		private void writeTosdCard () {
			String dataToSave = fileView.getText().toString();
			
			try {
				// SD Card Storage
				File file = new File(selpath);
				FileOutputStream fos = new FileOutputStream(file);
				OutputStreamWriter osw = new OutputStreamWriter(fos);

				// write the string to the file
				osw.write(dataToSave);
				osw.flush();
				osw.close();

				// success message
				Toast.makeText(getBaseContext(), "File saved successfilly", Toast.LENGTH_SHORT).show();

				// clears the EditText
				fileView.setText("");

			} catch (IOException e) {
				e.printStackTrace();
				}
		}	
}


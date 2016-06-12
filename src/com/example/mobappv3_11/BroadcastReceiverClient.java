package com.example.mobappv3_11;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;


// See http://developer.android.com/google/gcm/client.html
public class BroadcastReceiverClient extends WakefulBroadcastReceiver {
	
    @Override
    public void onReceive(Context context, Intent intent) {
    	//System.out.println("xyz");
    	
    	// Explicitly specify that GcmMessageHandler will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(), MessageHandlerClient.class.getName());
        //System.out.println("abc");
        
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    	
    }
}

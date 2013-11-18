package org.zdev.recall;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

public class CopyActivity extends Activity {
	
	private static class IncomingHandler extends Handler {
		
		private Activity parentActivity;
		
		public IncomingHandler(Activity parentActivity){
			this.parentActivity = parentActivity;
		}
		
		@Override
		public void handleMessage(Message msg) {
			
			switch(msg.what) {
				
				// this is a response for "second to last"
				case 2:
					
					// coerce into an array list
					ClippedItem clippedItem = (ClippedItem) msg.obj;
					
					// grab clipboard manager
					ClipboardManager cManager = (ClipboardManager) parentActivity.getSystemService(Context.CLIPBOARD_SERVICE);

					System.out.println("Reply Contents: " + clippedItem.getClippingContents());
					
					// create new clip and add as primary
					ClipData newClip = ClipData.newPlainText("RecallCopy", clippedItem.getClippingContents());
					cManager.setPrimaryClip(newClip);
					
					// let the user know what happened
					Toast.makeText(parentActivity, "Copied to clipboard", Toast.LENGTH_SHORT).show();

					// let's vibrate .. you know ... for fun!
					Vibrator vService = (Vibrator) parentActivity.getSystemService(Context.VIBRATOR_SERVICE);
					vService.vibrate(175);
										
					parentActivity.finish();
					break;
								
			}
		}
	};
	
	private Messenger incomingMessenger, mService;
	private boolean serviceIsBound;
	
	private ServiceConnection		mConnection					= 
			new ServiceConnection() {
				public void onServiceConnected(ComponentName className, IBinder service) {
					mService = new Messenger(service);
					serviceIsBound = true;
					
					
					Log.d("CopyActivity", "Sending Message For Second-to-last");
					Message copyRequest = new Message();
					copyRequest.replyTo = incomingMessenger;
					copyRequest.what = 2;
					try {
						mService.send(copyRequest);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					
				}

				public void onServiceDisconnected(ComponentName className) {
					mService = null;
					serviceIsBound = false;
				}
			};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.incomingMessenger = new Messenger(new IncomingHandler(this));

				
		// create intent to sent to background service
		Intent serviceAttachIntent = new Intent(this, BackgroundService.class);
		bindService(serviceAttachIntent, this.mConnection, Context.BIND_ABOVE_CLIENT);
		
		
	}
	

	@Override
	protected void onStop() {
		super.onStop();
		if (this.serviceIsBound) {
			unbindService(this.mConnection);
			this.serviceIsBound = false;
		}
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (this.serviceIsBound) {
			unbindService(this.mConnection);
			this.serviceIsBound = false;
		}
	}
	
	
	
	
}

package org.zdev.recall;

import android.app.Activity;
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
import android.util.Log;

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
					
					// TODO: place on clipboard
										
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

		
		Log.d("CopyActivity", "Created");
		
		// create intent to sent to background service
		Intent serviceAttachIntent = new Intent(this, BackgroundService.class);
		serviceAttachIntent.putExtra("Messenger", this.incomingMessenger);
		
		
		bindService(serviceAttachIntent, this.mConnection, Context.BIND_ABOVE_CLIENT);

		
		
		
		// end activity
		this.finish();
		
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

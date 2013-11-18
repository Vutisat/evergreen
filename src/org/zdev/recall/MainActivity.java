package org.zdev.recall;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.NotificationManager;
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
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import android.view.GestureDetector;

public class MainActivity extends Activity implements 
	GestureDetector.OnGestureListener,
	GestureDetector.OnDoubleTapListener {
		

	/*
	 * By creating a handler, we are able to listen for incoming messenges and
	 * then process them as needed.
	 */
	@SuppressLint("HandlerLeak")
	private static class IncomingHandler extends Handler {
		
		private MainActivity parentReference;

		public IncomingHandler(MainActivity parentReference){
			super();
			this.parentReference = parentReference;			
		}
		
		@Override
		public void handleMessage(Message msg) {
			
			System.out.println("Main -- Received Message");
			
			switch(msg.what) {
				
				// this is a response for "all data"
				case 0:
					
					// coerce into an array list
					@SuppressWarnings("unchecked")
					ArrayList<ClippedItem> returnedContents = (ArrayList<ClippedItem>) msg.obj;
					parentReference.setListContents(returnedContents);
							
					break;
								
			}
		}
	};

	
	private ArrayList<ClippedItem>  localClippedItems = new ArrayList<ClippedItem>();
	private ClippedItemArrayAdapter	listAdapter;

	private Messenger				incomingMessenger;
	private Messenger				mService					= null;
	boolean							serviceIsBound				= false;
    private GestureDetectorCompat mDetector; 


	private ServiceConnection		mConnection					= 
		new ServiceConnection() {
			public void onServiceConnected(ComponentName className, IBinder service) {
				mService = new Messenger(service);
				serviceIsBound = true;
				
				System.out.println("Main -- Service Bound");
				
				retrieveStoredClippings(); // now that the service is bound, retrieve data
				
			}

			public void onServiceDisconnected(ComponentName className) {
				mService = null;
				serviceIsBound = false;
			}
		};
		
	private void setListContents(ArrayList<ClippedItem> newData){
		this.localClippedItems.clear();
		this.localClippedItems.addAll(newData);
		this.listAdapter.notifyDataSetChanged();
	}

		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// set up local messenger
		this.incomingMessenger = new Messenger(new IncomingHandler(this));
		
		// gesture support
        this.mDetector = new GestureDetectorCompat(this,this);
        this.mDetector.setOnDoubleTapListener(this);
		
		// create list view for displaying items that are cached in memory locally
		ListView listView = (ListView) findViewById(R.id.clipboardListView);
		this.listAdapter = new ClippedItemArrayAdapter(this, this.localClippedItems);
		listView.setAdapter(this.listAdapter);
		listView.setClickable(true);
		listView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//System.out.println("View: " + v.);
				return mDetector.onTouchEvent(event);
			}
		});


				
		
		// check if the background service is running and kill it if so (no more than one at a time)
		if(this.backgroundServiceRunning()){
			stopService(new Intent(this, BackgroundService.class));
		}
		

		// create an intent and use it to spawn our background process
		Intent serviceIntent = new Intent(this, BackgroundService.class);
		serviceIntent.putExtra("Messenger", this.incomingMessenger);
		startService(serviceIntent);

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * This method sends a request for the clipping list to the background
	 * service. It does not return any data and does not manipulate the local
	 * copy. That is done when a response message is recieved.
	 */
	private void retrieveStoredClippings() {

		try {
			Message scRequest = new Message();
			scRequest.what = 0; // request for all data
			this.mService.send(scRequest);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

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
	protected void onStart() {
		super.onStart();

		Log.d("MainActivity", "onStart");
		bindService(new Intent(this, BackgroundService.class), this.mConnection, Context.BIND_AUTO_CREATE);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// let's find out what drop down item we chose
		switch (item.getItemId()) {

		// if we match this we'll transition to our settings activity
			case R.id.action_settings:

				// create intent instance wrapping our settings activity
				Intent settingsIntent = new Intent(this, SettingsActivity.class);
				startActivityForResult(settingsIntent, 1);

				break;

				
			case R.id.action_clear_all:
				
				// remove all items
				this.localClippedItems.clear();
				this.listAdapter.notifyDataSetChanged();
				
				try {
					Message scRequest = new Message();
					scRequest.what = 1; // empty all
					this.mService.send(scRequest);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				
				break;
				
				
			case R.id.action_exit:

				// clear all notifications
				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.cancelAll();

				// kill background service
				stopService(new Intent(this, BackgroundService.class));

				// terminate activity
				this.finish();
				break;

		}

		return true;
	}
	
	private boolean backgroundServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (BackgroundService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}

	
	
    @Override 
    public boolean onTouchEvent(MotionEvent event){ 
        this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) { 
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
    	
    	// attempt to retrieve the item that was flicked so we can remove it
		ListView listView = (ListView) findViewById(R.id.clipboardListView);
		    	
		// remove from list
		long rowItem = listView.pointToRowId(Math.round(event1.getX()), Math.round(event1.getY()));
		
		//listView.
		
		this.localClippedItems.remove(rowItem);
		
		// refresh view
		this.listAdapter.notifyDataSetChanged();
		
		// send to data store for persistence
		Message toDelete = new Message();
		toDelete.arg1 = (int) rowItem;
		toDelete.what = 3; // delete
		try {
			this.mService.send(toDelete);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        return true;
    }
	
}

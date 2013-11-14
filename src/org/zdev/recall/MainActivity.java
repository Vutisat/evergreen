package org.zdev.recall;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

	/*
	 * By creating a handler, we are able to listen for incoming messenges and
	 * then process them as needed.
	 */
	private static class IncomingHandler extends Handler {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			Log.d("MainActivity", "Handle Message");
			System.out.println(msg.what);
			
			switch(msg.what) {
				
				case 0:
					
					ArrayList<ClippedItem> returnedContents = (ArrayList<ClippedItem>) msg.obj;

					System.out.println("Main Activity Got Contents; Length:" + returnedContents.size());
					
					break;
					
				default:
				
			}
			
			
			
			
			//System.out.println(((ClippedItem) msg.obj).getClippingContents());
		}
	};

	private ArrayList<String>		temporaryClippedElements	= new ArrayList<String>();
	private ArrayAdapter<String>	listAdapter;

	private Messenger				incomingMessenger			= new Messenger(new IncomingHandler());
	private Messenger				mService					= null;
	boolean							serviceIsBound				= false;

	private ServiceConnection		mConnection					= 
		new ServiceConnection() {
			public void onServiceConnected(ComponentName className, IBinder service) {
				mService = new Messenger(service);
				serviceIsBound = true;
				
				retrieveStoredClippings(); // now that the service is bound, retrieve data
				
			}

			public void onServiceDisconnected(ComponentName className) {
				mService = null;
				serviceIsBound = false;
			}
		};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// create list view for displaying items that are cached in memory locally
		ListView listView = (ListView) findViewById(R.id.clipboardListView);
		this.listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.temporaryClippedElements);
		listView.setAdapter(this.listAdapter);

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

	private void retrieveStoredClippings() {

		// reset local list
		this.temporaryClippedElements = new ArrayList<String>();

		try {
			Message scRequest = new Message();
			scRequest.what = 0; // request for all data
			this.mService.send(scRequest);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		// re-rendering parent view
		// this.listAdapter.notifyDataSetChanged();

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
				
				// TODO:
				
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

}

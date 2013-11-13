package org.zdev.recall;

import java.util.ArrayList;

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
		@Override
		public void handleMessage(Message msg) {
			Log.d("MainActivity", "Handle Message");
			System.out.println( ((ClippedItem) msg.obj).getContents() );
		}
	};
	

	private ArrayList<String>		clipboardElements	= new ArrayList<String>();
	private DatabaseHandler			dbHandler;
	private ArrayAdapter<String>	listAdapter;
	
	private Messenger incomingMessenger = new Messenger(new IncomingHandler());
	private Messenger mService = null;
	boolean serviceIsBound = false;
	
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            serviceIsBound = true;
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

		// set up database interface
		this.dbHandler = new DatabaseHandler(this);
	
		
		Log.d("MainActivity", "onCreate");
		
			
		// Display items
		ListView listView = (ListView) findViewById(R.id.clipboardListView);
		this.listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.clipboardElements);
		listView.setAdapter(this.listAdapter);

		// retrieve data
		this.retrieveStoredClippings();
		
		
		
		// create an intent and use it to spawn our background process (attach messenger)
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
		this.clipboardElements = new ArrayList<String>();



		// re-rendering parent view
		//this.listAdapter.notifyDataSetChanged();

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
		
		this.retrieveStoredClippings();
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

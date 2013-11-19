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
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends Activity {

	/*
	 * By creating a handler, we are able to listen for incoming messenges and
	 * then process them as needed.
	 */
	@SuppressLint("HandlerLeak")
	private static class IncomingHandler extends Handler {

		private MainActivity	parentReference;

		public IncomingHandler(MainActivity parentReference) {
			super();
			this.parentReference = parentReference;
		}

		@Override
		public void handleMessage(Message msg) {

			System.out.println("Main -- Received Message");

			switch (msg.what) {

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

	private ArrayList<ClippedItem>	localClippedItems	= new ArrayList<ClippedItem>();
	private ClippedItemArrayAdapter	listAdapter;

	private Messenger				incomingMessenger;
	private Messenger				mService			= null;
	boolean							serviceIsBound		= false;

	private ServiceConnection		mConnection			= new ServiceConnection() {
															public void onServiceConnected(ComponentName className,
																	IBinder service) {
																mService = new Messenger(service);
																serviceIsBound = true;

																System.out.println("Main -- Service Bound");

																retrieveStoredClippings(); // now
																							// that
																							// the
																							// service
																							// is
																							// bound,
																							// retrieve
																							// data

															}

															public void onServiceDisconnected(ComponentName className) {
																mService = null;
																serviceIsBound = false;
															}
														};

	private void setListContents(ArrayList<ClippedItem> newData) {
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

		// create list view for displaying items that are cached in memory
		// locally
		ListView listView = (ListView) findViewById(R.id.clipboardListView);
		this.listAdapter = new ClippedItemArrayAdapter(this, this.localClippedItems);
		listView.setAdapter(this.listAdapter);
		listView.setClickable(true);

		// context menu
		registerForContextMenu(listView);

		// check if the background service is running and kill it if so (no more
		// than one at a time)
		if (this.backgroundServiceRunning()) {
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
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.clipboardListView) {

			// retrieved the item that was long-pressed
			AdapterView.AdapterContextMenuInfo itemInformation = (AdapterView.AdapterContextMenuInfo) menuInfo;
			ClippedItem pressedItem = this.localClippedItems.get(itemInformation.position);

			// set up context menu
			menu.setHeaderTitle("Clipping Options");
			menu.add(Menu.NONE, 0, 0, "Delete");
			menu.add(Menu.NONE, 1, 1, (pressedItem.isPinnedClipping()) ? "Unpin" : "Pin");

		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		// get list view information
		AdapterView.AdapterContextMenuInfo listInformation = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		
		// switch over selected menu item
		switch (item.getItemId()) {
			
			// delete item
			case 0:
								
				// send message to service deleting the item
				try {
					Message scRequest = new Message();
					scRequest.what = 3; // delete item
					scRequest.arg1 = listInformation.position;
					this.mService.send(scRequest);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				
				// remove locally for immediate updating
				this.localClippedItems.remove(listInformation.position);
				this.listAdapter.notifyDataSetChanged();
				
				break;

			// pin item
			case 1:
				
				// get item and flip its state
				ClippedItem itemContents = this.localClippedItems.get(listInformation.position);
				itemContents.setPinnedClipping(!itemContents.isPinnedClipping());
				
				// update locally
				this.localClippedItems.set(listInformation.position, itemContents);
				this.listAdapter.notifyDataSetChanged();
				
				// send message to service updating the item
				try {
					Message scRequest = new Message();
					scRequest.what = 4; // delete item
					scRequest.arg1 = listInformation.position;
					scRequest.obj = itemContents;
					this.mService.send(scRequest);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				
		}

		return true;
	}

}

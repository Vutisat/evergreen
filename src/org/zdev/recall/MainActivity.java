package org.zdev.recall;

import java.util.ArrayList;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

	private ArrayList<String>		clipboardElements	= new ArrayList<String>();
	private DatabaseHandler			dbHandler;
	private ArrayAdapter<String>	listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// set up database interface
		this.dbHandler = new DatabaseHandler(this);

		// Display items
		ListView listView = (ListView) findViewById(R.id.clipboardListView);
		this.listAdapter = 
			new ArrayAdapter<String>(
				this,
				android.R.layout.simple_list_item_1,
				this.clipboardElements
			);
		listView.setAdapter(this.listAdapter);
		
		// retrieve data
		this.retrieveStoredClippings();
		
		// start background service
		startService(new Intent(this, BackgroundService.class));
		
	}

	private void retrieveStoredClippings() {

		// reset local list
		this.clipboardElements = new ArrayList<String>();

		// go ahead and retrieve them from our SQLite db
		for (ClippedItem anItem : this.dbHandler.getAllItems()) {
			this.clipboardElements.add(anItem.getContents());
		}
		
		// re-rendering parent view
		this.listAdapter.notifyDataSetChanged();

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		this.retrieveStoredClippings();
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

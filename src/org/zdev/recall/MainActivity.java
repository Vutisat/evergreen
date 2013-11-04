package org.zdev.recall;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);



		// TODO: convert to new storage backend
//		// displaying items
//		ListView listView = (ListView) findViewById(R.id.clipboardListView);
//		ArrayAdapter<String> cbAdapter = new ArrayAdapter<String>(this,
//				android.R.layout.simple_list_item_1, this.clipboardElements);
//		listView.setAdapter(cbAdapter);
		
		// start background service
		startService(new Intent(this, BackgroundService.class));
		
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onStart() {
		super.onStart();
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

			// terminate activity
			this.finish();
			break;

		}

		return true;
	}

}

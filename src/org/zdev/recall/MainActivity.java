package org.zdev.recall;

import java.util.ArrayList;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

	private ArrayList<String> clipboardElements = new ArrayList<String>();

	public void addElementToList(String aValue) {

		// append element
		this.clipboardElements.add(aValue);

		// refresh notification with last val + count
		this.refreshNotification();
		
		// refresh parent
		@SuppressWarnings("unchecked")
		ArrayAdapter<String> cbAdapter = (ArrayAdapter<String>) ((ListView) findViewById(R.id.clipboardListView)).getAdapter();
		cbAdapter.notifyDataSetChanged();

	}

	public void refreshNotification() {

		// Setup notification
		NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
		nBuilder.setSmallIcon(R.drawable.ic_launcher);
		nBuilder.setContentTitle("Recall");
		nBuilder.setContentInfo((CharSequence) (this.clipboardElements.size() + ""));
		nBuilder.setNumber(this.clipboardElements.size());
		nBuilder.setContentText((this.clipboardElements.size() == 0) ? "Waiting for you to copy text"
				: this.clipboardElements.get(this.clipboardElements.size() - 1));

		// setup return intent
		Intent overlayIntent = new Intent(this, RecentClippingsActivity.class);
		overlayIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent thePendingIntent = PendingIntent.getActivity(this, 0, overlayIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		nBuilder.setContentIntent(thePendingIntent);

		// large view
		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		inboxStyle.setBigContentTitle("Recall");
		inboxStyle.setSummaryText("You have " + this.clipboardElements.size()
				+ " clipping"
				+ ((this.clipboardElements.size() != 1) ? "s" : "") + ".");

		// default text
		if (this.clipboardElements.size() == 0) {
			inboxStyle
					.addLine((CharSequence) "You haven't copied anything yet!");
		}

		// iterate over elements
		for (int i = this.clipboardElements.size() - 1; i >= 0; i--) {
			inboxStyle.addLine((CharSequence) this.clipboardElements.get(i));
		}

		// apply large style
		nBuilder.setStyle(inboxStyle);

		// this makes the notification persistent!
		nBuilder.setOngoing(true);

		// display notification
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(1337, nBuilder.build());

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// draw initial notification
		this.refreshNotification();

		// grab the manager
		ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

		// create instance of our copy listener
		CopyListener copyListener = new CopyListener(this, clipboardManager);

		// ...and specify our listener
		clipboardManager.addPrimaryClipChangedListener(copyListener);
		
		
		
		/// displaying items
		ListView listView = (ListView) findViewById(R.id.clipboardListView);
		ArrayAdapter<String> cbAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.clipboardElements);
		listView.setAdapter(cbAdapter);	
		
		
		
		// background service to avoid data loss
		startService(new Intent(getApplicationContext(), RunningService.class));

	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d("Recall", "Stopping");
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		Log.d("Recall", "Resuming, DP: " + this.clipboardElements.size());
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

		}

		return true;
	}

}

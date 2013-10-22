package org.zdev.recall;

import java.util.ArrayList;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	private ArrayList<String> clipboardElements = new ArrayList<String>();

	public void addElementToList(String aValue) {

		// append element
		this.clipboardElements.add(aValue);

		// refresh notification with last val + count
		this.refreshNotification();

	}

	public void refreshNotification() {

		// setup notification icon
		NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(
				this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Recall")
				.setContentText(
						(this.clipboardElements.size() == 0) ? "Waiting for you to copy text"
								: this.clipboardElements
										.get(this.clipboardElements.size() - 1))
				.setContentInfo(
						(CharSequence) (this.clipboardElements.size() + ""));

		// TODO: setup return intent
		// (https://developer.android.com/guide/topics/ui/notifiers/notifications.html)
		// Intent resultIntent = new Intent(this, MainActivity.class);
		
		// large view
		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		inboxStyle.setBigContentTitle("Recall");
		inboxStyle.setSummaryText("You have " + this.clipboardElements.size() + " clipping" + ((this.clipboardElements.size() != 1)? "s" : "") + ".");

		
		// default text
		if(this.clipboardElements.size() == 0) {
			inboxStyle.addLine((CharSequence) "You haven't copied anything yet!");
		}
		
		
		// iterate over elements
		for(int i = this.clipboardElements.size() - 1; i >= 0; i--) {
			inboxStyle.addLine((CharSequence) this.clipboardElements.get(i));
		}
		
		nBuilder.setStyle(inboxStyle);
		

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
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

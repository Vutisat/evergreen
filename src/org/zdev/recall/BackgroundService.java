package org.zdev.recall;

import java.util.LinkedList;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.SpannableString;
import android.util.Log;

public class BackgroundService extends Service implements
		OnPrimaryClipChangedListener {

	// for recalling / editing our notification
	private final static int NOTIFICATION_ID = 1337;
	
	// actual data
	private LinkedList<ClippedItem> clippedItems = new LinkedList<ClippedItem>();
	
	// instance of the clipboard manager
	ClipboardManager clipboardManager;

	public void onCreate() {

		super.onCreate();

		/**
		 * Ok, we've delegated that this background service will be responsible
		 * for listening for changes of the clipboard and managing the data
		 * accordingly.
		 */

		// grab the manager
		this.clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

		// ...and specify our listener
		clipboardManager.addPrimaryClipChangedListener(this);
		
		// draw notification
		this.redrawNotification();
		
	}
	

	@Override
	public IBinder onBind(Intent intent) {
		return null;	
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("BackgroundService", "Being Destroyed!!!");
	}

	@Override
	public void onPrimaryClipChanged() {
		
		Log.d("BackgroundService", "New Item On Clipboard!");
		
		// fucking java
		try {

			/*
			 * NOTE: This is a really hacky way of going about doing this.
			 * Apparently this is a bug in 4.x where getText will return an
			 * instance of SpannedString which cannot be cast to string. So we
			 * have to create an instance of SpannableString which takes the
			 * return value as a parameter and then converts it to a string.
			 */
			
			// TODO: better means of detecting multiple copies

			// Get the most recent clipping off of the clipboard
			String clippingText = new SpannableString(this.clipboardManager.getPrimaryClip().getItemAt(0).getText()).toString();
		
			// check for a proper length (non-empty string)
			if(clippingText.length() > 0) {
				for(ClippedItem anItem : this.clippedItems) { // check for string already present
					if(anItem.getContents().equals(clippingText)) return;
				}
				this.clippedItems.addFirst(new ClippedItem(clippingText)); // push to front
				Log.d("BackgroundService", "Item Added!");
			}
			
			// redraw the notification to reflect copied text
			this.redrawNotification();

		} catch (Exception e) {
			Log.d("err", e.toString());
		}
	}

	private void redrawNotification() {

		// Setup notification
		NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
		nBuilder.setSmallIcon(R.drawable.ic_launcher);
		nBuilder.setContentTitle("Recall");
		nBuilder.setContentInfo((CharSequence) (this.clippedItems.size() + ""));
		nBuilder.setNumber(this.clippedItems.size());
		nBuilder.setContentText((this.clippedItems.size() == 0) ? "Waiting for you to copy text"
				: this.clippedItems.get(this.clippedItems.size() - 1).getContents());
		

		

		// setup return intent
		Intent overlayIntent = new Intent(this, RecentClippingsActivity.class);
		overlayIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent thePendingIntent = PendingIntent.getActivity(this, 0, overlayIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// only issue an intent if there are elements to display
		if (this.clippedItems.size() > 0) {
			nBuilder.setContentIntent(thePendingIntent);
			
			// add copy intent
			nBuilder.addAction(
				R.drawable.add,
				"Copy Last",
				PendingIntent.getActivity(
					this,
					0,
					new Intent(this, CopyActivity.class),
					0
				)
			);
		}
		
		// display settings button
	    nBuilder.addAction(
    		R.drawable.settings,
    		"Settings",
    		PendingIntent.getActivity(
    			this,
    			0,
    			new Intent(
    				this,
    				SettingsActivity.class
    			),
    			0
    		)
    	);

		// large view
		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		inboxStyle.setBigContentTitle("Recall");
//		inboxStyle.setSummaryText("You have " + this.clippedItems.size()
//				+ " clipping"
//				+ ((this.clippedItems.size() != 1) ? "s" : "") + ".");

		
		
		// default text
		if (this.clippedItems.size() == 0) {
			inboxStyle
					.addLine((CharSequence) "You haven't copied anything yet!");
		}

		// iterate over elements
		for (int i = this.clippedItems.size() - 1; i >= 0; i--) {
			inboxStyle.addLine((CharSequence) this.clippedItems.get(i).getContents());
		}

		// apply large style
		nBuilder.setStyle(inboxStyle);

		// this makes the notification persistent!
		nBuilder.setOngoing(true);

		// display notification
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(BackgroundService.NOTIFICATION_ID, nBuilder.build());

	}

}

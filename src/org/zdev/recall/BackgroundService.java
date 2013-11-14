package org.zdev.recall;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.text.SpannableString;

public class BackgroundService extends Service implements OnPrimaryClipChangedListener {

	//
	// Notification access, clipboard listening, and data access
	// --------------------------------------------------------------------------------
	private final static int	NOTIFICATION_ID	= 1337;
	private ClipboardManager	clipboardManager;
	private DataInterface		dataInterface;

	//
	// Intra-process communication devices
	// --------------------------------------------------------------------------------
	private final Messenger		mMessenger		= new Messenger(new IncomingHandler());
	private Messenger			activityMessenger;

	/**
	 * This is the function that is called when this background service is
	 * created. In here, we go ahead and set up what we need to in order to
	 * access our data and manage the listener for new data on the clipboard.
	 * 
	 * This is also where we go ahead and initially draw the notification.
	 */
	public void onCreate() {
		super.onCreate();

		this.clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		clipboardManager.addPrimaryClipChangedListener(this);

		this.redrawNotification();

	}

	/**
	 * When an activity issues `bindService` this method is called. It returns a
	 * binder so that the activity can then communicate with the background
	 * service.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// NOTE: you must unbind this on destroy or the listener will be
		// leaked. This means that further copies will automatically start
		// the background service listening again...not good!
		this.clipboardManager.removePrimaryClipChangedListener(this);

	}

	@Override
	public void onPrimaryClipChanged() {

		try {

			/*
			 * NOTE: This is a really hacky way of going about doing this.
			 * Apparently this is a bug in 4.x where getText will return an
			 * instance of SpannedString which cannot be cast to string. So we
			 * have to create an instance of SpannableString which takes the
			 * return value as a parameter and then converts it to a string.
			 */

			// Get the most recent clipping off of the clipboard
			String clippingText = new SpannableString(this.clipboardManager.getPrimaryClip().getItemAt(0).getText())
					.toString();

			// check for a proper length and the text doesn't currently exist in
			// memory
			if (clippingText.length() > 0 && !this.dataInterface.itemExists(new ClippedItem(clippingText))) {

				// add to our in-memory storage
				this.dataInterface.addItem(new ClippedItem(clippingText));

			}

			// redraw the notification to reflect copied text
			this.redrawNotification();

		} catch (Exception e) {
			// wat wat wat wat
		}
	}

	private void redrawNotification() {

		// Setup notification
		NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
		nBuilder.setSmallIcon(R.drawable.ic_launcher);
		nBuilder.setContentTitle("Recall");
		nBuilder.setContentInfo((CharSequence) (this.dataInterface.length() + ""));
		nBuilder.setNumber(this.dataInterface.length());
		nBuilder.setContentText((this.dataInterface.length() == 0) ? "Waiting for you to copy text"
				: this.dataInterface.getItem(this.dataInterface.length() - 1).getClippingContents());

		// setup return intent
		Intent overlayIntent = new Intent(this, RecentClippingsActivity.class);
		overlayIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent thePendingIntent = PendingIntent.getActivity(this, 0, overlayIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		// only issue an intent if there are elements to display
		if (this.dataInterface.length() > 0) {

			// we do not need to show the user an empty list -- no intent if
			// there are no items
			nBuilder.setContentIntent(thePendingIntent);

			if (this.dataInterface.length() > 1) {

				// add copy intent
				nBuilder.addAction(R.drawable.add, "Quick Copy",
						PendingIntent.getActivity(this, 0, new Intent(this, CopyActivity.class), 0));

			}

		}

		// display settings button
		nBuilder.addAction(R.drawable.settings, "Settings",
				PendingIntent.getActivity(this, 0, new Intent(this, SettingsActivity.class), 0));

		// large view
		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		inboxStyle.setBigContentTitle("Recall");

		// default text
		if (this.dataInterface.length() == 0) {
			inboxStyle.addLine((CharSequence) "You haven't copied anything yet!");
		}

		// iterate over elements
		for (int i = 0; i < this.dataInterface.length(); i++) {
			inboxStyle.addLine((CharSequence) this.dataInterface.getItem(i).getClippingContents());
		}

		// apply large style
		nBuilder.setStyle(inboxStyle);

		// this makes the notification persistent!
		nBuilder.setOngoing(true);

		// display notification
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(BackgroundService.NOTIFICATION_ID, nBuilder.build());

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (intent != null) {
			this.activityMessenger = intent.getParcelableExtra("Messenger");

			Message testMessage = new Message();
			testMessage.obj = new ClippedItem("test 123");

			try {
				this.activityMessenger.send(testMessage);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		}

		System.out.println(this.activityMessenger);

		return START_STICKY_COMPATIBILITY;
	}

	private static class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {

		}
	}

}

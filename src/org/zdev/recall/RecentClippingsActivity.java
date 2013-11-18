package org.zdev.recall;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class RecentClippingsActivity extends Activity implements OnItemClickListener {
	
	@SuppressLint("HandlerLeak")
	private static class IncomingHandler extends Handler {
		
		private RecentClippingsActivity parentReference;

		public IncomingHandler(RecentClippingsActivity parentReference){
			super();
			this.parentReference = parentReference;			
		}
		
		@Override
		public void handleMessage(Message msg) {
						
			switch(msg.what) {
				
				// this is a response for "all data"
				case 0:
					
					Log.d("RecentClippings", "Received response");
					
					// coerce into an array list
					@SuppressWarnings("unchecked")
					ArrayList<ClippedItem> returnedContents = (ArrayList<ClippedItem>) msg.obj;
					parentReference.setListContents(returnedContents);
							
					break;
								
			}
		}
	};
	
	
	
	
	

	private ArrayList<ClippedItem>	clipboardElements	= new ArrayList<ClippedItem>();
	private Messenger	incomingMessenger;
	private ServiceConnection		mConnection					= 
			new ServiceConnection() {
				public void onServiceConnected(ComponentName className, IBinder service) {
					Messenger serviceMessenger = new Messenger(service);
					
					try {
						Log.d("RecentClippings", "Firing request for all data");
						Message scRequest = new Message();
						scRequest.what = 0; // request for all data
						scRequest.replyTo = incomingMessenger;
						serviceMessenger.send(scRequest);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					
					
				}

				@Override
				public void onServiceDisconnected(ComponentName arg0) {
					
				}
			};
	private ClippedItemArrayAdapter	listAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Log.d("RecentClippings", "onCreate");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recent_clippings);
		
		this.incomingMessenger = new Messenger(new IncomingHandler(this));

		// create list view for displaying items that are cached in memory locally
		ListView listView = (ListView) findViewById(R.id.recentClippingsList);
		this.listAdapter = new ClippedItemArrayAdapter(this, this.clipboardElements);
		listView.setAdapter(this.listAdapter);
		listView.setClickable(true);
		listView.setOnItemClickListener(this);
		
		
		// todo: reimplement
//		// create button to allow "cancel"
//		Button cancelButton = new Button(this);
//		cancelButton.setText("Cancel");
//		listView.addFooterView(cancelButton);
//
//		// TODO: add listener for cancel button to close window


		// attach service
		bindService(new Intent(this, BackgroundService.class), this.mConnection, Context.BIND_AUTO_CREATE);

	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unbindService(this.mConnection);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		ListView listView = (ListView) findViewById(R.id.recentClippingsList);
		ClippedItem anItem = (ClippedItem) listView.getItemAtPosition(arg2);

		// grab clipboard manager again
		ClipboardManager cManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

		// create new clip and add as primary
		ClipData newClip = ClipData.newPlainText("RecallCopy", anItem.getClippingContents());
		cManager.setPrimaryClip(newClip);

		// let the user know what happened
		Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();

		// let's vibrate .. you know ... for fun!
		Vibrator vService = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vService.vibrate(175);

		// we're finished -- we can close this running activity
		this.finish();

	}

	private void setListContents(ArrayList<ClippedItem> listContents) {
		this.clipboardElements.clear();
		this.clipboardElements.addAll(listContents);
		this.listAdapter.notifyDataSetChanged();		
	}

}

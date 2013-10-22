package org.zdev.recall;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	private ArrayList<String> clipboardElements = new ArrayList<String>();
	
	
	public void addElementToList(String aValue) {
		this.clipboardElements.add(aValue);
		
		Log.d("New Element", "Clipboard Length: " + this.clipboardElements.size());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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

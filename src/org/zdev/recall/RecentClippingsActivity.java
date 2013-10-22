package org.zdev.recall;

import java.util.ArrayList;
import java.util.HashSet;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class RecentClippingsActivity extends Activity implements OnItemClickListener {
	
	private ArrayList<String> clipboardElements = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recent_clippings);
		
		// load up clipboard data
		this.retrieveStoredClippings();
		
		/// displaying items
		ListView listView = (ListView) findViewById(R.id.recentClippingsList);
		ArrayAdapter<String> cbAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.clipboardElements);
		listView.setAdapter(cbAdapter);
		listView.setClickable(true);
		listView.setOnItemClickListener(this);
		
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		ListView listView = (ListView) findViewById(R.id.recentClippingsList);
		Object o = listView.getItemAtPosition(arg2);
			
		// grab clipboard manager again
		ClipboardManager cManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		
		// create new clip and add as primary
		ClipData newClip = ClipData.newPlainText("RecallCopy", o.toString());
		cManager.setPrimaryClip(newClip);
		
		// let the user know what happened
		Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
		
		
		// let's vibrate .. you know ... for fun!
		Vibrator vService = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vService.vibrate(175);
		
	
		// we're finished -- we can close this running activity		
		this.finish(); 
		
	}
	
	
	private void retrieveStoredClippings() {
		
		// retrieve shared preferences
		SharedPreferences sPreferences = this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
		HashSet<String> retrievalSet = (HashSet<String>) sPreferences.getStringSet("clipboardData", new HashSet<String>());
		
		this.clipboardElements.clear();
		this.clipboardElements.addAll(retrievalSet);
	
	}





}

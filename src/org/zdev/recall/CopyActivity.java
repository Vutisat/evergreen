package org.zdev.recall;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class CopyActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d("CopyActivity", "Created");
		
		// TODO: get second to last item
		
		// TODO: place item on clipboard
		
		// display toast
		Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
		
		// end activity
		this.finish();
		
	}
	
}

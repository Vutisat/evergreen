package org.zdev.recall;

import android.app.Activity;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*
		 * Essentially, this beginning portion is going to be retrieving the
		 * system's clipboard manager and then adding a listener for when the
		 * copied text changes so we can store it in memory.
		 */
		


		// grab the manager and specify our listener
		ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		
		// create instance of our copy listener (this will receive events from the ClipboardManager
		// also pass ClipboardManager instance because of context access ffs
		CopyListener copyListener = new CopyListener(this, clipboardManager);
		
		clipboardManager.addPrimaryClipChangedListener(copyListener);
		
		
		// will be useful to show the user that we copied the data
		// Toast.makeText(this, "Text Copied To Clipboard",
		// Toast.LENGTH_SHORT).show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// uncomment this to enable the dropdown
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

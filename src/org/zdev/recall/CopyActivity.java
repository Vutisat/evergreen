package org.zdev.recall;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Toast;

public class CopyActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// grab clipboard manager
		ClipboardManager cManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

		// create new clip and add as primary
		ClipData newClip = ClipData.newPlainText("RecallCopy", getIntent().getExtras().getString("copyText"));
		cManager.setPrimaryClip(newClip);

		// let the user know what happened
		Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();

		// let's vibrate .. you know ... for fun!
		Vibrator vService = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vService.vibrate(175);

		// end activity
		this.finish();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}

package org.zdev.recall;

import android.app.Activity;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.widget.Toast;

public class CopyListener implements OnPrimaryClipChangedListener {

	private Activity parentInstance;

	CopyListener(Activity parentInstance) {
		this.parentInstance = parentInstance;
	}

	@Override
	public void onPrimaryClipChanged() {

		// Get most recent text off Clipboard
		// ClipboardManager clipboardManager = (ClipboardManager)
		// getSystemService(this.parentInstance.CLIPBOARD_SERVICE);

		// Log.d("Change", "Text Changed");
		// this.parentInstance.toString();

		
		
		// just debugging
		Toast.makeText(this.parentInstance, "New Text On Clipboard!", Toast.LENGTH_SHORT).show();

	}
}

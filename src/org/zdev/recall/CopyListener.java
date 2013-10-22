package org.zdev.recall;

import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.text.SpannableString;
import android.util.Log;
import android.widget.Toast;

public class CopyListener implements OnPrimaryClipChangedListener {

	private MainActivity parentInstance;
	private ClipboardManager clipboardManager;

	CopyListener(MainActivity parentInstance, ClipboardManager clipboardManager) {
		this.parentInstance = parentInstance;
		this.clipboardManager = clipboardManager;
	}

	@Override
	public void onPrimaryClipChanged() {

		// fucking java
		try {

			/*
			 * NOTE: This is a really hacky way of going about doing this.
			 * Apparently this is a bug in 4.x where getText will return an
			 * instance of SpannedString which cannot be cast to string. So we
			 * have to create an instance of SpannableString which takes the
			 * return value as a parameter and then converts it to a string.
			 */

			// Get the most recent clipping off of the clipboard
			String newClipping = new SpannableString(this.clipboardManager.getPrimaryClip().getItemAt(0).getText()).toString();
			
			
			// check for a proper length (non-empty string)
			if(newClipping.length() > 0) {
				this.parentInstance.addElementToList(newClipping);
			}

			// just debugging
			Toast.makeText(this.parentInstance, "New Text:" + newClipping, Toast.LENGTH_SHORT).show();

		} catch (Exception e) {
			Log.d("err", e.toString());
		}
	}
}

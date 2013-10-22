package org.zdev.recall;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.widget.Toast;

public class CopyListener implements OnPrimaryClipChangedListener {

	private Activity parentInstance;
	private ClipboardManager clipboardManager;

	CopyListener(Activity parentInstance, ClipboardManager clipboardManager) {
		this.parentInstance = parentInstance;
		this.clipboardManager = clipboardManager;
	}

	@Override
	public void onPrimaryClipChanged() {

		// Get most recent text off Clipboard
		String newClipping = (String) this.clipboardManager.getPrimaryClip().getItemAt(0).getText();
		
		// just debugging
		Toast.makeText(this.parentInstance, "New Text:" + newClipping, Toast.LENGTH_SHORT).show();

	}
}

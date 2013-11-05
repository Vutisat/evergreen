package org.zdev.recall;

import android.content.ContentValues;

public class ClippedItem {

	private boolean	pinnedClipping	= false;
	private String	clippingContents;
	private int		itemId			= -1;

	ClippedItem(String clippingContents) {
		this.clippingContents = clippingContents;
	}

	ClippedItem(String clippingContents, boolean isPinned, int itemId) {
		this.clippingContents = clippingContents;
		this.pinnedClipping = isPinned;
		this.itemId = itemId;
	}

	public boolean hasId() {
		return this.itemId != -1;
	}

	public int getId() {
		return this.itemId;
	}

	public void setId(int id) {
		this.itemId = id;
	}

	public void pin() {
		this.pinnedClipping = true;
	}

	public void unpin() {
		this.pinnedClipping = false;
	}

	public boolean isPinned() {
		return this.pinnedClipping;
	}

	public String getContents() {
		return this.clippingContents;
	}

	public ContentValues getValuesForInsertion() {

		ContentValues cValues = new ContentValues();

		// only set the `id` field if it exists
		if (this.hasId())
			cValues.put("id", this.getId());

		// other relevant information
		cValues.put("text", this.getContents());
		cValues.put("pinned", this.isPinned());

		return cValues;

	}

}

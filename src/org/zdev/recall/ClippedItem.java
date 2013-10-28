package org.zdev.recall;

public class ClippedItem {

	private boolean pinnedClipping;
	private String clippingContents;

	ClippedItem(String clippingContents) {
		this.clippingContents = clippingContents;
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
//
//	public String toString() {
//		return this.clippingContents;
//	}

}

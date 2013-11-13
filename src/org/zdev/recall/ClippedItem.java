package org.zdev.recall;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

public class ClippedItem implements Parcelable {

	private boolean	pinnedClipping	= false;
	private String	clippingContents;
	private int		itemId			= -1;

	public ClippedItem(String clippingContents) {
		this.clippingContents = clippingContents;
	}

	public ClippedItem(String clippingContents, boolean isPinned, int itemId) {
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

	@Override
	public int describeContents() {
		return 0;
	}

	
	/**
	 * This method allows us to serialize our ClippedItem data in a 
	 * very fast transport format that will allow us to quickly move it between
	 * the background service and the running activities.
	 * 
	 * NOTE: It is important the order which these are written and read.
	 */
	@Override
	public void writeToParcel(Parcel outputParcel, int flags) {
		outputParcel.writeString(this.clippingContents);
		outputParcel.writeByte((byte) (this.pinnedClipping? 1 : 0));
	}

	private ClippedItem(Parcel in) {
		this.clippingContents = in.readString();
		this.pinnedClipping = in.readByte() != 0;
	}

	public static final Parcelable.Creator<ClippedItem>	CREATOR	= 
		new Parcelable.Creator<ClippedItem>() {
			@Override
			public ClippedItem createFromParcel(Parcel in) {
				return new ClippedItem(in);
			}

			@Override
			public ClippedItem[] newArray(int size) {
				return new ClippedItem[size];
			}
																		
	};

}

package org.zdev.recall;

import java.io.Serializable;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class ClippedItem implements Parcelable, Serializable {

	private static final long	serialVersionUID	= -4884294659081428938L;
	private boolean				pinnedClipping		= false;
	private String				clippingContents;
	private long				creationDate;

	protected ClippedItem(ClippedItem anItem) {
		this.pinnedClipping = anItem.pinnedClipping;
		this.clippingContents = anItem.clippingContents;
		this.creationDate = anItem.creationDate;
	}

	public ClippedItem(String clippingContents) {
		this.clippingContents = clippingContents;
		this.creationDate = new Date().getTime();
	}

	public ClippedItem(String clippingContents, boolean isPinned) {
		this.clippingContents = clippingContents;
		this.pinnedClipping = isPinned;
		this.creationDate = new Date().getTime();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * This method allows us to serialize our ClippedItem data in a very fast
	 * transport format that will allow us to quickly move it between the
	 * background service and the running activities.
	 * 
	 * NOTE: It is important the order which these are written and read.
	 */
	@Override
	public void writeToParcel(Parcel outputParcel, int flags) {
		outputParcel.writeString(this.clippingContents);
		outputParcel.writeByte((byte) (this.pinnedClipping ? 1 : 0));
		outputParcel.writeLong(this.creationDate);
	}

	private ClippedItem(Parcel in) {
		this.clippingContents = in.readString();
		this.pinnedClipping = in.readByte() != 0;
		this.creationDate = in.readLong();
	}

	public static final Parcelable.Creator<ClippedItem>	CREATOR	= new Parcelable.Creator<ClippedItem>() {
																	@Override
																	public ClippedItem createFromParcel(Parcel in) {
																		return new ClippedItem(in);
																	}

																	@Override
																	public ClippedItem[] newArray(int size) {
																		return new ClippedItem[size];
																	}

																};

	public boolean isPinnedClipping() {
		return pinnedClipping;
	}

	public void setPinnedClipping(boolean pinnedClipping) {
		this.pinnedClipping = pinnedClipping;
	}

	public String getClippingContents() {
		return clippingContents;
	}

	public void setClippingContents(String clippingContents) {
		this.clippingContents = clippingContents;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

}

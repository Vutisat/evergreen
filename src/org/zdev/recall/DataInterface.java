package org.zdev.recall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import android.app.Service;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class DataInterface {

	private LinkedList<ClippedItem>	clippedItems	= new LinkedList<ClippedItem>();
	private SharedPreferences		recallSharedPreferences;

	public DataInterface(Service parentService) {
		this.recallSharedPreferences = PreferenceManager.getDefaultSharedPreferences(parentService);
		this.clippedItems = this.readClippedItemsFromStorage();
	}

	public void addItem(ClippedItem anItem) {
		Log.d("DataInterface", "Item Added");
		this.clippedItems.addFirst(anItem);
		this.writeClippedItemsToStorage();
	}

	public ClippedItem getItem(int itemIndex) {
		return this.clippedItems.get(itemIndex);
	}

	public boolean itemExists(ClippedItem incomingItem) {
		for (ClippedItem anItem : this.clippedItems) {
			if (anItem.getClippingContents() == incomingItem.getClippingContents())
				return true;
		}
		return false;
	}

	public void updateItem(int itemIndex, ClippedItem anItem) {
		this.clippedItems.set(itemIndex, anItem);
	}

	public int length() {
		return this.clippedItems.size();
	}

	private LinkedList<ClippedItem> readClippedItemsFromStorage() {

		try {
			@SuppressWarnings("unchecked")
			LinkedList<ClippedItem> returnList =  (LinkedList<ClippedItem>) ObjectSerializer.deserialize(this.recallSharedPreferences.getString("clippedItemList", null));
			return returnList;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return new LinkedList<ClippedItem>();
	}

	private void writeClippedItemsToStorage() {

		// get editor so we can make changes to the shared preferences
		Editor ciEditor = this.recallSharedPreferences.edit();
			
		try {
			ciEditor.putString("clippedItemList", ObjectSerializer.serialize(this.clippedItems));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// write them out
		ciEditor.commit();
		
	}

	public ArrayList<ClippedItem> getAllItems() {
		ArrayList<ClippedItem> returnArray = new ArrayList<ClippedItem>();
		for (ClippedItem anItem : this.clippedItems) {
			returnArray.add(anItem);
		}
		return returnArray;
	}

}

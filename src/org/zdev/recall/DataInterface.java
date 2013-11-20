package org.zdev.recall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import android.app.Service;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class DataInterface {

	private LinkedList<ClippedItem>	clippedItems	= new LinkedList<ClippedItem>();
	private SharedPreferences		recallSharedPreferences;

	public DataInterface(Service parentService) {
		this.recallSharedPreferences = PreferenceManager.getDefaultSharedPreferences(parentService);
		this.clippedItems = this.readClippedItemsFromStorage();
		this.cleanList();
	}

	public void addItem(ClippedItem anItem) {
		this.clippedItems.addFirst(anItem);
		this.cleanList();
		this.writeClippedItemsToStorage();
	}

	private int getPinnedCount() {
		int pinnedCount = 0;
		for(ClippedItem anItem : this.clippedItems){
			if(anItem.isPinnedClipping()) pinnedCount++;
		}
		return pinnedCount;
	}

	public ClippedItem getItem(int itemIndex) {
		return this.clippedItems.get(itemIndex);
	}

	public boolean itemExists(ClippedItem incomingItem) {
		for (ClippedItem anItem : this.clippedItems) {
			if (anItem.getClippingContents().compareTo(incomingItem.getClippingContents()) == 0) {
				return true;
			}
		}
		return false;
	}

	public void updateItem(int itemIndex, ClippedItem anItem) {
		this.clippedItems.set(itemIndex, anItem);
		this.cleanList();
		this.writeClippedItemsToStorage();
	}

	public void removeItem(int itemIndex) {
		this.clippedItems.remove(itemIndex);
		this.cleanList();
		this.writeClippedItemsToStorage();
	}

	public void removeAll() {
		this.clippedItems.clear();
		this.writeClippedItemsToStorage();
	}

	public ClippedItem getSecondToLast() {
		return this.clippedItems.get(1);
	}

	public int length() {
		return this.clippedItems.size();
	}
	
	private void cleanList() {
		
		// sort items
		DataInterface.sortItems(this.clippedItems);

		// should limit items?
		if (this.recallSharedPreferences.getBoolean("pref_key_auto_delete", false)) {
			
			int pinnedCount = this.getPinnedCount();

			// get how many to store (by adding pinned count, we exclude them)
			int limitTo = Integer.parseInt(this.recallSharedPreferences.getString("pref_key_auto_delete_limit", "9001")) + pinnedCount;

			// remove if we surpass this value
			while(this.clippedItems.size() > limitTo){
				this.clippedItems.removeLast();
			}

		}
		
	}
	
	public static void sortItems(List<ClippedItem> aList) {
		Collections.sort(aList, new Comparator<ClippedItem>() {
			@Override
			public int compare(ClippedItem a, ClippedItem b){
				System.out.println(a.getClippingContents() + " vs " + b.getClippingContents());
				if(!a.isPinnedClipping() && b.isPinnedClipping()) {
					return 1;
				} else if(!b.isPinnedClipping() && a.isPinnedClipping()) {
					return -1;
				} else {
					return (int) (b.getCreationDate() - a.getCreationDate());
				}
			}
		});
	}

	private LinkedList<ClippedItem> readClippedItemsFromStorage() {

		try {
			String readContents = this.recallSharedPreferences.getString("clippedItemList", null);

			// check for no stored items
			if (readContents == null)
				return new LinkedList<ClippedItem>();

			@SuppressWarnings("unchecked")
			LinkedList<ClippedItem> returnList = (LinkedList<ClippedItem>) ObjectSerializer.deserialize(readContents);
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

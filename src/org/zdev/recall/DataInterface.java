package org.zdev.recall;

import java.util.ArrayList;
import java.util.LinkedList;

import android.app.Service;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class DataInterface {

	private LinkedList<ClippedItem> clippedItems = new LinkedList<ClippedItem>();
	private SharedPreferences recallSharedPreferences;
	
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
		for(ClippedItem anItem : this.clippedItems) {
			if(anItem.getClippingContents() == incomingItem.getClippingContents()) return true;
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
		
		LinkedList<ClippedItem> returnList = new LinkedList<ClippedItem>();
		
	//	this.recallSharedPreferences.getStringSet(arg0, arg1)
		
		
		
		
		return returnList;
	}
	
	private void writeClippedItemsToStorage() {
		
		Log.d("DataInterface", "Writing Data Out");
		
		Editor ciEditor = this.recallSharedPreferences.edit();
		
		//ciEditor.putStringSet("storedClippedItems", arg1)
		ciEditor.commit();
		
		//this.recallSharedPreferences.
		
		for(ClippedItem anItem : this.clippedItems) {
			System.out.println(anItem.toString());
		}
		
	}

	public ArrayList<ClippedItem> getAllItems() {
		ArrayList<ClippedItem> returnArray = new ArrayList<ClippedItem>();
		for(ClippedItem anItem : this.clippedItems) {
			returnArray.add(anItem);
		}
		return returnArray;
	}
	
}

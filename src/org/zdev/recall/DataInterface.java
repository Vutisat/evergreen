package org.zdev.recall;

import java.util.LinkedList;

import android.app.Service;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class DataInterface {

	private LinkedList<ClippedItem> clippedItems = new LinkedList<ClippedItem>();
	private SharedPreferences recallSharedPreferences;
	
	public DataInterface(Service parentService) {
		this.recallSharedPreferences = PreferenceManager.getDefaultSharedPreferences(parentService);
		this.clippedItems = this.readClippedItemsFromStorage();
	}
	
	public void addItem(ClippedItem anItem) {
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
		
		Editor ciEditor = this.recallSharedPreferences.edit();
		
		//ciEditor.putStringSet("storedClippedItems", arg1)
		ciEditor.commit();
		
		//this.recallSharedPreferences.
		
		for(ClippedItem anItem : this.clippedItems) {
			System.out.println(anItem.toString());
		}
		
	}
	
}

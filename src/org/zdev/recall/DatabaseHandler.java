package org.zdev.recall;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	public DatabaseHandler(Context aContext) {
		super(aContext, "Recall", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// attempt to create the table?
		db.execSQL("CREATE TABLE IF NOT EXISTS `Clippings` (id INTEGER PRIMARY KEY AUTOINCREMENT, text TEXT, pinned INTEGER(1) DEFAULT 0)");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// I really don't give a fuck :)
	}

	public void addItem(ClippedItem anItem) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.insert("Clippings", null, anItem.getValuesForInsertion());
		db.close();
	}

	public ClippedItem getItem(int itemId) {

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query("Clippings", new String[] { "id", "text",
				"pinned" }, "id=?", new String[] { String.valueOf(itemId) },
				null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		return new ClippedItem(cursor.getString(1), cursor.getInt(2) == 1,
				cursor.getInt(0));
	}

	public ArrayList<ClippedItem> getAllItems() {

		ArrayList<ClippedItem> itemList = new ArrayList<ClippedItem>();
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT  * FROM Clippings", null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				itemList.add(
					new ClippedItem(
						cursor.getString(1),
						cursor.getInt(2) == 1,
						cursor.getInt(0)
					)
				);
			} while (cursor.moveToNext());
		}

		return itemList;

	}

	public void removeItem(int itemId) {

	}

	public void updateItem(ClippedItem anItem) {

	}

}

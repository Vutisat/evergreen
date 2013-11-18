package org.zdev.recall;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ClippedItemArrayAdapter extends ArrayAdapter<ClippedItem> {

	private Context				parentContext;
	private List<ClippedItem>	listContents;

	public ClippedItemArrayAdapter(Context context, List<ClippedItem> objects) {
		super(context, R.layout.clipped_item_layout, objects);

		this.parentContext = context;
		this.listContents = objects;

	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		System.out.println("Requested Position: " + position);
		
		// get layout inflater from parent context
		LayoutInflater layoutInflater = (LayoutInflater) this.parentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		// create a new list item view
		View itemView = layoutInflater.inflate(R.layout.clipped_item_layout,  parent);
		
		// extract text view from list item
		TextView clippingTextView = (TextView) itemView.findViewById(R.id.clippingContents);
		
		// set the clipping contents
		clippingTextView.setText(this.listContents.get(position).getClippingContents());
		
		
		
		// return the view
		return itemView;
		
	}

}

package org.zdev.recall;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
		
		// retrieve item being drawn
		ClippedItem currentItem = this.listContents.get(position);

		// get layout inflater from parent context
		LayoutInflater layoutInflater = (LayoutInflater) this.parentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		// create a new list item view
		View itemView = layoutInflater.inflate(R.layout.clipped_item_layout,  null);
		
		// extract text view from list item
		TextView clippingTextView = (TextView) itemView.findViewById(R.id.clippingContents);
		
		// set the clipping contents
		clippingTextView.setText(currentItem.getClippingContents());
		
		// when it was copied
		TextView clippingDateView = (TextView) itemView.findViewById(R.id.clippingDate);
		clippingDateView.setText(DateFormat.format("MMMM d, yyyy hh:mm:ss a", new Date(currentItem.getCreationDate())));
		
		
		// is this fucker starred or what?
		if(currentItem.isPinnedClipping()) {
			ImageView starView = (ImageView) itemView.findViewById(R.id.isPinned);
			Drawable dResource = getContext().getResources().getDrawable(R.drawable.ic_action_star);
			starView.setImageDrawable(dResource);
		}
						
		// return the view
		return itemView;
		
	}

}

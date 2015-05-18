package com.interakt.ar.android.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.interakt.ar.android.R;
import com.interakt.ar.graphics.poi.POI;

/**
 * The adapter for the list of ingredients that is used with the
 * {@link AutoCompleteTextView} to view its results of selected items.
 * Implements {@link android.content.DialogInterface.OnClickListener} and
 * extends {@link ArrayAdapter}.
 * 
 * @author Rawan Khalil (rawan.tkhalil@gmail.com)
 * 
 */
@SuppressWarnings("rawtypes")
public class POIListAdapter extends ArrayAdapter implements OnClickListener {

	/** The {@link ArrayList} of ingredients */
	private ArrayList<POI> pois = new ArrayList<POI>();

	private Context context;

	/**
	 * boolean variables to represent the change that happened to the list.
	 * Whether an item has been added or removed.
	 */
	public boolean add = false;
	public boolean remove = false;

	/**
	 * The name of the last ingredient that has been added or removed from the
	 * list.
	 */
	public POI addedPOI;
	public POI removedPOI;

	/**
	 * Constructor sets the ingredients and context.
	 * 
	 * @param context
	 * @param ingredients
	 *            The array list of ingredients.
	 */
	@SuppressWarnings("unchecked")
	public POIListAdapter(Context context, ArrayList<POI> pois) {
		super(context, android.R.layout.simple_list_item_1, pois);
		this.pois = pois;
		this.context = context;
	}

	@Override
	public int getCount() {
		System.out.println("In get count");
		if (pois == null)
			System.out.println("poi in get count is null");
		return pois.size();
	}

	@Override
	public POI getItem(int position) {
		return pois.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Overridden getView method. Inflates the required view for the ingredient
	 * which is a text view and image. The text view is the ingredient name and
	 * the image is the close button to remove this ingredient. The
	 * {@link OnClickListener} for the button is set to this.
	 * 
	 * @author Rawan Khalil (rawan.tkhalil@gmail.com)
	 * 
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		System.out.println("in get view");
		String poiName = pois.get(position).getPOIHolder().name;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.poi_list_item, null);

		}
		TextView poiNameTextView = (TextView) convertView
				.findViewById(R.id.poi_name_textview);
		poiNameTextView.setText(poiName);

		ImageView removePOIButton = (ImageView) convertView
				.findViewById(R.id.remove_poi_image);
		removePOIButton.setOnClickListener(this);
		removePOIButton.setTag(pois.get(position).getPOIHolder().id);
		return convertView;
	}

	/**
	 * Overridden {@link OnClickListener}. This is called when the image of the
	 * close button for the ingredient is clicked. It will remove the ingredient
	 * from the list, set add to false, remove to true, removedIng to the
	 * ingedient and calls notifyDataSetChanged().
	 * 
	 * @param v
	 * 
	 * @author Rawan Khalil (rawan.tkhalil@gmail.com)
	 * 
	 */
	@Override
	public void onClick(View v) {
		for (int i = 0; i < pois.size(); i++) {
			if (pois.get(i).getPOIHolder().id.equals((String) v.getTag())) {
				removedPOI = pois.remove(i);
				remove = true;
				add = false;
				notifyDataSetChanged();
				return;
			}
		}
	}

	/**
	 * Called when the ingredient is to be added to the list. Adds the selected
	 * ingredient to the list. Sets the boolean add to true and remove to false
	 * and then calls notifyDataSetChanged(). Sets addedIng to selected.If the
	 * ingredient was already added it displays a {@link Toast} to notify the
	 * user.
	 * 
	 * @param selected
	 *            The name of the ingredient to be added
	 * 
	 * @author Rawan Khalil (rawan.tkhalil@gmail.com)
	 * 
	 */
	public void add(POI selected) {
		System.out.println("In add");
		if (!pois.contains(selected)) {
			System.out.println("Will add to list");
			pois.add(selected);
			add = true;
			addedPOI = selected;
			remove = false;
			notifyDataSetChanged();
		}
		System.out.println("POIS size " + pois.size());
		// System.out.println("In add");
		// if (pois.size() == 0) {
		// pois.add(selected);
		// add = true;
		// addedPOI = selected;
		// remove = false;
		// notifyDataSetChanged();
		// return;
		// }
		// for (int i = 0; i < pois.size(); i++) {
		// System.out.println("current id *" + pois.get(i).getPOIHolder().id
		// + "*");
		// System.out.println("selected id *" + selected.getPOIHolder().id
		// + "*");
		// if (pois.get(i).getPOIHolder().id
		// .equals(selected.getPOIHolder().id)) {
		// pois.add(selected);
		// add = true;
		// addedPOI = selected;
		// remove = false;
		// notifyDataSetChanged();
		// return;
		// }
		// }
	}

	public ArrayList<POI> getList() {
		return pois;
	}

}
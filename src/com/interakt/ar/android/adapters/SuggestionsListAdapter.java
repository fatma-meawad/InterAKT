package com.interakt.ar.android.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.interakt.ar.android.R;
import com.interakt.ar.graphics.poi.POI;
import com.interakt.ar.util.Enumerators;

@SuppressWarnings("rawtypes")
public class SuggestionsListAdapter extends ArrayAdapter {

	private ArrayList<POI> pois = new ArrayList<POI>();

	private Context context;
	private int type;

	@SuppressWarnings("unchecked")
	public SuggestionsListAdapter(Context context, ArrayList<POI> pois, int type) {
		super(context, android.R.layout.simple_list_item_1, pois);
		this.pois = pois;
		this.context = context;
		this.type = type;
	}

	@Override
	public int getCount() {
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		POI poi = pois.get(position);
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = inflater
					.inflate(R.layout.suggestions_list_item, null);
		}
		TextView suggestionTV = (TextView) convertView
				.findViewById(R.id.suggestion_list_item_text);
		suggestionTV.setText(poi.getPOIHolder().name + ' ' + poi.getRadius()
				+ 'm');
		switch (type) {
		case Enumerators.CHILDREN_SUGGESETION:
			convertView.findViewById(R.id.suggestion_list_item_check)
					.setVisibility(View.VISIBLE);
			break;
		case Enumerators.PARENT_SUGGESTION:
			convertView.findViewById(R.id.suggestion_list_item_check)
					.setVisibility(View.GONE);
			break;

		default:
			break;
		}
		return convertView;
	}

	public ArrayList<POI> getList() {
		return pois;
	}

}
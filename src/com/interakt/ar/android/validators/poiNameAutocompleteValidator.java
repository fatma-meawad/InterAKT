package com.interakt.ar.android.validators;

import java.util.ArrayList;

import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.interakt.ar.android.R;
import com.interakt.ar.graphics.poi.POI;
import com.interakt.ar.outdoor.tagging.TaggingDescriptionActivity;
import com.interakt.ar.util.MiscUtils;

public class poiNameAutocompleteValidator implements
		AutoCompleteTextView.Validator {

	ArrayList<POI> dataList;
	TaggingDescriptionActivity v;

	public poiNameAutocompleteValidator(TaggingDescriptionActivity v, ArrayList<POI> dataList) {
		this.dataList = dataList;
		this.v = v;
	}

	@Override
	public boolean isValid(CharSequence text) {
		ArrayList<POI> occurences = new ArrayList<POI>();
		Log.v("Test", "Checking if valid: " + text);
		for (int i = 0; i < dataList.size(); i++) {
			System.out.println("valid Text *" + text + "*");
			System.out.println("valid POI holder name *"
					+ dataList.get(i).getPOIHolder().name + "*");
			if (text != null && text.length() > 0) {

				if (MiscUtils.getPercentageStringDiff(text.toString(), dataList
						.get(i).getPOIHolder().name) <= 0.3) {
					// if
					// (dataList.get(i).getPOIHolder().name.equals(text.toString()))
					// {

					System.out.println("Valid we found a match");
					occurences.add(dataList.get(i));
				}
			}

		}
		ImageView nameWarning = (ImageView) v.findViewById(R.id.name_warning);
		System.out.println("valid The number of occurences is "
				+ occurences.size());
		if (occurences.size() == 0) {
			nameWarning.setVisibility(ImageView.GONE);
			return true;
		}
		nameWarning.setVisibility(ImageView.VISIBLE);
		TaggingDescriptionActivity.setNameSuggestions(occurences);
		return false;
		// Arrays.sort(validWords);
		// if (Arrays.binarySearch(validWords, text.toString()) > 0) {
		// return true;
		// }
		// return false;
	}

	@Override
	public CharSequence fixText(CharSequence invalidText) {
		Log.v("Test", "Returning fixed text");

		/*
		 * I'm just returning an empty string here, so the field will be
		 * blanked, but you could put any kind of action here, like popping up a
		 * dialog?
		 * 
		 * Whatever value you return here must be in the list of valid words.
		 */
		return invalidText;
	}
}

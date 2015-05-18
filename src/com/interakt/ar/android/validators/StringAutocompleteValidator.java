package com.interakt.ar.android.validators;

import java.util.ArrayList;


import android.app.Activity;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

public class StringAutocompleteValidator implements
		AutoCompleteTextView.Validator {

	ArrayList<String> dataList;
	Activity v;
	private int errorMessageId;

	public StringAutocompleteValidator(Activity v, ArrayList<String> dataList,int errorMessageId) {
		this.dataList = dataList;
		this.v = v;
		this.errorMessageId = errorMessageId;
	}

	@Override
	public boolean isValid(CharSequence text) {
		ArrayList<String> occurences = new ArrayList<String>();
		Log.v("Test", "Checking if valid: " + text);
		for (int i = 0; i < dataList.size(); i++) {
			System.out.println("valid Text *" + text + "*");
			System.out.println("valid POI holder name *"
					+ dataList.get(i)+ "*");
			if (text != null && text.length() > 0) {

				
				if (dataList.get(i).equals(text.toString())) {

					System.out.println("Valid we found a match");
					occurences.add(dataList.get(i));
				}
			}

		}
		TextView warning = (TextView) v.findViewById(errorMessageId);
		warning.setText("* Please enter an already existing entry");
		System.out.println("valid The number of occurences is "
				+ occurences.size());
		if (occurences.size() == 0 && text !=null &&text.length()>0) {
			warning.setVisibility(ImageView.VISIBLE);
			return false;
		}
		warning.setVisibility(ImageView.GONE);
		
		return true;
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

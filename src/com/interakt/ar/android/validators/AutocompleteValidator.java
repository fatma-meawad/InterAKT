package com.interakt.ar.android.validators;

import java.util.ArrayList;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.interakt.ar.android.R;
import com.interakt.ar.graphics.poi.POI;

public class AutocompleteValidator implements AutoCompleteTextView.Validator {

	ArrayList<POI> dataList;
	Activity mainView;
	int textViewId;
	int autocompleteId;

	public AutocompleteValidator(Activity mainView, int textViewId,
			int autocompleteId, ArrayList<POI> dataList) {
		System.out.println("valid in constructor");
		this.dataList = dataList;
		this.mainView = mainView;
		this.textViewId = textViewId;
		this.autocompleteId = autocompleteId;

	}

	public void updateUI(boolean isValid) {
		if (isValid) {
			mainView.findViewById(autocompleteId).setBackgroundDrawable(
					mainView.getResources()
							.getDrawable(R.drawable.no_validation));
			mainView.findViewById(textViewId).setVisibility(View.GONE);

		} else {
			mainView.findViewById(autocompleteId).setBackgroundDrawable(
					mainView.getResources()
							.getDrawable(R.drawable.red_validation_border));
			mainView.findViewById(textViewId).setVisibility(View.VISIBLE);


		}
	}

	public boolean isValid(CharSequence text) {
		if (text == null || text.length() == 0) {
			System.out.println("rana Empty text");
			updateUI(true);
			return true;
		}
		Log.v("Test", "Checking if valid: " + text);
		for (int i = 0; i < dataList.size(); i++) {
			if (dataList.get(i).getPOIHolder().name.equals(text.toString())) {
				System.out.println("rana same name text");
				updateUI(true);
				return true;
			}
		}
		System.out.println("rana invalid text");
		updateUI(false);
		return false;
		// Arrays.sort(validWords);
		// if (Arrays.binarySearch(validWords, text.toString()) > 0) {
		// return true;
		// }
		// return false;
	}

	public CharSequence fixText(CharSequence invalidText) {
		Log.v("Test", "Returning fixed text");
		System.out.println("valid In fix text");
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

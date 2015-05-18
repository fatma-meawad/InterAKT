package com.interakt.ar.android.ui;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.interakt.ar.android.R;
import com.interakt.ar.android.adapters.SuggestionsListAdapter;
import com.interakt.ar.graphics.poi.POI;
import com.interakt.ar.outdoor.tagging.TaggingDescriptionActivity;
import com.interakt.ar.util.Enumerators;

public class SuggestionsBuilder extends AlertDialog.Builder {

	private Context context;
	private ListView suggestionsListView;
	private SuggestionsListAdapter suggestionListAdapter;
	private ArrayList<POI> dataList;
	private LinearLayout mainView;
	private PopupWindow popUp;
	private AlertDialog dialog;
	private View lastSelectedView;
	private int suggestionEnum;
	private POI selectedPOI;

	public SuggestionsBuilder(Context context, ArrayList<POI> dataList,
			int positiveButtonMsg, int negativeButtonMsg, int msg,
			int suggestionEnum) {
		super(context);
		this.context = context;
		this.dataList = dataList;
		this.suggestionEnum = suggestionEnum;
		LayoutInflater inflater = LayoutInflater.from(context);
		mainView = (LinearLayout) inflater.inflate(R.layout.suggestions, null);
		suggestionsListView = (ListView) mainView
				.findViewById(R.id.suggestions_list_view);
		TextView msgTV = (TextView) mainView.findViewById(R.id.suggestions_msg);
		msgTV.setText(msg);
		initSuggestionsList();
		this.setView(mainView);
		this.setCancelable(false);

		this.setPositiveButton(
				context.getResources().getString(positiveButtonMsg),
				new DialogInterface.OnClickListener() {
					public void onClick(
							@SuppressWarnings("unused") final DialogInterface dialog,
							@SuppressWarnings("unused") final int id) {

						switch (getSuggestionEnum()) {
						case Enumerators.NAME_SUGGESTION:
							AutoCompleteTextView nameTextView = (AutoCompleteTextView) ((TaggingDescriptionActivity) SuggestionsBuilder.this.context)
									.findViewById(R.id.nameTextView);
							nameTextView.setText(selectedPOI.getPOIHolder().name);
							((TaggingDescriptionActivity) SuggestionsBuilder.this.context)
									.findViewById(R.id.name_warning)
									.setVisibility(View.GONE);
							break;
						case Enumerators.PARENT_SUGGESTION:
							AutoCompleteTextView parentTextView = (AutoCompleteTextView) ((TaggingDescriptionActivity) SuggestionsBuilder.this.context)
									.findViewById(R.id.parentTextView);
							parentTextView.setText(selectedPOI.getPOIHolder().name);
							((TaggingDescriptionActivity) SuggestionsBuilder.this.context).selectedParent = selectedPOI;
							((TaggingDescriptionActivity) SuggestionsBuilder.this.context)
									.findViewById(R.id.parentTextView)
									.setBackgroundDrawable(
											mainView.getContext()
													.getResources()
													.getDrawable(
															R.drawable.no_validation));
							((TaggingDescriptionActivity) SuggestionsBuilder.this.context)
									.findViewById(R.id.parentErrorMsg)
									.setVisibility(mainView.GONE);

							dialog.cancel();
							if (SuggestionsBuilder.this.dialog.isShowing())
								SuggestionsBuilder.this.dialog.dismiss();
							break;

						case Enumerators.CHILDREN_SUGGESETION:
							int cntChoice = suggestionsListView.getCount();
							SparseBooleanArray sparseBooleanArray = suggestionsListView
									.getCheckedItemPositions();

							for (int i = 0; i < cntChoice; i++) {
								if (sparseBooleanArray.get(i) == true)
									((TaggingDescriptionActivity) SuggestionsBuilder.this.context)
											.addSuggestedChild((POI) suggestionsListView
													.getItemAtPosition(i));
							}
							dialog.cancel();
							if (SuggestionsBuilder.this.dialog.isShowing())
								SuggestionsBuilder.this.dialog.dismiss();
							((TaggingDescriptionActivity) SuggestionsBuilder.this.context)
									.displayChildrenSuggestion();
							break;
						}
					}
				});
		this.setNegativeButton(
				context.getResources().getString(negativeButtonMsg),
				new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog,
							@SuppressWarnings("unused") final int id) {
						dialog.cancel();
						if (SuggestionsBuilder.this.dialog.isShowing())
							SuggestionsBuilder.this.dialog.dismiss();
						switch (getSuggestionEnum()) {
						case Enumerators.NAME_SUGGESTION:
							((TaggingDescriptionActivity) SuggestionsBuilder.this.context)
									.findViewById(R.id.name_warning)
									.setVisibility(View.GONE);
						}
						// dismiss
					}
				});
		dialog = this.create();
		dialog.show();
		dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
	}

	public int getSuggestionEnum() {
		return suggestionEnum;
	}

	public AlertDialog getDialog() {
		return dialog;
	}

	public void initSuggestionsList() {
		System.out.println("suggestion data list size " + dataList.size());
		suggestionListAdapter = new SuggestionsListAdapter(context, dataList,
				suggestionEnum);
		suggestionsListView.setAdapter(suggestionListAdapter);
		suggestionsListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapter, View v,
					int position, long id) {
				selectedPOI = (POI) suggestionListAdapter.getItem(position);
				dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
				if (lastSelectedView != null) {
					v.setBackgroundColor(0x00000000);
					System.out
							.println("We are removing the color of the selected item");
				}
				lastSelectedView = v;
				v.setBackgroundColor(0xff9900);
			}
		});
	}
}

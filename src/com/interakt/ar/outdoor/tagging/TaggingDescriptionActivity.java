package com.interakt.ar.outdoor.tagging;


import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.interakt.ar.Settings;
import com.interakt.ar.android.R;
import com.interakt.ar.android.adapters.POIListAdapter;
import com.interakt.ar.android.sensors.LocationBased;
import com.interakt.ar.android.ui.SuggestionsBuilder;
import com.interakt.ar.android.validators.AutocompleteValidator;
import com.interakt.ar.android.validators.StringAutocompleteValidator;
import com.interakt.ar.android.validators.poiNameAutocompleteValidator;
import com.interakt.ar.graphics.poi.POI;
import com.interakt.ar.graphics.poi.POIHolder;
import com.interakt.ar.networking.ConnectionDetector;
import com.interakt.ar.networking.ServerAPI;
import com.interakt.ar.util.Enumerators;
import com.interakt.ar.util.LocationUtils;
import com.interakt.ar.util.MiscUtils;

public class TaggingDescriptionActivity extends FragmentActivity {

	private ArrayList<POI> dataList;
	private HashMap<String, POI> poisHashMap = new HashMap<String, POI>();
	private Context context;
	public AutoCompleteTextView nameAutocompleteTextField;
	ArrayAdapter<POI> nameAutoCompleteAdapter;

	AutoCompleteTextView parentAutocompleteTextField;
	ArrayAdapter<POI> parentAutoCompleteAdapter;

	AutoCompleteTextView childrenAutocompleteTextField;
	ArrayAdapter<POI> childrenAutoCompleteAdapter;
	ListView childrenList;
	POIListAdapter childrenListAdapter;

	public POI selectedParent;

	ArrayList<String> categories = new ArrayList<String>();
	AutoCompleteTextView categoriesAutocompleteTextField;
	ArrayAdapter<String> categoriesAutoCompleteAdapter;

	ArrayList<String> sources = new ArrayList<String>();
	AutoCompleteTextView sourceAutocompleteTextField;
	ArrayAdapter<String> sourceAutoCompleteAdapter;

	static ArrayList<POI> nameSuggestions = new ArrayList<POI>();
	ArrayList<POI> parentSuggestions = new ArrayList<POI>();
	ArrayList<POI> childrenSuggestions = new ArrayList<POI>();
	private GoogleMap mMap;
	private double[] user_lat_long;
	private double[] poi_lat_long;
	public Handler handler = new Handler();
	private EditText description;
	private SharedPreferences mSharedPreferences;

	private boolean editMode;
	private String nameStr;
	private String descriptionStr;
	private String category = "arbrowser";
	private String source = "arbrowser";
	private String parentId;
	private ArrayList<String> childrenIds;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(0, 0);
		mSharedPreferences = getApplicationContext().getSharedPreferences(
				"com.mesai.nativecamra", 0);
		LocationBased.ignoreCounting = true;
		setContentView(R.layout.poi_information);
		user_lat_long = getIntent().getDoubleArrayExtra("USER_LAT_LONG");
		poi_lat_long = getIntent().getDoubleArrayExtra("POI_LAT_LONG");
		editMode = getIntent().getBooleanExtra("EDIT", false);
		if (editMode) {
			nameStr = getIntent().getStringExtra("POI_NAME");
			descriptionStr = getIntent().getStringExtra("POI_DESCRIPTION");
			category = getIntent().getStringExtra("POI_CATEGORY");
			source = getIntent().getStringExtra("POI_SOURCE");
			parentId = getIntent().getStringExtra("POI_PARENTID");
			childrenIds = getIntent()
					.getStringArrayListExtra("POI_CHILDRENIDS");
		}
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
		if (!cd.isConnectingToInternet()) {
			Toast.makeText(TaggingDescriptionActivity.this,
					"No Internet Connection !", Toast.LENGTH_LONG).show();

		} else {
			new LoadingPOIAsynckTask().execute(user_lat_long[0],
					user_lat_long[1], Settings.RADIUS_RANGE, Settings.LIMIT);
			new GetCategorySourcesAsyncTask().execute();

		}

		mMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		mMap.addMarker(new MarkerOptions()
				.position(new LatLng(user_lat_long[0], user_lat_long[1]))
				.title("Your Location")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.mylocation)));

		mMap.addMarker(new MarkerOptions()
				.position(new LatLng(poi_lat_long[0], poi_lat_long[1]))
				.title("New POI location").draggable(true));

		CameraUpdate center = CameraUpdateFactory.newLatLngZoom(new LatLng(
				user_lat_long[0], user_lat_long[1]), 17);

		mMap.animateCamera(center);

		mMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		mMap.setOnMarkerDragListener(new OnMarkerDragListener() {

			@Override
			public void onMarkerDragStart(Marker arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMarkerDragEnd(Marker arg0) {
				poi_lat_long[0] = arg0.getPosition().latitude;
				poi_lat_long[1] = arg0.getPosition().longitude;

			}

			@Override
			public void onMarkerDrag(Marker arg0) {

			}
		});

		TextView submit = (TextView) findViewById(R.id.tagging_information_poi);
		description = (EditText) findViewById(R.id.add_poi_description);
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (nameAutocompleteTextField.getText().toString().length() == 0) {
					Toast.makeText(TaggingDescriptionActivity.this,
							"Name can't be empty", Toast.LENGTH_LONG).show();
					return;
				}
				if (categoriesAutocompleteTextField.getText().toString()
						.length() == 0) {
					Toast.makeText(TaggingDescriptionActivity.this,
							"Category can't be empty", Toast.LENGTH_LONG)
							.show();
					return;
				}
				if (sourceAutocompleteTextField.getText().toString().length() == 0) {
					Toast.makeText(TaggingDescriptionActivity.this,
							"Source can't be empty", Toast.LENGTH_LONG).show();
					return;
				}
				String username = mSharedPreferences.getString("USERNAME", "");
				System.out.println(username);
				ConnectionDetector cd = new ConnectionDetector(
						getApplicationContext());
				if (!cd.isConnectingToInternet()) {
					Toast.makeText(TaggingDescriptionActivity.this,
							"No Internet Connection !", Toast.LENGTH_LONG)
							.show();
					return;
				}
				new AddPOIAsynckTask().execute(poi_lat_long[0],
						poi_lat_long[1], nameAutocompleteTextField.getText()
								.toString(), getSelectedParent(),
						getSelectedChildren(),
						description.getText().toString(),
						categoriesAutocompleteTextField.getText().toString(),
						sourceAutocompleteTextField.getText().toString(),
						username);
			}

		});
		nameAutocompleteTextField = (AutoCompleteTextView) findViewById(R.id.nameTextView);
		childrenList = (ListView) findViewById(R.id.children_list_view);
		childrenListAdapter = new POIListAdapter(this, new ArrayList<POI>());
		childrenAutocompleteTextField = (AutoCompleteTextView) findViewById(R.id.children_autocomplete_textView);
		categoriesAutocompleteTextField = (AutoCompleteTextView) findViewById(R.id.add_poi_category);
		categoriesAutocompleteTextField.setText("arbrowser");
		sourceAutocompleteTextField = (AutoCompleteTextView) findViewById(R.id.add_poi_source);
		sourceAutocompleteTextField.setText("arbrowser");
		parentAutocompleteTextField = (AutoCompleteTextView) findViewById(R.id.parentTextView);
		if (editMode) {
			nameAutocompleteTextField.setText(nameStr);
			categoriesAutocompleteTextField.setText(category);
			sourceAutocompleteTextField.setText(source);
			description.setText(descriptionStr);
		}
	
	}

	private void initChildrenList() {

		childrenListAdapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				if (childrenListAdapter.add) {
					childrenAutoCompleteAdapter
							.remove(childrenListAdapter.addedPOI);
					com.interakt.ar.util.Utility.setListViewHeightBasedOnChildren(childrenList);
				} else {
					childrenAutoCompleteAdapter
							.add(childrenListAdapter.removedPOI);
					com.interakt.ar.util.Utility.setListViewHeightBasedOnChildren(childrenList);
				}
			}
		});
		childrenList.setAdapter(childrenListAdapter);
	}

	private void initChildrenAutoCompleteField() {

		childrenAutocompleteTextField.setValidator(new AutocompleteValidator(
				this, R.id.children_error_msg,
				R.id.children_autocomplete_textView, dataList));
		childrenAutocompleteTextField
				.setOnFocusChangeListener(new FocusListener());
		childrenAutoCompleteAdapter = new ArrayAdapter<POI>(this,
				R.layout.list_item, (ArrayList<POI>) dataList.clone());
		childrenAutocompleteTextField.setAdapter(childrenAutoCompleteAdapter);
		childrenAutocompleteTextField.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				childrenAutocompleteTextField.showDropDown();
			}
		});
		childrenAutocompleteTextField
				.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						POI selected = childrenAutoCompleteAdapter
								.getItem(position);
						addSuggestedChild(selected);
					}
				});
		childrenAutocompleteTextField
				.setOnEditorActionListener(new OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						System.out.println("In children editor action");
						String text = v.getText().toString();
						childrenSuggestions = getPossibleChildren();
						if (childrenSuggestions.size() > 0) {
							findViewById(R.id.children_warning).setVisibility(
									View.VISIBLE);
						} else {
							findViewById(R.id.children_warning).setVisibility(
									View.GONE);
						}
						return false;
					}
				});

	}

	private void initCategoriesAutoCompleteField() {

		categoriesAutocompleteTextField
				.setValidator(new StringAutocompleteValidator(this, categories,
						R.id.categoryErrorMsg));
		categoriesAutocompleteTextField
				.setOnFocusChangeListener(new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						((AutoCompleteTextView) v).performValidation();
						if (((AutoCompleteTextView) v).length() == 0) {
							((TextView) findViewById(R.id.categoryErrorMsg))
									.setVisibility(View.GONE);
						}

					}
				});
		categoriesAutoCompleteAdapter = new ArrayAdapter<String>(this,
				R.layout.list_item, categories);
		categoriesAutocompleteTextField
				.setAdapter(categoriesAutoCompleteAdapter);
	}

	private void initSourceAutoCompleteField() {

		sourceAutocompleteTextField
				.setValidator(new StringAutocompleteValidator(this, sources,
						R.id.sourceErrorMsg));
		sourceAutocompleteTextField
				.setOnFocusChangeListener(new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						((AutoCompleteTextView) v).performValidation();
						if (((AutoCompleteTextView) v).length() == 0) {
							((TextView) findViewById(R.id.sourceErrorMsg))
									.setVisibility(View.GONE);
						}
					}
				});
		sourceAutoCompleteAdapter = new ArrayAdapter<String>(this,
				R.layout.list_item, sources);
		sourceAutocompleteTextField.setAdapter(sourceAutoCompleteAdapter);
	}

	public static ArrayList<POI> getNameSuggestions() {
		return nameSuggestions;
	}

	public static void setNameSuggestions(ArrayList<POI> suggestions) {
		nameSuggestions = suggestions;
	}

	public Context getThisContext() {
		return context;
	}

	public String getSelectedParent() {
		if (selectedParent == null)
			return "";
		else
			return selectedParent.getPOIHolder().id;
	}

	public String getSelectedChildren() {
		if (childrenListAdapter.getList() == null
				|| childrenListAdapter.getList().size() == 0)
			return "";
		else {
			String children = childrenListAdapter.getList().get(0)
					.getPOIHolder().id;
			for (int i = 1; i < childrenListAdapter.getList().size(); i++) {
				children = children
						+ ","
						+ childrenListAdapter.getList().get(i).getPOIHolder().id;
			}
			return children;
		}
	}

	private void initNameAutoCompleteField() {

		nameAutocompleteTextField
				.setValidator(new poiNameAutocompleteValidator(this, dataList));
		nameAutocompleteTextField.setOnFocusChangeListener(new FocusListener());
		nameAutoCompleteAdapter = new ArrayAdapter<POI>(this,
				R.layout.list_item, dataList);
		nameAutocompleteTextField.setAdapter(nameAutoCompleteAdapter);
		nameAutocompleteTextField
				.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						POI selected = nameAutoCompleteAdapter
								.getItem(position);
						nameAutocompleteTextField.setText(selected
								.getPOIHolder().name);
					}
				});

	}

	private void initParentAutoCompleteField() {

		parentAutocompleteTextField.setValidator(new AutocompleteValidator(
				this, R.id.parentErrorMsg, R.id.parentTextView, dataList));
		parentAutocompleteTextField
				.setOnFocusChangeListener(new FocusListener());
		parentAutoCompleteAdapter = new ArrayAdapter<POI>(this,
				R.layout.list_item, dataList);
		parentAutocompleteTextField.setAdapter(parentAutoCompleteAdapter);
		parentAutocompleteTextField.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				parentAutocompleteTextField.showDropDown();
			}
		});
		parentAutocompleteTextField
				.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						selectedParent = parentAutoCompleteAdapter
								.getItem(position);
						parentAutocompleteTextField.setText(selectedParent
								.getPOIHolder().name);
					}
				});
		parentAutocompleteTextField
				.setOnEditorActionListener(new OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						System.out.println("In parent editor action");
						String text = v.getText().toString();
						parentSuggestions = getPossibleParent();
						if (parentSuggestions.size() > 0) {
							findViewById(R.id.parentWarning).setVisibility(
									View.VISIBLE);
						} else {
							findViewById(R.id.parentWarning).setVisibility(
									View.GONE);
						}
						return false;
					}
				});
	}

	public ArrayList<POI> getPossibleParent() {
		HashMap<String, POI> possibleParents = new HashMap<String, POI>();
		for (POI poi : dataList) {
			POIHolder holder = poi.getPOIHolder();
			if (nameAutocompleteTextField.getText() != null
					&& nameAutocompleteTextField.getText().length() > 0) {
				if (MiscUtils.getPercentageStringDiff(holder.name,
						nameAutocompleteTextField.getText().toString()) <= 0.2) {
					if (holder.parent != null && holder.parent.length() > 0)
						possibleParents.put(holder.parent,
								poisHashMap.get(holder.parent));

				}
			}

			if (parentAutocompleteTextField.getText() != null
					&& parentAutocompleteTextField.getText().length() > 0) {
				if (MiscUtils.getPercentageStringDiff(
						parentAutocompleteTextField.getText().toString(),
						holder.name) <= 0.2) {
					possibleParents.put(holder.id, poisHashMap.get(holder.id));
				}
			}

		}
		if (possibleParents.values() != null)
			return new ArrayList<POI>(possibleParents.values());
		return new ArrayList<POI>();
	}

	public ArrayList<POI> getPossibleChildren() {
		HashMap<String, POI> possibleChildren = new HashMap<String, POI>();
		for (POI poi : dataList) {
			POIHolder holder = poi.getPOIHolder();
			if (holder.children != null && holder.children.length() > 0) {
				if (holder.parent != null && holder.parent.length() > 0
						&& selectedParent != null) {
					if (poisHashMap.containsKey(holder.parent)) {
						if (MiscUtils
								.getPercentageStringDiff(selectedParent
										.getPOIHolder().name,
										poisHashMap.get(holder.parent)
												.getPOIHolder().name) <= 0.2) {
							System.out.println("children " + holder.children);
							String[] childrenIds = holder.children.split(",");
							for (int i = 0; i < childrenIds.length; i++) {
								possibleChildren.put(childrenIds[i],
										poisHashMap.get(childrenIds[i]));
							}
						}
					}
				}
				if (nameAutocompleteTextField.getText() != null
						&& nameAutocompleteTextField.getText().length() > 0) {
					if (MiscUtils.getPercentageStringDiff(holder.name,
							nameAutocompleteTextField.getText().toString()) <= 0.2) {
						String[] childrenIds = holder.children.split(",");
						for (int i = 0; i < childrenIds.length; i++) {
							possibleChildren.put(childrenIds[i],
									poisHashMap.get(childrenIds[i]));
						}
					}
				}

			}
		}
		if (possibleChildren.values() != null)
			return new ArrayList<POI>(possibleChildren.values());
		return new ArrayList<POI>();
	}

	public void displayChildrenSuggestion() {
		System.out.println("In children editor action");
		childrenSuggestions = getPossibleChildren();
		if (childrenSuggestions.size() > 0) {
			findViewById(R.id.children_warning).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.children_warning).setVisibility(View.GONE);
		}
	}

	class FocusListener implements View.OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v.getId() == childrenAutocompleteTextField.getId() && !hasFocus) {
				displayChildrenSuggestion();
			}

			Log.v("Test", "Focus changed");
			// if (v.getId() == nameAutoComplete.getId() && !hasFocus) {
			if ((v.getId() == parentAutocompleteTextField.getId()
					|| v.getId() == childrenAutocompleteTextField.getId() || v
					.getId() == nameAutocompleteTextField.getId()) && !hasFocus) {
				Log.v("Test", "Performing validation");
				((AutoCompleteTextView) v).performValidation();
			}

		}
	}

	public void addSuggestedChild(POI child) {
		childrenListAdapter.add(child);
		childrenAutocompleteTextField.setText("");

	}

	private class LoadingPOIAsynckTask extends AsyncTask<Object, Void, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			String username = mSharedPreferences.getString("USERNAME", "");
			final ArrayList<String> out = ServerAPI.getPoiByRadius(username,
					((Double) params[0]).doubleValue(),
					((Double) params[1]).doubleValue(),
					((Double) params[2]).doubleValue());

			for (int i = 0; i < out.size() && !this.isCancelled(); i++) {

				parsingPOI(out.get(i), null);
			}
			return null;

		}

		@Override
		protected void onPostExecute(Object result) {
			dataList = new ArrayList<POI>(poisHashMap.values());
			initChildrenList();
			initNameAutoCompleteField();
			initParentAutoCompleteField();
			initChildrenAutoCompleteField();
			ImageView nameWarning = (ImageView) findViewById(R.id.name_warning);
			nameWarning.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// your code here
					System.out
							.println("The user has clicked on the name warning sign");
					new SuggestionsBuilder(TaggingDescriptionActivity.this,
							getNameSuggestions(), R.string.goEdit, R.string.no,
							R.string.createSuggestionMsg,
							Enumerators.NAME_SUGGESTION);
				}
			});
			ImageView parentWarning = (ImageView) findViewById(R.id.parentWarning);
			parentWarning.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// your code here
					System.out
							.println("The user has clicked on the parent warning sign");
					new SuggestionsBuilder(TaggingDescriptionActivity.this,
							parentSuggestions, R.string.ok, R.string.cancel,
							R.string.suggestionMsg,
							Enumerators.PARENT_SUGGESTION);
				}
			});
			ImageView childrenWarning = (ImageView) findViewById(R.id.children_warning);
			parentWarning.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// your code here
					System.out
							.println("The user has clicked on the children warning sign");
					new SuggestionsBuilder(TaggingDescriptionActivity.this,
							childrenSuggestions, R.string.ok, R.string.cancel,
							R.string.suggestionMsg,
							Enumerators.CHILDREN_SUGGESETION);
				}
			});
			if (dataList == null || dataList.size() == 0) {
				childrenAutocompleteTextField.setEnabled(false);
				parentAutocompleteTextField.setEnabled(false);
			}

			if (editMode) {
				for (String s : childrenIds) {
					for (String id : poisHashMap.keySet()) {
						if (id.equals(s)) {
							addSuggestedChild(poisHashMap.get(s));
						}
						if (id.equals(parentId)) {
							selectedParent = poisHashMap.get(id);
							parentAutocompleteTextField.setText(selectedParent
									.getPOIHolder().name);
						}
					}
				}

			}

			super.onPostExecute(result);
		}
	}

	public void parsingPOI(String poi, POI parent) {
		try {
			final POIHolder holder = new POIHolder();
			JSONObject POIJSONObject = new JSONObject(poi);
			holder.id = POIJSONObject.getString("id");
			holder.category = POIJSONObject.getString("category");
			holder.source = POIJSONObject.getString("source");
			holder.name = POIJSONObject.getString("name");
			holder.latitude = POIJSONObject.getString("latitude");
			holder.longitude = POIJSONObject.getString("longitude");
			holder.parentObj = parent;
			POI POI = new POI(TaggingDescriptionActivity.this, false);
			POI.setHolder(holder);
			String childrenstr = POIJSONObject.getString("children_pois");
			if (childrenstr.length() > 0) {
				JSONArray children = new JSONArray(childrenstr);
				for (int i = 0; i < children.length(); i++) {
					parsingPOI((String) children.get(i), POI);
				}
			}
			final double[] geoloc = { Double.parseDouble(holder.latitude),
					Double.parseDouble(holder.longitude), 0 };
			double[] current = { user_lat_long[0], user_lat_long[1], 0 };
			double[] enu = LocationUtils.GEOtoENU(current, geoloc);

			POI.setTranslationMatrix((float) enu[0], (float) enu[1],
					(float) enu[2]);
			POI.caclulateRadius();

			poisHashMap.put(holder.id, POI);
			handler.post(new Runnable() {

				@Override
				public void run() {
					mMap.addMarker(new MarkerOptions()
							.position(new LatLng(geoloc[0], geoloc[1]))
							.title(holder.name)
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.poimap)));

				}
			});

		} catch (JSONException e) {

			e.printStackTrace();
		}
	}

	private class AddPOIAsynckTask extends AsyncTask<Object, Void, Object> {

		private ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(TaggingDescriptionActivity.this, "",
					"Sending Data...", true);
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Object result) {
			progress.dismiss();
			if ((Boolean) result) {
				Toast.makeText(TaggingDescriptionActivity.this,
						"Your POI is been added.", Toast.LENGTH_LONG).show();
				finish();
				Intent i = new Intent(TaggingDescriptionActivity.this, com.interakt.ar.outdoor.browsing.BrowsingMainActivity.class);
				i.putExtra("MODE_CHOSEN", 1);
				startActivity(i);
			} else {
				Toast.makeText(TaggingDescriptionActivity.this,
						"Something went wrong, try again later.",
						Toast.LENGTH_LONG).show();
			}
			super.onPostExecute(result);
		}

		@Override
		protected Object doInBackground(Object... params) {

			// ServerAPI.addPOI(((Double) params[1]), ((Double) params[2]),
			// ((String) params[3]));
			if (!editMode) {
				return ServerAPI.addPOI(((Double) params[0]),
						((Double) params[1]), (String) params[2],
						(String) params[3], (String) params[4],
						(String) params[5], (String) params[6],
						(String) params[7], (String) params[8]);
			} else {
				return ServerAPI.addPOI(((Double) params[0]),
						((Double) params[1]), (String) params[2],
						(String) params[3], (String) params[4],
						(String) params[5], (String) params[6],
						(String) params[7], (String) params[8]);
			}

		}

	}

	private class GetCategorySourcesAsyncTask extends
			AsyncTask<Object, Void, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			for (String cat : ServerAPI.getCategories()) {
				categories.add(cat);
			}
			for (String src : ServerAPI.getSources()) {
				sources.add(src);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			initCategoriesAutoCompleteField();
			initSourceAutoCompleteField();
			super.onPostExecute(result);
		}

	}

	@Override
	protected void onResume() {
		LocationBased.ignoreCounting = true;
		super.onResume();
	}

	@Override
	protected void onPause() {
		LocationBased.ignoreCounting = false;
		super.onPause();
	}

	boolean save;
	boolean unstopable = true;

	
}
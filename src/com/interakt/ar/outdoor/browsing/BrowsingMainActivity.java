package com.interakt.ar.outdoor.browsing;


import java.io.FileWriter;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.math.Vector3;
import com.interakt.ar.Filter;
import com.interakt.ar.Settings;
import com.interakt.ar.android.R;
import com.interakt.ar.android.sensors.LocationBased;
import com.interakt.ar.android.ui.MapNative;
import com.interakt.ar.android.ui.RangeSeekBar;
import com.interakt.ar.android.ui.RangeSeekBar.OnRangeSeekBarChangeListener;
import com.interakt.ar.android.ui.StatusBar;
import com.interakt.ar.graphics.Shape;
import com.interakt.ar.graphics.poi.POI;
import com.interakt.ar.graphics.poi.POIHolder;
import com.interakt.ar.graphics.poi.POITextureGenerator;
import com.interakt.ar.graphics.radar.Radar;
import com.interakt.ar.listeners.LocationBasedListener;
import com.interakt.ar.listeners.NetworkStateListener;
import com.interakt.ar.listeners.OnShapeClickListener;
import com.interakt.ar.networking.ConnectionDetector;
import com.interakt.ar.networking.NetworkStateReceiver;
import com.interakt.ar.networking.ServerAPI;
import com.interakt.ar.outdoor.AndroidDeviceCameraController;
import com.interakt.ar.outdoor.MainActivity;
import com.interakt.ar.util.Color;
import com.interakt.ar.util.FilterItemHolder;
import com.interakt.ar.util.LocationUtils;

public class BrowsingMainActivity extends MainActivity implements
		LocationBasedListener, NetworkStateListener {

	private Radar radar;
	private BrowsingPOIInfoPanel poiInfoPanel;
	private Button addPOI;
	private static Location currentLocation;
	private Location lastCallLocation;
	private ArrayList<FilterItemHolder> categories = new ArrayList<FilterItemHolder>();
	private ArrayList<FilterItemHolder> sources = new ArrayList<FilterItemHolder>();
	BrowsingRenderer renderer;
	boolean progress;
	private ArrayList<Color> pickingColors = new ArrayList<Color>();
	private Semaphore waitingForCategoryAndSources = new Semaphore(1);
	private ArrayList<AsyncTask<Object, Object, Object>> networkTask = new ArrayList<AsyncTask<Object, Object, Object>>();
	private AndroidDeviceCameraController cameraControl;
	private Semaphore loading;
	private View view;
	private boolean continueLoading = true;
	private SharedPreferences mSharedPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		System.out.println("Here !!");
		super.onCreate(savedInstanceState);
		mSharedPreferences = getApplicationContext().getSharedPreferences(
				"com.mesai.nativecamra", 0);
		Intent i = getIntent();
		com.interakt.ar.Settings.ACTIVE_MODE = i.getIntExtra(
				"MODE_CHOSEN", 0);
		Filter.filter = false;
		Filter.filtered.clear();
		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useGL20 = false;
		// we need to change the default pixel format - since it does not
		// include an alpha channel
		// we need the alpha channel so the camera preview will be seen behind
		// the GL scene
		cfg.r = 8;
		cfg.g = 8;
		cfg.b = 8;
		cfg.a = 8;

		cameraControl = new AndroidDeviceCameraController(this);

		view = initializeForView(renderer = new BrowsingRenderer(this,
				cameraControl), cfg);

		if (graphics.getView() instanceof SurfaceView) {
			SurfaceView glView = (SurfaceView) graphics.getView();
			// force alpha channel - I'm not sure we need this as the GL surface
			// is already using alpha channel
			glView.getHolder().setFormat(PixelFormat.RGBA_8888);
		}
		// we don't want the screen to turn off during the long image saving
		// process
		graphics.getView().setKeepScreenOn(true);
		// keep the original screen size
		origWidth = graphics.getWidth();
		origHeight = graphics.getHeight();
		this.setContentView(R.layout.mainactivity_outdoor);
		main = (FrameLayout) findViewById(R.id.main_layout);
		// map = new Map(this);
		// map.loadMap();
		mapNative = new MapNative(this);
		mapNative.setZoomLevel(16);
		System.out.println("Here !!!");
		mapNative.loadMap();
		main.addView(mapNative);
		System.out.println("Here !!!!");
		Resources r = getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40,
				r.getDisplayMetrics());
		addPOI = new Button(this);
		addPOI.setBackgroundResource(R.drawable.button);
		addPOI.setTextColor(0xffff9900);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, (int) px);
		params.alignWithParent = true;
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		addPOI.setText("POI +");
		addPOI.setId(1);
		addPOI.setLayoutParams(params);
		addPOI.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				startActivity(new Intent(BrowsingMainActivity.this,
						com.interakt.ar.outdoor.tagging.TaggingMainActivity.class));

			}
		});

		radar = new Radar(this, 250);
		Settings.LAYER_NAME_MAX = 250;
		poiInfoPanel = new BrowsingPOIInfoPanel(this);
		generator = new POITextureGenerator(this);
		mainRelative = (RelativeLayout) findViewById(R.id.main_realtive);
		status = new StatusBar(this);
		mainRelative.addView(status);
		RelativeLayout.LayoutParams paramsS = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		paramsS.alignWithParent = true;
		paramsS.addRule(RelativeLayout.LEFT_OF, addPOI.getId());
		paramsS.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		status.setLayoutParams(paramsS);
		mainRelative.addView(addPOI);
		mainProgressBar = new com.interakt.ar.android.ui.ProgressBar(this);
		mainProgressBar.setId(2);
		mainRelative.addView(mainProgressBar);
		mainProgressBar.setVisibility(View.INVISIBLE);
		main.addView(view, 0);
		loading = new Semaphore(1);
		NetworkStateReceiver.addListener(this);
		currentLocation = LocationBased.getLastKnowLocation();
		System.out.println("Here !!!!!");
		lastCallLocation = currentLocation;

		RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(250, 0, this);
		seekBar.setNotifyWhileDragging(true);
		seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
			@Override
			public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar,
					Integer minValue, Integer maxValue) {
				Settings.LAYER_NAME_MIN = maxValue;
				Settings.LAYER_NAME_MAX = minValue;
				radar.conversion(maxValue, minValue, 250);
			}
		});

		RelativeLayout.LayoutParams seekBarParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		seekBarParams.alignWithParent = true;
		seekBarParams.addRule(RelativeLayout.CENTER_VERTICAL);
		seekBarParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		seekBarParams.addRule(RelativeLayout.ABOVE, addPOI.getId());
		seekBarParams.addRule(RelativeLayout.BELOW, mainProgressBar.getId());

		px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
				r.getDisplayMetrics());
		seekBarParams.topMargin = (int) px;
		seekBarParams.bottomMargin = (int) px;
		seekBar.setLayoutParams(seekBarParams);
		mainRelative.addView(seekBar);
		System.out.println("Here end");
	}

	public Radar getRadar() {
		return radar;
	}

	public StatusBar getStatusBar() {
		return status;
	}

	public POITextureGenerator getTextureGenerator() {
		return generator;
	}

	public void removeView(View view) {
		main.removeView(view);
	}

	public void addViewRelative(View view) {
		mainRelative.addView(view);
	}

	public void removeViewRelative(View view) {
		mainRelative.removeView(view);
	}

	public void removeAllViewRelative() {
		mainRelative.removeAllViews();
	}

	public void toggleProgressBar(boolean toggle) {
		if (!toggle) {
			mainProgressBar.setVisibility(View.INVISIBLE);
		} else {
			mainProgressBar.setVisibility(View.VISIBLE);
		}
		progress = toggle;
	}

	public void toggleMap(boolean toggle) {
		if (!toggle) {
			main.removeView(mapNative);
		} else {
			main.addView(mapNative);
		}
	}

	public void toggleMainRelative(boolean toggle) {
		if (!toggle) {
			for (int i = 0; i < mainRelative.getChildCount(); i++) {
				if (mainRelative.getChildAt(i) != mainProgressBar) {
					mainRelative.getChildAt(i).setVisibility(View.INVISIBLE);
				}
			}
		} else {
			for (int i = 0; i < mainRelative.getChildCount(); i++) {
				if (mainRelative.getChildAt(i) != mainProgressBar) {
					mainRelative.getChildAt(i).setVisibility(View.VISIBLE);
				}
			}
		}

	}

	public void togleforPanel(boolean toggle) {
		toggleMainRelative(toggle);
		toggleMap(toggle);

		if (!toggle) {
			radar.hide = true;
			// cameraControl.stopPreviewAsync();
		} else {
			radar.hide = false;
			// cameraControl.startPreviewAsync();
		}
	}

	public com.interakt.ar.android.ui.ProgressBar getProgressBar() {
		return mainProgressBar;
	}

	public Handler getMainHandler() {
		return this.handler;
	}

	public void setFixedSize(int width, int height) {
		if (graphics.getView() instanceof SurfaceView) {
			SurfaceView glView = (SurfaceView) graphics.getView();
			glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
			glView.getHolder().setFixedSize(width, height);
		}
	}

	public void restoreFixedSize() {
		if (graphics.getView() instanceof SurfaceView) {
			SurfaceView glView = (SurfaceView) graphics.getView();
			glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
			glView.getHolder().setFixedSize(origWidth, origHeight);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		radar = null;
		status = null;
		poiInfoPanel = null;
		generator = null;
		view = null;
		mainProgressBar = null;
		lastCallLocation = null;
		currentLocation = null;
		addPOI = null;
		cameraControl = null;
		waitingForCategoryAndSources = null;
		loading = null;
		mapNative.dispose();
		mapNative = null;
		main.removeAllViews();
		main = null;
		mainRelative.removeAllViews();
		mainRelative = null;
		categories.clear();
		categories = null;
		sources.clear();
		sources = null;
		networkTask.clear();
		networkTask = null;
		pickingColors.clear();
		pickingColors = null;
		renderer = null;
	}

	@Override
	protected void onPause() {
		super.onPause();
		LocationBased.removeLocationListener(this);
		mapNative.removeLocationListener();
		continueLoading = false;
		for (AsyncTask task : networkTask) {
			task.cancel(true);
		}
		new SubmitLogAsyncTask().execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		LocationBased.addLocationListener(this);
		mapNative.addLocationListener();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				try {
					if (loading.tryAcquire()) {
						loadPOIs();
					}
				} catch (Exception e) {

				}

			}
		}, 5000);
		continueLoading = true;
		loadCategories();

	}

	public BrowsingPOIInfoPanel getPOIInfoPanel() {
		// TODO Auto-generated method stub
		return poiInfoPanel;
	}

	@Override
	public void onBackPressed() {
		if (poiInfoPanel.getOnScreen()) {
			try {
				removeView(poiInfoPanel);
				poiInfoPanel.setOnScreen(false);
				togleforPanel(true);
				renderer.geoMode.clickedPOI.getPOIHolder().radarPOI.isClicked = false;
				renderer.geoMode.clickedPOI = null;
				// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			} catch (Exception e) {

			}
		} else {
			finish();
		}
	}

	public Button getaddPOI() {
		return addPOI;
	}

	@Override
	public void onLocationChanged(final Location location) {
		if (location.getAccuracy() < 50) {

			status.checkAndUpdate(true, location.getAccuracy());

		} else {

			status.checkAndUpdate(false, location.getAccuracy());

		}

		double[] Old = { currentLocation.getLatitude(),
				currentLocation.getLongitude(), 0 };
		double[] New = { location.getLatitude(), location.getLongitude(), 0 };
		if (currentLocation.distanceTo(location) > 0) {
			if (lastCallLocation.distanceTo(location) < Settings.RADIUS_RANGE / 2) {
				// Tricky part
				if (renderer.canRender.tryAcquire()) {
					if (renderer.geoMode != null) {
						for (Shape POI : renderer.geoMode.getShapes()) { // vheck
																			// here
																			// null
							double[] out = LocationUtils.updateResLocations(
									Old, New, ((POI) POI).getPosition());
							POI.setTranslationMatrix((float) out[0],
									(float) out[1], (float) out[2]);
							((POI) POI).caclulateRadius();

							if (POI.getRadius() < Settings.INNER_RADIUS_RANGE) {
								float newX;
								float newY;
								float newZ;
								newX = (float) (out[0]
										* Settings.INNER_RADIUS_RANGE / POI
										.getRadius());
								newY = (float) (out[1]
										* Settings.INNER_RADIUS_RANGE / POI
										.getRadius());
								newZ = (float) (out[2]
										* Settings.INNER_RADIUS_RANGE / POI
										.getRadius());
								POI.setTranslationMatrix(newX, newY, newZ);
							}

						}
					}
					renderer.canRender.release();
				}

				currentLocation = location;
			} else {

				currentLocation = location;
				lastCallLocation = location;

				mapNative.removeAllPOI();
				if (loading.tryAcquire()) {
					loadPOIs();
				}
			}

		}
		// update the map in the poiInfoPanel
		if (poiInfoPanel != null) {
			poiInfoPanel.map.onLocationChanged(location);
		}

	}

	private void loadPOIs() {
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());

		if (!cd.isConnectingToInternet()) {
			Toast.makeText(BrowsingMainActivity.this,
					"No Internet Connection !", Toast.LENGTH_LONG).show();
			return;
		}

		LoadingPOIAsynckTask task = new LoadingPOIAsynckTask();
		networkTask.add(task);
		task.execute(currentLocation.getLatitude(),
				currentLocation.getLongitude(), Settings.RADIUS_RANGE,
				Settings.LIMIT);

	}

	private void loadCategories() {
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
		if (!cd.isConnectingToInternet()) {
			Toast.makeText(BrowsingMainActivity.this,
					"No Internet Connection !", Toast.LENGTH_LONG).show();
			return;
		}
		GetCategorySourcesAsyncTask task = new GetCategorySourcesAsyncTask();
		networkTask.add(task);
		task.execute();
	}

	private class LoadingPOIAsynckTask extends
			AsyncTask<Object, Object, Object> {

		private ProgressDialog progressDialogue;

		@Override
		protected void onPreExecute() {
			progressDialogue = ProgressDialog.show(BrowsingMainActivity.this,
					"", "Loading POIs...", true);
			super.onPreExecute();

		}

		@Override
		protected void onProgressUpdate(Object... values) {
			progressDialogue.setMessage((String) values[0]);
		}

		@Override
		protected void onPostExecute(Object result) {
			progressDialogue.dismiss();
			super.onPostExecute(result);
			try {
				this.finalize();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		protected Object doInBackground(Object... params) {
			try {
				renderer.canRender.acquire();
			} catch (Exception e1) {

				e1.printStackTrace();
			}

			pickingColors.clear();
			pickingColors.add(new Color((byte) 0, (byte) 0, (byte) 0));
			radar.clearRadar();
			status.setShowfilter(false);// Cant press filter w howa loading
			status.updating();
			renderer.geoMode.clearShapes();
			renderer.geoMode.clearToBeDrawed();
			for (Shape shape : renderer.geoMode.getShapes()) {
				shape.dispose();
			}
			String username = mSharedPreferences.getString("USERNAME", "");

			final ArrayList<String> out = ServerAPI.getPoiByRadius(username,
					((Double) params[0]).doubleValue(),
					((Double) params[1]).doubleValue(),
					((Double) params[2]).doubleValue());

			this.publishProgress("Processing " + out.size() + " POIs");

			for (int i = 0; i < out.size() && !this.isCancelled(); i++) {

				parsingPOI(out.get(i), null, 0);
			}

			try {
				waitingForCategoryAndSources.acquire();
				for (int i = 0; i < categories.size(); i++) {
					for (int j = 0; j < categories.get(i).subItems.size(); j++)
						for (Shape shape : renderer.geoMode.getShapes()) {
							if (categories.get(i).subItems.get(j).filter
									.equals(((POI) shape).getPOIHolder().category)) {
								categories.get(i).subItems.get(j).value++;
							}

						}

				}
				System.out.println(categories);
				for (int i = 0; i < sources.size(); i++) {
					int count = 0;
					String src = sources.get(i).filter;
					for (Shape shape : renderer.geoMode.getShapes()) {
						if (src.equals(((POI) shape).getPOIHolder().source)) {
							count++;
						}
					}
					if (count != 0) {
						sources.get(i).value = count;

					}

				}

				// int count = 0;
				// for (int i = 0; i < categories.size() && count < 10; i++) {
				// if (categories.get(i).value > 0) {
				// for (Shape shape : renderer.geoMode.getShapes()) {
				// if (categories.get(i).filter.equals(((POI) shape)
				// .getPOIHolder().category)) {
				// ((POI) shape).getPOIHolder().isHighlighted = true;
				// count++;
				//
				// }
				// if (count >= 10) {
				// break;
				// }
				// }
				// }
				// }

				// for (int i = 1; i < sources.size(); i++) {
				// for (int j = i; j > 0; j--) {
				// if (sources.get(j).value > sources.get(j - 1).value) {
				// FilterItemHolder temp = sources.get(j);
				// sources.set(j, sources.get(j - 1));
				// sources.set(j - 1, temp);
				// }
				// }
				// }
				status.setCategories(categories);
				status.setSources(sources);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}

			status.setShowfilter(true);
			checkFilter();
			renderer.canRender.release();

			Thread t = new Thread() {

				public void run() {
					System.out.println("In applying textures");
					Bitmap bm;
					for (int i = 0; continueLoading &&  renderer.geoMode != null
							&& i < renderer.geoMode.getShapes().size(); i++) {
						try {

							if (((POI) renderer.geoMode.getShapes().get(i))
									.getPOIHolder().textureGen == null) {

								bm = generator
										.getBitmap(
												((POI) renderer.geoMode
														.getShapes().get(i))
														.getPOIHolder().name,
												((POI) renderer.geoMode
														.getShapes().get(i))
														.getPOIHolder().category,
												((POI) renderer.geoMode
														.getShapes().get(i))
														.getRadius()
														+ "",
												((POI) renderer.geoMode
														.getShapes().get(i))
														.getPOIHolder().childrenLst
														.size()
														+ "",
												((POI) renderer.geoMode
														.getShapes().get(i))
														.getPOIHolder().likes
														+ "",
												((POI) renderer.geoMode
														.getShapes().get(i))
														.getPOIHolder().rating
														+ "",
												((POI) renderer.geoMode
														.getShapes().get(i))
														.getPOIHolder().numberOfComments,
												((POI) renderer.geoMode
														.getShapes().get(i))
														.getPOIHolder().isHighlighted ? "true"
														: "false",
												((POI) renderer.geoMode
														.getShapes().get(i))
														.getPOIHolder().thumbnail);
								((POI) renderer.geoMode.getShapes().get(i))
										.getPOIHolder().textureGen = generator
										.bitmapToPixmap(bm);
								bm.recycle();
								bm = null;
							}

						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}
					}
				}
			};
			t.start();

			if (this.isCancelled()) {
				for (Shape shape : renderer.geoMode.getShapes()) {
					shape.dispose();

				}
				pickingColors.clear();
				radar.clearRadar();
				renderer.geoMode.clearShapes();
				renderer.geoMode.clearToBeDrawed();
			}
			loading.release();
			networkTask.remove(this);
			return null;

		}
	}

	private class GetCategorySourcesAsyncTask extends
			AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			String username = mSharedPreferences.getString("USERNAME", "");

			// TODO
			for (ArrayList<String> cat : ServerAPI.getCategories(username)) {
				FilterItemHolder item = new FilterItemHolder(cat.get(0));
				for (int i = 1; i < cat.size(); i++) {
					item.addFilterSubItem(new FilterItemHolder(cat.get(i)));
				}
				categories.add(item);
			}
			System.out.println("categories >>>" + categories.toString());
			for (String src : ServerAPI.getSources()) {
				sources.add(new FilterItemHolder(src));
			}

			waitingForCategoryAndSources.release();

			networkTask.remove(this);
			return null;
		}

	}

	private class SubmitLogAsyncTask extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			
			if (Settings.TobeLogged.length() > 0) {
				FileWriter f;
				try {
				 f = new FileWriter(Environment.getExternalStorageDirectory()+
				                 "/interAKT_log.txt");
				 f.write(Settings.TobeLogged);
				 f.flush();
				 f.close();
				}catch(Exception e)
				{
					
				}
				Settings.TobeLogged = "";
			}

			return null;
		}

	}

	public int parsingPOI(String poi, POI parent, int level) {

		try {
			POIHolder holder = new POIHolder();
			JSONObject POIJSONObject = new JSONObject(poi);
			holder.id = POIJSONObject.getString("id");
			holder.category = POIJSONObject.getString("category");
			holder.source = POIJSONObject.getString("source");
			holder.name = POIJSONObject.getString("name");
			holder.latitude = POIJSONObject.getString("latitude");
			holder.longitude = POIJSONObject.getString("longitude");
			holder.likes = POIJSONObject.getString("likes");
			holder.rating = POIJSONObject.getString("rating");
			holder.numberOfComments = POIJSONObject.getString("comments");
			holder.isHighlighted = POIJSONObject.getString("highlight")
					.equalsIgnoreCase("1");
			holder.texture = "models/POItobefixed1.png";
			holder.textureLock = "models/POItobefixed2.png";
			holder.parentObj = parent;
			holder.thumbnail = POIJSONObject.getString("img");
			String childrenstr = POIJSONObject.getString("children_pois");
			POI POI = new POI(BrowsingMainActivity.this, true);
			POI.setHolder(holder);

			if (childrenstr.length() > 0) {
				JSONArray children = new JSONArray(childrenstr);
				for (int i = 0; i < children.length(); i++) {
					int out = parsingPOI((String) children.get(i), POI,
							level + 1);
					if (level < out) {
						level = out;
					}

				}
			}
			holder.level = level;
			final double[] geoloc = { Double.parseDouble(holder.latitude),
					Double.parseDouble(holder.longitude), 0 };
			double[] current = { currentLocation.getLatitude(),
					currentLocation.getLongitude(), 0 };
			double[] enu = LocationUtils.GEOtoENU(current, geoloc);

			Color c;
			boolean unique;
			do {
				unique = true;
				c = new Color((byte) (256 * Math.random()),
						(byte) (256 * Math.random()),
						(byte) (256 * Math.random()));
				for (int i = 0; i < pickingColors.size(); i++) {
					if (pickingColors.get(i).equals(c)) {
						unique = false;
					}
				}

			} while (!unique);
			pickingColors.add(c);
			POI.setPickingColor(c);
			POI.setTranslationMatrix((float) enu[0], (float) enu[1],
					(float) enu[2]);
			POI.caclulateRadius();
			BrowsingMainActivity.this.handler.post(new Runnable() {

				@Override
				public void run() {
					mapNative.addPOI(geoloc[0], geoloc[1]);
				}
			});

			if (POI.getRadius() < Settings.INNER_RADIUS_RANGE) {
				float newX;
				float newY;
				float newZ;
				newX = (float) (enu[0] * Settings.INNER_RADIUS_RANGE / POI
						.getRadius());
				newY = (float) (enu[1] * Settings.INNER_RADIUS_RANGE / POI
						.getRadius());
				newZ = (float) (enu[2] * Settings.INNER_RADIUS_RANGE / POI
						.getRadius());
				POI.setTranslationMatrix(newX, newY, newZ);
			}

			POI.setOnclickListener(new OnShapeClickListener() {

				@Override
				public void click(final Shape shape, Vector3 intersection) {

					poiInfoPanel.FetchPOI((POI) shape);

				}

				@Override
				public void click(Shape shape) {
					poiInfoPanel.FetchPOI((POI) shape);
				}
			});

			renderer.geoMode.addShape(POI);
			radar.addPOI(POI);
			if (parent != null) {
				parent.getPOIHolder().childrenLst.add(POI);
			}
		} catch (JSONException e) {

			e.printStackTrace();
		}

		return level;
	}

	@Override
	public void onNetworkStateChanged(boolean state) {
		if(loading != null){
		if (state) {
			if (loading.tryAcquire()) {
				loadPOIs();
			}
		}
		}else{
			finish();
			Toast.makeText(this,"Bad Internet Connectivity Please Try Again Later !!",Toast.LENGTH_LONG).show();
		}

	}

	public void checkFilter() {
		renderer.geoMode.getToBeDrawed().clear();
		for (Shape shape : renderer.geoMode.getShapes()) {
			if (!(Filter.filtered
					.contains(((POI) shape).getPOIHolder().category) || Filter.filtered
					.contains(((POI) shape).getPOIHolder().source))) {
				renderer.geoMode.getToBeDrawed().add(shape);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.layout.outdoor_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_refresh:
			mapNative.removeAllPOI();
		
			if (loading.tryAcquire()) {
				loadPOIs();
			}
			
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		if (config.orientation == config.ORIENTATION_PORTRAIT) {
			renderer.stopRendering = true;
		} else {
			renderer.stopRendering = false;
		}
		super.onConfigurationChanged(config);
	}

}
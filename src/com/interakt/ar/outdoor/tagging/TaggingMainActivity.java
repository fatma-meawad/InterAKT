package com.interakt.ar.outdoor.tagging;

import java.io.FileWriter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.Time;
import android.util.TypedValue;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.interakt.ar.Settings;
import com.interakt.ar.android.R;
import com.interakt.ar.android.sensors.LocationBased;
import com.interakt.ar.android.ui.MapNative;
import com.interakt.ar.android.ui.StatusBar;
import com.interakt.ar.graphics.poi.POI;
import com.interakt.ar.graphics.poi.POIHolder;
import com.interakt.ar.graphics.poi.POITextureGenerator;
import com.interakt.ar.listeners.LocationBasedListener;
import com.interakt.ar.outdoor.AndroidDeviceCameraController;
import com.interakt.ar.outdoor.MainActivity;
import com.interakt.ar.outdoor.browsing.BrowsingMainActivity;
import com.interakt.ar.outdoor.browsing.BrowsingPOIInfoPanel;
import com.interakt.ar.util.LocationUtils;
import com.interakt.ar.util.Utility;

public class TaggingMainActivity extends MainActivity implements
		LocationBasedListener {

	private BrowsingPOIInfoPanel poiInfoPanel;
	private Button tagHere;
	private TextView distance;
	private POI toBefixedPOI;
	private Location currentLocation;
	private TaggingRenderer renderer;
	boolean alive = true;
	int back;
	private View view;
	private AndroidDeviceCameraController cameraControl;
	private SharedPreferences mSharedPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSharedPreferences = getApplicationContext().getSharedPreferences(
				"com.mesai.nativecamra", 0);
		overridePendingTransition(0, 0);
		Intent i = getIntent();
		com.interakt.ar.Settings.ACTIVE_MODE = i.getIntExtra(
				"MODE_CHOSEN", 0);
		back = i.getIntExtra("JUST_BACK", 0);
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

		view = initializeForView(renderer = new TaggingRenderer(this,
				cameraControl), cfg);

		if (graphics.getView() instanceof SurfaceView) {
			SurfaceView glView = (SurfaceView) graphics.getView();
			// force alpha channel - I'm not sure we need this as the GL surface
			// is already using alpha channel
			glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		}
		// we don't want the screen to turn off during the long image saving
		// process
		graphics.getView().setKeepScreenOn(true);
		// keep the original screen size
		origWidth = graphics.getWidth();
		origHeight = graphics.getHeight();
		this.setContentView(R.layout.mainactivity_outdoor);
		main = (FrameLayout) findViewById(R.id.main_layout);
		mapNative = new MapNative(this);
		mapNative.setZoomLevel(17);
		mapNative.loadMap();
		main.addView(mapNative);
		Resources r = getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40,
				r.getDisplayMetrics());
		tagHere = new Button(this);
		tagHere.setBackgroundResource(R.drawable.button);
		tagHere.setTextColor(0xffff9900);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, (int) px);
		params.alignWithParent = true;
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		tagHere.setText("Tag Here");
		tagHere.setId(5);
		tagHere.setLayoutParams(params);
		tagHere.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// getScreenshot when tagging
				Utility.getScreenShot(main);
				Time now = new Time();
				now.setToNow();
				Settings.TobeLogged+= now.toString()+"\tTag Here is pressed\n";
				if (toBefixedPOI != null) {
					double[] coordinates = toBefixedPOI.getCoordinatesGEO();
					finish();
					Intent i = new Intent(TaggingMainActivity.this,
							TaggingDescriptionActivity.class);
					i.putExtra("USER_LAT_LONG",
							new double[] { currentLocation.getLatitude(),
									currentLocation.getLongitude() });
					i.putExtra("POI_LAT_LONG", new double[] { coordinates[0],
							coordinates[1] });
					startActivity(i);
				}

			}
		});
		distance = new TextView(this);
		distance.setBackgroundResource(R.drawable.statusitemframe);
		distance.setTextColor(0xffff9900);
		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params2.alignWithParent = true;
		params2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		params2.addRule(RelativeLayout.CENTER_HORIZONTAL);
		distance.setText("6.0 m");
		distance.setLayoutParams(params2);
		poiInfoPanel = new BrowsingPOIInfoPanel(this);
		generator = new POITextureGenerator(this);
		mainRelative = (RelativeLayout) findViewById(R.id.main_realtive);
		status = new StatusBar(this);
		RelativeLayout.LayoutParams paramsS = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		paramsS.alignWithParent = true;
		paramsS.addRule(RelativeLayout.LEFT_OF, tagHere.getId());
		paramsS.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		status.setLayoutParams(paramsS);
		mainRelative.addView(status);
		mainRelative.addView(tagHere);
		mainRelative.addView(distance);
		mainProgressBar = new com.interakt.ar.android.ui.ProgressBar(this);
		main.addView(view, 0);
		currentLocation = LocationBased.getLastKnowLocation();
		Thread t = new Thread() {
			public void run() {
				while (alive) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					handler.post(new Runnable() {

						@Override
						public void run() {
							if (toBefixedPOI != null) {
								updateDepth();
							}

						}
					});
				}
			}
		};
		t.start();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		status = null;
		poiInfoPanel = null;
		generator = null;
		view = null;
		mainProgressBar = null;
		currentLocation = null;
		distance = null;
		tagHere = null;
		cameraControl = null;
		mapNative.dispose();
		mapNative = null;
		main.removeAllViews();
		main = null;
		mainRelative.removeAllViews();
		mainRelative = null;
		renderer = null;
		toBefixedPOI = null;
	}

	public MapNative getmapNative() {
		return mapNative;
	}

	public POI getToBeFixed() {
		return toBefixedPOI;
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
			mainRelative.removeView(mainProgressBar);
		} else {
			mainRelative.addView(mainProgressBar);
		}
	}

	public void togglemapNative(boolean toggle) {
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
		togglemapNative(toggle);
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
	protected void onPause() {
		super.onPause();
		LocationBased.removeLocationListener(this);
		mapNative.removeLocationListener();
		new SubmitLogAsyncTask().execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		LocationBased.addLocationListener(this);
		mapNative.addLocationListener();
	}

	public void addToBeFixed() {
		try {
			renderer.canRender.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		POIHolder holder = new POIHolder();
		holder.texture = "models/fartexture.png";
		holder.textureLock = "models/fartexture.png";
		toBefixedPOI = new POI(this, true);
		toBefixedPOI.setHolder(holder);
		toBefixedPOI.setLock(true);
		toBefixedPOI.setTranslationMatrix(6, 0, 0);
		toBefixedPOI.caclulateRadius();
		renderer.taggingMode.addShape(toBefixedPOI);
		mapNative.addToBeFixedPOI(0.0, 0.0);
		renderer.canRender.release();

	}

	@Override
	public void onBackPressed() {
		Time now = new Time();
		now.setToNow();
		Settings.TobeLogged+= now.toString()+"\tBack is pressed\n";
		finish();
		if (back == 0) {
			startActivity(new Intent(com.interakt.ar.outdoor.tagging.TaggingMainActivity.this,
					BrowsingMainActivity.class));
		}

	}

	public void updateDepth() {
		distance.setText(String.format("%.1f", ((POI) toBefixedPOI).getRadius())
				+ " m");
		double[] coordinatesGEO = LocationUtils.ENUtoGEO(new double[] {
				currentLocation.getLatitude(), currentLocation.getLongitude(),
				0 }, toBefixedPOI.getPosition());
		((POI) toBefixedPOI).getPOIHolder().latitude = coordinatesGEO[0] + "";
		((POI) toBefixedPOI).getPOIHolder().longitude = coordinatesGEO[1] + "";
		mapNative.updateToBeFixedPOI(
				((POI) toBefixedPOI).getCoordinatesGEO()[0],
				((POI) toBefixedPOI).getCoordinatesGEO()[1]);
	}

	@Override
	public void onLocationChanged(final Location location) {
		if (location.getAccuracy() < 50) {

			status.checkAndUpdate(true, location.getAccuracy());

		} else {

			status.checkAndUpdate(false, location.getAccuracy());

		}
		currentLocation = location;

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
				Settings.TobeLogged ="";
			}

			return null;
		}

	}

}
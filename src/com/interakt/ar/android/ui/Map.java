package com.interakt.ar.android.ui;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.interakt.ar.android.R;
import com.interakt.ar.android.sensors.LocationBased;
import com.interakt.ar.listeners.LocationBasedListener;

public class Map extends LinearLayout implements LocationBasedListener {
	WebView map;
	FrameLayout holder;
	boolean enlarge;
	Location location;
	double[] toBeFixed;
	ArrayList<double[]> pois = new ArrayList<double[]>();

	public Map(Context context) {
		super(context);
		enlarge = false;
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.map, this);
		map = (WebView) this.findViewById(R.id.map_webview);
		holder = (FrameLayout) this.findViewById(R.id.map_holder);
		map.getSettings().setJavaScriptEnabled(true);

		LinearLayout clicker = (LinearLayout) this
				.findViewById(R.id.map_clicker);
		clicker.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!enlarge) {
					Resources r = getResources();
					int px = (int) TypedValue.applyDimension(
							TypedValue.COMPLEX_UNIT_DIP, 300,
							r.getDisplayMetrics());
					int mr = (int) TypedValue.applyDimension(
							TypedValue.COMPLEX_UNIT_DIP, 10,
							r.getDisplayMetrics());
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							px, px);
					params.bottomMargin = mr;
					params.topMargin = mr;
					params.rightMargin = mr;
					params.leftMargin = mr;
					holder.scrollTo(0, 0);
					holder.setLayoutParams(params);
					enlarge = true;
				} else {
					Resources r = getResources();
					int px = (int) TypedValue.applyDimension(
							TypedValue.COMPLEX_UNIT_DIP, 100,
							r.getDisplayMetrics());
					int mr = (int) TypedValue.applyDimension(
							TypedValue.COMPLEX_UNIT_DIP, 10,
							r.getDisplayMetrics());
					int scr = (int) TypedValue.applyDimension(
							TypedValue.COMPLEX_UNIT_DIP, 110,
							r.getDisplayMetrics());
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							px, px);
					params.bottomMargin = mr;
					params.topMargin = mr;
					params.rightMargin = mr;
					params.leftMargin = mr;
					holder.scrollTo(scr, scr);
					holder.setLayoutParams(params);
					enlarge = false;
				}
			}
		});
	}

	public Map(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public WebView getMapVebView() {
		return map;

	}

	public void loadMap() {
		map.clearCache(true);
		map.clearView();
		map.reload();
		map.loadUrl("about:blank");
		map.loadUrl("file:///android_asset/map/map.html");

	}

	public void changeLocation(final double latitute, final double longitute) {

		map.loadUrl("javascript:changeLocation(" + latitute + "," + longitute
				+ ")");

	}

	public void addPOI(final double latitute, final double longitute) {
		pois.add(new double[] { latitute, longitute });
		map.loadUrl("javascript:addPOI(" + latitute + "," + longitute + ")");
	}

	public void hideALLPOI() {

		map.loadUrl("javascript:removeAllPOI()");
	}

	public void removeAllPOI() {
		pois.clear();
		map.loadUrl("javascript:removeAllPOI()");
	}

	public void showAllPOIs() {
		for (double[] poi : pois) {
			map.loadUrl("javascript:addPOI(" + poi[0] + "," + poi[1] + ")");
		}
	}

	public void addToBeFixedPOI(final double latitute, final double longitute) {
		toBeFixed = new double[] { latitute, longitute };
		map.loadUrl("javascript:addToBeFixedPOI(" + latitute + "," + longitute
				+ ")");
	}

	public void updateToBeFixedPOI(final double latitute, final double longitute) {
		map.loadUrl("javascript:updateToBeFixedPOI(" + latitute + ","
				+ longitute + ")");
	}

	public void update() {
		Location location = LocationBased.getLastKnowLocation();
		if (location != null)
			changeLocation(location.getLatitude(), location.getLongitude());
	}

	// public void update(Location location) {
	// changeLocation(location.getLatitude(), location.getLongitude());
	// }

	@Override
	public void onLocationChanged(final Location location) {
		this.location = location;
		map.post(new Runnable() {

			@Override
			public void run() {
				changeLocation(location.getLatitude(), location.getLongitude());
			}
		});

	}

	public void addTobeFixed() {
		pois.add(toBeFixed);

	}

	public void addLocationListener() {
		LocationBased.addLocationListener(this);
	}

	public void removeLocationListener() {
		LocationBased.removeLocationListener(this);
	}

	public void dispose() {
		removeLocationListener();
		holder.removeAllViews();
		map.clearHistory();
		map.clearCache(true);
		map.clearView();
		map.freeMemory(); // new code
		map.pauseTimers(); // new code
		map = null;
		pois.clear();
		pois = null;
		location = null;
		toBeFixed = null;
	}

}

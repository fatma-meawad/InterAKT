package com.interakt.ar.android.ui;


import java.util.ArrayList;

import org.w3c.dom.Document;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.interakt.ar.android.R;
import com.interakt.ar.android.sensors.LocationBased;
import com.interakt.ar.listeners.LocationBasedListener;
import com.interakt.ar.util.Utility;

public class MapNative extends LinearLayout implements LocationBasedListener {
	GoogleMap map;
	FrameLayout holder;
	boolean enlarge;
	Location location;
	Marker toBeFixed;
	ArrayList<Marker> pois = new ArrayList<Marker>();
	private Context context;
	private GMapV2Direction md;
	private Polyline polylin;
	private int zoom;

	public MapNative(Context context) {
		super(context);
		this.context = context;
		enlarge = false;
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.map_native, this);

		holder = (FrameLayout) this.findViewById(R.id.map_holder);
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
				
				Utility.addLog("cliked on Map");
			}
			
		});
		md = new GMapV2Direction();
	}

	public MapNative(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public GoogleMap getMapVebView() {
		return map;

	}

	public void loadMap() {
		map = ((MapFragment) (((com.interakt.ar.outdoor.MainActivity) context)
				.getFragmentManager()).findFragmentById(R.id.map_native_map))
				.getMap();
		map.setMyLocationEnabled(true);
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
	}

	public void addPOI(final double latitute, final double longitude) {
		pois.add(map.addMarker(new MarkerOptions()
				.position(new LatLng(latitute, longitude)).title("POI")
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.poimap))));

	}

	public void hideALLPOI() {
		for (Marker poi : pois) {
			poi.setVisible(false);
		}

	}

	public void removeAllPOI() {
		for (Marker poi : pois) {
			poi.remove();
		}
		pois.clear();
	}

	public void showAllPOIs() {
		for (Marker poi : pois) {
			poi.setVisible(true);
		}

	}

	public void addToBeFixedPOI(final double latitute, final double longitude) {
		toBeFixed = map.addMarker(new MarkerOptions().position(
				new LatLng(latitute, longitude)).title("To be Fixed"));
	}

	public void updateToBeFixedPOI(final double latitute, final double longitude) {
		toBeFixed.setPosition(new LatLng(latitute, longitude));
	}

	// public void update(Location location) {
	// changeLocation(location.getLatitude(), location.getLongitude());
	// }

	@Override
	public void onLocationChanged(final Location location) {
		this.location = location;
		LatLng latlong = new LatLng(location.getLatitude(),
				location.getLongitude());
		CameraUpdate center = CameraUpdateFactory.newLatLngZoom(latlong, zoom);
		map.animateCamera(center);

	}
	
	public void setZoomLevel(int zoom){
		this.zoom = zoom;
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

	public void addRoute(LatLng sourcePosition, LatLng destPosition) {

		Document doc = md.getDocument(sourcePosition, destPosition,
				GMapV2Direction.MODE_DRIVING);

		ArrayList<LatLng> directionPoint = md.getDirection(doc);
		PolylineOptions rectLine = new PolylineOptions().width(3).color(
				Color.RED);

		for (int i = 0; i < directionPoint.size(); i++) {
			rectLine.add(directionPoint.get(i));
		}
		polylin = map.addPolyline(rectLine);
		
	}

	public void dispose() {
		removeLocationListener();
		holder.removeAllViews();
		map.clear();
		map = null;
		pois.clear();
		pois = null;
		location = null;
		toBeFixed = null;
	}

}

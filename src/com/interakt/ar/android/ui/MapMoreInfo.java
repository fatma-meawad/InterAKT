package com.interakt.ar.android.ui;


import java.util.ArrayList;

import org.w3c.dom.Document;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
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

public class MapMoreInfo extends LinearLayout implements LocationBasedListener {
	GoogleMap map;
	FrameLayout holder;
	boolean enlarge;
	Location location;
	Marker toBeFixed;
	ArrayList<Marker> pois = new ArrayList<Marker>();
	private Context context;
	private GMapV2Direction md;
	private Polyline polylin;

	public MapMoreInfo(Context context) {
		super(context);
		this.context = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.poi_info_fliper_map, this);
		md = new GMapV2Direction();
	}

	public MapMoreInfo(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public GoogleMap getMapVebView() {
		return map;

	}

	public void loadMap() {
		map = ((MapFragment) (((com.interakt.ar.outdoor.MainActivity) context)
				.getFragmentManager()).findFragmentById(R.id.poi_info_fliper_map_map))
				.getMap();
		map.setMyLocationEnabled(true);
		map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
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
		CameraUpdate center = CameraUpdateFactory.newLatLngZoom(latlong, 17);
		if(map !=null){
		map.animateCamera(center);
		}
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

	public void addRoute(LatLng srcPosition,LatLng destPosition) {

		GetRouteDocument getRouteDocument = new GetRouteDocument();
		getRouteDocument.execute(srcPosition,destPosition);
		
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
	
	private class GetRouteDocument extends AsyncTask<Object, Void, Object> {

		@Override
		protected Document doInBackground(Object... params) {
			if (polylin !=null){
				polylin.remove();
				polylin = null;
			}
			return md.getDocument((LatLng)params[0],(LatLng) params[1],
					GMapV2Direction.MODE_WALKING);
		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();

		}

		@Override
		protected void onPostExecute(Object result) {

			super.onPostExecute(result);
			
			ArrayList<LatLng> directionPoint = md.getDirection((Document)result);
			PolylineOptions rectLine = new PolylineOptions().width(6).color(
					0xff0060ff);

			for (int i = 0; i < directionPoint.size(); i++) {
				rectLine.add(directionPoint.get(i));
			}
			polylin = map.addPolyline(rectLine);
		}

	}

}

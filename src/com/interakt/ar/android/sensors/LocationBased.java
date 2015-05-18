package com.interakt.ar.android.sensors;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.interakt.ar.listeners.LocationBasedListener;

public class LocationBased extends Service implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, LocationListener {

	static GoogleApiClient mLocationClient;

	// Milliseconds per second
	private static final int MILLISECONDS_PER_SECOND = 1000;
	// Update frequency in seconds
	public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
	// Update frequency in milliseconds
	private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND
			* UPDATE_INTERVAL_IN_SECONDS;
	// The fastest update frequency, in seconds
	private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
	// A fast frequency ceiling in milliseconds
	private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND
			* FASTEST_INTERVAL_IN_SECONDS;

	// Define an object that holds accuracy and frequency parameters
	static LocationRequest mLocationRequest;

	private static ArrayList<LocationBasedListener> listeners = new ArrayList<LocationBasedListener>();

	private static Semaphore wait = new Semaphore(1);

	public static boolean ignoreCounting;

	public static boolean serviceAlive = true;

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		System.out.print("FAILED");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("SERVICE online !!!");
		// try {
		serviceAlive = true;
		System.out.println("LOCATIONSERVICE-Start-Command");
		// wait.acquire();
		System.out.println("LOCATIONSERVICE-Start-Command");
		mLocationClient = new GoogleApiClient.Builder(this)
				.addApi(LocationServices.API).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).build();
		mLocationClient.connect();
		System.out.println("LOCATIONSERVICE-Connected");
		mLocationRequest = LocationRequest.create();
		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// Set the update interval to 5 seconds
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		// Set the fastest update interval to 1 second
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

		Thread checkWhoOnline = new Thread() {
			int count = 0;
			boolean iterate = true;

			public void run() {
				while (iterate) {
					if (mLocationClient.isConnected()) {
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println(">>>>>>>>>" + ignoreCounting);
						KeyguardManager kgMgr = (KeyguardManager) getSystemService(LocationBased.this.KEYGUARD_SERVICE);
						boolean showing = kgMgr.inKeyguardRestrictedInputMode();
						System.out.println(">>>>>>>>>" + showing);
						if (!showing) {
							if (!ignoreCounting) {
								System.out.println(">>>>>>>>>"
										+ listeners.size());
								if (listeners.size() == 0) {
									count++;
									if (count == 3) {
										iterate = false;
//										iterate = true;
//										count = 0;

										 if (mLocationClient.isConnected()) {
										 mLocationClient.unregisterConnectionCallbacks(LocationBased.this);
										 mLocationClient.unregisterConnectionFailedListener(LocationBased.this);
										 mLocationClient.disconnect();
										 System.out.println("STOPPING");
										 stopSelf();
										 }

									}
								} else {
									count = 0;
								}
							} else {
								count = 0;
							}
						} else {
							if (mLocationClient.isConnected()) {
								mLocationClient
										.unregisterConnectionCallbacks(LocationBased.this);
								mLocationClient.unregisterConnectionFailedListener(LocationBased.this);
								mLocationClient.disconnect();
								System.out.println("DISCONECTING - HERE");
								System.out.println("STOPPING");
								stopSelf();
							}
						}
					}
				}
			};
		};
		checkWhoOnline.start();

		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onConnected(Bundle connectionHint) {

		LocationServices.FusedLocationApi.requestLocationUpdates(
                mLocationClient, mLocationRequest, this);

	}

	public static Location getLastKnowLocation() {
		Location l = null;
		do {
			if (mLocationClient != null && mLocationClient.isConnected()) {
				l = LocationServices.FusedLocationApi.getLastLocation(mLocationClient);
			}
		} while (l == null);

		return l;

	}

	public static void addLocationListener(LocationBasedListener listener) {
		try {
			wait.acquire();
			listeners.add(listener);
			wait.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public static void removeLocationListener(LocationBasedListener listener) {
		try {
			wait.acquire();
			listeners.remove(listener);
			wait.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onLocationChanged(Location location) {
		System.out.println(location);
		try {
			wait.acquire();
			for (int i = 0; i < listeners.size(); i++) {
				listeners.get(i).onLocationChanged(location);
			}
			wait.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onDestroy() {
		serviceAlive = false;
		if (mLocationClient.isConnected()) {
			mLocationClient.disconnect();
		}
		System.out.println("LOCATIONSERVICE-Diconnected");
		super.onDestroy();
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}

}

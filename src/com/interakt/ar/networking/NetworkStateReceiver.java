package com.interakt.ar.networking;

import java.util.ArrayList;

import com.interakt.ar.listeners.NetworkStateListener;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStateReceiver extends BroadcastReceiver {
	static ArrayList<NetworkStateListener> listeners = new ArrayList<NetworkStateListener>();

	public void onReceive(Context context, Intent intent) {
		// super.onReceive(context, intent);
		
		if (intent.getExtras() != null) {
			NetworkInfo ni = (NetworkInfo) intent.getExtras().get(
					ConnectivityManager.EXTRA_NETWORK_INFO);
			if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
				for(NetworkStateListener listener : listeners){
					listener.onNetworkStateChanged(true);
				}
			}
		}
		if (intent.getExtras().getBoolean(
				ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
			for(NetworkStateListener listener : listeners){
				listener.onNetworkStateChanged(false);
			}
		}
	}
	
	public static void addListener(NetworkStateListener listener){
		listeners.add(listener);
	}
}
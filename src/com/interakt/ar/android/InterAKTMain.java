package com.interakt.ar.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.interakt.ar.android.sensors.LocationBased;
import com.interakt.ar.networking.ConnectionDetector;
import com.interakt.ar.networking.ServerAPI;

public class InterAKTMain extends Activity {

	//String user_id = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.interaktmain);
		final ViewAnimator viewFlipper = (ViewAnimator) findViewById(R.id.interaktmain_flipper);
		TextView logout = (TextView) findViewById(R.id.interaktmain_logout);
		logout.setOnClickListener(new OnClickListener() {

			private SharedPreferences mSharedPreferences;

			@Override
			public void onClick(View v) {
				mSharedPreferences = getApplicationContext()
						.getSharedPreferences("com.mesai.nativecamra", 0);
				Editor e = mSharedPreferences.edit();
				e.putString("USERNAME", "");
				e.putString("PASSWORD", "");
				e.putBoolean("ALREADYLOGGEDIN", false);
				e.commit();
				startActivity(new Intent(InterAKTMain.this, LoginActivity.class));

			}
		});
		Intent i = new Intent(this, LocationBased.class);
		startService(i);
		LocationBased.ignoreCounting = true;
		new GetCategorySourcesAsyncTask().execute();
	}

	@Override
	protected void onPause() {
		LocationBased.ignoreCounting = false;
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		LocationBased.ignoreCounting = false;
		super.onPause();
	}

	@Override
	protected void onResume() {
		if(!LocationBased.serviceAlive){
		Intent i = new Intent(this,LocationBased.class);
		startService(i);
		}
		LocationBased.ignoreCounting = true;
		super.onResume();

		// LocationManager man = (LocationManager) getApplicationContext()
		// .getSystemService(Context.LOCATION_SERVICE);
		// if (!man.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
		// buildAlertMessageNoGps();
		// }
	}

	public void goToBrowsingOutdoor(View v) {
		ConnectionDetector cd = new ConnectionDetector(this);
		if (!cd.isConnectingToInternet()) {
			Toast.makeText(this, "No Internet Connection !", Toast.LENGTH_LONG)
					.show();
			return;
		}
		Intent i = new Intent(this, com.interakt.ar.outdoor.browsing.BrowsingMainActivity.class);
		i.putExtra("MODE_CHOSEN", 1);
		startActivity(i);
	}

	public void addPoi(View v) {
		ConnectionDetector cd = new ConnectionDetector(this);
		if (!cd.isConnectingToInternet()) {
			Toast.makeText(this, "No Internet Connection !", Toast.LENGTH_LONG)
					.show();
			return;
		}
		Intent i = new Intent(this, com.interakt.ar.outdoor.tagging.TaggingMainActivity.class);
		i.putExtra("MODE_CHOSEN", 1);
		i.putExtra("JUST_BACK", 1);
		startActivity(i);
	}

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"GPS seems to be disabled, do you want to enable it?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(
									@SuppressWarnings("unused") final DialogInterface dialog,
									@SuppressWarnings("unused") final int id) {
								startActivity(new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog,
							@SuppressWarnings("unused") final int id) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	private class GetCategorySourcesAsyncTask extends
			AsyncTask<Object, Void, Object> {

		private SharedPreferences mSharedPreferences;

		@Override
		protected Object doInBackground(Object... params) {
			mSharedPreferences = getApplicationContext()
					.getSharedPreferences("com.mesai.nativecamra", 0);
			ServerAPI.getCategories(mSharedPreferences.getString("USERNAME", ""));
			ServerAPI.LoggdIn(mSharedPreferences.getString("USERNAME", ""));
			return null;
		}
	}

}

package com.interakt.ar.android.ui;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.interakt.ar.Settings;
import com.interakt.ar.android.R;
import com.interakt.ar.util.FilterItemHolder;
import com.interakt.ar.util.Utility;

public class StatusBar extends LinearLayout {

	ImageView internetStatus;
	ImageView gpsStatus;
	Context context;

	boolean internetConnected;
	public boolean gpsConnected;
	double accuracy;
	PopupWindow last;
	// QuickAction last;
	private ImageView filtering;
	private String radius;
	// private ImageView layers;
	private Filters filter;
	private boolean showFilter;
	private ImageView switchMode;
	private int activeMode;

	public StatusBar(final Context context) {
		super(context);
		this.context = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.status_bar, this);
		internetStatus = (ImageView) findViewById(R.id.status_bar_internet_indicator);
		gpsStatus = (ImageView) findViewById(R.id.status_bar_gps_indicator);
		filtering = (ImageView) findViewById(R.id.status_bar_radius_indicator);
		// layers = (ImageView) findViewById(R.id.status_bar_layer);
		//switchMode = (ImageView) findViewById(R.id.status_bar_switch);

		filter = new Filters(context);

		internetStatus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (last != null)
					last.dismiss();
				last = displayPopup(v, 0);
				
				Utility.addLog("cliked on Internet Status");

			}
		});
		gpsStatus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (last != null)
					last.dismiss();
				last = displayPopup(v, 1);
				
				Utility.addLog("cliked on GPS Status");
			}
		});
		filtering.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (activeMode == 0) {
					if (showFilter) {
						if (last != null)
							last.dismiss();
						last = displayPopup(v, 2);
					} else {
						if (last != null)
							last.dismiss();
						last = displayPopup(v, 4);
					}
				} else {
					if (last != null)
						last.dismiss();
					last = displayPopup(v, 2);
				}
				
				Utility.addLog("cliked on Filtering Status");
			}
		});
		// layers.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// if (last != null)
		// last.dismiss();
		// last = displayPopup(v, 3);
		//
		// }
		// });


	}

	public void checkAndUpdate(boolean gpsProvider, double acc) {
		if (isNetworkAvailable()) {
			internetConnected = true;
			internetStatus.setImageResource(R.drawable.internet_connected);
		} else {
			internetConnected = false;
			internetStatus.setImageResource(R.drawable.internet_notconnected);
		}
		if (gpsProvider) {
			gpsStatus.setImageResource(R.drawable.gps_connected);
		} else {
			gpsStatus.setImageResource(R.drawable.gps_notconnected);
		}
		gpsConnected = gpsProvider;
		accuracy = acc;
	}

	public boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

	public PopupWindow displayPopup(View view, int type)

	{
		final LinearLayout l = new LinearLayout(context);

		l.setOrientation(LinearLayout.VERTICAL);
		LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		l.setLayoutParams(lparams);
		ImageView iv = new ImageView(context);
		iv.setImageResource(R.drawable.anchor);
		Resources r = getResources();
		int wpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				20, r.getDisplayMetrics());
		int hpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				9, r.getDisplayMetrics());
		int mpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				5, r.getDisplayMetrics());
		LinearLayout.LayoutParams ivparams = new LinearLayout.LayoutParams(wpx,
				hpx);
		iv.setLayoutParams(ivparams);
		iv.setScaleType(ScaleType.FIT_XY);
		View out = null;
		switch (type) {
		case 0: {
			if (internetConnected) {

				out = new TextView(context);
				((TextView) out).setText("Internet Connected");
			} else {
				out = new TextView(context);
				((TextView) out).setText("Internet Not Connected");
			}
			LinearLayout.LayoutParams outparams = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			out.setLayoutParams(outparams);
			;
			break;
		}
		case 1: {
			if (gpsConnected) {
				out = new TextView(context);
				((TextView) out).setText("GPS Connected With " + accuracy
						+ " m");
			} else {
				out = new TextView(context);
				((TextView) out)
						.setText("GPS Not Connected\nWorking on Network with "
								+ accuracy + " m");
			}
			LinearLayout.LayoutParams outparams = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			out.setLayoutParams(outparams);
		}
			;
			break;
		case 2: {

			if (activeMode == 0) {
				out = filter;
				LinearLayout.LayoutParams outparams = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				out.setLayoutParams(outparams);
			} else {
				out = new TextView(context);
				((TextView) out).setText("POI at " + radius + " m");
				LinearLayout.LayoutParams outparams = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				out.setLayoutParams(outparams);
			}
		}
			;
			break;
		case 3: {
			out = new LinearLayout(context);
			((LinearLayout) out).setOrientation(LinearLayout.HORIZONTAL);
			LinearLayout.LayoutParams outparams = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			out.setLayoutParams(outparams);
			final TextView tv = new TextView(context);
			tv.setText(Settings.RADIUS_RANGE_PROGRESS + "-"
					+ Settings.LAYER_NAME_MAX);
			tv.setGravity(Gravity.CENTER);
			tv.setPadding(mpx, mpx, mpx, mpx);
			SeekBar outs = new SeekBar(context);
			((SeekBar) outs).setMax((int) Settings.RADIUS_RANGE);
			outs.setProgress((int) Settings.RADIUS_RANGE_PROGRESS);
			outs.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onStartTrackingTouch(SeekBar arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onProgressChanged(SeekBar arg0, int arg1,
						boolean arg2) {
					// int factor = (int) (4 * Settings.RADIUS_RANGE / 250);
					// double portion = (Settings.RADIUS_RANGE / factor);
					// double range = arg1 * portion;
					tv.setText(arg1 + "-" + Settings.RADIUS_RANGE);
					Settings.LAYER_NAME_MIN = arg1;
					Settings.RADIUS_RANGE_PROGRESS = arg1;

				}
			});
			int swpx = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 100, r.getDisplayMetrics());
			int shpx = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 30, r.getDisplayMetrics());
			LinearLayout.LayoutParams outsparams = new LinearLayout.LayoutParams(
					swpx, shpx);
			outs.setLayoutParams(outsparams);
			((LinearLayout) out).addView(outs);
			((LinearLayout) out).addView(tv);

		}
			;
			break;
		case 4: {

			out = new TextView(context);
			((TextView) out).setText("Please wait...");
			LinearLayout.LayoutParams outparams = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			out.setLayoutParams(outparams);
		}
			;
			break;
		}
		out.setPadding(mpx, mpx, mpx, mpx);
		out.setBackgroundResource(R.drawable.statusitemframe);
		l.addView(out);
		l.addView(iv);

		final PopupWindow popUp = new PopupWindow(l, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		popUp.setOutsideTouchable(true);
		popUp.setBackgroundDrawable(new BitmapDrawable());
		popUp.showAsDropDown(view, 0, view.getHeight());
		if (activeMode == 0) {
			popUp.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					l.removeAllViews();

				}
			});
			if (type != 3 && type != 2) {
				Thread timer = new Thread() {
					public void run() {
						try {
							sleep(5000);
						} catch (Exception e) {

						} finally {
							if (context instanceof com.interakt.ar.outdoor.browsing.BrowsingMainActivity)
								((com.interakt.ar.outdoor.browsing.BrowsingMainActivity) context).handler
										.post(new Runnable() {

											@Override
											public void run() {

												popUp.dismiss();
											}
										});
							else
								((com.interakt.ar.outdoor.tagging.TaggingMainActivity) context).handler
										.post(new Runnable() {

											@Override
											public void run() {

												popUp.dismiss();
											}
										});
						}
					}
				};
				timer.start();
			}
		}
		return popUp;
	}

	public void setRadius(String radius) {

		this.radius = radius;

	}

	// public void changeMode(int mode) {
	// if (mode == 1) {
	// this.filtering.setImageResource(R.drawable.distance);
	// this.layers.setImageResource(R.drawable.diszoom);
	// layers.setClickable(false);
	// // layers.performClick();
	// activeMode = mode;
	// } else {
	// this.filtering.setImageResource(R.drawable.filter);
	// this.layers.setImageResource(R.drawable.zoom);
	// layers.setClickable(true);
	// activeMode = mode;
	// }
	// }

	public void setCategories(ArrayList<FilterItemHolder> categories) {
		filter.setCategories(categories);

	}

	public void setSources(ArrayList<FilterItemHolder> sources) {
		filter.setSources(sources);

	}

	public void setShowfilter(boolean show) {
		showFilter = show;
	}

	public void updating() {
		filter.updating();
		if (last != null) {
			if (last.isShowing()) {
				if (context instanceof com.interakt.ar.outdoor.browsing.BrowsingMainActivity)
					((com.interakt.ar.outdoor.browsing.BrowsingMainActivity) context).handler
							.post(new Runnable() {

								@Override
								public void run() {

									last.dismiss();
								}
							});
				else
					((com.interakt.ar.outdoor.tagging.TaggingMainActivity) context).handler
							.post(new Runnable() {

								@Override
								public void run() {

									last.dismiss();
								}
							});
			}
		}

	}

	public void dispose() {
		if (last != null) {
			if (last.isShowing()) {
				if (context instanceof com.interakt.ar.outdoor.browsing.BrowsingMainActivity)
					((com.interakt.ar.outdoor.browsing.BrowsingMainActivity) context).handler
							.post(new Runnable() {

								@Override
								public void run() {

									last.dismiss();
								}
							});
				else
					((com.interakt.ar.outdoor.tagging.TaggingMainActivity) context).handler
							.post(new Runnable() {

								@Override
								public void run() {

									last.dismiss();
								}
							});
			}
		}
	}

	public void setMinRadius(double minRadius) {
		Settings.RADIUS_RANGE_PROGRESS = (int) Math.round(minRadius
				- Settings.INNER_RADIUS_RANGE);
		Settings.LAYER_NAME_MIN = Settings.RADIUS_RANGE_PROGRESS + 3;

	}

	// public void showZoom() {
	// if (last != null)
	// ((outdoor.BrowsingMainActivity) context).handler.post(new Runnable() {
	//
	// @Override
	// public void run() {
	//
	// last.dismiss();
	// }
	// });
	// last = displayPopup(layers, 3);
	// }
}

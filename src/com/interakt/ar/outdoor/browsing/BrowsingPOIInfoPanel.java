package com.interakt.ar.outdoor.browsing;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.pretty.time.PrettyTime;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.model.LatLng;
import com.interakt.ar.android.R;
import com.interakt.ar.android.sensors.LocationBased;
import com.interakt.ar.android.ui.MapMoreInfo;
import com.interakt.ar.graphics.Shape;
import com.interakt.ar.graphics.poi.POI;
import com.interakt.ar.graphics.poi.POIHolder;
import com.interakt.ar.graphics.poi.POIHolder.CommentHolder;
import com.interakt.ar.networking.ConnectionDetector;
import com.interakt.ar.networking.ServerAPI;
import com.interakt.ar.networking.ServerAPI.ResponseHolder;

public class BrowsingPOIInfoPanel extends LinearLayout {

	public Context context;
	private Button flipper0;
	private Button flipper1;
	private ViewFlipper fliper;
	private TextView name;
	private TextView creator;
	private TextView category;
	private TextView rating;
	private TextView date;
	private TextView description;
	private TextView parent;
	private TextView totalNumberOfrates;
	private LayoutInflater inflater;
	private Button close;
	private boolean onScreen;
	private boolean busy;
	private RatingBar ratingBar;
	private Button flipper2;
	private TextView likes;
	private TextView like;
	private boolean wait = true;
	private TextView source;
	private SharedPreferences mSharedPreferences;
	private Button flipper3;
	MapMoreInfo map;
	ImageView thumbnail;
	public Bitmap bm;

	public BrowsingPOIInfoPanel(Context context) {
		super(context);
		this.context = context;
		this.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {

				return true;
			}

		});
		mSharedPreferences = context.getSharedPreferences(
				"com.mesai.nativecamra", 0);
		inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.poi_info, this);
		name = (TextView) this.findViewById(R.id.poi_info_name);
		creator = (TextView) this.findViewById(R.id.poi_info_creator);
		category = (TextView) this.findViewById(R.id.poi_info_category);
		source = (TextView) this.findViewById(R.id.poi_info_source);
		rating = (TextView) this.findViewById(R.id.poi_info_rating);
		likes = (TextView) this.findViewById(R.id.poi_info_likes);
		date = (TextView) this.findViewById(R.id.poi_info_created_at);
		description = (TextView) this.findViewById(R.id.poi_info_description);
		parent = (TextView) this.findViewById(R.id.poi_info_parent);
		thumbnail = (ImageView) this.findViewById(R.id.poi_info_thumbnail);
		totalNumberOfrates = (TextView) this
				.findViewById(R.id.poi_info_total_number_rates);
		ratingBar = (RatingBar) this.findViewById(R.id.poi_info_rating_bar);
		flipper0 = (Button) this.findViewById(R.id.poi_info_fipper_description);
		flipper1 = (Button) this.findViewById(R.id.poi_info_fipper_children);
		flipper2 = (Button) this.findViewById(R.id.poi_info_fipper_comments);
		flipper3 = (Button) this.findViewById(R.id.poi_info_fipper_map);
		like = (TextView) this.findViewById(R.id.poi_info_like);
		fliper = (ViewFlipper) this.findViewById(R.id.poi_info_flipper);
		// quick
		fliper.addView(new FrameLayout(context));
		fliper.addView(new FrameLayout(context));
		fliper.addView(new FrameLayout(context));
		map = new MapMoreInfo(context);
	}

	public void setData(final Shape shape) {

		ratingBar
				.setRating(Float.parseFloat((((POI) shape).getPOIHolder()).userRate));
		if (Float.parseFloat((((POI) shape).getPOIHolder()).userRate) > 0) {
			ratingBar.setEnabled(false);
		}

		if ((((POI) shape).getPOIHolder()).userLike) {
			like.setText("Unlike");
		} else {
			like.setText("Like");
		}

		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				if (fromUser) {
					new RatePOIAsynckTask().execute(shape, new Float(rating));
				}
			}
		});
		final PrettyTime p = new PrettyTime();
		final SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss");

		flipper0.setBackgroundResource(R.drawable.button_clicked_sqaure);
		flipper1.setBackgroundResource(R.drawable.button_square);
		flipper2.setBackgroundResource(R.drawable.button_square);

		flipper3.setBackgroundResource(R.drawable.button_square);
		((LinearLayout) flipper1.getParent()).getChildAt(1).setVisibility(
				INVISIBLE);
		flipper2.setBackgroundResource(R.drawable.button_square);
		((LinearLayout) flipper2.getParent()).getChildAt(1).setVisibility(
				INVISIBLE);
		flipper3.setBackgroundResource(R.drawable.button_square);
		((LinearLayout) flipper3.getParent()).getChildAt(1).setVisibility(
				INVISIBLE);
		flipper0.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				fliper.setDisplayedChild(0);
				v.setBackgroundResource(R.drawable.button_clicked_sqaure);
				((LinearLayout) v.getParent()).getChildAt(1).setVisibility(
						VISIBLE);
				flipper1.setBackgroundResource(R.drawable.button_square);
				((LinearLayout) flipper1.getParent()).getChildAt(1)
						.setVisibility(INVISIBLE);
				flipper2.setBackgroundResource(R.drawable.button_square);
				((LinearLayout) flipper2.getParent()).getChildAt(1)
						.setVisibility(INVISIBLE);
				flipper3.setBackgroundResource(R.drawable.button_square);
				((LinearLayout) flipper3.getParent()).getChildAt(1)
						.setVisibility(INVISIBLE);
			}
		});

		flipper1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.setBackgroundResource(R.drawable.button_clicked_sqaure);
				((LinearLayout) v.getParent()).getChildAt(1).setVisibility(
						VISIBLE);
				flipper0.setBackgroundResource(R.drawable.button_square);
				((LinearLayout) flipper0.getParent()).getChildAt(1)
						.setVisibility(INVISIBLE);
				flipper2.setBackgroundResource(R.drawable.button_square);
				((LinearLayout) flipper2.getParent()).getChildAt(1)
						.setVisibility(INVISIBLE);
				flipper3.setBackgroundResource(R.drawable.button_square);
				((LinearLayout) flipper3.getParent()).getChildAt(1)
						.setVisibility(INVISIBLE);
				if (fliper.getChildAt(1) != null) {
					fliper.removeViewAt(1);
				}
				LinearLayout children = new LinearLayout(
						BrowsingPOIInfoPanel.this.context);
				inflater.inflate(R.layout.poi_info_fliper_children, children);
				LinearLayout holder = (LinearLayout) children
						.findViewById(R.id.children_holder);
				fliper.addView(children, 1);
				fliper.setDisplayedChild(1);
				for (int i = 0; i < ((POI) shape).getPOIHolder().childrenLst
						.size(); i++) {
					TextView child = new TextView(context);
					LayoutParams params = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.FILL_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT);
					child.setPadding(20, 10, 10, 10);
					child.setTextSize(18);
					child.setLayoutParams(params);
					child.setTag(i);
					child.setText(((POI) shape).getPOIHolder().childrenLst.get(
							i).getPOIHolder().name
							+ " - "
							+ ((POI) shape).getPOIHolder().childrenLst.get(i)
									.getPOIHolder().source
							+ " - "
							+ ((POI) shape).getPOIHolder().childrenLst.get(i)
									.getPOIHolder().category);
					child.setTextColor(0xffff9900);
					child.setBackgroundResource(R.drawable.button_square);
					child.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							((com.interakt.ar.outdoor.browsing.BrowsingMainActivity) context)
									.removeView(BrowsingPOIInfoPanel.this);
							FetchPOIDataAsynckTask task = new FetchPOIDataAsynckTask();
							POI dummy = new POI(context, false);

							dummy.setHolder(((POI) shape).getPOIHolder().childrenLst
									.get((Integer) v.getTag()).getPOIHolder());
							task.execute(dummy);

						}
					});
					holder.addView(child);
				}
			}
		});

		flipper2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.setBackgroundResource(R.drawable.button_clicked_sqaure);
				((LinearLayout) v.getParent()).getChildAt(1).setVisibility(
						VISIBLE);
				flipper0.setBackgroundResource(R.drawable.button_square);
				((LinearLayout) flipper0.getParent()).getChildAt(1)
						.setVisibility(INVISIBLE);
				flipper1.setBackgroundResource(R.drawable.button_square);
				((LinearLayout) flipper1.getParent()).getChildAt(1)
						.setVisibility(INVISIBLE);
				flipper3.setBackgroundResource(R.drawable.button_square);
				((LinearLayout) flipper3.getParent()).getChildAt(1)
						.setVisibility(INVISIBLE);
				if (fliper.getChildAt(2) != null) {
					fliper.removeViewAt(2);
				}
				LinearLayout comments = new LinearLayout(
						BrowsingPOIInfoPanel.this.context);
				inflater.inflate(R.layout.poi_info_fliper_comments, comments);
				fliper.addView(comments, 2);
				fliper.setDisplayedChild(2);

				TextView comment = (TextView) comments
						.findViewById(R.id.poi_info_fliper_comment_button);
				final EditText field = (EditText) comments
						.findViewById(R.id.poi_info_fliper_comment_field);

				final LinearLayout commentsBody = (LinearLayout) comments
						.findViewById(R.id.comments_body);
				for (CommentHolder cHolder : ((POI) shape).getPOIHolder().commentsHolder) {
					LinearLayout commentItem = new LinearLayout(
							BrowsingPOIInfoPanel.this.context);
					inflater.inflate(R.layout.comment_item, commentItem);
					TextView user = (TextView) commentItem
							.findViewById(R.id.comment_name);
					TextView commentText = (TextView) commentItem
							.findViewById(R.id.comment_text);
					TextView time = (TextView) commentItem
							.findViewById(R.id.comment_date);
					user.setText(cHolder.name);
					commentText.setText(cHolder.comment);
					try {
						time.setText(p.format(format.parse(cHolder.time)));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					commentsBody.addView(commentItem);
				}
				comment.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						ConnectionDetector cd = new ConnectionDetector(context);

						if (!cd.isConnectingToInternet()) {

							Toast.makeText(context, "No Internet Connection !",
									Toast.LENGTH_LONG).show();
							return;
						}

						new CommentPOIAsynckTask().execute(shape, field
								.getText().toString());

						LinearLayout commentItem = new LinearLayout(
								BrowsingPOIInfoPanel.this.context);
						inflater.inflate(R.layout.comment_item, commentItem);
						TextView user = (TextView) commentItem
								.findViewById(R.id.comment_name);
						TextView commentText = (TextView) commentItem
								.findViewById(R.id.comment_text);
						TextView time = (TextView) commentItem
								.findViewById(R.id.comment_date);
						user.setText("You");
						commentText.setText(field.getText().toString());
						time.setText("Now");
						commentsBody.addView(commentItem);
						field.setText("");

					}
				});

			}
		});

		flipper3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.setBackgroundResource(R.drawable.button_clicked_sqaure);
				((LinearLayout) v.getParent()).getChildAt(1).setVisibility(
						VISIBLE);
				flipper0.setBackgroundResource(R.drawable.button_square);
				((LinearLayout) flipper0.getParent()).getChildAt(1)
						.setVisibility(INVISIBLE);
				flipper1.setBackgroundResource(R.drawable.button_square);
				((LinearLayout) flipper1.getParent()).getChildAt(1)
						.setVisibility(INVISIBLE);
				flipper2.setBackgroundResource(R.drawable.button_square);
				((LinearLayout) flipper2.getParent()).getChildAt(1)
						.setVisibility(INVISIBLE);
				
				if (fliper.getChildAt(3) != null) {
					fliper.removeViewAt(3);
				}
				//here 
				map.loadMap();
				Location l  = LocationBased.getLastKnowLocation();
				map.addRoute(new LatLng(l.getLatitude(),l.getLongitude()),new LatLng(Double.parseDouble(((POI) shape).getPOIHolder().latitude),Double.parseDouble(((POI) shape).getPOIHolder().longitude)));
				fliper.addView(map, 3);
				fliper.setDisplayedChild(3);
				
			}
		});

		flipper0.performClick();
		like.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (wait) {
					if (((TextView) v).getText().toString()
							.equalsIgnoreCase("like")) {
						new LikePOIAsynckTask().execute(shape, 1 + "");
					} else {
						new LikePOIAsynckTask().execute(shape, -1 + "");
					}
					wait = false;
				}
			}
		});

		name.setText(((POI) shape).getPOIHolder().name.equalsIgnoreCase("")
				|| ((POI) shape).getPOIHolder().name.equalsIgnoreCase("null") ? "N/A"
				: ((POI) shape).getPOIHolder().name);
		creator.setText((((POI) shape).getPOIHolder().creator
				.equalsIgnoreCase("")
				|| ((POI) shape).getPOIHolder().creator
						.equalsIgnoreCase("null") ? "No one" : ((POI) shape)
				.getPOIHolder().creator.split("@")[0])
				+ " has tagged it");
		category.setText((((POI) shape).getPOIHolder().category
				.equalsIgnoreCase("")
				|| ((POI) shape).getPOIHolder().category
						.equalsIgnoreCase("null") ? "No Category"
				: ((POI) shape).getPOIHolder().category));

		source.setText((((POI) shape).getPOIHolder().source
				.equalsIgnoreCase("")
				|| ((POI) shape).getPOIHolder().source.equalsIgnoreCase("null") ? "No Source"
				: ((POI) shape).getPOIHolder().source));
		try {
			date.setText("created from "
					+ (((POI) shape).getPOIHolder().created_at
							.equalsIgnoreCase("")
							|| ((POI) shape).getPOIHolder().created_at
									.equalsIgnoreCase("null") ? "N/A"
							: p.format(format.parse((((POI) shape)
									.getPOIHolder().created_at)))));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		description.setText("About \n"
				+ (((POI) shape).getPOIHolder().description
						.equalsIgnoreCase("")
						|| ((POI) shape).getPOIHolder().description
								.equalsIgnoreCase("null") ? "N/A"
						: ((POI) shape).getPOIHolder().description));
		rating.setText((((POI) shape).getPOIHolder().rating
				.equalsIgnoreCase("")
				|| ((POI) shape).getPOIHolder().rating.equalsIgnoreCase("null") ? "N/A"
				: ((POI) shape).getPOIHolder().rating));

		parent.setText(((POI) shape).getPOIHolder().parentObj != null ? ((POI) shape)
				.getPOIHolder().parentObj.getPOIHolder().name : "");
		totalNumberOfrates.setText(((POI) shape).getPOIHolder().total_rates);
		likes.setText(((POI) shape).getPOIHolder().likes);
		if (((POI) shape).getPOIHolder().parentObj != null) {
			parent.setBackgroundResource(R.drawable.button_square);
			parent.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					((com.interakt.ar.outdoor.browsing.BrowsingMainActivity) context)
							.removeView(BrowsingPOIInfoPanel.this);
					FetchPOIDataAsynckTask task = new FetchPOIDataAsynckTask();
					POI dummy = new POI(context, false);
					dummy.setHolder(((POI) shape).getPOIHolder().parentObj
							.getPOIHolder());
					task.execute(dummy);

				}
			});
		} else {
			parent.setBackgroundResource(0);
		}
		((com.interakt.ar.outdoor.browsing.BrowsingMainActivity) context).addView(this);
		//((BrowsingMainActivity) context)
			//	.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

	}

	public synchronized void FetchPOI(POI poi) {
		ConnectionDetector cd = new ConnectionDetector(context);
		if (!cd.isConnectingToInternet()) {
			Toast.makeText(context, "No Internet Connection !",
					Toast.LENGTH_LONG);
			return;
		}
		if (!busy) {
			FetchPOIDataAsynckTask task = new FetchPOIDataAsynckTask();
			task.execute(poi);
		}
	}

	public void setOnScreen(boolean value) {
		onScreen = value;
		flipper0.performClick();
	
	}

	private class FetchPOIDataAsynckTask extends
			AsyncTask<Object, Void, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			POIHolder holder = ((POI) params[0]).getPOIHolder();
			String out1 = ServerAPI.getPOI(holder.id);
			String out2 = ServerAPI.checkUserPoiState(
					mSharedPreferences.getString("USERNAME", ""), holder.id
							+ "");
			if(bm != null){
				bm.recycle();
			}
			System.out.print(holder.thumbnail);
			if (holder.thumbnail.equals("null") || holder.thumbnail.equals("")) {
				thumbnail.setImageResource(R.drawable.locationbased);
			} else {
				System.out.print("*&^^@^@^&@&$!@#$");
				BitmapFactory.Options bmOptions;
				bmOptions = new BitmapFactory.Options();
				bmOptions.inSampleSize = 1;
				bm  = ServerAPI.LoadImage(holder.thumbnail, bmOptions);
				thumbnail.setImageBitmap(bm);
			}
			

			try {

				JSONObject POIJSON = new JSONObject(new JSONArray(
						new JSONObject(out1).getString("poi")).getString(0));
				holder.address = POIJSON.getJSONObject("fields").getString(
						"address");
				// holder.attached_media = POIJSON.getJSONObject("fields")
				// .getString("attached_media");
				holder.category = POIJSON.getJSONObject("fields").getString(
						"category");
				holder.likes = POIJSON.getJSONObject("fields").getString(
						"likes");
				holder.created_at = POIJSON.getJSONObject("fields").getString(
						"created_at");
				holder.creator = POIJSON.getJSONObject("fields").getString(
						"creator");
				holder.description = POIJSON.getJSONObject("fields").getString(
						"description");

				// holder.display_status = POIJSON.getJSONObject("fields")
				// .getString("display_status");
				// holder.email = POIJSON.getJSONObject("fields").getString(
				// "email");
				// holder.email = POIJSON.getJSONObject("fields").getString(
				// "email");
				// holder.image = POIJSON.getJSONObject("fields").getString(
				// "image");
				// holder.phone = POIJSON.getJSONObject("fields").getString(
				// "phone");
				// holder.presented_as = POIJSON.getJSONObject("fields")
				// .getString("presented_as");
				holder.rating = POIJSON.getJSONObject("fields").getString(
						"poi_rating");
				// holder.sharing = POIJSON.getJSONObject("fields").getString(
				// "sharing");
				holder.source = POIJSON.getJSONObject("fields").getString(
						"source");
				// holder.status = POIJSON.getJSONObject("fields").getString(
				// "status");
				// holder.tags =
				// POIJSON.getJSONObject("fields").getString("tags");
				holder.total_rates = POIJSON.getJSONObject("fields").getString(
						"total_rates");
				// holder.thumbnail = POIJSON.getJSONObject("fields").getString(
				// "thumbnail");
				// holder.url =
				// POIJSON.getJSONObject("fields").getString("url");

				JSONArray COMMENTJSON = new JSONArray(POIJSON.getJSONObject(
						"fields").getString("comments"));
				holder.commentsHolder.clear();
				for (int i = 0; i < COMMENTJSON.length(); i++) {
					JSONObject o = new JSONObject(COMMENTJSON.getString(i));
					holder.commentsHolder.add(new POIHolder.CommentHolder(o
							.getString("user"), o.getString("comment"), o
							.getString("time")));
					System.out.println("COMMENT");

				}

			} catch (JSONException e) {

				e.printStackTrace();
			}

			System.out.println(">>>>>>>>>>>" + out2);

			holder.userLike = "true".equalsIgnoreCase((out2.split("_")[0]));
			System.out.println(holder.userLike);
			holder.userRate = Float.parseFloat((out2.split("_")[1])) + "";

			Shape shape = (Shape) params[0];

			return shape;
		}

		@Override
		protected void onPreExecute() {
			busy = true;
			((com.interakt.ar.outdoor.browsing.BrowsingMainActivity) context).handler
					.post(new Runnable() {

						@Override
						public void run() {
							((com.interakt.ar.outdoor.browsing.BrowsingMainActivity) context)
									.toggleProgressBar(true);
							((com.interakt.ar.outdoor.browsing.BrowsingMainActivity) context)
									.getProgressBar().setMessage("Fetching");

						}
					});

			super.onPreExecute();

		}

		@Override
		protected void onPostExecute(Object result) {

			setData((Shape) result);
			((com.interakt.ar.outdoor.browsing.BrowsingMainActivity) context).toggleProgressBar(false);
			super.onPostExecute(result);
			busy = false;
			onScreen = true;
			((com.interakt.ar.outdoor.browsing.BrowsingMainActivity) context).togleforPanel(false);
		}

	}

	private class RatePOIAsynckTask extends AsyncTask<Object, Void, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			POIHolder holder = ((POI) params[0]).getPOIHolder();
			Float rate = ((Float) params[1]);

			ResponseHolder response = ServerAPI.ratePOI_Marker(
					mSharedPreferences.getString("USERNAME", ""), "outdoor",
					holder.id, rate + "");

			response.data.add(holder);
			response.data.add(rate);

			return response;
		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();

		}

		@Override
		protected void onPostExecute(Object result) {
			POIHolder holder = (POIHolder) ((ResponseHolder) result).data
					.get(0);
			Float rate = (Float) ((ResponseHolder) result).data.get(1);
			if (((ResponseHolder) result).success) {
				rating.setText(((Float.parseFloat(holder.rating) * Float
						.parseFloat(holder.total_rates)) + rate)
						/ (Integer.parseInt(holder.total_rates) + 1) + "");
				totalNumberOfrates.setText(""
						+ (Integer.parseInt(holder.total_rates) + 1));
			}
			super.onPostExecute(result);

		}

	}

	private class LikePOIAsynckTask extends AsyncTask<Object, Void, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			POIHolder holder = ((POI) params[0]).getPOIHolder();
			ResponseHolder response = ServerAPI.LikePOI_Marker(
					mSharedPreferences.getString("USERNAME", ""), "outdoor",
					holder.id, (String) params[1]);

			response.data.add(params[1]);

			return response;
		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();

		}

		@Override
		protected void onPostExecute(Object result) {
			if (((ResponseHolder) result).success) {
				if (Integer.parseInt((String) ((ResponseHolder) result).data
						.get(0)) == 1) {
					likes.setText(""
							+ (Integer.parseInt(likes.getText().toString()) + 1));
					like.setText("Unlike");
				} else {
					likes.setText(""
							+ (Integer.parseInt(likes.getText().toString()) - 1));
					like.setText("Like");
				}
			}
			wait = true;
			super.onPostExecute(result);

		}

	}

	private class CommentPOIAsynckTask extends AsyncTask<Object, Void, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			POIHolder holder = ((POI) params[0]).getPOIHolder();
			ResponseHolder response = ServerAPI.CommentPOI_Marker(
					mSharedPreferences.getString("USERNAME", ""), "outdoor",
					holder.id, (String) params[1]);

			response.data.add(params[1]);

			return response;
		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();

		}

		@Override
		protected void onPostExecute(Object result) {

			super.onPostExecute(result);

		}

	}
	
	

	public boolean getOnScreen() {
		return onScreen;
	}

}

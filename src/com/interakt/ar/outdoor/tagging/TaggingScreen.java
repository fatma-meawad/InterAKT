package com.interakt.ar.outdoor.tagging;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.format.Time;
import android.widget.TextView;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector;
import com.interakt.ar.Settings;
import com.interakt.ar.android.ui.StatusBar;
import com.interakt.ar.graphics.LibGDXPerspectiveCamera;
import com.interakt.ar.graphics.Shape;
import com.interakt.ar.graphics.poi.POI;
import com.interakt.ar.graphics.poi.POITextureGenerator;
import com.interakt.ar.graphics.radar.Radar;
import com.interakt.ar.graphics.screens.EventHandler;
import com.interakt.ar.graphics.screens.Screen;
import com.interakt.ar.outdoor.browsing.BrowsingPOIInfoPanel;
import com.interakt.ar.util.Color;
import com.interakt.ar.util.OBJLoader;

public class TaggingScreen extends Screen {



	private int mode;
	private POI toBefixedPOI;

	private StatusBar status;
	private POITextureGenerator generator;
	private ArrayList<AsyncTask<Object, Void, Object>> networkTask = new ArrayList<AsyncTask<Object, Void, Object>>();

	private HashMap<String, POI> poisHashMap = new HashMap<String, POI>();
	
	private TextView distance;

	private float oldDistanceDelta;
	static AlertDialog dialog;
	boolean tapped = false;
	int tapX;
	int tapY;
	private ArrayList<Color> pickingColors = new ArrayList<Color>();

	ByteBuffer pixels = ByteBuffer.allocateDirect(4);
	private Radar radar;
	private BrowsingPOIInfoPanel poiInfoPanel;

	
	public static AlertDialog getDialog() {
		return dialog;
	}

	public TaggingScreen(final Context context) {
		super(context);

	}

	public void init(int width, int height) {
		camera = new LibGDXPerspectiveCamera(context, 35f, width, height, true);
		camera.far = 1000.0f;
		camera.near = 0.1f;
		camera.position.set(0.0f, 0.0f, 0.0f);
		status = ((com.interakt.ar.outdoor.tagging.TaggingMainActivity) context).getStatusBar();
		generator = ((com.interakt.ar.outdoor.tagging.TaggingMainActivity) context)
				.getTextureGenerator();
		pixels.order(ByteOrder.nativeOrder());
		pixels.position(0);
		OBJLoader.loadPOIBODYOBJ("models/POIGEN.obj");

		this.eventHandler = new EventHandler() {
	
			@Override
			public boolean tap(float x, float y, int count, int button) {
				Time now = new Time();
				now.setToNow();
				Settings.TobeLogged+=now.toString()+"\t"+Gdx.graphics.getWidth()+","+Gdx.graphics.getHeight()+","+x+","+y+","+"Tagging-Tap\n";

				return super.tap(x, y, count, button);

			}

			@Override
			public boolean pan(float x, float y, float deltaX, float deltaY) {
				Time now = new Time();
				now.setToNow();
				Settings.TobeLogged+=now.toString()+"\t"+Gdx.graphics.getWidth()+","+Gdx.graphics.getHeight()+","+x+","+y+","+"Tagging-Pan\n";

				return super.pan(x, y, deltaX, deltaY);
			}
			@Override
			public boolean zoom(float initialDistance, float distance) {
				Time now = new Time();
				now.setToNow();
				depthChange(initialDistance, distance);
				
				Settings.TobeLogged+=now.toString()+"\t"+Gdx.graphics.getWidth()+","+Gdx.graphics.getHeight()+","+initialDistance+","+distance+","+"Tagging-Zoom\n";


				return super.zoom(initialDistance, distance);
			}

			// @Override
			// public boolean tap(float x, float y, int count, int button) {
			// // Ray pickRay = OutdoorScreen.this.camera.getPickRay(x, y);
			// // intersectRayMeshes(pickRay);
			// tapped = true;
			// tapX = (int) x;
			// tapY = (int) y;
			// return super.tap(x, y, count, button);
			//
			// }

		};

		this.gestureDetector = new GestureDetector(this.eventHandler);
	}

	@Override
	public void dispose() {
		((LibGDXPerspectiveCamera) camera).dispose();
		for (Shape shape : shapes) {
			shape.dispose();
		}
		OBJLoader.unLoadPOIBODYOBJ();
		eventHandler = null;
		gestureDetector = null;
		shapes.clear();
		shapes = null;
		toBeDrawed.clear();
		toBeDrawed = null;
		drawn.clear();
		drawn = null;
		status.dispose();
		status = null;
		pixels.clear();
	}

	@Override
	public void render() {

		for (int i = 0; i < shapes.size(); i++) {
			shapes.get(i).draw();
		}

	}

	// check for intersections
	// public void intersectRayMeshes(Ray ray) {
	//
	// float[] view = camera.view.inv().val.clone();
	// Shape mindis = null;
	// for (Shape shape : drawn) {
	// double distance = Double.MAX_VALUE;
	// if (shape.isIntersected(ray, view, mode)) {
	// if ((shape).getRadius() < distance) {
	// distance = (shape).getRadius();
	// mindis = shape;
	// System.out.println("Found");
	// }
	//
	// }
	// System.out.println("endofshape");
	// }
	// if (mindis != null) {
	// ((POI) mindis).fireListener();
	// }
	// }

	public void depthChange(float initdistance, float distance) {
		for (Shape shape : shapes) {
			shape.changeDepth(initdistance, distance);
		}
	}

//	/**
//	 * Method that initializes the pop for POI creation. Here we should add the
//	 * suggestions and relations
//	 * 
//	 * @param holder
//	 */
//	private void buildAlertSetPOIName(final POI poi) {
//		builder = new AddPOIBuilder(context, poisHashMap);
//		builder.setCancelable(false)
//				.setPositiveButton("Proceed",
//						new DialogInterface.OnClickListener() {
//							public void onClick(
//									@SuppressWarnings("unused") final DialogInterface dialog,
//									@SuppressWarnings("unused") final int id) {
//								if (builder.nameAutocompleteTextField.getText()
//										.equals("")) {
//									poi.getPOIHolder().name = "Unnamed POI";
//								} else {
//									poi.getPOIHolder().name = builder.nameAutocompleteTextField
//											.getText().toString();
//								}
//								poi.getPOIHolder().parent = builder
//										.getSelectedParent();
//								poi.getPOIHolder().children = builder
//										.getSelectedChildren();
//								buildAddPOIDescription(poi);
//							}
//						})
//				.setNegativeButton("Cancel",
//						new DialogInterface.OnClickListener() {
//							public void onClick(final DialogInterface dialog,
//									@SuppressWarnings("unused") final int id) {
//								dialog.cancel();
//								if (TaggingScreen.this.dialog.isShowing())
//									TaggingScreen.this.dialog.dismiss();
//								try {
//									canRender.acquire();
//									removeShape(toBefixedPOI);
//									toBefixedPOI.dispose();
//								} catch (InterruptedException e) {
//
//									e.printStackTrace();
//								}
//								map.hideALLPOI();
//								map.showAllPOIs();
//								canRender.release();
//
//							}
//						});
//		dialog = builder.create();
//		dialog.show();
//	}

//	private void buildAddPOIDescription(final POI poi) {
//		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
//		LayoutInflater inflater = (LayoutInflater) context
//				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		final LinearLayout layout = (LinearLayout) inflater.inflate(
//				R.layout.add_poi_description, null);
//		builder.setView(layout);
//		builder.setTitle("Add POI Description");
//		builder.setPositiveButton("Proceed",
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(final DialogInterface dialog,
//							final int id) {
//						if (((EditText) layout
//								.findViewById(R.id.add_poi_description))
//								.getText().toString().equals("")) {
//							poi.getPOIHolder().description = "";
//
//						} else {
//							poi.getPOIHolder().description = ((EditText) layout
//									.findViewById(R.id.add_poi_description))
//									.getText().toString();
//
//						}
//					}
//				});
//		builder.setNegativeButton("Cancel",
//				new DialogInterface.OnClickListener() {
//					public void onClick(final DialogInterface dialog,
//							@SuppressWarnings("unused") final int id) {
//						dialog.cancel();
//						if (TaggingScreen.this.dialog.isShowing())
//							TaggingScreen.this.dialog.dismiss();
//						try {
//							canRender.acquire();
//							removeShape(toBefixedPOI);
//							toBefixedPOI.dispose();
//						} catch (InterruptedException e) {
//
//							e.printStackTrace();
//						}
//						map.hideALLPOI();
//						map.showAllPOIs();
//						canRender.release();
//
//					}
//				});
//		final AlertDialog alert = builder.create();
//
//		alert.show();
//	}

	public void onSurfaceChanged(int width, int height) {

	}

}
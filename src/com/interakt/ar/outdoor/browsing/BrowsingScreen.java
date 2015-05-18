package com.interakt.ar.outdoor.browsing;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.content.Context;
import android.text.format.Time;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;
import com.interakt.ar.Filter;
import com.interakt.ar.Settings;
import com.interakt.ar.android.ui.StatusBar;
import com.interakt.ar.graphics.LibGDXPerspectiveCamera;
import com.interakt.ar.graphics.Shape;
import com.interakt.ar.graphics.poi.POI;
import com.interakt.ar.graphics.poi.twodimensions.Renderer2D;
import com.interakt.ar.graphics.screens.EventHandler;
import com.interakt.ar.graphics.screens.Screen;
import com.interakt.ar.util.Color;
import com.interakt.ar.util.OBJLoader;

public class BrowsingScreen extends Screen {

	private StatusBar status;
	private float oldDistanceDelta;
	boolean tapped = false;
	int tapX;
	int tapY;
	private boolean panned = false;
	private int panX;
	private int panY;
	private boolean expandMode;
	private boolean expandModeFirst;
	private float nearestPOI = Float.MAX_VALUE;
	private POI expandedPOI;
	public POI clickedPOI;

	ByteBuffer pixels = ByteBuffer.allocateDirect(4);

	private ShapeRenderer shapeRenderer;
	private Renderer2D expandedRenderer;

	public BrowsingScreen(final Context context) {
		super(context);

	}

	public void init(int width, int height) {
		camera = new LibGDXPerspectiveCamera(context, 30f, width, height, true);
		camera.far = 1000.0f;
		camera.near = 0.1f;
		camera.position.set(0.0f, 0.0f, 0.0f);
		Settings.SHOWING_DISTANCE = 80;
		status = ((com.interakt.ar.outdoor.browsing.BrowsingMainActivity) context).getStatusBar();
		shapeRenderer = new ShapeRenderer();
		pixels.order(ByteOrder.nativeOrder());
		pixels.position(0);
		OBJLoader.loadPOIBODYOBJ("models/POIGEN.obj");
		expandedRenderer = new Renderer2D(context);
		expandedRenderer.create();
		this.eventHandler = new EventHandler() {

			@Override
			public boolean tap(float x, float y, int count, int button) {
				// Ray pickRay = OutdoorScreen.this.camera.getPickRay(x, y);
				// intersectRayMeshes(pickRay);
				tapped = true;
				tapX = (int) x;
				tapY = (int) y;
				if (expandMode) {
					expandedRenderer.inputTap(x, Gdx.graphics.getHeight() - y);
				}
				Time now = new Time();
				now.setToNow();
				Settings.TobeLogged += now.toString()+"\t"+Gdx.graphics.getWidth() + ","
						+ Gdx.graphics.getHeight() + "," + x + "," + y + ","
						+ "Browse-Tap\n";
				return super.tap(x, y, count, button);

			}

			@Override
			public boolean pan(float x, float y, float deltaX, float deltaY) {
				// panned = true;
				panX = (int) x;
				panY = (int) y;
				if (expandMode) {
					expandedRenderer.inputPan(x, Gdx.graphics.getHeight() - y,
							deltaX, deltaY);
				}
				Time now = new Time();
				now.setToNow();
				Settings.TobeLogged += now.toString()+"\t"+Gdx.graphics.getWidth() + ","
						+ Gdx.graphics.getHeight() + "," + x + "," + y + ","
						+ "Browse-Pan\n";

				return super.pan(x, y, deltaX, deltaY);
			}

			@Override
			public boolean zoom(float initialDistance, float distance) {
				Time now = new Time();
				now.setToNow();
				Settings.TobeLogged += now.toString()+"\t"+Gdx.graphics.getWidth() + ","
						+ Gdx.graphics.getHeight() + "," + initialDistance
						+ "," + distance + "," + "Browse-Zoom\n";

				return super.zoom(initialDistance, distance);
			}

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
		shapeRenderer.dispose();
		shapeRenderer = null;
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

	public void render(POI child, POI parent) {
		float zTranslation;
		((POI) child).caclulateRadius();
		if (((POI) child).getRadius() < Settings.LAYER_NAME_MIN + 80) {
			if (Settings.LAYER_NAME_MIN > 10) {
				((POI) child).setScalingMatrix(
						(float) Settings.LAYER_NAME_MIN / 10.0f,
						(float) Settings.LAYER_NAME_MIN / 10.0f,
						(float) Settings.LAYER_NAME_MIN / 10.0f);
			} else {
				float scale = (float) (((POI) child).getRadius() / 5.0 - ((POI) child)
						.getRadius() / 10.0);
				((POI) child).setScalingMatrix(scale, scale, scale);
			}

		} else {
			float scale = (float) (((POI) child).getRadius() / 5.0 - ((POI) child)
					.getRadius() / 10.0);
			((POI) child).setScalingMatrix(scale, scale, scale);
		}
		
		if (((POI) child).getRadius() < Settings.LAYER_NAME_MIN + 80) {

			zTranslation = -(((POI) child).getRadius() / 5);
			double x = (((POI) child).getRadius() / 5)
					+ ((POI) child).getRadius() / 15;
			double y = ((POI) child).getRadius()
					- Settings.LAYER_NAME_MIN;
			double z = y * 100.0 / 80.0;
			double w = x * z / 100.0;
			zTranslation += (float) w;
			((POI) child).setTranslationMatrix(
					((POI) child).getPositionf()[0],
					((POI) child).getPositionf()[1], zTranslation);
		} else {
			((POI) child).setTranslationMatrix(
					((POI) child).getPositionf()[0],
					((POI) child).getPositionf()[1],
					((POI) child).getRadius() / 15);
		}
		
		

		if (((POI) child).getRadius() > Settings.LAYER_NAME_MIN
				&& ((POI) child).getRadius() < Settings.LAYER_NAME_MAX) {
			{
				
				//System.out.println("here1");

				if (!tapped && !panned) {
					//System.out.println("here2");
						//System.out.println("in 2nd render");
						child.draw();
				} else {
					if (((POI) child).getPOIHolder().parentObj == null) {
						((POI) child).renderForPicking();
					}
				}

			}
		}


			for (int i = 0; i < child.getPOIHolder().childrenLst.size(); i++) {
				render(child.getPOIHolder().childrenLst.get(i), child);

			}
		

		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(0, 0, 0, 1);
		shapeRenderer.line(child.getShowPositionf()[0],
				child.getShowPositionf()[1], child.getShowPositionf()[2],
				parent.getShowPositionf()[0], parent.getShowPositionf()[1],
				parent.getShowPositionf()[2]);
		shapeRenderer.end();

	}

	@Override
	public void render() {
		if (Filter.filter) {
			((BrowsingMainActivity) context).checkFilter();
			try {
				Filter.filtering.acquire();
				Filter.filter = false;
				Filter.filtering.release();
			} catch (InterruptedException e) {

				e.printStackTrace();
			}

		}
		float zTranslation;
		if (!expandMode) {
			for (Shape shape : toBeDrawed) {

				((POI) shape).caclulateRadius();
				if (((POI) shape).getRadius() < Settings.LAYER_NAME_MIN + 80) {
					if (Settings.LAYER_NAME_MIN > 10) {
						((POI) shape).setScalingMatrix(
								(float) Settings.LAYER_NAME_MIN / 10.0f,
								(float) Settings.LAYER_NAME_MIN / 10.0f,
								(float) Settings.LAYER_NAME_MIN / 10.0f);
					} else {
						float scale = (float) (((POI) shape).getRadius() / 5.0 - ((POI) shape)
								.getRadius() / 10.0);
						((POI) shape).setScalingMatrix(scale, scale, scale);
					}

				} else {
					float scale = (float) (((POI) shape).getRadius() / 5.0 - ((POI) shape)
							.getRadius() / 10.0);
					((POI) shape).setScalingMatrix(scale, scale, scale);
				}
				if (((POI) shape).getRadius() < nearestPOI) {
					nearestPOI = ((POI) shape).getRadius();
				}
				if (((POI) shape).getRadius() < Settings.LAYER_NAME_MIN + 80) {

					zTranslation = -(((POI) shape).getRadius() / 5);
					double x = (((POI) shape).getRadius() / 5)
							+ ((POI) shape).getRadius() / 15;
					double y = ((POI) shape).getRadius()
							- Settings.LAYER_NAME_MIN;
					double z = y * 100.0 / 80.0;
					double w = x * z / 100.0;
					zTranslation += (float) w;
					((POI) shape).setTranslationMatrix(
							((POI) shape).getPositionf()[0],
							((POI) shape).getPositionf()[1], zTranslation);
				} else {
					((POI) shape).setTranslationMatrix(
							((POI) shape).getPositionf()[0],
							((POI) shape).getPositionf()[1],
							((POI) shape).getRadius() / 15);
				}

				if (((POI) shape).getRadius() > Settings.LAYER_NAME_MIN
						&& ((POI) shape).getRadius() < Settings.LAYER_NAME_MAX) {
					{

						if (!tapped && !panned) {
							if (((POI) shape).getPOIHolder().parentObj == null) {
								shape.draw();
							}
						} else {
							if (((POI) shape).getPOIHolder().parentObj == null) {
								((POI) shape).renderForPicking();
							}
						}

					}
				}

				// float[] out = ((POI) shape).getPositionf();
				// if (Settings.LAYER_NAME_MIN != 0) { // Value zoomed
				// // in
				// float newX = (float) (out[0]
				// * (((POI) shape).getRadius() - Settings.LAYER_NAME_MIN) /
				// Settings.LAYER_NAME_MIN);
				// float newY = (float) (out[1]
				// * (((POI) shape).getRadius() - Settings.LAYER_NAME_MIN) /
				// Settings.LAYER_NAME_MIN);
				// float newZ = (float) (out[2]
				// * (((POI) shape).getRadius() - Settings.LAYER_NAME_MIN) /
				// Settings.LAYER_NAME_MIN);
				// float radius = (float) Math.sqrt(Math.pow(newX, 2)
				// + Math.pow(newY, 2) + Math.pow(newZ, 2));
				// if (radius < Settings.INNER_RADIUS_RANGE) {
				// } else {
				// shape.setShowAt(newX, newY, zTranslation);
				// if (!tapped) {
				// shape.draw();
				// } else {
				// ((POI) shape).renderForPicking();
				//
				// }
				// }
				// } else {
				// shape.setShowAt(out[0], out[1], zTranslation);
				// if (!tapped) {
				// shape.draw();
				// } else {
				// ((POI) shape).renderForPicking();
				// }
				// }
				//
				// } else {
				// ((POI) shape).caclulateShowRadius();
				// if (shape.getShowRadius() > Settings.INNER_RADIUS_RANGE) {
				// shape.setShowAt(0, 0, 0);
				// if (!tapped) {
				// shape.draw();
				// } else {
				// ((POI) shape).renderForPicking();
				// }
				// }
				// }

			}
		} else {

			((POI) expandedPOI).caclulateRadius();
			if (((POI) expandedPOI).getRadius() < Settings.LAYER_NAME_MIN + 80) {
				if (Settings.LAYER_NAME_MIN > 10) {
					((POI) expandedPOI).setScalingMatrix(
							(float) Settings.LAYER_NAME_MIN / 10.0f,
							(float) Settings.LAYER_NAME_MIN / 10.0f,
							(float) Settings.LAYER_NAME_MIN / 10.0f);
				} else {
					float scale = (float) (((POI) expandedPOI).getRadius() / 5.0 - ((POI) expandedPOI)
							.getRadius() / 10.0);
					((POI) expandedPOI).setScalingMatrix(scale, scale, scale);
				}

			} else {
				float scale = (float) (((POI) expandedPOI).getRadius() / 5.0 - ((POI) expandedPOI)
						.getRadius() / 10.0);
				((POI) expandedPOI).setScalingMatrix(scale, scale, scale);
			}
			if (((POI) expandedPOI).getRadius() < nearestPOI) {
				nearestPOI = ((POI) expandedPOI).getRadius();
			}
			if (((POI) expandedPOI).getRadius() < Settings.LAYER_NAME_MIN + 80) {

				zTranslation = -(((POI) expandedPOI).getRadius() / 5);
				double x = (((POI) expandedPOI).getRadius() / 5)
						+ ((POI) expandedPOI).getRadius() / 15;
				double y = ((POI) expandedPOI).getRadius()
						- Settings.LAYER_NAME_MIN;
				double z = y * 100.0 / 80.0;
				double w = x * z / 100.0;
				zTranslation += (float) w;
				((POI) expandedPOI).setTranslationMatrix(
						((POI) expandedPOI).getPositionf()[0],
						((POI) expandedPOI).getPositionf()[1], zTranslation);
			} else {
				((POI) expandedPOI).setTranslationMatrix(
						((POI) expandedPOI).getPositionf()[0],
						((POI) expandedPOI).getPositionf()[1],
						((POI) expandedPOI).getRadius() / 15);
			}

			if (((POI) expandedPOI).getRadius() > Settings.LAYER_NAME_MIN
					&& ((POI) expandedPOI).getRadius() < Settings.LAYER_NAME_MAX) {
				{

					if (!tapped && !panned) {
						if (((POI) expandedPOI).getPOIHolder().parentObj == null) {
							expandedPOI.draw();
						}
					} else {
						if (((POI) expandedPOI).getPOIHolder().parentObj == null) {
							((POI) expandedPOI).renderForPicking();
						}
					}

				}
			}
			
			for (int i = 0; i < expandedPOI.getPOIHolder().childrenLst.size(); i++) {
	
				render(expandedPOI.getPOIHolder().childrenLst.get(i),
						expandedPOI);
			}

			if (expandModeFirst)
				expandModeFirst = false;

			// expandedRenderer.render();

		}
		if (tapped) {
			tapped = false;
			Gdx.gl10.glReadPixels(tapX, Gdx.graphics.getHeight() - tapY, 1, 1,
					Gdx.gl10.GL_RGBA, Gdx.gl10.GL_UNSIGNED_BYTE, pixels);
			System.out.println(pixels.get(0) + " " + pixels.get(1) + " "
					+ pixels.get(2) + " " + pixels.get(3));
			Gdx.gl11.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 255);
			Gdx.gl10.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			Gdx.gl10.glClear(GL10.GL_COLOR_BUFFER_BIT
					| GL10.GL_DEPTH_BUFFER_BIT);
			Color c = new Color(pixels.get(0), pixels.get(1), pixels.get(2));
			if (c.equals(new Color((byte) 0, (byte) 0, (byte) 0))) {

				expandMode = false;
				expandedPOI = null;

			} else {
				for (Shape shape : shapes) {
					if (((POI) shape).isThisYou(c)) {
						if (expandMode
								|| !(((POI) shape).getPOIHolder().childrenLst
										.size() > 0)) {
							((POI) shape).fireListener();
							((POI) shape).getPOIHolder().radarPOI.isClicked = true;
							clickedPOI = (POI) shape;
						} else {
							expandMode = true;
							expandModeFirst = true;
							expandedPOI = ((POI) shape);
							expandedRenderer.createNewGraph(((POI) shape),
									tapX, Gdx.graphics.getHeight() - tapY);
						}

					}
				}
			}
			render();

		}

		if (panned) {
			panned = false;
			Gdx.gl10.glReadPixels(panX, Gdx.graphics.getHeight() - panY, 1, 1,
					Gdx.gl10.GL_RGBA, Gdx.gl10.GL_UNSIGNED_BYTE, pixels);
			System.out.println(pixels.get(0) + " " + pixels.get(1) + " "
					+ pixels.get(2) + " " + pixels.get(3));
			Gdx.gl11.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 255);
			Gdx.gl10.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			Gdx.gl10.glClear(GL10.GL_COLOR_BUFFER_BIT
					| GL10.GL_DEPTH_BUFFER_BIT);
			Color c = new Color(pixels.get(0), pixels.get(1), pixels.get(2));
			if (c.equals(new Color((byte) 0, (byte) 0, (byte) 0))) {

			} else {
				for (Shape shape : shapes) {
					if (((POI) shape).isThisYou(c)) {
						camera.near = shape.getRadius();
						camera.update();
						camera.apply(Gdx.gl10);
						Vector3 v = new Vector3(panX, Gdx.graphics.getHeight()
								- panY, 0);
						camera.unproject(v);
						camera.near = 0.1f;
						camera.update();
						camera.apply(Gdx.gl10);
						double z = Math.sqrt(Math.pow(shape.getRadius(), 2)
								- Math.pow(v.x, 2) - Math.pow(v.y, 2));
						System.out.println("location " + v.x + " " + v.y + " "
								+ z);
						((POI) shape).setTranslationMatrix(v.x, v.y, (float) z);

					}
				}
			}
			render();

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

	public boolean changeDepth(float before, float after) {
		System.out.println("Here" + Settings.RADIUS_RANGE_PROGRESS);
		if (after - before < oldDistanceDelta) {
			if (Math.abs(after - before - oldDistanceDelta) > 2) {
				if (Settings.RADIUS_RANGE_PROGRESS - 0.2 > 0.2) {

					Settings.RADIUS_RANGE_PROGRESS -= 0.2;
					Settings.LAYER_NAME_MIN = Settings.RADIUS_RANGE_PROGRESS;

				}
			}
		} else {
			if (Math.abs(after - before - oldDistanceDelta) > 2) {
				if (Settings.RADIUS_RANGE_PROGRESS + 0.2 < Settings.LAYER_NAME_MAX) {
					Settings.RADIUS_RANGE_PROGRESS += 0.2;
					Settings.LAYER_NAME_MIN = Settings.RADIUS_RANGE_PROGRESS;
				}
			}

		}
		oldDistanceDelta = after - before;
		return true;
	}

	public void onSurfaceChanged(int width, int height) {
		camera = new LibGDXPerspectiveCamera(context, 30f, width, height, true);
		camera.far = 1000.0f;
		camera.near = 0.1f;
		camera.position.set(0.0f, 0.0f, 0.0f);
	}

}
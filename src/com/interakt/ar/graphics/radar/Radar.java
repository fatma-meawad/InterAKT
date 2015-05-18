package com.interakt.ar.graphics.radar;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import javax.microedition.khronos.opengles.GL;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.collision.Ray;
import com.interakt.ar.graphics.LibGDXPerspectiveCamera;
import com.interakt.ar.graphics.Shape;
import com.interakt.ar.graphics.poi.POI;

import android.content.Context;

public class Radar extends Shape {

	private SpriteBatch spriteBatch;

	private int RADARSIZE = 506;
	private int TEXTURESIZE = 512;
	private int SIZE = 0;
	private int CENTER = 0;
	private int xOffset = 10;
	private int yOffset = 10;
	private float INNERRADIUS;
	private float OUTERRADIUS;
	private float angle = 0;
	private float MAXRADIUS;
	private Semaphore canRender = new Semaphore(1);

	ArrayList<RadarPOI> RadarPOIs = new ArrayList<RadarPOI>();

	private ShapeRenderer shapeRenderer;

	public boolean hide;

	public Radar(Context context, float radius) {
		super(context);
		MAXRADIUS = radius;
	}

	public void init() {
	
		SIZE = Gdx.graphics.getHeight() / 3;
		CENTER = SIZE / 2;
		RADARSIZE = (RADARSIZE * SIZE) / 512;
		INNERRADIUS = 0;
		OUTERRADIUS = RADARSIZE / 2;
		spriteBatch = new SpriteBatch();
		texture = new Texture(Gdx.files.internal("data/radar.png"));
		shapeRenderer = new ShapeRenderer();
		System.out.println(SIZE + " " + CENTER + " " + RADARSIZE);
	}

	@Override
	public void dispose() {
		super.dispose();
		RadarPOIs.clear();
		RadarPOIs = null;
		shapeRenderer.dispose();
		spriteBatch.dispose();
		texture.dispose();
		texture = null;
		

	}

	@Override
	public void draw() {
		if (!hide) {
			if (spriteBatch == null) {
				init();
			}
			Gdx.gl10.glEnable(Gdx.gl10.GL_ALPHA_TEST);
			spriteBatch.setColor(1, 1, 1, 1);
			spriteBatch.begin();
			spriteBatch.draw(texture, xOffset, yOffset, SIZE, SIZE);
			spriteBatch.end();
			if (canRender.tryAcquire()) {
				Gdx.gl10.glPointSize(4);
				shapeRenderer.begin(ShapeType.Point);
				shapeRenderer.identity();
				shapeRenderer.translate((xOffset + CENTER), (yOffset + CENTER),0);
				angle = Gdx.input.getAzimuth() + 90;
				shapeRenderer.rotate(0, 0, 1, angle);
				for (int i = 0; i < RadarPOIs.size(); i++) {
					if (RadarPOIs.get(i).visible && Math.sqrt((Math.pow(RadarPOIs.get(i).x, 2)+Math.pow(RadarPOIs.get(i).y, 2)))>INNERRADIUS&& Math.sqrt((Math.pow(RadarPOIs.get(i).x, 2)+Math.pow(RadarPOIs.get(i).y, 2)))<OUTERRADIUS) {
						shapeRenderer.setColor(RadarPOIs.get(i).getColor()[0],
								RadarPOIs.get(i).getColor()[1], RadarPOIs
										.get(i).getColor()[2], 1);
						shapeRenderer.point(RadarPOIs.get(i).x,
								RadarPOIs.get(i).y, 0);
					}

				}
				shapeRenderer.end();
				shapeRenderer.begin(ShapeType.Circle);
				shapeRenderer.setColor(0, 1, 0, 1);
				shapeRenderer.circle(0, 0, INNERRADIUS, 50);
				shapeRenderer.setColor(0, 0, 1, 1);
				if(INNERRADIUS+(((RADARSIZE / 2)) * (80 * MAXRADIUS/250)) / MAXRADIUS<OUTERRADIUS){
				shapeRenderer.circle(0, 0, INNERRADIUS+(((RADARSIZE / 2) - 10) * (80 * MAXRADIUS/250)) / MAXRADIUS, 50);
				}
				shapeRenderer.setColor(1, 0, 0, 1);
				shapeRenderer.circle(0, 0, OUTERRADIUS, 50);
				shapeRenderer.end();
				canRender.release();

			}
			Gdx.gl10.glDisable(Gdx.gl10.GL_ALPHA_TEST);
		}
	}

	public void conversion(float min, float max, float radius) {
		INNERRADIUS = (((RADARSIZE / 2)) * min) / radius;
		OUTERRADIUS = (((RADARSIZE / 2)) * max) / radius;
	}

	public void clearRadar(){
		if(RadarPOIs != null)
		RadarPOIs.clear();
	}
	
	
	public void addPOI(POI poi) {
		try {
			canRender.acquire();
			float[] position = poi.getPositionf();
			float x = (((RADARSIZE / 2) - 10) * position[0]) / MAXRADIUS;
			float y = (((RADARSIZE / 2) - 10) * position[1]) / MAXRADIUS;
			RadarPOI r;
			RadarPOIs.add(r = new RadarPOI(x, y));
			poi.getPOIHolder().radarPOI = r;
			if (Math.abs(x) > ((RADARSIZE / 2) - 10)
					|| Math.abs(y) > ((RADARSIZE / 2) - 10)) {
				r.visible = false;
			} else {
				r.visible = true;
			}
			canRender.release();
		} catch (InterruptedException e) {

		}

	}

}

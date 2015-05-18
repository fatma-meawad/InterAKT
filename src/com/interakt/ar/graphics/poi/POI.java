package com.interakt.ar.graphics.poi;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;
import com.badlogic.gdx.math.collision.Ray;
import com.interakt.ar.Settings;
import com.interakt.ar.android.sensors.LocationBased;
import com.interakt.ar.graphics.LibGDXPerspectiveCamera;
import com.interakt.ar.graphics.Shape;
import com.interakt.ar.outdoor.browsing.BrowsingScreen;
import com.interakt.ar.util.Color;
import com.interakt.ar.util.LowPassFilter;
import com.interakt.ar.util.OBJLoader;

public class POI extends Shape {
	StillModel body;
	String intersectedMesh;
	private Texture textureBody;
	private POIHolder holder;
	private double[] coordinatesENU;
	private double[] coordinatesGEO;
	private Texture textureBodyLock;
	private Texture loadingTexture;
	private boolean bodyTextureLoaded;
	private boolean loadingTextureLoaded;

	private Color pickingColor;
	public float ENUX;
	public float ENUY;
	public float ENUZ;
	private Texture farTexture;

	public POI(Context context, boolean willBeDrawed) {
		super(context);
		if (willBeDrawed) {
			body = new StillModel(OBJLoader.POIBODY.getSubMeshes());
			filter = new LowPassFilter();
		}

	}

	public void setPickingColor(Color c) {
		pickingColor = c;
	}

	public boolean isThisYou(Color c) {
		return pickingColor.equals(c);
	}

	public void setHolder(POIHolder holder) {
		this.holder = holder;
	}

	public void setCoordinates(double[] coordinates) {
		this.coordinatesENU = coordinates;

	}

	public void dispose() {
		try {
			textureBody.dispose();
			textureBodyLock.dispose();
			loadingTexture.dispose();
			textureBody = null;
			textureBodyLock = null;
			loadingTexture = null;
			body.dispose();
		} catch (Exception e) {

		}
	}

	public void setBodyTexture(){
		addTexture("",true);
	}
	
	public void addTexture(String file, boolean gen) {
		if (!gen) {
			if (!islocked) {
				textureBody = new Texture(Gdx.files.internal(file));
				textureBody.setFilter(TextureFilter.Linear,
						TextureFilter.Linear);

			} else {
				textureBodyLock = new Texture(Gdx.files.internal(file));
				textureBodyLock.setFilter(TextureFilter.Linear,
						TextureFilter.Linear);
			}
		} else {
		//	Texture.setEnforcePotImages(false);
			textureBody = new Texture(holder.textureGen);
			textureBody.setFilter(TextureFilter.Linear,
					TextureFilter.Linear);
		}
	}

	public void setLoadingTexture() {
		loadingTexture = new Texture(
				Gdx.files.internal("models/loadingPOI.png"));
		loadingTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}

	public void setFarTexture() {
		Texture.setEnforcePotImages(false);
		farTexture = new Texture(Gdx.files.internal("models/fartexture.png"));
		farTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}

	public static int loadGLTextureFromBitmap(Bitmap bitmap) {

		// Generate one texture pointer
		int[] textureIds = new int[1];
		Gdx.gl10.glGenTextures(1, textureIds, 0);

		// bind this texture
		Gdx.gl10.glBindTexture(GL10.GL_TEXTURE_2D, textureIds[0]);

		// Create Nearest Filtered Texture
		Gdx.gl10.glTexParameterf(GL10.GL_TEXTURE_2D,
				GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		Gdx.gl10.glTexParameterf(GL10.GL_TEXTURE_2D,
				GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		Gdx.gl10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
				GL10.GL_REPEAT);
		Gdx.gl10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GL10.GL_REPEAT);

		// Use the Android GLUtils to specify a two-dimensional texture image
		// from our bitmap
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		return textureIds[0];
	}

	public void draw() {


		Gdx.gl10.glPushMatrix();
		if (islocked) {
			float[] lookAt = ((LibGDXPerspectiveCamera) this.parent.getCamera())
					.getLookVector();
			float newX = lookAt[0] * radius / this.parent.getCamera().far;
			float newY = lookAt[1] * radius / this.parent.getCamera().far;
			float newZ = lookAt[2] * radius / this.parent.getCamera().far;
			float[] infilter = { newX, newY, newZ };
			float[] outfilter = this.filter.lowPass(infilter, 0.06f);
			this.setTranslationMatrix(outfilter[0], outfilter[1], outfilter[2]);
			Gdx.gl10.glTranslatef(outfilter[0], outfilter[1], outfilter[2]);
			angle = (float) Math.atan(outfilter[1] / outfilter[0]);
			xAxis = 0;
			yAxis = 0;
			zAxis = 1;
			angle = outfilter[0] > 0 && outfilter[1] > 0 || outfilter[0] > 0
					&& outfilter[1] < 0 ? (float) (angle * 180 / Math.PI)
					: 180 + (float) (angle * 180 / Math.PI);
			Gdx.gl10.glRotatef(angle, xAxis, yAxis, zAxis);
			Gdx.gl10.glScalef(scaleX, scaleY, scaleZ);
			if (textureBodyLock != null) {
				textureBodyLock.bind();
			} else {
				addTexture(holder.textureLock, false);
				textureBodyLock.bind();
			}
		} else {

			float[] infilter = { virX, virY, virZ };
			float[] outfilter = this.filter.lowPass(infilter, 0.06f);
			Gdx.gl10.glTranslatef(outfilter[0], outfilter[1], outfilter[2]);
			angle = (float) Math.atan(outfilter[1] / outfilter[0]);
			showX = outfilter[0];
			showY = outfilter[1];
			showZ = outfilter[2];
			xAxis = 0;
			yAxis = 0;
			zAxis = 1;
			angle = showX > 0 && showY > 0 || showX > 0 && showY < 0 ? (float) (angle * 180 / Math.PI)
					: 180 + (float) (angle * 180 / Math.PI);
			Gdx.gl10.glRotatef(angle, xAxis, yAxis, zAxis);
			Gdx.gl10.glScalef(scaleX, scaleY, scaleZ);
			if (radius < Settings.LAYER_NAME_MIN+Settings.SHOWING_DISTANCE) {
				if (holder.textureGen != null) {
					if (textureBody != null) {
						textureBody.bind();
					} else {
						if (this.parent instanceof BrowsingScreen) {
							addTexture(holder.texture, true);
							holder.textureGen.dispose();
							bodyTextureLoaded = true;
						} else {
							
							addTexture(holder.texture, false);
							bodyTextureLoaded = true;
						}
						textureBody.bind();
					}
				} else {
					if (loadingTexture == null) {
						
						setLoadingTexture();
						loadingTextureLoaded = true;
					}
					loadingTexture.bind();
				}
			} else {
				if (farTexture == null) {
					setFarTexture();
				}
				farTexture.bind();
			}
		}
		body.subMeshes[0].mesh.render(GL10.GL_TRIANGLES);

		Gdx.gl10.glBindTexture(GL10.GL_TEXTURE_2D, 0);
		// Gdx.gl11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, accModelView, 0);
//		Gdx.gl10.glDisable(GL10.GL_BLEND);
//		Gdx.gl10.glDisable(GL10.GL_ALPHA_TEST);

		Gdx.gl10.glPopMatrix();
		if (instantiance) {
			virX = x;
			virY = y;
			virZ = z;
			instantiance = false;
		}

	}

	public void renderForPicking() {
		Gdx.gl10.glPushMatrix();
		Gdx.gl10.glEnable(GL10.GL_COLOR_MATERIAL);
		Gdx.gl10.glDisable(GL10.GL_TEXTURE_2D);
		float[] infilter = { virX, virY, virZ };
		float[] outfilter = this.filter.lowPass(infilter, 0.06f);
		Gdx.gl10.glTranslatef(outfilter[0], outfilter[1], outfilter[2]);
		angle = (float) Math.atan(outfilter[1] / outfilter[0]);
		showX = outfilter[0];
		showY = outfilter[1];
		showZ = outfilter[2];
		xAxis = 0;
		yAxis = 0;
		zAxis = 1;
		angle = showX > 0 && showY > 0 || showX > 0 && showY < 0 ? (float) (angle * 180 / Math.PI)
				: 180 + (float) (angle * 180 / Math.PI);
		Gdx.gl10.glRotatef(angle, xAxis, yAxis, zAxis);
		Gdx.gl10.glScalef(scaleX, scaleY, scaleZ);
		Gdx.gl11.glColor4ub((byte) pickingColor.R, (byte) pickingColor.G,
				(byte) pickingColor.B, (byte) 255);
		body.subMeshes[0].mesh.render(GL10.GL_TRIANGLES);
		Gdx.gl10.glEnable(GL10.GL_TEXTURE_2D);
		Gdx.gl10.glEnable(GL10.GL_DITHER);
		Gdx.gl10.glDisable(GL10.GL_COLOR_MATERIAL);
		Gdx.gl10.glPopMatrix();

	}

	public void caclulateRadius() {
		radius = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)
				+ Math.pow(z, 2));
	}

	public void caclulateShowRadius() {
		showRadius = (float) Math.sqrt(Math.pow(showX, 2) + Math.pow(showY, 2)
				+ Math.pow(showZ, 2));
	}

	public boolean isIntersected(Ray ray, float[] view, int mode) {

		return false;
	}

	public double[] getCoordinatesGEO() {
		if(holder.latitude !=null && holder.longitude != null){
		return new double[] { Double.parseDouble(holder.latitude),
				Double.parseDouble(holder.longitude), 0.0 };
		}else{
			return new double [] {LocationBased.getLastKnowLocation().getLatitude(),LocationBased.getLastKnowLocation().getLongitude()};
		}
	}

	public void fireListener() {
		if (listener != null) {
			listener.click(this);
		}
	}

	public POIHolder getPOIHolder() {
		return holder;
	}

	public String toString() {
		return holder.name + " " + (int) radius + "m";
	}

	public void resetPosition() {
		x = ENUX;
		y = ENUY;
		z = ENUZ;
	}

	public boolean isBodyTextureLoaded(){
		return bodyTextureLoaded;
	}
	
	public boolean isLoadingTextureLoaded(){
		return loadingTextureLoaded;
	}
	
	public Texture getTextureBody() {
		// TODO Auto-generated method stub
		return textureBody;
	}
	
	public Texture getLoadingTexture() {
		// TODO Auto-generated method stub
		return loadingTexture;
	}

}
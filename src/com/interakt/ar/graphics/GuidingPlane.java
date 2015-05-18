package com.interakt.ar.graphics;


import android.content.Context;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.collision.Ray;
import com.interakt.ar.graphics.screens.Screen;

public class GuidingPlane extends Shape {

	private ShapeRenderer shaperenderer;
	public float zv;

	public GuidingPlane(Context context) {
		super(context);
		shaperenderer = new ShapeRenderer();

	}

	public void dispose() {

	}

	public void draw() {
		Gdx.gl10.glPushMatrix();
		Gdx.gl10.glEnable(GL10.GL_COLOR_MATERIAL);
		Gdx.gl10.glEnable(GL10.GL_ALPHA_TEST);
		Gdx.gl10.glAlphaFunc(GL10.GL_GREATER, 0.0f);

		// Gdx.gl10.glMultMatrixf(this.parent.getCamera().getOrientationMatrix(),
		// 0);
		// shaperenderer.setTransformMatrix(new
		// Matrix4(this.parent.getCamera().getOrientationMatrix()));
		// shaperenderer.rotate(0, 1, 0, -90);
		shaperenderer.setProjectionMatrix(this.parent.getCamera().combined);
		shaperenderer.identity();
		shaperenderer.translate(0, 0, zv);
		shaperenderer.begin(ShapeType.Line);
		shaperenderer.setColor(1f, 0f, 0f, 1f);
		for (int i = -50; i < 50; i++)
			shaperenderer.line(-50, i, -2, 50, i, -2);
		for (int i = -50; i < 50; i++)
			shaperenderer.line(i, -50, -2, i, 50, -2);
		shaperenderer.end();
		Gdx.gl10.glEnable(GL10.GL_COLOR_MATERIAL);
		Gdx.gl10.glPopMatrix();
		Gdx.gl10.glDisable(GL10.GL_ALPHA_TEST);

	}

	public boolean isIntersected(Ray ray, float[] view, int mode) {
		return false;
	}

}

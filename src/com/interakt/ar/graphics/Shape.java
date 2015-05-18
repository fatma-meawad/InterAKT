package com.interakt.ar.graphics;

import android.content.Context;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.interakt.ar.graphics.screens.Screen;
import com.interakt.ar.listeners.OnShapeClickListener;
import com.interakt.ar.util.LowPassFilter;

public abstract class Shape {


	protected float x;
	protected float y;
	protected float z;

	protected float xAxis;
	protected float yAxis;
	protected float zAxis;
	protected float angle;

	protected float showX;
	protected float showY;
	protected float showZ;

	protected float virX;
	protected float virY;
	protected float virZ;

	protected boolean isvisible;

	protected float scaleX = 1;
	protected float scaleY = 1;
	protected float scaleZ = 1;

	protected boolean islocked;
	protected Screen parent;
	protected float oldDistanceDelta;

	protected Mesh[] mesh;

	protected Texture texture;

	protected short[][] intersectionIndices;

	protected OnShapeClickListener listener;

	protected float radius;

	protected float[] modelView = new float[16];
	protected float[] accModelView = new float[16];

	protected int index;

	protected int drawIndex;

	protected LowPassFilter filter = new LowPassFilter();

	protected Context context;

	protected float showRadius;
	
	protected boolean instantiance;

	public void dispose(){
		modelView = null;
		accModelView = null;
		filter = null;
	}

	public abstract void draw();

	public void setTranslationMatrix(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.virX = x;
		this.virY = y;
		this.virZ = z;
	}

	public void setTranslationMatrixInstantiance(float x, float y, float z) {
		this.virX = x;
		this.virY = y;
		this.virZ = z;
		instantiance = true;
	}


	public void setRotationMatrix(float angle, float x, float y, float z) {
		this.angle = angle;
		this.xAxis = x;
		this.yAxis = y;
		this.zAxis = z;
	}

	public void setScalingMatrix(float x, float y, float z) {
		this.scaleX = x;
		this.scaleY = y;
		this.scaleZ = z;
	}

	public Shape(Context context) {
		this.context = context;
	}

	

	public void setOnclickListener(OnShapeClickListener l) {
		listener = l;
	}

	public void setDrawIndex(int i) {
		drawIndex = i;

	}

	public boolean changeDepth(float before, float after) {
		if (this.islocked) {
			if (after - before < oldDistanceDelta) {
				if (Math.abs(after - before - oldDistanceDelta) > 2)
					if (radius + 0.2 < this.parent.getCamera().far)
						radius += 0.2;
			} else {
				if (Math.abs(after - before - oldDistanceDelta) > 2)
					if (radius - 0.2 > 0)
						radius -= 0.2;
			}
			oldDistanceDelta = after - before;
			return true;
		}
		return false;
	}

	public void setLock(boolean lock) {
		this.islocked = lock;
	}

	public boolean isLock() {
		return islocked;
	}

	public void setParent(Screen parent) {
		this.parent = parent;
	}

	public void setIndex(int i) {
		index = i;

	}

	public boolean isVisible() {
		return isvisible;
	}

	public void setVisibility(boolean visible) {
		this.isvisible = visible;
	}

	public double[] getPosition() {
		return new double[] { x, y, z };
	}

	public float[] getPositionf() { // ENU
		return new float[] { x, y, z };
	}

	public float[] getShowPositionf() {// Animation
		return new float[] { showX, showY, showZ };
	}

	public float[] getPositionForIntersectionf() {// ENU Translated To be Used
		return new float[] { virX, virY, virZ };
	}

	public float getRadius() {
		return radius;
	}

	public float getShowRadius() {
		return showRadius;
	}

	public float[] getAccModelView() {
		// TODO Auto-generated method stub
		return accModelView;
	}

	public int getDrawIndex() {
		return drawIndex;
	}

	public void draw(com.badlogic.gdx.graphics.Camera camera) {
		// TODO Auto-generated method stub
		
	}


}

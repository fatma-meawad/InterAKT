package com.interakt.ar.graphics;

import android.content.Context;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.interakt.ar.android.sensors.OrientationBased;

public class LibGDXOrthoCamera extends OrthographicCamera{

	OrientationBased orientation = new OrientationBased(1000,false); 
	
	float [] orientationMatrix;
	float [] lookVector;
	float [] upVector;

	private Object camera;
	
	public LibGDXOrthoCamera(Context context,float w, int h) {
		super(w,h);
		this.setToOrtho(true, w, h);
		orientation.start(context);
	}



	public void render(){
		this.update();
		//World Rotation
		this.apply(Gdx.gl10);
	}
	
	
	@Override
	public void update() {
		super.update();
		
	}

	@Override
	public void update(boolean updateFrustum) {
		super.update(updateFrustum);
		
	}
	
	public void dispose(){
		 orientation.finish(); 
	}
	
	public boolean getStability(){
		return orientation.getStable();
	}
}

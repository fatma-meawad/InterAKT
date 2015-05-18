package com.interakt.ar.graphics;

import android.content.Context;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.interakt.ar.android.sensors.OrientationBased;

public class LibGDXPerspectiveCamera extends PerspectiveCamera{

	public OrientationBased orientation;
	
	float [] orientationMatrix;
	float [] lookVector;
	float [] upVector;
	boolean geo ;


	
	public LibGDXPerspectiveCamera(Context context){
		super();
		orientation = new OrientationBased(1000,true);
		orientation.start(context);
		geo = false;
	}
	
	public LibGDXPerspectiveCamera(Context context,float f, int i, int j,boolean geo) {
		super(f,i,j);
		orientation = new OrientationBased(1000,!geo);
		orientation.start(context);
		this.geo =geo;
	}


	public void setPosition(float x,float y,float z){
		this.position.set(x, y, z);
		orientation.setLookAtOffset(x,y,z);
	}
	
	public void render(){
		
		//World Rotation
		if(geo){
		this.update();
		orientationMatrix = orientation.getMatrix();
//		Quaternion quat = new Quaternion();
//		quat.setFromMatrix(new Matrix4(orientationMatrix));
//		this.view.rotate(quat);
		//Camera Rotation
		upVector = orientation.getUp();
		this.up.set(upVector[0],upVector[1], upVector[2]);
		lookVector = orientation.getLookAt();
		this.lookAt(lookVector[0],lookVector[1],lookVector[2]);
		}
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
		 orientation = null;
		 orientationMatrix = null;
		 lookVector = null;
		 upVector = null;
	}

	public float[] getOrientationMatrix(){
		return orientationMatrix;
	}
	
	public float[] geUpVector(){
		return upVector;
	}
	
	public float[] getLookVector(){
		float [] out = new float[3];
		out[0] = lookVector[0];
		out[1] = lookVector[1];
		out[2] = lookVector[2];
		return out;
	}
	
	public boolean getStability(){
		return orientation.getStable();
	}
}

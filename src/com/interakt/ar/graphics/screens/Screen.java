package com.interakt.ar.graphics.screens;

import java.util.ArrayList;

import android.content.Context;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.input.GestureDetector;
import com.interakt.ar.graphics.Shape;


public abstract class Screen {

	protected ArrayList<Shape> shapes = new ArrayList<Shape>();
	protected ArrayList<Shape> toBeDrawed = new ArrayList<Shape>();
	protected ArrayList<Shape> drawn = new ArrayList<Shape>();
	protected Camera camera;
	protected GestureDetector gestureDetector;
	protected EventHandler eventHandler;
	
	protected Context context;
	public Screen(Context context) {
		this.context = context;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public abstract void init(int width,int height);
	public abstract void onSurfaceChanged(int width,int height);
	public abstract void dispose();

	public abstract void render();

	public EventHandler getEventHandler() {
		return eventHandler;
	}
	public GestureDetector getGestureDetector() {
		return gestureDetector;
	}
	public void addShape(Shape shape){
		shapes.add(shape);
		shape.setParent(this);
	}
	
	public void clearShapes(){
		shapes.clear();
	
	}
	
	public void clearToBeDrawed(){
		toBeDrawed.clear();
		
	}
	
	public void removeShape(Shape shape){
		shapes.remove(shape);

	}
	public Camera getCamera(){
		return camera;
	}
	public ArrayList<Shape> getShapes(){
		return shapes;
	}
	public ArrayList<Shape> getToBeDrawed(){
		return toBeDrawed;
	}
	public Context getContext(){
		return context;
	}

	public boolean isDrawn(Shape onMarkerShape) {
		
		return drawn.contains(onMarkerShape);
	}

}

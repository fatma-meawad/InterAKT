package com.interakt.ar.listeners;


import com.badlogic.gdx.math.Vector3;
import com.interakt.ar.graphics.Shape;

public interface OnShapeClickListener {

	public void click(Shape shape,Vector3 intersection);
	public void click(Shape shape);
	
}

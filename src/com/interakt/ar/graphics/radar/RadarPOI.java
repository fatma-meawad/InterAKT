package com.interakt.ar.graphics.radar;

public class RadarPOI {

	public float x;
	public float y;
	public boolean visible;
	public float [] normal = {1.0f,0.8f,0.0f};
	
	public float[] clicked = {1.0f,0.0f,0.0f};

	public boolean isClicked;
	
	
	public RadarPOI(float x ,float y){
		this.x = x;
		this.y = y;
		
	}
	
	public float[] getColor(){
		if(isClicked ==true){
			return clicked;
		}else{
			return normal;
		}
	}

	
	
}

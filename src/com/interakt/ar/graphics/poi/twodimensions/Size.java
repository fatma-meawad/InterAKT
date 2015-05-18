package com.interakt.ar.graphics.poi.twodimensions;

public class Size {

	private int width;
	private int height;
	
	public Size(int width,int height){
		this.width  = width;
		this.height  = height;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public void setWidth(int width){
		this.width = width;
	}
	public void setHeight(int height){
		this.height = height;
	}
}

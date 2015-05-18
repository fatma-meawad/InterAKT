package com.interakt.ar.graphics.poi.twodimensions;

import com.badlogic.gdx.math.Vector2;

public class Point {

	private int x;
	private int y;
	
	public Point(int x,int y){
		this.x  = x;
		this.y  = y;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public void setX(int x){
		this.x = x;
	}
	public void setY(int y){
		this.y = y;
	}
	public String toString(){
		return x+" "+y;
		
	}
	public int getDistanceTo(Point p){
		return (int) Math.sqrt((Math.pow(Math.abs(this.x-p.getX()),2))+(Math.pow(Math.abs(this.y-p.getY()),2)));
	}
	
	public Point getDirection(Point p){
		return new Point(this.x-p.getX(),this.y-p.getY());
	}
	public Vector2 toVector2(){
		return new Vector2 (this.x,this.y);
	}
}

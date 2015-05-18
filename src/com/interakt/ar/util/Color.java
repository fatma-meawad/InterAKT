package com.interakt.ar.util;

public class Color {

	public byte R;
	public byte G;
	public byte B;
	
	public Color(byte r,byte g,byte b){
		R= r;
		G= g;
		B= b;
	}
	
	@Override
	public boolean equals(Object o){
		
		if(R == ((Color)o).R && G == ((Color)o).G && B == ((Color)o).B){
			return true;
		}
		return false;
		
	}
	
	
}

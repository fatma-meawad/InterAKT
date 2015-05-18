package com.interakt.ar.graphics.poi;

import java.util.ArrayList;



import android.location.Location;

import com.badlogic.gdx.graphics.Pixmap;
import com.interakt.ar.graphics.radar.RadarPOI;
import com.interakt.ar.outdoor.browsing.BrowsingScreen;
import com.interakt.ar.util.LocationUtils;

public class POIHolder {
	// POI SERVER Attributes
	public String children;
	public String parent;
	public POI parentObj;
	public String id;
	public ArrayList<String> contains;
	public String isPartOf;
	public String model;
	public String rating; //---------------
	public String creator;
	public String image;
	public String category;
	public String presented_as;
	public String latitude;
	public String longitude;
	public String altitude;
	public String likes; //-----------------------
	public String source;
	public String email;
	public String status;
	public String sharing;
	public String description; //---------------------------
	public String tags; //-------------------------------
	public String phone; //-----------------
	public String address; //-------------------------
	public String attached_media;
	public String name;
	public String url;
	public String display_status;
	public String created_at;
	public String thumbnail;
	public String texture;
	public String textureLock;
	public Pixmap textureGen;
	public String userRate;
	public boolean userLike;
	public ArrayList<POI> childrenLst = new ArrayList<POI>();
	public ArrayList<CommentHolder> commentsHolder = new ArrayList<CommentHolder>();
	public String total_rates;
	public int level; 
	public RadarPOI radarPOI;
	public String numberOfComments;
	public String comments;
	public boolean isHighlighted;
	
	
	public static class CommentHolder{
		public String name;
		public String comment;
		public String time;
		
		public CommentHolder(String name, String comment,String time){
			this.time = time;
			this.name = name;
			this.comment = comment;
		}
		
	}
	
}
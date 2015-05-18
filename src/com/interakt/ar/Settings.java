package com.interakt.ar;

import com.badlogic.gdx.math.Matrix4;

public class Settings {

	
	public final static String url = "http://178.62.139.220/";
	public static double RADIUS_RANGE = 250.0;
	public static double INNER_RADIUS_RANGE = 3.0;
	public static double LAYER_NAME_MIN = 0.0;
	public static double LAYER_NAME_MAX = RADIUS_RANGE;
	public static double SHOWING_DISTANCE;
	public static double RADIUS_RANGE_PROGRESS = 0;
	public static double FOCAL_LENGTH_X = 0;
	public static double FOCAL_LENGTH_Y = 0;
	public static double FRAME_HEIGHT = 0;
	public static double FRAME_WIDTH = 0;
	public static double PRINCIPALE_POINT_X = 0;
	public static double PRINCIPALE_POINT_Y = 0;
	public static double FAR_PLANE = 3000;
	public static double NEAR_PLANE = 0.1;
	public static Matrix4 PROJECTION = new Matrix4();
	public static final int MAX_PROCESSING_ANGLE = 50;
	public static int ACTIVE_MODE;
	public static double FOV;
	public static double FOV_H;
	public static double FOV_V;
	public static final int ANGLE_FIELD_OF_VIEW = 30;
	public static String userID="";
	public static int LIMIT = 20;
	public static String TobeLogged = "";
}

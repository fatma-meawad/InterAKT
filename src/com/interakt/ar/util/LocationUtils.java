package com.interakt.ar.util;

import java.util.ArrayList;

public class LocationUtils {

	public static final double SEMI_MAJOR_AXIS = 6378137.0;
	public static final double FIRST_ECCENTRICITY_SQUARED = 6.69437999014 * Math
			.pow(10, -3);
	public static double EARTH_RADIUS = 3958.75;
	public static int METER_CONVERSION = 1609;

	/**
	 * 
	 * This method updates the ENU locations of a list of POIs upon user
	 * movement.
	 * 
	 * @param userLocationOld
	 *            lat/long/alt of user's original position
	 * @param userLocationNew
	 *            lat/long/alt of user's new position
	 * @param resLocationsENU
	 *            list of POI locations in ENU that need to be updated due to
	 *            user movement
	 * @return updated list of POI ENU locations
	 */
	public static double[] updateResLocations(double[] userLocationOld,
			double[] userLocationNew, double[] resLocationsENU) {
		double[] userLocationNewENU = GEOtoENU(userLocationOld, userLocationNew);
		double[] newResENU = MathUtils.getVectorDifference(resLocationsENU,
				userLocationNewENU);
		return newResENU;
	}

	/**
	 * This method converts Geodetic coordinates(latitude/longitude/altitude) to
	 * ENU coordinates aligned with the phone. The output of this method is the
	 * coordinate system to be used in openGL
	 * 
	 * @param userLocation
	 * @param resLocation
	 * @param rotMatrix
	 * @return
	 */
	public static double[] GEOtoENU(double[] userLocation, double[] resLocation) {

		double[] resLocationRadians = new double[] {
				Math.toRadians(resLocation[0]), Math.toRadians(resLocation[1]),
				resLocation[2] };
		double[] resLocationECEF = GEOtoECEF(resLocationRadians, resLocation[2]);

		double[] userLocationRadians = new double[] {
				Math.toRadians(userLocation[0]),
				Math.toRadians(userLocation[1]), userLocation[2] };

		double[] userLocationECEF = GEOtoECEF(userLocationRadians,
				userLocation[2]);
		double[] poiENU = ECEFtoENU(resLocationECEF, userLocationECEF,
				userLocationRadians);
		return poiENU;
	}

	/**
	 * This methods converts ENU coordinates aligned with the phone cooridnates
	 * system to Geodetic coordinate system (latitude/longitude/altitude). NB:
	 * The altitude obtained from this method should be ignored and replaced by
	 * that from a webservice that returns the altitude given a specific lat/lng
	 * combination
	 * 
	 * @param userLocation
	 * @param resLocationENU
	 * @param rotMatrix
	 * @return
	 */
	public static double[] ENUtoGEO(double[] userLocation,
			double[] resLocationENU) {

		double[] userLocationRadians = new double[] {
				Math.toRadians(userLocation[0]),
				Math.toRadians(userLocation[1]), userLocation[2] };

		double[] userLocationECEF = GEOtoECEF(userLocationRadians,
				userLocation[2]);

		double[] resLocationECEF = ENUtoECEF(resLocationENU, userLocationECEF,
				userLocationRadians);
		double[] resLocationGEO = ECEFToGEODegrees(resLocationECEF);
		return resLocationGEO;
	}

	/**
	 * Converts POI from ECEF to ENU coordinates.
	 * 
	 * @param POI
	 *            the POI in ECEF coordinates.
	 * @param location
	 *            the location of the phone in ECEF coordinates.
	 * @param locationRadians
	 *            the location array where location[0] (lat) and location[1]
	 *            (lng) are in radians in the geodetic coordinates.
	 * @return POI in ENU coordinates.
	 */
	public static double[] ECEFtoENU(double[] POI, double[] location,
			double[] locationRadians) {
		double[] POISecondMatrix = { POI[0] - location[0],
				POI[1] - location[1], POI[2] - location[2] };

		double[] POIENU = {
				-Math.sin(locationRadians[1]) * POISecondMatrix[0]
						+ Math.cos(locationRadians[1]) * POISecondMatrix[1],
				-Math.sin(locationRadians[0]) * Math.cos(locationRadians[1])
						* POISecondMatrix[0] + -Math.sin(locationRadians[0])
						* Math.sin(locationRadians[1]) * POISecondMatrix[1]
						+ Math.cos(locationRadians[0]) * POISecondMatrix[2],
				Math.cos(locationRadians[0]) * Math.cos(locationRadians[1])
						* POISecondMatrix[0] + Math.cos(locationRadians[0])
						* Math.sin(locationRadians[1]) * POISecondMatrix[1]
						+ Math.sin(locationRadians[0]) * POISecondMatrix[2] };

		return POIENU;
	}

	public static double[] ENUtoECEF(double[] poiENU, double[] gps_ecef,
			double[] userLocationRadians) {
		double[] bigmatrix = new double[9];
		bigmatrix[0] = -Math.sin(userLocationRadians[1]);
		bigmatrix[1] = -Math.sin(userLocationRadians[0])
				* Math.cos(userLocationRadians[1]);
		bigmatrix[2] = Math.cos(userLocationRadians[0])
				* Math.cos(userLocationRadians[1]);
		bigmatrix[3] = Math.cos(userLocationRadians[1]);
		bigmatrix[4] = -Math.sin(userLocationRadians[0])
				* Math.sin(userLocationRadians[1]);
		bigmatrix[5] = Math.cos(userLocationRadians[0])
				* Math.sin(userLocationRadians[1]);
		bigmatrix[6] = 0;
		bigmatrix[7] = Math.cos(userLocationRadians[0]);
		bigmatrix[8] = Math.sin(userLocationRadians[0]);

		double[] temp = new double[] {
				bigmatrix[0] * poiENU[0] + bigmatrix[1] * poiENU[1]
						+ bigmatrix[2] * poiENU[2],
				bigmatrix[3] * poiENU[0] + bigmatrix[4] * poiENU[1]
						+ bigmatrix[5] * poiENU[2],
				bigmatrix[6] * poiENU[0] + bigmatrix[7] * poiENU[1]
						+ bigmatrix[8] * poiENU[2] };
		double[] poiECEF = new double[] { temp[0] + gps_ecef[0],
				temp[1] + gps_ecef[1], temp[2] + gps_ecef[2] };
		return poiECEF;

	}

	/**
	 * Converts from geodetic to ECEF coordinates.
	 * 
	 * @param location
	 *            the location array where location[0] (lat) and location[1]
	 *            (lng) are in radians in the geodetic coordinates.
	 * @param height
	 *            the height in meters.
	 * @return the three components (lat, lng, height) in ECEF coordinates.
	 */
	public static double[] GEOtoECEF(double[] location, double height) {
		double nTheta = SEMI_MAJOR_AXIS
				/ Math.sqrt(1 - FIRST_ECCENTRICITY_SQUARED
						* Math.pow(Math.sin(location[0]), 2));

		double[] locationECEF = {
				(nTheta + height) * Math.cos(location[0])
						* Math.cos(location[1]),
				(nTheta + height) * Math.cos(location[0])
						* Math.sin(location[1]),
				((nTheta) * (1 - FIRST_ECCENTRICITY_SQUARED) + height)
						* Math.sin(location[0]) };

		return locationECEF;
	}

	public static double[] ECEFToGEODegrees(double[] ecef) {
		double[] poiGEORadians = LocationUtils.ECEFToGeoRadians(ecef);

		return new double[] { (double) Math.toDegrees(poiGEORadians[0]),
				(double) Math.toDegrees(poiGEORadians[1]), 0 };
	}

	/**
	 * Transforms ECEF coordinates to geodetic
	 * 
	 * @param POIecef
	 * @return
	 */
	public static double[] ECEFToGeoRadians(double[] POIecef) {
		int a = 6378137;
		double x = POIecef[0];
		double y = POIecef[1];
		double z = POIecef[2];

		double b = 6356752.3142;
		double e2 = 6.69437999014 * Math.pow(10, -3);
		double e12 = 6.73949674228 * Math.pow(10, -3);

		double p = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

		double Y = Math.atan(z * a / (p * b));

		double lat = Math.atan((z + e12 * b * Math.pow(Math.sin(Y), 3))
				/ (p - e2 * a * Math.pow(Math.cos(Y), 3)));

		double longitude = Math.atan(y / x);
		double N = a / Math.sqrt(1 - e2 * (Math.pow(Math.sin(lat), 2)));

		double alt = (p / Math.cos(lat)) - N;

		return new double[] { (double) lat, (double) longitude, (double) alt };
	}

	public static double distFrom(double[] r1, double[] r2) {
		double a = Math.sin(Math.toRadians(r1[0] - r2[0]) / 2)
				* Math.sin(Math.toRadians(r1[0] - r2[0]) / 2)
				+ Math.cos(Math.toRadians(r1[0]))
				* Math.cos(Math.toRadians(r2[0]))
				* Math.sin(Math.toRadians(r1[1] - r2[1]) / 2)
				* Math.sin(Math.toRadians(r1[1] - r2[1]) / 2);
		double dist = 2 * Math.asin(Math.sqrt(a)) * EARTH_RADIUS
				* METER_CONVERSION;

		return dist;
	}

	// public static void main(String[] args) {
	// double[] loc1 = { 29.986881, 31.4395 };
	// double[] loc2 = { 29.986925, 31.43963 };
	// double dist1 = distFrom(loc1, loc2);
	// double dist2 = distFromTest(loc1, loc2);
	// System.out.println(dist1);
	// System.out.println(dist2);
	// }
	//
	// public static double distFrom(double[] r1, double[] r2) {
	// //
	// // d=2*asin(sqrt((sin((lat1-lat2)/2))^2 +
	// // cos(lat1)*cos(lat2)*(sin((lon1-lon2)/2))^2))
	// double lat1 = r1[0];
	// double lng1 = r1[1];
	// double lat2 = r2[0];
	// double lng2 = r2[1];
	// double earthRadius = 3958.75;
	// double dLat = Math.toRadians(lat2 - lat1);
	// double dLng = Math.toRadians(lng2 - lng1);
	// double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
	// + Math.cos(Math.toRadians(lat1))
	// * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
	// * Math.sin(dLng / 2);
	// double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	// double dist = earthRadius * c;
	//
	// int meterConversion = 1609;
	//
	// return new Double(dist * meterConversion).doubleValue();
	//
	// }
	public static void test() {
		//
		// Location 1
		// poiENU [ 17.954450215141126 , -14.357508900376473 ,
		// -4.1478285141671734E-5 ]
		// Location 2
		// poiENU [ 51.15013152880888 , -40.629382249373165 ,
		// -3.3488322332431153E-4 ]
		double[] resLocation = { 29.986772480506563, 31.439782058265102, 0 };
		double[] userLocation2 = { 29.987139, 31.439252, 0 };
		double[] userLocation = { 29.986902, 31.439596, 0 };
		double[] rotMatrix = { 0.05721274, 0.86070436, -0.50588, 0.019338276,
				0.5056595, 0.86251634, 0.9981747, -0.059129775, 0.012285627 };

		double[] loc2ENU = GEOtoENU(userLocation, userLocation2);
		double[] resENU = GEOtoENU(userLocation, resLocation);
		double[] result = MathUtils.getVectorDifference(resENU, loc2ENU);
		ArrayUtils.printArr("result", result);
	}

	public static void test2() {
		// double[] rotMatrix = { 0.05721274, 0.86070436, -0.50588, 0.019338276,
		// 0.5056595, 0.86251634, 0.9981747, -0.059129775, 0.012285627 };
		//
		// double[] resLocation = { 29.986772480506563, 31.439782058265102, 0 };
		// double[] userLocation2 = { 29.987139, 31.439252, 0 };
		// double[] userLocation = { 29.986902, 31.439596, 0 };
		//
		double[] resLocation = { 29.987236512766792, 31.439565860276872, 0.0 };
		// basket
		double[] userLocationOld = { 29.987041, 31.439695, 0.0 };
		double[] userLocationNew = { 29.987139, 31.439252, 0 };
		double[] rotMatrix = { -0.06176547, -0.86404616, 0.49960914,
				-0.024982102, -0.49906966, -0.86620164, 0.99777794,
				-0.06598264, 0.009239583 };
		ArrayList<double[]> resLocationsENU = new ArrayList<double[]>();
		double[] resENU = GEOtoENU(userLocationOld, resLocation);
		double[] newresENU = GEOtoENU(userLocationNew, resLocation);
		ArrayUtils.printArr("org ENU", newresENU);
		resLocationsENU.add(resENU);
		
	}

}

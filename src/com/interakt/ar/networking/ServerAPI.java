package com.interakt.ar.networking;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.interakt.ar.Settings;
import com.interakt.ar.networking.ServerAPI.ResponseHolder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ServerAPI {

	public static ArrayList<String> getPoiByRadius(String username,
			double latitude, double longitude, double radius) {
		System.out.println("["+username+" "+latitude+" "+longitude+" "+radius+"]" );
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String responsePOIsString = "";
		ArrayList<String> out = new ArrayList<String>();
		HttpPost httppost = new HttpPost(
				Settings.url+"get/poisbyradius/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(8);
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("radius", radius + ""));
		nameValuePairs.add(new BasicNameValuePair("latitude", latitude + ""));
		nameValuePairs.add(new BasicNameValuePair("longitude", longitude + ""));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			responsePOIsString = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(POIsStatusCode);
		if (POIsStatusCode == 200) {
			
			JSONObject POIsJSONObject;
			try {
				POIsJSONObject = new JSONObject(responsePOIsString);
				JSONArray POIsJSONArray = new JSONArray(
						POIsJSONObject.getString("pois"));
				for (int i = 0; i < POIsJSONArray.length(); i++) {
					String s = (String) POIsJSONArray.get(i);
					out.add(s);
					// categoriesMap.put(s, 0);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println(out);
		return out;
	}

	public static ArrayList<String> discoverPoiByRadius(String username,
			double latitude, double longitude, double radius) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String responsePOIsString = "";
		ArrayList<String> out = new ArrayList<String>();
		HttpPost httppost = new HttpPost(
				Settings.url+"discover/pois/");

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(8);
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("radius", radius + ""));
		nameValuePairs.add(new BasicNameValuePair("latitude", latitude + ""));
		nameValuePairs.add(new BasicNameValuePair("longitude", longitude + ""));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			responsePOIsString = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (POIsStatusCode == 200) {
			JSONObject POIsJSONObject;
			try {
				POIsJSONObject = new JSONObject(responsePOIsString);
				JSONArray POIsJSONArray = new JSONArray(
						POIsJSONObject.getString("pois"));
				for (int i = 0; i < POIsJSONArray.length(); i++) {
					String s = (String) POIsJSONArray.get(i);
					out.add(s);
					// categoriesMap.put(s, 0);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println(out);
		return out;
	}

	public static ArrayList<String> getPoiByRadiusLimit(double latitude,
			double longitude, double radius, int limit) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String responsePOIsString = "";
		ArrayList<String> out = new ArrayList<String>();
		HttpPost httppost = new HttpPost(
				Settings.url+"get/poisbyradiuslimit/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(8);
		nameValuePairs.add(new BasicNameValuePair("radius", radius + ""));
		nameValuePairs.add(new BasicNameValuePair("latitude", latitude + ""));
		nameValuePairs.add(new BasicNameValuePair("longitude", longitude + ""));
		nameValuePairs.add(new BasicNameValuePair("limit", limit + ""));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			responsePOIsString = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (POIsStatusCode == 200) {
			JSONObject POIsJSONObject;
			try {
				POIsJSONObject = new JSONObject(responsePOIsString);
				JSONArray POIsJSONArray = new JSONArray(
						POIsJSONObject.getString("pois"));
				for (int i = 0; i < POIsJSONArray.length(); i++) {
					String s = (String) POIsJSONArray.get(i);
					out.add(s);
					// categoriesMap.put(s, 0);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return out;
	}

	public static String getPOI(String id) {
		System.out.println(id);
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String responsePOIsString = "";
		String out = "";
		HttpPost httppost = new HttpPost(Settings.url+"get/poi");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("id", id));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			responsePOIsString = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.print(POIsStatusCode);
		if (POIsStatusCode == 200) {
			System.out.println(responsePOIsString);
			out = responsePOIsString;

		}
		return out;
	}
	
	
	

	public static boolean addPOI(double latitude, double longitude,
			String name, String parent, String children, String description,
			String category, String source, String username) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responseAddPOI;
		int POIsStatusCode = 0;
		HttpPost httppost = new HttpPost(
				Settings.url+"create/poi/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(18);
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("name", name));
		nameValuePairs.add(new BasicNameValuePair("description", description));
		nameValuePairs.add(new BasicNameValuePair("source", source));
		nameValuePairs.add(new BasicNameValuePair("category", category));
		nameValuePairs.add(new BasicNameValuePair("latitude", latitude + ""));
		nameValuePairs.add(new BasicNameValuePair("longitude", longitude + ""));
		nameValuePairs.add(new BasicNameValuePair("parent_poi", parent));
		nameValuePairs.add(new BasicNameValuePair("children_pois", children));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responseAddPOI = httpclient.execute(httppost);
			POIsStatusCode = responseAddPOI.getStatusLine().getStatusCode();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(POIsStatusCode);
		if (POIsStatusCode == 200) {
			System.out.println("SUCESS");
			return true;
		}
		return false;
	}

	public static void addMarker(String latitude, String longitude,
			String title, String discriptors) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				Settings.url+"create/marker/");

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(8);
			nameValuePairs.add(new BasicNameValuePair("title", title));
			nameValuePairs.add(new BasicNameValuePair("discriptors",
					discriptors));
			nameValuePairs.add(new BasicNameValuePair("latitude", latitude));
			nameValuePairs.add(new BasicNameValuePair("longitude", longitude));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			System.out.println(response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == 200) {
				System.out.println("SUCESS");
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	// public static void addPOI(double latitude,double longitude,String name){
	// HttpClient httpclient = new DefaultHttpClient();
	// HttpResponse responseAddPOI;
	// int POIsStatusCode =0;
	// HttpPost httppost = new
	// HttpPost(Settings.url+"create/poi/");
	// List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(10);
	// nameValuePairs.add(new BasicNameValuePair("name", name));
	// nameValuePairs.add(new BasicNameValuePair("description",
	// "This is a test"));
	// nameValuePairs.add(new BasicNameValuePair("category", "arbrowser"));
	// nameValuePairs.add(new BasicNameValuePair("latitude", latitude+ ""));
	// nameValuePairs.add(new BasicNameValuePair("longitude", longitude + ""));
	// try {
	// httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	// responseAddPOI = httpclient.execute(httppost);
	// POIsStatusCode = responseAddPOI.getStatusLine().getStatusCode();
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// if(POIsStatusCode == 200){
	// System.out.println("SUCESS");
	// }
	// }

	public static void submitLog(String username, String log) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responseAddPOI;
		int statusCode = 0;
		HttpPost httppost = new HttpPost(
				Settings.url+"post/user_interaction_log/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("log", log));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responseAddPOI = httpclient.execute(httppost);
			statusCode = responseAddPOI.getStatusLine().getStatusCode();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("LOG" + statusCode);
		if (statusCode == 200) {
			System.out.println("Log_SUCESS");
		}
	}

	public static void LoggdIn(String username) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responseAddPOI;
		int statusCode = 0;
		HttpPost httppost = new HttpPost(
				Settings.url+"app/update_user_profile/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("username", username));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responseAddPOI = httpclient.execute(httppost);
			statusCode = responseAddPOI.getStatusLine().getStatusCode();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("LOG" + statusCode);
		if (statusCode == 200) {
			System.out.println("LogIn_SUCESS");
		}
	}

	public static ArrayList<String> getCategories() {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responseCategories;
		int categoriesStatusCode = 0;
		String responseCategoriesString = "";
		ArrayList<String> out = new ArrayList<String>();
		HttpGet httpgetCategories = new HttpGet(
				Settings.url+"get/categories");
		try {
			responseCategories = httpclient.execute(httpgetCategories);
			categoriesStatusCode = responseCategories.getStatusLine()
					.getStatusCode();
			try {
				responseCategoriesString = EntityUtils
						.toString(responseCategories.getEntity());
			} catch (ParseException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (categoriesStatusCode == 200) {
			JSONObject object;
			try {
				object = new JSONObject(responseCategoriesString);
				JSONArray Jarray = object.getJSONArray("categories");
				for (int i = 0; i < Jarray.length(); i++) {
					String s = (String) Jarray.get(i);
					out.add(s);
					// categoriesMap.put(s, 0);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return out;
	}

	public static ArrayList<ArrayList<String>> getCategories(String username) {
		System.out.print(">>>>>>>" + username);
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 30000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		int timeoutSocket = 30000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responseCategories;
		int categoriesStatusCode = 0;
		String responseCategoriesString = "";
		ArrayList<ArrayList<String>> out = new ArrayList<ArrayList<String>>();
		HttpPost httppost = new HttpPost(
				Settings.url+"get/categories");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

		nameValuePairs.add(new BasicNameValuePair("username", username));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responseCategories = httpclient.execute(httppost);
			categoriesStatusCode = responseCategories.getStatusLine()
					.getStatusCode();
			try {
				responseCategoriesString = EntityUtils
						.toString(responseCategories.getEntity());
			} catch (ParseException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("XXXXX" + categoriesStatusCode);
		if (categoriesStatusCode == 200) {
			JSONObject object;
			try {
				object = new JSONObject(responseCategoriesString);
				JSONArray Jarray1 = object.getJSONArray("categories");
				for (int i = 0; i < Jarray1.length(); i++) {
					JSONArray Jarray2 = Jarray1.getJSONArray(i);
					ArrayList<String> category = new ArrayList<String>();
					for (int j = 0; j < Jarray2.length(); j++) {
						category.add((String) Jarray2.get(j));
					}
					out.add(category);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return out;
	}

	public static ArrayList<String> getSources() {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responseCategories;
		int categoriesStatusCode = 0;
		String responseCategoriesString = "";
		ArrayList<String> out = new ArrayList<String>();
		HttpGet httpgetCategories = new HttpGet(
				Settings.url+"get/sources");
		try {
			responseCategories = httpclient.execute(httpgetCategories);
			categoriesStatusCode = responseCategories.getStatusLine()
					.getStatusCode();
			try {
				responseCategoriesString = EntityUtils
						.toString(responseCategories.getEntity());
			} catch (ParseException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (categoriesStatusCode == 200) {
			JSONObject object;
			try {
				object = new JSONObject(responseCategoriesString);
				JSONArray Jarray = object.getJSONArray("sources");
				for (int i = 0; i < Jarray.length(); i++) {
					String s = (String) Jarray.get(i);
					out.add(s);
					// categoriesMap.put(s, 0);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return out;
	}

	public static void addPOI(double latitude, double longitude, String name) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responseAddPOI;
		int POIsStatusCode = 0;
		HttpPost httppost = new HttpPost(
				Settings.url+"create/poi/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(10);
		nameValuePairs.add(new BasicNameValuePair("name", name));
		nameValuePairs.add(new BasicNameValuePair("description",
				"This is a test"));
		nameValuePairs.add(new BasicNameValuePair("category", "arbrowser"));
		nameValuePairs.add(new BasicNameValuePair("latitude", latitude + ""));
		nameValuePairs.add(new BasicNameValuePair("longitude", longitude + ""));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responseAddPOI = httpclient.execute(httppost);
			POIsStatusCode = responseAddPOI.getStatusLine().getStatusCode();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (POIsStatusCode == 200) {
			System.out.println("SUCESS");
		}
	}

	public static String checkUserPoiState(String userID, String POIID) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String responsePOIsString = "";
		String out = "";
		HttpPost httppost = new HttpPost(
				Settings.url+"poi/stats");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
		nameValuePairs.add(new BasicNameValuePair("username", userID));
		nameValuePairs.add(new BasicNameValuePair("poi", POIID));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			responsePOIsString = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (POIsStatusCode == 200) {
			System.out.println(responsePOIsString);
			out = responsePOIsString;

		}
		System.out.println(POIsStatusCode);
		return out;
	}

	public static ResponseHolder ratePOI_Marker(String userID, String type,
			String POIID, String rate) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				Settings.url+"arbrowser/ransac");
		ResponseHolder output = new ResponseHolder();
		output.success = false;
		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(8);
			nameValuePairs.add(new BasicNameValuePair("username", userID));
			nameValuePairs.add(new BasicNameValuePair("type", type));
			nameValuePairs.add(new BasicNameValuePair("poi", POIID));
			nameValuePairs.add(new BasicNameValuePair("rate", rate));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request

			HttpResponse response = httpclient.execute(httppost);

			if (response.getStatusLine().getStatusCode() == 200) {
				output.success = true;
			} else {
				System.out.println(response.getStatusLine().getStatusCode());
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e);
		}
		return output;
	}

	public static void createApplication(String name, String category,
			String userID, String image) {
		HttpClient httpclient = new DefaultHttpClient();
		System.out.println("Wslna el server " + name);
		HttpPost httppost = new HttpPost(
				Settings.url+"/app/create_app/");

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(8);
			nameValuePairs.add(new BasicNameValuePair("name", name));
			nameValuePairs.add(new BasicNameValuePair("category", category));
			nameValuePairs.add(new BasicNameValuePair("userID", userID));
			nameValuePairs.add(new BasicNameValuePair("image", image));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			System.out.println("Creation details " + userID + " " + category
					+ " " + name + "" + image);
			HttpResponse response = httpclient.execute(httppost);
			System.out.println(response);
			System.out.println("ASUCESS");

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static ArrayList<String> getApps(String userID) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String responsePOIsString = "";
		ArrayList<String> out = new ArrayList<String>();
		System.out.println("Apps for " + userID);
		HttpPost httppost = new HttpPost(
				Settings.url+"/app/fetch_apps/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("userID", userID));

		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			responsePOIsString = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (POIsStatusCode == 200) {
			JSONObject POIsJSONObject;
			try {
				POIsJSONObject = new JSONObject(responsePOIsString);
				System.out.println("Apps in server " + responsePOIsString);
				JSONArray POIsJSONArray = new JSONArray(
						POIsJSONObject.getString("apps"));
				for (int i = 0; i < POIsJSONArray.length(); i++) {
					String s = (String) POIsJSONArray.get(i);
					out.add(s);
					// categoriesMap.put(s, 0);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return out;
	}

	public static ArrayList<String> getAppsID(String userID) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String responsePOIsString = "";
		ArrayList<String> out = new ArrayList<String>();
		System.out.println("Apps IDS for " + userID);
		HttpPost httppost = new HttpPost(
				Settings.url+"/app/fetch_apps_id/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("userID", userID));

		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			responsePOIsString = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (POIsStatusCode == 200) {
			JSONObject POIsJSONObject;
			try {
				POIsJSONObject = new JSONObject(responsePOIsString);
				System.out.println("POIs in server " + responsePOIsString);
				JSONArray POIsJSONArray = new JSONArray(
						POIsJSONObject.getString("appIDs"));
				for (int i = 0; i < POIsJSONArray.length(); i++) {
					String s = (String) POIsJSONArray.get(i);
					out.add(s);
					// categoriesMap.put(s, 0);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return out;
	}

	public static ArrayList<String> getMarkers(String userID) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String responsePOIsString = "";
		ArrayList<String> out = new ArrayList<String>();
		HttpPost httppost = new HttpPost(
				Settings.url+"app/fetch_all_markers/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("username", userID));

		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			responsePOIsString = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (POIsStatusCode == 200) {
			JSONObject POIsJSONObject;
			try {
				POIsJSONObject = new JSONObject(responsePOIsString);
				System.out.println("Markers in server " + responsePOIsString);
				JSONArray POIsJSONArray = new JSONArray(
						POIsJSONObject.getString("markers"));
				for (int i = 0; i < POIsJSONArray.length(); i++) {
					String s = (String) POIsJSONArray.getString(i);
					out.add(s);
					// categoriesMap.put(s, 0);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return out;
	}

	public static ArrayList<String> getMarkers_by_Radius(String latitude,
			String longitude, String radius) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String responsePOIsString = "";
		ArrayList<String> out = new ArrayList<String>();
		HttpPost httppost = new HttpPost(
				Settings.url+"app/get_markers_by_radius/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
		nameValuePairs.add(new BasicNameValuePair("latitude", latitude));
		nameValuePairs.add(new BasicNameValuePair("longitude", longitude));
		nameValuePairs.add(new BasicNameValuePair("radius", radius));

		System.out.println("Details are " + "lat" + latitude + " lng"
				+ longitude + " radius" + radius);

		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			responsePOIsString = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (POIsStatusCode == 200) {
			JSONObject POIsJSONObject;
			try {
				POIsJSONObject = new JSONObject(responsePOIsString);
				System.out.println("Markers in server " + responsePOIsString);
				JSONArray POIsJSONArray = new JSONArray(
						POIsJSONObject.getString("markers"));
				for (int i = 0; i < POIsJSONArray.length(); i++) {
					String s = (String) POIsJSONArray.get(i);
					out.add(s);
					// categoriesMap.put(s, 0);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return out;
	}

	public static ArrayList<String> getAllMarkersIDs(String userID) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String responsePOIsString = "";
		ArrayList<String> out = new ArrayList<String>();
		HttpPost httppost = new HttpPost(
				Settings.url+"/app/fetch_all_markers_by_id/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("userID", userID));

		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			responsePOIsString = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (POIsStatusCode == 200) {
			JSONObject POIsJSONObject;
			try {
				POIsJSONObject = new JSONObject(responsePOIsString);
				System.out.println("IDs in Server " + responsePOIsString);
				JSONArray POIsJSONArray = new JSONArray(
						POIsJSONObject.getString("markerIDs"));
				for (int i = 0; i < POIsJSONArray.length(); i++) {
					String s = (String) POIsJSONArray.get(i);
					out.add(s);
					// categoriesMap.put(s, 0);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return out;
	}

	public static boolean createMarker(String name, String markerName,
			String rate, String comment, String location, String userID,
			String image, String descriptor, double latitude, double longitude,
			String category, String source, String parent, String description) {
		HttpClient httpclient = new DefaultHttpClient();
		System.out.println("Wslna el server " + name);
		HttpPost httppost = new HttpPost(
				Settings.url+"app/create_marker/");

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
					28);
			nameValuePairs.add(new BasicNameValuePair("name", name));
			nameValuePairs
					.add(new BasicNameValuePair("markerName", markerName));
			nameValuePairs.add(new BasicNameValuePair("rate", rate));
			nameValuePairs.add(new BasicNameValuePair("comment", comment));
			nameValuePairs.add(new BasicNameValuePair("location", location));
			nameValuePairs.add(new BasicNameValuePair("userID", userID));
			nameValuePairs.add(new BasicNameValuePair("image", image));
			nameValuePairs
					.add(new BasicNameValuePair("descriptor", descriptor));
			nameValuePairs.add(new BasicNameValuePair("description",
					description));
			nameValuePairs.add(new BasicNameValuePair("parent", parent));
			nameValuePairs.add(new BasicNameValuePair("source", source));
			nameValuePairs.add(new BasicNameValuePair("category", category));
			nameValuePairs
					.add(new BasicNameValuePair("latitude", latitude + ""));
			nameValuePairs.add(new BasicNameValuePair("longitude", longitude
					+ ""));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			System.out.println("Marker Info in Server: " + name + " "
					+ markerName + " " + rate + " " + comment);
			System.out.println("Name " + name);
			System.out.println("Marker " + markerName);
			// Execute HTTP Post Request
			System.out.println("BSUCESS");
			HttpResponse response = httpclient.execute(httppost);
			System.out.println(response.getStatusLine().getStatusCode() + " "
					+ response.toString());

			System.out.println("ASUCESS");
			if (response.getStatusLine().getStatusCode() != 200) {
				return false;
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			return false;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		return true;
	}

	public static String attachInfo(String title, String helpText, String type,
			String markerID, String answer1, String answer2, String answer3,
			String answer4, String userID, String correctAnswer, String message) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response = null;
		String r = "";
		System.out.println("Wslna el server " + title);
		HttpPost httppost = new HttpPost(
				Settings.url+"/app/attach_info/");

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
					22);
			nameValuePairs.add(new BasicNameValuePair("title", title));
			nameValuePairs.add(new BasicNameValuePair("helpText", helpText));
			nameValuePairs.add(new BasicNameValuePair("type", type));
			nameValuePairs.add(new BasicNameValuePair("id", markerID));
			nameValuePairs.add(new BasicNameValuePair("answer1", answer1));
			nameValuePairs.add(new BasicNameValuePair("answer2", answer2));
			nameValuePairs.add(new BasicNameValuePair("answer3", answer3));
			nameValuePairs.add(new BasicNameValuePair("answer4", answer4));
			nameValuePairs.add(new BasicNameValuePair("userID", userID));
			nameValuePairs.add(new BasicNameValuePair("correctAnswer",
					correctAnswer));
			nameValuePairs.add(new BasicNameValuePair("message", message));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			System.out.println("Name " + title);
			System.out.println("Marker ID " + markerID);
			System.out.println("Type " + type);
			System.out.println("Help " + helpText);
			System.out.println("Ans1 " + answer1);
			System.out.println("Ans2 " + answer2);
			System.out.println("Ans3 " + answer3);
			System.out.println("Ans4 " + answer4);
			// Execute HTTP Post Request
			System.out.println("BSUCESS");
			response = httpclient.execute(httppost);
			r = EntityUtils.toString(response.getEntity());
			System.out.println(r);
			System.out.println("ASUCESS");

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e);
		}
		return r;
	}

	public static ArrayList<String> fetchMarker(String name, String userID) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String responsePOIsString = "";
		System.out.println("byfetch markers bta3t " + name);
		ArrayList<String> out = new ArrayList<String>();
		HttpPost httppost = new HttpPost(
				Settings.url+"/app/fetch_markers/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
		nameValuePairs.add(new BasicNameValuePair("name", name + ""));
		nameValuePairs.add(new BasicNameValuePair("userID", userID + ""));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			responsePOIsString = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
			System.out.println("Code " + POIsStatusCode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// if (POIsStatusCode == 200) {
		JSONObject POIsJSONObject;
		System.out.println("Success");
		System.out.println(responsePOIsString);
		try {
			POIsJSONObject = new JSONObject(responsePOIsString);
			JSONArray POIsJSONArray = new JSONArray(
					POIsJSONObject.getString("markers"));
			for (int i = 0; i < POIsJSONArray.length(); i++) {
				String s = (String) POIsJSONArray.get(i);
				out.add(s);
				System.out.println(s);
				// categoriesMap.put(s, 0);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// }
		System.out.println("Fetched in ServerAPI");
		return out;
	}

	public static String fetchMarkerID(String markerName, String app,
			String userID) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		System.out.println("getting id for " + markerName);
		String id = "";
		HttpPost httppost = new HttpPost(
				Settings.url+"/app/fetch_marker_id/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
		nameValuePairs.add(new BasicNameValuePair("name", app + ""));
		nameValuePairs
				.add(new BasicNameValuePair("markerName", markerName + ""));
		nameValuePairs.add(new BasicNameValuePair("userID", userID + ""));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			id = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
			System.out.println("Code " + POIsStatusCode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// if (POIsStatusCode == 200) {
		System.out.println("Success");
		System.out.println(id);

		System.out.println("Fetched in ServerAPI");
		return id;
	}

	public static void deleteApp(String name, String userID) {
		HttpClient httpclient = new DefaultHttpClient();
		System.out.println("Wslna el server " + name);
		HttpPost httppost = new HttpPost(
				Settings.url+"/app/delete_app/");

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
			nameValuePairs.add(new BasicNameValuePair("name", name));
			nameValuePairs.add(new BasicNameValuePair("userID", userID));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			System.out.println("BSUCESS");
			HttpResponse response = httpclient.execute(httppost);
			System.out.println(response);
			System.out.println("ASUCESS");

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void deleteMarker(String markerID, String userID) {
		HttpClient httpclient = new DefaultHttpClient();
		System.out.println("Wslna el server " + markerID);
		HttpPost httppost = new HttpPost(
				Settings.url+"/app/delete_marker/");

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
			nameValuePairs.add(new BasicNameValuePair("id", markerID));
			nameValuePairs.add(new BasicNameValuePair("userID", userID));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			System.out.println("BSUCESS");
			HttpResponse response = httpclient.execute(httppost);
			System.out.println(response);
			System.out.println("ASUCESS");

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static ArrayList<String> fetchInfo(String markerID, String userID) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String responsePOIsString = "";
		System.out.println("fetching info attached to id# " + markerID);
		ArrayList<String> out = new ArrayList<String>();
		HttpPost httppost = new HttpPost(
				Settings.url+"/app/fetch_attached_info/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
		nameValuePairs.add(new BasicNameValuePair("markerID", markerID + ""));
		nameValuePairs.add(new BasicNameValuePair("userID", userID + ""));

		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			responsePOIsString = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
			System.out.println("Code for attached info " + POIsStatusCode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (POIsStatusCode == 200) {
			System.out.println("takbeeer");
			JSONObject POIsJSONObject;
			try {
				POIsJSONObject = new JSONObject(responsePOIsString);
				System.out.println("Response " + responsePOIsString);
				JSONArray POIsJSONArray = new JSONArray(
						POIsJSONObject.getString("info"));
				System.out.println("Array " + POIsJSONArray);
				for (int i = 0; i < POIsJSONArray.length(); i++) {
					String s = (String) POIsJSONArray.get(i);
					out.add(s);
					// categoriesMap.put(s, 0);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println("Fetched in ServerAPI");
		System.out.println("Out to update " + out);
		return out;
	}

	public static ArrayList<String> fetchMarkerInfo(String id, String userID) {
		// comment and rate only
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String responsePOIsString = "";
		System.out.println("id to be sent " + id);
		ArrayList<String> out = new ArrayList<String>();
		HttpPost httppost = new HttpPost(
				Settings.url+"/app/fetch_marker_details/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
		nameValuePairs.add(new BasicNameValuePair("id", id + ""));
		nameValuePairs.add(new BasicNameValuePair("userID", userID + ""));

		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			responsePOIsString = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
			System.out.println("Code " + POIsStatusCode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (POIsStatusCode == 200) {
			JSONObject POIsJSONObject;
			try {
				POIsJSONObject = new JSONObject(responsePOIsString);
				JSONArray POIsJSONArray = new JSONArray(
						POIsJSONObject.getString("info"));
				for (int i = 0; i < POIsJSONArray.length(); i++) {
					String s = (String) POIsJSONArray.get(i);
					out.add(s);
					// categoriesMap.put(s, 0);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println("Fetched in ServerAPI");
		System.out.println("Comment and rate to update " + out);
		return out;
	}

	public static String checkAppDuplicates(String name, String userID) {
		// TODO Auto-generated method stub
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String response = "";
		System.out.println("duplicates for " + name);
		HttpPost httppost = new HttpPost(
				Settings.url+"/app/check_app_duplicates/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
		nameValuePairs.add(new BasicNameValuePair("name", name));
		nameValuePairs.add(new BasicNameValuePair("userID", userID));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			response = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
			System.out.println("Code " + POIsStatusCode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Success");
		System.out.println("App Duplicates " + response);
		System.out.println("Fetched in ServerAPI");
		return response;
	}

	public static String checkMarkerDuplicates(String app, String markerName,
			String userID) {
		// TODO Auto-generated method stub
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String response = "";
		HttpPost httppost = new HttpPost(
				Settings.url+"/app/check_marker_duplicates/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
		nameValuePairs.add(new BasicNameValuePair("name", app));
		nameValuePairs.add(new BasicNameValuePair("markerName", markerName));
		nameValuePairs.add(new BasicNameValuePair("userID", userID));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			response = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
			System.out.println("Code " + POIsStatusCode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Success");
		System.out.println("Duplicates " + response);
		System.out.println("Fetched in ServerAPI");
		return response;
	}

	public static String checkQuestionsDuplicates(String markerID, String title) {
		// TODO Auto-generated method stub
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String response = "";
		System.out.println("Checking for " + markerID + " and " + "title");
		HttpPost httppost = new HttpPost(
				Settings.url+"/app/check_questions_duplicates/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
		nameValuePairs.add(new BasicNameValuePair("markerID", markerID));
		nameValuePairs.add(new BasicNameValuePair("title", title));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			response = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
			System.out.println("Code " + POIsStatusCode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Success");
		System.out.println("Title response " + response);
		System.out.println("Response in ServerAPI");
		return response;
	}

	public static void updateMarker(String markerID, String oldName,
			String name, String markerName, String rate, String comment,
			String selectedLocation, String userID, String image,
			String descriptor) {
		// TODO Auto-generated method stub
		HttpClient httpclient = new DefaultHttpClient();
		System.out.println("Wslna el server " + name);
		HttpPost httppost = new HttpPost(
				Settings.url+"/app/update_marker/");

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
					20);
			nameValuePairs.add(new BasicNameValuePair("id", markerID + ""));
			nameValuePairs.add(new BasicNameValuePair("oldName", oldName));
			nameValuePairs.add(new BasicNameValuePair("name", name));
			nameValuePairs
					.add(new BasicNameValuePair("markerName", markerName));
			nameValuePairs.add(new BasicNameValuePair("rate", rate));
			nameValuePairs.add(new BasicNameValuePair("comment", comment));
			nameValuePairs.add(new BasicNameValuePair("location",
					selectedLocation));
			nameValuePairs.add(new BasicNameValuePair("userID", userID));
			nameValuePairs.add(new BasicNameValuePair("image", image));
			nameValuePairs
					.add(new BasicNameValuePair("descriptor", descriptor));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			System.out.println("Marker Update in Server: " + oldName + " "
					+ name + " " + markerName + " " + rate + " " + comment
					+ " " + selectedLocation + " " + descriptor);
			System.out.println("Name" + name);
			System.out.println("Marker" + markerName);
			// Execute HTTP Post Request
			System.out.println("BSUCESS");
			HttpResponse response = httpclient.execute(httppost);
			System.out.println(response);
			System.out.println("ASUCESS");

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	public static String register(String user, String pass, String twitter) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String response = "";
		HttpPost httppost = new HttpPost(
				Settings.url+"app/register/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
		nameValuePairs.add(new BasicNameValuePair("username", user));
		nameValuePairs.add(new BasicNameValuePair("password", pass));
		nameValuePairs.add(new BasicNameValuePair("twitter", twitter));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			response = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
			System.out.println("Code " + POIsStatusCode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	
	public static String editMarkerDescription(String id, String description) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String response = "";
		HttpPost httppost = new HttpPost(
				Settings.url+"app/edit_marker_desc/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
		nameValuePairs.add(new BasicNameValuePair("id", id));
		nameValuePairs.add(new BasicNameValuePair("desc", description));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			response = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
			System.out.println("Code " + POIsStatusCode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
	
	
	public static String login(String user, String pass) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String response = "";
		HttpPost httppost = new HttpPost(
				Settings.url+"app/login/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
		nameValuePairs.add(new BasicNameValuePair("username", user));
		nameValuePairs.add(new BasicNameValuePair("password", pass));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			response = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
			System.out.println("Code " + POIsStatusCode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	public static String getUserID(String user) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String response = "";
		HttpPost httppost = new HttpPost(
				Settings.url+"app/get_user_id/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
		nameValuePairs.add(new BasicNameValuePair("username", user));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			response = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
			System.out.println("Code " + POIsStatusCode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Success");
		System.out.println("User and Pass " + response);
		System.out.println("Fetched in ServerAPI");
		return response;
	}

	public static void getLatLongFromAddress(String youraddress) {

		HttpGet httpGet = new HttpGet(
				"http://maps.google.com/maps/api/geocode/json?address="
						+ youraddress + "&sensor=false");
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		StringBuilder stringBuilder = new StringBuilder();
		double lng, lat = 0;

		try {
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(stringBuilder.toString());

			lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lng");

			lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lat");

			System.out.println("latitude in server" + lat);
			System.out.println("longitude" + lng);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static ArrayList<String> AllMarkers(String name) {
		// comment and rate only
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String responsePOIsString = "";
		ArrayList<String> out = new ArrayList<String>();
		HttpPost httppost = new HttpPost(
				Settings.url+"/app/get_all_markers/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("name", name + ""));

		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			responsePOIsString = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
			System.out.println("Code " + POIsStatusCode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (POIsStatusCode == 200) {
			JSONObject POIsJSONObject;
			try {
				POIsJSONObject = new JSONObject(responsePOIsString);
				JSONArray POIsJSONArray = new JSONArray(
						POIsJSONObject.getString("markers"));
				for (int i = 0; i < POIsJSONArray.length(); i++) {
					String s = (String) POIsJSONArray.get(i);
					out.add(s);
					// categoriesMap.put(s, 0);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println("Fetched in ServerAPI");
		System.out.println("Comment and rate to update " + out);
		return out;
	}

	public static ArrayList<String> getAllApps() {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responseCategories;
		int categoriesStatusCode = 0;
		String responseCategoriesString = "";
		ArrayList<String> out = new ArrayList<String>();
		HttpGet httpgetCategories = new HttpGet(
				Settings.url+"/app/get_all_apps/");
		try {
			responseCategories = httpclient.execute(httpgetCategories);
			categoriesStatusCode = responseCategories.getStatusLine()
					.getStatusCode();
			try {
				responseCategoriesString = EntityUtils
						.toString(responseCategories.getEntity());
			} catch (ParseException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("Categories Code " + categoriesStatusCode);
		if (categoriesStatusCode == 200) {
			JSONObject object;
			try {
				object = new JSONObject(responseCategoriesString);
				JSONArray Jarray = object.getJSONArray("apps");
				for (int i = 0; i < Jarray.length(); i++) {
					String s = (String) Jarray.get(i);
					out.add(s);
					// categoriesMap.put(s, 0);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return out;
	}

	public static String AddComment(String userID, String markerID,
			String comment) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String response = "";
		HttpPost httppost = new HttpPost(
				Settings.url+"/app/add_comment/");
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
		nameValuePairs.add(new BasicNameValuePair("userID", userID));
		nameValuePairs.add(new BasicNameValuePair("markerID", markerID));
		nameValuePairs.add(new BasicNameValuePair("comment", comment));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			response = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
			System.out.println("Code " + POIsStatusCode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Success");
		System.out.println("User and Pass " + response);
		System.out.println("Fetched in ServerAPI");
		return response;
	}

	public static String AddRate(String userID, String markerID, String rate) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String response = "";
		HttpPost httppost = new HttpPost(
				Settings.url+"/app/add_rate/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
		nameValuePairs.add(new BasicNameValuePair("userID", userID));
		nameValuePairs.add(new BasicNameValuePair("markerID", markerID));
		nameValuePairs.add(new BasicNameValuePair("rate", rate));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			response = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
			System.out.println("Code " + POIsStatusCode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Success");
		System.out.println("User and Pass " + response);
		System.out.println("Fetched in ServerAPI");
		return response;
	}

	public static ArrayList<String> fetchQuestionDetails(String markerID,
			String questionsSelected) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String responsePOIsString = "";
		ArrayList<String> out = new ArrayList<String>();
		HttpPost httppost = new HttpPost(
				Settings.url+"/app/fetch_question_details/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);

		nameValuePairs.add(new BasicNameValuePair("markerID", markerID + ""));
		nameValuePairs.add(new BasicNameValuePair("question", questionsSelected
				+ ""));

		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			responsePOIsString = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
			System.out.println("Code " + POIsStatusCode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (POIsStatusCode == 200) {
			JSONObject POIsJSONObject;
			try {
				POIsJSONObject = new JSONObject(responsePOIsString);
				JSONArray POIsJSONArray = new JSONArray(
						POIsJSONObject.getString("questions"));
				for (int i = 0; i < POIsJSONArray.length(); i++) {
					String s = (String) POIsJSONArray.get(i);
					if (!s.equals("")) {
						out.add(s);
					}

					// categoriesMap.put(s, 0);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println("Fetched in ServerAPI");
		System.out.println("Choices in Server " + out);
		return out;
	}

	public static String AnswerQuestions(String userID, String markerID,
			String question, String questionType, String answerSubmitted) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String response = "";
		HttpPost httppost = new HttpPost(
				Settings.url+"/app/answer_questions/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(10);
		nameValuePairs.add(new BasicNameValuePair("userID", userID));
		nameValuePairs.add(new BasicNameValuePair("markerID", markerID));
		nameValuePairs.add(new BasicNameValuePair("question", question));
		nameValuePairs
				.add(new BasicNameValuePair("questionType", questionType));
		nameValuePairs.add(new BasicNameValuePair("answer", answerSubmitted));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			response = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
			System.out.println("Code " + POIsStatusCode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Success");
		System.out.println("Question Response " + response);
		System.out.println("Fetched in ServerAPI");
		return response;
	}

	public static ArrayList<String> fetchComments(String markerID) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String responsePOIsString = "";
		ArrayList<String> out = new ArrayList<String>();
		HttpPost httppost = new HttpPost(
				Settings.url+"/app/fetch_comments/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

		nameValuePairs.add(new BasicNameValuePair("markerID", markerID + ""));

		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			responsePOIsString = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
			System.out.println("Code " + POIsStatusCode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (POIsStatusCode == 200) {
			JSONObject POIsJSONObject;
			try {
				POIsJSONObject = new JSONObject(responsePOIsString);
				JSONArray POIsJSONArray = new JSONArray(
						POIsJSONObject.getString("comments"));
				for (int i = 0; i < POIsJSONArray.length(); i++) {
					String s = (String) POIsJSONArray.get(i);
					out.add(s);

					// categoriesMap.put(s, 0);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println("Fetched in ServerAPI");
		System.out.println("Comments in Server " + out);
		return out;
	}

	public static String CheckIfAnswered(String userID, String markerID,
			String question) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String response = "";
		HttpPost httppost = new HttpPost(
				Settings.url+"app/check_question_answered/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
		nameValuePairs.add(new BasicNameValuePair("userID", userID));
		nameValuePairs.add(new BasicNameValuePair("markerID", markerID));
		nameValuePairs.add(new BasicNameValuePair("question", question));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			response = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
			System.out.println("Code " + POIsStatusCode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Success");
		System.out.println("User and Pass " + response);
		System.out.println("Fetched in ServerAPI");
		return response;
	}

	public static ArrayList<String> getDescriptors() {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responseCategories;
		int categoriesStatusCode = 0;
		String responseCategoriesString = "";
		ArrayList<String> out = new ArrayList<String>();
		HttpGet httpgetCategories = new HttpGet(
				Settings.url+"app/fetch_descriptors/");
		try {
			responseCategories = httpclient.execute(httpgetCategories);
			categoriesStatusCode = responseCategories.getStatusLine()
					.getStatusCode();
			try {
				responseCategoriesString = EntityUtils
						.toString(responseCategories.getEntity());
			} catch (ParseException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("Desc Code " + categoriesStatusCode);
		if (categoriesStatusCode == 200) {
			JSONObject object;
			try {
				object = new JSONObject(responseCategoriesString);
				JSONArray Jarray = object.getJSONArray("imgDescriptors");
				for (int i = 0; i < Jarray.length(); i++) {
					String s = (String) Jarray.get(i);
					out.add(s);
					System.out.println("Out " + i + " is" + s);
					// categoriesMap.put(s, 0);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println("Desc Array " + out);
		return out;
	}

	public static String fetchAppLogo(String userID, String a) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse responsePOIs;
		int POIsStatusCode = 0;
		String response = "";
		HttpPost httppost = new HttpPost(
				Settings.url+"/app/fetch_app_logo/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
		nameValuePairs.add(new BasicNameValuePair("userID", userID));
		nameValuePairs.add(new BasicNameValuePair("name", a));
		System.out.println("fetch logo for " + userID + " and app " + a);
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			responsePOIs = httpclient.execute(httppost);
			response = EntityUtils.toString(responsePOIs.getEntity());
			POIsStatusCode = responsePOIs.getStatusLine().getStatusCode();
			System.out.println("Code " + POIsStatusCode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Success");
		System.out.println("Title response " + response);
		System.out.println("Response in ServerAPI");
		return response;
	}

	public static class ResponseHolder {
		public boolean success;
		public ArrayList<Object> data = new ArrayList<Object>();
	}

	public static ResponseHolder LikePOI_Marker(String userID, String type,
			String POIID, String value) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				Settings.url+"done/ransac4");
		ResponseHolder output = new ResponseHolder();
		output.success = false;
		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(8);
			nameValuePairs.add(new BasicNameValuePair("username", userID));
			nameValuePairs.add(new BasicNameValuePair("type", type));
			nameValuePairs.add(new BasicNameValuePair("poi", POIID));
			nameValuePairs.add(new BasicNameValuePair("like", value));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request

			HttpResponse response = httpclient.execute(httppost);

			if (response.getStatusLine().getStatusCode() == 200) {
				output.success = true;
			} else {
				System.out.println(response.getStatusLine().getStatusCode());
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e);
		}
		return output;
	}

	public static ResponseHolder CommentPOI_Marker(String userID, String type,
			String POIID, String value) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				Settings.url+"done/editloc_poi");
		ResponseHolder output = new ResponseHolder();
		output.success = false;
		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(8);
			nameValuePairs.add(new BasicNameValuePair("username", userID));
			nameValuePairs.add(new BasicNameValuePair("type", type));
			nameValuePairs.add(new BasicNameValuePair("poi", POIID));
			System.out.println(POIID);
			nameValuePairs.add(new BasicNameValuePair("comment", value));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request

			HttpResponse response = httpclient.execute(httppost);

			if (response.getStatusLine().getStatusCode() == 200) {
				output.success = true;
			} else {
				System.out.println(response.getStatusLine().getStatusCode());
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e);
		}
		return output;
	}

	public static Bitmap getImage(String URL) {
		try {
			URL url = new URL(URL);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.connect();
			InputStream input = connection.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(input);
			Bitmap bmp = BitmapFactory.decodeStream(bis);
			bis.close();
			input.close();
			return bmp;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap LoadImage(String URL, BitmapFactory.Options options) {
		Bitmap bitmap = null;
		InputStream in = null;
		try {
			in = OpenHttpConnection(URL);
			bitmap = BitmapFactory.decodeStream(in, null, options);

			in.close();
		} catch (IOException e1) {
		}
		return bitmap;
	}

	private static InputStream OpenHttpConnection(String strURL)
			throws IOException {
		InputStream inputStream = null;
		URL url = new URL(strURL);
		URLConnection conn = url.openConnection();

		try {
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setRequestMethod("GET");
			httpConn.connect();
			if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				inputStream = httpConn.getInputStream();
			}
		} catch (Exception ex) {
		}
		return inputStream;
	}

	private void doFileUpload(String selectedPath,String uploadedfile){
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        DataInputStream inStream = null;
        String lineEnd = "rn";
        String twoHyphens = "--";
        String boundary =  "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;
        String responseFromServer = "";
        String urlString = "http://your_website.com/upload_audio_test/upload_audio.php";
        try
        {
         //------------------ CLIENT REQUEST
        FileInputStream fileInputStream = new FileInputStream(new File(selectedPath) );
         // open a URL <span id="IL_AD10" class="IL_AD">connection</span> to the Servlet
         URL url = new URL(urlString);
         // Open a HTTP connection to the URL
         conn = (HttpURLConnection) url.openConnection();
         // Allow Inputs
         conn.setDoInput(true);
         // Allow Outputs
         conn.setDoOutput(true);
         // Don't use a cached copy.
         conn.setUseCaches(false);
         // Use a post method.
         conn.setRequestMethod("POST");
         conn.setRequestProperty("Connection", "Keep-Alive");
         conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
         dos = new DataOutputStream( conn.getOutputStream() );
         dos.writeBytes(twoHyphens + boundary + lineEnd);
         dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + selectedPath + "\"" + lineEnd);
         dos.writeBytes(lineEnd);
         // create a buffer of maximum size
         bytesAvailable = fileInputStream.available();
         bufferSize = Math.min(bytesAvailable, maxBufferSize);
         buffer = new byte[bufferSize];
         // read file and write it into form...
         bytesRead = fileInputStream.read(buffer, 0, bufferSize);
         while (bytesRead > 0)
         {
          dos.write(buffer, 0, bufferSize);
          bytesAvailable = fileInputStream.available();
          bufferSize = Math.min(bytesAvailable, maxBufferSize);
          bytesRead = fileInputStream.read(buffer, 0, bufferSize);
         }
         // send multipart form data necesssary after file data...
         dos.writeBytes(lineEnd);
         dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
         // close streams
         Log.e("Debug","File is written");
         fileInputStream.close();
         dos.flush();
         dos.close();
        }
        catch (MalformedURLException ex)
        {
             Log.e("Debug", "error: " + ex.getMessage(), ex);
        }
        catch (IOException ioe)
        {
             Log.e("Debug", "error: " + ioe.getMessage(), ioe);
        }
        //------------------ read the SERVER RESPONSE
        try {
              inStream = new DataInputStream ( conn.getInputStream() );
              String str;
 
              while (( str = inStream.readLine()) != null)
              {
                   Log.e("Debug","Server Response "+str);
              }
              inStream.close();
 
        }
        catch (IOException ioex){
             Log.e("Debug", "error: " + ioex.getMessage(), ioex);
        }
      }

}

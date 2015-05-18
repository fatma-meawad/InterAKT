package com.interakt.ar.graphics.poi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badlogic.gdx.graphics.Pixmap;
import com.interakt.ar.android.R;
import com.interakt.ar.networking.ServerAPI;

public class POITextureGenerator extends LinearLayout {

	LinearLayout gen;
	TextView tx1;
	TextView tx2;
	TextView tx3;
	TextView tx4;
	private TextView tx5;
	private TextView tx6;
	private TextView tx7;
	private ImageView thumbnail;

	public POITextureGenerator(Context context) {
		super(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.poi_for_gl, this);
		gen = (LinearLayout) findViewById(R.id.poi_for_gl_holder);
		thumbnail = (ImageView) findViewById(R.id.poi_for_gl_thumbnail);
		tx1 = (TextView) findViewById(R.id.poi_for_gl_name);
		tx2 = (TextView) findViewById(R.id.poi_for_gl_category);
		tx3 = (TextView) findViewById(R.id.poi_for_gl_children);
		tx4 = (TextView) findViewById(R.id.poi_for_gl_distance);
		tx5 = (TextView) findViewById(R.id.poi_for_gl_likes);
		tx6 = (TextView) findViewById(R.id.poi_for_gl_rate);
		tx7 = (TextView) findViewById(R.id.poi_for_gl_comments);
	}

	public Bitmap getBitmap(String... text) {
		gen.measure(MeasureSpec.makeMeasureSpec(gen.getLayoutParams().width,
				MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
				gen.getLayoutParams().height, MeasureSpec.EXACTLY));
		if (text[7].equals("true")) {
			gen.setBackgroundResource(R.drawable.poibackground_highlighted);
		} else {
			gen.setBackgroundResource(R.drawable.poibackground);
		}
		tx1.setText(text[0]);
		tx2.setText(text[1]);
		tx4.setText(String.format("%.1f", Double.parseDouble(text[2])) + "m");
		tx5.setText(text[4]);
		tx6.setText(text[5]);
		tx7.setText(text[6]);
		Bitmap bm = null;
		if (!text[3].equalsIgnoreCase("0")) {
			tx3.setVisibility(View.VISIBLE);
			tx3.setText(text[3]);
		} else {
			tx3.setVisibility(View.INVISIBLE);
		}
		// System.out.println(text[8]);
		if (text[8].equals("null") || text[8].equals("")) {
			thumbnail.setImageResource(R.drawable.locationbased);
		} else {
			try {
				BitmapFactory.Options bmOptions;
				bmOptions = new BitmapFactory.Options();
				bmOptions.inSampleSize = 1;
				bm = ServerAPI.LoadImage(text[8], bmOptions);
				thumbnail.setImageBitmap(bm);
			} catch (Exception e) {
				thumbnail.setImageResource(R.drawable.locationbased);

			}

		}
		Bitmap bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bitmap);
		gen.layout(0, 0, 512, 128);
		gen.draw(c);
		if (bm != null) {
			bm.recycle();
			bm = null;
		}
		// saveImage(bitmap);

		return bitmap;
	}

	void saveImage(Bitmap myBitmap) {

		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "");

		String fname = "Image.jpg";
		File file = new File(myDir, fname);
		if (file.exists())
			file.delete();
		try {
			FileOutputStream out = new FileOutputStream(file);
			myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Pixmap bitmapToPixmap(Bitmap bitmap) {
		int size = bitmap.getWidth() * bitmap.getHeight() * 2;
		ByteArrayOutputStream outStream;
		while (true) {
			outStream = new ByteArrayOutputStream(size);
			if (bitmap.compress(Bitmap.CompressFormat.PNG, 0, outStream))
				break;
			size = size * 3 / 2;
		}
		byte[] img = outStream.toByteArray();
		bitmap.recycle();
		return new Pixmap(img, 0, img.length);
	}

}

package com.interakt.ar.outdoor;

import android.content.Intent;
import android.content.res.Configuration;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.interakt.ar.android.ui.Map;
import com.interakt.ar.android.ui.MapNative;
import com.interakt.ar.android.ui.StatusBar;
import com.interakt.ar.graphics.poi.POITextureGenerator;

public class MainActivity extends AndroidApplication {
	protected int origWidth;
	protected int origHeight;
	protected FrameLayout main;
	protected MapNative mapNative;
	protected RelativeLayout mainRelative;
	protected com.interakt.ar.android.ui.ProgressBar mainProgressBar;
	protected StatusBar status;
	protected POITextureGenerator generator;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

	}

	public void addView(View view, int index) {
		main.addView(view, index);
	}

	public void addView(View view) {
		main.addView(view);
	}

	public Handler getMainHandler() {
		return this.handler;
	}

	public void post(Runnable r) {
		handler.post(r);
	}

	public void setFixedSize(int width, int height) {
		if (graphics.getView() instanceof SurfaceView) {
			SurfaceView glView = (SurfaceView) graphics.getView();
			glView.getHolder().setFormat(PixelFormat.RGB_565);
			glView.getHolder().setFixedSize(width, height);
		}
	}

	public void restoreFixedSize() {
		if (graphics.getView() instanceof SurfaceView) {
			SurfaceView glView = (SurfaceView) graphics.getView();
			glView.getHolder().setFormat(PixelFormat.RGB_565);
			glView.getHolder().setFixedSize(origWidth, origHeight);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();

	}


	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		boolean result;
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_VOLUME_UP:
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			result = true;
			break;

		default:
			result = super.dispatchKeyEvent(event);
			break;
		}

		return result;
	}

}
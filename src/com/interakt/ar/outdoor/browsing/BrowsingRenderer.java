/*
 * Copyright 2012 Johnny Lish (johnnyoneeyed@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.interakt.ar.outdoor.browsing;


import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Semaphore;




import android.content.Context;
import android.os.Looper;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Pixmap.Filter;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.interakt.ar.Settings;
import com.interakt.ar.graphics.LibGDXPerspectiveCamera;
import com.interakt.ar.graphics.radar.Radar;
import com.interakt.ar.listeners.NetworkStateListener;
import com.interakt.ar.outdoor.DeviceCameraControl;

public class BrowsingRenderer implements ApplicationListener {

	public enum Mode {
		normal, prepare, preview, takePicture, waitForPictureReady,
	}

	private PerspectiveCamera camera;

	private Mode mode = Mode.normal;

	private final DeviceCameraControl deviceCameraControl;

	public BrowsingScreen geoMode;

	private Context context;
	boolean stopRendering;

	private InputMultiplexer multiplexer;
	public Semaphore canRender = new Semaphore(1);
	private Radar radar;

	public BrowsingRenderer(Context context, DeviceCameraControl cameraControl) {
		this.deviceCameraControl = cameraControl;
		this.context = context;
	}

	@Override
	public void create() {
		Looper.prepare();

		geoMode = new BrowsingScreen(context);
		geoMode.init(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		radar = ((BrowsingMainActivity) context).getRadar();
		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(geoMode.getGestureDetector());
		Gdx.input.setInputProcessor(multiplexer);
		
	}

	@Override
	public void render() {
		
			if (mode == Mode.normal) {
				mode = Mode.prepare;
				if (deviceCameraControl != null) {

					deviceCameraControl.prepareCameraAsync();
				}
			}

			Gdx.gl10.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
			if (mode == Mode.prepare) {
				Gdx.gl10.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
				if (deviceCameraControl != null) {
					if (deviceCameraControl.isReady()) {
						deviceCameraControl.startPreviewAsync();
						mode = Mode.preview;
					}
				}
			} else if (mode == Mode.preview) {
				Gdx.gl10.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			} else { // mode = normal
				Gdx.gl10.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			}
			Gdx.gl10.glClear(GL10.GL_COLOR_BUFFER_BIT
					| GL10.GL_DEPTH_BUFFER_BIT);
			Gdx.gl10.glEnable(GL10.GL_DEPTH_TEST);
			Gdx.gl10.glEnable(GL10.GL_TEXTURE);
			Gdx.gl10.glEnable(GL10.GL_TEXTURE_2D);
			Gdx.gl10.glEnable(GL10.GL_LINE_SMOOTH);
			Gdx.gl10.glDepthFunc(GL10.GL_LEQUAL);
			Gdx.gl10.glClearDepthf(1.0F);
			Gdx.gl10.glEnable(GL10.GL_ALPHA_TEST);
			Gdx.gl10.glDisable(GL10.GL_COLOR_MATERIAL);
			Gdx.gl10.glAlphaFunc(GL10.GL_GREATER, 0.0f);
			Gdx.gl10.glEnable(GL10.GL_BLEND);
			Gdx.gl10.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			Gdx.gl10.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			if (!stopRendering) {
			if (canRender.tryAcquire()) {
				((LibGDXPerspectiveCamera) geoMode.getCamera()).render();
				geoMode.render();
				canRender.release();

			}

			radar.draw();
		}

	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {

		deviceCameraControl.stopPreviewAsync();
		mode = Mode.normal;

	}

	@Override
	public void resume() {

	}

	
	
	
	@Override
	public void dispose() {
		radar.dispose();
		radar = null;
		geoMode.dispose();
		geoMode = null;
		multiplexer.clear();
		multiplexer = null;
		canRender = null;
	}
}
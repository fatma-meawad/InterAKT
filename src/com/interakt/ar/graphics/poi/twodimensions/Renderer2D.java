package com.interakt.ar.graphics.poi.twodimensions;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import android.content.Context;
import android.widget.Toast;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.interakt.ar.graphics.poi.POI;

public class Renderer2D {

	private OrthographicCamera camera;
	private World world;
	private Box2DDebugRenderer debugRenderer;
	private Graph graph;
	private Context context;
	public static int currentGraph = 0;

	public Renderer2D(Context context) {
		this.context = context;
	}

	public void create() {
		camera = new OrthographicCamera(Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		camera.setToOrtho(false);
		// create a world
		
		// for debuging
		debugRenderer = new Box2DDebugRenderer();

	}

	public void resize(int width, int height) {
		camera = new OrthographicCamera(width, height);
		camera.setToOrtho(false);
	}

	public void render() {
		camera.update();
		camera.apply(Gdx.gl10);
		graph.render(camera.projection);
		//debugRenderer.render(world, camera.combined);
		world.step(1f, 20, 20);
	}
	
	public void createNewGraph(POI parent,int tapX, int tapY){
		if(world != null){
			world.dispose();
		}
		world = new World(new Vector2(0, 0), true);
		
		// wall1
		BodyDef wall1BodyDef = new BodyDef();
		wall1BodyDef.position.set(new Vector2(camera.viewportWidth / 2, 0));
		Body wall1Body = world.createBody(wall1BodyDef);
		PolygonShape wall1 = new PolygonShape();
		wall1.setAsBox(camera.viewportWidth / 2, 0.0f);
		wall1Body.createFixture(wall1, 0.0f);
		wall1.dispose();
		// wall2
		BodyDef wall2BodyDef = new BodyDef();
		wall2BodyDef.position.set(new Vector2(camera.viewportWidth / 2,
				camera.viewportHeight));
		Body wall2Body = world.createBody(wall2BodyDef);
		PolygonShape wall2 = new PolygonShape();
		wall2.setAsBox(camera.viewportWidth / 2, 0.0f);
		wall2Body.createFixture(wall2, 0.0f);
		wall2.dispose();
		// wall3
		BodyDef wall3BodyDef = new BodyDef();
		wall3BodyDef.position.set(new Vector2(0, camera.viewportHeight / 2));
		Body wall3Body = world.createBody(wall3BodyDef);
		PolygonShape wall3 = new PolygonShape();
		wall3.setAsBox(0, camera.viewportHeight / 2);
		wall3Body.createFixture(wall3, 0.0f);
		wall3.dispose();
		// wall4
		BodyDef wall4BodyDef = new BodyDef();
		wall4BodyDef.position.set(new Vector2(camera.viewportWidth,
				camera.viewportHeight / 2));
		Body wall4Body = world.createBody(wall4BodyDef);
		PolygonShape wall4 = new PolygonShape();
		wall4.setAsBox(0, camera.viewportHeight / 2);
		wall4Body.createFixture(wall4, 0.0f);
		wall4.dispose();
		graph = new Graph(world,parent, new Point(tapX,tapY));
		
	}

	public void inputPan(float x, float y, float deltaX, float deltaY) {
	
		graph.checkForPan(x, y);
		
	}
	
	public void inputTap(float x, float y) {
		 
		graph.checkForClick(x, y);
		
	}

}

package com.interakt.ar.graphics.poi.twodimensions;

import java.util.ArrayList;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.interakt.ar.graphics.poi.POI;

public class Vertex {

	private Size size;
	private Point point;
	private float r, g, b, a;
	private ArrayList<Vertex> adjacents;
	private SpriteBatch spriteBatch;
	private Texture texture;
	private BitmapFont font;
	private Body body;
	private boolean selected;
	private int force;
	private int radius;
	private Vector2 resultant = new Vector2(0, 0);
	private float scale = 1;
	private boolean stillOnLoading;
	private POI poi;
	private TextureRegion textureRegion;

	public Vertex(Graph parent, Point point, POI poi) {
		this.size = new Size(512, 128);
		this.point = point;
		this.poi = poi;
		adjacents = new ArrayList<Vertex>();
		spriteBatch = new SpriteBatch();
		font = new BitmapFont();
		// First we create a body definition
		BodyDef bodyDef = new BodyDef();
		// We set our body to dynamic, for something like ground which doesnt
		// move we would set it to StaticBody
		bodyDef.type = BodyType.DynamicBody;
		// Set our body's starting position in the world
		bodyDef.position.set(point.getX(), point.getY());
		// Create our body in the world using our body definition
		body = parent.getWorld().createBody(bodyDef);
		body.setLinearDamping(0.01f);
		body.setAngularDamping(5);
		// Create a polygon shape
		PolygonShape node = new PolygonShape();
		// Set the polygon shape as a box which is twice the size of our view
		// port and 20 high
		// (setAsBox takes half-width and half-height as arguments)
		node.setAsBox(size.getWidth() / 2, size.getHeight() / 2);
		// node.setRadius(size.getHeight()/2);
		// Create a fixture from our polygon shape and add it to our ground body
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = node;
		fixtureDef.density = 1;
		fixtureDef.friction = 1.0f;
		fixtureDef.restitution = 0.0f; // Make it bounce a little bit
		body.createFixture(fixtureDef);
		// Clean up after ourselves
		node.dispose();

		if (poi.isBodyTextureLoaded()) {
			setTexture(poi.getTextureBody());
			stillOnLoading = false;
		} else {
			if (poi.getPOIHolder().textureGen != null) {
				poi.setBodyTexture();
				setTexture(poi.getTextureBody());
				stillOnLoading = false;
			} else {
				if (poi.isLoadingTextureLoaded()) {
					setTexture(poi.getLoadingTexture());
				} else {
					poi.setLoadingTexture();
					setTexture(poi.getLoadingTexture());
				}
				stillOnLoading = true;
			}

		}
		
		Thread t = new Thread(){
			public void run(){
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				selected = true;
			}
		};
		t.start();

	}

	public void setPosition(Point point) {
		this.point = point;
	}

	public void setSize(Size size) {
		this.size = size;
	}

	public Size getSize() {
		return size;
	}

	public Point getPoint() {
		return point;
	}

	public ArrayList<Vertex> getAdjacents() {
		return adjacents;
	}

	public void addAdjacent(Vertex vertex) {
		adjacents.add(vertex);
	}

	public void setSelected(boolean touched) {
		this.selected = touched;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setVertexColor(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public void setTexture(String file) {
		texture = new Texture(Gdx.files.internal(file));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		textureRegion = new TextureRegion(texture,512,128);

	}

	public void setTexture(Texture texture) {
		this.texture = texture;
		this.texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		textureRegion = new TextureRegion(this.texture,512,128);

	}

	public void applyForce(Vector2 force) {
		resultant.x += force.x;
		resultant.y += force.y;
		System.out.println(resultant);

	}

	public Vector2 getResultant() {
		return resultant;
	}

	public void setForce(int force) {
		this.force = force;
	}

	public int getForce() {
		// TODO Auto-generated method stub
		return force;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getRadius() {
		return radius;
	}

	public Body getBody() {
		return body;
	}
	
	

	public void render(Matrix4 projection) {
		// spriteBatch.setProjectionMatrix(projection);
//		Gdx.gl10.glEnable(GL10.GL_COLOR_MATERIAL);
//		Gdx.gl10.glAlphaFunc(GL10.GL_GREATER, 0.0f);
//		Gdx.gl10.glDisable(GL10.GL_BLEND);
//		Gdx.gl10.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
//		Gdx.gl10.glEnable(Gdx.gl10.GL_ALPHA_TEST);
		spriteBatch.begin();
		spriteBatch.enableBlending();
		spriteBatch.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		spriteBatch.setColor(1, 1, 1, 1);
		if (!selected) {
			Vector2 position = body.getPosition();
			
			this.point.setX((int) (position.x));
			this.point.setY((int) (position.y));
			Gdx.gl10.glTranslatef(position.x, position.y, 0);
			Gdx.gl10.glScalef(scale, scale, 0);
			scale = scale >= 1 ? 1 : scale + 0.02f;
			// Gdx.gl10.glRotatef((float) ((body.getAngle()*180)/Math.PI), 0, 0,
			// 1);
			
			Gdx.gl10.glTranslatef(-position.x, -position.y, 0);
			if (stillOnLoading) {
				if (poi.getPOIHolder().textureGen != null) {
					poi.setBodyTexture();
					setTexture(poi.getTextureBody());
					stillOnLoading = false;
				}
			}
			spriteBatch.draw(textureRegion, position.x - size.getWidth() / 2,
					position.y - (size.getHeight()/2), size.getWidth(),
					size.getHeight());
		} else {

			spriteBatch.draw(textureRegion, point.getX() - size.getWidth() / 2,
					point.getY() - size.getHeight() / 2, size.getWidth(),
					size.getHeight());
			body.setTransform(new Vector2(point.getX(), point.getY()), 0);
		}
		spriteBatch.end();
		//Gdx.gl10.glDisable(Gdx.gl10.GL_ALPHA_TEST);
	}

	public POI getPOI() {
		// TODO Auto-generated method stub
		return poi;
	}

}

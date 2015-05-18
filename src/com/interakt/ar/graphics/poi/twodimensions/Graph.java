package com.interakt.ar.graphics.poi.twodimensions;

import java.util.ArrayList;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.interakt.ar.graphics.poi.POI;

public class Graph {

	private ShapeRenderer shapeRenderer;
	private ArrayList<Vertex> vertices;
	private World world;
	private int size;

	public Graph(World world, POI parent, Point tapPpoint) {
		this.world = world;
		shapeRenderer = new ShapeRenderer();
		vertices = new ArrayList<Vertex>();
		expandPOI(null, parent, tapPpoint);

	}

	public void expandPOI(Vertex parent, POI node, Point tapPoint) {
		Vertex v;

		vertices.add(v = new Vertex(this, new Point(tapPoint.getX(), tapPoint
				.getY()), node));
		v.setForce(110000);
		v.setRadius(512 * 2);
		if (parent != null) {
			parent.addAdjacent(v);
		}
		for (int i = 0; i < node.getPOIHolder().childrenLst.size(); i++) {
			expandPOI(v, node.getPOIHolder().childrenLst.get(i), tapPoint);
		}
	}

	public World getWorld() {
		return world;
	}

	public void render(Matrix4 projection) {
//		shapeRenderer.setProjectionMatrix(projection);
//		shapeRenderer.begin(ShapeType.Line);
//		shapeRenderer.setColor(1, 0, 0, 0);
//		for (Vertex vertex : vertices) {
//			ArrayList<Vertex> adjacents = vertex.getAdjacents();
//			for (Vertex adjacent : adjacents) {
//				shapeRenderer.line(vertex.getPoint().getX(), vertex.getPoint()
//						.getY(), adjacent.getPoint().getX(), adjacent
//						.getPoint().getY());
//				System.out.println("here");
//			}
//		}
//		shapeRenderer.end();
		for (Vertex vertex : vertices) {
			vertex.render(projection);
			for (Vertex forceApplier : vertices) {

				if (vertex.getPoint().getDistanceTo(forceApplier.getPoint()) < forceApplier
						.getRadius()) {
					int radius = vertex.getPoint().getDistanceTo(
							forceApplier.getPoint());
				
					int force = forceApplier.getForce();
					int factor = force / forceApplier.getRadius();
					int appliedForce = (forceApplier.getRadius() - radius)
							* factor;
					Vector2 forceVector = vertex.getPoint()
							.getDirection(forceApplier.getPoint()).toVector2()
							.mul(appliedForce);
					vertex.getBody().applyForce(forceVector,
							vertex.getPoint().toVector2());
					
					vertex.applyForce(forceVector);
					// System.out.println(appliedForce);
				}
			}

			Vector2 globalGravity = vertex
					.getPoint()
					.getDirection(
							new Point(Gdx.graphics.getWidth() / 2, Gdx.graphics
									.getHeight() / 2)).toVector2().mul(-110000);
			vertex.applyForce(globalGravity);
			vertex.getBody().applyForce(globalGravity,
					vertex.getPoint().toVector2());
		}

	}

	public void deSelectAll() {
		for (Vertex vertex : vertices) {
			vertex.setSelected(false);
		}
	}

	public void checkForClick(float x, float y) {
		for (Vertex vertex : vertices) {
			if (vertex.getPoint().getX() - vertex.getSize().getWidth() / 2 < x
					&& vertex.getPoint().getY() - vertex.getSize().getHeight()
							/ 2 < y
					&& vertex.getPoint().getX() + vertex.getSize().getWidth()
							/ 2 > x
					&& vertex.getPoint().getY() + vertex.getSize().getHeight()
							/ 2 > y) {
				System.out.println("click>>>>");
				//vertex.setSelected(!vertex.isSelected());
				vertex.getPOI().fireListener();
				break;
			}
		}
	}

	public void checkForPan(float x, float y) {
//		for (Vertex vertex : vertices) {
//			if (vertex.getPoint().getX() - vertex.getSize().getWidth() / 2 < x
//					&& vertex.getPoint().getY() - vertex.getSize().getHeight()
//							/ 2 < y
//					&& vertex.getPoint().getX() + vertex.getSize().getWidth()
//							/ 2 > x
//					&& vertex.getPoint().getY() + vertex.getSize().getHeight()
//							/ 2 > y) {
//				System.out.println("click>>>>");
//				if (vertex.isSelected()) {
//					vertex.setPosition(new Point((int) x, (int) y));
//				}
//				break;
//			} else {
//				if (vertex.isSelected()) {
//					vertex.setSelected(false);
//				}
//			}
//
//		}

	}

}

package com.interakt.ar.graphics;
import javax.microedition.khronos.opengles.GL11;


import android.content.Context;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

public class Cube extends Shape {

	public static final float vertexData[] = { 0.5f, 0.5f, 0.5f, -0.5f, 0.5f,
			0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f,

			0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f,
			0.5f, -0.5f,

			0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f,
			0.5f, 0.5f,

			0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f,
			0.5f, -0.5f,

			-0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f,
			-0.5f, 0.5f,

			-0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, -0.5f,
			-0.5f, 0.5f, };

	public static final float texData[] = { 0.0f, 0.0f, // quad/face 0/Vertex 0
			0.0f, 1.0f, // quad/face 0/Vertex 1
			1.0f, 1.0f, // quad/face 0/Vertex 2
			1.0f, 0.0f, // quad/face 0/Vertex 3

			1.0f, 1.0f, // quad/face 1/Vertex 4
			0.0f, 1.0f, // quad/face 1/Vertex 5
			0.0f, 0.0f, // quad/face 1/Vertex 6
			1.0f, 0.0f, // quad/face 1/Vertex 7

			0.0f, 0.0f, // quad/face 2/Vertex 8
			0.0f, 1.0f, // quad/face 2/Vertex 9
			1.0f, 1.0f, // quad/face 2/Vertex 10
			1.0f, 0.0f, // quad/face 2/Vertex 11

			1.0f, 1.0f, // quad/face 3/Vertex 12
			0.0f, 1.0f, // quad/face 3/Vertex 13
			0.0f, 0.0f, // quad/face 3/Vertex 14
			1.0f, 0.0f, // quad/face 3/Vertex 15

			1.0f, 1.0f, // quad/face 4/Vertex 16
			0.0f, 1.0f, // quad/face 4/Vertex 17
			0.0f, 0.0f, // quad/face 4/Vertex 18
			1.0f, 0.0f, // quad/face 4/Vertex 19

			1.0f, 1.0f, // quad/face 5/Vertex 20
			0.0f, 1.0f, // quad/face 5/Vertex 21
			0.0f, 0.0f, // quad/face 5/Vertex 22
			1.0f, 0.0f, // quad/face 5/Vertex 23
	};

	public static final short facesVerticesIndex[][] = { { 0, 1, 2, 3 },
			{ 4, 5, 6, 7 }, { 8, 9, 10, 11 }, { 12, 13, 14, 15 },
			{ 16, 17, 18, 19 }, { 20, 21, 22, 23 } };

	// public static final short facesVerticesIndex2[][] = { { 0, 1, 2, 0, 2, 3
	// },
	// { 4, 5, 6 , 4, 6, 7 }, { 8, 9, 10 , 8, 10, 11 }, { 12, 13, 14 , 12, 14,
	// 15 },
	// { 16, 17, 18 , 16, 18, 19 }, { 20, 21, 22 , 20, 22, 23 } };

	public final static VertexAttribute verticesAttributes[] = new VertexAttribute[] {
			new VertexAttribute(Usage.Position, 3, "a_position"),
			new VertexAttribute(Usage.ColorPacked, 4, "a_color"),
			new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"), };


	public Cube(Context context) {
		super(context);
		mesh = new Mesh[6];
		// Load the Libgdx splash screen texture
		texture = new Texture(Gdx.files.internal("data/box.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		// Create the 6 faces of the Cube
		for (int i = 0; i < 6; i++) {
			mesh[i] = new Mesh(true, 24, 4, verticesAttributes);
			int pIdx = 0;
			int tIdx = 0;
			float[] vertices = new float[144];
			for (int j = 0; j < vertices.length;) {
				vertices[j++] = vertexData[pIdx++];
				vertices[j++] = vertexData[pIdx++];
				vertices[j++] = vertexData[pIdx++];
				vertices[j++] = Color.toFloatBits(255,255, 255, 255);
				vertices[j++] = texData[tIdx++];
				vertices[j++] = texData[tIdx++];
			}

			mesh[i].setVertices(vertices);
			mesh[i].setIndices(Cube.facesVerticesIndex[i]);
		}
		intersectionIndices = new short[6][6];
		for (int i = 0; i < 6; i++) {
			short[] temp = new short[6];
			for (int j = 0; j < facesVerticesIndex.length; j++) {
				if (j <= 2) {
					temp[j] = facesVerticesIndex[i][j];
				}
				if (j > 2) {
					temp[3] = facesVerticesIndex[i][0];
					temp[4] = facesVerticesIndex[i][2];
					temp[5] = facesVerticesIndex[i][3];
				}
			}
			intersectionIndices[i] = temp;
		}

	}

	public void draw() {
		Gdx.gl10.glPushMatrix();
		if (islocked) {
//			Gdx.gl10.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
//					GL10.GL_MODULATE);
//			Gdx.gl10.glEnable(GL10.GL_BLEND);
//			Gdx.gl10.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
//			float[] lookAt = this.parent.getCamera().getLookVector();
//			float newX = lookAt[0] * radius / this.Parent.getCamera().far;
//			float newY = lookAt[1] * radius / this.Parent.getCamera().far;
//			float newZ = lookAt[2] * radius / this.Parent.getCamera().far;
//			float[] infilter = { newX, newY, newZ };
//			float[] outfilter = this.filter.lowPass(infilter, 0.06f);
//			Gdx.gl10.glTranslatef(outfilter[0], outfilter[1], outfilter[2]);
//			x = outfilter[0];
//			y = outfilter[1];
//			z = outfilter[2];
//			angle = (float) Math.atan(outfilter[1] / outfilter[0]);
//			xAxis = 0;
//			yAxis = 0;
//			zAxis = 1;
//			angle = outfilter[0] > 0 && outfilter[1] > 0
//					|| outfilter[0] > 0 && outfilter[1] < 0 ? (float) (angle * 180 / Math.PI)
//					: 180 + (float) (angle * 180 / Math.PI);
//
//			Gdx.gl10.glRotatef(angle, xAxis, yAxis, zAxis);
	//		Gdx.gl10.glScalef(scaleX, scaleY, scaleZ);
		} else {
			Gdx.gl10.glTranslatef(x, y, z);
			Gdx.gl10.glRotatef(angle, xAxis, yAxis, zAxis);
			Gdx.gl10.glScalef(scaleX, scaleY, scaleZ);
		}
		//texture.bind();
		for (int i = 0; i < 6; i++) {
			mesh[i].render(GL10.GL_TRIANGLE_FAN, 0, 4);
		}
		//texture.bind(0);
		//Gdx.gl11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, accModelView, 0);
//		if(this.index>0){
//		//	modelView=new Matrix4(this.Parent.getShapes().get(this.index-1).accModelView).inv().val;
//		Matrix4.mul(modelView,accModelView);
//		}else{
//			modelView = accModelView;
//		}
		Gdx.gl10.glPopMatrix();
		//Gdx.gl10.glDisable(GL10.GL_BLEND);
		//Gdx.gl10.glTexEnvf(GL10.GL_FALSE, GL10.GL_FALSE, GL10.GL_FALSE);

	}

	public boolean isIntersected(Ray ray, float[] view) {
//		radius = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)
//				+ Math.pow(z, 2));
//		Matrix4.mul(view, modelView);
//		Matrix4 mat = new Matrix4(view);
//		float[] temp = new float[vertexData.length];
//		for (int i = 0; i < vertexData.length; i += 3) {
//			float[] vec = { vertexData[0 + i], vertexData[1 + i],
//					vertexData[2 + i], 1 };
//			Matrix4.mulVec(mat.val, vec);
//			temp[0 + i] = vec[0];
//			temp[1 + i] = vec[1];
//			temp[2 + i] = vec[2];
//		}
//		for(int i=0;i<temp.length;i++){
//			System.out.println(temp[i]);
//		}
//		Vector3 localIntersection = new Vector3();
//		Vector3 globalIntersection = new Vector3();
//		boolean intersectionOccured = false;
//		for (int j = 0; j < mesh.length; j++) {
//			if (Intersector.intersectRayTriangles(ray, temp,
//					intersectionIndices[j], 3, localIntersection)) {
//				intersectionOccured = true;
//				if (!(globalIntersection.isZero())) {
//					if (ray.origin.sub(localIntersection).len() < ray.origin
//							.sub(globalIntersection).len()) {
//						globalIntersection.set(localIntersection);
//
//					}
//				} else {
//					globalIntersection.set(localIntersection);
//
//				}
//			}
//		}
//		if (!globalIntersection.isZero() && intersectionOccured) {
//			listener.click(this, globalIntersection);
//		
//		}

		return false;
	}

	public void dispose() {
		texture.dispose();
		for (int i = 0; i < 6; i++) {
			mesh[i].dispose();
			mesh[i] = null;
		}
		texture = null;
	}





	
}

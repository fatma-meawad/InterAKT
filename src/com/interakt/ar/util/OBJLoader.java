package com.interakt.ar.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.loaders.wavefront.ObjLoader;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;

public class OBJLoader {
	public static StillModel POIHEAD;
	public static StillModel POIBODY;
	public static StillModel NOTAMARKER;
	public static StillModel CANDIDATE;
	public static StillModel GETCLOSER;
	public static StillModel ONMARKERSHAPE;

	public static StillModel loadOBJ(String file) {
		StillModel model;
		ObjLoader loader = new ObjLoader();
		model = loader.loadObj(Gdx.files.internal(file), true);

		return model;
	}

	public static void loadPOIHEADOBJ(String file) {

		ObjLoader loader = new ObjLoader();
		POIHEAD = loader.loadObj(Gdx.files.internal(file), true);

	}
	
	public static void loadPOIBODYOBJ(String file) {

		ObjLoader loader = new ObjLoader();
		POIBODY = loader.loadObj(Gdx.files.internal(file), true);

	}
	
	
	public static void loadNOTAMARKER(String file) {

		ObjLoader loader = new ObjLoader();
		NOTAMARKER = loader.loadObj(Gdx.files.internal(file), true);

	}
	
	public static void loadCANDIDATE(String file) {

		ObjLoader loader = new ObjLoader();
		CANDIDATE= loader.loadObj(Gdx.files.internal(file), true);

	}
	
	public static void loadGETCLOSER(String file) {
		
		ObjLoader loader = new ObjLoader();
		GETCLOSER= loader.loadObj(Gdx.files.internal(file), true);

	}
	
	public static void loadONMARKERSHAPE(String file) {
		
		ObjLoader loader = new ObjLoader();
		ONMARKERSHAPE= loader.loadObj(Gdx.files.internal(file), true);
		
	}

	public static ArrayList<float[]> extractInfo(Mesh mesh) {
		ArrayList<float[]> out = new ArrayList<float[]>();
		boolean normalExist = mesh
				.getVertexAttribute(VertexAttributes.Usage.Normal) != null;
		boolean texCoordExist = mesh
				.getVertexAttribute(VertexAttributes.Usage.TextureCoordinates) != null;
		int totalNumber = 3 + (normalExist ? 3 : 0) + (texCoordExist ? 2 : 0);
		float[] vert = new float[mesh.getNumVertices() * 3];
		float[] normal = normalExist ? new float[mesh.getNumVertices() * 3]
				: null;
		float[] tex = texCoordExist ? new float[mesh.getNumVertices() * 2]
				: null;
		int posIndex = 0;
		int norIndex = 0;
		int texIndex = 0;
		float[] all = new float[mesh.getNumVertices() * totalNumber];
		mesh.getVertices(all);
		for (int i = 0; i < mesh.getNumVertices() * totalNumber;) {
			vert[posIndex++] = all[i++];
			vert[posIndex++] = all[i++];
			vert[posIndex++] = all[i++];
			if (normalExist) {
				normal[norIndex++] = all[i++];
				normal[norIndex++] = all[i++];
				normal[norIndex++] = all[i++];
			}
			if (texCoordExist) {
				tex[texIndex++] = all[i++];
				tex[texIndex++] = all[i++];
			}
		}
		out.add(0, vert);
		out.add(1, normal);
		out.add(2, tex);
		return out;
	}

	public static void unLoadPOIBODYOBJ() {
		POIBODY.dispose();
		
	}







}

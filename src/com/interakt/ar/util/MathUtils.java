package com.interakt.ar.util;

import java.util.ArrayList;

public class MathUtils {

	public static void main(String[] args) {
		double[][] rotMatrix = { { -0.06176547, -0.86404616, 0.49960914 },
				{ -0.024982102, -0.49906966, -0.86620164 },
				{ 0.99777794, -0.06598264, 0.009239583 } };
		double[][] inv = inverz_matrike(rotMatrix);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				System.out.print(inv[i][j] + " , ");
			}
			System.out.println();
		}
	}

	public static double[] getVectorDifference(double[] b, double[] c) {
		double[] a = { b[0] - c[0], b[1] - c[1], b[2] - c[2] };

		return a;
	}

	public static double getAvg(ArrayList<Double> input) {
		double sum = 0.0;
		for (int i = 0; i < input.size(); i++) {
			sum += input.get(i);
		}
		return sum / input.size();
	}

	public static double getStandardDeviation(ArrayList<Double> input) {
		double avg = getAvg(input);
		double sumDiff = 0;
		for (int i = 0; i < input.size(); i++) {
			double diff = input.get(i) - avg;
			sumDiff = sumDiff + diff * diff;
		}
		return Math.sqrt((sumDiff / (input.size() - 1)));
	}

	public static double getZScore(double p, ArrayList<Double> input) {

		return ((p - getAvg(input)) / getStandardDeviation(input));
	}

	/**
	 * This method returns the second argument of the atan2 function given the
	 * result and the first param
	 * 
	 * @return
	 */
	public static double getAtan2_x(double result, double y) {
		if (result == 90)
			return 0;
		return y / (Math.tan(Math.toRadians(result)));
	}

	public static double[] rotateMatrix(double[] rotationMatrix, double[] org) {
		double[] result = new double[3];
		result[0] = rotationMatrix[0] * org[0] + rotationMatrix[1] * org[1]
				+ rotationMatrix[2] * org[2];
		result[1] = rotationMatrix[3] * org[0] + rotationMatrix[4] * org[1]
				+ rotationMatrix[5] * org[2];
		result[2] = rotationMatrix[6] * org[0] + rotationMatrix[7] * org[1]
				+ rotationMatrix[8] * org[2];
		return result;
	}

	public static double getDistance(double[] v1, double[] v2) {
		if (v1.length != v2.length) {
			System.out.println("Sizes do not match in getDistance");
			return 0;
		}
		double total = 0;
		for (int i = 0; i < v1.length; i++) {
			total = total + Math.pow((v1[i] - v2[i]), 2);
		}
		return Math.sqrt(total);
	}

	public static double[] addVectors(double[] v1, double[] v2) {
		if (v1.length != v2.length) {
			System.out.println("The sizes are not equal in addVectors");
			return null;
		}
		double[] result = new double[v1.length];
		for (int i = 0; i < v1.length; i++) {
			result[i] = v1[i] + v2[i];
		}
		return result;
	}

	public static double[] scaleArray(double[] arr, double scaleFactor) {
		double[] result = new double[arr.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = scaleFactor * arr[i];
		}
		return result;
	}

	public static double getAngleBetweenSkewLines(double[] v1, double[] v2) {
		double angle = 0;
		double[] u1 = getUnitVector(v1);
		double[] u2 = getUnitVector(v2);
		double dotProduct = getDotProduct(u1, u2);
		double u1Mag = Math.sqrt(getDotProduct(u1, u1));
		double u2Mag = Math.sqrt(getDotProduct(u2, u2));
		angle = Math.acos(dotProduct / (u1Mag * u2Mag));
		return angle;
	}
	
	
	

	public static double getAngleBetweenSkewLines(float[] v1, double[] v2) {
		double angle = 0;
		double[] u1 = getUnitVector(v1);
		double[] u2 = getUnitVector(v2);
		double dotProduct = getDotProduct(u1, u2);
		double u1Mag = Math.sqrt(getDotProduct(u1, u1));
		double u2Mag = Math.sqrt(getDotProduct(u2, u2));
		angle = Math.acos(dotProduct / (u1Mag * u2Mag));
		return angle;
	}

	public static double getDotProduct(double[] v1, double[] v2) {
		if (v1.length != v2.length)
			return 0;
		double product = 0;
		for (int i = 0; i < v1.length; i++) {
			product = product + v1[i] * v2[i];
		}
		return product;
	}

	// x0 represents the unknown point on the cylinder
	public static double applyCylinderEqn(double[] x0, double[] x1, double[] x2) {
		double[] v1 = getDifference(x0, x1);
		double[] v2 = getDifference(x0, x2);
		double[] crossProduct = getCrossProduct(v1, v2);
		double[] v3 = getDifference(x2, x1);
		double nominatorMag = getDotProduct(crossProduct, crossProduct);
		double denominatorMag = getDotProduct(v3, v3);
		return nominatorMag / denominatorMag;
	}

	public static double[] getDifference(double[] v1, double[] v2) {
		return new double[] { (v1[0] - v2[0]), (v1[1] - v2[1]), (v1[2] - v2[2]) };
	}

	public static double[] getCrossProduct(double[] b, double[] c) {

		if (b.length != c.length) {
			System.out
					.println("Arrays are of different sizes in cross product");
			return null;
		}
		if (b.length != 3) {
			System.out.println("Length in cross product is not 3");
			return null;
		}
		double[] a = new double[3];
		a[0] = b[1] * c[2] - b[2] * c[1];
		a[1] = b[2] * c[0] - b[0] * c[2];
		a[2] = b[0] * c[1] - b[1] * c[0];
		return a;
	}

	public static double[] getUnitVector(double[] v) {
		double magnitude = 0;
		for (int i = 0; i < v.length; i++) {
			magnitude = magnitude + v[i] * v[i];
		}
		double[] result = new double[v.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = v[i] / magnitude;
		}
		return result;
	}
	
	
	

	public static double[] getUnitVector(float[] v) {
		double magnitude = 0;
		for (int i = 0; i < v.length; i++) {
			magnitude = magnitude + v[i] * v[i];
		}
		double[] result = new double[v.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = v[i] / magnitude;
		}
		return result;
	}

	public static double[] getAvg(ArrayList<double[]> arr) {
		if (arr.size() == 0)
			return new double[3];
		double[] avg = new double[arr.get(0).length];
		for (int i = 0; i < arr.size(); i++) {
			for (int j = 0; j < avg.length; j++) {
				avg[j] += arr.get(i)[j];
			}
		}
		for (int k = 0; k < avg.length; k++) {
			avg[k] = avg[k] / arr.size();
		}
		return avg;
	}

	/**
	 * This method takes a 2x2 matrix and returns its inverse
	 * 
	 * @param matrix
	 * @return
	 */
	public static double[] getInverse(double[] matrix) {
		double[] result = new double[4];
		double det = getDeterminant(matrix);
		if (det == 0) {
			System.out
					.println("The det was found to be zero when calculating the inverse");
			return result;
		}
		double[] temp = { matrix[3], -matrix[1], -matrix[2], matrix[0] };
		result = scaleArray(temp, 1 / det);
		return result;
	}

	public static double getDeterminant(double[] matrix) {
		return matrix[0] * matrix[3] - matrix[1] * matrix[2];
	}

	// I got this code online
	public static double[][] inverz_matrike(double[][] in) {
		int st_vrs = in.length, st_stolp = in[0].length;
		double[][] out = new double[st_vrs][st_stolp];
		double[][] old = new double[st_vrs][st_stolp * 2];
		double[][] newM = new double[st_vrs][st_stolp * 2];

		for (int v = 0; v < st_vrs; v++) {// ones vector
			for (int s = 0; s < st_stolp * 2; s++) {
				if (s - v == st_vrs)
					old[v][s] = 1;
				if (s < st_stolp)
					old[v][s] = in[v][s];
			}
		}
		// zeros below the diagonal
		for (int v = 0; v < st_vrs; v++) {
			for (int v1 = 0; v1 < st_vrs; v1++) {
				for (int s = 0; s < st_stolp * 2; s++) {
					if (v == v1)
						newM[v][s] = old[v][s] / old[v][v];
					else
						newM[v1][s] = old[v1][s];
				}
			}
			old = prepisi(newM);
			for (int v1 = v + 1; v1 < st_vrs; v1++) {
				for (int s = 0; s < st_stolp * 2; s++) {
					newM[v1][s] = old[v1][s] - old[v][s] * old[v1][v];
				}
			}
			old = prepisi(newM);
		}
		// zeros above the diagonal
		for (int s = st_stolp - 1; s > 0; s--) {
			for (int v = s - 1; v >= 0; v--) {
				for (int s1 = 0; s1 < st_stolp * 2; s1++) {
					newM[v][s1] = old[v][s1] - old[s][s1] * old[v][s];
				}
			}
			old = prepisi(newM);
		}
		for (int v = 0; v < st_vrs; v++) {// rigt part of matrix is invers
			for (int s = st_stolp; s < st_stolp * 2; s++) {
				out[v][s - st_stolp] = newM[v][s];
			}
		}
		return out;
	}

	public static double[][] prepisi(double[][] in) {
		double[][] out = new double[in.length][in[0].length];
		for (int v = 0; v < in.length; v++) {
			for (int s = 0; s < in[0].length; s++) {
				out[v][s] = in[v][s];
			}
		}
		return out;
	}
}

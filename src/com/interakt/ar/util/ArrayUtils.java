package com.interakt.ar.util;

import java.util.ArrayList;


public class ArrayUtils {
	public static double[] parseArrayDouble(String line, int size) {

		String[] getDigitsString = line.split(",");
		double[] result = new double[size];
		for (int i = 0; i < getDigitsString.length; i++) {
			result[i] = handleExponentDouble(getDigitsString[i]);
		}
		return result;
	}

	public static double handleExponentDouble(String numString) {
		String[] temp = numString.split("E");
		if (temp.length == 1)
			return Double.parseDouble(temp[0]);
		else {
			double num = Double.parseDouble(temp[0]);
			double exp = Double.parseDouble(temp[1]);
			double result = Math.pow(num, exp);
			return result;
		}
	}

	public static void printArr(String name, ArrayList<double[]> arr) {
		System.out.print(name + " [ ");
		for (int i = 0; i < arr.size(); i++) {
			if (i == arr.size() - 1)
				printArr("", arr.get(i));
			else {
				printArr("", arr.get(i));
				System.out.print(",");
			}
		}
		System.out.print(" ]\n");
	}



	public static void printArr(ArrayList<String> arr, String name) {
		System.out.println(name);
		for (int i = 0; i < arr.size(); i++) {
			System.out.println(arr.get(i));
		}
	}

	public static void printArr(String name, double[] arr) {
		System.out.print(name + " [ ");
		for (int i = 0; i < arr.length; i++) {
			if (i == arr.length - 1)
				System.out.print(arr[i]);
			else
				System.out.print(arr[i] + " , ");
		}
		System.out.print(" ]\n");
	}

	public static String arrayToString(double[] arr) {
		String result = "";
		for (int i = 0; i < arr.length; i++) {
			if (i == arr.length - 1) {
				result = result + " " + arr[i];
			} else
				result = result + " " + arr[i] + " ";
		}
		result = result + "";
		return result;
	}

	// public static String arrayToString(ArrayList<double[]> arr) {
	// String result = "";
	// for (int i = 0; i < arr.size(); i++) {
	// if (i == arr.size() - 1) {
	// result = result + arrayToString(arr.get(i));
	// } else
	// result = result + arrayToString(arr.get(i)) + "\r\n";
	// }
	// return result;
	// }


}

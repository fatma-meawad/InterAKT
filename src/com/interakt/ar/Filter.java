package com.interakt.ar;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Filter {
	
	public static boolean filter;
	public static ArrayList<String> filtered = new ArrayList<String>();
	public static Semaphore filtering = new Semaphore (1);

}

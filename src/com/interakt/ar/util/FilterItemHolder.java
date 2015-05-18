package com.interakt.ar.util;

import java.util.ArrayList;

public class FilterItemHolder {
	
	public String filter;
	public int value;
	public ArrayList<FilterItemHolder> subItems;
	public FilterItemHolder(String filter){
		this.filter = filter;
		subItems = new ArrayList<FilterItemHolder>();
	}
	
	public void addFilterSubItem(FilterItemHolder item){
		subItems.add(item);
	}

}

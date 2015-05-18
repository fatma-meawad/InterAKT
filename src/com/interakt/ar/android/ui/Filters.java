package com.interakt.ar.android.ui;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.interakt.ar.Filter;
import com.interakt.ar.Settings;
import com.interakt.ar.android.R;
import com.interakt.ar.util.FilterItemHolder;

public class Filters extends LinearLayout {

	private Context context;
	private TextView sourcesTab;
	private TextView categoriesTab;
	private ScrollView body;
	private LinearLayout categoriesLayout;
	private LinearLayout sourcesLayout;

	public Filters(Context context) {
		super(context);
		this.context = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.geo_filters, this);
		sourcesTab = (TextView) findViewById(R.id.sources_tab);
		sourcesTab.setTextColor(0xffff9900);
		categoriesTab = (TextView) findViewById(R.id.categories_tab);
		body = (ScrollView) findViewById(R.id.geo_filter_body);
		categoriesLayout = new LinearLayout(context);
		categoriesLayout.setOrientation(LinearLayout.VERTICAL);
		sourcesLayout = new LinearLayout(context);
		sourcesLayout.setOrientation(LinearLayout.VERTICAL);
		sourcesTab.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				sourcesTab.setTextColor(0xffff9900);
				categoriesTab.setTextColor(0xff999999);
				body.removeAllViews();
				body.addView(sourcesLayout);
			}
		});

		categoriesTab.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				categoriesTab.setTextColor(0xffff9900);
				sourcesTab.setTextColor(0xff999999);
				body.removeAllViews();
				body.addView(categoriesLayout);
			}
		});
	}

	public class FilterListItem extends LinearLayout {

		private TextView text;
		private CheckBox check;
		private LinearLayout subItems;
		private FilterItemHolder holder;
		private boolean parent;
		private boolean isSubItemsVisible;

		public FilterListItem(Context context, FilterItemHolder holder) {
			super(context);
			LayoutInflater inflater = LayoutInflater.from(context);
			inflater.inflate(R.layout.filter_list_item, this);
			text = (TextView) findViewById(R.id.filter_list_item_text);
			check = (CheckBox) findViewById(R.id.filter_list_item_check);
			subItems = (LinearLayout) findViewById(R.id.subItemsBody);
			int value = holder.value;
			if (holder.subItems.size() > 0) {
				
				for (FilterItemHolder item : holder.subItems) {
					subItems.addView(new FilterListItem(context, item));
				
				}
				parent = true;
			}
			text.setText(holder.filter + " (" + value + ")");
			
			this.holder = holder;
			if (!parent) {
				check.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean arg1) {
						if (!arg1) {
							Filter.filtered
									.add(FilterListItem.this.holder.filter);
							Settings.TobeLogged+=0+","+0+","+0+","+0+","+(getParent().getParent().getParent()==categoriesLayout?"category_":"source_")+"check_"+FilterListItem.this.holder.filter+"\n";
						} else {
							Filter.filtered
									.remove(FilterListItem.this.holder.filter);
							Settings.TobeLogged+=0+","+0+","+0+","+0+","+(getParent().getParent().getParent()==categoriesLayout?"category_":"source_")+"uncheck_"+FilterListItem.this.holder.filter+"\n";
							
						}
						try {
							Filter.filtering.acquire();
							Filter.filter = true;
							Filter.filtering.release();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				});

				this.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						check.setChecked(!check.isChecked());

					}
				});

			} else {
				check.setVisibility(View.INVISIBLE);

				this.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						isSubItemsVisible = !isSubItemsVisible;
						if (isSubItemsVisible) {
							text.setTextColor(0xffff9900);
							subItems.setVisibility(View.VISIBLE);
						} else {
							text.setTextColor(0xff999999);
							subItems.setVisibility(View.GONE);
						}

					}
				});
			}

		}

		public void setText(String text) {
			this.text.setText(text);
		}

	}

	public void setCategories(ArrayList<FilterItemHolder> categories) {
		
		for (FilterItemHolder cat : categories) {
			int value = cat.value;
			if (cat.subItems.size() > 0) {
				for (FilterItemHolder item : cat.subItems) {	
					value+=item.value;
				}
			}
			cat.value = value;
		}
			sortItemHolders(categories);
		for (FilterItemHolder cat : categories) {
			sortItemHolders(cat.subItems);
			FilterListItem item = new FilterListItem(context, cat);
			categoriesLayout.addView(item);
		}

	}

	public void setSources(ArrayList<FilterItemHolder> sources) {
		sortItemHolders(sources);
		for (FilterItemHolder src : sources) {
			FilterListItem item = new FilterListItem(context, src);
			item.setText(src.filter + " (" + src.value + ")");
			sourcesLayout.addView(item);
		}
		body.addView(sourcesLayout);

	}
	
	
	public void sortItemHolders(ArrayList<FilterItemHolder> array){
		
		for(int i =0;i<array.size();i++){
			for(int j = 0;j<array.size()-1;j++){
				if(array.get(j).value<array.get(j+1).value){
					FilterItemHolder temp = array.get(j+1);
					array.set(j+1, array.get(j));
					array.set(j, temp);
				}
			}
		}
	}

	public void updating() {
		body.removeAllViews();
		categoriesLayout.removeAllViews();
		sourcesLayout.removeAllViews();

	}

}

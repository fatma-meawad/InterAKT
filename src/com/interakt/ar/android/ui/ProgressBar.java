package com.interakt.ar.android.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.interakt.ar.android.R;

public class ProgressBar extends LinearLayout {

	TextView message;
	Context context;

	public ProgressBar(Context context) {
		super(context);
		this.context = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.progress_bar, this);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.alignWithParent = true;
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		this.setLayoutParams(params);
		message = (TextView) this.findViewById(R.id.main_progressBar_text);
		
	}
	
	
	
	public void setMessage(String text){
		message.setText(text);
	}

	

}

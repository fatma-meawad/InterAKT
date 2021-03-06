package com.interakt.ar.android.ui;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.interakt.ar.android.R;

public class Notifier extends LinearLayout {

	TextView message;
	Context context;

	public Notifier(Context context) {
		super(context);
		this.context = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.notifier, this);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.alignWithParent = true;
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		this.setLayoutParams(params);
		message = (TextView) this.findViewById(R.id.main_notifier_text);
		
	}
	
	
	
	public void setMessage(String text){
		message.setText(text);
	}

}
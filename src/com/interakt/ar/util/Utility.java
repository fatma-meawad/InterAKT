package com.interakt.ar.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.interakt.ar.Settings;

import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class Utility {
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    
    public static void getScreenShot(View view) {
    View v = view.getRootView();
    v.setDrawingCacheEnabled(true);
    Bitmap b = v.getDrawingCache();
    String extr = Environment.getExternalStorageDirectory().toString();
    File myPath = new File(extr, "interAKT_screenshot.jpg");
    FileOutputStream fos = null;
    try {
        fos = new FileOutputStream(myPath);
        b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();
    } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    }
    
    public static void addLog(String s){
    	Time now = new Time();
    	now.setToNow();
    	Settings.TobeLogged += now.toString()+"\t"+s;	
    }
}
package com.interakt.ar.android;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(
        formKey = "", // This is required for backward compatibility but not used
        mailTo = "felsayedmeawad@gmail.com "
    )

public class InterAKTMainApplication extends Application{
	  @Override
      public void onCreate() {
          super.onCreate();
          
          // The following line triggers the initialization of ACRA
          ACRA.init(this);
      }
}

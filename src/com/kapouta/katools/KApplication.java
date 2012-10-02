package com.kapouta.katools;


import android.app.Application;

public abstract class KApplication extends Application {
	protected static KApplication self;

	public abstract String getAnalyticsUACode();
	
	public void onCreate() {
		super.onCreate();
		self = this;
	}

	
}

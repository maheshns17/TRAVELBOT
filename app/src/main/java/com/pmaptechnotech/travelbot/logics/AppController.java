package com.pmaptechnotech.travelbot.logics;


import android.app.Application;
import android.content.Context;

public class AppController extends Application {
	public static final String TAG = AppController.class.getSimpleName();
	private static AppController mInstance;
	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
	}
	public static synchronized AppController getInstance() {
		return mInstance;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
	}
}
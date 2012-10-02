package com.kapouta.katools;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class KActivity extends Activity {
	public static final int GA_DISPATCH_PERIOD = 10;
	public static final String PARENT_URL_KEY = "Parent URL!";
	protected static GoogleAnalyticsTracker tracker = null;

	private String analytics_url;
	private String tracking_name;
	private boolean doNotTrackThisTime;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.doNotTrackThisTime = false;
		if (tracker == null) {
			tracker = GoogleAnalyticsTracker.getInstance();
			// tracker.setDebug(true);
			tracker.startNewSession(getAnalyticsUACode(), this);
			tracker.setSampleRate(100);
			// tracker.setDispatchPeriod(GA_DISPATCH_PERIOD);
		}
	}

	protected GoogleAnalyticsTracker getTracker() {
		return tracker;
	}

	public void onResume() {
		super.onResume();
		if (this.doNotTrackThisTime) {
			this.doNotTrackThisTime = false;
			return;
		}
		// System.out.println("Session: [" + getAnalyticsUACode() + "]");
		// System.out.println("URL: " + getActivityAnalyticsURL());
		tracker.trackPageView(getActivityAnalyticsURL());
		tracker.dispatch();
		// tracker.stopSession();
	}

	public void skipTrackingThisOnce() {
		this.doNotTrackThisTime = true;
	}

	public void onDestroy() {
		// tracker.dispatch();
		// tracker.stopSession();
		super.onDestroy();
	}

	public String getActivityAnalyticsURL() {
		if (this.analytics_url == null) {
			Bundle extras = getIntent().getExtras();

			if (extras != null
					&& extras.getString(KActivity.PARENT_URL_KEY) != null) {

				this.analytics_url = extras.getString(KActivity.PARENT_URL_KEY)
						+ getTrackingName();
			} else
				this.analytics_url = getTrackingName();
		}
		return this.analytics_url;
	}

	public String getTrackingName() {
		if (this.tracking_name == null) {
			this.tracking_name = "/" + this.getClass().getSimpleName();

			this.tracking_name = this.tracking_name
					.replaceFirst("Activity", "");

		}
		return this.tracking_name;
	}

	public String getAnalyticsUACode() {
		KApplication kapp =  (KApplication) this.getApplication();
		return kapp.getAnalyticsUACode();
		// return this.getString(R.string.ga_api_key);
	}

	private void addSelfAsParent(Intent intent) {
		intent.putExtra(KActivity.PARENT_URL_KEY, getActivityAnalyticsURL());
	}

	@Override
	public void startActivity(Intent intent) {
		addSelfAsParent(intent);
		super.startActivity(intent);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		addSelfAsParent(intent);
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	public void startActivityFromChild(Activity child, Intent intent,
			int requestCode) {
		addSelfAsParent(intent);
		super.startActivityFromChild(child, intent, requestCode);
	}

	@Override
	public boolean startActivityIfNeeded(Intent intent, int requestCode) {
		addSelfAsParent(intent);
		return super.startActivityIfNeeded(intent, requestCode);
	}

}

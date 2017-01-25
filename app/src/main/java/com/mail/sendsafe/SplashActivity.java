package com.mail.sendsafe;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mail.sendsafe.callbacks.IItemHandler;
import com.mail.sendsafe.common.AppPreferences;
import com.mail.sendsafe.common.AppSettings;
import com.mail.sendsafe.common.Constants;
import com.mail.sendsafe.tasks.HTTPTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity implements IItemHandler {

	private Dialog dialog = null;

	private boolean isDestroyed = false;
	private GoogleCloudMessaging gcm;
	private String regId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		// new CheckUpdate().check("com.mail.bsecure",SplashActivity.this);

		((TextView) findViewById(R.id.version)).setText("Version : " + applicationVName());

		fetchMSISDN();
		// String kdkdOk = TimeUtils.getTimeZone();
		//
		// Log.e("-=-=-=-=-=-=-=-=-=", TimeUtils.getDeviceDateTime("yyyy-MM-dd
		// hh:mm:ss a"));
		//
		// Log.e("-=-=-=-=-=-=-=-=-=", kdkdk);
	}

	private String applicationVName() {
		String versionName = "";
		PackageManager manager = getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			versionName = info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

	public String registerGCM() {

		gcm = GoogleCloudMessaging.getInstance(this);
		registerInBackground();

		// Log.d("RegisterActivity", "registerGCM - successfully registered with
		// GCM server - regId: " + regId);

		return regId;
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";

				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
					}
					regId = gcm.register(Constants.GOOGLE_PROJECT_ID);
					//Log.d("RegisterActivity", "registerInBackground - regId: " + regId);
					msg = "Device registered, registration ID=" + regId;

				} catch (Exception ex) {
					msg = "Error :" + ex.getMessage();
					//Log.d("RegisterActivity", "Error: " + msg);
				}

				//Log.d("RegisterActivity", "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				AppPreferences.getInstance(SplashActivity.this).addToStore("regID", regId);
				navigateToNextScreen();
			}
		}.execute(null, null, null);
	}

	/**
	 * fetchUG - This method is called when activity is created. It will
	 * communicate with server to get user agent of device.
	 */
	private void fetchMSISDN() {

		String url = AppSettings.getInstance(this).getPropertyValue("ispin");

		HTTPTask task = new HTTPTask(this, this);
		task.disableProgress();
		task.userRequest("", 1, url);

	}

	private void showNetworkDialog() {
		findViewById(R.id.splash_pbar).setVisibility(View.GONE);
		dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.setCancelable(false);
		View view = View.inflate(this, R.layout.nonet, null);
		view.findViewById(R.id.nonet_ok).setOnClickListener(onClick);
		dialog.setContentView(view);
		dialog.show();
	}

	private void closeDialog() {
		if (dialog != null)
			dialog.dismiss();
		dialog = null;
	}

	OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View view) {

			switch (view.getId()) {
			case R.id.nonet_ok:
				closeDialog();
				SplashActivity.this.finish();
				break;

			default:
				break;
			}

		}
	};

	private void showToast(String message) {
		findViewById(R.id.splash_pbar).setVisibility(View.GONE);
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onDestroy() {

		isDestroyed = true;
		super.onDestroy();
	}

	@Override
	public void onFinish(Object results, int requestId) {

		if (isDestroyed)
			return;

		switch (requestId) {
		case 1:

			regId = AppPreferences.getInstance(SplashActivity.this).getFromStore("regID");
			if (TextUtils.isEmpty(regId)) {
				registerGCM();
			} else {
				navigateToNextScreen();
			}

			break;

		default:
			break;
		}

	}

	@Override
	public void onError(String errorCode, int requestType) {

		if (errorCode.equalsIgnoreCase(getString(R.string.nipcyns))) {
			showNetworkDialog();
			return;
		}

		showToast(errorCode);

		navigateToNextScreen();

	}

	private void navigateToNextScreen() {
		String authpin = AppPreferences.getInstance(this).getFromStore("authpin");
		if (authpin.length() == 0) {
			launchActivty(Login.class);
		} else {
			launchActivty(LoginInWithPin.class);
		}
	}

	private void launchActivty(Class<?> cls) {

		Intent mainIntent = new Intent(SplashActivity.this, cls);
		SplashActivity.this.startActivity(mainIntent);

		SplashActivity.this.finish();
	}

}
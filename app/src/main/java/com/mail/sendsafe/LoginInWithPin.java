package com.mail.sendsafe;

import org.json.JSONException;
import org.json.JSONObject;

import com.mail.sendsafe.callbacks.IItemHandler;
import com.mail.sendsafe.common.AppPreferences;
import com.mail.sendsafe.common.AppSettings;
import com.mail.sendsafe.tasks.HTTPTask;
import com.mail.sendsafe.tasks.HTTPostJson;
import com.mail.sendsafe.utils.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class LoginInWithPin extends FragmentActivity implements IItemHandler {

	private EditText ed_pin = null;

	private int counter = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.pin_layout_new);

		ed_pin = (EditText) findViewById(R.id.ed_pin);
		ed_pin.requestFocus();

		findViewById(R.id.tv_forgotpin).setOnClickListener(onClick);
		findViewById(R.id.tv_addaccount).setOnClickListener(onClick);
		// findViewById(R.id.tv_addaccount).setVisibility(View.GONE);

		ed_pin.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				keyboard.showSoftInput(ed_pin, 0);
			}
		}, 50);

		ed_pin.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Integer textlength1 = ed_pin.getText().length();
				if (textlength1 >= 4) {
					makePinRequest();
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
		});
		checkUpdate();
	}

	OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View view) {

			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(view.getWindowToken(), 0);

			switch (view.getId()) {

			case R.id.tv_forgotpin:
				launchActivity(Login.class);
				LoginInWithPin.this.finish();
				break;
			case R.id.tv_addaccount:
				// showToast(R.string.acnt);

				launchActivity(Login.class);
				LoginInWithPin.this.finish();

				break;

			default:
				break;
			}

		}
	};
	protected void checkUpdate() {
		// TODO Auto-generated method stub
		String url = AppSettings.getInstance(this).getPropertyValue("curr_versn");

		HTTPTask task = new HTTPTask(this, this);
		task.disableProgress();
		task.userRequest("", 2, url);
	}
	private void makePinRequest() {
		try {
			String pin = ed_pin.getText().toString().trim();

			if (pin.length() == 0) {
				ed_pin.requestFocus();
				showToast(R.string.pepin);
				return;
			}

			// String authpin =
			// AppPreferences.getInstance(LoginInWithPin.this).getFromStore("authpin");
			// if (authpin.equalsIgnoreCase(pin)) {
			// launchActivity(Bsecure.class);
			// LoginInWithPin.this.finish();
			//
			// return;
			// }
			String url = AppSettings.getInstance(this).getPropertyValue("login1");
			JSONObject object = new JSONObject();
			object.put("email", AppPreferences.getInstance(LoginInWithPin.this).getFromStore("email"));
			object.put("pin", pin);
			HTTPostJson post = new HTTPostJson(this, this, object.toString(), 1);
			post.setContentType("application/json");
			post.execute(url, "");
			Utils.showProgress(getString(R.string.pwait), this);

			// counter++;
			//
			// if (counter > 3) {
			// launchActivity(Login.class);
			// LoginInWithPin.this.finish();
			// return;
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onError(String errorData, int requestType) {
		showToast(errorData);
	}

	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	private void launchActivity(Class<?> cls) {
		Intent mainIntent = new Intent(this, cls);
		startActivity(mainIntent);
	}

	public void showToast(int text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onFinish(Object results, int requestType) {
		Utils.dismissProgress();

		try {

			switch (requestType) {

			case 1:
				parseResponse((String) results, requestType);
				break;
				case 2:
					JSONObject json;
					try {
						json = new JSONObject(results.toString());
						updatesAvaliable(json);
					} catch (JSONException e) {
						//
						e.printStackTrace();
					}

					break;
			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void parseResponse(String response, int requestId) throws Exception {

		if (response != null && response.length() > 0) {

			JSONObject jsonObject = new JSONObject(response);

			// if (jsonObject.optString("statuscode").equalsIgnoreCase("145") &&
			// requestId != -1) {
			if (jsonObject.optString("status").equalsIgnoreCase("0")) {
				// addToStore("authpin", ((EditText)
				// findViewById(R.id.et_pin)).getText().toString().trim());
				launchActivity(Bsecure.class);
				showToast(jsonObject.optString("statusdescription"));
				LoginInWithPin.this.finish();
				return;
			}
			// showToast(jsonObject.optString("statusdescription"));
			counter++;
			showToast(jsonObject.optString("statusdescription"));
			if (counter > 3) {
				launchActivity(Login.class);
				LoginInWithPin.this.finish();
				showToast(jsonObject.optString("statusdescription"));
				return;
			}
		}
	}
	private void updatesAvaliable(final JSONObject responseObj) {
		// TODO Auto-generated method stub

		PackageInfo pInfo = null;

		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		String versionName = pInfo.versionName;
		if (responseObj.has("status") && responseObj.optString("status").equalsIgnoreCase("0")) {

			String latestVersion = responseObj.optString("currentversion");

			if (!latestVersion.equals(versionName)) {

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("New Update Available")
						.setMessage("There is newer version of this application available, click OK to upgrade now?")
						.setCancelable(false).setIcon(R.drawable.ic_launcher)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							// if the user agrees to upgrade
							public void onClick(DialogInterface dialog, int id) {

								try {
									startActivity(new Intent(Intent.ACTION_VIEW,
											Uri.parse("market://details?id=" + getPackageName())));
								} catch (android.content.ActivityNotFoundException anfe) {
									startActivity(new Intent(Intent.ACTION_VIEW, Uri
											.parse("http://play.google.com/store/apps/details?id" + getPackageName())));
								}
							}
						}).setNegativeButton("Remind Later", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});

				builder.create().show();

			}

		}

	}

}

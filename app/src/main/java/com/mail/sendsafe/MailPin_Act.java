package com.mail.sendsafe;

import org.json.JSONObject;

import com.mail.sendsafe.callbacks.IItemHandler;
import com.mail.sendsafe.tasks.HTTPostJson;
import com.mail.sendsafe.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class MailPin_Act extends FragmentActivity implements IItemHandler {

	private EditText ed_pin = null;

	private Intent intent = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.pin_layout_new);

		intent = getIntent();

		ed_pin = (EditText) findViewById(R.id.ed_pin);

		ed_pin.requestFocus();
		findViewById(R.id.tv_forgotpin).setOnClickListener(onClick);
		findViewById(R.id.tv_addaccount).setOnClickListener(onClick);
		findViewById(R.id.tv_forgotpin).setVisibility(View.GONE);
		findViewById(R.id.tv_addaccount).setVisibility(View.GONE);

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

	}

	OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View view) {

			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(view.getWindowToken(), 0);

			switch (view.getId()) {

			case R.id.tv_forgotpin:
				launchActivity(Login.class);
				break;
			case R.id.tv_addaccount:
				launchActivity(Login.class);
				break;

			default:
				break;
			}

		}
	};

	private void makePinRequest() {
		try {
			String ed_pin1 = ed_pin.getText().toString().trim();
			String pin = intent.getStringExtra("pin");
			String composeid = intent.getStringExtra("composeid");
			String chkmsg = intent.getStringExtra("chkmsg");
			String msgid = intent.getStringExtra("msgid");

			if (ed_pin1.length() == 0) {
				ed_pin.requestFocus();
				showToast(R.string.pepin);
				return;
			}
			if (!ed_pin1.matches(pin)) {
				ed_pin.requestFocus();
				showToast(R.string.ivipepin);
				return;
			}
			String url = intent.getStringExtra("url");
			JSONObject object = new JSONObject();
			object.put("composeid", composeid);
			object.put("chkmsg", chkmsg);
			object.put("msgid", msgid);
			object.put("pin", ed_pin1);
			HTTPostJson post = new HTTPostJson(this, this, object.toString(), 1);
			post.setContentType("application/json");
			post.execute(url, "");
			Utils.showProgress(getString(R.string.pwait), this);
		} catch (Exception e) {

		}
	}

	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

	private void launchActivity(Class<?> cls) {
		Intent mainIntent = new Intent(this, cls);
		startActivity(mainIntent);
	}

	@Override
	public void onFinish(Object results, int requestType) {
		Utils.dismissProgress();

		try {

			switch (requestType) {

			case 1:
				parseMailPinResponse((String) results, requestType);
				Utils.dismissProgress();
				break;

			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onError(String errorData, int requestType) {
		showToast(errorData);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == 4) {

			setResult(RESULT_CANCELED);
			MailPin_Act.this.finish();

		}

		return super.onKeyDown(keyCode, event);
	}

	public void showToast(int text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	private void parseMailPinResponse(String response, int requestId) throws Exception {

		if (response != null && response.length() > 0) {

			JSONObject jsonObject = new JSONObject(response);

			if (jsonObject.optString("status").equalsIgnoreCase("0")) {

				setResult(RESULT_OK);
				MailPin_Act.this.finish();
				return;
			}
			if (jsonObject.optString("status").equalsIgnoreCase("1")) {

				setResult(RESULT_OK);
				MailPin_Act.this.finish();
				return;
			}
			// showToast(jsonObject.optString("statusdescription"));

		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	public boolean emailValidation(String email) {

		if (email == null || email.length() == 0 || email.indexOf("@") == -1 || email.indexOf(" ") != -1) {
			return false;
		}
		int emailLenght = email.length();
		int atPosition = email.indexOf("@");

		String beforeAt = email.substring(0, atPosition);
		String afterAt = email.substring(atPosition + 1, emailLenght);

		if (beforeAt.length() == 0 || afterAt.length() == 0) {
			return false;
		}
		if (email.charAt(atPosition - 1) == '.') {
			return false;
		}
		if (email.charAt(atPosition + 1) == '.') {
			return false;
		}
		if (afterAt.indexOf(".") == -1) {
			return false;
		}
		char dotCh = 0;
		for (int i = 0; i < afterAt.length(); i++) {
			char ch = afterAt.charAt(i);
			if ((ch == 0x2e) && (ch == dotCh)) {
				return false;
			}
			dotCh = ch;
		}
		if (afterAt.indexOf("@") != -1) {
			return false;
		}
		int ind = 0;
		do {
			int newInd = afterAt.indexOf(".", ind + 1);

			if (newInd == ind || newInd == -1) {
				String prefix = afterAt.substring(ind + 1);
				if (prefix.length() > 1 && prefix.length() < 6) {
					break;
				} else {
					return false;
				}
			} else {
				ind = newInd;
			}
		} while (true);
		dotCh = 0;
		for (int i = 0; i < beforeAt.length(); i++) {
			char ch = beforeAt.charAt(i);
			if (!((ch >= 0x30 && ch <= 0x39) || (ch >= 0x41 && ch <= 0x5a) || (ch >= 0x61 && ch <= 0x7a) || (ch == 0x2e)
					|| (ch == 0x2d) || (ch == 0x5f))) {
				return false;
			}
			if ((ch == 0x2e) && (ch == dotCh)) {
				return false;
			}
			dotCh = ch;
		}
		return true;
	}

}

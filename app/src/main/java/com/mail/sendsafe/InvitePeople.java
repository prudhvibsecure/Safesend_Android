package com.mail.sendsafe;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mail.sendsafe.callbacks.IItemHandler;
import com.mail.sendsafe.common.AppSettings;
import com.mail.sendsafe.tasks.HTTPostJson;
import com.mail.sendsafe.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

public class InvitePeople extends AppCompatActivity implements IItemHandler {
	private AutoCompleteTextView et_invite = null;
	private ArrayList<String> list;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.invite_people);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.invite_peple);
		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		et_invite = (AutoCompleteTextView) findViewById(R.id.invi_people);
		et_invite.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				getSearchContacts(s.toString());
			}
		});
		findViewById(R.id.invit_submit).setOnClickListener(onClick);

	}

	protected void getSearchContacts(String searchkey) {
		try {
			String url = AppSettings.getInstance(this).getPropertyValue("view_contacts");
			JSONObject object = new JSONObject();
			object.put("email", getFromStore("email"));

			HTTPostJson post = new HTTPostJson(this, this, object.toString(), 2);
			post.setContentType("application/json");
			post.execute(url, "");
			// Utils.showProgress(getString(R.string.pwait), this);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View view) {

			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(view.getWindowToken(), 0);

			switch (view.getId()) {

			case R.id.invit_submit:
				referalpeople();
			default:
				break;
			}

		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:

			InvitePeople.this.finish();

			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	protected void referalpeople() {
		// TODO Auto-generated method stub
		try {

			String imail = ((EditText) findViewById(R.id.invi_people)).getText().toString().trim();

			if (imail.length() == 0) {
				showToast(R.string.peyner);
				((EditText) findViewById(R.id.invi_people)).requestFocus();
				return;
			}
			if (!emailValidation(imail)) {
				showToast(R.string.peavei);
				return;
			}
			String url = AppSettings.getInstance(this).getPropertyValue("refer_people");
			JSONObject object = new JSONObject();
			object.put("email", getFromStore("email"));
			object.put("iemail", imail);

			HTTPostJson post = new HTTPostJson(this, this, object.toString(), 1);
			post.setContentType("application/json");
			post.execute(url, "");
			Utils.showProgress(getString(R.string.pwait), this);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onFinish(Object results, int requestType) {
		Utils.dismissProgress();
		try {

			switch (requestType) {
			case 1:
				parseReferalResponse((String) results, requestType);
				break;
			case 2:

				if (results != null) {
					JSONObject object = new JSONObject(results.toString());
					if (object.has("status") && object.optString("status").equalsIgnoreCase("0")) {
						JSONArray array = object.getJSONArray("addressbook_detail");
						if (array != null && array.length() > 0) {
							list = new ArrayList<String>();
							for (int i = 0; i < array.length(); i++) {
								// Contacts map = new Contacts();
								JSONObject jsonobject = array.getJSONObject(i);
								String fname = jsonobject.getString("fname");
								String lname = jsonobject.getString("lname");
								String emailid = jsonobject.getString("emailid");

								list.add(emailid);
							}
							ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
									android.R.layout.select_dialog_singlechoice, list);
							et_invite.setAdapter(adapter);
							et_invite.setThreshold(1);
						}

					} else if (results != null) {

						if (object.has("status") && object.optString("status").equalsIgnoreCase("1")) {

							showToast(object.optString("statusdescription"));
						}
					}
				}

				break;
			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void addToStore(String key, String value) {
		SharedPreferences pref = this.getSharedPreferences("bsecure", MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(key, value);
		editor.commit();
	}

	private String getFromStore(String key) {
		SharedPreferences pref = this.getSharedPreferences("bsecure", MODE_PRIVATE);
		String res = pref.getString(key, "");
		return res;
	}

	private void parseReferalResponse(String response, int requestType) throws Exception {
		// TODO Auto-generated method stub
		// Log.e("-=-=-=-=-=-=-=-=-", response + "");

		if (response != null && response.length() > 0) {
			response = response.trim();
			JSONObject jsonObject = new JSONObject(response);

			if (jsonObject.has("status") && jsonObject.optString("status").equalsIgnoreCase("0")) {
				showToast(jsonObject.optString("statusdescription"));
				InvitePeople.this.finish();
				return;
			}
			showToast(jsonObject.optString("statusdescription"));

		}

	}

	@Override
	public void onError(String errorData, int requestType) {
		Utils.dismissProgress();
		showToast(errorData);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == 4) {
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

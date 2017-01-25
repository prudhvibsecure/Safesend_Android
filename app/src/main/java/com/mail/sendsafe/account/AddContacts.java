package com.mail.sendsafe.account;

import org.json.JSONObject;

import com.mail.sendsafe.R;
import com.mail.sendsafe.callbacks.IItemHandler;
import com.mail.sendsafe.common.AppPreferences;
import com.mail.sendsafe.common.AppSettings;
import com.mail.sendsafe.tasks.HTTPostJson;
import com.mail.sendsafe.utils.Utils;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddContacts extends AppCompatActivity implements IItemHandler {
	private EditText add_fname, add_lname, add_email, add_phno;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_contacts);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.asub);
		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		add_fname = (EditText) findViewById(R.id.ad_fname);

		add_lname = (EditText) findViewById(R.id.ad_lname);

		add_email = (EditText) findViewById(R.id.ad_email);

		add_phno = (EditText) findViewById(R.id.ad_phno);

		findViewById(R.id.ad_cont).setOnClickListener(onClick);
	}

	OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View view) {

			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(view.getWindowToken(), 0);

			switch (view.getId()) {

			case R.id.ad_cont:
				addContacts();
				break;

			default:
				break;
			}

		}
	};

	protected void addContacts() {

		try {

			String add_fname = ((EditText) findViewById(R.id.ad_fname)).getText().toString().trim();

			if (add_fname.length() == 0) {
				showToast(R.string.apfn);
				((EditText) findViewById(R.id.ad_fname)).requestFocus();
				return;
			}
			if (!isValidName(add_fname)){
				showToast(R.string.pechars);
				((EditText) findViewById(R.id.ad_fname)).requestFocus();
				return;
			}
			String add_lname = ((EditText) findViewById(R.id.ad_lname)).getText().toString().trim();

			if (add_lname.length() == 0) {
				showToast(R.string.alfn);
				((EditText) findViewById(R.id.ad_lname)).requestFocus();
				return;
			}
			if (!isValidName(add_lname)){
				showToast(R.string.pechars);
				((EditText) findViewById(R.id.ad_lname)).requestFocus();
				return;
			}
			String add_email = ((EditText) findViewById(R.id.ad_email)).getText().toString().trim();

			if (add_email.length() == 0) {
				showToast(R.string.peei);
				((EditText) findViewById(R.id.ad_email)).requestFocus();
				return;
			}
			if (!emailValidation(add_email)) {
				showToast(R.string.peavei);
				((EditText) findViewById(R.id.ad_email)).requestFocus();
				return;
			}

			String add_ph = ((EditText) findViewById(R.id.ad_phno)).getText().toString().trim();

			String url = AppSettings.getInstance(this).getPropertyValue("single_cont");
			JSONObject object = new JSONObject();

			object.put("email", AppPreferences.getInstance(AddContacts.this).getFromStore("email"));
			object.put("fname", add_fname);
			object.put("lname", add_lname);
			object.put("emailid", add_email);
			object.put("phoneno", add_ph);

			HTTPostJson post = new HTTPostJson(this, this, object.toString(), 1);
			post.setContentType("application/json");
			post.execute(url, "");
			Utils.showProgress(getString(R.string.pwait), this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:

			AddContacts.this.finish();

			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onFinish(Object results, int requestType) {
		Utils.dismissProgress();

		try {

			switch (requestType) {
			case 1:
				if (results != null) {
					JSONObject object = new JSONObject(results.toString());
					if (object.has("status") && object.optString("status").equalsIgnoreCase("0")) {
						AddContacts.this.finish();
						showToast(object.optString("msg"));
						return;
					}
					showToast(object.optString("msg"));
				}
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
		Utils.dismissProgress();
		showToast(errorData);
	}

	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	public void showToast(int text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
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
	private boolean isValidName(String name) {
		String NAME_PATTERN ="[a-zA-z]*(['\\s]+[a-z]*)*";

		Pattern pattern = Pattern.compile(NAME_PATTERN);
		Matcher matcher = pattern.matcher(name);
		return matcher.matches();
	}
}
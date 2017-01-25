package com.mail.sendsafe.account;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mail.sendsafe.R;
import com.mail.sendsafe.callbacks.IItemHandler;
import com.mail.sendsafe.common.AppPreferences;
import com.mail.sendsafe.common.AppSettings;
import com.mail.sendsafe.tasks.HTTPostJson;
import com.mail.sendsafe.utils.Utils;

import android.content.Context;
import android.content.Intent;
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

public class Contact_Edit extends AppCompatActivity implements IItemHandler {
	private EditText add_fname, add_lname, add_email, add_phno;
	private String abid;
	private ArrayList<String> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_contacts);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.eesub);
		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		Intent intabid = getIntent();
		abid = intabid.getStringExtra("abid");

		add_fname = (EditText) findViewById(R.id.ad_fname);

		add_lname = (EditText) findViewById(R.id.ad_lname);

		add_email = (EditText) findViewById(R.id.ad_email);

		add_phno = (EditText) findViewById(R.id.ad_phno);

		findViewById(R.id.ad_cont).setOnClickListener(onClick);

		try {
			String url = AppSettings.getInstance(this).getPropertyValue("editcontact_view");
			JSONObject object = new JSONObject();

			object.put("email", AppPreferences.getInstance(this).getFromStore("email"));
			object.put("abid", abid);

			HTTPostJson post = new HTTPostJson(this, this, object.toString(), 1);
			post.setContentType("application/json");
			post.execute(url, "");
			Utils.showProgress(getString(R.string.pwait), this);
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
			String add_lname = ((EditText) findViewById(R.id.ad_lname)).getText().toString().trim();

			if (add_lname.length() == 0) {
				showToast(R.string.alfn);
				((EditText) findViewById(R.id.ad_lname)).requestFocus();
				return;
			}
			String add_email = ((EditText) findViewById(R.id.ad_email)).getText().toString().trim();

			if (add_email.length() == 0) {
				showToast(R.string.peei);
				((EditText) findViewById(R.id.ad_email)).requestFocus();
				return;
			}
			String add_ph = ((EditText) findViewById(R.id.ad_phno)).getText().toString().trim();

			String url = AppSettings.getInstance(this).getPropertyValue("editcontact_update");
			JSONObject object = new JSONObject();

			object.put("email", AppPreferences.getInstance(this).getFromStore("email"));
			object.put("abid", abid);
			object.put("fname", add_fname);
			object.put("lname", add_lname);
			object.put("emailid", add_email);
			object.put("phoneno", add_ph);

			HTTPostJson post = new HTTPostJson(this, this, object.toString(), 2);
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

			Contact_Edit.this.finish();

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
						JSONArray array = object.getJSONArray("addressbook_detail");
						if (array != null && array.length() > 0) {
							list = new ArrayList<String>();
							for (int i = 0; i < array.length(); i++) {
								JSONObject jObject = array.getJSONObject(i);

								String fname = jObject.optString("fname");
								String lname = jObject.optString("lname");
								String email = jObject.optString("emailid");
								String phone = jObject.optString("phoneno");
								add_fname.setText(fname);
								add_email.setText(email);
								if (lname.equalsIgnoreCase("null")) {
									add_lname.setText("");
								}else {
									add_lname.setText(lname);
								}
								if (phone.equalsIgnoreCase("null")) {
									add_phno.setText("");
								}else {
									add_phno.setText(phone);
								}
							}
						}
					}

				}
				break;
			case 2:
				if (results != null) {
					JSONObject object = new JSONObject(results.toString());
					if (object.has("status") && object.optString("status").equalsIgnoreCase("0")) {
						Contact_Edit.this.finish();
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

}
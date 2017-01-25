package com.mail.sendsafe.account;

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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TransferAmount extends AppCompatActivity implements IItemHandler {

	private TextView v_fullname = null, v_bank_details;
	private TextView v_acc = null;
	private TextView v_ifsc = null;
	private TextView v_branchloc = null;
	private TextView v_branchname = null;
	private TextView v_accagain = null;
	private TextView v_swift = null;
	private TextView v_acctype = null;
	private EditText v_amount = null;

	private String fullname, accnum, typeacc, bname, ifsc, swift, location, amount, bid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_wallwt_bank_details);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.bthead);
		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		v_fullname = (TextView) findViewById(R.id.v_fullname);

		v_acc = (TextView) findViewById(R.id.v_acc);

		// v_accagain = (TextView) findViewById(R.id.wlt_cnfrmacc);

		v_branchloc = (TextView) findViewById(R.id.v_brlocation);

		v_branchname = (TextView) findViewById(R.id.v_bankname);

		v_ifsc = (TextView) findViewById(R.id.v_ifsc);

		v_swift = (TextView) findViewById(R.id.v_swift);

		v_acctype = (TextView) findViewById(R.id.v_acctype);
		// v_bank_details = (TextView) findViewById(R.id.up_dt_bank);

		v_amount = (EditText) findViewById(R.id.v_amount);

		findViewById(R.id.v_transfer).setOnClickListener(onClick);
		// findViewById(R.id.up_dt_bank).setOnClickListener(onClick);

		v_amount.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					String amt = v_amount.getText().toString();
					amt = amt.trim();

					if (amt.length() == 0) {
						showToast(R.string.enteramt);
						v_amount.setSelection(amt.length());
						return;
					}

				}
			}
		});
		viewDetailUpdate();
	}

	private void viewDetailUpdate() {

		try {
			String url = AppSettings.getInstance(this).getPropertyValue("wallet_edit_detail");
			JSONObject object = new JSONObject();
			object.put("email", AppPreferences.getInstance(this).getFromStore("email"));
			HTTPostJson post = new HTTPostJson(this, this, object.toString(), 1);
			post.setContentType("application/json");
			post.execute(url, "");
			Utils.showProgress(getString(R.string.pwait), this);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.bank_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:

			TransferAmount.this.finish();

			break;

		case R.id.action_bank:
			Intent i = new Intent(getApplicationContext(), UpdateBankDetails.class);
			i.putExtra("bid", bid);

			finish();
			startActivity(i);
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View view) {

			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(view.getWindowToken(), 0);

			switch (view.getId()) {

			case R.id.v_transfer:
				transferAmount();
				break;

			default:
				break;
			}

		}
	};

	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	protected void transferAmount() {

		try {
			String t_amount = ((EditText) findViewById(R.id.v_amount)).getText().toString().trim();

			if (t_amount.length() == 0) {
				showToast(R.string.enteramt);
				((EditText) findViewById(R.id.v_amount)).requestFocus();
				return;
			}
			String url = AppSettings.getInstance(this).getPropertyValue("w_trns_amt");//
			JSONObject object = new JSONObject();
			object.put("email", AppPreferences.getInstance(this).getFromStore("email"));
			object.put("tamount", t_amount);
			HTTPostJson post = new HTTPostJson(this, this, object.toString(), 2);
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

				if (results != null) {
					JSONObject object = new JSONObject(results.toString());
					if (object.has("status") && object.optString("status").equalsIgnoreCase("0")) {

						JSONArray array = object.getJSONArray("bank_details");
						if (array != null && array.length() > 0) {
							for (int i = 0; i < array.length(); i++) {
								JSONObject jObject = array.getJSONObject(i);

								fullname = jObject.optString("bankname");
								accnum = jObject.optString("accno");
								typeacc = jObject.optString("type");
								bname = jObject.optString("branch");
								ifsc = jObject.optString("ifsccode");
								swift = jObject.optString("swiftcode");
								location = jObject.optString("address");
								bid = jObject.optString("bankid");

								v_fullname.setText(fullname);
								v_acc.setText(accnum);
								v_branchloc.setText(location);
								v_branchname.setText(bname);
								v_ifsc.setText(ifsc);
								v_swift.setText(swift);

								v_acctype.setText(typeacc);

							}
						}
					}
				}
				break;
			case 2:
				if (results != null) {
					JSONObject object = new JSONObject(results.toString());
					if (object.has("status") && object.optString("status").equalsIgnoreCase("0")) {
						showToast(object.optString("statusdescription"));
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

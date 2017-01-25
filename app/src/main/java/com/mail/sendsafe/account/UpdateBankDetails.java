package com.mail.sendsafe.account;

import java.util.ArrayList;
import java.util.Collections;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateBankDetails extends AppCompatActivity implements IItemHandler {

	private EditText wt_fullname = null;
	private EditText wt_acc = null;
	private EditText wt_ifsc = null;
	private EditText wt_branchloc = null;
	private EditText wt_branchname = null;
	private EditText wt_accagain = null;
	private EditText wt_swift = null;
	private TextView wt_submit = null, wt_update;

	private Spinner acc_type;

	private String fullname, accnum, typeacc, bname, ifsc, swift, location, amount, selaccount, bidd;

	private ArrayList<String> list;
	private String[] typeaccount, typeaccount2;
	private int position;
	private ArrayAdapter<String> acct_adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wallet_update_bank);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.wlthead);
		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		Intent in = getIntent();

		// Get JSON values from previous intent
		bidd = in.getStringExtra("bid");

		wt_fullname = (EditText) findViewById(R.id.wlt_fname);

		wt_acc = (EditText) findViewById(R.id.wlt_account);

		wt_accagain = (EditText) findViewById(R.id.wlt_cnfrmacc);

		wt_branchloc = (EditText) findViewById(R.id.wlt_brnloc);

		wt_branchname = (EditText) findViewById(R.id.wlt_branch);

		wt_ifsc = (EditText) findViewById(R.id.wlt_ifse);

		wt_swift = (EditText) findViewById(R.id.wlt_swift);

		acc_type = (Spinner) findViewById(R.id.wlt_actype);

		wt_submit = (TextView) findViewById(R.id.wlt_update);
		wt_update = (TextView) findViewById(R.id.wlt_update_data);

		findViewById(R.id.wlt_update).setOnClickListener(onClick);
		findViewById(R.id.wlt_update_data).setOnClickListener(onClick);

		typeaccount = new String[] { "Select Account", "SAVINGS BANK", "CURRENT ACCOUNT", "CASH CREDIT", "LOAN ACCOUNT",
				"NRE ACCOUNT", "CREDIT CARD" };

		acct_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, typeaccount);
		acct_adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
		acc_type.setAdapter(acct_adapter);

		wt_fullname.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					String name = wt_fullname.getText().toString();
					name = name.trim();

					if (name.length() == 0) {
						showToast(R.string.bpebn);
						wt_fullname.setSelection(name.length());
						return;
					}

				}
			}
		});

		wt_acc.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					String account = wt_acc.getText().toString();
					account = account.trim();

					if (account.length() == 0 || account.length() < 8) {
						showToast(R.string.bpebac);
						wt_acc.setSelection(account.length());
						return;
					}
				}
			}
		});

		wt_accagain.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {

					String cpaccount = wt_accagain.getText().toString();
					cpaccount = cpaccount.trim();

					if (cpaccount.length() == 0 || cpaccount.length() < 9) {
						showToast(R.string.bpebcac);
						wt_accagain.setSelection(cpaccount.length());
						return;
					}

					String account = wt_acc.getText().toString();
					account = account.trim();

					if (!account.equals(cpaccount)) {
						showToast(R.string.bcpmm);
						return;
					}

				}
			}
		});

		wt_accagain.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before, int count) {

				int textlength1 = wt_acc.getText().length();
				int textlength2 = wt_accagain.getText().length();

				String account = wt_acc.getText().toString();
				account = account.trim();

				String cpaccount = wt_accagain.getText().toString();
				cpaccount = cpaccount.trim();

				if (textlength1 == textlength2) {
					if (!account.equals(cpaccount)) {
						showToast(R.string.bcpmm);
						return;
					}
				}

			}

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
		});
		viewDetailUpdate();
	}

	private void viewDetailUpdate() {

		try {

			String url = AppSettings.getInstance(this).getPropertyValue("wallet_edit_detail");
			JSONObject object = new JSONObject();
			object.put("email", AppPreferences.getInstance(UpdateBankDetails.this).getFromStore("email"));

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

			UpdateBankDetails.this.finish();

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

			case R.id.wlt_update:
				addBankDetails();
				break;
			case R.id.wlt_update_data:
				updateBankDetails();
				break;

			default:
				break;
			}

		}
	};

	private void addBankDetails() {

		try {

			String wt_fname = ((EditText) findViewById(R.id.wlt_fname)).getText().toString().trim();

			if (wt_fname.length() == 0) {
				showToast(R.string.bpebn);
				((EditText) findViewById(R.id.wlt_fname)).requestFocus();
				return;
			}

			String wt_acc = ((EditText) findViewById(R.id.wlt_account)).getText().toString().trim();

			if (wt_acc.length() == 0) {
				showToast(R.string.bpebac);
				((EditText) findViewById(R.id.wlt_account)).requestFocus();
				return;
			}

			String wt_againacc = ((EditText) findViewById(R.id.wlt_cnfrmacc)).getText().toString().trim();

			if (wt_againacc.length() == 0) {
				showToast(R.string.bpebcac);
				((EditText) findViewById(R.id.wlt_cnfrmacc)).requestFocus();
				return;
			}
			String wt_bankname = ((EditText) findViewById(R.id.wlt_branch)).getText().toString().trim();

			if (wt_bankname.length() == 0) {
				showToast(R.string.bpebname);
				((EditText) findViewById(R.id.wlt_branch)).requestFocus();
				return;
			}

			String wt_branchloc = ((EditText) findViewById(R.id.wlt_brnloc)).getText().toString().trim();

			if (wt_branchloc.length() == 0) {
				showToast(R.string.bpebrl);
				((EditText) findViewById(R.id.wlt_brnloc)).requestFocus();
				return;
			}
			String wt_ifse = ((EditText) findViewById(R.id.wlt_ifse)).getText().toString().trim();

			if (wt_ifse.length() == 0) {
				showToast(R.string.bifc);
				((EditText) findViewById(R.id.wlt_ifse)).requestFocus();
				return;
			}
			String wt_swft = ((EditText) findViewById(R.id.wlt_swift)).getText().toString().trim();

			if (wt_swft.length() == 0) {
				showToast(R.string.bswft);
				((EditText) findViewById(R.id.wlt_brnloc)).requestFocus();
				return;
			}

			String account_type = acc_type.getSelectedItem().toString();

			// String et_city = ((Spinner)
			// findViewById(R.id.et_city)).toString();
			if (account_type.length() == 0) {
				showToast(R.string.bpetyp);
				((Spinner) findViewById(R.id.wlt_actype)).requestFocus();
				return;
			}

			String url = AppSettings.getInstance(this).getPropertyValue("wallet_add_bank");
			JSONObject object = new JSONObject();
			object.put("email", AppPreferences.getInstance(this).getFromStore("email"));
			object.put("bankname", wt_fname);
			object.put("accno", wt_acc);
			object.put("typeofaccount", account_type);
			object.put("branchname", wt_bankname);
			object.put("ifsc", wt_ifse);
			object.put("swift", wt_swft);
			object.put("location", wt_branchloc);

			HTTPostJson post = new HTTPostJson(this, this, object.toString(), 1);
			post.setContentType("application/json");
			post.execute(url, "");
			Utils.showProgress(getString(R.string.pwait), this);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void updateBankDetails() {

		try {

			String wt_fname = ((EditText) findViewById(R.id.wlt_fname)).getText().toString().trim();

			if (wt_fname.length() == 0) {
				showToast(R.string.bpebn);
				((EditText) findViewById(R.id.wlt_fname)).requestFocus();
				return;
			}

			String wt_acc = ((EditText) findViewById(R.id.wlt_account)).getText().toString().trim();

			if (wt_acc.length() == 0) {
				showToast(R.string.bpebac);
				((EditText) findViewById(R.id.wlt_account)).requestFocus();
				return;
			}

			String wt_againacc = ((EditText) findViewById(R.id.wlt_cnfrmacc)).getText().toString().trim();

			if (wt_againacc.length() == 0) {
				showToast(R.string.bpebcac);
				((EditText) findViewById(R.id.wlt_cnfrmacc)).requestFocus();
				return;
			}
			String wt_bankname = ((EditText) findViewById(R.id.wlt_branch)).getText().toString().trim();

			if (wt_bankname.length() == 0) {
				showToast(R.string.bpebname);
				((EditText) findViewById(R.id.wlt_branch)).requestFocus();
				return;
			}

			String wt_branchloc = ((EditText) findViewById(R.id.wlt_brnloc)).getText().toString().trim();

			if (wt_branchloc.length() == 0) {
				showToast(R.string.bpebrl);
				((EditText) findViewById(R.id.wlt_brnloc)).requestFocus();
				return;
			}
			String wt_ifse = ((EditText) findViewById(R.id.wlt_ifse)).getText().toString().trim();

			if (wt_ifse.length() == 0) {
				showToast(R.string.bifc);
				((EditText) findViewById(R.id.wlt_ifse)).requestFocus();
				return;
			}
			String wt_swft = ((EditText) findViewById(R.id.wlt_swift)).getText().toString().trim();

			if (wt_swft.length() == 0) {
				showToast(R.string.bswft);
				((EditText) findViewById(R.id.wlt_brnloc)).requestFocus();
				return;
			}

			String account_type = acc_type.getSelectedItem().toString();

			// String et_city = ((Spinner)
			// findViewById(R.id.et_city)).toString();
			if (account_type.length() == 0) {
				showToast(R.string.bpetyp);
				((Spinner) findViewById(R.id.wlt_actype)).requestFocus();
				return;
			}

			String url = AppSettings.getInstance(this).getPropertyValue("wallet_update_bank");
			JSONObject object = new JSONObject();
			object.put("email", AppPreferences.getInstance(this).getFromStore("email"));
			object.put("bankname", wt_fname);
			object.put("accno", wt_acc);
			object.put("typeofaccount", account_type);
			object.put("branchname", wt_bankname);
			object.put("ifsc", wt_ifse);
			object.put("swift", wt_swft);
			object.put("location", wt_branchloc);
			url = url.replace("bankid", bidd);

			HTTPostJson post = new HTTPostJson(this, this, object.toString(), 3);
			post.setContentType("application/json");
			post.execute(url, "");
			Utils.showProgress(getString(R.string.pwait), this);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onFinish(Object results, int requestType) {
		Utils.dismissProgress();

		try {

			switch (requestType) {
			case 1:
				parseAddBankResponse((String) results, requestType);
				break;
			case 2:

				if (results != null) {
					JSONObject object = new JSONObject(results.toString());
					if (object.has("status") && object.optString("status").equalsIgnoreCase("0")) {
						JSONArray array = object.getJSONArray("bank_details");
						if (array != null && array.length() > 0) {
							list = new ArrayList<String>();
							for (int i = 0; i < array.length(); i++) {
								JSONObject jObject = array.getJSONObject(i);

								fullname = jObject.optString("bankname");
								accnum = jObject.optString("accno");
								typeacc = jObject.optString("type");
								bname = jObject.optString("branch");
								ifsc = jObject.optString("ifsccode");
								swift = jObject.optString("swiftcode");
								location = jObject.optString("address");

								wt_fullname.setText(fullname);
								wt_acc.setText(accnum);
								wt_branchloc.setText(location);
								wt_branchname.setText(bname);
								wt_ifsc.setText(ifsc);
								wt_swift.setText(swift);
								wt_accagain.setText(accnum);

								list.add(typeacc);

								list.add("SAVINGS BANK");
								list.add("CURRENT ACCOUNT");
								list.add("CASH CREDIT");
								list.add("LOAN ACCOUNT");
								list.add("NRE ACCOUNT");
								list.add("CREDIT CARD");
								list.remove(position);

								Collections.sort(list);
								position = list.indexOf(typeacc);

							}
							ArrayAdapter<String> adp = new ArrayAdapter<String>(this,
									android.R.layout.simple_spinner_item, list);
							adp.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
							acc_type.setAdapter(adp);
							acc_type.setSelection(position, false);

						}
						//wt_update.setVisibility(View.VISIBLE);
						((TextView) findViewById(R.id.wlt_update)).setVisibility(View.GONE);
					}

				}
				break;
			case 3:
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

	private void parseAddBankResponse(String response, int requestId) throws Exception {

		if (response != null && response.length() > 0) {
			response = response.trim();
			JSONObject jsonObject = new JSONObject(response);

			if (jsonObject.has("status") && jsonObject.optString("status").equalsIgnoreCase("0")) {
				showToast(jsonObject.optString("statusdescription"));

				UpdateBankDetails.this.finish();
				return;
			}
			showToast(jsonObject.optString("statusdescription"));
//			wt_submit.setVisibility(View.VISIBLE);

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
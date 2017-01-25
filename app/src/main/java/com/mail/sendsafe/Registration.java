package com.mail.sendsafe;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mail.sendsafe.callbacks.IItemHandler;
import com.mail.sendsafe.common.AppSettings;
import com.mail.sendsafe.models.City;
import com.mail.sendsafe.models.Country;
import com.mail.sendsafe.models.State;
import com.mail.sendsafe.tasks.HTTPTask;
import com.mail.sendsafe.tasks.HTTPostJson;
import com.mail.sendsafe.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Registration extends FragmentActivity implements IItemHandler {

	private EditText et_username = null;
	private EditText et_ceid = null;
	private EditText et_ref = null;

	private EditText et_password = null;
	private EditText et_passwordAgain = null;

	protected State st;
	private Spinner country, state, city;

	private CheckBox terms;

	JSONObject jsonobject;
	JSONArray jsonarray;
	ArrayList<String> countrylist, statelist;
	ArrayList<Country> counties;
	ArrayList<State> states;
	ArrayList<City> cities;
	ArrayList<String> countryList = new ArrayList<String>();
	ArrayList<String> stateList = new ArrayList<String>();
	ArrayList<String> cityList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration);

		et_username = (EditText) findViewById(R.id.et_username);

		et_ceid = (EditText) findViewById(R.id.et_ceid);

		et_password = (EditText) findViewById(R.id.et_password);

		et_passwordAgain = (EditText) findViewById(R.id.et_passwordAgain);

		et_ref = (EditText) findViewById(R.id.et_referal);

		country = (Spinner) findViewById(R.id.et_counry);

		state = (Spinner) findViewById(R.id.et_state);
		city = (Spinner) findViewById(R.id.et_city);
		terms = (CheckBox) findViewById(R.id.trm_conditions);
		terms.setText(Html.fromHtml("I have read and agree to the "
				+ "<a href='https://sendsafe.com.au/Terms_Conditions_Sendsafe.pdf'>TERMS AND CONDITIONS</a>"));
		terms.setClickable(true);
		terms.setTextSize(12f);
		terms.setMovementMethod(LinkMovementMethod.getInstance());

		findViewById(R.id.tv_RegisterSubmit).setOnClickListener(onClick);

		et_username.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					String email = et_username.getText().toString();
					email = email.trim();

					if (email.length() == 0) {
						showToast(R.string.peei);
						et_username.setSelection(email.length());
						return;
					}

					if (!emailValidation(email)) {
						showToast(R.string.peavei);
						et_username.setSelection(email.length());
						return;
					}

				}
			}
		});

		et_ceid.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					String cemail = et_ceid.getText().toString();
					cemail = cemail.trim();

					if (cemail.length() == 0) {
						showToast(R.string.peei);
						et_ceid.setSelection(cemail.length());
						return;
					}

					if (!emailValidation(cemail)) {
						showToast(R.string.peavei);
						et_ceid.setSelection(cemail.length());
						return;
					}

					String email = et_username.getText().toString();
					email = email.trim();
					if (!email.equals(cemail)) {
						showToast(R.string.eiaceidrnc);
						return;
					}

				}
			}
		});

		country.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					String et_country = country.getSelectedItem().toString();
					et_country = et_country.trim();

					if (et_country.length() == 0) {
						showToast(R.string.pecn1);
						country.setSelection(et_country.length());
						return;
					}

				}
			}
		});

		state.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					String et_state = state.getSelectedItem().toString();
					et_state = et_state.trim();

					if (et_state.length() == 0) {
						showToast(R.string.pecn2);
						state.setSelection(et_state.length());
						return;
					}

				}
			}
		});
		city.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					String et_city = city.getSelectedItem().toString();
					et_city = et_city.trim();

					if (et_city.length() == 0) {
						showToast(R.string.pecn);
						city.setSelection(et_city.length());
						return;
					}

				}
			}
		});
		et_ref.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					String refemail = et_ref.getText().toString().trim();
					refemail = refemail.trim();

					if (refemail.length() == 0) {
						showToast(R.string.peei);
						et_ref.setSelection(refemail.length());
						return;
					}

					if (!emailValidation(refemail)) {
						showToast(R.string.peavei);
						et_ref.setSelection(refemail.length());
						return;
					}

				}
			}
		});
		et_ceid.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before, int count) {

				int textlength1 = et_username.getText().length();
				int textlength2 = et_ceid.getText().length();

				String username = et_username.getText().toString();
				username = username.trim();

				String ceid = et_ceid.getText().toString();
				ceid = ceid.trim();

				if (textlength1 == textlength2) {
					if (!username.equals(ceid)) {
						showToast(R.string.eiaceidrnc);
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

		et_password.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					String password = et_password.getText().toString();
					password = password.trim();

					if (password.length() == 0 || password.length() < 8) {
						showToast(R.string.psmbc);
						et_password.setSelection(password.length());
						return;
					}
				}
			}
		});

		et_passwordAgain.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {

					String cpassword = et_passwordAgain.getText().toString();
					cpassword = cpassword.trim();

					if (cpassword.length() < 8 || cpassword.length() > 16) {
						showToast(R.string.cpmm);
						et_passwordAgain.setSelection(cpassword.length());
						return;
					}

					String password = et_password.getText().toString();
					password = password.trim();

					if (!password.equals(cpassword)) {
						showToast(R.string.cpmm);
						return;
					}

				}
			}
		});

		et_passwordAgain.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before, int count) {

				int textlength1 = et_password.getText().length();
				int textlength2 = et_passwordAgain.getText().length();

				String password = et_password.getText().toString();
				password = password.trim();

				String cpassword = et_passwordAgain.getText().toString();
				cpassword = cpassword.trim();

				if (textlength1 == textlength2) {
					if (!password.equals(cpassword)) {
						showToast(R.string.cpmm);
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

		HTTPTask task = new HTTPTask(Registration.this, Registration.this);
		task.disableProgress();
		task.userRequest("", 2, AppSettings.getInstance(this).getPropertyValue("countrylist"));

		/*
		 * CountryAsyc CountryAsyc = new CountryAsyc(); CountryAsyc.execute();
		 */

	}

	OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View view) {

			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(view.getWindowToken(), 0);

			switch (view.getId()) {

			case R.id.tv_RegisterSubmit:
				registration();
				break;

			default:
				break;
			}

		}
	};

	private void registration() {

		try {

			String et_fname = ((EditText) findViewById(R.id.et_fname)).getText().toString().trim();

			if (et_fname.length() == 0) {
				showToast(R.string.peyfn);
				((EditText) findViewById(R.id.et_fname)).requestFocus();
				return;
			}
			if (!isValidName(et_fname)){
				showToast(R.string.pechars);
				((EditText) findViewById(R.id.et_fname)).requestFocus();
				return;
			}
			String et_lname = ((EditText) findViewById(R.id.et_lname)).getText().toString().trim();

			if (et_lname.length() == 0) {
				showToast(R.string.peyln);
				((EditText) findViewById(R.id.et_lname)).requestFocus();
				return;
			}
			if (!isValidName(et_lname)){
				showToast(R.string.pechars);
				((EditText) findViewById(R.id.et_lname)).requestFocus();
				return;
			}
			String email = et_username.getText().toString().trim();

			if (email.length() == 0) {
				showToast(R.string.peei);
				((EditText) findViewById(R.id.et_username)).requestFocus();
				return;
			}

			if (!emailValidation(email)) {
				showToast(R.string.peavei);
				((EditText) findViewById(R.id.et_username)).requestFocus();
				return;
			}
			String refemail = et_ref.getText().toString().trim();
			email = email.trim();

			String refmail = et_ref.getText().toString();
			refmail = refmail.trim();

			if (refmail.length() != 0) {
				if (!emailValidation1(refmail)) {
					showToast(R.string.peavei);
					((EditText) findViewById(R.id.et_username)).requestFocus();
					return;
				}
				if (email.equals(refmail)) {
					showToast(R.string.peei);
					return;
				}

			}

			String et_ceid_txt = et_ceid.getText().toString().trim();

			if (et_ceid_txt.length() == 0) {
				showToast(R.string.peei);
				((EditText) findViewById(R.id.et_ceid)).requestFocus();
				return;
			}

			if (!emailValidation(et_ceid_txt)) {
				showToast(R.string.peavei);
				// ((EditText) findViewById(R.id.et_ceid)).requestFocus();
				return;
			}

			if (!email.equals(et_ceid_txt)) {
				showToast(R.string.eiaceidrnc);
				((EditText) findViewById(R.id.et_ceid)).requestFocus();
				return;
			}

			String password = ((EditText) findViewById(R.id.et_password)).getText().toString().trim();

			if (password.length() == 0) {
				showToast(R.string.pePswd);
				((EditText) findViewById(R.id.et_password)).requestFocus();
				return;
			}

			if (password.length() < 8 || password.length() > 16) {
				showToast(R.string.psmbc);
				((EditText) findViewById(R.id.et_password)).requestFocus();
				return;
			}

			String et_passwordAgain = ((EditText) findViewById(R.id.et_passwordAgain)).getText().toString().trim();
			if (et_passwordAgain.length() == 0) {
				showToast(R.string.pecPswd);
				((EditText) findViewById(R.id.et_passwordAgain)).requestFocus();
				return;
			}
			if (et_passwordAgain.length() < 8 || et_passwordAgain.length() > 16) {
				showToast(R.string.cpmm);
				((EditText) findViewById(R.id.et_password)).requestFocus();
				return;
			}
			if (!password.equals(et_passwordAgain)) {
				showToast(R.string.cpmm);
				((EditText) findViewById(R.id.et_passwordAgain)).requestFocus();
				return;
			}

			Country selectedcountry = (Country) country.getSelectedItem();
			String value1 = selectedcountry.getCountryid();
			if (value1.length() == 0) {
				showToast(R.string.pecn1);
				((Spinner) findViewById(R.id.et_counry)).requestFocus();
				return;
			}
			//
			// String et_country = country.getSelectedItem().toString();
			// if (et_country.equalsIgnoreCase("Select Country")) {
			// showToast(R.string.pecn1);
			// ((Spinner) findViewById(R.id.et_counry)).requestFocus();
			// return;
			// }
			State selectedState = (State) state.getSelectedItem();
			String value2 = selectedState.getStateid();
			if (value2.length() == 0) {
				showToast(R.string.pecn2);
				((Spinner) findViewById(R.id.et_state)).requestFocus();
				return;
			}
			// String et_state = state.getSelectedItem().toString();
			// if (et_state.equalsIgnoreCase("Select State")) {
			// showToast(R.string.pecn2);
			// ((Spinner) findViewById(R.id.et_state)).requestFocus();
			// return;
			// }
			// String et_city = city.getSelectedItem().toString();
			// if (et_city.equalsIgnoreCase("Select City")) {
			// showToast(R.string.pecn);
			// ((Spinner) findViewById(R.id.et_city)).requestFocus();
			// return;
			// }
			City selectedcity = (City) city.getSelectedItem();
			String value3 = selectedcity.getCityid();
			if (value3.length() == 0) {
				showToast(R.string.pecn);
				((Spinner) findViewById(R.id.et_city)).requestFocus();
				return;
			}
			if (terms.isChecked() == false) {
				showToast(R.string.pecn22);
				((CheckBox) findViewById(R.id.trm_conditions)).requestFocus();
				return;
			}

			String strPassword = URLEncoder.encode(password);

			String url = AppSettings.getInstance(this).getPropertyValue("registration");
			JSONObject object = new JSONObject();
			object.put("firstname", et_fname);
			object.put("lastname", et_lname);
			object.put("email", email);
			object.put("password", strPassword);
			object.put("country", value1);
			object.put("state", value2);
			object.put("city", value3);
			object.put("refemail", refemail);
			HTTPostJson post = new HTTPostJson(this, this, object.toString(), 1);
			post.setContentType("application/json");
			post.execute(url, "");
			Utils.showProgress(getString(R.string.pwait), this);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	private boolean isValidName(String name) {
		String NAME_PATTERN ="[a-zA-z]*(['\\s]+[a-z]*)*";

		Pattern pattern = Pattern.compile(NAME_PATTERN);
		Matcher matcher = pattern.matcher(name);
		return matcher.matches();
	}
	private boolean emailValidation1(String refmail) {
		if (refmail.indexOf("@") == -1 || refmail.indexOf(" ") != -1) {
			return false;
		}
		int emailLenght = refmail.length();
		int atPosition = refmail.indexOf("@");

		String beforeAt = refmail.substring(0, atPosition);
		String afterAt = refmail.substring(atPosition + 1, emailLenght);

		if (beforeAt.length() == 0 || afterAt.length() == 0) {
			return false;
		}
		if (refmail.charAt(atPosition - 1) == '.') {
			return false;
		}
		if (refmail.charAt(atPosition + 1) == '.') {
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

	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onFinish(Object results, int requestType) {
		Utils.dismissProgress();

		try {

			switch (requestType) {
			case 1:
				parseRegistrationResponse((String) results, requestType);
				break;

			case 2:

				if (results == null)
					return;

				jsonobject = new JSONObject(results.toString());

				counties = new ArrayList<Country>();

				try {

					jsonarray = jsonobject.getJSONArray("countries");
					for (int i = 0; i < jsonarray.length(); i++) {
						jsonobject = jsonarray.getJSONObject(i);

						Country country = new Country();

						country.setCountryid(jsonobject.optString("location_id"));
						country.setCountryname(jsonobject.optString("name"));
						country.setLoctype(jsonobject.optString("location_type"));
						country.setIs_visible(jsonobject.optString("is_visible"));
						country.setP_id(jsonobject.optString("parent_id"));

						counties.add(country);
					}

					country.setAdapter(new ArrayAdapter<Country>(Registration.this,
							R.layout.spinner_row_nothing_selected, counties));
					country.setPrompt("Select Country");
					country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

							Country selectedCountry = null;

							selectedCountry = (Country) country.getSelectedItem();
							try {
								String link = AppSettings.getInstance(Registration.this).getPropertyValue("statelist");
								JSONObject object = new JSONObject();
								object.put("location_id", selectedCountry.getCountryid());
								HTTPostJson post = new HTTPostJson(Registration.this, Registration.this,
										object.toString(), 3);
								post.setContentType("application/json");
								post.execute(link, "");
							} catch (Exception e) {
								e.printStackTrace();
							}
							// HTTPTask task = new HTTPTask(Registration.this,
							// Registration.this);
							// task.disableProgress();
							// task.userRequest("", 3,
							// AppSettings.getInstance(Registration.this).getPropertyValue("statelist")
							// + selectedCountry.getCountryid());

							/*
							 * StateAsyc StateAsyc = new StateAsyc();
							 * StateAsyc.execute();
							 */

						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
				}

				break;

			case 3:

				if (results == null)
					return;

				states = new ArrayList<State>();

				try {

					JSONObject jsonobject = new JSONObject(results.toString());

					jsonarray = jsonobject.getJSONArray("states");
					for (int i = 0; i < jsonarray.length(); i++) {
						jsonobject = jsonarray.getJSONObject(i);

						State state = new State();

						state.setStateid(jsonobject.optString("location_id"));
						state.setStatename(jsonobject.optString("name"));
						state.setLoctype(jsonobject.optString("location_type"));
						state.setP_id(jsonobject.optString("parent_id"));
						state.setIs_visible(jsonobject.optString("is_visible"));

						states.add(state);

					}

					state.setAdapter(
							new ArrayAdapter<State>(Registration.this, R.layout.spinner_row_nothing_selected, states));
					state.setPrompt("Select State");

					state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

							State selectedState = null;

							selectedState = (State) state.getSelectedItem();
							try {
								String link = AppSettings.getInstance(Registration.this).getPropertyValue("citylist");
								JSONObject object = new JSONObject();
								object.put("location_id", selectedState.getStateid());
								HTTPostJson post = new HTTPostJson(Registration.this, Registration.this,
										object.toString(), 4);
								post.setContentType("application/json");
								post.execute(link, "");
							} catch (Exception e) {
								e.printStackTrace();
							}
							// HTTPTask task = new HTTPTask(Registration.this,
							// Registration.this);
							// task.disableProgress();
							// task.userRequest("", 4,
							// AppSettings.getInstance(Registration.this).getPropertyValue("citylist")
							// + selectedState.getStateid());

							/*
							 * CityAsyc CityAsyc = new CityAsyc();
							 * CityAsyc.execute();
							 */
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
				}

				break;

			case 4:

				if (results == null)
					return;

				cities = new ArrayList<City>();

				try {

					JSONObject jsonobject = new JSONObject(results.toString());

					jsonarray = jsonobject.getJSONArray("cities");
					for (int i = 0; i < jsonarray.length(); i++) {
						jsonobject = jsonarray.getJSONObject(i);

						City cityy = new City();

						cityy.setCityid(jsonobject.optString("location_id"));
						cityy.setCityname(jsonobject.optString("name"));
						cityy.setLoctype(jsonobject.optString("location_type"));
						cityy.setP_id(jsonobject.optString("parent_id"));
						cityy.setIs_visible(jsonobject.optString("is_visible"));

						cities.add(cityy);

					}

					city.setAdapter(
							new ArrayAdapter<City>(Registration.this, R.layout.spinner_row_nothing_selected, cities));
					city.setPrompt("Select City");
					city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub
						}
					});

				} catch (Exception e) {
					// Log.e("Error", e.getMessage());
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

	private void parseRegistrationResponse(String response, int requestId) throws Exception {

		if (response != null && response.length() > 0) {
			response = response.trim();
			JSONObject jsonObject = new JSONObject(response);

			if (jsonObject.has("status") && jsonObject.optString("status").equalsIgnoreCase("0")) {
				showToast(jsonObject.optString("statusdescription"));
				Registration.this.finish();
				return;
			}
			showToast(jsonObject.optString("statusdescription"));

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

	/*
	 * class CountryAsyc extends AsyncTask<Void, Void, Void> {
	 * 
	 * @Override protected Void doInBackground(Void... params) { // Locate the
	 * Countries Class counties = new ArrayList<Country>();
	 * 
	 * jsonobject = com.mail.bsecure.models.JSONfunctions
	 * .getJSONfromURL("https://bsecure.email/androidapp/states/countrylist");
	 * 
	 * try { // Locate the NodeList name jsonarray =
	 * jsonobject.getJSONArray("countries"); for (int i = 0; i <
	 * jsonarray.length(); i++) { jsonobject = jsonarray.getJSONObject(i);
	 * 
	 * Country country = new Country();
	 * 
	 * // country.setName("select country");
	 * country.setCountryid(jsonobject.optString("location_id"));
	 * country.setCountryname(jsonobject.optString("name"));
	 * country.setLoctype(jsonobject.optString("location_type"));
	 * country.setIs_visible(jsonobject.optString("is_visible"));
	 * country.setP_id(jsonobject.optString("parent_id"));
	 * 
	 * counties.add(country); // countrylist.add(country.getCountryname()); } }
	 * catch (Exception e) { Log.e("Error", e.getMessage());
	 * e.printStackTrace(); } return null; }
	 * 
	 * @Override protected void onPostExecute(Void args) {
	 * 
	 * country = (Spinner) findViewById(R.id.et_counry);
	 * 
	 * // Spinner adapter // String country_values[] = new String[]{
	 * "Please select country."}; country.setAdapter(new
	 * ArrayAdapter<Country>(Registration.this,
	 * android.R.layout.simple_spinner_dropdown_item, counties));
	 * 
	 * // Spinner on item click listener country.setOnItemSelectedListener(new
	 * AdapterView.OnItemSelectedListener() {
	 * 
	 * @Override public void onItemSelected(AdapterView<?> arg0, View arg1, int
	 * position, long arg3) {
	 * 
	 * // StateAsyc StateAsyc = new StateAsyc(); StateAsyc.execute();
	 * 
	 * }
	 * 
	 * @Override public void onNothingSelected(AdapterView<?> arg0) { // TODO
	 * Auto-generated method stub } }); //
	 * country.setSelection(countrylist.indexOf("Select Country")); } }
	 */

	/*
	 * class StateAsyc extends AsyncTask<Void, Void, Void> {
	 * 
	 * private Country selectedCountry;
	 * 
	 * @Override protected void onPreExecute() { selectedCountry = (Country)
	 * country.getSelectedItem();
	 * 
	 * super.onPreExecute(); }
	 * 
	 * @Override protected Void doInBackground(Void... myparams) {
	 * 
	 * states = new ArrayList<State>();
	 * 
	 * jsonobject = com.mail.bsecure.models.JSONfunctions.getJSONfromURL(
	 * "https://bsecure.email/androidapp/states/statelist?location_id=" +
	 * selectedCountry.getCountryid());
	 * 
	 * try { // Locate the NodeList name jsonarray =
	 * jsonobject.getJSONArray("states"); for (int i = 0; i <
	 * jsonarray.length(); i++) { jsonobject = jsonarray.getJSONObject(i);
	 * 
	 * State state = new State();
	 * 
	 * state.setStateid(jsonobject.optString("location_id"));
	 * state.setStatename(jsonobject.optString("name"));
	 * state.setLoctype(jsonobject.optString("location_type"));
	 * state.setP_id(jsonobject.optString("parent_id"));
	 * state.setIs_visible(jsonobject.optString("is_visible"));
	 * 
	 * states.add(state);
	 * 
	 * // Populate spinner with states names //
	 * statelist.add(jsonobject.optString("name"));
	 * 
	 * } } catch (Exception e) { Log.e("Error", e.getMessage());
	 * e.printStackTrace(); } return null; }
	 * 
	 * @Override protected void onPostExecute(Void args) {
	 * 
	 * state = (Spinner) findViewById(R.id.et_state);
	 * 
	 * // Spinner adapter state.setAdapter( new
	 * ArrayAdapter<State>(Registration.this,
	 * android.R.layout.simple_spinner_dropdown_item, states));
	 * 
	 * // Spinner on item click listener state.setOnItemSelectedListener(new
	 * AdapterView.OnItemSelectedListener() {
	 * 
	 * @Override public void onItemSelected(AdapterView<?> arg0, View arg1, int
	 * position, long arg3) { CityAsyc CityAsyc = new CityAsyc();
	 * CityAsyc.execute(); }
	 * 
	 * @Override public void onNothingSelected(AdapterView<?> arg0) { // TODO
	 * Auto-generated method stub } }); } }
	 */

	/*
	 * class CityAsyc extends AsyncTask<Void, Void, Void> {
	 * 
	 * private State selectedState;
	 * 
	 * @Override protected void onPreExecute() { selectedState = (State)
	 * state.getSelectedItem();
	 * 
	 * super.onPreExecute(); }
	 * 
	 * @Override protected Void doInBackground(Void... myparams) {
	 * 
	 * cities = new ArrayList<City>();
	 * 
	 * jsonobject = com.mail.bsecure.models.JSONfunctions.getJSONfromURL(
	 * "https://bsecure.email/androidapp/states/citylist?location_id=" +
	 * selectedState.getStateid());
	 * 
	 * try { // Locate the NodeList name jsonarray =
	 * jsonobject.getJSONArray("cities"); for (int i = 0; i <
	 * jsonarray.length(); i++) { jsonobject = jsonarray.getJSONObject(i);
	 * 
	 * City cityy = new City();
	 * 
	 * cityy.setCityid(jsonobject.optString("location_id"));
	 * cityy.setCityname(jsonobject.optString("name"));
	 * cityy.setLoctype(jsonobject.optString("location_type"));
	 * cityy.setP_id(jsonobject.optString("parent_id"));
	 * cityy.setIs_visible(jsonobject.optString("is_visible"));
	 * 
	 * cities.add(cityy);
	 * 
	 * } } catch (Exception e) { Log.e("Error", e.getMessage());
	 * e.printStackTrace(); } return null; }
	 * 
	 * @Override protected void onPostExecute(Void args) {
	 * 
	 * city = (Spinner) findViewById(R.id.et_city);
	 * 
	 * city.setAdapter( new ArrayAdapter<City>(Registration.this,
	 * android.R.layout.simple_spinner_dropdown_item, cities));
	 * 
	 * // Spinner on item click listener city.setOnItemSelectedListener(new
	 * AdapterView.OnItemSelectedListener() {
	 * 
	 * @Override public void onItemSelected(AdapterView<?> arg0, View arg1, int
	 * position, long arg3) {
	 * 
	 * }
	 * 
	 * @Override public void onNothingSelected(AdapterView<?> arg0) { // TODO
	 * Auto-generated method stub } }); } }
	 */

}

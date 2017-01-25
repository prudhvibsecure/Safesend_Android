package com.mail.sendsafe.account;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mail.sendsafe.InvitePeople;
import com.mail.sendsafe.R;
import com.mail.sendsafe.adapter.SubAdapter;
import com.mail.sendsafe.adapter.TransactionsAdapter;
import com.mail.sendsafe.adapter.WalletsAdapter;
import com.mail.sendsafe.callbacks.IItemHandler;
import com.mail.sendsafe.common.AppSettings;
import com.mail.sendsafe.imageloader.ImageLoader;
import com.mail.sendsafe.models.WalletsAmout;
import com.mail.sendsafe.tasks.HTTPostJson;
import com.mail.sendsafe.utils.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class View_Wallet extends AppCompatActivity implements IItemHandler {

	private TextView t_amt, t_balanceamt, wlt_trnsefer, transactions, refral, sublvl_text;
	private String username, fname, amt, comission, tno, totalcom;
	private double Amount, tmaincom, t_earnamt = 0, t_balance = 0;
	private ImageLoader imgLoader = null;
	private List<WalletsAmout> walletList;
	private ListView referlslist, translist, sublevllist;
	private WalletsAdapter WalletsAdapter;
	private TransactionsAdapter transactionsAdapter;
	private SubAdapter SubAdapter;
	private ImageView imageview;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_wallet);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.walet);
		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		t_amt = (TextView) findViewById(R.id.t_amt);
		t_balanceamt = (TextView) findViewById(R.id.tv_bal);
		wlt_trnsefer = (TextView) findViewById(R.id.wlt_trnsefer);
		imageview = (ImageView) findViewById(R.id.iv_wallet_pic);
		transactions = (TextView) findViewById(R.id.trans_text);

		refral = (TextView) findViewById(R.id.ref_mails);

		sublvl_text = (TextView) findViewById(R.id.sublvl_text);

		findViewById(R.id.wlt_trnsefer).setOnClickListener(onClick);
		findViewById(R.id.trans_text).setOnClickListener(onClick);
		findViewById(R.id.ref_mails).setOnClickListener(onClick);
		findViewById(R.id.sublvl_text).setOnClickListener(onClick);
		findViewById(R.id.wlt_trnsefer).setOnClickListener(onClick);

		viewWallet();

		try {
			String url = AppSettings.getInstance(this).getPropertyValue("profile_ret");
			JSONObject object = new JSONObject();
			object.put("email", getFromStore("email"));

			HTTPostJson post = new HTTPostJson(this, this, object.toString(), 3);
			post.setContentType("application/json");
			post.execute(url, "");
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	private void viewWallet() {

		try {

			String url = AppSettings.getInstance(this).getPropertyValue("view_wallet");
			JSONObject object = new JSONObject();
			object.put("email", getFromStore("email"));

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

			View_Wallet.this.finish();

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

			case R.id.wlt_trnsefer:
				transferAmount();
				break;
			case R.id.trans_text:
				transactionsList();

				break;
			case R.id.ref_mails:

				viewWallet();
				break;
			case R.id.sublvl_text:
				subLevels();

				break;
			default:
				break;
			}

		}
	};

	protected void subLevels() {

		try {

			String url = AppSettings.getInstance(this).getPropertyValue("view_wallet");
			JSONObject object = new JSONObject();
			object.put("email", getFromStore("email"));

			HTTPostJson post = new HTTPostJson(this, this, object.toString(), 5);
			post.setContentType("application/json");
			post.execute(url, "");
			Utils.showProgress(getString(R.string.pwait), this);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	protected void transactionsList() {
		try {

			String url = AppSettings.getInstance(this).getPropertyValue("view_wallet");
			JSONObject object = new JSONObject();
			object.put("email", getFromStore("email"));

			HTTPostJson post = new HTTPostJson(this, this, object.toString(), 4);
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
		walletList=new ArrayList<WalletsAmout>();
		try {

			switch (requestType) {
			case 1:
				if (results != null) {
					JSONObject object = new JSONObject(results.toString());

					if (object.has("status") && object.optString("status").equalsIgnoreCase("0")) {
						JSONArray array = object.getJSONArray("wallet_details");
						for (int i = 0; i < array.length(); i++) {
							WalletsAmout map = new WalletsAmout();
							JSONObject jObject = array.getJSONObject(i);
							username = jObject.optString("useremail");
							amt = jObject.optString("amount");
							fname = jObject.optString("firstname");
							fname = fname + " " + jObject.optString("lastname");
							comission = amt;
							Amount = Double.parseDouble(comission);
							tmaincom = (Amount / 100) * 10;
							DecimalFormat DueFormatter = new DecimalFormat("#,##,###");
							totalcom = DueFormatter.format(tmaincom);
							int ino = (i + 1);
							tno = Integer.toString(ino);

							t_earnamt = t_earnamt + Double.parseDouble(amt);

							map.setFirstname(fname);
							map.setUseremail(username);
							walletList.add(map);

						}
						String Tamount = object.getString("total");
						t_amt.setText(Html.fromHtml("Total Amount Earned :" + " " + Tamount));
						t_balanceamt.setText(Html.fromHtml("Balance Amount in Wallet :" + " " + Tamount));

						referlslist = (ListView) findViewById(R.id.list_refers);
						WalletsAdapter = new WalletsAdapter(getApplicationContext(), walletList);
						referlslist.setAdapter(WalletsAdapter);
					} else if (object.has("status") && object.optString("status").equalsIgnoreCase("1")) {
						refral.setVisibility(View.GONE);
						transactions.setVisibility(View.GONE);
						sublvl_text.setVisibility(View.GONE);

						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
						alertDialogBuilder.setTitle("Invite People");
						alertDialogBuilder.setMessage("Please Refer you friend");
						alertDialogBuilder.setCancelable(false);
						alertDialogBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent nextref = new Intent(getApplicationContext(), InvitePeople.class);
								startActivity(nextref);
								finish();
							}
						});

						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();

					}
				}

				break;
			case 2:
				if (results != null) {
					JSONObject object = new JSONObject(results.toString());
					if (object.has("status") && object.optString("status").equalsIgnoreCase("0")) {

						launchActivity(TransferAmount.class, 1000);
						this.finish();
						showToast(object.optString("statusdescription"));

					}
					else if (object.has("status") && object.optString("status").equalsIgnoreCase("1")) {
						launchActivity(UpdateBankDetails.class, 1001);
						this.finish();
						showToast(object.optString("statusdescription"));
					}
				}
				break;
			case 3:
				parseProfileResponse(results, requestType);
				break;
			case 4:
				if (results != null) {
					JSONObject object = new JSONObject(results.toString());
					String payment_details = object.getString("payment_details");
					if (payment_details.length() == 0) {
						showToast("No data Found");
						transactions.setVisibility(View.GONE);
					}
					if (object.has("status") && object.optString("status").equalsIgnoreCase("0")) {
						JSONArray array = object.getJSONArray("payment_details");
						if (array.length() == 0) {
							showToast("No data");
							return;

						}
						for (int i = 0; i < array.length(); i++) {
							WalletsAmout map = new WalletsAmout();
							JSONObject jsonobject = array.getJSONObject(i);
							String paydate = jsonobject.getString("paydate");
							String expirydate = jsonobject.getString("expirydate");
							String amount = jsonobject.getString("amount");
							map.setPaydate(paydate);
							map.setExpirydate(expirydate);
							map.setAmount(amount);
							walletList.add(map);
						}
						translist = (ListView) findViewById(R.id.list_refers);
						transactionsAdapter = new TransactionsAdapter(getApplicationContext(), walletList);
						translist.setAdapter(transactionsAdapter);
					}

				}
				break;
			case 5:
				if (results != null) {
					JSONObject object = new JSONObject(results.toString());
					String subwallet_details = object.getString("subwallet_details");
					if (subwallet_details.length() == 0) {
						showToast("No data Found");
						sublvl_text.setVisibility(View.GONE);
					}
					if (object.has("status") && object.optString("status").equalsIgnoreCase("0")) {
						JSONArray array = object.getJSONArray("subwallet_details");
						if (array.length() == 0) {
							showToast("No data");
							return;
						}
						for (int i = 0; i < array.length(); i++) {
							WalletsAmout map = new WalletsAmout();
							JSONObject jsonobject = array.getJSONObject(i);
							String pmail = jsonobject.getString("parentemail");
							String uemail = jsonobject.getString("useremail");
							String fname = jsonobject.getString("firstname");
							map.setParentmail(pmail);
							map.setUseremail(uemail);
							map.setFirstname(fname);
							walletList.add(map);
						}
						sublevllist = (ListView) findViewById(R.id.list_refers);
						SubAdapter = new SubAdapter(getApplicationContext(), walletList);
						sublevllist.setAdapter(SubAdapter);
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

	private void parseProfileResponse(Object response, int requestId) throws Exception {

		if (response == null)
			return;

		JSONObject result = new JSONObject(response.toString());

		if (result != null) {
			try {

				String imgURL = result.getString("avatar");

				if (imgLoader == null)
					imgLoader = new ImageLoader(View_Wallet.this, true);

				imgLoader.DisplayImage(imgURL, imageview, 5);

			} catch (Exception e) {

			}
		}

	}

	protected void transferAmount() {

		try {

			String url = AppSettings.getInstance(this).getPropertyValue("wallet_transfer");
			JSONObject object = new JSONObject();
			object.put("email", getFromStore("email"));

			HTTPostJson post = new HTTPostJson(this, this, object.toString(), 2);
			post.setContentType("application/json");
			post.execute(url, "");
			Utils.showProgress(getString(R.string.pwait), this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getFromStore(String key) {
		SharedPreferences pref = this.getSharedPreferences("bsecure", MODE_PRIVATE);
		String res = pref.getString(key, "");
		return res;
	}

	@Override
	public void onError(String errorData, int requestType) {
		Utils.dismissProgress();
		showToast(errorData);
	}

	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

	}

	private void launchActivity(Class<?> cls, int requestCode) {
		Intent mainIntent = new Intent(View_Wallet.this, cls);
		startActivityForResult(mainIntent, requestCode);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}

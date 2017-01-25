package com.mail.sendsafe.account;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mail.sendsafe.R;
import com.mail.sendsafe.adapter.ContactsAdapter;
import com.mail.sendsafe.callbacks.IItemHandler;
import com.mail.sendsafe.common.AppSettings;
import com.mail.sendsafe.models.Contacts;
import com.mail.sendsafe.tasks.HTTPostJson;
import com.mail.sendsafe.utils.Utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class ViewContacts extends AppCompatActivity implements IItemHandler {

	private ContactsAdapter contactadapter;
	private List<Contacts> contactlist;
	private String abid;
	private ListView listView;
	public boolean flag = false;
	private SwipeRefreshLayout mSwipeRefreshLayout = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_contact);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.viewcontact_head);
		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.RefreshLayout);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimaryDark,
				R.color.colorPrimaryDark, R.color.colorPrimaryDark, R.color.colorPrimaryDark);
		mSwipeRefreshLayout.setEnabled(false);
		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				viewContactWebserviceCall();
				Utils.dismissProgress();
			}
		});

		Intent intabid = getIntent();
		String abid = intabid.getStringExtra("abid");
		if (abid != null) {

			try {
				String url = AppSettings.getInstance(this).getPropertyValue("contact_delete");
				JSONObject object = new JSONObject();
				object.put("email", getFromStore("email"));
				object.put("abid", abid);

				HTTPostJson post = new HTTPostJson(this, this, object.toString(), 2);
				post.setContentType("application/json");
				post.execute(url, "");
				//Utils.showProgress(getString(R.string.pwait), this);
			} catch (Exception e) {
				e.printStackTrace();

			}
		}
		viewContactWebserviceCall();

	}

	private void viewContactWebserviceCall() {
		try {
			String url = AppSettings.getInstance(this).getPropertyValue("view_contacts");
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.contacts, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

			case android.R.id.home:

				ViewContacts.this.finish();

				break;
			case R.id.delete:
				if (!contactlist.isEmpty()) {
					if (flag) {
						contactadapter.showDelete(false);
						contactadapter.notifyDataSetChanged();
						supportInvalidateOptionsMenu();
						flag = false;
					} else {
						contactadapter.showDelete(true);
						contactadapter.notifyDataSetChanged();
						supportInvalidateOptionsMenu();
						flag = true;
					}
				} else {
					showToast("No Contacts Imported");
				}

				break;

			default:
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onFinish(Object results, int requestType) {
		Utils.dismissProgress();
		contactlist = new ArrayList<Contacts>();
		try {

			switch (requestType) {
				case 1:
					if (results != null) {
						mSwipeRefreshLayout.setRefreshing(false);
						mSwipeRefreshLayout.setEnabled(true);
						JSONObject object = new JSONObject(results.toString());
						if (object.has("status") && object.optString("status").equalsIgnoreCase("0")) {
							JSONArray array = object.getJSONArray("addressbook_detail");
							for (int i = 0; i < array.length(); i++) {
								Contacts map = new Contacts();
								JSONObject jsonobject = array.getJSONObject(i);
								String fname = jsonobject.getString("fname");
								String lname = jsonobject.getString("lname");
								String emailid = jsonobject.getString("emailid");
								abid = jsonobject.getString("abid");
								if (fname.length() != 0 && emailid.length() != 0) {
									map.setFname(fname);
									map.setEmailid(emailid);
									map.setAbid(abid);
									contactlist.add(map);
								}
							}
							listView = (ListView) findViewById(R.id.contact_list);
							contactadapter = new ContactsAdapter(getApplicationContext(), contactlist);
							listView.setAdapter(contactadapter);

						} else if (results != null) {

							if (object.has("status") && object.optString("status").equalsIgnoreCase("1")) {

								showToast(object.optString("statusdescription"));
							}
						}
					}

					break;
				case 2:
					if (results != null) {
						JSONObject object = new JSONObject(results.toString());
						if (object.has("status") && object.optString("status").equalsIgnoreCase("0")) {
							showToast(object.optString("msg"));
							// ViewContacts.this.finish();
						}
						if (object.has("status") && object.optString("status").equalsIgnoreCase("1")) {

							showToast(object.optString("msg"));
							// ViewContacts.this.finish();
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

	private String getFromStore(String key) {
		SharedPreferences pref = this.getSharedPreferences("bsecure", MODE_PRIVATE);
		String res = pref.getString(key, "");
		return res;
	}

	@Override
	public void onError(String errorData, int requestType) {
		mSwipeRefreshLayout.setRefreshing(false);
		mSwipeRefreshLayout.setEnabled(true);
		// Utils.dismissProgress();
		showToast(errorData);
	}

	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	protected void onDestroy() {
		//contactadapter.Clear();
		super.onDestroy();

	}

}


package com.mail.sendsafe;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mail.sendsafe.adapter.FolderListAdapter;
import com.mail.sendsafe.callbacks.IItemHandler;
import com.mail.sendsafe.common.AppPreferences;
import com.mail.sendsafe.common.AppSettings;
import com.mail.sendsafe.models.Folder;
import com.mail.sendsafe.tasks.HTTPostJson;
import com.mail.sendsafe.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManageFolders extends AppCompatActivity implements IItemHandler {

	private List<Folder> folderlist;
	private FolderListAdapter cadapter = null;
	private EditText folder_name = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fold_listview);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.mnff);
		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		try {
			String urlfl = AppSettings.getInstance(this).getPropertyValue("view_folder");
			JSONObject object = new JSONObject();
			object.put("email", AppPreferences.getInstance(this).getFromStore("email"));
			HTTPostJson post = new HTTPostJson(this, this, object.toString(), 1);
			post.setContentType("application/json");
			post.execute(urlfl, "");
			Utils.showProgress(getString(R.string.pwait), this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onFinish(Object results, int requestType) {

		Utils.dismissProgress();
		folderlist = new ArrayList<Folder>();
		try {

			switch (requestType) {
			case 1:

				if (results != null) {
					JSONObject object = new JSONObject(results.toString());
					if (object.has("status") && object.optString("status").equalsIgnoreCase("0")) {
						JSONArray array = object.getJSONArray("folder_detail");
						for (int i = 0; i < array.length(); i++) {
							Folder map = new Folder();
							JSONObject jsonobject = array.getJSONObject(i);
							String foldername = jsonobject.getString("foldername");
							String fid = jsonobject.getString("fid");
							map.setFolderName(foldername);
							map.setFid(fid);
							folderlist.add(map);
							Collections.sort(folderlist);

						}
						ListView dialog_ListView = (ListView) findViewById(R.id.folder_list);
						cadapter = new FolderListAdapter(this, folderlist);
						dialog_ListView.setAdapter(cadapter);
						dialog_ListView.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> av, View v, int position, long id) {

								String foldername = folderlist.get(position).getFolderName();
								String fid = folderlist.get(position).getFid();
								editFolderPop(foldername, fid);

							}
						});

					} else {
						((TextView) findViewById(R.id.textfolder)).setVisibility(View.VISIBLE);
						// showToast("No Folders Found");
					}
				}
				break;
			case 2:
				Utils.dismissProgress();
				if (results != null) {
					JSONObject object = new JSONObject(results.toString());
					if (object.has("status") && object.optString("status").equalsIgnoreCase("0")) {
						showToast(object.optString("statusdescription"));
						ManageFolders.this.finish();
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

	public void showToast(int text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	protected void editFolderPop(final String foldername, final String fid) {

		final AlertDialog.Builder folderDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, android.R.style.Theme_DeviceDefault_Light_Dialog));
		LayoutInflater factory = LayoutInflater.from(this);
		final View view = factory.inflate(R.layout.edit_folder, null);
		folderDialog.setView(view);

		TextView title = (TextView) view.findViewById(R.id.txt_folder);
		title.setText(R.string.eyrfnam);
		TextView submit = (TextView) view.findViewById(R.id.bn_bu_submit);
		folder_name = (EditText) view.findViewById(R.id.cmttxt_folder);
		folder_name.setText(foldername);

		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				String et_edittext=folder_name.getText().toString().trim();
				if (et_edittext.length() == 0) {
					showToast(R.string.fnef);
					((EditText) view.findViewById(R.id.cmttxt_folder)).requestFocus();
					return;
				}


				if (et_edittext.charAt(0)==' '){
					showToast(R.string.fnef);
					((EditText) view.findViewById(R.id.cmttxt_folder)).requestFocus();
					return;
				}

				submitToFolderName(foldername, fid);
			}

		});

		folderDialog.show();

	}

	protected void submitToFolderName(String fname, String fid) {
		try {
			String urlfl = AppSettings.getInstance(this).getPropertyValue("update_folder");
			JSONObject object = new JSONObject();
			object.put("email", AppPreferences.getInstance(this).getFromStore("email"));
			object.put("fid", fid);
			object.put("foldername", folder_name.getText().toString());
			HTTPostJson post = new HTTPostJson(this, this, object.toString(), 2);
			post.setContentType("application/json");
			post.execute(urlfl, "");
			Utils.showProgress(getString(R.string.pwait), this);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onError(String errorCode, int requestType) {

	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

			case android.R.id.home:

				ManageFolders.this.finish();

				break;

			default:
				break;
		}

		return super.onOptionsItemSelected(item);
	}
}

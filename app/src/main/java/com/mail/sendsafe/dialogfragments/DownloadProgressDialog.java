package com.mail.sendsafe.dialogfragments;

import java.io.File;
import java.io.InputStream;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.mail.sendsafe.Bsecure;
import com.mail.sendsafe.ImageActivity;
import com.mail.sendsafe.R;
import com.mail.sendsafe.callbacks.IRequestCallback;
import com.mail.sendsafe.common.AppPreferences;
import com.mail.sendsafe.common.Constants;
import com.mail.sendsafe.controls.ProgressControl;
import com.mail.sendsafe.tasks.HTTPGetTask;
import com.mail.sendsafe.web.PdfView;
import com.mail.sendsafe.web.VideoPlay;

import static com.mail.sendsafe.tasks.HTTPPostTask.urlEncode;

public class DownloadProgressDialog extends DialogFragment implements IRequestCallback {

	private Bsecure bsecure = null;

	Intent intent = new Intent(Intent.ACTION_SEND);

	private int id = -1;
	private String fileName = "";
	private String library = "";

	private String to = "";

	private ProgressControl download_progress = null;
	private HTTPGetTask task = null;

	private Button tvRetry = null;

	private int showtime = 0;

	public static DownloadProgressDialog newInstance() {
		return new DownloadProgressDialog();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		bsecure = (Bsecure) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Bundle mArgs = getArguments();

		id = mArgs.getInt("id", R.id.tv_attachmentView);

		showtime = mArgs.getInt("showtime", 0);

		fileName = mArgs.getString("fileName");

		to = mArgs.getString("to");

		library = mArgs.getString("library", "");

		View view = inflater.inflate(R.layout.downloadprog_popup, container, false);

		tvRetry = (Button) view.findViewById(R.id.tvRetry);
		tvRetry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stopDownloading();
			}
		});

		download_progress = (ProgressControl) view.findViewById(R.id.download_progress);
		download_progress.setText(fileName);

		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		getDialog().setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == 4)
					return true;
				return false;
			}
		});

		view.findViewById(R.id.bn_dg_cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				stopDownloading();

				task = null;
				DownloadProgressDialog.this.dismiss();

			}
		});

		// view.findViewById(R.id.tvCommentSubmit).setOnClickListener(bsecure);
		// view.findViewById(R.id.tvCommentSubmit).setTag((Item)
		// mArgs.getSerializable("item"));

		return view;
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);

		getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

		startDownloading();
	}

	private void startDownloading() {

		String url = bsecure.getPropertyValue("filedownload") + fileName;
		if (library.equalsIgnoreCase("yes"))
			url = bsecure.getPropertyValue("group_filedownload") + fileName;

		task = new HTTPGetTask(bsecure, this, true, fileName);

		if (id == R.id.tv_attachmentView)
			task.inAppStorage();

		task.execute(url);

	}

	@Override
	public void onRequestComplete(InputStream inputStream, String mimeType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRequestComplete(String data, String mimeType) {
		launchActivity(data, mimeType);
	}

	@Override
	public void onRequestFailed(String errorData) {
		bsecure.showToast(errorData);
		tvRetry.setVisibility(View.VISIBLE);
	}

	@Override
	public void onRequestCancelled(String extraInfo) {
		download_progress.clear();
	}

	@Override
	public void onRequestProgress(Long... values) {
		download_progress.updateProgressState(values);
	}

	private void stopDownloading() {
		tvRetry.setVisibility(View.INVISIBLE);
		if (task != null && task.getStatus() == AsyncTask.Status.RUNNING) {
			task.cancel(true);
			task = null;
		}
	}

	public void launchActivity(String data, String mimeType) {

		this.dismiss();

		try {

			String[] temp = data.split("###");

			if (mimeType != null) {
				File file = new File(Constants.DWLPATH, temp[1]);

				if (id == R.id.tv_attachmentView) {

					File mydir = bsecure.getDir("Downloads", Context.MODE_PRIVATE);

					file = new File(mydir, temp[1]);

					if (mimeType.contains("video") || mimeType.contains("audio")) {

						launchActivity(VideoPlay.class, "file://" + file.toString(), mimeType);
					}
					if (mimeType.contains("image")) {
						launchActivity(ImageActivity.class, "file://" + file.toString(), mimeType);
					}
					if (mimeType.contains("application/pdf")) {
						launchActivity(PdfView.class, "file://" + file.toString(), mimeType);
					}
					return;
				}

				Intent promptInstall = new Intent(Intent.ACTION_VIEW);
				promptInstall.setDataAndType(Uri.fromFile(file), mimeType);
				startActivityForResult(promptInstall, 107);
				return;
			}

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(temp[0]));
			startActivity(intent);

		} catch (ActivityNotFoundException ANF) {
			bsecure.showToast("Phone does not support MIME Type :" + mimeType);
		} catch (Exception e) {
			bsecure.showToast("Error while processing, please try later");
		}
	}

	private void launchActivity(Class<?> cls, String filepath, String mimeType) {

		Intent mainIntent = new Intent(bsecure, cls);
		mainIntent.putExtra("filepath", filepath + "");
		mainIntent.putExtra("mimeType", mimeType + "");
		mainIntent.putExtra("showtime", showtime);
		mainIntent.putExtra("to", to);

		startActivity(mainIntent);
	}

}

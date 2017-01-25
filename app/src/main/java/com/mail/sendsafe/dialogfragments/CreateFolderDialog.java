package com.mail.sendsafe.dialogfragments;

import com.mail.sendsafe.Bsecure;
import com.mail.sendsafe.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class CreateFolderDialog extends DialogFragment {

	private Bsecure bsecure = null;
	private View view = null;

	Intent intent = new Intent(Intent.ACTION_SEND);

	public static CreateFolderDialog newInstance() {
		return new CreateFolderDialog();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		bsecure = (Bsecure) activity;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Bundle mArgs = getArguments();

		view = inflater.inflate(R.layout.folder, container, false);

		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		view.findViewById(R.id.fl_cancel).setOnClickListener(bsecure);
		view.findViewById(R.id.fl_create).setOnClickListener(bsecure);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

	}

}

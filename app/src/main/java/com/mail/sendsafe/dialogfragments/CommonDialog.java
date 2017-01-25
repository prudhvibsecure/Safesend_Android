package com.mail.sendsafe.dialogfragments;

import com.mail.sendsafe.Bsecure;
import com.mail.sendsafe.R;
import com.mail.sendsafe.common.Item;

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
import android.widget.EditText;
import android.widget.TextView;

public class CommonDialog extends DialogFragment {

	private Item item = null;

	TextView title, sendSMS, submit;

	EditText mobileNum;

	private Bsecure bsecure = null;

	int layout;

	public static CommonDialog newInstance() {
		return new CommonDialog();
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle mArgs = getArguments();

		layout = mArgs.getInt("layout");

		item = (Item) mArgs.getSerializable("item");

		View view = inflater.inflate(layout, container, false);

		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		//view.findViewById(R.id.closebtn).setOnClickListener(bsecure);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
	}

}

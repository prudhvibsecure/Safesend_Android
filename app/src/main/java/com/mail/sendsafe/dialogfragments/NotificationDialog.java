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
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class NotificationDialog extends DialogFragment {
	private Bsecure bsecure = null;
	private RadioGroup radioGroup = null;
	Intent intent = new Intent(Intent.ACTION_SEND);

	public static NotificationDialog newInstance() {
		return new NotificationDialog();
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

		View view = inflater.inflate(R.layout.notification, container, false);

		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		view.findViewById(R.id.notify_grp);
		view.findViewById(R.id.notify_sub).setOnClickListener(bsecure);

//		if ("Yes".equalsIgnoreCase(mArgs.getString("notifyValue"))) {
//			((RadioButton) view.findViewById(R.id.notify_yes)).setChecked(true);
//		}
//		if ("Yes1".equalsIgnoreCase(mArgs.getString("notifyValue"))) {
//			((RadioButton) view.findViewById(R.id.notify_yes)).setChecked(true);
//		}

		return view;
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

	}

	public String getSelectedValue() {

		int id = radioGroup.getCheckedRadioButtonId();
		if (id == -1) {
			return "";
		}

		return radioGroup.findViewById(id).getTag().toString();

	}

}

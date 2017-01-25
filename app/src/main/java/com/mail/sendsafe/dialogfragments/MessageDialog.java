package com.mail.sendsafe.dialogfragments;

import com.mail.sendsafe.MPayment;
import com.mail.sendsafe.R;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

public class MessageDialog extends DialogFragment {

	private MPayment bsecure = null;

	int layout;

	public static MessageDialog newInstance(Bundle bundle) {
		
		MessageDialog messageDialog = new MessageDialog();  
		messageDialog.setArguments(bundle);
		return messageDialog;
				
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		bsecure = (MPayment) activity;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Bundle mArgs = getArguments();

		View view = inflater.inflate(R.layout.popup_subscription, container, false);

		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
				
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		
		getDialog().setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(keyCode == 4)
					return true;
				return false;
			}
		});
		
		
		((TextView)view.findViewById(R.id.title_txt)).setText(getString("title", getString(R.string.message), mArgs));
		((TextView)view.findViewById(R.id.ok_msg)).setText(getString("message", "", mArgs));

		TextView tv_upgradenow = (TextView)view.findViewById(R.id.tv_upgradenow);
				
		tv_upgradenow.setText(R.string.ok);
		tv_upgradenow.setTag(getString("action", "0", mArgs));
		//tv_upgradenow.setOnClickListener(bsecure);
		tv_upgradenow.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View view) {
				MessageDialog.this.dismiss();
				bsecure.onClick(view);				
			}
		});
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
	}
	
	public String getString(String key, String defaultValue, Bundle Bundle) {
        final String s = Bundle.getString(key);
        return (s == null) ? defaultValue : s;
    }

}

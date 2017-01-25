package com.mail.sendsafe.fragments;

import com.mail.sendsafe.Bsecure;
import com.mail.sendsafe.R;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Settings extends ParentFragment {

	private View layout = null;

	private Bsecure bsecure = null;

	@Override
	public void onAttach(Context activity) {
		super.onAttach(activity);
		this.bsecure = (Bsecure) activity;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		layout = inflater.inflate(R.layout.settings, container, false);
		layout.setClickable(true);

		layout.findViewById(R.id.tv_blockusers).setOnClickListener(bsecure);
		layout.findViewById(R.id.tv_unblockusers).setOnClickListener(bsecure);

		layout.findViewById(R.id.tv_rating).setOnClickListener(bsecure);
		layout.findViewById(R.id.tv_shareapp).setOnClickListener(bsecure);
		layout.findViewById(R.id.tv_about).setOnClickListener(bsecure);
		layout.findViewById(R.id.tv_change_pass).setOnClickListener(bsecure);
		layout.findViewById(R.id.tv_profile).setOnClickListener(bsecure);
		layout.findViewById(R.id.tv_invitation).setOnClickListener(bsecure);
		layout.findViewById(R.id.tv_notification).setOnClickListener(bsecure);
		layout.findViewById(R.id.tv_about).setOnClickListener(bsecure);
		layout.findViewById(R.id.tv_guidelines).setOnClickListener(bsecure);
		layout.findViewById(R.id.tv_version).setOnClickListener(bsecure);
		layout.findViewById(R.id.tv_contact).setOnClickListener(bsecure);
		layout.findViewById(R.id.tv_viewcontacts).setOnClickListener(bsecure);
		layout.findViewById(R.id.tv_wallet).setOnClickListener(bsecure);
		layout.findViewById(R.id.tv_addcontacts).setOnClickListener(bsecure);
		layout.findViewById(R.id.tv_importcontacts).setOnClickListener(bsecure);
		layout.findViewById(R.id.tv_view_payment).setOnClickListener(bsecure);
		layout.findViewById(R.id.tv_folder).setOnClickListener(bsecure);
		layout.findViewById(R.id.tv_folder_manage).setOnClickListener(bsecure);
		layout.findViewById(R.id.tv_version).setOnClickListener(bsecure);
		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setHasOptionsMenu(true);

		getActivity().supportInvalidateOptionsMenu();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroyView() {

		layout = null;

		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onFragmentChildClick(View view) {
		super.onFragmentChildClick(view);
	}

	@Override
	public String getFragmentName() {
		return "Settings";// getString(R.string.protections);
	}

	@Override
	public int getFragmentActionBarColor() {
		return R.color.settings_gray;
	}

}

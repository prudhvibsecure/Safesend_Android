package com.mail.sendsafe.adapter;

import java.util.List;

import com.mail.sendsafe.R;
import com.mail.sendsafe.models.WalletsAmout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WalletsAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<WalletsAmout> walletlist = null;

	public WalletsAdapter(Context context, List<WalletsAmout> list) {
		this.context = context;
		walletlist = list;
	}

	@Override
	public int getCount() {
		return walletlist.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position + 1;
	}

	public void Clear() {
		walletlist.clear();
	}

	public static class ViewHolder {

		TextView usernae;
		TextView email;

	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View itemView = inflater.inflate(R.layout.list_items, parent, false);
		holder = new ViewHolder();
		holder.usernae = (TextView) itemView.findViewById(R.id.v_user_name);
		holder.email = (TextView) itemView.findViewById(R.id.v_user_email);

		holder.usernae.setText(walletlist.get(position).getFirstname());
		holder.email.setText(walletlist.get(position).getUseremail());

		return itemView;
	}

}
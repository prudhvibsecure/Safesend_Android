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

public class SubAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<WalletsAmout> walletlist = null;

	public SubAdapter(Context context, List<WalletsAmout> list) {
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

		TextView parent_email;
		TextView sub_email;
		TextView name_full;

	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View itemView = inflater.inflate(R.layout.sublevel_layout, parent, false);
		holder = new ViewHolder();
		holder.parent_email = (TextView) itemView.findViewById(R.id.parent_email);
		holder.sub_email = (TextView) itemView.findViewById(R.id.sub_email);
		holder.name_full = (TextView) itemView.findViewById(R.id.name_full);

		holder.parent_email.setText(walletlist.get(position).getParentmail());
		holder.sub_email.setText(walletlist.get(position).getUseremail());
		holder.name_full.setText(walletlist.get(position).getFirstname());

		return itemView;
	}
}

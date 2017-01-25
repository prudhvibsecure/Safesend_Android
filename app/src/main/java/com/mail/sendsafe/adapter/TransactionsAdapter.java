package com.mail.sendsafe.adapter;

import java.util.List;

import com.mail.sendsafe.R;
import com.mail.sendsafe.models.WalletsAmout;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TransactionsAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<WalletsAmout> walletlist = null;

	public TransactionsAdapter(Context context, List<WalletsAmout> list) {
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

		TextView paiddate;
		TextView expdate;
		TextView Amount;

	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View itemView = inflater.inflate(R.layout.wallet_payment_item_trans, parent, false);
		holder = new ViewHolder();
		holder.paiddate = (TextView) itemView.findViewById(R.id.paid_date);
		holder.expdate = (TextView) itemView.findViewById(R.id.exp_date);
		holder.Amount = (TextView) itemView.findViewById(R.id.paid_amt);

		holder.paiddate.setText(Html.fromHtml("Paid date<br/>" + walletlist.get(position).getPaydate()));
		holder.expdate.setText(Html.fromHtml("Expire date<br/>" +walletlist.get(position).getExpirydate()));
		holder.Amount.setText(Html.fromHtml("Amount<br/>" +walletlist.get(position).getAmount()));

		return itemView;
	}

}

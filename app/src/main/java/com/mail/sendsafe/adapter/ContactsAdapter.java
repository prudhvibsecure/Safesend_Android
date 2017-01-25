package com.mail.sendsafe.adapter;

import java.util.List;

import com.mail.sendsafe.R;
import com.mail.sendsafe.account.Contact_Edit;
import com.mail.sendsafe.account.ViewContacts;
import com.mail.sendsafe.models.Contacts;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactsAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<Contacts> contactlist = null;
	private boolean isDeleteEnable = false;

	public ContactsAdapter(Context context, List<Contacts> list) {
		this.context = context;
		contactlist = list;
	}

	@Override
	public int getCount() {
		return contactlist.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position + 1;
	}

	public static class ViewHolder {

		TextView emailid;
		TextView name;
		ImageView chk;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View itemView = inflater.inflate(R.layout.list_items, parent, false);
		holder = new ViewHolder();
		holder.emailid = (TextView) itemView.findViewById(R.id.v_user_email);
		holder.name = (TextView) itemView.findViewById(R.id.v_user_name);
		holder.chk = (ImageView) itemView.findViewById(R.id.check_list);

		holder.name.setText(contactlist.get(position).getFname());
		holder.emailid.setText(contactlist.get(position).getEmailid());

		if (isDeleteEnable) {
			holder.chk.setTag(position);
			holder.chk.setVisibility(View.VISIBLE);
		}
		itemView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				Intent allcat = new Intent(context, Contact_Edit.class);
				allcat.putExtra("abid", contactlist.get(position).getAbid());
				allcat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(allcat);

			}
		});

		holder.chk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent allcat = new Intent(context, ViewContacts.class);
				allcat.putExtra("abid", contactlist.get(position).getAbid());
				allcat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				allcat.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(allcat);

			}
		});
		return itemView;
	}

	public void showDelete(boolean show) {
		isDeleteEnable = show;
	}
}

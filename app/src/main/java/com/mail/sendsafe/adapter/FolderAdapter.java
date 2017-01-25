package com.mail.sendsafe.adapter;

import java.util.Vector;

import com.mail.sendsafe.R;
import com.mail.sendsafe.common.Item;
import com.mail.sendsafe.imageloader.ImageLoader;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ContactViewHolder> {

	Vector<Item> items = new Vector<Item>();
	private Context context = null;
	public ImageLoader imageLoader;

	private boolean isCheckBoxEnable = false, showimage = false;

	String folderName = null;

	public FolderAdapter(Context context, String domainName) {
		this.context = context;
		imageLoader = new ImageLoader(context, false);
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	@Override
	public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {

		Item item = items.get(i);

		folderName = item.getAttribute("foldername");

		contactViewHolder.template_ftitle.setText(item.getAttribute("foldername"));

		contactViewHolder.chkSelected.setVisibility(View.GONE);

		if (isCheckBoxEnable) {
			contactViewHolder.chkSelected.setTag(i);
			contactViewHolder.chkSelected.setVisibility(View.VISIBLE);

			if (item.getAttribute("checked").equalsIgnoreCase("y")) {
				contactViewHolder.chkSelected.setChecked(true);
			} else {
				contactViewHolder.chkSelected.setChecked(false);
			}
		}

	}

	@Override
	public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.folderitem, viewGroup, false);
		itemView.setOnClickListener((OnClickListener) context);

		((CheckBox) itemView.findViewById(R.id.chk_folder)).setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				int id = (Integer) buttonView.getTag();
				if (isChecked) {
					items.get(id).setAttribute("checked", "y");
				} else {
					items.get(id).setAttribute("checked", "");
				}
			}
		});

		return new ContactViewHolder(itemView);
	}

	public int getCount() {
		return items.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position + 1;
	}

	public void setItems(Vector<Item> vector) {
		this.items = vector;
	}

	public Vector<Item> getItems() {
		return this.items;
	}

	public void clear() {
		items.clear();
	}

	public void removeItem(String fid) {

		for (int i = 0; i < items.size(); i++) {
			Item item = items.get(i);
			String cid = item.getAttribute("fid");

			if (cid.equalsIgnoreCase(fid)) {
				items.remove(i);
				break;
			}
		}

		this.notifyDataSetChanged();
	}

	public void clearValue(String fid) {

		for (int i = 0; i < items.size(); i++) {
			Item item = items.get(i);
			String cid = item.getAttribute("fid");

			if (cid.equalsIgnoreCase(fid)) {
				item.setAttribute("checked", "");
				break;
			}
		}

		this.notifyDataSetChanged();
	}

	public void release() {
		items.clear();
		imageLoader.clearCache();
		items = null;
		imageLoader = null;
	}

	public static class ContactViewHolder extends RecyclerView.ViewHolder {

		protected TextView template_ftitle;

		protected ImageView template_folder;

		protected CheckBox chkSelected;

		protected LinearLayout folder_layout;

		public ContactViewHolder(View v) {
			super(v);

			template_ftitle = (TextView) v.findViewById(R.id.fl_name);

			template_folder = (ImageView) v.findViewById(R.id.ur_folder);

			chkSelected = (CheckBox) v.findViewById(R.id.chk_folder);

			folder_layout=(LinearLayout) v.findViewById(R.id.folder_layout);

		}
	}

	public int dpToPx(int dp) {
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return px;
	}

	public void showCheckBoxs(boolean show) {
		isCheckBoxEnable = show;
	}

}
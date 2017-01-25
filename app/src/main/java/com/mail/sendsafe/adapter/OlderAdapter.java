package com.mail.sendsafe.adapter;

import java.util.Vector;

import com.mail.sendsafe.R;
import com.mail.sendsafe.adapter.MailsListingAdapter.ContactViewHolder;
import com.mail.sendsafe.common.Item;
import com.mail.sendsafe.controls.ColorGenerator;
import com.mail.sendsafe.controls.TextDrawable;
import com.mail.sendsafe.imageloader.ImageLoader;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class OlderAdapter extends RecyclerView.Adapter<OlderAdapter.ContactViewHolder> {

	Vector<Item> items = new Vector<Item>();
	private Context context = null;

	public ImageLoader imageLoader;

	private TextDrawable.IBuilder builder = null;
	private ColorGenerator generator = ColorGenerator.MATERIAL;

	private boolean isCheckBoxEnable = false;

	public OlderAdapter(Context context, String domainName) {
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

		String temp = item.getAttribute("address");
		if (temp.length() == 0)
			temp = item.getAttribute("address");
		
		contactViewHolder.template_title.setText(temp);

		if (temp.length() == 0)
			temp = "B";

		contactViewHolder.template_msg.setText(Html.fromHtml(item.getAttribute("message")));

		contactViewHolder.template_date.setText(item.getAttribute("datetime"));

		contactViewHolder.template_attachment.setVisibility(View.GONE);

		if (item.getAttribute("attachments").equalsIgnoreCase("yes"))
			contactViewHolder.template_attachment.setVisibility(View.VISIBLE);

		int color = generator.getColor(temp);

		TextDrawable ic1 = builder.build(temp.substring(0, 1), color);

		contactViewHolder.template_icon.setImageDrawable(ic1);

		String imageUrl = item.getAttribute("IMAGE");

		if (imageUrl.contains("size")) {
			Uri uri = Uri.parse(imageUrl);
			String size = uri.getQueryParameter("size");
			if (size != null) {
				int dp = dpToPx(100);
				imageUrl = imageUrl.replace("size=" + size, "size=" + dp + "x" + dp);
			}
		}

		if (imageUrl.startsWith("http://"))
			imageLoader.DisplayImage(imageUrl, contactViewHolder.template_icon, 2);

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

		if (item.getAttribute("flag").equalsIgnoreCase("read"))
			// contactViewHolder.rv_row_lisiting_layout.setBackgroundColor(context
			// .getResources().getColor(android.R.color.transparent));
			contactViewHolder.rv_row_lisiting_layout
					.setBackgroundColor(context.getResources().getColor(R.color.mailreadcolor));
		// contactViewHolder.template_yellow.setVisibility(View.VISIBLE);

		else
			contactViewHolder.rv_row_lisiting_layout
					.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
		// contactViewHolder.rv_row_lisiting_layout.setBackgroundColor(context
		// .getResources().getColor(R.color.mailreadcolor));

	}

	@Override
	public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

		View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_row_maillisting, viewGroup,
				false);

		itemView.setOnClickListener((OnClickListener) context);

		((CheckBox) itemView.findViewById(R.id.chkSelected)).setOnCheckedChangeListener(new OnCheckedChangeListener() {

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

		builder = TextDrawable.builder().beginConfig().toUpperCase().textColor(Color.WHITE).endConfig().round();

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

	

	public void release() {
		items.clear();
		imageLoader.clearCache();
		items = null;
		imageLoader = null;
	}

	public static class ContactViewHolder extends RecyclerView.ViewHolder {

		protected TextView template_title;

		protected TextView template_msg;

		protected TextView template_date;

		protected ImageView template_icon;

		protected ImageView template_attachment;

		protected ImageView template_restrict;

		protected ImageView template_yellow;

		protected CheckBox chkSelected;

		protected RelativeLayout rv_row_lisiting_layout;

		public ContactViewHolder(View v) {
			super(v);

			template_title = (TextView) v.findViewById(R.id.template_title);

			template_msg = (TextView) v.findViewById(R.id.template_msg);

			template_date = (TextView) v.findViewById(R.id.template_date);

			template_icon = (ImageView) v.findViewById(R.id.template_icon);

			template_restrict = (ImageView) v.findViewById(R.id.template_restrict);

			template_attachment = (ImageView) v.findViewById(R.id.template_attachment);
			template_yellow = (ImageView) v.findViewById(R.id.template_yellow);

			chkSelected = (CheckBox) v.findViewById(R.id.chkSelected);

			rv_row_lisiting_layout = (RelativeLayout) v.findViewById(R.id.rv_row_lisiting_layout);

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
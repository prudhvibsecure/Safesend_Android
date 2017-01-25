package com.mail.sendsafe.adapter;

import java.util.List;

import com.mail.sendsafe.R;
import com.mail.sendsafe.models.Folder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FolderListAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<Folder> folderlist = null;

	public FolderListAdapter(Context context, List<Folder> list) {
		this.context = context;
		folderlist = list;
	}

	@Override
	public int getCount() {
		return folderlist.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position + 1;
	}

	public void Clear() {
		folderlist.clear();
	}

	public static class ViewHolder {

		TextView flername;
		ImageView flderimg;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View itemView = inflater.inflate(R.layout.folderitem, parent, false);
		holder = new ViewHolder();
		holder.flername = (TextView) itemView.findViewById(R.id.fl_name);
		holder.flderimg = (ImageView) itemView.findViewById(R.id.ur_folder);
		holder.flername.setText(folderlist.get(position).getFolderName());

		return itemView;
	}

}

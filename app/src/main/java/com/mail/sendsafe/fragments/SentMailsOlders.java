package com.mail.sendsafe.fragments;

import java.util.Vector;

import org.json.JSONObject;

import com.mail.sendsafe.Bsecure;
import com.mail.sendsafe.R;
import com.mail.sendsafe.adapter.OlderAdapter;
import com.mail.sendsafe.callbacks.IItemHandler;
import com.mail.sendsafe.common.Item;
import com.mail.sendsafe.helper.RecyclerOnScrollListener;
import com.mail.sendsafe.tasks.HTTPPostTask;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class SentMailsOlders extends ParentFragment implements IItemHandler {

	private View layout = null;

	private Bsecure bsecure = null;

	private Item item = null;

	private OlderAdapter adapter = null;

	private RecyclerView mRecyclerView = null;

	private SwipeRefreshLayout mSwipeRefreshLayout = null;

	private RecyclerOnScrollListener recycScollListener = null;

	private boolean isRefresh = false;

	private String total_pages = "0";

	private String forwardprotected = "";
	
	@Override
	public void onAttach(Context activity) {
		super.onAttach(activity);
		this.bsecure = (Bsecure) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Bundle mArgs = getArguments();

		item = (Item) mArgs.getSerializable("item");

		layout = inflater.inflate(R.layout.tempalte_recyclerview, container, false);
		mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeRefreshLayout);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimaryDark,
				R.color.colorPrimaryDark, R.color.colorPrimaryDark, R.color.colorPrimaryDark);
		mSwipeRefreshLayout.setEnabled(false);
		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				recycScollListener.resetValue();
				getData(1, 0);
			}
		});
		layout.findViewById(R.id.fab_newmail).setVisibility(View.GONE);

		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		LinearLayoutManager layoutManager = new LinearLayoutManager(bsecure);

		mRecyclerView = (RecyclerView) layout.findViewById(R.id.content_list);

		mRecyclerView.setLayoutManager(layoutManager);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());

		recycScollListener = new RecyclerOnScrollListener(layoutManager) {

			@Override
			public void onLoadMoreData(int currentPage) {
				if (total_pages.length() > 0)
					if (currentPage <= Integer.parseInt(total_pages) - 1) {
						getData(2, currentPage);
						getView().findViewById(R.id.catgr_pbar).setVisibility(View.VISIBLE);

					}
			}
		};

		mRecyclerView.addOnScrollListener(recycScollListener);
		getData(1, 0);

		setHasOptionsMenu(true);
		getActivity().supportInvalidateOptionsMenu();
	}

	public void refresh() {
		isRefresh = true;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (!bsecure.isDrawerOpen())
			inflater.inflate(R.menu.viewmail_menu, menu);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:

			bsecure.onKeyDown(4, null);

			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void getData(int requestId, int currentNo) {
		getView().findViewById(R.id.catgr_txt).setVisibility(View.GONE);
		try {
			if (adapter == null) {
				adapter = new OlderAdapter(bsecure, "");
				mRecyclerView.setAdapter(adapter);
			}
			String value = item.getAttribute("composeid");
			if (value.length() > 0) {

				String link = bsecure.getPropertyValue("compose_v_m_p");
				JSONObject object = new JSONObject();
				object.put("email", bsecure.getFromStore("email"));
				object.put("composeid", item.getAttribute("composeid"));
				HTTPPostTask task = new HTTPPostTask(getActivity(), this);
				task.disableProgress();
				task.userRequest("", requestId, link, object.toString(), 1);

			}
		} catch (Exception e) {

		}

	}

	@Override
	public void onDestroyView() {

		adapter.clear();
		adapter.notifyDataSetChanged();
		adapter.release();
		adapter = null;

		mRecyclerView.removeAllViews();
		mRecyclerView = null;

		layout = null;

		super.onDestroyView();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden)
			if (isRefresh) {
				isRefresh = false;
				getData(1, 0);
			}
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {

		layout = null;

		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onFinish(Object results, int requestType) {

		try {

			getView().findViewById(R.id.catgr_pbar).setVisibility(View.GONE);

			switch (requestType) {

			case 1:

				if (results != null) {

					Item item = (Item) results;

					forwardprotected = item.getAttribute("forwardprotected");
					
					Vector<Item> items = (Vector<Item>) item.get("older_messages");
					if (items != null && items.size() > 0) {
						adapter.setItems(items);
						adapter.notifyDataSetChanged();
						return;
					}

				}

				adapter.clear();
				adapter.notifyDataSetChanged();
				getView().findViewById(R.id.catgr_txt).setVisibility(View.VISIBLE);

				break;

			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onError(String errorCode, int requestType) {

		mSwipeRefreshLayout.setRefreshing(false);
		mSwipeRefreshLayout.setEnabled(true);

		bsecure.showToast(errorCode);

		getView().findViewById(R.id.catgr_pbar).setVisibility(View.GONE);
	}

	@Override
	public void onFragmentChildClick(View view) {

		int itemPosition = mRecyclerView.getChildLayoutPosition(view);

		Item item = adapter.getItems().get(itemPosition);
		item.setAttribute("forwardprotected", forwardprotected + "");
		bsecure.showSentViewMessagePageView(item);

		super.onFragmentChildClick(view);
	}

	@Override
	public String getFragmentName() {
		return "";
	}

}

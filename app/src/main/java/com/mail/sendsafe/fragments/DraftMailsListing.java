package com.mail.sendsafe.fragments;

import java.util.ArrayList;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mail.sendsafe.Bsecure;
import com.mail.sendsafe.R;
import com.mail.sendsafe.adapter.MailsListingAdapter;
import com.mail.sendsafe.adapter.SearchFeedResultsAdaptor;
import com.mail.sendsafe.callbacks.IItemHandler;
import com.mail.sendsafe.common.AppSettings;
import com.mail.sendsafe.common.Item;
import com.mail.sendsafe.helper.RecyclerOnScrollListener;
import com.mail.sendsafe.tasks.HTTPPostTask;
import com.mail.sendsafe.tasks.HTTPostJson;
import com.mail.sendsafe.utils.Utils;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.SearchView.OnSuggestionListener;
import android.support.v7.widget.SearchView.SearchAutoComplete;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DraftMailsListing extends ParentFragment implements IItemHandler {

	private View layout = null;

	private Bsecure bsecure = null;

	private RecyclerView mRecyclerView = null;

	private SwipeRefreshLayout mSwipeRefreshLayout = null;

	private MailsListingAdapter adapter = null;

	private String total_pages = "0";

	private int tempId = -1;

	private boolean isRefresh = false;

	private String comid = "";

	private SearchView searchView = null;

	private SearchFeedResultsAdaptor mSearchViewAdapter = null;

	private SearchAutoComplete autoCompleteTextView = null;

	public static String[] columns = new String[] { "_id", "TITLE" };

	private RecyclerOnScrollListener recycScollListener = null;

	@Override
	public void onAttach(Context activity) {
		super.onAttach(activity);
		this.bsecure = (Bsecure) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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

		layout.findViewById(R.id.fab_newmail).setOnClickListener(bsecure);

		((TextView) layout.findViewById(R.id.catgr_txt)).setText(R.string.nodrafts);

		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		LinearLayoutManager layoutManager = new LinearLayoutManager(bsecure);

		mRecyclerView = (RecyclerView) layout.findViewById(R.id.content_list);
		mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeRefreshLayout);
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

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		if (!bsecure.isDrawerOpen()) {
			inflater.inflate(R.menu.mailslisting_menu, menu);

			searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
			mSearchViewAdapter = new SearchFeedResultsAdaptor(this.getActivity(),
					R.layout.abc_search_dropdown_item_icons_2line, null, columns, null, -1000);

			autoCompleteTextView = (SearchAutoComplete) searchView.findViewById(R.id.search_src_text);

			searchView.setSuggestionsAdapter(mSearchViewAdapter);

			searchView.setOnQueryTextListener(new OnQueryTextListener() {

				@Override
				public boolean onQueryTextSubmit(String searchKey) {
					searchContent(searchKey);
					return false;
				}

				@Override
				public boolean onQueryTextChange(String arg0) {
					return false;
				}

			});

			searchView.setOnCloseListener(new OnCloseListener() {

				@Override
				public boolean onClose() {

					return false;
				}
			});

			searchView.setOnSuggestionListener(new OnSuggestionListener() {

				@Override
				public boolean onSuggestionSelect(int position) {

					Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
					String feedName = cursor.getString(1);
					searchView.setQuery(feedName, false);
					searchView.clearFocus();

					return false;
				}

				@Override
				public boolean onSuggestionClick(int position) {

					Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
					String feedName = cursor.getString(1);
					searchView.setQuery(feedName, false);
					searchView.clearFocus();

					return false;
				}
			});

		}

		super.onCreateOptionsMenu(menu, inflater);
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
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	private void getData(int requestId, int currentNo) {

		// getView().findViewById(R.id.catgr_pbar).setVisibility(View.VISIBLE);
		getView().findViewById(R.id.catgr_txt).setVisibility(View.GONE);
		try {
			if (adapter == null) {
				adapter = new MailsListingAdapter(bsecure, "");
				mRecyclerView.setAdapter(adapter);
			}

			String link = bsecure.getPropertyValue("getdraftmaillist");
			JSONObject object = new JSONObject();
			object.put("email", bsecure.getFromStore("email"));
			object.put("pageno", currentNo + "");
			HTTPPostTask task = new HTTPPostTask(getActivity(), this);
			task.disableProgress();
			task.userRequest("", requestId, link, object.toString(), 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void searchContent(String searchKey) {
		// getView().findViewById(R.id.catgr_pbar).setVisibility(View.VISIBLE);
		getView().findViewById(R.id.catgr_txt).setVisibility(View.GONE);
		try {
			if (adapter == null) {
				adapter = new MailsListingAdapter(bsecure, "");
				mRecyclerView.setAdapter(adapter);
			}
			adapter.clear();
			adapter.notifyDataSetChanged();

			String link = bsecure.getPropertyValue("searchInDraft");
			JSONObject object = new JSONObject();
			object.put("email", bsecure.getFromStore("email"));
			object.put("searchemail", searchKey);

			HTTPPostTask task = new HTTPPostTask(getActivity(), this);
			task.disableProgress();
			task.userRequest("", 2, link, object.toString(), 1);//
		} catch (Exception e) {

		}
	}

	@Override
	public void onFinish(Object results, int requestType) {

		try {

			getView().findViewById(R.id.catgr_pbar).setVisibility(View.GONE);

			switch (requestType) {
			case 1:
				mSwipeRefreshLayout.setRefreshing(false);
				mSwipeRefreshLayout.setEnabled(true);

//				if (bsecure.getFromStore("refreshdraft").length() > 0) {
//					getData(1, 0);
//				}
				if (results != null) {

					Item item = (Item) results;

					Object object = item.get("totalpages");
					if (object != null) {

						Vector<Item> items = (Vector<Item>) object;
						Item temp = items.get(0);
						String cnt = temp.getAttribute("pages");

						if (cnt.trim().length() == 0) {
							total_pages = "0";
						}

						if (cnt.trim().length() > 0) {
							total_pages = cnt.trim();
						}
					}

					bsecure.updateMailCounts(item);

					// Vector<Item> items = (Vector<Item>)
					// item.get("mail_detail");
					Vector<Item> items = (Vector<Item>) item.get("drafts_detail");
					if (items == null)
						items = (Vector<Item>) item.get("drafts_detail");

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

			case 2:

				if (results != null) {

					Item item = (Item) results;

					Vector<Item> items = (Vector<Item>) item.get("drafts_detail");

					if (items == null)
						items = (Vector<Item>) item.get("drafts_detail");

					if (items != null && items.size() > 0) {

						Vector<Item> oldCContain = adapter.getItems();
						oldCContain.addAll(items);

						adapter.setItems(oldCContain);
						adapter.notifyDataSetChanged();
						return;
					}

				}

				break;

			case 3:
				Utils.dismissProgress();
				parseMoveToTrashResponse(results);
				comid = "";
				getData(1, 0);
				break;

			case 4:

				if (results != null) {
					JSONObject object = new JSONObject(results.toString());
					if (object.has("status") && object.optString("status").equalsIgnoreCase("0")) {
						JSONArray array = object.getJSONArray("emails_det");
						if (array != null && array.length() > 0) {
							ArrayList<String> list = new ArrayList<String>();
							for (int i = 0; i < array.length(); i++) {
								JSONObject jObject = array.getJSONObject(i);
								String useremails = jObject.optString("useremails");
								useremails = useremails.trim();
								if (useremails.length() > 0) {
									list.add(useremails);
								}
							}

							MatrixCursor matrixCursor = convertToCursor(list);
							mSearchViewAdapter.changeCursor(matrixCursor);
							mSearchViewAdapter.notifyDataSetChanged();
							autoCompleteTextView.showDropDown();
						}
					}
				}

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

		comid = "";

	}

	@Override
	public void onFragmentChildClick(View view) {

		int itemPosition = mRecyclerView.getChildLayoutPosition(view);

		Item item = adapter.getItems().get(itemPosition);

		bsecure.openDraftMail(item);

		super.onFragmentChildClick(view);
	}

	public void removeItem(String cid) {
		if (adapter == null)
			return;

		adapter.removeItem(cid);

		if (adapter.getCount() == 0) {
			getView().findViewById(R.id.catgr_txt).setVisibility(View.VISIBLE);
		}
	}

	@Override
	public String getFragmentName() {
		return "Drafts";// getString(R.string.drafts);
	}

	public void updateView(String cid, String packageId) {
		removeItem(cid);
	}

	public void refresh() {
		isRefresh = true;
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {

		if (tempId == 1) {
			MenuItem item = menu.findItem(R.id.cm_delete);
			item.setVisible(false);

			MenuItem cm_delete_done = menu.findItem(R.id.cm_delete_done);
			cm_delete_done.setVisible(true);
		} else if (tempId == 2) {
			MenuItem item = menu.findItem(R.id.cm_delete);
			if (item != null)
				item.setVisible(true);

			MenuItem cm_delete_done = menu.findItem(R.id.cm_delete_done);
			if (cm_delete_done != null)
				cm_delete_done.setVisible(false);
		}

		super.onPrepareOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:

			bsecure.unLockMode();
			adapter.showCheckBoxs(false);
			adapter.notifyDataSetChanged();

			getActivity().supportInvalidateOptionsMenu();
			tempId = 2;

			break;

		case R.id.cm_delete:

			if (adapter != null && adapter.getItems().size() == 0)
				return true;

			adapter.showCheckBoxs(true);
			adapter.notifyDataSetChanged();
			bsecure.lockMode();

			getActivity().supportInvalidateOptionsMenu();
			tempId = 1;

			break;

		case R.id.cm_delete_done:

			moveMailToTrash();

			bsecure.unLockMode();
			adapter.showCheckBoxs(false);
			adapter.notifyDataSetChanged();

			getActivity().supportInvalidateOptionsMenu();
			tempId = 2;

			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void moveMailToTrash() {

		try {
			int count = adapter.getItems().size();

			for (int i = 0; i < count; i++) {
				Item item = adapter.getItems().get(i);
				if (item.getAttribute("checked").equalsIgnoreCase("y")) {
					comid = comid + "," + item.getAttribute("draftid");
				}
			}

			if (comid.length() == 0) {
				return;
			}

			comid = comid.substring(1);

			String url = AppSettings.getInstance(bsecure).getPropertyValue("draftsTotrash");
			JSONObject object = new JSONObject();
			object.put("email", bsecure.getFromStore("email"));

			Item item = new Item("");
			item.setAttribute("X-BSECURE-IDS", comid);

			HTTPostJson post = new HTTPostJson(this, getActivity(), object.toString(), 3);
			post.setContentType("application/json");
			post.setHeaders(item);
			post.execute(url, "");
			Utils.showProgress(getString(R.string.pwait), getActivity());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void parseMoveToTrashResponse(Object object) throws Exception {

		if (object != null) {
			JSONObject jsonObject = new JSONObject(object.toString());
			if (jsonObject.optString("status").equalsIgnoreCase("0")) {

				String temp[] = comid.split(",");

				for (int i = 0; i < temp.length; i++) {
					adapter.removeDraftItem(temp[i]);
				}
				adapter.notifyDataSetChanged();

			}
			bsecure.showToast(jsonObject.optString("statusdescription"));
		}

	}

	private MatrixCursor convertToCursor(ArrayList<String> feedlyResults) {
		MatrixCursor cursor = new MatrixCursor(columns);
		int i = 0;

		for (int j = 0; j < feedlyResults.size(); j++) {
			String[] temp = new String[2];
			i = i + 1;
			temp[0] = Integer.toString(i);

			String feedUrl = feedlyResults.get(j);
			temp[1] = feedUrl;
			cursor.addRow(temp);
		}
		return cursor;
	}

	@Override
	public int getFragmentActionBarColor() {
		return R.color.orange;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden)
			if (isRefresh) {
				isRefresh = false;

				if (bsecure.getFromStore("refreshdraft").length() > 0) {
					bsecure.addToStore("refreshdraft", "");
					getData(1, 0);
				}
				getData(1, 0);
			}
	}

}

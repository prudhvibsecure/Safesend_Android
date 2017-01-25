package com.mail.sendsafe.fragments;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.json.JSONObject;

import com.mail.sendsafe.Bsecure;
import com.mail.sendsafe.MailPin_Act;
import com.mail.sendsafe.R;
import com.mail.sendsafe.callbacks.IItemHandler;
import com.mail.sendsafe.common.AppPreferences;
import com.mail.sendsafe.common.Item;
import com.mail.sendsafe.tasks.HTTPPostTask;
import com.mail.sendsafe.utils.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MailViews extends ParentFragment implements IItemHandler {

	private View layout = null;

	private Bsecure bsecure = null;

	private Item item = null;

	// private TextView tv_msg = null;

	private WebView tv_msg = null;

	private int mailType = -1;

	private TextView tv_reply = null;

	private TextView tv_from = null;

	private TextView tv_subject = null;

	private TextView tv_timeleft = null, tv_validupto = null;

	private int isShowTime = 0;

	private Timer timer = null, secTimer = null;

	private TimerTask timerTask = null;

	final Handler myHandler = new Handler();

	private LinearLayout ll_attachments_layout = null;

	private TextView tv_olderMsg = null, tv_report = null, tv_forward = null, tv_cctext = null;

	private LinearLayout ll_oldermsg_layout = null;

	private Dialog dialog = null;

	private View scroll_layout = null;

	@Override
	public void onAttach(Context activity) {
		super.onAttach(activity);
		this.bsecure = (Bsecure) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Bundle mArgs = getArguments();

		item = (Item) mArgs.getSerializable("item");

		layout = inflater.inflate(R.layout.mailview, container, false);

		ll_oldermsg_layout = (LinearLayout) layout.findViewById(R.id.ll_oldermsg_layout);

		tv_olderMsg = (TextView) layout.findViewById(R.id.tv_olderMsg);
		tv_olderMsg.setOnClickListener(bsecure);

		tv_report = (TextView) layout.findViewById(R.id.tv_Report);

		tv_forward = (TextView) layout.findViewById(R.id.tv_Forward);

		// tv_msg = (TextView) layout.findViewById(R.id.tv_msg);

		tv_msg = (WebView) layout.findViewById(R.id.tv_msg);
		tv_msg.setVerticalScrollBarEnabled(true);
		tv_msg.setHorizontalScrollBarEnabled(true);
		tv_msg.getSettings().setJavaScriptEnabled(true);
		tv_msg.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return true;
			}
		});

		tv_cctext = (TextView) layout.findViewById(R.id.tv_cc_text);
		tv_cctext.setVisibility(View.VISIBLE);
		// tv_msg.setMovementMethod(new ScrollingMovementMethod());

		tv_reply = (TextView) layout.findViewById(R.id.tv_reply);

		tv_from = (TextView) layout.findViewById(R.id.tv_from);

		tv_timeleft = (TextView) layout.findViewById(R.id.tv_timeleft);

		tv_validupto = (TextView) layout.findViewById(R.id.tv_validupto);

		tv_subject = (TextView) layout.findViewById(R.id.tv_subject);

		ll_attachments_layout = (LinearLayout) layout.findViewById(R.id.ll_attachments_layout);

		return layout;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		tv_reply.setOnClickListener(bsecure);
		tv_report.setOnClickListener(bsecure);
		getData();

		setHasOptionsMenu(true);
		getActivity().supportInvalidateOptionsMenu();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (!bsecure.isDrawerOpen())
			inflater.inflate(R.menu.forwardmail_menu, menu);

		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		if (!item.getAttribute("forwardprotected").equalsIgnoreCase("no")) {
			MenuItem menuItem = menu.findItem(R.id.cm_forword);
			menuItem.setVisible(false);
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:

			bsecure.onKeyDown(4, null);

			break;
		case R.id.cm_forword:

			bsecure.openForwardMail(this.item);

			break;
		case R.id.cm_report:

			bsecure.showReportDialog(this.item);

			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void getData() {

		try {
			String link = "";
			JSONObject object = null;
			// String pin = item.getAttribute("pin");

			if (!item.getAttribute("pin").equalsIgnoreCase("0")) {
				mailType = 0;
				link = bsecure.getPropertyValue("view_pro_mesg_pin");
				object = new JSONObject();
				// object.put("email", bsecure.getFromStore("email"));
				object.put("composeid", item.getAttribute("composeid"));
				object.put("chkmsg", item.getAttribute("chkmsg"));
				object.put("msgid", item.getAttribute("msgid"));
				object.put("pin", item.getAttribute("pin"));

				Intent intent = new Intent(bsecure, MailPin_Act.class);
				intent.putExtra("url", link);
				intent.putExtra("pin", item.getAttribute("pin"));
				intent.putExtra("composeid", item.getAttribute("composeid"));
				intent.putExtra("chkmsg", item.getAttribute("chkmsg"));
				intent.putExtra("msgid", item.getAttribute("msgid"));
				bsecure.launchMailPin(intent);

			}
			if (item.getAttribute("pin").equalsIgnoreCase("0")) {
				mailType = 1;
				link = bsecure.getPropertyValue("view_pro_mesg");
				object = new JSONObject();
				// object.put("email", bsecure.getFromStore("email"));
				object.put("composeid", item.getAttribute("composeid"));
				object.put("chkmsg", item.getAttribute("chkmsg"));
				object.put("msgid", item.getAttribute("msgid"));

			}
			HTTPPostTask task = new HTTPPostTask(getActivity(), this);
			task.disableProgress();
			task.userRequest(getString(R.string.pleasewait), 1, link, object.toString(), 1);// object.toString()
		} catch (Exception e) {

		}
	}

	@Override
	public void onDestroyView() {

		layout = null;

		super.onDestroyView();
	}

	@Override
	public void onDestroy() {

		if (secTimer != null)
			secTimer.cancel();

		secTimer = null;

		isShowTime = 0;

		stoptimertask();

		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onFinish(Object results, int requestType) {

		try {

			Utils.dismissProgress();

			switch (mailType) {

			case 0:
				showComposeMessagePin(results);
				break;
			case 1:
				showComposeMessage(results);
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

		bsecure.showToast(errorCode);
		Utils.dismissProgress();
	}

	@Override
	public void onFragmentChildClick(View view) {
		super.onFragmentChildClick(view);
	}

	@Override
	public String getFragmentName() {
		return "";
	}

	private void showComposeMessagePin(Object results) {
		if (results != null) {

			Item item = (Item) results;

			if (item.getAttribute("status").equalsIgnoreCase("0")) {

				Vector<Item> items = (Vector<Item>) item.get("mail_details");
				Item msgItem = items.get(0);

				if (msgItem.getAttribute("error").equalsIgnoreCase("yes")) {
					// tv_msg.setText(Html.fromHtml("<h3>Message:</h3>" +
					// msgItem.getAttribute("mesg")));
					// tv_msg.setMovementMethod(new ScrollingMovementMethod());
					// tv_msg.setMovementMethod(LinkMovementMethod.getInstance());
					// tv_msg.setLinksClickable(true);
					String mymessage = msgItem.getAttribute("mesg");
					mymessage = mymessage.replaceAll("\n", "<br/>");
					tv_msg.loadData("<h3>Message:</h3>" + mymessage, "text/html", "utf-8");
					return;
				} else {
					// tv_msg.setText(Html.fromHtml("<h3>Message:</h3>" +
					// msgItem.getAttribute("mesg")));
					String mymessage = msgItem.getAttribute("mesg");
					mymessage = mymessage.replaceAll("\n", "<br/>");
					tv_msg.loadData("<h3>Message:</h3>" + mymessage, "text/html", "utf-8");

					String from = msgItem.getAttribute("to");

					showAttachments(msgItem.getAttribValue("attachment_detail"),from,msgItem.getAttribute("showtime"));
					startShowTimeTask();
					sendMailListRefresh();

					if (msgItem.getAttribute("button").equalsIgnoreCase("Reply")) {
						tv_reply.setVisibility(View.VISIBLE);
						tv_reply.setTag(item);
					}
					if (msgItem.getAttribute("button").equalsIgnoreCase("Recompose")) {
						tv_reply.setVisibility(View.VISIBLE);
						tv_reply.setTag(item);

					}
					String cc_valus = msgItem.getAttribute("displaycc");
					if (cc_valus.equalsIgnoreCase("")) {
						tv_cctext.setVisibility(View.GONE);
					} else {
						// String[] emailArray = cc_valus.split(",");
						String ccc_valus = cc_valus.replace(",", ",<br/>");
						tv_cctext.setText(Html.fromHtml("<b>Cc:</b>" + " " + ccc_valus));
					}

					String validuntil = msgItem.getAttribute("validuntil");
					if (validuntil.length() > 0 && !validuntil.equalsIgnoreCase("01-01-1970")) {
						tv_validupto.setText("Valid Until: " + validuntil);
					}

					String showtime = msgItem.getAttribute("showtime");
					showtime = showtime.replace("Sec", "");
					showtime = showtime.replace("sec", "");
					showtime = showtime.trim();

					if (showtime.length() > 0 && !showtime.equalsIgnoreCase("Unlimited")) {
						isShowTime = 10;

						try {
							isShowTime = Integer.parseInt(showtime);
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

					String composeid = (String) msgItem.getAttribValue("composeid");
					if (composeid.length() > 0 && composeid != null) {
						tv_report.setVisibility(View.VISIBLE);
						tv_report.setTag(item);
					}

				}

			} else if (item.getAttribute("status").equalsIgnoreCase("1")) {
				// tv_msg.setText(item.getAttribute("statusdescription"));
				tv_msg.loadData(item.getAttribute("statusdescription"), "", "utf-8");
			}

		}
	}

	private void showComposeMessage(Object results) {
		if (results != null) {

			Item item = (Item) results;

			if (item.getAttribute("status").equalsIgnoreCase("0")) {

				Vector<Item> items = (Vector<Item>) item.get("mail_details");
				Item msgItem = items.get(0);

				if (msgItem.getAttribute("error").equalsIgnoreCase("yes")) {
					// tv_msg.setText(Html.fromHtml("<h3>Message:</h3>" +
					// msgItem.getAttribute("mesg")));
					String mymessage = msgItem.getAttribute("mesg");
					mymessage = mymessage.replaceAll("\n", "<br/>");
					tv_msg.loadData("<h3>Message:</h3>" + mymessage, "text/html", "utf-8");
					return;
				} else {
					// tv_msg.setText(Html.fromHtml("<h3>Message:</h3>" +
					// msgItem.getAttribute("mesg")));
					// tv_msg.setMovementMethod(LinkMovementMethod.getInstance());
					String mymessage = msgItem.getAttribute("mesg");
					mymessage = mymessage.replaceAll("\n", "<br/>");
					tv_msg.loadData("<h3>Message:</h3>" + mymessage, "text/html", "utf-8");

					String from = msgItem.getAttribute("to");
					showAttachments(msgItem.getAttribValue("attachment_detail"),from,msgItem.getAttribute("showtime"));

					sendMailListRefresh();

					if (msgItem.getAttribute("button").equalsIgnoreCase("Reply")) {
						tv_reply.setVisibility(View.VISIBLE);
						tv_reply.setTag(item);
					}
					if (msgItem.getAttribute("button").equalsIgnoreCase("Recompose")) {
						tv_reply.setVisibility(View.VISIBLE);
						tv_reply.setTag(item);

					}
					String cc_valus = msgItem.getAttribute("displaycc");
					if (cc_valus.equalsIgnoreCase("")) {
						tv_cctext.setVisibility(View.GONE);
					} else {
						// String[] emailArray = cc_valus.split(",");
						String ccc_valus = cc_valus.replace(",", ",<br/>");
						tv_cctext.setText(Html.fromHtml("<b>Cc:</b>" + " " + ccc_valus));
					}
					String validuntil = msgItem.getAttribute("validuntil");
					if (validuntil.length() > 0 && !validuntil.equalsIgnoreCase("01-01-1970")) {
						tv_validupto.setText("Valid Until: " + validuntil);
					}

					String showtime = msgItem.getAttribute("showtime");
					showtime = showtime.replace("Sec", "");
					showtime = showtime.replace("sec", "");
					showtime = showtime.trim();

					if (showtime.length() > 0 && !showtime.equalsIgnoreCase("Unlimited")) {
						isShowTime = 10;

						try {
							isShowTime = Integer.parseInt(showtime);
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
					startShowTimeTask();
					String composeid = (String) msgItem.getAttribValue("composeid");
					if (composeid.length() > 0 && composeid != null) {
						tv_report.setVisibility(View.VISIBLE);
						tv_report.setTag(item);
					}

				}

			} else if (item.getAttribute("status").equalsIgnoreCase("1")) {
				// tv_msg.setText(item.getAttribute("statusdescription"));
				tv_msg.loadData(item.getAttribute("statusdescription"), "", "utf-8");
			}

		}
	}

	public void startShowTimeTask() {

		if (isShowTime == -1)
			return;

		if (isShowTime == 0)
			return;

		final long milliseconds = isShowTime * 1000;

		startTimer(milliseconds);

		/*
		 * mUpdateTimeTask = new Runnable() { public void run() {
		 * 
		 * bsecure.onKeyDown(4, null); stoptimertask();
		 * 
		 * } }; handler.postDelayed(mUpdateTimeTask, milliseconds);
		 */

		int delay = 1000;
		int period = 1000;

		secTimer = new Timer();

		secTimer.schedule(new TimerTask() {
			long ms = milliseconds;

			public void run() {
				myHandler.post(new Runnable() {
					public void run() {
						ms = ms - 1000;
						tv_timeleft.setText("Time left: " + milliSecondsToTime(ms));
					}
				});

			}
		}, delay, period);

	}

	public void startTimer(long time) {

		try {

			timer = new Timer();

			initializeTimerTask();

			timer.schedule(timerTask, time);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void stoptimertask() {

		try {

			if (timer != null)
				timer.cancel();

			timer = null;

			if (timerTask != null)
				timerTask.cancel();

			timerTask = null;

			// handler = null;

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void initializeTimerTask() {

		try {

			timerTask = new TimerTask() {
				public void run() {

					Handler h = new Handler(Looper.getMainLooper());
					h.post(new Runnable() {
						public void run() {
							sendMsgToCloseActivity();
							bsecure.commitFlag = false;
							bsecure.onKeyDown(4, null);
							bsecure.commitFlag = true;
							bsecure.showToast(R.string.ymnlefv);
							bsecure.showToast(R.string.ymnlefv);
						}
					});
				}
			};

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendMsgToCloseActivity() {
		Intent gcmRegistrationComplete = new Intent();
		gcmRegistrationComplete.setAction("com.mail.sendsafe.close.activity");
		LocalBroadcastManager.getInstance(bsecure).sendBroadcast(gcmRegistrationComplete);
	}

	private void sendMailListRefresh() {
		bsecure.sendMailListRefresh();
	}

	public static String milliSecondsToTime(long milliseconds) {
		String finalTimerString = "";
		String secondsString = "";

		// Convert total duration into time
		int hours = (int) (milliseconds / (1000 * 60 * 60));
		int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
		int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
		// Add hours if there
		if (hours > 0) {
			finalTimerString = hours + ":";
		}

		// Prepending 0 to seconds if it is one digit
		if (seconds < 10) {
			secondsString = "0" + seconds;
		} else {
			secondsString = "" + seconds;
		}

		finalTimerString = finalTimerString + minutes + ":" + secondsString;

		// return timer string
		return finalTimerString;
	}

	public void showAttachments(Object attachments,String from,String timer) {
		try {

			if (attachments == null)
				return;

			Vector<Item> attachmentsArray = (Vector<Item>) attachments;

			for (int i = 0; i < attachmentsArray.size(); i++) {

				Item item = attachmentsArray.get(i);

				String attachmentlevel = item.getAttribute("attachmentlevel");
				String library = item.getAttribute("library");

				item.setAttribute("showtime",timer);

				AppPreferences.getInstance(getActivity()).addToStore("from",from);

				View view = View.inflate(bsecure, R.layout.template_attachments_view_layout, null);


				view.findViewById(R.id.tv_attachmentView).setOnClickListener(bsecure);
				view.findViewById(R.id.tv_attachmentViewnDwn).setOnClickListener(bsecure);

				// view.findViewById(R.id.tv_attachmentView).setTag(item.getAttribValue("attachmentorigname"));
				// view.findViewById(R.id.tv_attachmentViewnDwn).setTag(item.getAttribValue("attachmentorigname"));
				view.findViewById(R.id.tv_attachmentView).setTag(item);
				view.findViewById(R.id.tv_attachmentViewnDwn).setTag(item);

				((TextView) view.findViewById(R.id.tv_attachmentName)).setText(item.getAttribute("attachmentorigname"));

				if (attachmentlevel.equalsIgnoreCase("View Only") || attachmentlevel.equalsIgnoreCase("Only View")) {
					view.findViewById(R.id.tv_attachmentView).setVisibility(View.VISIBLE);
					view.findViewById(R.id.tv_attachmentViewnDwn).setVisibility(View.GONE);
				} else if (attachmentlevel.equalsIgnoreCase("View & Download")) {
					view.findViewById(R.id.tv_attachmentView).setVisibility(View.GONE);
					view.findViewById(R.id.tv_attachmentViewnDwn).setVisibility(View.VISIBLE);
				}

				ll_attachments_layout.addView(view);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public int getShowTime() {
		return isShowTime;
	}

}

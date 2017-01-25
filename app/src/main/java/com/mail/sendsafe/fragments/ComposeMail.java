package com.mail.sendsafe.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mail.sendsafe.Bsecure;
import com.mail.sendsafe.R;
import com.mail.sendsafe.callbacks.IDownloadCallback;
import com.mail.sendsafe.callbacks.IItemHandler;
import com.mail.sendsafe.common.AppPreferences;
import com.mail.sendsafe.common.AppSettings;
import com.mail.sendsafe.common.Item;
import com.mail.sendsafe.controls.ProgressControl;
import com.mail.sendsafe.controls.bubble.Hyperlink;
import com.mail.sendsafe.controls.bubble.OnBubbleClickListener;
import com.mail.sendsafe.controls.bubble.TagEditText;
import com.mail.sendsafe.tasks.FileUploader;
import com.mail.sendsafe.tasks.HTTPostJson;
import com.mail.sendsafe.utils.TimeUtils;
import com.mail.sendsafe.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

public class ComposeMail extends ParentFragment implements IItemHandler, IDownloadCallback {

    private View layout = null;

    private ImageView tv_showhideccbcc;

    private View ll_ccbcc;

    private Bsecure bsecure = null;

    ArrayList<String> list = null;

    private String readonly = "no";
    private String reply = "0"; /* 0 = yes, 1 = no */
    private String autodelete = "No";
    private String pin = ""; /* 0 = no, 1 = yes */
    private String time = "Unlimited";
    // private String validity = "01-01-1970";
    private String validity = "Unlimited";

    private Item item = null;

    private String rid = "", sub = "", subid = "subcompose";

    private EditText ed_ccText = null, ed_bccText = null, ed_subText = null, ed_comText = null;

    //private AutoCompleteTextView ed_toText = null;

    private int attachmentCounter = 0;

    private long attachmentSize = 0;

    private LinearLayout attachmentLayout = null, ll_forward_attachments_layout = null;

    private String draftid = "";

    private int counter = 20;

    private boolean exitFlag = false;

    private Hashtable<Integer, FileUploader> requestItems = new Hashtable<Integer, FileUploader>();

    private JSONArray attachmentArray = null;

    private boolean isFwdAction;

    private View categories_layout = null;

    public boolean flag = false;

    private String msgid, cidd, button;

    private Item msgItem;

    private Vector<Item> items;

    private String forwardaid = "";

    private View view;
    private int viewId;

    private boolean isRefresh = false;

    private TagEditText bet_cctext, bet_bcctext, ed_toText;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        this.bsecure = (Bsecure) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle mArgs = getArguments();

        if (mArgs != null) {
            item = (Item) mArgs.getSerializable("item");
            // isFwdAction = mArgs.getBoolean("isFwdAction");
        }

        layout = inflater.inflate(R.layout.composemail, container, false);
        ll_ccbcc = layout.findViewById(R.id.ll_ccbcc);
        tv_showhideccbcc = (ImageView) layout.findViewById(R.id.tv_showhideccbcc);
        attachmentArray = new JSONArray();
        categories_layout = layout.findViewById(R.id.categories_layout);

        /*View ccText = layout.findViewById(R.id.tv_ccText);
        View bccText = layout.findViewById(R.id.tv_bccText);
        ccText.setOnClickListener(bsecure);
        bccText.setOnClickListener(bsecure);
*/
        if (item != null) {
            tv_showhideccbcc.setVisibility(View.INVISIBLE);
        }

        //layout.findViewById(R.id.tv_addattachment).setOnClickListener(bsecure);
        bet_cctext = (TagEditText) layout.findViewById(R.id.bet_cctext);

        bet_bcctext = (TagEditText) layout.findViewById(R.id.bet_bcctext);
        ed_toText = (TagEditText) layout.findViewById(R.id.ed_toText);
        /*
         * ed_toText.setTokenizer(new
		 * MultiAutoCompleteTextView.CommaTokenizer());
		 */

        ed_toText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                getSearchContacts(s.toString());
            }
        });

        ed_ccText = (EditText) layout.findViewById(R.id.ed_ccText);

        ed_bccText = (EditText) layout.findViewById(R.id.ed_bccText);

        ed_subText = (EditText) layout.findViewById(R.id.ed_subText);

        ed_comText = (EditText) layout.findViewById(R.id.ed_comText);

        /*ed_comText.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.ed_comText) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });*/

        attachmentLayout = (LinearLayout) layout.findViewById(R.id.ll_attachments_layout);

        ll_forward_attachments_layout = (LinearLayout) layout.findViewById(R.id.ll_forward_attachments_layout);

        if (item != null) {
            items = (Vector<Item>) item.get("mail_details");
            if (items != null) {
                msgItem = items.get(0);
                if (msgItem != null) {
                    msgid = msgItem.getAttribute("msgid").trim();
                    button = msgItem.getAttribute("button");
                    cidd = msgItem.getAttribute("composeid").trim();
                    if (msgid.length() > 0 || cidd.length() > 0) {
                        ed_toText.setText(msgItem.getAttribute("to"));
                        ed_toText.setEnabled(false);
                        ed_toText.setFocusableInTouchMode(false);
                    }
                    if (msgItem.getAttribute("to").length() == 0)
                        ed_toText.setText(msgItem.getAttribute("from"));

                }
            }
            if (item.getAttribute("drafts_values").equalsIgnoreCase("yes")) {
                ed_toText.setText(item.getAttribute("tomsg"));
                ed_subText.setText(item.getAttribute("subject"));
                ed_comText.setText(Html.fromHtml(item.getAttribute("message")));

                if (item.getAttribute("cc").length() > 0) {
                    layout.findViewById(R.id.ll_cc).setVisibility(View.VISIBLE);
                    bet_cctext.setText(item.getAttribute("cc"));
                }

                if (item.getAttribute("bcc").length() > 0) {
                    layout.findViewById(R.id.ll_bcc).setVisibility(View.VISIBLE);
                    bet_bcctext.setText(item.getAttribute("bcc"));
                }

                readonly = item.getAttribute("readonly");
                time = item.getAttribute("showtime");
                autodelete = item.getAttribute("autodelete");
                pin = item.getAttribute("pin");
                validity = item.getAttribute("validuntil");
                reply = item.getAttribute("reply");

                draftid = item.getAttribute("draftid");

                showFilterValues(getFilterVlaues());

                addDraftAttachmentView(item);

            }

            if (item.getAttribute("forward_values").equalsIgnoreCase("yes")) {
                getForwardDetails(item);
            }

        }

        /*int height = AppPreferences.getInstance(bsecure).getFromStoreInteger("height");
        if (height > 0) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, height);
            ed_comText.setLayoutParams(params);
        } else {
            getSize();
        }*/

        // getSize();

        return layout;

    }

    private void getForwardDetails(Item item) {

        try {

            String link = bsecure.getPropertyValue("getForwarddetails");
            JSONObject object = new JSONObject();
            object.put("email", bsecure.getFromStore("email"));
            object.put("composeid", item.getAttribute("composeid"));
            object.put("msgno", item.getAttribute("msgno"));

            HTTPostJson post = new HTTPostJson(this, getActivity(), object.toString(), 4);
            post.setContentType("application/json");
            post.execute(link, "");
            Utils.showProgress(getString(R.string.pwait), getActivity());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getSearchContacts(String searchkey) {

        try {
            String link = bsecure.getPropertyValue("searchContacts");
            JSONObject object = new JSONObject();
            object.put("email", bsecure.getFromStore("email"));
            object.put("searchdata", searchkey);

            HTTPostJson post = new HTTPostJson(this, getActivity(), object.toString(), 3);
            post.setContentType("application/json");
            post.execute(link, "");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ed_comText.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                int textlength1 = ed_comText.getText().length();

                if (textlength1 >= counter) {
                    saveDraft();
                    counter = counter * 2;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        setHasOptionsMenu(true);

        getActivity().supportInvalidateOptionsMenu();

        tv_showhideccbcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ll_ccbcc.setVisibility(ll_ccbcc.isShown() ? View.GONE : View.VISIBLE);

                tv_showhideccbcc.setImageResource(ll_ccbcc.isShown() ? R.drawable.ic_keyboard_arrow_up_black_24dp : R.drawable.ic_keyboard_arrow_down_black_24dp);

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!bsecure.isDrawerOpen())
            inflater.inflate(R.menu.compose_menu, menu);
        MenuItem item = menu.findItem(R.id.cm_attachments);
        if (attachmentCounter < 10) {
            item.setVisible(true);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.cm_filter:
                bsecure.showFilterFragment(true);
                break;

            case R.id.cm_send:
                sendMail();
                break;
            case R.id.cm_attachments:
                try {
                    if (attachmentCounter == 10) {
                        //item.setVisible(false);
                        bsecure.showToast("10 attachments only per mail");
                        return true;
                    }
                    bsecure.showAttachmentPopup();
                    return true;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {

        if (!exitFlag) {
            saveDraft();
        }

        layout = null;

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
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

            switch (requestType) {
                case 1:

                    if (results != null) {

                        exitFlag = true;

                        JSONObject object = new JSONObject(results.toString());

                        bsecure.showToast(object.optString("statusdescription"));

                        bsecure.addToStore("refreshdraft", "yes");
                        bsecure.draftMailListRefresh();
                        bsecure.onKeyDown(4, null);

                        object = null;
                    }

                    break;

                case 2:

                    if (results != null) {

                        JSONObject object = new JSONObject(results.toString());

                        draftid = object.optString("draftid");
                        bsecure.draftMailListRefresh();
                        object = null;
                    }

                    break;

                case 3:

                    parseToContacts((String) results, requestType);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),
                            android.R.layout.simple_expandable_list_item_1, list);
                    ed_toText.setAdapter(adapter);
                    ed_toText.setThreshold(1);

                    break;

                case 4:

                    if (results != null) {

                        JSONObject object = new JSONObject(results.toString());
                        if (object.optString("status").equalsIgnoreCase("0")) {
                            if (object.has("message_content")) {
                                JSONArray jsonArray = object.getJSONArray("message_content");
                                StringBuffer builder = new StringBuffer();
                                builder.append("\n");
                                builder.append("---------- Forwarded message----------");
                                //builder.append("<br/>");
                                builder.append("\n");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject innertMessage = jsonArray.getJSONObject(i);
                                    //builder.append("<br/>");

                                    // builder.append("<html><body><div><span style=\\\"font-family:HelveticaNeue-Bold;font-size:14px\\\"><b>From: </b>"+innertMessage.optString("from")+"<span style=\\\"font-family:MyriadPro-Regular;font-size:14px\\\"></span></span><br><span style=\\\"font-family:HelveticaNeue-Bold;font-size:14px\\\"><b>Date: </b>"+innertMessage.optString("datetime")+"<span style=\\\"font-family:MyriadPro-Regular;font-size:14px\\\"></span></span><br><span style=\\\"font-family:HelveticaNeue-Bold;font-size:14px\\\"><b>Subject: </b>"+innertMessage.optString("subject")+"<span style=\\\"font-family:MyriadPro-Regular;font-size:14px\\\"></span></span><br><span style=\\\"font-family:HelveticaNeue-Bold;font-size:14px\\\"><b>To: </b>"+ bsecure.getFromStore("email")+"</b><span style=\\\"font-family:MyriadPro-Regular;font-size:14px\\\"></span></span></div><br><p style=\\\"font-family:MyriadPro-Regular;font-size:15px\\\">"+innertMessage.optString("message")+"</p><br><br></body></html>");
                                    builder.append("\n");
                                    builder.append("From: " + innertMessage.optString("from"));
//                                    //builder.append("<br/>");
                                    builder.append("\n");
                                    builder.append("Date: " + innertMessage.optString("datetime"));
//                                    //builder.append("<br/>");
                                    builder.append("\n");
                                    builder.append("Subject: " + innertMessage.optString("subject"));
                                    builder.append("\n");
                                    builder.append("To: " + bsecure.getFromStore("email"));
                                    builder.append("\n");
                                    builder.append("\n");
                                    builder.append(innertMessage.optString("message"));
//                                    //builder.append("<br/>");
//                                    builder.append("\n");
                                }
                                builder.append("\n");

                                //Spanned Message=Html.fromHtml(builder.toString());
                                ed_comText.setText(Html.fromHtml(builder.toString()));
                            }

                            if (!object.optString("totalattachments").equalsIgnoreCase("0")) {
                                if (object.has("attachment_content")) {
                                    JSONArray attachmentsArray = object.getJSONArray("attachment_content");
                                    for (int i = 0; i < attachmentsArray.length(); i++) {
                                        JSONObject jsObj = attachmentsArray.getJSONObject(i);
                                        forwardaid = forwardaid + jsObj.optString("aid");
                                        addForwardAttachmentView(jsObj);
                                        if (i < attachmentsArray.length()) {
                                            forwardaid = forwardaid + ",";
                                        }
                                    }
                                }
                            }
                        }
                    }

                    break;

                case 5: // Keep it blank
                    break;

                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseToContacts(String results, int requestType) throws Exception {
        if (results != null && results.length() > 0) {
            JSONObject object = new JSONObject(results.toString());
            if (object.has("status") && object.optString("status").equalsIgnoreCase("0")) {
                JSONArray array = object.getJSONArray("emails_det");
                if (array != null && array.length() > 0) {
                    list = new ArrayList<String>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jObject = array.getJSONObject(i);
                        String useremails = jObject.optString("useremails");
                        useremails = useremails.trim();
                        if (useremails.length() > 0) {
                            list.add(useremails);

                        }
                    }

                }
            }

        }
    }

    @Override
    public void onError(String errorCode, int requestType) {

        switch (requestType) {
            case 1:
                bsecure.showToast(errorCode);
                Utils.dismissProgress();
                break;

            case 2:
                break;
            case 3:
                break;

            default:
                break;
        }
    }

    @Override
    public void onFragmentChildClick(View view) {
        super.onFragmentChildClick(view);
    }
    public void refresh() {
        isRefresh = true;
    }
    @Override
    public String getFragmentName() {
        return "Compose"; // getString(R.string.composemail);
    }

    public void showFilterValues(JSONObject object) {

        try {

            StringBuilder filterValues = new StringBuilder();
            filterValues.append("Read Once: ");

            if (object.getBoolean("readonly")) {
                filterValues.append("Yes");
                readonly = "Yes";
            } else {
                filterValues.append("No");
                readonly = "No";
            }

            filterValues.append(" | ");

            filterValues.append("Reply: ");

            if (object.getBoolean("reply")) {
                filterValues.append("Yes");
                reply = "0";
            } else {
                filterValues.append("No");
                reply = "1";
            }

            filterValues.append(" | ");

            filterValues.append("Auto Delete: ");

            if (object.getBoolean("autodelete")) {
                filterValues.append("Yes");
                autodelete = "Yes";
            } else {
                filterValues.append("No");
                autodelete = "No";
            }

            filterValues.append(" | ");

            if (object.optString("pin").length() > 0) {
                filterValues.append("Pin: Yes");
                pin = object.optString("pin");
                // filterValues.append(" | ");
            } else {
                filterValues.append("Pin: No");
                pin = object.optString("pin");
            }
            filterValues.append(" | ");

            filterValues.append("Time: " + object.optString("time"));
            time = object.optString("time");

            filterValues.append(" | ");

            filterValues.append("Valid Until: " + object.optString("validity"));
            validity = object.optString("validity");

            ((TextView) layout.findViewById(R.id.tv_filter)).setVisibility(View.VISIBLE);
            ((TextView) layout.findViewById(R.id.tv_filter)).setText(filterValues.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onCCClick() {
        if (flag) {
            layout.findViewById(R.id.ll_cc).setVisibility(View.GONE);
            flag = false;
        } else {
            layout.findViewById(R.id.ll_cc).setVisibility(View.VISIBLE);
            flag = true;
        }
    }

    public void onBCCClick() {
        if (flag) {
            layout.findViewById(R.id.ll_bcc).setVisibility(View.GONE);
            flag = false;
        } else {
            layout.findViewById(R.id.ll_bcc).setVisibility(View.VISIBLE);
            flag = true;
        }
    }

    private void sendMail() {

        try {

            if (requestItems != null && requestItems.size() > 0) {
                bsecure.showToast("File sending in progress please wait...");
                return;
            }

            String ed_toText_val = ed_toText.getText().toString().trim();
            if (ed_toText_val.length() == 0) {
                bsecure.showToast(R.string.peei);
                return;
            }

            boolean flag = false;

            if (ed_toText_val.contains(",")) {
                String[] temp = ed_toText_val.split(",");
                for (int i = 0; i < temp.length; i++) {
                    String em = temp[i].trim();
                    if (!emailValidation(em)) {
                        bsecure.showToast(em + " " + R.string.peavei);
                        bsecure.showToast(em + " " + R.string.peavei);
                        flag = true;
                        break;
                    }
                }

                if (flag)
                    return;

            } else if (!emailValidation(ed_toText_val)) {
                bsecure.showToast(R.string.peavei);
                return;
            }

            String ed_subText_val = ed_subText.getText().toString();
            // if (ed_subText_val.length() == 0) {
            // bsecure.showToast(R.string.pes);
            // return;
            // }
            if (ed_subText_val.length() == 0) {
                // bsecure.showToast(R.string.pes);
                ed_subText_val = "No subject";
                // return;
            }

            String ed_ccText_Vlaue = bet_cctext.getText().toString().trim();

            if (ed_ccText_Vlaue.length() > 0) {

                if (ed_ccText_Vlaue.contains(",")) {
                    String[] temp = ed_ccText_Vlaue.split(",");
                    for (int i = 0; i < temp.length; i++) {
                        String em = temp[i].trim();
                        if (!emailValidation(em)) {
                            bsecure.showToast(em + " " + R.string.peavccEi);
                            bsecure.showToast(em + " " + R.string.peavccEi);
                            flag = true;
                            break;
                        }
                    }

                    if (flag)
                        return;

                } else if (!emailValidation(ed_ccText_Vlaue)) {
                    bsecure.showToast(R.string.peavccEi);
                    return;
                }

            }

			/*
             * if (ed_ccText_Vlaue.length() > 0 &&
			 * !emailValidation(ed_ccText_Vlaue)) {
			 * bsecure.showToast(R.string.peavccEi); return; }
			 */

            String ed_bccText_Vlaue = bet_bcctext.getText().toString().trim();

            if (ed_bccText_Vlaue.length() > 0) {

                if (ed_bccText_Vlaue.contains(",")) {
                    String[] temp = ed_bccText_Vlaue.split(",");
                    for (int i = 0; i < temp.length; i++) {
                        String em = temp[i].trim();
                        if (!emailValidation(em)) {
                            bsecure.showToast(em + " " + R.string.peavbccEi);
                            bsecure.showToast(em + " " + R.string.peavbccEi);
                            flag = true;
                            break;
                        }
                    }

                    if (flag)
                        return;

                } else if (!emailValidation(ed_bccText_Vlaue)) {
                    bsecure.showToast(R.string.peavbccEi);
                    return;
                }

            }

			/*
			 * if (ed_bccText_Vlaue.length() > 0 &&
			 * !emailValidation(ed_bccText_Vlaue)) {
			 * bsecure.showToast(R.string.peavbccEi); return; }
			 */

            Utils.showProgress(getString(R.string.pwait), bsecure);

            JSONObject object = new JSONObject();

            if (rid.length() > 0)
                object.put("rid", rid + "");

            if (this.item != null) {
                object.put("composeid", this.item.getAttribValue("composeid"));
                object.put("memberid", this.item.getAttribValue("memberid"));
                if (msgItem != null) {
                    object.put("composeid", msgItem.getAttribValue("composeid"));
                }
            }

            if (forwardaid.length() > 0) {
                object.put("forwardaid", forwardaid + "");
            }

            object.put("email", bsecure.getFromStore("email"));

            object.put("draftid", draftid);

            object.put("to", ed_toText_val);

            object.put("cc", ed_ccText_Vlaue);
            object.put("bcc", ed_bccText_Vlaue);

            object.put("subject", ed_subText_val);
            object.put("message", ed_comText.getText().toString().trim());

            object.put("readonly", readonly);
            object.put("showtime", time);
            object.put("autodelete", autodelete);
            object.put("pin", pin);

            if (validity.equalsIgnoreCase("Unlimited")) {
                validity = "01-01-1970";
            }

            object.put("validuntil", validity);
            object.put("reply", reply);

            object.put("attachments", attachmentArray);

            String url = AppSettings.getInstance(bsecure).getPropertyValue("compose_mail");

            if (button != null && button.matches("Recompose")) {
                url = AppSettings.getInstance(bsecure).getPropertyValue("recompose");
            }
            if (button != null && button.matches("Subcompose")) {
                url = AppSettings.getInstance(bsecure).getPropertyValue("subcompose");
            }

            // Log.e("-=-=-=-=-=-=-", object.toString() + "");
            // Log.e("-:-:-:-:-:-:-", url + "");

            HTTPostJson post = new HTTPostJson(this, bsecure, object.toString(), 1);
            post.setContentType("application/json");
            post.execute(url, "");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean emailValidation(String email) {

        if (email == null || email.length() == 0 || email.indexOf("@") == -1 || email.indexOf(" ") != -1) {
            return false;
        }
        int emailLenght = email.length();
        int atPosition = email.indexOf("@");

        String beforeAt = email.substring(0, atPosition);
        String afterAt = email.substring(atPosition + 1, emailLenght);

        if (beforeAt.length() == 0 || afterAt.length() == 0) {
            return false;
        }
        if (email.charAt(atPosition - 1) == '.') {
            return false;
        }
        if (email.charAt(atPosition + 1) == '.') {
            return false;
        }
        if (afterAt.indexOf(".") == -1) {
            return false;
        }
        char dotCh = 0;
        for (int i = 0; i < afterAt.length(); i++) {
            char ch = afterAt.charAt(i);
            if ((ch == 0x2e) && (ch == dotCh)) {
                return false;
            }
            dotCh = ch;
        }
        if (afterAt.indexOf("@") != -1) {
            return false;
        }
        int ind = 0;
        do {
            int newInd = afterAt.indexOf(".", ind + 1);

            if (newInd == ind || newInd == -1) {
                String prefix = afterAt.substring(ind + 1);
                if (prefix.length() > 1 && prefix.length() < 6) {
                    break;
                } else {
                    return false;
                }
            } else {
                ind = newInd;
            }
        } while (true);
        dotCh = 0;
        for (int i = 0; i < beforeAt.length(); i++) {
            char ch = beforeAt.charAt(i);
            if (!((ch >= 0x30 && ch <= 0x39) || (ch >= 0x41 && ch <= 0x5a) || (ch >= 0x61 && ch <= 0x7a) || (ch == 0x2e)
                    || (ch == 0x2d) || (ch == 0x5f))) {
                return false;
            }
            if ((ch == 0x2e) && (ch == dotCh)) {
                return false;
            }
            dotCh = ch;
        }
        return true;
    }

    public void addAttachmentView(Item fullItem, long attachSize) {

        attachmentSize = attachmentSize + attachSize;

        if (attachmentSize > 100000000) {
            bsecure.showToast(R.string.aasblt);
            attachmentSize = attachmentSize - attachSize;
            return;
        }

        attachmentCounter++;

        /*layout.findViewById(R.id.tv_addattachment).setVisibility(View.VISIBLE);

        if (attachmentCounter == 10) {
            layout.findViewById(R.id.tv_addattachment).setVisibility(View.GONE);
        }*/

        viewId = Integer.parseInt(getCustomSystemTime());

        view = View.inflate(bsecure, R.layout.template_attachments_layout, null);
        view.setId(viewId);

        view.findViewById(R.id.tv_attachmentAction).setOnClickListener(bsecure);
        view.findViewById(R.id.tv_attachmentAction).setTag(viewId);

        ((ProgressControl) view.findViewById(R.id.download_progress)).setText(fullItem.getAttribute("displayname"));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 10);

        String attachTime = TimeUtils.getDeviceDateTime("yyyyMMdd_hhmmss");

        String displayname = fullItem.getAttribute("displayname");
        if (displayname.contains(".")) {

            String[] temp = displayname.split("\\.");
            displayname = temp[0] + "_" + attachTime + "." + temp[1];

        } else {

            displayname = displayname + "_" + attachTime;

        }

        fullItem.setAttribute("attachname", displayname);
        view.setTag(fullItem);

        attachmentLayout.addView(view, params);

        startUploadingFile(fullItem, viewId);

        ((android.support.v4.widget.NestedScrollView) categories_layout).smoothScrollTo(0, ((android.support.v4.widget.NestedScrollView) categories_layout).getBottom() + 20);
    }

    public void addDraftAttachmentView(Item item) {

        try {

            String draftattachment_details = item.getAttribute("draftattachment_details");

            if (draftattachment_details.contains("=,")) {
                draftattachment_details = draftattachment_details.replaceAll("=,", "=-1,");
            }

            draftattachment_details = draftattachment_details.replaceAll("=", ":");

            if (draftattachment_details.contains("View & Download")
                    || draftattachment_details.contains("view & download")
                    || draftattachment_details.contains("view &amp; download")) {
                draftattachment_details = draftattachment_details.replaceAll("View & Download", "\"View & Download\"");
                draftattachment_details = draftattachment_details.replaceAll("view & download", "\"view & download\"");
                draftattachment_details = draftattachment_details.replaceAll("view &amp; download",
                        "\"view &amp; download\"");
            }

            if (draftattachment_details.contains("View Only") || draftattachment_details.contains("view only")) {
                draftattachment_details = draftattachment_details.replaceAll("View Only", "\"View Only\"");
                draftattachment_details = draftattachment_details.replaceAll("view & download", "\"view only\"");

            }

            if (draftattachment_details.length() > 0) {
                JSONArray array = new JSONArray(draftattachment_details);

                if (array.length() > 0) {
                    //layout.findViewById(R.id.tv_addattachment).setVisibility(View.VISIBLE);

                    for (int i = 0; i < array.length(); i++) {

                        Item tagItem = new Item("");

                        JSONObject jsonObject = array.getJSONObject(i);

                        String attachSize = jsonObject.getString("filesize").trim();
                        tagItem.setAttribute("size", jsonObject.getString("filesize").trim());
                        if (item.getAttribute("drafts_values").equalsIgnoreCase("yes")) {
                            tagItem.setAttribute("drafts_values", "yes");
                        }
                        if (attachSize.length() > 0) {
                            attachmentSize = attachmentSize + Long.parseLong(attachSize);
                        }
                        attachmentCounter++;

                        viewId = Integer.parseInt(getCustomSystemTime());

                        view = View.inflate(bsecure, R.layout.template_attachments_layout, null);
                        view.setId(viewId);
                        view.setTag(tagItem);

                        view.findViewById(R.id.tv_attachmentAction).setOnClickListener(bsecure);
                        view.findViewById(R.id.tv_attachmentAction).setTag(viewId);

                        ((ProgressControl) view.findViewById(R.id.download_progress))
                                .setText(jsonObject.optString("attachmentname"));
                        ((ProgressControl) view.findViewById(R.id.download_progress)).hideProgressBar();

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                                LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, 0, 0, 10);

                        JSONObject jsonobject = new JSONObject();
                        jsonobject.put("id", viewId + "");
                        jsonobject.put("daid", jsonObject.optString("daid") + "");

                        jsonobject.put("level", jsonObject.optString("attachmentlevel") + "");
                        jsonobject.put("attachname", jsonObject.optString("attachmentorigname") + "");
                        jsonobject.put("attachorgname", jsonObject.optString("attachmentname") + "");

                        attachmentArray.put(jsonobject);

                        attachmentLayout.addView(view, params);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addForwardAttachmentView(JSONObject jsonObject) {

        //layout.findViewById(R.id.tv_addattachment).setVisibility(View.VISIBLE);

        int viewId = Integer.parseInt(getCustomSystemTime());

        Log.e("forward mail ", viewId + " " + jsonObject);

        View view = View.inflate(bsecure, R.layout.template_forward_attachments_layout, null);
        view.setId(viewId);
        view.setTag(jsonObject.optString("aid") + "");

        ((TextView) view.findViewById(R.id.tv_forwardattachmenttitle)).setText(jsonObject.optString("attachmentname"));

        view.findViewById(R.id.tv_forwardattachmentAction).setOnClickListener(bsecure);
        view.findViewById(R.id.tv_forwardattachmentAction).setTag(viewId);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 10);

        ll_forward_attachments_layout.addView(view, params);

        ((android.support.v4.widget.NestedScrollView) categories_layout).smoothScrollTo(0, ((android.support.v4.widget.NestedScrollView) categories_layout).getBottom() + 20);
    }

    public void removeForwardAttachment(int id) {

        try {

            Log.e("forward mail remove", id + "");

            View view = ll_forward_attachments_layout.findViewById(id);
            if (view != null) {
                ll_forward_attachments_layout.removeView(ll_forward_attachments_layout.findViewById(id));
                ll_forward_attachments_layout.invalidate();
                ll_forward_attachments_layout.requestLayout();

                forwardaid = forwardaid.replace(view.getTag() + ",", "");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void removeAttachment(int id) {

        try {

            FileUploader uploader = (FileUploader) requestItems.remove(id);
            if (uploader != null)
                uploader.cancelrequest();
            uploader = null;

            Item item = (Item) attachmentLayout.findViewById(id).getTag();
            long fileSize = 0;
            fileSize = Long.parseLong(item.getAttribute("size"));

            attachmentSize = attachmentSize - fileSize;

            attachmentLayout.removeView(attachmentLayout.findViewById(id));
            attachmentLayout.invalidate();
            attachmentLayout.requestLayout();
            attachmentCounter--;

            if (attachmentCounter < 10) {
                //layout.findViewById(R.id.tv_addattachment).setVisibility(View.VISIBLE);
            }

            for (int i = 0; i < attachmentArray.length(); i++) {
                JSONObject object = (JSONObject) attachmentArray.get(i);
                if (object.optString("id").equalsIgnoreCase(id + "")) {

                    JSONObject deleteJson = attachmentArray.getJSONObject(i);

                    // String link =
                    // bsecure.getPropertyValue("deletedraftattachment");
                    if (item.getAttribute("drafts_values").equalsIgnoreCase("yes")) {
                        String link = bsecure.getPropertyValue("deletedraftattachment");
                        // link = link.replace("(ATTNAME)",
                        // deleteJson.optString("daid"));
                        JSONObject jobject = new JSONObject();
                        jobject.put("daid", deleteJson.optString("daid"));

                        HTTPostJson post = new HTTPostJson(this, getActivity(), jobject.toString(), 5);
                        post.setContentType("application/json");
                        post.execute(link, "");
                    }
                    // attachmentArray.remove(i);
                    removeObject(i);
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getFragmentActionBarColor() {
        return R.color.green;
    }

    private void saveDraft() {

        try {

            JSONObject object = new JSONObject();

            if (rid.length() > 0)
                object.put("rid", rid + "");

            if (this.item != null) {
                object.put("composeid", this.item.getAttribValue("composeid"));
                object.put("memberid", this.item.getAttribValue("memberid"));

            }

            object.put("email", bsecure.getFromStore("email"));
            object.put("draftid", draftid);
            object.put("to", ed_toText.getText().toString().trim());

            object.put("cc", bet_cctext.getText().toString().trim());
            object.put("bcc", bet_bcctext.getText().toString().trim());

            object.put("subject", ed_subText.getText().toString());
            object.put("message", ed_comText.getText().toString().trim());

            object.put("readonly", readonly);
            object.put("showtime", time);
            object.put("autodelete", autodelete);
            object.put("pin", pin);

            if (validity.equalsIgnoreCase("Unlimited")) {
                validity = "01-01-1970";
            }

            object.put("validuntil", validity);
            object.put("reply", reply);

            object.put("attachments", attachmentArray);

            String url = AppSettings.getInstance(bsecure).getPropertyValue("savedrafts");

            HTTPostJson post = new HTTPostJson(this, bsecure, object.toString(), 2);
            post.setContentType("application/json");
            post.execute(url, "");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    OnFocusChangeListener onFocusChange = new OnFocusChangeListener() {

        @Override
        public void onFocusChange(View view, boolean hasFocus) {

            switch (view.getId()) {

                case R.id.ed_toText:

                    if (!hasFocus)
                        saveDraft();


                    break;

                case R.id.bet_cctext:

                    if (!hasFocus)
                        saveDraft();

                    break;

                case R.id.bet_bcctext:

                    if (!hasFocus)
                        saveDraft();


                    break;

                case R.id.ed_subText:

                    if (!hasFocus)
                        saveDraft();

                    break;

                case R.id.ed_comText:

                    break;

                default:
                    break;
            }

        }
    };

    private void startUploadingFile(Item item, int requestId) {

        if (item == null) {
            bsecure.showToast(R.string.syhnsai);
            return;
        }

        String url = AppSettings.getInstance(bsecure).getPropertyValue("upload_file");

        FileUploader uploader = new FileUploader(bsecure, this);
        uploader.setFileName(item.getAttribute("displayname"), item.getAttribute("attachname"));
        uploader.userRequest("", requestId, url, item.getAttribute("data"));

        requestItems.put(requestId, uploader);

        // uploadingIds.add(requestId);
    }

    @Override
    public void onStateChange(int what, int arg1, int arg2, Object obj, int requestId) {

        try {

            switch (what) {

                case -1: // failed

                    View fview = attachmentLayout.findViewById(requestId);
                    Item fitem = (Item) fview.getTag();

                    removeAttachment(requestId);

                    bsecure.showToast("Attachment " + fitem.getAttribute("displayname") + " failed. Please try again. ! "
                            + obj.toString());

                    fitem = null;
                    fview = null;

                    // uploadingIds.remove(requestId);

                    break;

                case 1: // progressBar

                    View pview = attachmentLayout.findViewById(requestId);

                    ((ProgressControl) pview.findViewById(R.id.download_progress)).updateProgressState((Long[]) obj);// setText(fullItem.getAttribute("displayname"));

                    break;

                case 0: // success

                    View sview = attachmentLayout.findViewById(requestId);
                    Item sitem = (Item) sview.getTag();

                    JSONObject jsonobject = new JSONObject();
                    jsonobject.put("id", requestId + "");
                    jsonobject.put("level", sitem.getAttribute("attachmentlevel"));
                    jsonobject.put("attachname", sitem.getAttribute("attachname"));
                    jsonobject.put("attachorgname", sitem.getAttribute("displayname"));

                    attachmentArray.put(jsonobject);

                    ((ProgressControl) sview.findViewById(R.id.download_progress)).hideProgressBar();

                    ((ProgressControl) sview.findViewById(R.id.download_progress))
                            .setText(sitem.getAttribute("displayname"));

                    // removeAttachment(requestId);
                    // uploadingIds.remove(requestId);

                    FileUploader uploader = requestItems.remove(requestId);
                    // uploader.cancelrequest();
                    uploader = null;

                    break;

                default:
                    break;
            }

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    private String getCustomSystemTime() {
        DateFormat dateFormat = new SimpleDateFormat("hhmmssSSS");
        java.util.Date date = new java.util.Date();
        return dateFormat.format(date);
    }

    public int getAttachmentCount() {
        return attachmentCounter;
    }

    private void removeObject(int index) {

        try {

            JSONArray temp = new JSONArray();

            for (int i = 0; i < attachmentArray.length(); i++) {
                JSONObject jsonObj = attachmentArray.getJSONObject(i);
                if (i != index) {
                    temp.put(index, jsonObj);
                }
            }

            attachmentArray = null;
            attachmentArray = temp;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject getFilterVlaues() {

        JSONObject object = new JSONObject();

        try {

            object.put("readonly", readonly.equalsIgnoreCase("yes") ? true : false);
            object.put("reply", reply.equalsIgnoreCase("0") ? true : false);
            object.put("autodelete", autodelete.equalsIgnoreCase("yes") ? true : false);

            object.put("pin", pin.length() > 1 ? pin : "");

            object.put("time", time + "");

            object.put("validity", validity.equalsIgnoreCase("01-01-1970") ? "Unlimited" : validity);

            return object;

        } catch (Exception e) {
            return object;
        }

    }

    private void getSize() {
        categories_layout.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            categories_layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else
                            categories_layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                        int height = categories_layout.getHeight();
                        // Log.e(":::::------", height + "");

                        int rl_tobodyHeight = layout.findViewById(R.id.rl_tobody).getHeight();

                        height = height - (rl_tobodyHeight * 3);
                        // Log.e(":::::------", height + "");

                        AppPreferences.getInstance(bsecure).addToStoreInteger("height", height);

                        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, height);
                        //ed_comText.setLayoutParams(params);

                        // ed_comText.getLayoutParams().height = height;

                    }
                });
    }

    public InputFilter EMOJI_FILTER = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int index = start; index < end; index++) {

                int type = Character.getType(source.charAt(index));

                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL
                        || type == Character.OTHER_PUNCTUATION) {
                    //bsecure.showToast(R.string.emoji);
                    return "";
                }
            }
            return null;
        }
    };

    private class MyOnBubbleClickListener implements OnBubbleClickListener {

        @Override
        public void onBubbleClick(View view, Hyperlink linkSpec) {
            Log.v("compose mail", "onBubbleClick");
            // we can do whatever You want with this click event. In this case
            // we simply remove the bubble.
            //bet_cctext.removeBubble(linkSpec);

        }

    }

    private final TextWatcher phoneNumberWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {

            if (s.toString().endsWith(" ")) {

                String txt = s.toString();
                //bet_cctext.addBubbleText(txt);
                // set text to textView, including one that is wrapped in bubble
                bet_cctext.removeTextChangedListener(phoneNumberWatcher);
                bet_cctext.setText(bet_cctext.getText() + txt + " ");
                bet_cctext.addTextChangedListener(phoneNumberWatcher);


                bet_bcctext.removeTextChangedListener(phoneNumberWatcher);
                bet_bcctext.setText(bet_bcctext.getText() + txt + " ");
                bet_bcctext.addTextChangedListener(phoneNumberWatcher);


                ed_toText.removeTextChangedListener(phoneNumberWatcher);
                ed_toText.setText(ed_toText.getText() + txt + " ");
                ed_toText.addTextChangedListener(phoneNumberWatcher);
            }
        }
    };
}

package com.mail.sendsafe;

import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mail.sendsafe.account.AddContacts;
import com.mail.sendsafe.account.ViewContacts;
import com.mail.sendsafe.account.View_Wallet;
import com.mail.sendsafe.callbacks.IItemHandler;
import com.mail.sendsafe.common.AppPreferences;
import com.mail.sendsafe.common.AppSettings;
import com.mail.sendsafe.common.Constants;
import com.mail.sendsafe.common.Item;
import com.mail.sendsafe.dialogfragments.AttachmentDialog;
import com.mail.sendsafe.dialogfragments.BlockUnblockDialog;
import com.mail.sendsafe.dialogfragments.BlockUserDialog;
import com.mail.sendsafe.dialogfragments.CreateFolderDialog;
import com.mail.sendsafe.dialogfragments.DownloadProgressDialog;
import com.mail.sendsafe.dialogfragments.NotificationDialog;
import com.mail.sendsafe.dialogfragments.ReplyDialog;
import com.mail.sendsafe.dialogfragments.ReportDialog;
import com.mail.sendsafe.dialogfragments.SubscriptionDialog;
import com.mail.sendsafe.fragments.BsecureFilters;
import com.mail.sendsafe.fragments.ComposeMail;
import com.mail.sendsafe.fragments.DraftMailsListing;
import com.mail.sendsafe.fragments.FolderMessageList;
import com.mail.sendsafe.fragments.FoldersFragments;
import com.mail.sendsafe.fragments.MailViews;
import com.mail.sendsafe.fragments.MailsListing;
import com.mail.sendsafe.fragments.OlderGrop;
import com.mail.sendsafe.fragments.ParentFragment;
import com.mail.sendsafe.fragments.SentMailViews;
import com.mail.sendsafe.fragments.SentMailsListing;
import com.mail.sendsafe.fragments.SentMailsOlders;
import com.mail.sendsafe.fragments.Settings;
import com.mail.sendsafe.fragments.TrashMailsListing;
import com.mail.sendsafe.imageloader.ImageLoader;
import com.mail.sendsafe.tasks.HTTPostJson;
import com.mail.sendsafe.utils.Utils;
import com.mail.sendsafe.web.AboutUs;
import com.mail.sendsafe.web.PdfView;
import com.mail.sendsafe.web.UserGuide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Stack;
import java.util.Vector;

public class Bsecure extends AppCompatActivity
        implements /* NavigationDrawerFragment.NavigationDrawerCallbacks, */ OnClickListener, IItemHandler {

    private SubscriptionDialog subscriptionDialog = null;

    private FragmentManager manager = null;

    private MailsListing mailListing = null;

    private SentMailsListing sentMailListing = null;

    private TrashMailsListing trashMailListing = null;

    private DraftMailsListing draftMailListing = null;

    private MailViews mailViews = null;

    private OlderGrop olders = null;

    private SentMailsOlders sentolders = null;

    private SentMailViews sentMailViews = null;

    private Settings settings = null;

    private ComposeMail composeMail = null;

    private BsecureFilters filters = null;

    private Properties properties = null;

    private String cid = "0", foldername, fid;

    private DownloadProgressDialog dpDialog = null;

    private ReplyDialog rpyDialog = null;

    private ReportDialog repDialog = null;

    private AttachmentDialog attachDialog = null;

    private NotificationDialog notifyDialog = null;

    private BlockUnblockDialog bunbDialog = null;

    private BlockUserDialog blockUser = null;

    private Stack<ParentFragment> fragStack = null;

    private DrawerLayout drawerLayout = null;

    private Toolbar toolbar;

    private ActionBar actionBar = null;

    private ActionBarDrawerToggle mDrawerToggle;

    private NavigationView navigationView = null;

    private ImageView iv_profilePic = null;

    private ImageLoader imageLoader = null;

    private View drawerHeader = null;

    public boolean commitFlag = true;

    public String notify;

    private int showtime = 0;

    private ArrayList<String> list = null;

    private CreateFolderDialog folderDialog = null;

    private FoldersFragments folderFrgment = null;

    private FolderMessageList folderFrgmentlist = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bsecure);

        imageLoader = new ImageLoader(this, false);

        loadProperties();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        fragStack = new Stack<ParentFragment>();

        getSupportActionBar().getThemedContext();

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,

                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //composeMail = null;
                supportInvalidateOptionsMenu();
            }

        };

        drawerLayout.setDrawerListener(mDrawerToggle);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        actionBar.setTitle(R.string.inbox);

        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerToggle.setDrawerIndicatorEnabled(true);

        manager = getSupportFragmentManager();

        if (savedInstanceState != null) {
            temp();
        }

        mailListing = new MailsListing();

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.container, mailListing, "mailListing");

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
        fragStack.push(mailListing);

        navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                onNavigationDrawerItemSelected(menuItem.getItemId());
                return true;
            }
        });

        drawerHeader = navigationView.inflateHeaderView(R.layout.nav_header);

        iv_profilePic = (ImageView) drawerHeader.findViewById(R.id.iv_profilePic);

        ((TextView) drawerHeader.findViewById(R.id.tv_username)).setText(getFromStore("username"));

        ((TextView) drawerHeader.findViewById(R.id.tv_useremail)).setText(getFromStore("email"));

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (filters != null && filters.isVisible()) {
            MenuItem item = menu.findItem(R.id.cm_send);
            if (item != null)
                item.setVisible(false);
            MenuItem item_attach = menu.findItem(R.id.cm_attachments);
            if (item_attach != null)
                item_attach.setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    public void onNavigationDrawerItemSelected(int menuId) {

		/*
         * if (position == R.id.m_logout) { addToStore("fromExit", "exit");
		 * return; }
		 */

        int count = fragStack.size();
        while (count > 1) {

            ParentFragment pf = fragStack.remove(count - 1);

            FragmentTransaction trans = manager.beginTransaction();
            trans.remove(pf);
            trans.commit();

            sentMailListing = null;
            draftMailListing = null;
            trashMailListing = null;
            mailViews = null;
            sentMailViews = null;
            folderFrgment = null;
            settings = null;

            count--;
        }

        switch (menuId) {

            case R.id.m_inbox: // Inbox

                swiftFragments(mailListing, "mailListing");

                break;

            case R.id.m_sent: // Sent

                if (sentMailListing == null)
                    sentMailListing = new SentMailsListing();

                swiftFragments(sentMailListing, "sentMailListing");

                break;

            case R.id.m_drafts: // Drafts

                if (draftMailListing == null)
                    draftMailListing = new DraftMailsListing();

                swiftFragments(draftMailListing, "draftMailListing");

                break;

            case R.id.m_trash: // Trash

                if (trashMailListing == null)
                    trashMailListing = new TrashMailsListing();

                swiftFragments(trashMailListing, "trashMailListing");

                break;

            case R.id.m_settings: // Settings

                if (settings == null)
                    settings = new Settings();

                swiftFragments(settings, "settings");

                break;
            case R.id.m_folder: // FoldersFragments

                if (folderFrgment == null)
                    folderFrgment = new FoldersFragments();

                swiftFragments(folderFrgment, "folderFrgment");

                break;
            case R.id.m_logout: // Logout

                // addToStore("email", getFromStore("email"));
                AppPreferences.getInstance(this).addToStore("authid",
                    /*
                     * AppPreferences.getInstance(this).getFromStore("authid")
					 */"");
                Intent i = new Intent(Bsecure.this, Login.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(i);
                Bsecure.this.finish();
                break;

            case 7:
                break;

            default:
                break;
        }

    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
         * if (!mNavigationDrawerFragment.isDrawerOpen()) { restoreActionBar();
		 * return true; }
		 */
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawerLayout != null)
                    drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * loadProperties - Loads properties file from raw folder.
     */
    private void loadProperties() {
        try {
            InputStream rawResource = getResources().openRawResource(R.raw.settings);
            properties = new Properties();
            properties.load(rawResource);
            rawResource.close();
            rawResource = null;
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Properties getProperties() {
        return properties;
    }

    public String getPropertyValue(String key) {
        return properties.getProperty(key);
    }

    public void showToast(String value) {
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public void addToStore(String key, String value) {
        AppPreferences.getInstance(this).addToStore(key, value);
    }

    public String getFromStore(String key) {
        return AppPreferences.getInstance(this).getFromStore(key);
    }

    private void launchActivity(Class<?> cls, String filepath, String mimeType) {
        Intent mainIntent = new Intent(Bsecure.this, cls);
        mainIntent.putExtra("filepath", filepath + "");
        mainIntent.putExtra("mimeType", mimeType + "");
        mainIntent.putExtra("showtime", showtime);
        mainIntent.putExtra("to", AppPreferences.getInstance(this).getFromStore("from"));
        startActivity(mainIntent);
    }

    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.tv_folder_manage:

                Intent mnfoldr = new Intent(this, ManageFolders.class);
                startActivity(mnfoldr);
                break;

            case R.id.tv_folder:

                if (folderDialog == null)
                    folderDialog = new CreateFolderDialog();

                Bundle conatcts = new Bundle();
                folderDialog.setArguments(conatcts);
                folderDialog.show(Bsecure.this.getSupportFragmentManager(), "dialog");
                break;
            case R.id.fl_create:
                FolderDialog();
                break;
            case R.id.fl_cancel:
                closeDialogs();
                break;

            case R.id.tv_upgradenow:

                if (subscriptionDialog != null)
                    subscriptionDialog.dismiss();
                subscriptionDialog = null;

                showCustomWebview();

                break;

            case R.id.tv_attachmentView:
            case R.id.tv_attachmentViewnDwn:
                try {
                    Item item = (Item) view.getTag();
                    if (item == null)
                        return;

                    if (view.getId() == R.id.tv_attachmentView) {
                        if (item.getAttribute("attachmentorigname").contains(".pdf")) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                String my_time = item.getAttribute("showtime");
                                if (!my_time.equalsIgnoreCase("Unlimited")) {
                                    showtime = Integer.parseInt(my_time);
                                }


                                String url = getPropertyValue("filedownload") + item.getAttribute("attachmentorigname");
                                if (item.getAttribute("library").equalsIgnoreCase("yes"))
                                    url = getPropertyValue("group_filedownload") + item.getAttribute("attachmentorigname");

                                launchActivity(PdfView.class, url, "application/pdf");

                                return;
                            }
                        }
                    }

                    String to = AppPreferences.getInstance(this).getFromStore("from");
                    launchDownloadDialog(item.getAttribute("attachmentorigname"), view.getId(), item.getAttribute("library"), to);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.bn_browse:
                try {
                    String tag = attachDialog.getSelectedValue();

                    if (tag.length() == 0) {
                        showToast(R.string.psal);
                        return;
                    }

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(intent, 2798);

                } catch (Exception e) {
                    e.printStackTrace();
                    showToast("Too much peform on Activity");
                }
                break;

            case R.id.bn_browseCancel:
                closeDialogs();
                break;

            case R.id.bn_bu_cancel:
                closeDialogs();
                break;

            case R.id.bn_bu_submit:
                makeBlockUnblockUser((Integer) view.getTag());
                break;

            case R.id.tv_blockusers:
                showBlockDialog(view.getId(), R.string.blockusers);
                break;

            case R.id.tv_unblockusers:
                showBlockUnDialog(view.getId(), R.string.unblockusers);
                break;

            case R.id.tv_rating:
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }
                break;

            case R.id.tv_shareapp:
                Intent in = new Intent(android.content.Intent.ACTION_SEND);
                in.setType("text/plain");
                in.putExtra(android.content.Intent.EXTRA_SUBJECT, "Sendsafe");
                in.putExtra(android.content.Intent.EXTRA_TEXT,
                        "To download Sendsafe email app, please click here https://play.google.com/store/apps/details?id=com.mail.sendsafe");
                this.startActivity(Intent.createChooser(in, "Share Via"));
                break;

            case R.id.tv_about:

                Intent aboutintent = new Intent(this, AboutUs.class);
                startActivity(aboutintent);
                break;

            case R.id.tvCommentSubmit/* submit_comment */:
                submitReply((Item) view.getTag());
                break;

            case R.id.bn_dg_cancel:
                closeDialogs();
                break;
            case R.id.tv_forwardattachmentAction:
                composeMail.removeForwardAttachment((Integer) view.getTag());
                break;

            case R.id.tv_attachmentAction:
                composeMail.removeAttachment((Integer) view.getTag());
                break;

//            case R.id.tv_addattachment:
//
//                showAttachmentPopup();
//
//                break;

            case R.id.fab_newmail:

                if (composeMail == null)
                    composeMail = new ComposeMail();

                swiftFragments(composeMail, "composeMail");

                break;

            case R.id.rv_row_lisiting_layout:
                try {
                    fragStack.peek().onFragmentChildClick(view);
                } catch (Exception e) {

                }
                break;
            case R.id.folder_layout:
                try {
                    fragStack.peek().onFragmentChildClick(view);
                } catch (Exception e) {

                }
                break;

            case R.id.tv_filterdone:

                JSONObject object = filters.getFilterVlaues();
                String pin = object.optString("pin");
                if (pin.length() != 0) {
                    if (pin.length() < 4) {
                        showToast(R.string.psbdl);
                        return;
                    }
                }

                if (composeMail != null) {
                    composeMail.showFilterValues(object);
                    onKeyDown(4, null);
                    filters = null;
                } else {
                    onKeyDown(4, null);
                    filters = null;
                }


                break;

            case R.id.tv_reply:

                Item itemp = (Item) view.getTag();
                Vector<Item> items = (Vector<Item>) itemp.get("mail_details");
                Item msgItem = items.get(0);
                String value = msgItem.getAttribute("button");
                if (value.equalsIgnoreCase("Recompose")) {

                    if (composeMail == null)
                        composeMail = new ComposeMail();

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("item", (Item) view.getTag());
                    composeMail.setArguments(bundle);

                    swiftFragments(composeMail, "composeMail");

                    return;
                }

                showReplyDialog(msgItem);

                break;

            case R.id.tv_resend:
                Item itemp1 = (Item) view.getTag();
                Vector<Item> items1 = (Vector<Item>) itemp1.get("mail_details");
                Item msgItem1 = items1.get(0);
                String value1 = msgItem1.getAttribute("composeid").trim();
                if (value1.length() > 0) {

                    if (composeMail == null)
                        composeMail = new ComposeMail();

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("item", (Item) view.getTag());
                    composeMail.setArguments(bundle);

                    swiftFragments(composeMail, "composeMail");

                    return;
                }
                showReplyDialog(itemp1);
                break;

//            case R.id.tv_ccText:
//                composeMail.onCCClick();
//                break;
//
//            case R.id.tv_bccText:
//                composeMail.onBCCClick();
//                break;
            case R.id.tv_change_pass:

                Intent changepas = new Intent(getApplicationContext(), ChangePassword.class);
                startActivity(changepas);

                break;
            case R.id.tv_profile:

                Intent profiler = new Intent(getApplicationContext(), Profile_Activity.class);
                startActivity(profiler);

                break;

            case R.id.tv_invitation:
                Intent invite = new Intent(getApplicationContext(), InvitePeople.class);
                startActivity(invite);
                break;
            case R.id.tv_wallet:
                Intent view_wallet = new Intent(this, View_Wallet.class);
                startActivity(view_wallet);
                break;
            case R.id.tv_view_payment:
                Intent Mpayment = new Intent(this, MPayment.class);
                startActivity(Mpayment);
                break;
            case R.id.tv_viewcontacts:
                Intent view_contact = new Intent(this, ViewContacts.class);
                startActivity(view_contact);
                break;
            case R.id.tv_guidelines:
                // UserguigeDisplypop();
                Intent user_guid = new Intent(this, UserGuide.class);
                startActivity(user_guid);
                break;

            case R.id.notify_sub:
                makeNotifyUser((Integer) view.getTag());
                break;
            case R.id.tv_notification:
                showNotification(view.getId(), R.string.notifcation_head);
                break;
//		case R.id.tv_Report:
//			Item itemer = (Item) view.getTag();
//			showReportDialog(itemer);
//			break;

            case R.id.report:
                submitReport((Item) view.getTag());
                break;

            case R.id.tv_addcontacts:
                Intent add_contact = new Intent(getApplicationContext(), AddContacts.class);
                startActivity(add_contact);
                break;
            case R.id.tv_version:
                String versionName = "";
                PackageManager manager = getPackageManager();
                try {
                    PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
                    versionName = info.versionName;
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
                showToast(versionName);

                break;
            case R.id.tv_contact:
                Intent fedback = new Intent(getApplicationContext(), ContactUs.class);
                startActivity(fedback);
                break;
            case R.id.tv_importcontacts:
                importContacts();
                break;

            default:
                break;
        }
    }

    private void importContacts() {
        final String link = AppSettings.getInstance(this).getPropertyValue("import_contacts");
        Utils.showProgress(getString(R.string.pwait), this);
        new Thread(new Runnable() {

            @Override
            public void run() {

                HTTPostJson post = new HTTPostJson(Bsecure.this, Bsecure.this,
                        Utils.getContacts(Bsecure.this).toString(), 4);
                post.enableMutipart(false);
                post.setContentType("application/json");
                post.execute(link, "");
            }
        }).start();
    }

    public void showReportDialog(Item item) {

        if (repDialog == null)
            repDialog = new ReportDialog();
        Bundle reports = new Bundle();
        reports.putSerializable("item", item);
        repDialog.setArguments(reports);
        repDialog.show(Bsecure.this.getSupportFragmentManager(), "dialog");

    }

    private void swiftFragments(ParentFragment frag, String tag) {

        FragmentTransaction trans = manager.beginTransaction();
        if (frag.isAdded() && frag.isVisible())
            return;
        else if (frag.isAdded() && frag.isHidden()) {

            trans.hide(fragStack.get(fragStack.size() - 1));
            trans.show(frag);

        } else if (!frag.isAdded()) {

            ParentFragment pf = fragStack.get(fragStack.size() - 1);
            trans.hide(pf);

            trans.add(R.id.container, frag, tag);
            trans.show(frag);
        }

        trans.commitAllowingStateLoss();
        trans = null;

        getSupportActionBar().setTitle(frag.getFragmentName());

		/*
         * getSupportActionBar().setBackgroundDrawable(new
		 * ColorDrawable(frag.getFragmentActionBarColor()));
		 * updateStatusBarColor(frag.getFragmentStatusBarColor());
		 */

        if (!(frag instanceof MailsListing))
            fragStack.push((ParentFragment) frag);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {

            // if (mNavigationDrawerFragment.isDrawerOpen()) {
            // mNavigationDrawerFragment.close();
            // return true;
            // }

            if (fragStack.size() > 1) {

                ParentFragment pf = fragStack.peek();

                if (pf.back() == true)
                    return true;

                fragStack.pop();

                FragmentTransaction trans = manager.beginTransaction();
                trans.remove(pf);

                if (pf instanceof ComposeMail)
                    composeMail = null;

                ParentFragment pf1 = fragStack.get(fragStack.size() - 1);
                trans.show(pf1);

                if (commitFlag)
                    trans.commit();
                else
                    trans.commitAllowingStateLoss();

                getSupportActionBar().setTitle(pf1.getFragmentName());

				/*
                 * getSupportActionBar().setBackgroundDrawable(new
				 * ColorDrawable(pf1.getFragmentActionBarColor()));
				 * updateStatusBarColor(pf1.getFragmentStatusBarColor());
				 */

                return true;
            }

            return super.onKeyDown(keyCode, event);

        }
        return super.onKeyDown(keyCode, event);
    }

    OnClickListener onclick = new OnClickListener() {

        @Override
        public void onClick(View view) {

            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);

            switch (view.getId()) {

                case R.id.home:
                    swiftFragments(mailListing, "mailListing");

                    break;

                default:
                    break;
            }

        }
    };

    @Override
    public void onFinish(Object results, int requestType) {

        try {

            switch (requestType) {

                case 1:

                    Utils.dismissProgress();

                    if (results != null) {
                        JSONObject object = new JSONObject(results.toString());

                        showToast(object.optString("statusdescription"));
                        onKeyDown(4, null);
                        object = null;
                    }

                    break;

                case 2:
                    parseBlockUnBlockResponse((String) results, requestType);
                    break;
                case 3:
                    parseNotifyResponse((String) results, requestType);
                    break;

                case 4:

                    parseImportResponse((String) results, requestType);
                    Utils.dismissProgress();

                    break;

                case 5:
                    Utils.dismissProgress();
                    parseReportResponse((String) results, requestType);
                    break;
                case 6: // user profile

                    if (results == null) {

                        ((TextView) drawerHeader.findViewById(R.id.tv_username)).setText(getFromStore("username"));

                        ((TextView) drawerHeader.findViewById(R.id.tv_useremail)).setText(getFromStore("email"));

                        return;
                    }

                    try {

                        JSONObject jsonObject = new JSONObject(results.toString());

                        String imgURL = jsonObject.optString("avatar");
                        String name = jsonObject.optString("firstname");
                        name = name + " " + jsonObject.optString("lastname");
                        imageLoader.DisplayImage(imgURL, iv_profilePic, 5);
//                        int rotate=AppPreferences.getInstance(this).getFromStoreInteger("rotate");
//                        if (rotate== 270) {
//                            iv_profilePic.setRotation(rotate);
//                            imageLoader.DisplayImage(imgURL, iv_profilePic, 5);
//                        }
//                        else if (rotate== 180) {
//                            iv_profilePic.setRotation(rotate);
//                            imageLoader.DisplayImage(imgURL, iv_profilePic, 5);
//                        }
//                        else if (rotate== 90) {
//                            iv_profilePic.setRotation(rotate);
//                            imageLoader.DisplayImage(imgURL, iv_profilePic, 5);
//                        }
//                        else {
//                            imageLoader.DisplayImage(imgURL, iv_profilePic, 5);
//                        }


                        AppPreferences.getInstance(this).addToStore("country", jsonObject.optString("country_name"));
                        ((TextView) drawerHeader.findViewById(R.id.tv_username)).setText(name);

                        ((TextView) drawerHeader.findViewById(R.id.tv_useremail)).setText(getFromStore("email"));

                    } catch (Exception e) {

                        ((TextView) drawerHeader.findViewById(R.id.tv_username)).setText(getFromStore("username"));

                        ((TextView) drawerHeader.findViewById(R.id.tv_useremail)).setText(getFromStore("email"));

                    }

                    break;
                case 7:
                    Utils.dismissProgress();
                    if (results != null) {
                        JSONObject jsonObject = new JSONObject(results + "");
                        if (jsonObject.has("status")) {
                            if (jsonObject.optString("status").equalsIgnoreCase("0")) {
                                showFilterFragment(false);
                                return;
                            } else if (jsonObject.optString("status").equalsIgnoreCase("1")) {

                                if (subscriptionDialog == null)
                                    subscriptionDialog = new SubscriptionDialog();
                                subscriptionDialog.show(Bsecure.this.getSupportFragmentManager(), "dialog");

                            }
                        }

                        // showToast(jsonObject.optString("statusdescription"));

                    }
                    break;
                case 8:
                    if (results != null) {
                        JSONObject jsonObject = new JSONObject(results.toString());
                        if (jsonObject.optString("status").equalsIgnoreCase("0")) {
                            notify = jsonObject.optString("notification");
                            if (notify.equalsIgnoreCase("Yes") || notify.equalsIgnoreCase("Yes1")) {
                                ((RadioButton) notifyDialog.getView().findViewById(R.id.notify_yes)).setChecked(true);
                            }
                            if (notify.equalsIgnoreCase("No")) {
                                ((RadioButton) notifyDialog.getView().findViewById(R.id.notify_no)).setChecked(true);
                            }
                            addToStore("notify", notify);

                        }
                    }
                    break;
                case 9:
                    parseToContacts((String) results, requestType);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                            android.R.layout.select_dialog_singlechoice, list);
                    ((AutoCompleteTextView) bunbDialog.getView().findViewById(R.id.cmttxt)).setAdapter(adapter);
                    ((AutoCompleteTextView) bunbDialog.getView().findViewById(R.id.cmttxt)).setThreshold(1);
                    break;
                case 10:
                    Utils.dismissProgress();
                    if (results != null) {
                        JSONObject object = new JSONObject(results.toString());
                        if (object.optString("status").equalsIgnoreCase("0")) {
                            showToast(object.optString("statusdescription"));
                            closeDialogs();
                        } else if (object.optString("status").equalsIgnoreCase("1")) {
                            showToast(object.optString("statusdescription"));
                            closeDialogs();
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
        showToast(errorCode);

        switch (requestType) {

            case 1:
                Utils.dismissProgress();
                break;

            case 2:
                break;

            default:
                break;
        }
    }

    public FragmentManager getFragManager() {
        return manager;
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {

            case 1001:
                break;

            case 2002:

                if (resultCode == RESULT_CANCELED) {
                    onKeyDown(4, null);
                    return;
                }

                if (data.getStringExtra("action").equalsIgnoreCase("0")) {

                    showFilterFragment(false);

                } else if (data.getStringExtra("action").equalsIgnoreCase("1")) {

                }

                break;

            case 2001:

                if (resultCode == RESULT_CANCELED) {
                    onKeyDown(4, null);
                    return;
                }

                if (resultCode == RESULT_OK) {
                    if (mailViews != null && mailViews.isVisible()) {
                        mailViews.startShowTimeTask();
                    }
                    return;
                }
                if (resultCode == RESULT_OK) {
                    if (sentMailViews != null && sentMailViews.isVisible()) {
                        sentMailViews.startShowTimeTask();
                    }
                    return;
                }

                break;

            case 2798:
                if (resultCode == RESULT_OK) {

                    if (data == null) {
                        showToast(R.string.fnf);
                        return;
                    }

                    validateSelectedFile(processFileMetadata(data));


                }
                break;

        }
    }

    public void launchMailPin(Intent intent) {
        startActivityForResult(intent, 2001);
    }

    protected String getContactInfo(Intent intent) {

        String number = "";

        if (intent == null)
            return number;

        Cursor cursor = getContentResolver().query(intent.getData(),
                new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.HAS_PHONE_NUMBER}, null, null,
                null);

        while (cursor != null && cursor.moveToNext()) {
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

            String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if (hasPhone.equalsIgnoreCase("1"))
                hasPhone = "true";
            else
                hasPhone = "false";

            if (Boolean.parseBoolean(hasPhone)) {
                Cursor phones = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

                while (phones.moveToNext()) {
                    number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                phones.close();
            }

        }
        cursor.close();
        return number;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        addToStore("inactivity", "no");
        //AppPreferences.getInstance(this).addToStoreInteger("rotate",0);
        clearCache();

    }

    @Override
    protected void onStart() {
        super.onStart();
        addToStore("inactivity", "yes");
    }

    private void clearCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    deleteAppFiles(new File(Constants.CACHETEMP));
                } catch (Exception e) {
                    // TODO: handle exception

                }
                try {
                    File mydir = getDir("Downloads", Context.MODE_PRIVATE);
                    deleteAppFiles(mydir);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }).start();
    }

    /**
     * deleteAppFiles - This method is called on canceling a request.
     *
     * @param - file name to be deleted
     **/
    private boolean deleteAppFiles(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteAppFiles(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    public String getCid() {
        return cid;
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

    public boolean isDrawerOpen() {
        return false;// mNavigationDrawerFragment.isDrawerOpen();
    }

    @Override
    public void onResume() {
        super.onResume();
        getProfileData();
    }

    public void showFilterFragment(boolean checkSubscirption) {

        if (filters == null)
            filters = new BsecureFilters();

        if (checkSubscirption && !filters.isAdded()) {

            try {
                String url = AppSettings.getInstance(this).getPropertyValue("subscirption");
                JSONObject object = new JSONObject();
                object.put("email", AppPreferences.getInstance(this).getFromStore("email"));
                HTTPostJson post = new HTTPostJson(this, this, object.toString(), 7);
                post.setContentType("application/json");
                post.execute(url, "");
                Utils.showProgress(getString(R.string.pleasewait), this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        FragmentTransaction transaction = manager.beginTransaction();

        if (filters.isAdded() && filters.isVisible()) {

            transaction.remove(filters);
            fragStack.pop();
            filters = null;

        } else if (filters.isAdded() && filters.isHidden()) {

            transaction.show(filters);

        } else {

            transaction.add(R.id.container, filters, "filters");
            transaction.show(filters);
            fragStack.push((ParentFragment) filters);

        }

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
        manager.executePendingTransactions();

    }

    public void updateMailCounts(Item item) {

        Menu menu = navigationView.getMenu();

        Object object = item.get("inboxcount");
        if (object != null) {

            Vector<Item> items = (Vector<Item>) object;
            Item temp = items.get(0);
            String cnt = temp.getAttribute("icount");
            if (!cnt.equalsIgnoreCase("0")) {
                MenuItem menuItem = menu.getItem(0);
                menuItem.setTitle(getString(R.string.inbox) + "      " + "(" + cnt + ")");
            } else {
                MenuItem menuItem = menu.getItem(0);
                menuItem.setTitle(getString(R.string.inbox) + "       " + "");
            }
        }

        object = item.get("sentmailcount");
        if (object != null) {
            Vector<Item> items = (Vector<Item>) object;
            Item temp = items.get(0);
            String cnt = temp.getAttribute("scount");
            if (!cnt.equalsIgnoreCase("0")) {

                MenuItem menuItem = menu.getItem(1);
                menuItem.setTitle(getString(R.string.sent) + "        " + "(" + cnt + ")");

            } else {
                MenuItem menuItem = menu.getItem(1);
                menuItem.setTitle(getString(R.string.sent) + "        " + "(" + cnt + ")");
            }
        }

        object = item.get("draftscount");
        if (object != null) {
            Vector<Item> items = (Vector<Item>) object;
            Item temp = items.get(0);
            String cnt = temp.getAttribute("dcount");
            if (!cnt.equalsIgnoreCase("0")) {

                MenuItem menuItem = menu.getItem(2);
                menuItem.setTitle(getString(R.string.drafts) + "       " + "(" + cnt + ")");

            } else {
                MenuItem menuItem = menu.getItem(2);
                menuItem.setTitle(getString(R.string.drafts) + "       " + "(" + cnt + ")");
            }
        }

        object = item.get("trashcount");
        if (object != null) {
            Vector<Item> items = (Vector<Item>) object;
            Item temp = items.get(0);
            String cnt = temp.getAttribute("tcount");
            if (!cnt.equalsIgnoreCase("0")) {
                MenuItem menuItem = menu.getItem(3);
                menuItem.setTitle(getString(R.string.trash) + "       " + "(" + cnt + ")");
            } else {
                MenuItem menuItem = menu.getItem(3);
                menuItem.setTitle(getString(R.string.trash) + "       " + "(" + cnt + ")");
            }
        }

    }

    public void showComposeViewMessagePageView(Item item) {

        if (mailViews == null)
            mailViews = new MailViews();

        Bundle bundle = new Bundle();
        bundle.putSerializable("item", item);
        mailViews.setArguments(bundle);

        swiftFragments(mailViews, "mailViews");

    }

    public void FolderViewMessage(Item item) {

        if (folderFrgmentlist == null)
            folderFrgmentlist = new FolderMessageList();

        Bundle bundle = new Bundle();
        bundle.putSerializable("item", item);
        folderFrgmentlist.setArguments(bundle);

        swiftFragments(folderFrgmentlist, "folderFrgmentlist");

    }

    public void showComposeViewMessagePage(Item item) {

        if (olders == null)
            olders = new OlderGrop();

        Bundle bundle = new Bundle();
        bundle.putSerializable("item", item);
        olders.setArguments(bundle);

        swiftFragments(olders, "olders");

    }

    public void showSentViewMessagePageView(Item item) {

        if (sentMailViews == null)
            sentMailViews = new SentMailViews();

        Bundle bundle = new Bundle();
        bundle.putSerializable("item", item);
        sentMailViews.setArguments(bundle);

        swiftFragments(sentMailViews, "sentMailViews");

    }

    public void showSentViewMessagePagePage(Item item) {

        if (sentolders == null)
            sentolders = new SentMailsOlders();

        Bundle bundle = new Bundle();
        bundle.putSerializable("item", item);
        sentolders.setArguments(bundle);

        swiftFragments(sentolders, "sentolders");
    }

    public void openForwardMail(Item item) {
        if (composeMail == null)
            composeMail = new ComposeMail();

        item.setAttribute("forward_values", "yes");
        Bundle bundle = new Bundle();
        bundle.putSerializable("item", item);
        composeMail.setArguments(bundle);

        swiftFragments(composeMail, "composeMail");
    }

    public void logE(String key, String value) {
        // Log.e(key, value);
    }

    public void sendMailListRefresh() {
        if (mailListing != null) {
            mailListing.refresh();
        }
    }

    public void draftMailListRefresh() {
        if (draftMailListing != null) {
            draftMailListing.refresh();
        }
    }

    public void lockMode() {
        // mNavigationDrawerFragment.lockMode();
    }

    public void unLockMode() {
        // mNavigationDrawerFragment.unLockMode();
    }

    private Item processData(Uri uri) {

        try {

            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                Item item = new Item("");

                do {
                    String[] resultsColumns = cursor.getColumnNames();
                    for (int i = 0; i < resultsColumns.length; i++) {
                        String key = resultsColumns[i];
                        String value = cursor.getString(cursor.getColumnIndexOrThrow(resultsColumns[i]));

                        if (value != null) {
                            if (key.contains("_"))
                                key = key.replace("_", "");
                            item.setAttribute(key, value);

                        }
                    }

                } while (cursor.moveToNext());

                cursor.close();
                cursor = null;

                return item;
            }

        } catch (Exception e) {

        }

        return null;

    }

    private void closeDialogs() {
        if (rpyDialog != null)
            rpyDialog.dismiss();
        rpyDialog = null;

        if (bunbDialog != null)
            bunbDialog.dismiss();
        bunbDialog = null;

        if (blockUser != null)
            blockUser.dismiss();
        blockUser = null;

        if (attachDialog != null)
            attachDialog.dismiss();
        attachDialog = null;
        if (notifyDialog != null)
            notifyDialog.dismiss();
        notifyDialog = null;

        if (repDialog != null)
            repDialog.dismiss();
        repDialog = null;
        if (folderDialog != null)
            folderDialog.dismiss();
        folderDialog = null;

    }

    private void showReplyDialog(Item item) {

        if (rpyDialog == null)
            rpyDialog = new ReplyDialog();

        Bundle conatcts = new Bundle();
        conatcts.putSerializable("item", item);
        rpyDialog.setArguments(conatcts);
        rpyDialog.show(Bsecure.this.getSupportFragmentManager(), "dialog");

    }

    private void showNotification(int id, int yes) {
        if (notifyDialog == null)
            notifyDialog = new NotificationDialog();
        try {
            String link = AppSettings.getInstance(this).getPropertyValue("notify_user_view");
            JSONObject object = new JSONObject();
            object.put("email", AppPreferences.getInstance(this).getFromStore("email"));
            HTTPostJson post = new HTTPostJson(this, this, object.toString(), 8);
            post.setContentType("application/json");
            post.execute(link, "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        notifyDialog.show(Bsecure.this.getSupportFragmentManager(), "dialog");
    }

    private void showBlockUnDialog(int id, int titleId) {

        if (bunbDialog == null)
            bunbDialog = new BlockUnblockDialog();

        Bundle conatcts = new Bundle();
        conatcts.putInt("id", id);
        conatcts.putInt("titleId", titleId);
        bunbDialog.setArguments(conatcts);
        bunbDialog.show(Bsecure.this.getSupportFragmentManager(), "dialog");

    }

    private void showBlockDialog(int id, int titleId) {

        if (blockUser == null)
            blockUser = new BlockUserDialog();

        Bundle conatcts = new Bundle();
        conatcts.putInt("id", id);
        conatcts.putInt("titleId", titleId);
        blockUser.setArguments(conatcts);
        blockUser.show(Bsecure.this.getSupportFragmentManager(), "dialog");

    }

    private void submitReport(Item item) {

        try {
            String reportMsg = ((EditText) repDialog.getView().findViewById(R.id.cmttxt_rep)).getText().toString();

            reportMsg = reportMsg.trim();

//			Vector<Item> items = (Vector<Item>) item.get("mail_details");
//			Item msgItem = items.get(0);
            String composeid = item.getAttribute("composeid");

            if (reportMsg.length() == 0) {
                showToast(R.string.cur_rep);
                return;
            }

            String link = AppSettings.getInstance(this).getPropertyValue("report_abs");
            JSONObject object = new JSONObject();
            object.put("email", AppPreferences.getInstance(this).getFromStore("email"));
            object.put("composeid", composeid);
            object.put("message", reportMsg);

            HTTPostJson post = new HTTPostJson(this, this, object.toString(), 5);
            post.setContentType("application/json");
            post.execute(link, "");
            Utils.showProgress(getString(R.string.pwait), this);

            closeDialogs();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void submitReply(Item item) {

        try {

            // String replyMsg = ((EditText)
            // rpyDialog.getView().findViewById(R.id.cmttxt_reply)).getText().toString();

            // replyMsg = replyMsg.trim();
            //
            // if (replyMsg.length() == 0) {
            // showToast(R.string.pwyr);
            // return;
            // }

            String link = AppSettings.getInstance(this).getPropertyValue("reply_receiver");

            Utils.showProgress(getString(R.string.pwait), this);

            JSONObject object = new JSONObject();

            object.put("composeid", item.getAttribute("composeid"));

            object.put("from", item.getAttribute("from"));

            object.put("to", getFromStore("email"));

            object.put("msg",
                    ((EditText) rpyDialog.getView().findViewById(R.id.cmttxt_reply)).getText().toString().trim());

            HTTPostJson post = new HTTPostJson(this, this, object.toString(), 1);
            post.setContentType("application/json");
            post.execute(link, "");

            closeDialogs();

			/*
			 * link = link.replace("(COMID)", item.getAttribute("composeid"));
			 * link = link.replace("(TO)", getFromStore("email")); link =
			 * link.replace("(FROM)", item.getAttribute("from")); link =
			 * link.replace("(MSG)", replyMsg);
			 *
			 * HTTPTask task = new HTTPTask(this, this);
			 * task.userRequest(getString(R.string.pwait), 1, link);
			 *
			 * closeDialogs();
			 */

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void makeNotifyUser(Integer tagId) {

        try {
            RadioGroup rg = (RadioGroup) notifyDialog.getView().findViewById(R.id.notify_grp);
            String value = ((RadioButton) notifyDialog.getView().findViewById(rg.getCheckedRadioButtonId())).getText()
                    .toString();

            // addToStore("notifyvalue", notify);

            String link1 = AppSettings.getInstance(this).getPropertyValue("notify_user");
            JSONObject object = new JSONObject();
            object.put("notify", value);
            object.put("email", AppPreferences.getInstance(this).getFromStore("email"));

            HTTPostJson post = new HTTPostJson(this, this, object.toString(), 3);
            post.setContentType("application/json");
            post.execute(link1, "");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void FolderDialog() {

        try {
            String foldername = ((EditText) folderDialog.getView().findViewById(R.id.cr_folder)).getText().toString();
            if (foldername.length() == 0) {
                showToast(R.string.fnef);
                return;
            }
            if (foldername.charAt(0) == ' ') {
                showToast(R.string.fnef);
                return;
            }

            String cr_folder = AppSettings.getInstance(this).getPropertyValue("folder_cr");
            JSONObject object = new JSONObject();
            object.put("email", getFromStore("email"));
            object.put("foldername", foldername);
            HTTPostJson post = new HTTPostJson(this, this, object.toString(), 10);
            post.setContentType("application/json");
            post.execute(cr_folder, "");
            Utils.showProgress(getString(R.string.pwait), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makeBlockUnblockUser(Integer id) {

        String link = "";
        JSONObject object = null;

        switch (id) {

            case R.id.tv_blockusers:

                String email = ((AutoCompleteTextView) blockUser.getView().findViewById(R.id.cmttxt)).getText().toString();

                email = email.trim();

                if (email.length() == 0) {
                    showToast(R.string.peei);
                    return;
                }

                if (!emailValidation(email)) {
                    showToast(R.string.peavei);
                    return;
                }
                try {
                    link = getPropertyValue("unsubUser");
                    // link = link.replace("(EMAIL)", getFromStore("email"));
                    // link = link.replace("(UNEMAIL)", email);
                    object = new JSONObject();
                    object.put("email", getFromStore("email"));
                    object.put("unsubscribeemail", email);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case R.id.tv_unblockusers:
                String email1 = ((AutoCompleteTextView) bunbDialog.getView().findViewById(R.id.cmttxt)).getText()
                        .toString();

                email1 = email1.trim();

                if (email1.length() == 0) {
                    showToast(R.string.peei);
                    return;
                }

                if (!emailValidation(email1)) {
                    showToast(R.string.peavei);
                    return;
                }
                try {
                    link = getPropertyValue("unblockUser");
                    object = new JSONObject();
                    object.put("email", getFromStore("email"));
                    object.put("unblockemail", email1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }

        // HTTPTask task = new HTTPTask(this, this);
        // task.userRequest(getString(R.string.pwait), 2, link);
        HTTPostJson post = new HTTPostJson(this, this, object.toString(), 2);
        post.setContentType("application/json");
        post.execute(link, "");

    }

    public void getSearchUnblock(String unblockkey) {

        try {
            String url = AppSettings.getInstance(this).getPropertyValue("unblock_list");
            JSONObject object = new JSONObject();
            object.put("email", getFromStore("email"));
            object.put("unblocksearch", unblockkey);

            HTTPostJson post = new HTTPostJson(this, this, object.toString(), 9);
            post.setContentType("application/json");
            post.execute(url, "");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void parseReportResponse(String response, int requestType) throws Exception {
        if (response != null && response.length() > 0) {

            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.optString("status").equalsIgnoreCase("0")) {
                closeDialogs();
            }
            showToast(jsonObject.optString("statusdescription"));

        }

    }

    private void parseBlockUnBlockResponse(String response, int requestId) throws Exception {

        if (response != null && response.length() > 0) {

            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.optString("status").equalsIgnoreCase("0")) {
                closeDialogs();
            }
            showToast(jsonObject.optString("statusdescription"));

        }
    }

    private void parseNotifyResponse(String response1, int requestType) throws Exception {
        if (response1 != null && response1.length() > 0) {

            JSONObject jsonObject = new JSONObject(response1);

            if (jsonObject.optString("status").equalsIgnoreCase("0")) {
                closeDialogs();
            }
            showToast(jsonObject.optString("notify"));
        }

    }

    private void parseImportResponse(String res, int requestType) throws Exception {
        if (res != null && res.length() > 0) {

            JSONObject jsonObject = new JSONObject(res);

            if (jsonObject.optString("status").equalsIgnoreCase("0")) {
                closeDialogs();
            }
            showToast(jsonObject.optString("statusdescription"));

        }
    }
	/*
	 * private void initCrashLog() { mUEHandler = new
	 * Thread.UncaughtExceptionHandler() {
	 *
	 * @Override public void uncaughtException(Thread t, Throwable e) { try {
	 *
	 * PrintWriter pw = new PrintWriter(new BufferedWriter(new
	 * FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath()+
	 * "/bsecure.log", true))); e.printStackTrace(pw); pw.flush(); pw.close();
	 *
	 * e.printStackTrace();
	 *
	 * } catch (Exception ex) { ex.printStackTrace(); } Bsecure.this.finish(); }
	 * }; Thread.setDefaultUncaughtExceptionHandler(mUEHandler); }
	 */

    private void updateStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // window.setStatusBarColor(color);
        }
    }

    public void openDraftMail(Item item) {
        if (composeMail == null)
            composeMail = new ComposeMail();

        item.setAttribute("drafts_values", "yes");
        Bundle conatcts = new Bundle();
        conatcts.putSerializable("item", item);
        composeMail.setArguments(conatcts);

        swiftFragments(composeMail, "composeMail");
    }

    private Item processFileMetadata(Intent intent) {

        String url = intent.getDataString();

        writeLogs("URL : " + url);

        url = URLDecoder.decode(url);


        String id = "0";

        url = url.replace("content://", "");

        if (url.contains(":"))
            id = url.substring(url.lastIndexOf(":") + 1, url.length());
        else
            id = url.substring(url.lastIndexOf("/") + 1, url.length());

        writeLogs("URL after : " + url);
        writeLogs("id after : " + id);

        String sel = MediaStore.Video.Media._ID + "=?";

        //grantUriPermission(null, Uri.parse(sel), Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"), null, sel,
                new String[]{id}, null);

        writeLogs("cursor : " + cursor);

        Item item = null;

        if (cursor != null && cursor.moveToFirst()) {
            item = new Item("");

            String[] resultsColumns = cursor.getColumnNames();
            do {

                for (int i = 0; i < resultsColumns.length; i++) {
                    String key = resultsColumns[i];
                    String value = cursor.getString(cursor.getColumnIndexOrThrow(resultsColumns[i]));

                    writeLogs("key : " + key + "       ---   " + "value : " + value);

                    if (value != null) {
                        if (key.contains("_"))
                            key = key.replace("_", "");
                        item.setAttribute(key, value);

                    }
                }

            } while (cursor.moveToNext());

            cursor.close();
            cursor = null;

        }

        if (item == null) {

            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

            Uri uri = intent.getData();

            if (isKitKat && DocumentsContract.isDocumentUri(this, uri)) {

                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        // return Environment.getExternalStorageDirectory() +
                        // "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String documentId = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                            Long.valueOf(documentId));

                    try {
                        cursor = getContentResolver().query(contentUri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            item = new Item("");
                            String[] resultsColumns = cursor.getColumnNames();

                            for (int i = 0; i < resultsColumns.length; i++) {
                                String key = resultsColumns[i];
                                String value = cursor.getString(cursor.getColumnIndexOrThrow(resultsColumns[i]));

                                // Log.e("" + key, value + "");

                                if (value != null) {
                                    if (key.contains("_"))
                                        key = key.replace("_", "");
                                    item.setAttribute(key, value);

                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }

                }
            }


        }

        return item;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public void showAttachmentPopup() {

        try {

            if (attachDialog == null)
                attachDialog = new AttachmentDialog();

            Bundle conatcts = new Bundle();
            // conatcts.putSerializable("item", item);
            attachDialog.setArguments(conatcts);
            attachDialog.setCancelable(false);
            attachDialog.show(Bsecure.this.getSupportFragmentManager(), "dialog");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void validateSelectedFile(Item item) {
        try {
            if (item == null) {
                showToast(R.string.syhnsai1);
                return;
            }

            // writeLogs("Item : " + item);
            //
            // logE("----------------", item + "");

            long fileSize = 0;
            fileSize = Long.parseLong(item.getAttribute("size")); // 6808826
            if (fileSize > 800000000) {
                showToast(R.string.fsitbpsafwilt);
                return;
            }

            if (item.getAttribute("mimetype").contains("exe")) {
                showToast(R.string.exefans);
                return;
            }
            if (item.getAttribute("displayname").contains(".exe")) {
                showToast(R.string.exefans);
                return;
            }
            if (attachDialog.getSelectedValue().equalsIgnoreCase("View Only")) {
                if (item.getAttribute("mimetype").contains("video/divx")) {
                    showToast(R.string.smp4);
                    return;
                }
            }
            if (attachDialog.getSelectedValue().equalsIgnoreCase("View Only")) {
                if (item.getAttribute("displayname").contains(".mkv")) {
                    showToast(R.string.smp4);
                    return;
                }
            }
            if (attachDialog.getSelectedValue().equalsIgnoreCase("View Only")) {
                if (item.getAttribute("displayname").contains(".3gp")) {
                    showToast(R.string.smp4);
                    return;
                }
            }
            if (attachDialog.getSelectedValue().equalsIgnoreCase("View Only")) {
                if (item.getAttribute("displayname").contains(".flv")) {
                    showToast(R.string.smp4);
                    return;
                }
            }

            item.setAttribute("attachmentlevel", attachDialog.getSelectedValue());

            closeDialogs();

            composeMail.addAttachmentView(item, fileSize);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void launchDownloadDialog(String fileName, int id, String library, String from) {

        try {
            if (dpDialog == null)
                dpDialog = new DownloadProgressDialog();

            Bundle conatcts = new Bundle();
            conatcts.putString("fileName", fileName);
            conatcts.putString("library", library);
            conatcts.putInt("id", id);
            conatcts.putString("to", from);

            if (mailViews != null && mailViews.isVisible())
                conatcts.putInt("showtime", mailViews.getShowTime());

            if (sentMailViews != null && sentMailViews.isVisible())
                conatcts.putInt("showtime", sentMailViews.getShowTime());

            dpDialog.setArguments(conatcts);
            dpDialog.setCancelable(false);
            dpDialog.show(Bsecure.this.getSupportFragmentManager(), "dialog");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeLogs(String request) {
        try {//////////////////////////

//            PrintWriter pw = new PrintWriter(new BufferedWriter(new
//                    FileWriter(Constants.PATH + "/sendsafe.log", true)));
//            pw.append("\n");
//            pw.append("-----------------------------------------------------------\n");
//            pw.append("\n");
//            pw.append("Request API:: " + request + "");
//            pw.append("\n");
//            pw.append("------------------------------------------------------------\n");
//            pw.flush();
//            pw.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void temp() {

        FragmentTransaction trans = manager.beginTransaction();

        if (manager.findFragmentByTag("mailListing") != null) {
            trans.remove(manager.findFragmentByTag("mailListing"));
        }

        if (manager.findFragmentByTag("sentMailListing") != null) {
            trans.remove(manager.findFragmentByTag("sentMailListing"));
        }

        if (manager.findFragmentByTag("trashMailListing") != null) {
            trans.remove(manager.findFragmentByTag("trashMailListing"));
        }

        if (manager.findFragmentByTag("draftMailListing") != null) {
            trans.remove(manager.findFragmentByTag("draftMailListing"));
        }

        if (manager.findFragmentByTag("mailViews") != null) {
            trans.remove(manager.findFragmentByTag("mailViews"));
        }

        if (manager.findFragmentByTag("sentMailViews") != null) {
            trans.remove(manager.findFragmentByTag("sentMailViews"));
        }

        if (manager.findFragmentByTag("settings") != null) {
            trans.remove(manager.findFragmentByTag("settings"));
        }
        if (manager.findFragmentByTag("folderFrgment") != null) {
            trans.remove(manager.findFragmentByTag("folderFrgment"));
        }
        if (manager.findFragmentByTag("composeMail") != null) {
            trans.remove(manager.findFragmentByTag("composeMail"));
        }

        if (manager.findFragmentByTag("filters") != null) {
            trans.remove(manager.findFragmentByTag("filters"));
        }

        trans.commit();
        trans = null;

    }

    private void getProfileData() {

        try {
            String url = AppSettings.getInstance(this).getPropertyValue("profile_ret");
            JSONObject object = new JSONObject();
            object.put("email", getFromStore("email"));

            HTTPostJson post = new HTTPostJson(this, this, object.toString(), 6);
            post.setContentType("application/json");
            post.execute(url, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCustomWebview() {

        Intent mainIntent = new Intent(Bsecure.this, MPayment.class);
        startActivityForResult(mainIntent, 2002);

    }

    private void parseToContacts(String results, int requestType) throws Exception {
        if (results != null && results.length() > 0) {
            JSONObject object = new JSONObject(results.toString());
            if (object.has("status") && object.optString("status").equalsIgnoreCase("0")) {
                JSONArray array = object.getJSONArray("blocklist");
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
//            if (object.has("status") && object.optString("status").equalsIgnoreCase("1")) {
//                showToast(object.optString("statusdescription"));
//            }

        }
    }

}

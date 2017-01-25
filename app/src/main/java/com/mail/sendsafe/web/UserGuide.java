package com.mail.sendsafe.web;

import com.mail.sendsafe.R;
import com.mail.sendsafe.callbacks.IItemHandler;
import com.mail.sendsafe.common.AppSettings;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class UserGuide extends AppCompatActivity implements IItemHandler {

	private WebView wv_content = null;
	private WebSettings webSettings = null;

	private ProgressBar progressBar;
	//private ContainerScriptInterface jsInterface = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		setContentView(R.layout.web_page);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.inst_ttitle);
		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		wv_content = (WebView) findViewById(R.id.wv_page_disply);
		wv_content.getSettings().setAllowFileAccess(true);
		wv_content.getSettings().setSupportZoom(true);
		wv_content.setVerticalScrollBarEnabled(true);
		wv_content.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		wv_content.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
		wv_content.getSettings().setLoadWithOverviewMode(true);
		wv_content.getSettings().setUseWideViewPort(true);
		wv_content.getSettings().setJavaScriptEnabled(true);
		wv_content.getSettings().setPluginState(WebSettings.PluginState.ON);

		wv_content.getSettings().setSaveFormData(false);
		wv_content.getSettings().setSavePassword(false);

		wv_content.getSettings().setRenderPriority(RenderPriority.HIGH);
		wv_content.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

		wv_content.setWebViewClient(new MyWebViewClient());
		wv_content.setWebChromeClient(new MyWebChromeClient());

		wv_content.getSettings().setJavaScriptEnabled(true);
		wv_content.getSettings().setLoadWithOverviewMode(true);
		wv_content.getSettings().setUseWideViewPort(true);

		webSettings = wv_content.getSettings();
		webSettings.setJavaScriptEnabled(true);
		//wv_content.addJavascriptInterface(jsInterface, "jsInterface");
		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setDefaultFontSize(50);

		String user_guide = AppSettings.getInstance(this).getPropertyValue("user_guide");
		wv_content.loadUrl(user_guide);

	}

	private class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {

//			Log.e("-=-=-=-=-=-", newProgress + "");

			if (newProgress == 5)
				findViewById(R.id.pb_allpg).setVisibility(View.VISIBLE);

			if (newProgress >= 95) {
				findViewById(R.id.pb_allpg).setVisibility(View.GONE);
			}
			super.onProgressChanged(view, newProgress);
		}

		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			return super.onJsAlert(view, url, message, result);
		}
	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			findViewById(R.id.pb_allpg).setVisibility(View.GONE);
		}
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}
	}

	private void close() {
		if (wv_content != null) {
			wv_content.stopLoading();
			wv_content.setBackgroundDrawable(null);
			wv_content.clearFormData();
			wv_content.clearHistory();
			wv_content.clearMatches();
			wv_content.clearCache(true);
			wv_content.clearSslPreferences();
			wv_content = null;
		}
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:

			UserGuide.this.finish();

			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onFinish(Object results, int requestType) {

	}

	@Override
	public void onError(String errorCode, int requestType) {

	}

}

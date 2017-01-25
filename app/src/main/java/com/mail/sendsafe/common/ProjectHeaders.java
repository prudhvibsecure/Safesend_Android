package com.mail.sendsafe.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.webkit.WebView;

public class ProjectHeaders {

	private Context context = null;
	
	static ProjectHeaders headers = null;
	
	private Item item = null;	
	
	public static ProjectHeaders getInstance(Context context) {
		if (headers == null)
			headers = new ProjectHeaders(context);
		return headers;
	}
	
	private ProjectHeaders(Context context) {
		this.context = context;		
	}	
	
	public Item getHeaders() {				

		if(item != null && item.size() > 0) {
			return item;
		}	
			
		item = new Item("headers");

		try {
						
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					WebView webView = new WebView(context);
					webView.getSettings().getUserAgentString();
					item.setAttribute("User-Agent", webView.getSettings().getUserAgentString());
					webView = null;												
				}
			});
						
			item.setAttribute("X-App-OEM",android.os.Build.MANUFACTURER.toString());
			item.setAttribute("X-App-Model",android.os.Build.MODEL.toString());
			item.setAttribute("X-App-OS", "Android");
			item.setAttribute("X-App-OSVersion",android.os.Build.VERSION.RELEASE.toString());
			item.setAttribute("X-App-Res", getResolution());			
			item.setAttribute("X-VERSION", applicationVName()+"");	
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return item;
	}		

	/**
	 * getResolution - This method return device screen resolution.
	 * @return String
	 */
	private String getResolution() {
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		int height = windowManager.getDefaultDisplay().getHeight();
		int width = windowManager.getDefaultDisplay().getWidth();
		return width + "x" + height;
	}
	
	public String applicationVName() {
		String versionName = "";
		PackageManager manager = context.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			versionName = info.versionName;
		} catch (NameNotFoundException e) {
			
		}
		return versionName;
	}	

	public void onDestroy() {

	}	

}

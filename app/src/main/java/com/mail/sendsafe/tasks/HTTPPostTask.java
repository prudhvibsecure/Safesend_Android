package com.mail.sendsafe.tasks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Enumeration;

import com.mail.sendsafe.R;
import com.mail.sendsafe.callbacks.IItemHandler;
import com.mail.sendsafe.common.AppPreferences;
import com.mail.sendsafe.common.Item;
import com.mail.sendsafe.parsers.JSONParser;
import com.mail.sendsafe.utils.TimeUtils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class HTTPPostTask implements IItemHandler {

	private Context context = null;

	private IItemHandler callback = null;
	
	private int requestId = -1;
	
	private boolean progressFlag = true;
	
	private Item headers = null;		
	
	private String contentType = "";
	
	private Object obj = null;
	
	private boolean state = false;
	
	private int cacheType = 0;
	
	private String errorMsg = "";

	private Dialog dialog = null;

	public HTTPPostTask(Context context, IItemHandler callback) {
		this.context = context;
		this.callback = callback;
	}

	public void disableProgress() {
		progressFlag = false;
	}

	public void setHeaders(Item aItem) {
		headers = aItem;
	}

	public Item getHeaders() {
		return headers;
	}
	
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void userRequest(String progressMsg, int requestId, final String url, final String postData, final int parserType) {
		
		this.requestId = requestId;
		
		if (progressFlag)
			showProgress(progressMsg, context);

		if (!isNetworkAvailable()) {
			showUserActionResult(-1, context.getString(R.string.nipcyns));
			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {

				HttpURLConnection conn = null;				
				DataOutputStream outputStream = null;			
				InputStream inputStream = null;
				
				try {

					String link = urlEncode(url);

					inputStream = new ByteArrayInputStream(postData.getBytes());
					
					conn = getHTTPConnection(link, postData, contentType);
					
					if (conn == null) {
						postUserAction(-1, context.getString(R.string.isr));
						return;
					}	

					if (inputStream != null) {
						outputStream = new DataOutputStream(conn.getOutputStream());
						
						byte[] data = new byte[1024];
						int bytesRead = 0;

						while ((bytesRead = inputStream.read(data)) != -1)

						{																									
							outputStream.write(data, 0, bytesRead);
						}						
					}
					
					int serverResponseCode = conn.getResponseCode();
					
					String serverResponseMessage = conn.getResponseMessage();

					if (serverResponseCode == 200) {

						inputStream = conn.getInputStream();
						
						byte[] bytebuf = new byte[0x1000];
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						for (;;) {
							int len = inputStream.read(bytebuf);
							if (len < 0)
								break;
							baos.write(bytebuf, 0, len);
						}
						bytebuf = baos.toByteArray();
						String response = new String(bytebuf, "UTF-8");

						if(parserType == -1) {
							obj = response;
							postUserAction(0, "");
							return;
						}
						
						inputStream = new ByteArrayInputStream(baos.toByteArray());

						parseData(parserType, inputStream);
						
						if (state) {
							if (cacheType != 0 && obj != null) {								
								if(obj instanceof Item && ((Item)obj).size() > 0) {

								}
							}
							postUserAction(0, "");
						} else 	
							postUserAction(-1, errorMsg);
						return;
					}
					
					postUserAction(-1, serverResponseMessage);

				} catch (MalformedURLException me) {
					
					postUserAction(-1, context.getString(R.string.iurl));
				}

				catch (ConnectException e) {
					
					postUserAction(-1, context.getString(R.string.snr1));
				}

				catch (SocketException se) {
					
					postUserAction(-1, context.getString(R.string.snr2));
				}

				catch (SocketTimeoutException stex) {
					postUserAction(-1, context.getString(R.string.sct));
				}

				catch (Exception ex) {
					postUserAction(-1, context.getString(R.string.snr3));
				}

				finally {
					if (inputStream != null)
						try {
							inputStream.close();
							inputStream = null;
						} catch (IOException e) {
							//TraceUtils.logException(e);
						}
					
					if (outputStream != null)
						try {
							outputStream.close();
							outputStream = null;
						} catch (IOException e) {
							//TraceUtils.logException(e);
						}										
					
					if (conn != null)
						conn.disconnect();
					conn = null;
				}
			}
		}).start();
	}		

	private void postUserAction(final int status, final String response) {
		
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {

				showUserActionResult(status, response);

			}
		});
	}

	private void showUserActionResult(int status, String response) {

		dismissProgress();
		
		switch (status) {
		case 0:
			callback.onFinish(obj, requestId);
			break;

		case -1:
			callback.onError(response, requestId);
			break;

		default:
			break;
		}

	}

	/**
	 * checkConnectivity - Checks Whether Internet connection is available or
	 * not.
	 */
	private boolean isNetworkAvailable() {

		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager == null) {
			return false;
		}

		NetworkInfo net = manager.getActiveNetworkInfo();
		if (net != null) {
			return net.isConnected();
		} else {
			return false;
		}
	}
	
	/**
	 * urlEncode -
	 *
	 * @return String
	 */
	public static String urlEncode(String sUrl) {
		int i = 0;
		String urlOK = "";
		while (i < sUrl.length()) {
			if (sUrl.charAt(i) == ' ') {
				urlOK = urlOK + "%20";
			} else {
				urlOK = urlOK + sUrl.charAt(i);
			}
			i++;
		}
		return (urlOK);
	}
	
	@Override
	public void onFinish(Object results, int requestType) {
		state = true;
		this.requestId = requestType;
		this.obj = results;
	}

	@Override
	public void onError(String errorCode, int requestType) {
		state = false;
		errorMsg = errorCode;
		this.requestId = requestType;		
	}		
	
	public HttpURLConnection getHTTPConnection(String url, String postData,
			String contentType) throws Exception {

		HttpURLConnection _conn = null;
		URL serverAddress = null;
		for (int i = 0; i <= 1; i++) {

			try {
				serverAddress = new URL(url);
				_conn = (HttpURLConnection) serverAddress.openConnection();
				if (_conn != null) {

					_conn.setDoInput(true);
					_conn.setDoOutput(true);
					_conn.setUseCaches(false);
					_conn.setRequestProperty("connection", "close");
					_conn.setRequestMethod("POST");

					if (headers != null) {
						Enumeration keys = headers.keys();
						while (keys.hasMoreElements()) {
							String key = keys.nextElement().toString();
							String value = headers.get(key).toString();
							_conn.setRequestProperty(key, value);
						}
					}

					_conn.setRequestProperty("Connection", "Keep-Alive");
					_conn.setRequestProperty("Content-Type", contentType);									

					_conn.setReadTimeout(120000);
					_conn.setConnectTimeout(120000);

					_conn.setRequestProperty("X-BSECURE-UTIME",
							TimeUtils.getDeviceDateTime("yyyy-MM-dd HH:mm:ss"/* "yyyy-MM-dd hh:mm:ss a" */) + "");
					_conn.setRequestProperty("X-BSECURE-UZONE", TimeUtils.getTimeZone() + "");
					_conn.setRequestProperty("X-DOMAIN", "5");
					
					String aid=AppPreferences.getInstance(context).getFromStore("authid");
					_conn.setRequestProperty("X-AUTHID", aid);
					_conn.connect();

					return _conn;
				}
			}

			catch (MalformedURLException me) {
				throw me;
			}

			catch (SocketTimeoutException se) {
				_conn = null;
				if (i != 0)
					throw se;
			}

			catch (Exception e) {
				throw e;
			}
		}
		return null;
	}


	private void parseData(int parserType, InputStream inputStream) throws Exception {
		try {
			switch (parserType) {

			case 1:
				JSONParser jsonParser = new JSONParser(this, requestId);
				jsonParser.parseXmlData(inputStream);
				break;

			default:
				break;
			}

		} catch (Exception e) {
			throw e;
		}
	}

	private void showProgress(String title, Context context) {

		dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		dialog.setCancelable(false);
		View view = View.inflate(context, R.layout.processing, null);

		((TextView) view.findViewById(R.id.progrtxt)).setText(title);
		try {
			Typeface zawgyi = null;
			if (zawgyi == null)
				zawgyi = Typeface.createFromAsset(context.getAssets(), "fonts/padauk.ttf");

			if (AppPreferences.getInstance(context).getFromStore("language").equals("2")) {
				((TextView) view.findViewById(R.id.progrtxt)).setTypeface(zawgyi);
			}
		} catch (Exception e) {
			//TraceUtils.logException(e);
		}

		dialog.setContentView(view);
		dialog.show();

	}

	private void dismissProgress() {
		try {

			if (dialog != null)
				dialog.dismiss();
			dialog = null;

		} catch (Exception e) {
			//TraceUtils.logException(e);
		}
	}
	
}

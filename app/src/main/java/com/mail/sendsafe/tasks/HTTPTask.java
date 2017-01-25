package com.mail.sendsafe.tasks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.mail.sendsafe.R;
import com.mail.sendsafe.callbacks.IItemHandler;
import com.mail.sendsafe.common.Item;
import com.mail.sendsafe.utils.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

public class HTTPTask {

	private Context context = null;
	
	private IItemHandler callback = null;
	
	private int requestId = -1;
	
	private boolean progressFlag = true;
	
	private Item item = null;
		
	private GetConnection getConn = null;
		
	public HTTPTask(Context context, IItemHandler callback) {
		this.context = context;
		this.callback = callback;
	}
	
	public void disableProgress() {
		progressFlag = false;
	}

	public void setHeaders(Item aItem) {
		item = aItem;
	}

	public void userRequest(String progressMsg, int requestId, final String url) {
		this.requestId = requestId;
		
		if (progressFlag)
			Utils.showProgress(progressMsg, context);

		if (!isNetworkAvailable()) {
			showUserActionResult(-1, context.getString(R.string.nipcyns));
			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {

				HttpURLConnection conn = null;
				InputStream inputStream = null;

				try {
					
//					LogUtiles.getInstance().logE("link::: ", url+"");
					
					getConn = new GetConnection(context);
					getConn.setRequestMethod("GET");
					getConn.setRequestHeaders(item);
					
					conn = getConn.getHTTPConnection(url);
					
					if (conn == null) {
						postUserAction(-1, context.getString(R.string.isr));
						return;
					}

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

//					LogUtiles.getInstance().logE("response :-:- ", response+"");
					
					postUserAction(0, response);

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
							
						}
					
					if (conn != null)
						conn.disconnect();
					conn = null;
					
					if(getConn != null)
						getConn.clearConn();
					
					getConn = null;
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

		Utils.dismissProgress();
		
		switch (status) {
		case 0:
			callback.onFinish(response, requestId);
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
			if (net.isConnected()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}


}

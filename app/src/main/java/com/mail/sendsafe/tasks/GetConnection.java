package com.mail.sendsafe.tasks;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Enumeration;

import com.mail.sendsafe.common.AppPreferences;
import com.mail.sendsafe.common.Item;
import com.mail.sendsafe.common.ProjectHeaders;
import com.mail.sendsafe.utils.TimeUtils;

import android.content.Context;

public class GetConnection {

	private Context context = null;

	private int RESCODE = 0;

	private Item headers = null;

	private String requestMethod = "GET";

	private String contentType = "application/json";

	public GetConnection(Context context) {
		this.context = context;
	}

	public void setRequestHeaders(Item item) {
		this.headers = item;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@SuppressWarnings("rawtypes")
	public HttpURLConnection getHTTPConnection(String url) throws Exception {

		HttpURLConnection _conn = null;
		URL serverAddress = null;
		int socketExepCt = 0;
		int ExepCt = 0;
		int numRedirect = 0;

		url = urlEncode(url);

		serverAddress = new URL(url);
		String host = "http://" + serverAddress.getHost() + "/";

		for (int i = 0; i <= 2; i++) {

			try {

				serverAddress = new URL(url);

				_conn = (HttpURLConnection) serverAddress.openConnection();
				if (_conn != null) {
					_conn.setRequestMethod(requestMethod);
					_conn.setReadTimeout(30000);
					_conn.setConnectTimeout(10000);
					_conn.setUseCaches(false);
					_conn.setInstanceFollowRedirects(false);
					_conn.setDoOutput(false);

					if (requestMethod.equalsIgnoreCase("POST")) {
						_conn.setRequestMethod("POST");
						_conn.setDoInput(true);
						_conn.setDoOutput(true);
						_conn.setUseCaches(false);
						// _conn.setChunkedStreamingMode(4*1024);
						_conn.setRequestProperty("Connection", "close");
						_conn.setRequestProperty("Connection", "Keep-Alive");
						_conn.setReadTimeout(120000);
						_conn.setConnectTimeout(120000);
						_conn.setRequestProperty("Content-Type", contentType);
					}

					Item defaultItem = (Item) ProjectHeaders.getInstance(context).getHeaders().clone();

					if (defaultItem != null) {
						Enumeration keys = defaultItem.keys();
						while (keys.hasMoreElements()) {
							String key = keys.nextElement().toString();
							String value = defaultItem.get(key).toString();

							_conn.setRequestProperty(key, value);
						}
					}

					if (headers != null) {
						Enumeration keys = headers.keys();
						while (keys.hasMoreElements()) {
							String key = keys.nextElement().toString();
							String value = headers.get(key).toString();
							_conn.setRequestProperty(key, value);
						}
					}

					_conn.setRequestProperty("X-BSECURE-UTIME",
							TimeUtils.getDeviceDateTime("yyyy-MM-dd HH:mm:ss"/* "yyyy-MM-dd hh:mm:ss a" */) + "");
					_conn.setRequestProperty("X-BSECURE-UZONE", TimeUtils.getTimeZone() + "");
					_conn.setRequestProperty("X-DOMAIN", "5");
					_conn.setRequestProperty("X-AUTHID", AppPreferences.getInstance(context).getFromStore("authid"));

					RESCODE = 0;
					_conn.connect();

					if (requestMethod.equalsIgnoreCase("POST"))
						return _conn;

					RESCODE = _conn.getResponseCode();
					if (RESCODE == HttpURLConnection.HTTP_OK || RESCODE == HttpURLConnection.HTTP_PARTIAL) {
						return _conn;
					} else if (RESCODE == HttpURLConnection.HTTP_MOVED_TEMP
							|| RESCODE == HttpURLConnection.HTTP_MOVED_PERM) {
						if (numRedirect > 15) {
							_conn.disconnect();
							_conn = null;
							break;
						}

						numRedirect++;
						i = 0;
						url = _conn.getHeaderField("Location");
						if (!url.startsWith("http")) {
							url = host + url;
						}

						_conn.disconnect();
						_conn = null;
						continue;

					} else {
						_conn.disconnect();
						_conn = null;
					}
				}
			}

			catch (MalformedURLException me) {
				throw me;
			}

			catch (SocketTimeoutException se) {
				_conn = null;
				if (i >= 2)
					throw se;
			}

			catch (SocketException s) {
				if (socketExepCt > 2) {
					_conn = null;
					throw s;
				}
				socketExepCt++;
				i = 0;
				continue;
			}

			catch (Exception e) {
				if (ExepCt > 2) {
					_conn = null;
					throw e;
				}
				ExepCt++;
				i = 0;
				continue;

			}
		}
		return null;
	}

	/**
	 * urlEncode -
	 * 
	 * @param String
	 * @return String
	 */
	private String urlEncode(String sUrl) {
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

	public void clearConn() {

		if (headers != null)
			headers.clear();
		headers = null;

		requestMethod = null;

	}

}

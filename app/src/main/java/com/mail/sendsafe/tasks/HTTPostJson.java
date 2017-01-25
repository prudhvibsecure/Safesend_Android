package com.mail.sendsafe.tasks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.mail.sendsafe.R;
import com.mail.sendsafe.callbacks.IItemHandler;
import com.mail.sendsafe.common.Item;

import android.content.Context;
import android.os.AsyncTask;

public class HTTPostJson extends AsyncTask<String, Long, Integer> {

	private HttpURLConnection conn = null;

	private String requestUrl = null;

	private Item headers = null;

	private IItemHandler callback = null;

	private Context context = null;

	private String response;

	private GetConnection getConn = null;

	private int requestId = 0;

	private DataOutputStream outputStream = null;

	private String postData;

	private boolean multipart = true;

	private String contentType = "application/json";

	public void setHeaders(Item aItem) {
		this.headers = aItem;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public HTTPostJson(IItemHandler callback, Context context, String postData, int requestId) {
		this.callback = callback;
		this.context = context;
		this.postData = postData;
		this.requestId = requestId;
	}

	public void enableMutipart(boolean multipart) {
		this.multipart = multipart;
	}

	@Override
	protected Integer doInBackground(String... params) {

		requestUrl = params[0];

		InputStream fileInputStream = null;

		try {

			getConn = new GetConnection(context);
			getConn.setRequestMethod("POST");
			getConn.setContentType(contentType);
			getConn.setRequestHeaders(headers);

			conn = getConn.getHTTPConnection(requestUrl);

			if (conn == null)
				return 1;

			fileInputStream = new ByteArrayInputStream(postData.getBytes());

			if (fileInputStream != null) {
				outputStream = new DataOutputStream(conn.getOutputStream());

				byte[] data = new byte[1024];
				int bytesRead = 0, totalRead = 0;

				while ((bytesRead = fileInputStream.read(data)) != -1)

				{
					totalRead += bytesRead;
					int totalReadInKB = totalRead / 1024;
					publishProgress((long) totalReadInKB);
					outputStream.write(data, 0, bytesRead);
				}
			}

			int serverResponseCode = conn.getResponseCode();
			// String serverResponseMessage = conn.getResponseMessage();

			if (serverResponseCode == 200) {

				InputStream inputStream = conn.getInputStream();

				byte[] bytebuf = new byte[0x1000];
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				for (;;) {
					int len = inputStream.read(bytebuf);
					if (len < 0)
						break;
					baos.write(bytebuf, 0, len);
				}
				bytebuf = baos.toByteArray();
				response = new String(bytebuf, "UTF-8");

				response = response.replaceAll("\\\\/", "/");
				//response = response.toLowerCase(Locale.ENGLISH);

				try {

					if (inputStream != null)
						inputStream.close();

					inputStream = null;

				} catch (Exception e) {

				}

			} else {
				return 8;
			}
			return 0;

		} catch (MalformedURLException me) {
			return 4;
		}

		catch (ConnectException e) {
			return 3;
		}

		catch (SocketException se) {
			return 2;
		}

		catch (SocketTimeoutException stex) {
			return 2;
		}

		catch (Exception ex) {
			return 1;
		}

		finally {

			try {

				if (conn != null)
					conn.disconnect();
				conn = null;

				if (fileInputStream != null)
					fileInputStream.close();
				fileInputStream = null;

			} catch (Exception e) {

			}

			try {

				if (outputStream != null) {
					outputStream.flush();
					outputStream.close();
				}
				outputStream = null;

			} catch (Exception e) {

			}

			try {

				if (getConn != null)
					getConn.clearConn();

				getConn = null;

			} catch (Exception e) {

			}

			postData = "";
			postData = null;

		}
	}

	@Override
	protected void onPostExecute(Integer result) {

		if (result != 0) {
			if (result == 3)
				callback.onError(context.getString(R.string.snr1), requestId);
			if (result == 2)
				callback.onError(context.getString(R.string.sct), requestId);
			if (result == 6)
				callback.onError(context.getString(R.string.snr2), requestId);
			if (result == 4)
				callback.onError(context.getString(R.string.iurl), requestId);
			if (result == 1)
				callback.onError(context.getString(R.string.isres), requestId);
			if (result == 5)
				callback.onError(context.getString(R.string.nipcyns), requestId);
			if (result == 7)
				callback.onError(context.getString(R.string.snr3), requestId);
			if (result == 8)
				callback.onError(context.getString(R.string.ftu), requestId);

			return;
		}

		callback.onFinish(response, requestId);

	}

	@Override
	protected void onProgressUpdate(Long... values) {
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

}

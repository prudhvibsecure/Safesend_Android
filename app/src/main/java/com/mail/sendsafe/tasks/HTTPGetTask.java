package com.mail.sendsafe.tasks;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.mail.sendsafe.R;
import com.mail.sendsafe.callbacks.IRequestCallback;
import com.mail.sendsafe.common.Constants;
import com.mail.sendsafe.common.Item;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

public class HTTPGetTask extends AsyncTask<String, Long, Integer> {

	private String requestUrl = null;

	private HttpURLConnection conn = null;
	
	private InputStream inputStream = null;
	
	private IRequestCallback callback = null;
	
	private String mimeType = null;
	
	private Context context = null;
	
	private String downloadFileName = "";
	
	private Item item = null;

	boolean saveToFile = false;		
	
	private long length = 0;

	long mDownloadStartTime = 0;

	private boolean isAppStorage = false;

	private GetConnection getConn = null;
	
	public HTTPGetTask(Context context, IRequestCallback callback,
			boolean savetofile, String downloadname) {
		this.context = context;
		this.callback = callback;
		this.saveToFile = savetofile;
		this.downloadFileName = downloadname;
	}

	public void setHeaders(Item aItem) {
		item = aItem;
	}

	public void inAppStorage() {
		isAppStorage = true;
	}	

	@Override
	protected void onCancelled() {

		try {
			if (conn != null)
				conn.disconnect();

			conn = null;
			callback.onRequestCancelled(context.getString(R.string.yhcdr));

			File file = new File(Constants.DWLPATH + downloadFileName);

			if (file.exists()) {
				file.delete();
			}

		} catch (Exception e) {

		}

		super.onCancelled();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Integer doInBackground(String... params) {

		try {

			if (!isNetworkAvailable()) {
				return 5;
			}

			requestUrl = params[0];			

			getConn = new GetConnection(context);
			getConn.setRequestMethod("GET");
			getConn.setRequestHeaders(item);

			conn = getConn.getHTTPConnection(requestUrl);

			if (conn == null) {
				return 1;
			}

			mDownloadStartTime = System.currentTimeMillis();

			mimeType = conn.getContentType();

			String type = mimeType;

			inputStream = conn.getInputStream();

			length = conn.getContentLength();

			if (length == -1) {
				String leng = conn.getHeaderField("content-length");// content-filesize
				if (leng != null)
					length = Long.parseLong(leng);
			}

			if (type != null && saveToFile)
				if (type.contains("video/")
						|| type.contains("image/")
						|| type.contains("audio/")
						|| type.contains("videotone/")
						|| type.contains("application/pdf")
						|| type.contains("application/vnd.android.package-archive")
						|| type.contains("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
						|| type.contains("text/plain"))
					saveFile(mimeType, length);

				// type.contains("application/vnd.android.package-archive")
				else {

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
					bytebuf = null;
					if (response != null && response.contains("102")) {
						return 9;
					}
				}

			return 0;

		} catch (MalformedURLException me) {
			return 4;
		}

		catch (ConnectException e) {
			return 3;
		}

		catch (SocketException se) {
			return 6;
		}

		catch (SocketTimeoutException stex) {
			return 2;
		}

		catch (SizeNotMatched snm) {
			return 8;
		}

		catch (Exception ex) {
			return 7;
		} finally {
			if (inputStream != null)
				try {
					inputStream.close();
				} catch (IOException e) {
				}

			if (conn != null)
				conn.disconnect();
			conn = null;

			if (getConn != null)
				getConn.clearConn();

			getConn = null;

		}

	}

	protected void onPostExecute(Integer result) {

		try {

			if (result != 0) {
				if (result == 20)

					if (result == 3)
						callback.onRequestFailed(context
								.getString(R.string.snr1));
				if (result == 2)
					callback.onRequestFailed(context.getString(R.string.sct));
				if (result == 6)
					callback.onRequestFailed(context.getString(R.string.snr2));
				if (result == 4)
					callback.onRequestFailed(context.getString(R.string.iurl));
				if (result == 1)
					callback.onRequestFailed(context.getString(R.string.isres));
				if (result == 5)
					callback.onRequestFailed(context
							.getString(R.string.nipcyns));
				if (result == 7)
					callback.onRequestFailed(context.getString(R.string.snr3));
				if (result == 8)
					callback.onRequestFailed(context.getString(R.string.edcpda));
				if (result == 9)
					callback.onRequestFailed(context
							.getString(R.string.subscribe));

				File file = new File(Constants.DWLPATH + downloadFileName);
				if (file.exists()) {
					file.delete();
				}

				return;
			}

			if (!saveToFile && inputStream != null) {

				callback.onRequestComplete(inputStream, mimeType);
				return;

			} else if (saveToFile && mimeType != null) {

				/*
				 * if(downloadFileName.contains(".")) downloadFileName =
				 * downloadFileName.substring(0, downloadFileName.indexOf("."));
				 */

				callback.onRequestComplete(requestUrl + "###"
						+ downloadFileName, mimeType);
				return;
			}

			return;
		} catch (Exception e) {

		}
	}

	@Override
	protected void onProgressUpdate(Long... values) {
		callback.onRequestProgress(values);
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

	private void saveFile(String aMineType, long length) throws Exception {

		int bufferSize = 4096;

		byte[] buffer = new byte[bufferSize];
		int bytesRead = 0;
		long totalRead = 0;

		File f = new File(Constants.DWLPATH);
		if (!f.exists()) {
			f.mkdirs();
		}
		try {

			downloadFileName = downloadFileName.replaceAll("[^a-zA-Z0-9_.]+", "");

			FileOutputStream outStream = null;

			if (isAppStorage) {
				File mydir = context.getDir("Downloads", Context.MODE_PRIVATE);
				if (!mydir.isDirectory()) {
					f.mkdirs();
				}

				File outputFile = new File(mydir, downloadFileName);

				outStream = new FileOutputStream(outputFile);
			} else {
				outStream = new FileOutputStream(Constants.DWLPATH
						+ downloadFileName);
			}

			while ((bytesRead = inputStream.read(buffer, 0, bufferSize)) >= 0) {

				outStream.write(buffer, 0, bytesRead);

				totalRead += bytesRead;

				if (this.length > 0) {
					Long[] progress = new Long[5];
					progress[0] = (long) ((double) totalRead
							/ (double) this.length * 100.0);
					progress[1] = totalRead;
					progress[2] = this.length;

					double elapsedTimeSeconds = (System.currentTimeMillis() - mDownloadStartTime) / 1000.0;

					// Compute the avg speed up to now
					double bytesPerSecond = totalRead / elapsedTimeSeconds;

					// How many bytes left?
					long bytesRemaining = this.length - totalRead;

					double timeRemainingSeconds;

					if (bytesPerSecond > 0) {
						timeRemainingSeconds = bytesRemaining / bytesPerSecond;
					} else {
						// Infinity so set to -1
						timeRemainingSeconds = -1.0;
					}

					progress[3] = (long) (elapsedTimeSeconds * 1000);

					progress[4] = (long) (timeRemainingSeconds * 1000);

					publishProgress(progress);
				}

				if (this.isCancelled()) {
					if (conn != null)
						conn.disconnect();
					conn = null;
					break;
				}

			}

			buffer = null;
		} catch (Exception e) {
			throw e;
		}
	}

	class SizeNotMatched extends Exception {
		SizeNotMatched(String s) {
			super(s);
		}
	}

}
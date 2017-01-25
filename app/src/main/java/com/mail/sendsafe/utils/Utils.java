package com.mail.sendsafe.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mail.sendsafe.R;
import com.mail.sendsafe.common.AppPreferences;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

	private static Dialog dialog = null;

	/**
	 * urlEncode -
	 * 
	 * @param String
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

	public static Bitmap decodeBitmapFromFile(String filePath, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		final float totalPixels = width * height;
		final float totalReqPixelsCap = reqWidth * reqHeight * 2;
		while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
			inSampleSize++;
		}

		return inSampleSize;
	}

	public static void showProgress(String title, Context context) {
		dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.setCancelable(false);
		View view = View.inflate(context, R.layout.processing, null);

		((TextView) view.findViewById(R.id.progrtxt)).setText(title);

		dialog.setContentView(view);
		dialog.show();
	}

	public static void dismissProgress() {
		if (dialog != null)
			dialog.dismiss();
		dialog = null;
	}

	public static boolean checkSDCard(Context context) {

		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;

		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			Toast.makeText(context, R.string.sdcardmsg2, Toast.LENGTH_SHORT).show();
			return false;
		} else {
			Toast.makeText(context, R.string.sdcardmsg1, Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	public static JSONObject getContacts(Context context) {
		JSONArray array = new JSONArray();
		JSONObject json = new JSONObject();

		try {
			json.put("email", AppPreferences.getInstance(context).getFromStore("email"));
			json.put("data", array);

			ContentResolver cr = context.getContentResolver();
			Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

			if (cur.getCount() > 0) {
				while (cur.moveToNext()) {

					String contactId = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
					String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					if (name == null) {
						name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_ALTERNATIVE));
					}

					JSONObject contact = new JSONObject();
					contact.put("fname", name);
					// contact.put("contactId", contactId);

					// Create query to use CommonDataKinds classes to fetch
					// emails
					Cursor emails = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
							ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);

					/*
					 * //You can use all columns defined for
					 * ContactsContract.Data // Query to get phone numbers by
					 * directly call data table column
					 * 
					 * Cursor c = getContentResolver().query(Data.CONTENT_URI,
					 * new String[] {Data._ID, Phone.NUMBER, Phone.TYPE,
					 * Phone.LABEL}, Data.CONTACT_ID + "=?" + " AND " +
					 * Data.MIMETYPE + "= + Phone.CONTENT_ITEM_TYPE + ", new
					 * String[] {String.valueOf(contactId)}, null);
					 */

					while (emails.moveToNext()) {

						// This would allow you get several email addresses
						String emailAddress = emails
								.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
						if (!TextUtils.isEmpty(emailAddress)) {
							contact.put("email", emailAddress);
						}
						// Log.e("email==>", emailAddress);

					}

					emails.close();
					if (contact.has("email")) {
						array.put(contact);
					}
				}

			}

			cur.close();

		} catch (Exception e) {

		}

		return json;
	}

	public static String getDeviceId(Context context) {
		String deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		return deviceId;
	}

}
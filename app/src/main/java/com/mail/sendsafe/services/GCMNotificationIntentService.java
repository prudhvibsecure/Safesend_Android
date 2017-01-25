package com.mail.sendsafe.services;

import org.json.JSONObject;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mail.sendsafe.LoginInWithPin;
import com.mail.sendsafe.R;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class GCMNotificationIntentService extends IntentService {

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}

	public static final String TAG = "GCMNotificationIntentService";

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				sendNotification("Deleted messages on server: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				String msg = intent.getStringExtra("message");

				// for (int i = 0; i < 3; i++) {
				//
				// try {
				// Thread.sleep(5000);
				// } catch (InterruptedException e) {
				// }
				//
				// }
				sendNotification(msg);

			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String msg) {
		int numMessages= 0;
		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, LoginInWithPin.class), 0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			int color = 0x008000;
			mBuilder.setColor(color);
			mBuilder.setSmallIcon(R.drawable.small_pushicon);
		} else {
			mBuilder.setSmallIcon(R.drawable.ic_launcher);
		}
		try {
			JSONObject json = new JSONObject(msg);
			mBuilder.setContentTitle(json.optString("title"))
					.setStyle(new NotificationCompat.BigTextStyle().bigText(json.optString("msg")))
					.setContentText(json.optString("msg"));
		} catch (Exception e) {
			mBuilder.setContentTitle("Sendsafe").setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
					.setContentText(msg).setNumber(++numMessages);
		}
		mBuilder.setContentIntent(contentIntent);
		mBuilder.setDefaults(Notification.DEFAULT_SOUND);
		mBuilder.setAutoCancel(true);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

		// mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);

	}
}

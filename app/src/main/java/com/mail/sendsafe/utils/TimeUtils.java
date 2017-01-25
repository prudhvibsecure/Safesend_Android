package com.mail.sendsafe.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {

	public static String getDeviceDateTime(String dtFormat) {

		Calendar c = Calendar.getInstance();

		c.setTimeInMillis(System.currentTimeMillis());

		Date date = new Date();

		SimpleDateFormat format = new SimpleDateFormat(dtFormat);

		// Log.e("-=-=-=-=-=-=-=-=-=-=", format.format(date)+"");

		return format.format(date);

	}

	public static String getTimeZone() {
		TimeZone timeZone = TimeZone.getDefault();

		// Log.e("-=-=-=-=-=-=-=-=-=-=", timeZone.getID()+"");

		return /* timeZone.getDisplayName()+" "+ */timeZone.getID();
	}

}

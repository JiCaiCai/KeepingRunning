package com.project.keepingrunning.frame;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
	/**
	 * format time
	 * @param time
	 * @return
	 */
	public static String formatDateOutPut(String time) {
    	SimpleDateFormat df = new SimpleDateFormat(Constant.DATE_FORMAT);
    	try {
			Date startDate = df.parse(time);
			return df.format(startDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return "";
    }
}

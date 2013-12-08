package com.project.keepingrunning.frame;

import com.project.keepingrunning.object.RunActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class RunActivityComparator implements Comparator<RunActivity> {

	@Override
	public int compare(RunActivity ra1, RunActivity ra2) {
		// TODO Auto-generated method stub
		SimpleDateFormat df = new SimpleDateFormat(Constant.DATE_FORMAT);
		try {
			Date startDate1 = df.parse(ra1.getStartTime());
			Date startDate2 = df.parse(ra2.getStartTime());
			return startDate2.compareTo(startDate1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}

package com.project.keepingrunning.frame;

import com.project.keepingrunning.object.ActivityPath;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class AcitvityPathComparator implements Comparator<ActivityPath> {

	@Override
	public int compare(ActivityPath lhs, ActivityPath rhs) {
		SimpleDateFormat df = new SimpleDateFormat(Constant.DATE_FORMAT);
		try {
			Date recordDate1 = df.parse(lhs.getRecordTime());
			Date recordDate2 = df.parse(rhs.getRecordTime());
			return recordDate1.compareTo(recordDate2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}

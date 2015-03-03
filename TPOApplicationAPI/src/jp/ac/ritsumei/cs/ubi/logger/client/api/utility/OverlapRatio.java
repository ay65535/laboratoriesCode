/*
 * Copyright (C) 2008-2012 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.logger.client.api.utility;

import java.util.Collection;
import java.util.Map;

public class OverlapRatio {
	public static double between(Map<String,Integer> one, Map<String,Integer> another) {
		Collection<String> c1 = one.keySet(), c2 = another.keySet();
		if(c1.size() == 0 && c2.size() == 0) return -1;
		if((c1.size() == 0 && c2.size() == 1) || (c1.size() == 1 && c2.size() == 0)){ return -1;}
		if (c1.size() == 0 || c2.size() == 0) return 0;
		int count = 0;
		for (String bssid : c1) {
			if (c2.contains(bssid)) count++;
		}
		return count / (double) (c1.size() + c2.size() - count);
	}
}

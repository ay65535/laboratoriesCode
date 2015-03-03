/*
 * Copyright (C) 2008-2012 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.logger.client.api.sensors;

import java.util.ArrayList;
import java.util.Collection;

public class MyWiFiLog {
	private long time;
	private ArrayList<MyAp> aps;
	public MyWiFiLog( long time ){
		this.time = time;
		aps = new ArrayList<MyAp>();
	}
	public long getTime() {
		return time;
	}
	public ArrayList<MyAp> getAps() {
		return aps;
	}
	public Collection<String> keySetB(){
		ArrayList<String> keySet = new ArrayList<String>();		
		for( MyAp temp : aps ){
			keySet.add( temp.getBssid() );
		}
		return keySet;
	}
	public Collection<String> keySetE(){
		ArrayList<String> keySet = new ArrayList<String>();		
		for( MyAp temp : aps ){
			keySet.add( temp.getEssid() );
		}
		return keySet;
	}
	public void addAp( MyAp ap ) {
		aps.add(ap);
	}
	public String toString(){
		String returnString = "{WiFi(";
		for( MyAp temp : aps ){
			returnString += temp.toString();
		}
		return  returnString + ")}";
	}
}

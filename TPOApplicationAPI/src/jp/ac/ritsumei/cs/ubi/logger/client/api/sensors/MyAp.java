/*
 * Copyright (C) 2008-2012 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.logger.client.api.sensors;

public class MyAp {
	private String bssid;
	private String essid;
	private int rssi;
	public MyAp(String bssid, String essid, int rssi){
		this.bssid = bssid;
		this.essid = essid;
		this.rssi = rssi;
	}
	public String getBssid() {
		return bssid;
	}
	public void setBssid(String bssid) {
		this.bssid = bssid;
	}
	public String getEssid() {
		return essid;
	}
	public void setEssid(String essid) {
		this.essid = essid;
	}
	public int getRssi() {
		return rssi;
	}
	public void setRssi(int rssi) {
		this.rssi = rssi;
	}
	public String toString(){
		return "{AP(" + bssid + "," + essid + "," + rssi + ")}";
	}
}

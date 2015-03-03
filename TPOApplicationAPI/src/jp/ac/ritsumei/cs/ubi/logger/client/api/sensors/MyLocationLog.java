/*
 * Copyright (C) 2008-2012 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.logger.client.api.sensors;

/**
 * @author sacchin
 */
public class MyLocationLog {
	protected long time;
	private double lat;
	private double lng;
	private double speed;
	private int accuracy;
	private int satellite;
	public MyLocationLog( long time, double lat, double lng, double speed, int accuracy, int satellite ){
		this.time = time;
		this.lat = lat;
		this.lng = lng;
		this.speed = speed;
		this.accuracy = accuracy;
		this.satellite = satellite;
	}
	public long getTime(){
		return time;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public double getSpeed() {
		return speed;
	}
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	public int getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(int accuracy) {
		this.accuracy = accuracy;
	}
	public int getSatellite() {
		return satellite;
	}
	public void setSatellite(int satellite) {
		this.satellite = satellite;
	}
	public String toString(){
		return "{Location(" + lat + "," + lng + "," + speed + "," + accuracy + "," + satellite + ")}";
	}
}

package jp.ac.ritsumei.cs.ubi.zukky.BRM.kubiwa;

import java.io.Serializable;
import java.sql.Timestamp;

public class Gps implements Serializable{

	
	private static final long serialVersionUID = 1L;
	
	private Timestamp time;
	private int devId;
	private double lat;
	private double lng;
	private float acc;
	private float speed;
	private Timestamp lTime;
	
	public Gps(double lat, double lng){
		this.setLat(lat);
		this.setLng(lng);
	}
	
	public Gps(){
		
		
	}
	
	public Timestamp getTime() {
		return time;
	}
	public void setTime(Timestamp time) {
		this.time = time;
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
	public float getAcc() {
		return acc;
	}
	public void setAcc(float acc) {
		this.acc = acc;
	}
	public float getSpeed() {
		return speed;
	}
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	public int getDevId() {
		return devId;
	}
	public void setDevId(int devId) {
		this.devId = devId;
	}
	public Timestamp getlTime() {
		return lTime;
	}
	public void setlTime(Timestamp lTime) {
		this.lTime = lTime;
	}
	
	
}

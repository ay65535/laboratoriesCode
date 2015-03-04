package jp.ac.ritsumei.cs.ubi.zukky.BRM.kubiwa;

public class Device {
	
	private int devId;
	private int userid;
	private int devTypeId;
	private String devName;
	private String picture;
	private String color;
	private int active;
	private String bthName;
	private String bthAddr;
	
	
	public int getDevId() {
		return devId;
	}
	public void setDevId(int devId) {
		this.devId = devId;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userId) {
		this.userid = userId;
	}
	public int getDevTypeId() {
		return devTypeId;
	}
	public void setDevTypeId(int deviceTypeId) {
		this.devTypeId = deviceTypeId;
	}
	public String getDevName() {
		return devName;
	}
	public void setDevName(String devName) {
		this.devName = devName;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public int getActive() {
		return active;
	}
	public void setActive(int active) {
		this.active = active;
	}
	public String getBthName() {
		return bthName;
	}
	public void setBthName(String bthName) {
		this.bthName = bthName;
	}
	public String getBthAddr() {
		return bthAddr;
	}
	public void setBthAddr(String bthAddr) {
		this.bthAddr = bthAddr;
	}
}

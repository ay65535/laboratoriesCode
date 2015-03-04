package jp.ac.ritsumei.cs.ubi.zukky.BRM.hubenyVelocity;

import java.sql.Timestamp;

public class HubenyVelocity {

	private Timestamp startTime;
	private Timestamp endTime;
	private double velocity;
	
	public HubenyVelocity() {
	}
	
	public HubenyVelocity(double velocity){
		this.velocity = velocity;
	}
	
	public Timestamp getStartTime() {
		return startTime;
	}
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	public Timestamp getEndTime() {
		return endTime;
	}
	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}
	public double getVelocity() {
		return velocity;
	}
	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}
}

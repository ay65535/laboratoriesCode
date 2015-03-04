package jp.ac.ritsumei.cs.ubi.zukky.BRM.transport;

import java.sql.Timestamp;


public class Transportation {

	private TransportationType type = TransportationType.UNKNOWN;
	private Timestamp startTime;
	private Timestamp endTime;
	
	public enum TransportationType {
		WALKING, STAYING, BUS, BYCYECLE, TRAIN, UNKNOWN;
	}
	
	public Transportation(Transportation ts){
		this.startTime = ts.getStartTime();
		this.endTime = ts.getEndTime();
		this.type = ts.getType();
	}
	
	public Transportation(){
		
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

	public TransportationType getType() {
		return type;
	}

	public void setType(TransportationType type) {
		this.type = type;
	}

}

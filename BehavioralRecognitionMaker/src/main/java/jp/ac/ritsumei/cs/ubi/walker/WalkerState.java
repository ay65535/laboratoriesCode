/**
 * Copyright (C) 2008-2012 Nishio Laboratory All Rights Reserved
 */
package jp.ac.ritsumei.cs.ubi.walker;

import java.util.ArrayList;


/**
 * This class represents states of walkers.
 * 
 * @author Ubiquitous Computing and Networking Laboratory
 */
public class WalkerState {

	public enum Type {
		STANDSTILL, WALKING, ASCENDING, DESCENDING;
	}

	Type type;

	/**
	 * The time in milliseconds when this state started.
	 */
	long startTime;

	/**
	 * The time in milliseconds when this state ended.
	 */
	long endTime;

	/**
	 * The number of steps the walker walked during this state.
	 */
	int steps;
	
	/**
	 * 
	 */
	int moveFloor;
	
	
	int wifiId;
	
	/**
	 * The unique id of staying point.
	 */
	static int id = 0;
	
	int pointId;
	
	ArrayList<Integer> transitionWifiId =
		new ArrayList<Integer>();
	

	ArrayList<ElevatorState> evState =
		new ArrayList<ElevatorState>();
	


	WalkerState(Type type, long startTime, long endTime, int steps) {
		this.type = type;
		this.startTime = startTime;
		this.endTime = endTime;
		this.steps = steps;
		if(type.equals(WalkerState.Type.STANDSTILL)){
			id ++;
			pointId = id;
		}
	}

	/**
	 * Returns the type (standstill, walking, etc) of this state.
	 * 
	 * @return the type of this state
	 */
	public Type getType() {
		return this.type;
	}
	
	public void setId(int id){
		this.wifiId = id;
	}
	
	public int getId(){
		return this.pointId;
	}
	
	public void setPointId(int id){
		this.pointId = id;
	}
	/**
	 * Returns the time in milliseconds when this state started.
	 * 
	 * @return the number of milliseconds since the midnight of Jan 1, 1970
	 */
	public long getStartTime() {
		return this.startTime;
	}

	/**
	 * Returns the time in milliseconds when this state ended.
	 * 
	 * @return the number of milliseconds since the midnight of Jan 1, 1970
	 */
	public long getEndTime() {
		return this.endTime;
	}
	
	public int getSteps() {
		return this.steps;
	}

	void setType(Type type) {
		this.type = type;
	}
}

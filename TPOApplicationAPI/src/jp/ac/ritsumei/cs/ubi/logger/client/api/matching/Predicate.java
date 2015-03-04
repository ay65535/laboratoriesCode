/*
 * Copyright (C) 2008-2012 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.logger.client.api.matching;

import jp.ac.ritsumei.cs.ubi.logger.client.api.sensors.SensorData;
import android.os.Parcelable;

/**
 * this is a abstract class of Predicate.
 * all Compare or Logic class must extend this.
 * @author sacchin
 */
public abstract class Predicate implements Parcelable{
	
	/**
	 * this predicate's parent operation
	 */
	protected LogicOperation parent;

	/**
	 * latest Matching result
	 */
	protected boolean latestResult = false;
	
	/**
	 * the number of times of event detection
	 */
	protected int numberOfDetection = 1;
	
	/**
	 * get latest Matching result
	 * @return if latest Matching succeed, return true
	 */
	public boolean getLatestResult(){
		return latestResult;
	}
	
	/**
	 * get root's latest Matching result
	 * @return root of this predicate has latest Matching result and return it
	 */
	public boolean getRootResult(){
		if( parent!=null ){
			if( evaluate() ){
				return parent.getRootResult();
			}else{
				return false;
			}
		}else{
			return evaluate();
		}
	}
	
	/**
	 * get root's latest Matching result
	 * @param bool 
	 * @return if bool is true, return the root's latest Matching result. if bool is false, return the this.evaluate().
	 */
	public boolean getRootResult( boolean bool ){
		if( parent!=null ){
			if( bool ){
				return parent.getRootResult();
			}else{
				return false;
			}
		}else{
			return evaluate();
		}
	}
	
	/**
	 * get root operation
	 * @return
	 */
	public Predicate getRootOperation(){
		if( parent!=null ){
			return parent.getRootOperation();
		}else{
			return this;
		}
	}

	/**
	 * get the number of times of event detection
	 * @return
	 */
	public int getNumberOfDetection() {
		return numberOfDetection;
	}

	/**
	 * set the number of times of event detection
	 * @param numberOfDetection
	 */
	public void setNumberOfDetection(int numberOfDetection) {
		this.numberOfDetection = numberOfDetection;
	}

	public abstract boolean evaluate();
	public abstract boolean evaluate( SensorData latest );
	
	/**
	 * return String "[sensorName].[attribute].[columQualifier]"
	 */
	public abstract String getKey();
	
	/**
	 * return calculater
	 * Ex : "=",">","<"
	 */
	public abstract int getCalculater();
	
	/**
	 * return attributeName
	 * Ex : "OverlapRatio","Variation","StepDetecter"
	 */
	public abstract int getAttributeName();

	/**
	 * return valueName
	 * Ex : "Lat","Lng","Accuracy","Satellite","Speed","Bssid","Essid","Rssi","LatLng",
	 * "X-axis","Y-axis","Z-axis","Vector","OverlapRatio","AccelerometerVariation","StepDetecter"
	 */
	public abstract int getValueName();
	
	/**
	 * return attribute
	 * Ex : "Location","WiFi","Accelerometer"
	 */
	public abstract int getSensorName();
	
	/**
	 * return MatchingConstants.COMPARE or MatchingConstants.LOGIC
	 */
	public abstract int getOperationType();
	public abstract String toString();	
	public abstract void setParent( LogicOperation parent );
}

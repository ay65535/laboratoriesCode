/**
 * Copyright (C) 2008-2012 Nishio Laboratory All Rights Reserved
 */
package jp.ac.ritsumei.cs.ubi.walker;

/**
 * This class represents a sample of accelerometer values.
 * 
 * @author Ubiquitous Computing and Networking Laboratory
 */
class SensorEvent {

	/**
	 * Acceleration on the x-axis in SI units (m/s^2).
	 */
	float x;

	/**
	 * Acceleration on the y-axis in SI units (m/s^2).
	 */
	float y;

	/**
	 * Acceleration on the z-axis in SI units (m/s^2).
	 */
	float z;

	/**
	 * Time stamp in milliseconds.
	 */
	long time;

	/**
	 * Time stamp in nanoseconds.
	 */
	long nanos;
	
	/**
	 * Time stamp in second.
	 */
	float second;
	
	boolean isFirstLog = false;
	
	boolean isLastLog = false;
	
	SensorEvent(float x, float y, float z, long time, long nanos) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.time = time;
		this.nanos = nanos;
		this.second = time / 1000;
	}
	SensorEvent(float second, float x, float y, float z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.time = (long) (second * 1000);
		this.nanos = (long) (second * 1000000000);
		this.second = second;
	}
	
	SensorEvent(float x, float y, float z, long time, long nanos, boolean isLastLog) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.time = time;
		this.nanos = nanos;
		this.second = time / 1000;
		this.isLastLog = isLastLog;
	}
	
	SensorEvent(boolean isFirstLog, float x, float y, float z, long time, long nanos) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.time = time;
		this.nanos = nanos;
		this.second = time / 1000;
		this.isFirstLog = isFirstLog;
	}
	
	@Override
	public String toString() {
		return "[" + x + ", " + y + ", " + z + "]";
	}

}

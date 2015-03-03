/*
 * Copyright (C) 2008-2012 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.logger.client.api.sensors;

public class MyAccData {
	private long time;
	private double x;
	private double y;
	private double z;
	private double vector;
	public MyAccData( long time, double x, double y, double z ){
		this.time = time;
		this.x = x;
		this.y = y;
		this.z = z;
		this.vector = Math.sqrt(x*x + y*y + z*z);
	}
	public long getTime() {
		return time;
	}	
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public double getZ() {
		return z;
	}
	public double getVector() {
		return vector;
	}
	public String toString(){
		return "{Acc(" + x + "," + y + "," + z + "," + vector + ")}";
	}
}

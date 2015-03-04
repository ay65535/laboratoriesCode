/**
 * Copyright (C) 2008-2012 Nishio Laboratory All Rights Reserved
 */
package jp.ac.ritsumei.cs.ubi.walker;


/**
 * Interface for objects to listen for step events.
 */
interface StepListener {

	/**
	 * Invoked when a step is detected.
	 * 
	 * @param time	the time in milliseconds
	 */
	public void onStep(long time);
	
	public void firstLog(long time);
	
	public void lastLog();

}

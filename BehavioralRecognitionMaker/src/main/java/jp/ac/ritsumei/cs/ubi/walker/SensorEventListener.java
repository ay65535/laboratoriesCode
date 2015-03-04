/**
 * Copyright (C) 2008-2012 Nishio Laboratory All Rights Reserved
 */
package jp.ac.ritsumei.cs.ubi.walker;

/**
 * Interface for objects to receive sensor events.
 * 
 * @author Ubiquitous Computing and Networking Laboratory
 */
interface SensorEventListener {

	/**
	 * Invoked when sensor values have changed.
	 * 
	 * @param event	the new sensor values
	 */
	void onSensorChanged(SensorEvent event);

}

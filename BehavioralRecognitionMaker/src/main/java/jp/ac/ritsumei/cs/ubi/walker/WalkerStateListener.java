/**
 * Copyright (C) 2008-2012 Nishio Laboratory All Rights Reserved
 */
package jp.ac.ritsumei.cs.ubi.walker;

/**
 * Interface for objects to listen for walker state change events.
 * 
 * @author Ubiquitous Computing and Networking Laboratory
 */
interface WalkerStateListener {

	/**
	 * Invoked when walker state has changed.
	 * 
	 * @param state	the last state of the walker
	 */
	void onWalkerStateChanged(WalkerState state);

}

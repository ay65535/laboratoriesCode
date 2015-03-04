/**
 * Copyright (C) 2008-2012 Nishio Laboratory All Rights Reserved
 */
package jp.ac.ritsumei.cs.ubi.walker;

import java.util.ArrayList;
import java.util.List;

/**
 * This class listens for step events and tells when the walker is standstill or
 * walking.
 * 
 * @author Ubiquitous Computing and Networking Laboratory
 */
 public class WalkerStateDetector implements StepListener {

	/**
	 * The maximum possible number of milliseconds between steps while walking.
	 */
	private double PITCH_MAX;

	final List<WalkerStateListener> listeners =
		new ArrayList<WalkerStateListener>(1);
	final List<WalkerState> cluster =
		new ArrayList<WalkerState>(1);
	

	/**
	 * The time in milliseconds of the first step of the last walk.
	 */
	long firstStep = -1;

	/**
	 * The time in milliseconds of the last step.
	 */
	long lastStep = -1;

	/**
	 * The number of steps after {@link #firstStep}.
	 */
	int steps = 0;

	long firstLog;
	
	boolean isLastLog = false;

	boolean isFirstState = true;

	/**
	 * Adds the specified {@code WalkerStateListener} to receive notifications
	 * when the walker's state has changed.
	 * 
	 * @param l	the listener to add
	 * @throws NullPointerException if {@code l} is {@code null}
	 */
	void addWalkerStateListener(WalkerStateListener l) {
		if (l == null) {
			throw new NullPointerException();
		}
		listeners.add(l);
	}
	protected void setTimeThresh(int sec){
		this.PITCH_MAX = sec * 1000;
	}
	
	public List<WalkerState> getCluster(){
		return cluster;	
	}
	
	public void firstLog(long time) {
		this.firstLog = time;
	}
	
	 public void lastLog(){
		this.isLastLog = true;
	}
	 int i = 0;
	public void onStep(long time) {
		steps++;
		i++;
		//System.out.println("counter:"+i);
		if(isFirstState){
//			System.out.println("aaaaaaa");
		//	System.out.println("start:"+firstLog+ " end:"+time);
			fireWalkerStateChanged(new WalkerState(
					WalkerState.Type.STANDSTILL, firstLog, time, 0));
			firstStep = time;
			isFirstState = false;
			lastStep = time;
		}

		if(isLastLog){
			//System.out.println("sstart:"+lastStep+ " end:"+time);
		
			fireWalkerStateChanged(new WalkerState(
					WalkerState.Type.WALKING, firstStep, lastStep, steps));
			
			fireWalkerStateChanged(new WalkerState(
					WalkerState.Type.STANDSTILL, lastStep, time, 0));
		}

		if (lastStep > 0 && PITCH_MAX < time - lastStep && !isLastLog) {
			//System.out.println("ssstart:"+lastStep+ " end:"+time);
			//if (firstStep > 0) {
			fireWalkerStateChanged(new WalkerState(
					WalkerState.Type.WALKING, firstStep, lastStep, steps));
			//}
			
			fireWalkerStateChanged(new WalkerState(
					WalkerState.Type.STANDSTILL, lastStep, time, 0));
			steps = 0;
			firstStep = time;
		}

		//steps++;
		lastStep = time;
		//System.out.println("lastStep:"+lastStep);
		
	}

	protected void fireWalkerStateChanged(WalkerState state) {
		cluster.add(state);
		for (WalkerStateListener l : listeners) {
			l.onWalkerStateChanged(state);
		}
	}

}

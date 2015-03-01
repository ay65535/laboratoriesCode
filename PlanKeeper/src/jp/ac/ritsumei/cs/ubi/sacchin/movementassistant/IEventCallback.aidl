/*
 * Copyright (C) 2008-2012 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant;

interface IEventCallback{
	void leaveFromDropInsite(in String type, in String name);
	void arrivedAtDropInsite(in String type, in String name);
	void arrivedAtTransferPoint(in String type, in String name);
	void arrivedAtCheckPoint(in String type, in String name);
}
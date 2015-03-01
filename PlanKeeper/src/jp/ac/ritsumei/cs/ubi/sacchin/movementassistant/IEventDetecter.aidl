/*
 * Copyright (C) 2008-2012 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant;

import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.IEventCallback;

interface IEventDetecter {
	void setIntentFilter(String id);
	void removeIntentFilter(String id);
	void registerEventCallback(IEventCallback callback);
	void unregisterEventCallback(IEventCallback callback);
}
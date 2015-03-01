/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils;

import java.lang.ref.WeakReference;

import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.EventNotificationActivity;
import android.os.Handler;
import android.os.Message;

/**
 * this is a handler which receive event from EventChatcher.
 * [EventChatcher -> EventNotificationActivity.IEventCallback -> this.handleMessage()]
 * @author sacchin
 *
 */
public class ArrivedHandler extends Handler {
	public static final int ARRIVED_AT_DROPINSITE = 0;
	public static final int LEAVE_FROM_DROPINSITE = 1;
	public static final int ARRIVED_AT_CHECKPOINT = 2;
	public static final int ARRIVED_AT_TRANSFERPOINT = 3;
	
    private final WeakReference<EventNotificationActivity> mActivity;

    /**
	 * this is constructor.
	 * @param activity EventNotificationActivity.this
     */
    public ArrivedHandler(EventNotificationActivity activity) {
        mActivity = new WeakReference<EventNotificationActivity>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
    	EventNotificationActivity activity = mActivity.get();
        if (activity == null){
            return;
        }
        
		int identifier = msg.what;
		String message = String.valueOf(msg.obj);
		
		switch (identifier) {
		case ARRIVED_AT_DROPINSITE:
			activity.downloadDailyPlan(Long.parseLong(message.split(":")[1]));
			break;
			
		case LEAVE_FROM_DROPINSITE:
			activity.startPlanCheck(Long.parseLong(message.split(":")[1]));
			break;

		case ARRIVED_AT_TRANSFERPOINT:
			activity.selectTransportaion(message);
			break;
			
		case ARRIVED_AT_CHECKPOINT:
			activity.estimateArrivedTime(message);
			break;
		default:
			break;
		}
    }
}


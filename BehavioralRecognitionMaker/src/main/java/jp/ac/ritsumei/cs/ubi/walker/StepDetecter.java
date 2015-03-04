/**
 * Copyright (C) 2008-2012 Nishio Laboratory All Rights Reserved
 */
package jp.ac.ritsumei.cs.ubi.walker;

import java.util.ArrayList;
import java.util.List;


/**
 * Detects steps and notifies {@link StepListener}s.
 * <p>
 * The codes are taken from <a href="http://code.google.com/p/pedometer/">
 * {@code name.bagi.levente.pedometer.StepDetector}</a> with slight
 * modifications.
 * 
 * @see <a href="http://code.google.com/p/pedometer/">Pedometer - Android app</a>
 */
class StepDetecter implements SensorEventListener {

	static final float STANDARD_GRAVITY = 9.80665f;

	static final float Y_OFFSET = 240.0f;

	static final float SCALE = -(Y_OFFSET * (1.0f / (STANDARD_GRAVITY * 2)));


	final List<StepListener> mStepListeners = new ArrayList<StepListener>(1);

	/*
	 * very high 10
	 * high      20
	 * medium    30
	 * low       40
	 * very low  50
	 */

	int mLimit; //41 nexus s

	int lastDirection = 0;

	int lastMatch = 0;

	float lastV = 0;

	float lastDiff = 0;

	float[] lastExtremes = new float[] { 0, 0 };

	public StepDetecter(int thresh){
		this.mLimit = thresh;
	}
	public void addStepListener(StepListener l) {
		mStepListeners.add(l);
	}
	float x = 0,y=0,z=0;
	float alpha = 0.015f;//0.015
	public void onSensorChanged(SensorEvent event) {

		if(event.isFirstLog){
			for (StepListener stepListener : mStepListeners) {
				stepListener.firstLog(event.time);
			}
		}
		if(event.isLastLog){
			for (StepListener stepListener : mStepListeners) {
				stepListener.lastLog();
				stepListener.onStep(event.time);		
			}
			return;
		}

		//x = alpha * event.x + (1.0f - alpha) * x;
		//y = alpha * event.y + (1.0f - alpha) * y;
		//z = alpha * event.z + (1.0f - alpha) * z;		

		float v = Y_OFFSET + SCALE * (event.x + event.y + event.z) / 3.0f;

		int direction = Float.compare(v, lastV);
		if (direction * lastDirection < 0) {
			// Direction changed
			int extType = (direction > 0 ? 0 : 1); // minimum or maximum?
			lastExtremes[extType] = lastV;
			float diff = Math.abs(lastExtremes[extType]
			                                   - lastExtremes[1 - extType]);

			if (diff > mLimit) {
				boolean isAlmostAsLargeAsPrevious = (diff > (lastDiff * 2 / 3));
				boolean isPreviousLargeEnough = (lastDiff > (diff / 3));
				boolean isNotContra = (lastMatch != 1 - extType);

				if (isAlmostAsLargeAsPrevious
						&& isPreviousLargeEnough && isNotContra) {
					for (StepListener stepListener : mStepListeners) {

						stepListener.onStep(event.time);
					}
					lastMatch = extType;
				} else {
					lastMatch = -1;
				}
			}
			lastDiff = diff;
		}		
		lastDirection = direction;
		lastV = v;
	}
}
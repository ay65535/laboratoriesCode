/**
 * Copyright (C) 2008-2012 Nishio Laboratory All Rights Reserved
 */
package jp.ac.ritsumei.cs.ubi.walker;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class detects the elevator DESCENDIG or ASCENDING.
 * 
 * @author dany
 */
class ElevatorWalkerStateDetector extends WalkerStateDetector implements
SensorEventListener {
	
	
	                                      //desire  DEV2      //desire colearning
	static final double UP_MAX = 10.4;  //11.0  //10.8      10.25     10.65  11.18

	static final double UP_MIN = 9.6;  //10.40   //10.45     10.0     10.35  10.33

	static final double DOWN_MAX = 9.6; //9.8   //9.76      9.5     10.0  10.03

	static final double DOWN_MIN = 8.8;  //9.42  //9.42      9.2     9.75  9.18

	static final double MINIMA_TIME_THRESH = 1.5 * 1000000000; //default 1.5

	static final double MAXIMA_TIME_THRESH = 16.0 * 1000000000;

	
	
	static final double GRAVITY = 9.58;//9.58 //10.18(desire)

	static final double V_ABS_THRESH_MIN = 0.15; //0.15

	static final double V_ABS_THRESH_MAX = 1.0;

	static double lastAbs = 0;

	static double lastUpAbs = 9.58;//10.18(desire)

	static double lastDownAbs = 9.58;//10.18(desire)

	static final double ALLOW_DIFF_THRESH = 0.3;




	static int flag =0;
	static int flag2 =0;
	static int last;


	private long startTime;
	float alpha = 0.015f; //0.015

	final Queue<SensorEvent> buf = new ArrayDeque<SensorEvent>();
	final Queue<Double> q = new LinkedList<Double>();
	public void onSensorChanged(SensorEvent event) {
		buf.offer(event);
	}
	public ElevatorWalkerStateDetector(int sec) {
		super.setTimeThresh(sec);
	}
	@Override
	protected void fireWalkerStateChanged(WalkerState state) {
		//System.out.println("beforessssstttttttt");
		if (state.getType() == WalkerState.Type.STANDSTILL) {
			
		//	long r = state.endTime-state.startTime;

			//System.out.println("state.endTime-state.startTime  "+r);
			if(state.endTime-state.startTime < 600000){
				state = detectElevator(state);
			}		
			buf.clear();
		}
		super.fireWalkerStateChanged(state);
	}
	long calcTimeWidth(long startTime, long endTime){
		long timeWidth = endTime - startTime;
		return timeWidth;	
	}


	Double CalcDispersion(Queue<Double> q){
		double sum = 0;
		double size = q.size();
		double ave;
		double sumDiff = 0;
		for(Double d : q){
			sum += d;
		}
		//System.out.println("sum: "+sum);
		ave = sum / size;
		//System.out.println("ave: "+ave);
		for(Double d : q){
			sumDiff += (Math.abs(ave - d)) * (Math.abs(ave - d));
		}	
		//System.out.println("sumDiff: "+sumDiff);
		return sumDiff / size;
	}

	WalkerState detectElevator(WalkerState state) {
		float x = 0, y = 0, z = 0;
		double lastV = 0; //lastVMaxima = 0, //lastVMinima = 0;
		double lastNanos = 0, lastNanosMaxima = 0, lastNanosMinima = 0;
		int lastDirection = 0;
		boolean up = false, down = false;

		for (SensorEvent event; (event = buf.poll()) != null;) {
			if (event.time < state.startTime) {
				continue;			
			}
			// Low-pass filters
			x = alpha * event.x + (1.0f - alpha) * x;
			y = alpha * event.y + (1.0f - alpha) * y;
			z = alpha * event.z + (1.0f - alpha) * z;
			double v = Math.sqrt(x * x + y * y + z * z);
			//System.out.println(v);
			if(q.size()==100){
				q.poll();
			}
		//	int a = 0;
			

			q.offer(v);	
			//System.out.println(v);
			int direction = Double.compare(v, lastV);			
			if(direction == 0){
				direction = lastDirection;
			}

			if (direction * lastDirection < 0) {
				// Direction changed
				if (direction < 0) {
					// Local maxima
					lastNanosMaxima = lastNanos;

				//	boolean isInRange  = (MINIMA_TIME_THRESH < lastNanosMaxima - lastNanosMinima ? 
				//			(lastNanosMaxima - lastNanosMinima < MAXIMA_TIME_THRESH ? true : false) : false);

					if (MINIMA_TIME_THRESH < lastNanosMaxima - lastNanosMinima) {
						//lastVMaxima = v;
						if (UP_MIN <= v && v <= UP_MAX) {
			//				double div = CalcDispersion(q);
							//System.out.println("dddddd");
							if (down) {
								double diff = Math.abs(lastDownAbs - Math.abs(GRAVITY - v));
								if(diff < ALLOW_DIFF_THRESH){
									state.setType(WalkerState.Type.DESCENDING);
									ElevatorState evState = new ElevatorState(
											ElevatorState.Type.DESCENDING, startTime, event.time, 
											calcTimeWidth(startTime, event.time));
									state.evState.add(evState); 
									//System.out.println("10.2");

								}	

								flag=1;
								up = false;  down = false;
							}
							else{
								up = true;
								lastUpAbs = Math.abs(v - GRAVITY);
								//System.out.println("10");
								flag2=1;
							}
							startTime = event.time;
						}
					}
				} else {
					// Local minima
					lastNanosMinima = lastNanos;
					if (MINIMA_TIME_THRESH < lastNanosMinima - lastNanosMaxima) {
						//lastVMinima = v;
						if (DOWN_MIN <= v && v <= DOWN_MAX) {
							//System.out.println("aaaaa");
							if (up) {
								double diff = Math.abs(lastUpAbs - Math.abs(GRAVITY - v));
								if(diff < ALLOW_DIFF_THRESH){
									state.setType(WalkerState.Type.ASCENDING);
									
									//fireWalkerStateChanged(new WalkerState(
									//		WalkerState.Type.ASCENDING, startTime, event.time, 0));
									
									
									ElevatorState evState = new ElevatorState(
											ElevatorState.Type.ASCENDING, startTime, event.time, 
											calcTimeWidth(startTime, event.time));
									System.out.println("CalcDispersion(q): "+CalcDispersion(q));
									state.evState.add(evState); 
									//System.out.println("10.2");
									flag = 1;
									up = false;  down = false;
								}
							}					
							else{
								down = true;
								lastDownAbs = Math.abs(v - GRAVITY);
								//System.out.println("10");
								flag2=1;
							}
							startTime = event.time;
						}
					}
				}
			}
			if(flag==0 && flag2==0){
				//System.out.println("0");
			}
			if(flag==1){
				flag=0;
			}
			if(flag2==1){
				flag2=0;
			}
			lastV = v;
			lastDirection = direction;
			lastNanos = event.nanos;
		}
		for(ElevatorState es : state.evState){
			state.moveFloor += es.moveFloor;
		}

		return state;
	}
}




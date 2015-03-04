/**
 * Copyright (C) 2008-2012 Nishio Laboratory All Rights Reserved
 */
package jp.ac.ritsumei.cs.ubi.walker;


 class ElevatorState {

	static final double MOVE_ONE_FLOOR_MIN = 2.7 * 1000;

	static final double MOVE_TWO_FLOOR_MIN = 4.3 * 1000;

	static final double MOVE_THREE_FLOOR_MIN = 6.5 * 1000;

	static final double MOVE_FOUR_FLOOR_MIN = 9.2 * 1000;

	static final double MOVE_FIVE_FLOOR_MIN = 11.0 * 1000;

	static final double MOVE_SIX_FLOOR_MIN = 13.5 * 1000;



	static final double MOVE_ONE_FLOOR_MAX = 3.6 * 1000;

	static final double MOVE_TWO_FLOOR_MAX = 5.6 * 1000;

	static final double MOVE_THREE_FLOOR_MAX = 7.4 * 1000;

	static final double MOVE_FOUR_FLOOR_MAX = 10.7 * 1000;

	static final double MOVE_FIVE_FLOOR_MAX = 12.0 * 1000;

	static final double MOVE_SIX_FLOOR_MAX = 14.7 * 1000;

	enum Type {

		ASCENDING, DESCENDING;

	}

	Type type;

	long startTime;

	long endTime;

	long timeWidth;

	int moveFloor = 0;

	
	void calcMoveFloor(long timeWidth){
		
		if(MOVE_ONE_FLOOR_MIN <= timeWidth &&  timeWidth <= MOVE_ONE_FLOOR_MAX){
			this.moveFloor = 1;
		}
		else if(MOVE_TWO_FLOOR_MIN <= timeWidth &&  timeWidth <= MOVE_TWO_FLOOR_MAX){
			this.moveFloor = 2;
		}
		else if(MOVE_THREE_FLOOR_MIN <= timeWidth &&  timeWidth <= MOVE_THREE_FLOOR_MAX){
			this.moveFloor = 3;
		}
		else if(MOVE_FOUR_FLOOR_MIN <= timeWidth &&  timeWidth <= MOVE_FOUR_FLOOR_MAX){
			this.moveFloor = 4;
		}
		else if(MOVE_FIVE_FLOOR_MIN <= timeWidth &&  timeWidth <= MOVE_FIVE_FLOOR_MAX){
			this.moveFloor = 5;
		}
		else if(MOVE_SIX_FLOOR_MIN <= timeWidth &&  timeWidth <= MOVE_SIX_FLOOR_MAX){
			this.moveFloor = 6;
		}
		else{
			this.moveFloor = -9999;
		}
		
		if(this.type==ElevatorState.Type.DESCENDING && this.moveFloor != -1){
			this.moveFloor *= -1;
		}
	}
	
	ElevatorState
	(Type type, long startTime, long endTime, long timeWidth){

		this.type = type;

		this.startTime = startTime;

		this.endTime = endTime;

		this.timeWidth = timeWidth;

		calcMoveFloor(timeWidth);
	}
}

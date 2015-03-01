package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.plan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.AssistUtils;

public class StatusEstimater {
	private MovementResult movementresult;
	
	private Calendar expectedArrivalTime;
	
	private long estimatedArrivalTime = 0;

	private HashMap<String, ArrayList<HashMap<Integer, Long>>> minimumTravel;

	private HashMap<String, ArrayList<HashMap<Integer, Long>>> maximumTravel;
	
	
	public StatusEstimater(MovementResult movementresult, Calendar expectedArrivalTime){
		this.movementresult = movementresult;
		this.expectedArrivalTime = expectedArrivalTime;
	}
	
	public boolean isSameDestination(long fromid, long toid){
		if(movementresult == null){
			return false;
		}
		return (movementresult.getFromid() == fromid && movementresult.getToID() == toid);
	}
	
	public long backCalc(int order, ArrayList<HashMap<Integer, Long>> travel){
		long sum = 0;
		for(int i = travel.size() - 1 ; order < i ; i--){
			sum += travel.get(i).values().iterator().next();
		}
		return expectedArrivalTime.getTimeInMillis() - sum;
	}
	
	public int estimateStatus(String transportationName, int order, long estimatedTime){
		String key = maximumTravel.keySet().iterator().next();
		long a = backCalc(order, maximumTravel.get(key));
		key = minimumTravel.keySet().iterator().next();
		long b = backCalc(order, minimumTravel.get(key));
		
		if(estimatedTime <= a){
			return AssistUtils.STATUS_FINE;
		}else if(a < estimatedTime && estimatedTime <= b){
			return AssistUtils.STATUS_WILL_BE_LATENESS;
		}else if(b < estimatedTime){
			return AssistUtils.STATUS_LATENESS;
		}
		return AssistUtils.STATUS_LATENESS;
	}

	public void setEstimatedArrivalTime(long estimatedArrivalTime) {
		this.estimatedArrivalTime = estimatedArrivalTime;
	}
}

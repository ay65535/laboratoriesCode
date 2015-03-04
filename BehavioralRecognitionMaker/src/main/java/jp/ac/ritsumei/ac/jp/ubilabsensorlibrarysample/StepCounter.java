package jp.ac.ritsumei.ac.jp.ubilabsensorlibrarysample;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.ac.ritsumei.cs.ubi.library.accelerometer.PeakStepDetectorForMining;
import jp.ac.ritsumei.cs.ubi.library.accelerometer.StepListener;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.kubiwa.Acceleration;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.transport.Transportation;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.transport.Transportation.TransportationType;

public class StepCounter implements StepListener{
	private List<Acceleration> logs = null;
	private PeakStepDetectorForMining stepDetector = null;
	private int step = 0;
	private boolean onStep = false;

	public StepCounter(){
		stepDetector = new PeakStepDetectorForMining(2.0f, 0.4f);
		stepDetector.addListener(this);
	}

	private class AccelerometerComparator implements Comparator<Acceleration> {
		public int compare(Acceleration arg0, Acceleration arg1) {
			long diff = arg0.getT() - arg1.getT();
			if(diff == 0){
				return 0;
			}else if(0 < diff){
				return 1;
			}else{
				return -1;
			}
		}
	}

	/**
	 * 歩行区間とそれ以外の区間を判定するメソッド
	 * @param timeThreshold timeThreshold時間以下の歩行区間はノイズとして除去
	 * @param coneectiongThreshold connectiongThreshold時間以内にstepが観測された場合には,同一の区間としてつなげる
	 * @return
	 */
	public List<Transportation> judgeWalkingZone(long conectiongThreshold, long timeThreshold){
		List<Transportation> TransportationList = new ArrayList<Transportation>();

		long startTime = 0;
		long beforeStepTime = 0;
		
		//ミリ秒を秒に直す
		//conectiongThreshold = conectiongThreshold * 1000;
		//timeThreshold = timeThreshold * 1000;

		step = 0;
		boolean stepStart = true;
		boolean first = true;
		int lastCount = 0;
		
		for(Acceleration a : logs){
			lastCount++;
			onStep = false;
			stepDetector.detectStepAndNotify(a.getT(), a.getX(), a.getY(), a.getZ());
			
			//stepが観測された場合
			if(onStep){
				if(first){
					startTime = a.getTime();
					beforeStepTime = startTime;
					first = false;
					stepStart = false;
				}
				
				if(stepStart){
					startTime = beforeStepTime;
					beforeStepTime = startTime;
					stepStart = false;
				}else if(conectiongThreshold < (a.getTime() - beforeStepTime)){
					if(timeThreshold < (a.getTime() - startTime)){
						Transportation transportation = new Transportation();
						transportation.setStartTime(new Timestamp(startTime));
						transportation.setEndTime(new Timestamp(beforeStepTime));
						transportation.setType(TransportationType.WALKING);
						TransportationList.add(transportation);
						stepStart = true;
					}
				}
				beforeStepTime = a.getTime();
			}
			
			if(lastCount==logs.size() && stepStart == false){
				Transportation transportation = new Transportation();
				transportation.setStartTime(new Timestamp(startTime));
				transportation.setEndTime(new Timestamp(beforeStepTime));
				transportation.setType(TransportationType.WALKING);
				TransportationList.add(transportation);
				stepStart = true;
			}
		}
		return TransportationList;
	}
	
	public void onStep() {
		step++;
		onStep = true;
	}
	
	public int getStep() {
		return step;
	}

	public void setLogs(List<Acceleration> logs) {
		Collections.sort(logs, new AccelerometerComparator());
		if(this.logs != null){
			this.logs.clear();
			this.logs = null;
		}
		this.logs = logs;
	}
}

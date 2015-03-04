package jp.ac.ritsumei.ac.jp.ubilabsensorlibrarysample;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import jp.ac.ritsumei.cs.ubi.zukky.BRM.kubiwa.Acceleration;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.transport.Transportation;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.transport.Transportation.TransportationType;

public class StepCounterMain {
	private final static long HARH_HOUR = 1000 * 60 * 30;

	/**
	 * 指定された区間を歩行とそれ以外で区切るメソッド
	 * stepが判定された区間を歩行と判定（ThresholdTime以内にstepが観測されたら同一の歩行区間とする）
	 * @return
	 */
	public List<Transportation> judgeStepZone(Timestamp startTime, Timestamp endTime, int devId, long coneectiongThreshold, long timeThreshold){
		List<Acceleration> logs;
		List<Transportation> result = new ArrayList<Transportation>();
		StepCounter s = new StepCounter();

		boolean notFinish = true;
		while(notFinish){
			List<Transportation> tsList = new ArrayList<Transportation>();
			Timestamp begin = startTime;
			Timestamp end = endTime;
			if(startTime.getTime() + HARH_HOUR < endTime.getTime()){
				end = new Timestamp(startTime.getTime() + HARH_HOUR);
			}else{
				end = endTime;
				notFinish = false;
			}
			try {
				logs = select("select * from accelerometer where devid = " +devId + " and time between " +
						"'" + begin.toString() +"' and '"+ end.toString() + "' order by time asc");
				s.setLogs(logs);

				tsList = s.judgeWalkingZone(coneectiongThreshold, timeThreshold);				
				result.addAll(tsList);
				logs.clear();
				tsList.clear();
				startTime = end;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return addStandingZone(startTime, endTime, result);
	}

	/**
	 * sTime-eTime区間内で抽出された歩行区間と歩行区間の間をStandingZoneとして挿入するメソッド
	 * @param sTime
	 * @param eTime
	 * @param walkingZone
	 * @return
	 */
	private List<Transportation> addStandingZone(Timestamp sTime, Timestamp eTime, List<Transportation> walkingZone){
		List<Transportation> resultTsList = new ArrayList<Transportation>();
		if(walkingZone.isEmpty()){
			return resultTsList;
		}
		boolean firstTime = true;

		for(Transportation ts: walkingZone){
			if(firstTime){
				if(!(ts.getStartTime().equals(sTime))){
					Transportation transportation = new Transportation();
					transportation.setStartTime(sTime);
					transportation.setEndTime(ts.getEndTime());
					transportation.setType(TransportationType.WALKING); //立ち寄りポイントからの最初はwalking
					resultTsList.add(transportation);	
				}else{
					resultTsList.add(ts);
				}
				firstTime = false;
			}else{
				Transportation transportation = new Transportation();
				transportation.setStartTime(resultTsList.get(resultTsList.size()-1).getEndTime());
				transportation.setEndTime(ts.getStartTime());
				transportation.setType(TransportationType.UNKNOWN);
				resultTsList.add(transportation);	

				resultTsList.add(ts);
			}
		}

		if(!(walkingZone.get(walkingZone.size()-1).getEndTime().equals(eTime))){
			Transportation transportation = new Transportation();
			transportation.setStartTime(walkingZone.get(walkingZone.size()-1).getEndTime());
			transportation.setEndTime(eTime);
			transportation.setType(TransportationType.UNKNOWN);
			resultTsList.add(transportation);	
		}

		return resultTsList;
	}

	private static List<Acceleration> select(String sql) throws SQLException {
		BlackholeConnector connector = null;
		try {
			connector = new BlackholeConnector("kubiwa.properties");
			connector.createConnection();

			return connector.selectAcc(sql);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}finally{
			if(connector != null && !connector.isClosed()){
				connector.close();
			}
		}
	}
}

package jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule.plan;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule.database.MovementResultConnector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ArrivalTimeEstimater {

	public static void estimate1(int devid, ArrayList<MyGeoPoint> trajectory)
			throws SQLException, JSONException{
		if(trajectory == null || trajectory.isEmpty()){
			System.out.println("-trajectory is null or empty!!");
			return;
		}

//		long sum = 0;
//		for(int i = 0 ; i < min.length() ; i++){
//			JSONObject o = min.getJSONObject(i);
//			sum += o.getJSONObject("endpoint").getLong("arrival") - 
//					o.getJSONObject("startpoint").getLong("departure");
//		}
//		System.out.println("sum = " + (sum / 1000) + ", elapseTimeSec = " + elapseTimeSec);
		

		MovementResultConnector connector = null;
		try {
			connector = new MovementResultConnector(
					"jdbc:mysql://localhost:3306/movement_result?useUnicode=true&characterEncoding=utf8", 
					"plancheckeruser", "n0pr1vacy");
			connector.createConnection();
			double min = Double.MAX_VALUE;
			JSONArray minArray = null;
			ArrayList<JSONArray> logs = connector.selectMovemetLogs(devid);
			if(logs == null || logs.isEmpty()){
				System.out.println("---logs are null or empty!!---");
				return;
			}
			
			for(int i = 0 ; i < logs.size() ; i++){
				JSONArray log = logs.get(i);
				ArrayList<MyGeoPoint> checkPoints = convertToArrayList(log);
				if(checkPoints == null || checkPoints.isEmpty()){
					System.out.println("Path" + i + " ---checkPoints are null or empty!!---");
					continue;
				}
				Map<Integer, Double> indexs = getNearestIndexs(checkPoints, trajectory);
				if(indexs == null || indexs.isEmpty()){
					System.out.println("Path" + i + " ---indexs are null or empty!!---");
					continue;
				}
				System.out.println("Path" + i + ", CP = " + checkPoints.size() + ", indexs = " + indexs.size());

				JSONArray sim = new JSONArray();
				for(Integer index : indexs.keySet()){
					List<MyGeoPoint> subTrajectory = trajectory.subList(0, index);
					MyGeoPoint latest = trajectory.get(index);
					System.out.println("Path" + i + " - " + trajectory.size() + ".get(" + index + ") = " +
							subTrajectory.size() + " - " + latest.getTimestamp().getTime());
					double similarity = calcSimilarity(subTrajectory, checkPoints, latest);
					if(similarity < 0){
						System.out.println("Path" + i + " - can't calcurate Similarity");
					}else{
						sim.put(similarity);
					}
				}
				if(0 < sim.length()){
					System.out.println(sim);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally{
			if(connector != null){
				connector.close();
			}
		}
	}
	
	public static double calcSimilarity(List<MyGeoPoint> subTrajectory, ArrayList<MyGeoPoint> checkPoints, 
			MyGeoPoint latest){
		ArrayList<double[]> stayPointRects = 
				CheckPointsCreater.createCheckpoints(subTrajectory);
		
		if(stayPointRects == null || stayPointRects.isEmpty()){
			return -1;
		}

		ArrayList<MyGeoPoint> stayPoints = new ArrayList<MyGeoPoint>();
		for(double[] rect : stayPointRects){
			MyGeoPoint latlng = new MyGeoPoint(rect[0], rect[1], rect[2], rect[3]);
			stayPoints.add(latlng);
		}
		int elapseTimeSec = (int) GeoPointUtils.getTime(subTrajectory);
		

		double dist = calcDistance(elapseTimeSec, latest, stayPoints, checkPoints);
		long diff = Math.abs(calcElapsedTime(checkPoints, latest) - elapseTimeSec);
		System.out.println("StayPoint:" + stayPointRects.size() +
				" / " + subTrajectory.size() + ", elapse = " + elapseTimeSec +
				"sec, Dist = " + dist + ", Diff=" + diff);

		if(dist < 0 || diff < 0){
			return -1;
		}
		return dist * diff;
	}
	


	private static double calcDistance(long elapseTimeSec, MyGeoPoint latest, 
			ArrayList<MyGeoPoint> stayPoints, ArrayList<MyGeoPoint> checkPoints){
		if(latest == null || stayPoints == null || checkPoints == null ||
				stayPoints.isEmpty() || checkPoints.isEmpty()){
			System.out.println("---null or empty in [calcDistance]---");
			return -1;
		}

		int same = 0;
		int all = stayPoints.size() + checkPoints.size();
		for(MyGeoPoint a : stayPoints){
			for(MyGeoPoint b : checkPoints){
				double huberyDistance = GeoPointUtils.calcDistanceHubery(
						a.lat, a.lng, b.lat, b.lng, GeoPointUtils.GRS80);
				if(huberyDistance < 5){
					same++;
				}
			}
		}
		double samePoint = same;
		double allPoint = all;

		if(samePoint <= 0 || allPoint <= 0){
			System.out.println("---samePoint = " + samePoint + 
					", allPoint = " + allPoint + " in [calcDistance]---");
			return -1;
		}
		System.out.print("(" + allPoint + " - " + samePoint + " + 1) / " + samePoint + ") ");
		return (allPoint - samePoint + 1) / samePoint;
	}

	private static ArrayList<MyGeoPoint> convertToArrayList(JSONArray log) throws JSONException {
		if(log == null || log.length() == 0){
			System.out.println("---null or empty in [convertToArrayList]---");
			return null;
		}
		ArrayList<MyGeoPoint> list = new ArrayList<MyGeoPoint>();;
		for(int i = 0 ; i < log.length() ; i++){
			JSONObject block = log.getJSONObject(i);
			list.addAll(convertToList(block.getJSONArray("checkpoints")));
		}
		return list;
	}

	private static ArrayList<MyGeoPoint> convertToList(JSONArray checkpoints){
		ArrayList<MyGeoPoint> points = new ArrayList<MyGeoPoint>();
		if(checkpoints == null || checkpoints.length() == 0){
			return points;
		}
		int size = checkpoints.length() - 1;
		for(int i = 0 ; i < size ; i++){
			try {
				points.add(convertToMyGeoPoint(checkpoints.getJSONObject(i)));
			} catch (JSONException e) {
				continue;
			}
		}
		return points;
	}

	private static MyGeoPoint convertToMyGeoPoint(JSONObject block) throws JSONException{
		MyGeoPoint p = new MyGeoPoint(block.getDouble("minLat"),
				block.getDouble("minLng"),
				block.getDouble("maxLat"),
				block.getDouble("maxLng"));
		p.setTime(new Timestamp(block.getLong("time")));
		return p;
	}
	
	private static long calcElapsedTime(ArrayList<MyGeoPoint> checkpoint, MyGeoPoint latestPoint){
		if(checkpoint == null || latestPoint == null){
			System.out.println("---null or empty in [calcElapsedTime]---");
			return -1;
		}
		int index = getNearestIndex(checkpoint, latestPoint);
		if(index < 0){
			return -1;
		}
		return checkpoint.get(index).getTimestamp().getTime() - 
				checkpoint.get(0).getTimestamp().getTime();
	}
	
	private static Map<Integer, Double> getNearestIndexs(ArrayList<MyGeoPoint> checkpoints, 
			ArrayList<MyGeoPoint> trajectory){
		if(checkpoints == null || trajectory == null || 
				checkpoints.isEmpty() || trajectory.isEmpty()){
			System.out.println("---null or empty in [getNearestIndexs]---");
			return null;
		}

		Map<Integer, Double> returnMap = new HashMap<Integer, Double>();
		for(int i = 0 ; i < checkpoints.size() ; i++){
			MyGeoPoint cp = checkpoints.get(i);
			if(cp == null){
				continue;
			}
			double min = Double.MAX_VALUE;
			int index = -1;
			for(int j = 0 ; j < trajectory.size() ; j++){
				MyGeoPoint p = trajectory.get(j);
				double dist = GeoPointUtils.calcDistanceHubery(
						p.getLatitude(), p.getLongtitude(),
						cp.getLatitude(), cp.getLongtitude(), GeoPointUtils.GRS80);
				if(dist < min){
					min = dist;
					index = j;
				}
			}
			System.out.println("index = " + index + ", dist = " + min);
			returnMap.put(index, min);
		}
		return returnMap;
	}
	
	private static int getNearestIndex(ArrayList<MyGeoPoint> checkpoints, MyGeoPoint latestPoint){
		double min = Double.MAX_VALUE;
		int returnNum = -1;
		if(checkpoints == null || latestPoint == null || checkpoints.isEmpty()){
			System.out.println("---null or empty in [getNearestIndex]---");
			return returnNum;
		}

		for(int i = 0 ; i < checkpoints.size() ; i++){
			MyGeoPoint cp = checkpoints.get(i);
			if(cp == null){
				continue;
			}
			double dist = GeoPointUtils.calcDistanceHubery(
					latestPoint.getLatitude(), latestPoint.getLongtitude(),
					cp.getLatitude(), cp.getLongtitude(), GeoPointUtils.GRS80);
			if(dist < min){
				min = dist;
				returnNum = i;
			}
		}
		return returnNum;
	}
}

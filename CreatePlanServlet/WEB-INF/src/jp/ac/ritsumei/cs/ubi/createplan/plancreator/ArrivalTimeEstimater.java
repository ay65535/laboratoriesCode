package jp.ac.ritsumei.cs.ubi.createplan.plancreator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import jp.ac.ritsumei.cs.ubi.createplan.database.MovementResultConnector;
import jp.ac.ritsumei.cs.ubi.createplan.utils.GeoPointUtils;
import jp.ac.ritsumei.cs.ubi.createplan.utils.MyGeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ArrivalTimeEstimater {

	public static JSONArray estimate(String stayPointRects, String latestPoint,
			int elapseTimeSec, int devid) throws SQLException, JSONException{

		String[] rects = stayPointRects.split(":");
		ArrayList<MyGeoPoint> stayPoints = new ArrayList<MyGeoPoint>();
		for(String rect : rects){
			String[] rectMenber = rect.split(",");
			MyGeoPoint latlng = new MyGeoPoint(Double.parseDouble(rectMenber[0]), Double.parseDouble(rectMenber[1]), 
					Double.parseDouble(rectMenber[2]), Double.parseDouble(rectMenber[3]));
			stayPoints.add(latlng);
		}

		String[] latlngString = latestPoint.split(",");
		MyGeoPoint latestLatLng = new MyGeoPoint(0, 
				Double.parseDouble(latlngString[0]), Double.parseDouble(latlngString[1]), 0, 0);


		MovementResultConnector connector = null;
		try {
			connector = new MovementResultConnector(
					"jdbc:mysql://localhost:3306/movement_result?useUnicode=true&characterEncoding=utf8", 
					"plancheckeruser", "n0pr1vacy");
			connector.createConnection();
			double min = Double.MAX_VALUE;
			JSONArray minArray = null;
			ArrayList<JSONArray> logs = connector.selectMovemetLogs(devid);
			for(JSONArray log : logs){
				ArrayList<MyGeoPoint> checkPoints = get(log);
				double dist = calcDistance(elapseTimeSec, latestLatLng, stayPoints, checkPoints);
				long diff = Math.abs(calcElapsedTime(checkPoints, latestLatLng) - 
						elapseTimeSec);

				if(dist < 0 || diff < 0){
					continue;
				}

				if(dist * diff < min){
					min = dist * diff;
					minArray = log;
				}
			}
			return minArray;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally{
			if(connector != null){
				connector.close();
			}
		}
		return null;
	}

	private static double calcDistance(long elapseTimeSec, MyGeoPoint latest, 
			ArrayList<MyGeoPoint> stayPoints, ArrayList<MyGeoPoint> checkPoints){
		if(latest == null || stayPoints == null || checkPoints == null){
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

		if(samePoint < 0 || allPoint < 0){
			return -1;
		}
		return (allPoint - samePoint + 1) / samePoint;
	}

	private static ArrayList<MyGeoPoint> get(JSONArray log) throws JSONException {
		if(log == null || log.length() == 0){
			return null;
		}
		if(log.length() == 1){
			JSONObject block = log.getJSONObject(0);
			return convertToList(block.getJSONArray("checkpoints"));
		}else{
			ArrayList<MyGeoPoint> list = new ArrayList<MyGeoPoint>();;
			for(int i = 0 ; i < log.length() - 1 ; i++){
				JSONObject block = log.getJSONObject(i);
				list.addAll(convertToList(block.getJSONArray("checkpoints")));
				list.add(convertToMyGeoPoint(block.getJSONObject("endpoint")));
			}
			return list;
		}
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
		return new MyGeoPoint(block.getDouble("minLat"),
				block.getDouble("minLng"),
				block.getDouble("maxLat"),
				block.getDouble("maxLng"));
	}

	private static long calcElapsedTime(ArrayList<MyGeoPoint> checkpoint, MyGeoPoint latestPoint){
		return -1;
	}
}

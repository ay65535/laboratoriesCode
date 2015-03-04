package jp.ac.ritsumei.cs.ubi.zukky.BRM.main;

import java.sql.Timestamp;
import java.util.List;

import jp.ac.ritsumei.cs.ubi.zukky.BRM.featurePoint.FeaturePoint;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.json.Json_Mode;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.json.Json_Route;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.json.Json_Transportation;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.route.Path;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.route.PathSegment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONConverter {
	public static JSONArray convertToJSON(Path path) {
		JSONArray pathArray = new JSONArray();
		if(path == null){
			return pathArray;
		}
		for(PathSegment ps : path.getPathSegmentList()){
			try {
				JSONObject segment = new JSONObject();
				segment.put("mode", ps.getType());
				segment.put("startpoint", convertToJSON(ps.getTsStartPoint()));
				segment.put("endpoint", convertToJSON(ps.getTsEndPoint()));
				JSONArray checkpoints = new JSONArray();
				for(FeaturePoint p : ps.getCheckPointCandidateList()){
					checkpoints.put(convertToJSON(p));
				}
				segment.put("checkpoints", checkpoints);
				pathArray.put(segment);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return pathArray;
	}
	
	public static JSONArray convertToJSON(List<Json_Transportation> jTransportationList) {
		JSONArray transportations = new JSONArray();
		for(Json_Transportation jTransportation: jTransportationList){
			transportations.put(convertToJSON(jTransportation));
		}
		return transportations; 
	}
	
	public static JSONObject convertToJSON(Json_Transportation jTransportation){
		JSONObject transportation = new JSONObject();
		try {
			JSONArray jModes = new JSONArray();
			for(Json_Mode jMode: jTransportation.getModeList()){
				jModes.put(convertToJSON(jMode));
			}
			transportation.put(jTransportation.getTransportationListString(), jModes);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return transportation;
	}
	
	public static JSONObject convertToJSON(Json_Mode jMode){
		JSONObject mode = new JSONObject();
		try {
			JSONArray routes = new JSONArray();
			for(Json_Route jRoute: jMode.getRouteList()){
				routes.put(convertToJSON(jRoute));
			}
			mode.put("mode", jMode.getMode());
			mode.put("routes", routes);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return mode;
	}
	
	public static JSONObject convertToJSON(Json_Route jRoute){
		JSONObject route = new JSONObject();
		try {
			route.put("startpoint", convertToJSON(jRoute.getTsStartPoint()));
			route.put("endpoint", convertToJSON(jRoute.getTsEndPoint()));
			JSONArray checkpoints = new JSONArray();
			for(FeaturePoint checkPoint: jRoute.getCheckPointCandidateList()){
				checkpoints.put(convertToJSON(checkPoint));
			}
			route.put("checkpoints", checkpoints);
			
			JSONArray travelTimes = new JSONArray();
			for(Long t: jRoute.getTravelTimes()){
				travelTimes.put(t);
			}
			route.put("traveltimes", travelTimes);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return route;
	}
	
	public static JSONObject convertToJSON(FeaturePoint point){
		JSONObject object = new JSONObject();
		try {
			object.put("id", point.getId());
			object.put("maxLat", point.getMaxLat());
			object.put("maxLng", point.getMaxLng());
			object.put("minLat", point.getMinLat());
			object.put("minLng", point.getMinLng());
			
			JSONArray times = new JSONArray();
			for(Long l : point.getStayingTimeList()){
				times.put(l);
			}
			object.put("staytimes", times);
			Timestamp temp = point.getArrivalTime();
			if(temp != null){
				object.put("arrival", temp.getTime());
			}else{
				object.put("arrival", 0);
			}
			temp = point.getTime();
			if(temp != null){
				object.put("time", temp.getTime());
			}else{
				object.put("time", 0);
			}
			temp = point.getDepartureTime();
			if(temp != null){
				object.put("departure", temp.getTime());
			}else{
				object.put("departure", 0);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}
}

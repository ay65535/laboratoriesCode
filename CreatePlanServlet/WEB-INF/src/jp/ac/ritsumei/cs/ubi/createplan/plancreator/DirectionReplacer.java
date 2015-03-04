package jp.ac.ritsumei.cs.ubi.createplan.plancreator;

import jp.ac.ritsumei.cs.ubi.createplan.drivingdirection.StationObject;
import jp.ac.ritsumei.cs.ubi.createplan.utils.GeoPointUtils;
import jp.ac.ritsumei.cs.ubi.createplan.utils.MyGeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DirectionReplacer {
	
	public static JSONObject replaceRouteToDestination(JSONObject directions, JSONArray movementResult){
		
//		directions.getJSONObject("sctdbc");
//		directions.getJSONObject("sctdbw");
//		directions.getJSONObject("sctdbb");
		return null;
	}

	
	public static JSONObject replaceRouteFromStation(JSONObject directions, JSONArray movementResult)
			throws JSONException {
		for(int i = 0 ; i < movementResult.length() ; i++){
			JSONObject toDropInSite = movementResult.getJSONObject(i);
			jjj(directions, toDropInSite);

			directions.getJSONObject("sctsbc");
			directions.getJSONObject("sctsbw");
			directions.getJSONObject("sctsbb");
		}
		
		return null;

	}
	
	public static JSONObject replaceRouteToStation(JSONObject directions, JSONArray movementResult)
			throws JSONException {
		for(int i = 0 ; i < movementResult.length() ; i++){
			JSONObject toDropInSite = movementResult.getJSONObject(i);
			jjj(directions, toDropInSite);

			directions.getJSONObject("sstdbc");
			directions.getJSONObject("sstdbw");
			directions.getJSONObject("sstdbb");

		}
		
		return null;

	}

	private static void jjj(JSONObject directions, JSONObject toDropInSite)
			throws JSONException {
		JSONArray transferNames = toDropInSite.names();
		for(int j = 0 ; j < transferNames.length() ; j++){
			JSONArray meansOfTransportations = toDropInSite.getJSONArray(transferNames.getString(j));
			for(int k = 0 ; k < meansOfTransportations.length() ; k++){
				JSONObject meansOfTransportation = meansOfTransportations.getJSONObject(k);
				if("Train".equals(meansOfTransportation.getString("mode"))){
					JSONArray routes = meansOfTransportation.getJSONArray("route");
					for(int l = 0 ; l < routes.length() ; l++){
//						boolean t1 = isNear(routes.getJSONObject(l).getJSONObject("trasferpointA"), 
//								StationObject.create(directions.getJSONObject("stationsCloseToCurrent")));
//						boolean t2 = isNear(routes.getJSONObject(l).getJSONObject("trasferpointB"), 
//								StationObject.create(directions.getJSONObject("stationsCloseToDestination")));
					}

				}
			}

		}
	}

	public static boolean isNear(JSONObject transferPoint, StationObject station) 
			throws JSONException{
		if(transferPoint == null || station == null){
			return false;
		}

		double lat = transferPoint.getDouble("lat");
		double lng = transferPoint.getDouble("lng");
		MyGeoPoint p = station.getLatlng();
		double dist = GeoPointUtils.calcDistanceHubery(
				lat, lng, p.lat, p.lng, GeoPointUtils.GRS80);

		if(dist <= 0){
			return false;
		}
		return (dist < 10);
	}
}


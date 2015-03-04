/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.createplan.drivingdirection;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import jp.ac.ritsumei.cs.ubi.createplan.utils.HttpConnector;
import jp.ac.ritsumei.cs.ubi.createplan.utils.MyGeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DrivingDirectionDownloader{
	private static String DIRECTION_DOWNLOD_URL = "http://maps.google.com/maps/api/directions/json?";
	
	private static String STATION_DOWNLOD_URL = "http://express.heartrails.com/api/json?method=getStations";
	
	private List<StepObject> stepsCurrentToDestinationByCar = null;
	private List<StepObject> stepsCurrentToDestinationByWalk = null;
	private List<StepObject> stepsCurrentToDestinationByBicycle = null;
	
	private List<StepObject> stepsCurrentToStationByCar = null;
	private List<StepObject> stepsCurrentToStationByWalk = null;
	private List<StepObject> stepsCurrentToStationByBicycle = null;
	
	private List<StationObject> stationsCloseToCurrent = null;
	private List<StationObject> stationsCloseToDestination = null;
	
	private List<StepObject> stepsStationToDestinationByCar = null;
	private List<StepObject> stepsStationToDestinationByWalk = null;
	private List<StepObject> stepsStationToDestinationByBicycle = null;
	
	private MyGeoPoint origin, destination;
//	PrintWriter pw;
	
	public DrivingDirectionDownloader(MyGeoPoint origin, MyGeoPoint destination, PrintWriter pw){
		this.origin = origin;
		this.destination = destination;
//		this.pw = pw;
	}

	public void downloadDerections() {
		this.stationsCloseToCurrent = downloadNearestStation(origin);
		this.stationsCloseToDestination = downloadNearestStation(destination);
		
		downloadCurrentToDestination();
		downloadCurrentToStation();
		downloadStationToDestination();
	}

	private void downloadStationToDestination() {
		if(stationsCloseToDestination != null && !stationsCloseToDestination.isEmpty()){
			this.stepsStationToDestinationByCar = downloadGoogleDirection(
					stationsCloseToDestination.get(0).getLatlng(), destination, "driving");
		}else{
//			pw.println("stepsStationToDestinationByCar is null!");
		}
		if(stationsCloseToDestination != null && !stationsCloseToDestination.isEmpty()){
			this.stepsStationToDestinationByWalk = downloadGoogleDirection(
					stationsCloseToDestination.get(0).getLatlng(), destination, "walking");
		}else{
//			pw.println("stepsStationToDestinationByWalk is null!");
		}
		if(stationsCloseToDestination != null && !stationsCloseToDestination.isEmpty()){
			this.stepsStationToDestinationByBicycle = downloadGoogleDirection(
					stationsCloseToDestination.get(0).getLatlng(), destination, "bicycling");
		}else{
//			pw.println("stepsStationToDestinationByBicycle is null!");
		}
	}

	private void downloadCurrentToStation() {
		if(stationsCloseToCurrent != null && !stationsCloseToCurrent.isEmpty()){
			this.stepsCurrentToStationByCar = downloadGoogleDirection(
					origin, stationsCloseToCurrent.get(0).getLatlng(), "driving");
		}else{
//			pw.println("stepsCurrentToStationByCar is null!");
		}
		if(stationsCloseToCurrent != null && !stationsCloseToCurrent.isEmpty()){
			this.stepsCurrentToStationByWalk = downloadGoogleDirection(
					origin, stationsCloseToCurrent.get(0).getLatlng(), "walking");
		}else{
//			pw.println("stepsCurrentToStationByWalk is null!");
		}
		if(stationsCloseToCurrent != null && !stationsCloseToCurrent.isEmpty()){
			this.stepsCurrentToStationByBicycle = downloadGoogleDirection(
					origin, stationsCloseToCurrent.get(0).getLatlng(), "bicycling");
		}else{
//			pw.println("stepsCurrentToStationByBicycle is null!");
		}
	}

	private void downloadCurrentToDestination() {
		this.stepsCurrentToDestinationByCar = downloadGoogleDirection(origin, destination, "driving");
		if(stepsCurrentToDestinationByCar == null){
//			pw.println("stepsCurrentToDestinationByCar is null!");
		}
		this.stepsCurrentToDestinationByWalk = downloadGoogleDirection(origin, destination, "walking");
		if(stepsCurrentToDestinationByWalk == null){
//			pw.println("stepsCurrentToDestinationByWalk is null!");
		}
		this.stepsCurrentToDestinationByBicycle = downloadGoogleDirection(origin, destination, "bicycling");
		if(stepsCurrentToDestinationByBicycle == null){
//			pw.println("stepsCurrentToDestinationByBicycle is null!");
		}
	}
	
	/**
	 * 
	 * @param origin
	 * @return
	 */
	private List<StationObject> downloadNearestStation(MyGeoPoint origin) {
		try{
			String urlOforigin = STATION_DOWNLOD_URL + 
					"&x=" + origin.getLongtitude() +
					"&y=" + origin.getLatitude();
			
			String s = HttpConnector.downloadDataForALine(urlOforigin);
			JSONObject j = new JSONObject(s).getJSONObject("response");
			JSONArray dResult = j.getJSONArray("station");
			
			List<StationObject> re = new ArrayList<StationObject>();
			for(int i = 0 ; i < dResult.length() ; i++){
				re.add(StationObject.create(dResult.getJSONObject(i)));
			}
			return re;
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param origin
	 * @param destination
	 * @param mode "driving", "walking", "bicycling"
	 * @return
	 */
	private List<StepObject> downloadGoogleDirection(
			MyGeoPoint origin, MyGeoPoint destination, String mode) {
		String originParam = "origin=" + origin.getLatitude() + "," + origin.getLongtitude();
		String destinationParam = "destination=" + destination.getLatitude() + "," + destination.getLongtitude();
		
		try{
			String url = DIRECTION_DOWNLOD_URL + originParam + "&" + destinationParam + "&mode=" + mode + "&sensor=false";
			String result = HttpConnector.downloadDataForALine(url);
			return parseDirectionJSON(new JSONObject(result));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ArrayList<StepObject> parseDirectionJSON(JSONObject dResult) throws JSONException {
		String status = dResult.getString("status");
		JSONArray routes = dResult.getJSONArray("routes");
		ArrayList<StepObject> stepObjects = new ArrayList<StepObject>();
		
		int routeSize = routes.length();
		for(int i = 0 ; i < routeSize ; i++){
			JSONObject routeObject = routes.getJSONObject(i);
//			printSummary(status, routeObject);
		    
			JSONArray legs = routeObject.getJSONArray("legs");
			int legSize = legs.length();
			for(int j = 0 ; j < legSize ; j++){
				LegObject legObject = LegObject.create(legs.getJSONObject(j));
						
				JSONArray steps = legObject.getSteps();
				int stepSize = steps.length();
				for(int k = 0 ; k < stepSize ; k++){
					StepObject stepObject = StepObject.create(steps.getJSONObject(k));
					if(stepObject != null){
						stepObjects.add(stepObject);
					}
//					System.out.println("steps : " + stepObject.getHtmlInstructions());
				}
			}
//			List<MyGeoPoint> overviewPolyline = PolylineDecoder.decodePoly(
//					routeObject.getJSONObject("overview_polyline").getString("points"));
		}
		return stepObjects;
	}

	private void printSummary(String status, JSONObject routeObject)
			throws JSONException {
		String summary = routeObject.getString("summary");
		String copyrights = routeObject.getString("copyrights");
		
		JSONObject bounds = routeObject.getJSONObject("bounds");
		double northeastLat = bounds.getJSONObject("northeast").getDouble("lat");
		double northeastLng = bounds.getJSONObject("northeast").getDouble("lng");
		double southwestLat = bounds.getJSONObject("southwest").getDouble("lat");
		double southwestLng = bounds.getJSONObject("southwest").getDouble("lng");
		
		JSONArray warnings = routeObject.getJSONArray("warnings");
		JSONArray waypointOrder = routeObject.getJSONArray("waypoint_order");
		
		System.out.println("status:" + status + " : " + summary + " : " + copyrights +
				"(" + northeastLat + "," + northeastLng + "," + southwestLat + "," + southwestLng + ")" + 
				", other[" + warnings.length() + "," + waypointOrder.length() + "]");
	}

	public static String getDIRECTION_DOWNLOD_URL() {
		return DIRECTION_DOWNLOD_URL;
	}

	public static String getSTATION_DOWNLOD_URL() {
		return STATION_DOWNLOD_URL;
	}

	public List<StationObject> getStationsCloseToCurrent() {
		return stationsCloseToCurrent;
	}

	public List<StationObject> getStationsCloseToDestination() {
		return stationsCloseToDestination;
	}

	public MyGeoPoint getOrigin() {
		return origin;
	}

	public MyGeoPoint getDestination() {
		return destination;
	}

	public List<StepObject> getStepsCurrentToDestinationByCar() {
		return stepsCurrentToDestinationByCar;
	}

	public List<StepObject> getStepsCurrentToDestinationByWalk() {
		return stepsCurrentToDestinationByWalk;
	}

	public List<StepObject> getStepsCurrentToDestinationByBicycle() {
		return stepsCurrentToDestinationByBicycle;
	}

	public List<StepObject> getStepsCurrentToStationByCar() {
		return stepsCurrentToStationByCar;
	}

	public List<StepObject> getStepsCurrentToStationByWalk() {
		return stepsCurrentToStationByWalk;
	}

	public List<StepObject> getStepsCurrentToStationByBicycle() {
		return stepsCurrentToStationByBicycle;
	}

	public List<StepObject> getStepsStationToDestinationByCar() {
		return stepsStationToDestinationByCar;
	}

	public List<StepObject> getStepsStationToDestinationByWalk() {
		return stepsStationToDestinationByWalk;
	}

	public List<StepObject> getStepsStationToDestinationByBicycle() {
		return stepsStationToDestinationByBicycle;
	}
	
}

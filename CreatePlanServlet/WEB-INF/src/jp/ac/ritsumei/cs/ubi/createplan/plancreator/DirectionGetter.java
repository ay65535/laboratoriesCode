/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.createplan.plancreator;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;

import jp.ac.ritsumei.cs.ubi.createplan.database.MovementResultConnector;
import jp.ac.ritsumei.cs.ubi.createplan.drivingdirection.DrivingDirectionDownloader;
import jp.ac.ritsumei.cs.ubi.createplan.drivingdirection.StationObject;
import jp.ac.ritsumei.cs.ubi.createplan.drivingdirection.StepObject;
import jp.ac.ritsumei.cs.ubi.createplan.drivingdirection.StepsArray;
import jp.ac.ritsumei.cs.ubi.createplan.drivingdirection.TransferDownloader;
import jp.ac.ritsumei.cs.ubi.createplan.utils.MyGeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DirectionGetter{
	private PrintWriter pw;
	private int devid = 0;
	
	public DirectionGetter(PrintWriter pw, int devid){
		this.pw = pw;
		this.devid = devid;
	}
	
	public JSONObject getMergedDrivingDirection(
		double originLat, double originLng, double destinationLat, double destinationLng) 
		throws IOException, ServletException, JSONException {

		try {
			JSONObject directions = 
					downloadDrivingDirection(originLat, originLng, destinationLat, destinationLng);
			JSONArray movementResult = getMovementResult(devid);
			
			if(movementResult == null || movementResult.length() == 0 || directions == null){
				return directions;
			}
			
			directions = DirectionReplacer.replaceRouteToStation(directions, movementResult);
			directions = DirectionReplacer.replaceRouteFromStation(directions, movementResult);
			directions = DirectionReplacer.replaceRouteToDestination(directions, movementResult);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public JSONObject downloadDrivingDirection(
			double originLat, double originLng, double destinationLat, double destinationLng) 
			throws IOException, ServletException, JSONException {
		MyGeoPoint origin = new MyGeoPoint(0, originLat, originLng, 0, 0);
		MyGeoPoint destination = new MyGeoPoint(0, destinationLat, destinationLng, 0, 0);

		DrivingDirectionDownloader dddr = 
				new DrivingDirectionDownloader(origin, destination, pw);
		dddr.downloadDerections();

		List<StationObject> stationsCloseToCurrent = dddr.getStationsCloseToCurrent();
		List<StationObject> stationsCloseToDestination = dddr.getStationsCloseToDestination();
		if(stationsCloseToCurrent == null || stationsCloseToCurrent.isEmpty()){
			return null;
		}else if(stationsCloseToDestination == null || stationsCloseToDestination.isEmpty()){
			return null;
		}

		JSONObject directions = buildJSON(dddr, stationsCloseToCurrent,
				stationsCloseToDestination);

		return directions;
	}

	private JSONObject buildJSON(DrivingDirectionDownloader dddr,
			List<StationObject> stationsCloseToCurrent,
			List<StationObject> stationsCloseToDestination)
			throws JSONException {
		long now = System.currentTimeMillis();
		
		StepsArray stepsCurrentToDestinationByCar = 
			new StepsArray(dddr.getStepsCurrentToDestinationByCar(), now);
		StepsArray stepsCurrentToDestinationByWalk = 
			new StepsArray(dddr.getStepsCurrentToDestinationByWalk(), now);
		StepsArray stepsCurrentToDestinationByBicycle = 
			new StepsArray(dddr.getStepsCurrentToDestinationByBicycle(), now);		
		
		
		StepsArray stepsCurrentToStationByCar = 
			new StepsArray(dddr.getStepsCurrentToStationByCar(), now);
		StepsArray stepsCurrentToStationByWalk = 
			new StepsArray(dddr.getStepsCurrentToStationByWalk(), now);
		StepsArray stepsCurrentToStationByBicycle = 
			new StepsArray(dddr.getStepsCurrentToStationByBicycle(), now);

		
		StepsArray stepsStationToDestinationByCar = 
			create(stationsCloseToCurrent, stationsCloseToDestination, 
					stepsCurrentToStationByCar, dddr.getStepsStationToDestinationByWalk());
		StepsArray stepsStationToDestinationByWalk = 
			create(stationsCloseToCurrent, stationsCloseToDestination, 
					stepsCurrentToStationByWalk, dddr.getStepsStationToDestinationByWalk());
		StepsArray stepsStationToDestinationByBicycle = 
			create(stationsCloseToCurrent, stationsCloseToDestination, 
					stepsCurrentToStationByBicycle, dddr.getStepsStationToDestinationByWalk());
		
		JSONObject directions = new JSONObject();
		directions.put("stationsCloseToCurrent", stationsCloseToCurrent.get(0).toJSONObject());
		directions.put("stationsCloseToDestination", stationsCloseToDestination.get(0).toJSONObject());

		directions = putSteps(directions, "sctdbc", stepsCurrentToDestinationByCar);
		directions = putSteps(directions, "sctdbw", stepsCurrentToDestinationByWalk);
		directions = putSteps(directions, "sctdbb", stepsCurrentToDestinationByBicycle);
		
		directions = putSteps(directions, "sctsbc", stepsCurrentToStationByCar);
		directions = putSteps(directions, "sctsbw", stepsCurrentToStationByWalk);
		directions = putSteps(directions, "sctsbb", stepsCurrentToStationByBicycle);

		directions = putSteps(directions, "sstdbc", stepsStationToDestinationByCar);
		directions = putSteps(directions, "sstdbw", stepsStationToDestinationByWalk);
		directions = putSteps(directions, "sstdbb", stepsStationToDestinationByBicycle);
		return directions;
	}
	
	public StepsArray create(List<StationObject> a, List<StationObject> b, 
			StepsArray g, List<StepObject> h){
		if(a == null || b == null || g == null || h == null){
			return null;
		}
		if(g.getArrival() < System.currentTimeMillis()){
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(g.getArrival());
		List<Long> trainTimes = TransferDownloader.getMoveTimes(
				a.get(0).getName(), b.get(0).getName(), c, pw);
		if(trainTimes == null || trainTimes.isEmpty()){
			return null;
		}
		return new StepsArray(h, g.getArrival() + trainTimes.get(0).longValue());
	}
	
	public JSONObject putSteps(JSONObject json, String key, StepsArray stepsArray) 
	throws JSONException{
		if(stepsArray == null){
			return json;
		}
		JSONObject jo = stepsArray.toJSON();
		if(jo != null){
			json.put(key, jo);
		}
		return json;
	}
	
	
	public static JSONArray getMovementResult(int devid)
			throws SQLException, JSONException {
		MovementResultConnector connector = null;
		try {
			connector = new MovementResultConnector(
					"jdbc:mysql://localhost:3306/movement_result?useUnicode=true&characterEncoding=utf8", 
					"plancheckeruser", "n0pr1vacy");
			connector.createConnection();
			
			return connector.selectMovementResult(devid);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			connector.close();
		}
		
		return null;
	}
}

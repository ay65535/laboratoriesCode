/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.createplan.plancreator;

import java.io.IOException;
import java.sql.SQLException;

import jp.ac.ritsumei.cs.ubi.createplan.database.MovementResultConnector;

//import jp.ac.ritsumei.cs.ubi.sacchin.geostatistics.CheckPoint;
//import jp.ac.ritsumei.cs.ubi.sacchin.geostatistics.MyGeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovementResultGetter {
//	[
//	    {},
//	    {},
//	    {},
//	    {
//	        "walk": [],
//	        "walkBicycleWalk": [],
//	        "walkTrainWalk": [],
//	        "walkBusWalk": [],
//	        "walkCarWalk": [],
//	        "walkBicycleWalkTrainWalk": [],
//	        "walkCarWalkTrainWalk": [],
//	        "walkBusWalkTrainWalk": [
//	            {
//	                "mode": "walk",
//	                "route": []
//	            },
//	            {
//	                "mode": "Bus",
//	                "route": []
//	            },
//	            {
//	                "mode": "Walk",
//	                "route": []
//	            },
//	            {
//	                "mode": "Train",
//	                "route": []
//	            },
//	            {
//	                "mode": "Walk",
//	                "route": [
//	                    {},
//	                    {},
//	                    
//	                        {
//	                            "transferpointA": {
//	                                "lat": "35.000",
//	                                "lng": "135.000"
//	                            },
//	                            "transferpointB": {
//	                                "lat": "35.000",
//	                                "lng": "135.000"
//	                            },
//	                            "checkpoint": [
//	                                {
//	                                    "name": "CP1"
//	                                },
//	                                {
//	                                    "name": "CP2",
//	                                    "probability": "0.5",
//	                                    "stayTime": "50",
//	                                    "lat": "35.000",
//	                                    "lng": "135.000"
//	                                }
//	                            ]
//	                        }
//	                    
//	                ]
//	            }
//	        ]
//	    }
//	]
	
	public static JSONArray selectMovementResult(int devid, int fromId, int toId){
		MovementResultConnector connector;
		try {
			connector = new MovementResultConnector(
					"jdbc:mysql://localhost:3306/movement_result?useUnicode=true&characterEncoding=utf8", 
					"plancheckeruser", "n0pr1vacy");
			connector.createConnection();
			
			return connector.selectMovemetResult(devid, fromId, toId);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static JSONObject createCheckPoint(String name, int sec, double probability, double lat, double lng) throws JSONException{
		JSONObject checkpoint = new JSONObject();
		checkpoint.put("name", name);
		checkpoint.put("stayTime", sec);
		checkpoint.put("probability", probability);
		checkpoint.put("lat", lat);
		checkpoint.put("lng", lng);
		return checkpoint;
	}
	
//	private static final long serialVersionUID = 1L;
//	
//	@Override
//	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//		request.setCharacterEncoding("utf-8");
//		int id = -1;
//		String reject = "";
//		try {
//			id = Integer.parseInt(request.getParameter("id"));
//			reject = request.getParameter("reject");
//		} catch (NumberFormatException e) {
//			System.out.println("NumberFormatException");
//		}
//		System.out.println("[CheckPointGetter] ID" + id + " : reject is " + reject);
//		
//		String[][] sqlMRArray = null;
//		String[] sqlCPArray = null;
//		switch (id) {
//		case 0:
//			sqlMRArray = MoveassistantConstants.createNishioBusKasa();
//			sqlCPArray = MoveassistantConstants.NISHIO_BUS_KASA_PATH;
//			break;
//		case 1:
//			sqlMRArray = MoveassistantConstants.createNishioBusNoji();
//			sqlCPArray = MoveassistantConstants.NISHIO_BUS_NOJI_PATH;
//			break;
//		case 2:
//			sqlMRArray = MoveassistantConstants.createNishioBusKaga();
//			sqlCPArray = MoveassistantConstants.NISHIO_BUS_KAGA_PATH;
//			break;
//		case 3:
//			sqlMRArray = MoveassistantConstants.createNishioBusPana();
//			sqlCPArray = MoveassistantConstants.NISHIO_BUS_PANA_PATH;
//			break;
//		default:
//			break;
//		}
//		System.out.println("sqlMRArray = " + sqlMRArray.length + ", sqlCPArray = " + sqlCPArray.length);
//		
//		try {
//			List<Integer> rejects = getNumbers(reject);
//			List<CheckPoint> points = createCheckPoints(sqlMRArray, sqlCPArray, rejects);
////			System.out.println("All MergeCheckpoint");
//			
//			JSONArray res = convertToJSONArray(points);
//			for(int i = 0 ; i < res.length() ; i++){
//				try {
//					JSONObject o = (JSONObject) res.get(i);
//					response.getWriter().println(o.toString());
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	private List<CheckPoint> createCheckPoints(String[][] movementResultSqls, String[] checkpointSqls, List<Integer> rejects) 
//	throws ClassNotFoundException, SQLException, IOException {
//		List<CheckPoint> checkpoints = CheckPointsCreater.createCheckpoints(movementResultSqls, checkpointSqls, rejects);
//
//		System.out.println(String.valueOf(checkpoints));
//		List<List<MyGeoPoint>> medianList = PlanCreater.getMedianPath(PlanCreater.createMultiplePath(movementResultSqls, null));
//		List<MyGeoPoint> mr = new ArrayList<MyGeoPoint>();
//		for(List<MyGeoPoint> temp : medianList){
//			mr.addAll(temp);
//		}
//		
////		index(checkpoints, mr);
//		
//		return checkpoints;
//	}
//	
////	/**
////	 * don't use
////	 * @param checkpoints
////	 * @param medianList
////	 */
////	private void index( List<CheckPoint> checkpoints, List<MyGeoPoint> medianList){
////		for(CheckPoint point : checkpoints){
////			double min = Double.MAX_VALUE;
////			int index = -1;
////			for(int i = 0 ; i < medianList.size() ; i++){
////				double dist = GeoPointUtils.calcDistanceHubery(
////						point.lat, point.lng, medianList.get(i).lat, medianList.get(i).lng, GeoPointUtils.GRS80);
////				if(dist < min){
////					min = dist;
////					index = i;
////				}
////			}
////			point.setOrder(index);
////		}
////	}
//	
//	public static JSONArray convertToJSONArray(List<CheckPoint> points){
//		JSONArray re = new JSONArray();
//		for(CheckPoint temp : points){
//			re.put(temp.getJSON());
//		}
//		return re;
//	}
//	
//	public List<Integer> getNumbers(String reject){
//		List<Integer> re = new ArrayList<Integer>();
//		if(reject == null){
//			return re;
//		}
//		String[] rejects = reject.split("|");
//		if(rejects == null){
//			return re;
//		}
//		for(String s : rejects){
//			try {
//				if(s.equals("")){
//					continue;
//				}
//				re.add(Integer.parseInt(s));
//			} catch (NumberFormatException e) {
//				System.out.println("NumberFormatException");
//			}
//		}
//		return re;
//	}
}

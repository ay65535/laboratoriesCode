package jp.ac.ritsumei.cs.ubi.zukky.BRM.main;

/**
 * さっちんさんがプログラムを呼び出すことを前提としたクラス
 * @author zukky
 *
 */
public class Main {
	public static void main(String[] args){
		
		
	}
//	public static void main(String[] args){
//	    }
//		/* ログを取ってくる期間を指定 */
//		Timestamp[][] times = {
//				{Timestamp.valueOf("2013-01-21 19:42:16.0"), Timestamp.valueOf("2013-01-21 19:46:11.0")},
//				{Timestamp.valueOf("2013-01-23 21:57:54.0"), Timestamp.valueOf("2013-01-23 22:09:18.0")},
//				{Timestamp.valueOf("2013-01-24 11:43:18.0"), Timestamp.valueOf("2013-01-24 12:45:24.0")},
//				{Timestamp.valueOf("2013-01-21 22:44:07.0"), Timestamp.valueOf("2013-01-21 23:23:16.0")}
//		};
//		Gps forest1 = new Gps(34.980709, 135.963439);
//		Gps forest2 = new Gps(34.980880, 135.963718);
//
//		Gps sacchin1 = new Gps(35.003315, 135.949035);
//		Gps sacchin2 = new Gps(35.003816 ,135.949588);
//
//		//スタート地点を指定
//		FeaturePoint startPoint = new FeaturePoint();
//		FeaturePoint endPoint = new FeaturePoint();
//
//		startPoint.setArrivalTime(times[0][0]);
//		startPoint.setDepartureTime(times[0][0]);
//
//		endPoint.setArrivalTime(times[0][1]);
//		endPoint.setDepartureTime(times[0][1]);
//
//		startPoint.setMinMaxLatLng(forest1, forest2);
//		endPoint.setMinMaxLatLng(sacchin1, sacchin2);
//
//		BehaviorRecognitionMaker behaviorRecognitionMaker= new BehaviorRecognitionMaker();
//		try {
//			behaviorRecognitionMaker.getBehaviorRecognition(times, 29);
//			System.out.println("movementResult-" + 
//					behaviorRecognitionMaker.getMovementResult().toString());
//			for(JSONArray jA : behaviorRecognitionMaker.getPathList()){
//				System.out.println("path-" + jA);
//			}
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
}

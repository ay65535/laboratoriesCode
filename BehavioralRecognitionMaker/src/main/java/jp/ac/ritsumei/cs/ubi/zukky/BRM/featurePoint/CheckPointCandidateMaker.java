package jp.ac.ritsumei.cs.ubi.zukky.BRM.featurePoint;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.featurePoint.FeaturePoint;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.hubenyVelocity.HubenyVelocityCalculator;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.kubiwa.Gps;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.transport.Transportation.TransportationType;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.util.Util;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.util.Util.VelocityType;

/**
 * チェックポイント候補を生成するクラス
 * @author zukky
 *
 */

public class CheckPointCandidateMaker {
	private final int TIME_THRESHOLD = 0;

	/**
	 * チェックポイント候補を生成するメソッド
	 * @param startTime
	 * @param endTime
	 * @param velocityThreshold
	 * @param distanceThreshold
	 * @param type
	 * @param routeNumber
	 * @return
	 */
	public List<FeaturePoint> makeCheckPoint(Timestamp startTime, Timestamp endTime, 
			double velocityThreshold, int distanceThreshold, List<Gps> rowGpsList, 
			TransportationType transportationType, int routeNumber){
		HubenyVelocityCalculator hubenyVelocityCalculator = new HubenyVelocityCalculator();
		CheckPointCandidateMaker checkPointMaker = new CheckPointCandidateMaker();

		VelocityType type = VelocityType.HUBENY;
		List<FeaturePoint> checkPointList = new ArrayList<FeaturePoint>();

		List<Gps> gpsList = new ArrayList<Gps>();
		for(Gps gps : rowGpsList){
			if(startTime.getTime() <= gps.getTime().getTime() &&
					gps.getTime().getTime() <= endTime.getTime()){
				gpsList.add(gps);
			}			
		}
		if(gpsList.isEmpty()){
			return checkPointList;
		}

		if(type.equals(VelocityType.DOPPLER)){
			List<Gps> gpsListByDoppler = 
					checkPointMaker.getFeaturePointByDopplerVelocity(gpsList, velocityThreshold);
			List<FeaturePoint> checkPointDoppler = 
					Clustering.convertGpsListToFeaturePointList(gpsListByDoppler);
			checkPointDoppler = Clustering.clusterNearGpsByCentroidMethod(checkPointDoppler, 
					distanceThreshold, routeNumber);
			checkPointList = cutLowStayingTimeCluster(checkPointDoppler, TIME_THRESHOLD);
			checkPointList = cutFirstCheckPoint(checkPointList);

		}else{
			List<Gps> gpsListByHubeny = hubenyVelocityCalculator.getGpsListFilteringHubeny(
					gpsList, velocityThreshold, Util.getSmoothingRateCheckPoint());
			if(gpsListByHubeny.isEmpty()){
				return checkPointList;
			}
			List<FeaturePoint> checkPointHubeny = Clustering.convertGpsListToFeaturePointList(
					gpsListByHubeny);
			checkPointList = Clustering.clusterNearGpsByCentroidMethod(checkPointHubeny, 
					distanceThreshold, routeNumber);
			checkPointList = cutLowStayingTimeCluster(checkPointList, TIME_THRESHOLD);	
		}
		return checkPointList;
	}

	/**
	 * ドップラー速度が一定以下のポイントを抽出するメソッド
	 * @param gpsList 解析したいGPSデータのリスト
	 * @return resultGpsList ドップラー速度が一定以下のGPSデータのリスト
	 */
	private List<Gps> getFeaturePointByDopplerVelocity(List<Gps>gpsList, double velocityThreshold){
		List<Gps> resultGpsList = new ArrayList<Gps>();

		for(Gps gps: gpsList){
			if(gps.getSpeed() <= velocityThreshold){
				resultGpsList.add(gps);
			}
		}
		return resultGpsList;
	}

	/**
	 * 滞在時間をキーとしてGPSクラスタを選別するメソッド
	 * @param FeaturePointList
	 * @param gpsList
	 * @return
	 */
	private List<FeaturePoint> cutLowStayingTimeCluster(List<FeaturePoint> featurePointList, int TimeShreshold){

		List<FeaturePoint> resultClusterList = new ArrayList<FeaturePoint>();

		for(FeaturePoint featurePoint: featurePointList){

			long stayingTime = calculateStayingTime(featurePoint);

			featurePoint.addStayingTime(stayingTime);

			if(stayingTime > TimeShreshold){
				resultClusterList.add(featurePoint);
			}
		}
		return resultClusterList;
	}

	private List<FeaturePoint> cutFirstCheckPoint(List<FeaturePoint> checkPointList){
		List<FeaturePoint> resultCheckPointList = new ArrayList<FeaturePoint>();

		for(int i=1; i<checkPointList.size(); i++){
			resultCheckPointList.add(checkPointList.get(i));
		}

		return resultCheckPointList;
	}

	/**
	 * Timestampから滞在時間（Long）を計算するメソッド
	 * @param featurePoint
	 * @param gpsList
	 * @return
	 */
	private long calculateStayingTime(FeaturePoint featurePoint){
		long arriavalTime = featurePoint.getArrivalTime().getTime();
		long departureTime = featurePoint.getDepartureTime().getTime();

		long diffMilliSec = departureTime - arriavalTime;

		/* ミリ秒を秒に変換 */
		long diffSec = diffMilliSec / 1000;

		return diffSec;
	}


}

package jp.ac.ritsumei.cs.ubi.zukky.BRM.featurePoint;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jp.ac.ritsumei.cs.ubi.zukky.BRM.featurePoint.FeaturePoint;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.hubenyVelocity.HubenyVelocityCalculator;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.kubiwa.Gps;

/**
 * 同一滞在地のGPSをクラスタリングするクラス
 * @author zukky
 *
 */
public class Clustering {
//	private final static Long STAYING_TIME_THRESHOLD = (long) 0;
	private final static int INITIAL_DISTANCE = 10000;
	private final static double ACC_SECOND_PER_METER = 0.039435287;
	private final static double ACC_DEGREE_PER_METER = ACC_SECOND_PER_METER / 3600;

	/**
	 * GPSクラスのリストをFeaturePointクラスのリストに変換するメソッド
	 * @param gpsList
	 * @return　FeaturePointList
	 */
	public static List<FeaturePoint> convertGpsListToFeaturePointList(List<Gps> gpsList){
		List<FeaturePoint> FeaturePointList = new ArrayList<FeaturePoint>();
		for(int i = 0 ; i < gpsList.size() ; i++){
			Gps gps = gpsList.get(i);
			FeaturePoint featurePoint = new FeaturePoint(gps);
			FeaturePointList.add(featurePoint);
		}
		return FeaturePointList;
	}

	/**
	 * 重心法を用いてGPSデータをクラスタリングするメソッド(隣り合った点のみをクラスタリングする)
	 * @param culsterGpsList クラスタリングしたいGPSのリスト
	 * @param thresholdDistance クラスタリングする距離の閾値
	 * @return cehckpointList （クラスタリングしたGPS = チェックポイント）のリスト
	 * @param routeNumber 何番目のルートか
	 */
	public static List<FeaturePoint> clusterNearGpsByCentroidMethod(
			List<FeaturePoint> featurePointList, int thresholdDistance, int routeNumber){

		double hubenyDistance = INITIAL_DISTANCE;
		double centroidLat = 0, centroidLng = 0;

		boolean arrivalPoint = true;
		Timestamp arrivalTime = null, departureTime = null;
		long stayingTime = 0;
		int count = 0;
		int j=0;
		for(int i=0; (i+1)<featurePointList.size(); i=j){
			count = i;
			if(arrivalPoint){
				stayingTime = 0;
				arrivalTime = featurePointList.get(i).getTime();
				featurePointList.get(i).setArrivalTime(arrivalTime);
				arrivalPoint = false;
			}

			hubenyDistance = HubenyVelocityCalculator.calcDistHubeny(
					featurePointList.get(i).getLat(), 
					featurePointList.get(i).getLng(), 
					featurePointList.get(i+1).getLat(), 
					featurePointList.get(i+1).getLng());


			if(hubenyDistance < thresholdDistance){
				//新しく作成したクラスタに重心をとった緯度経度を入れる

				centroidLat = (featurePointList.get(i).getLat() + featurePointList.get(i+1).getLat()) / 2;
				centroidLng = (featurePointList.get(i).getLng() + featurePointList.get(i+1).getLng()) / 2;	

				featurePointList.get(i).setLat(centroidLat);
				featurePointList.get(i).setLng(centroidLng);

				FeaturePoint tmp = judgeMaxMinLatLngData_clusterNearGps(
						featurePointList.get(i), featurePointList.get(i+1));

				featurePointList.get(i).setMinLat(tmp.getMinLat());
				featurePointList.get(i).setMaxLat(tmp.getMaxLat());
				featurePointList.get(i).setMinLng(tmp.getMinLng());
				featurePointList.get(i).setMaxLng(tmp.getMaxLng());

				Timestamp startTime = featurePointList.get(i).getTime();
				Timestamp endTime = featurePointList.get(i+1).getTime();

				stayingTime = (endTime.getTime() - startTime.getTime())/1000;

				stayingTime = stayingTime + featurePointList.get(i).getStayingTime();
				departureTime = featurePointList.get(i+1).getTime();

				featurePointList.get(i).setStayingTime(stayingTime);

				featurePointList.remove(i+1);
				j=i;

			}else{
				if(stayingTime == 0){
					featurePointList.get(i).setDepartureTime(featurePointList.get(i).getArrivalTime());
				}else{
					featurePointList.get(i).setDepartureTime(departureTime);
				} 
				arrivalPoint = true;
				j++;
			}
		}

		if(featurePointList.get(count).getDepartureTime() == null){
			featurePointList.get(count).setDepartureTime(departureTime);
			arrivalPoint = true;
		}

		return featurePointList;
	}


	/**
	 * 2つのチェックポイントのリストの中で、同一のチェックポイントをまとめるメソッド
	 * TODO: クラスタリングの実装は要見直しの必要あり！！
	 * @param beforeCluster
	 * @param nextCluster
	 * @return 
	 */
	public static List<FeaturePoint> clusterFeaturePoint(List<FeaturePoint> beforeClusterList, 
			List<FeaturePoint> nextClusterList, double thresholdDistance, int routeNumber){

		final int INITIAL_VALUE = 10000;
		double centroidLat = 0, centroidLng = 0;
		double minDistance = thresholdDistance;
		int beforeClusterListSize = beforeClusterList.size();
		
		int minDistancePointNumber = INITIAL_VALUE;

		double hubenyDistance;

		for(int i=0; i<nextClusterList.size(); i++){
			minDistance = thresholdDistance;
			
			for(int j=0; j<beforeClusterListSize; j++){
				hubenyDistance = HubenyVelocityCalculator.calcDistHubeny(
						beforeClusterList.get(j).getLat(), beforeClusterList.get(j).getLng(), 
						nextClusterList.get(i).getLat(), nextClusterList.get(i).getLng());
				if(hubenyDistance < minDistance){
					minDistancePointNumber = j;
					minDistance = hubenyDistance;
				}
			}

			if(minDistance < thresholdDistance){
				FeaturePoint nearest = beforeClusterList.get(minDistancePointNumber);
				centroidLat = (nearest.getLat() + nextClusterList.get(i).getLat()) / 2;
				centroidLng = (nearest.getLng() + nextClusterList.get(i).getLng()) / 2;

				nearest.setLat(centroidLat);
				nearest.setLng(centroidLng);

				FeaturePoint tmp = judgeMaxMinLatLngData_ClusterFeaturePoint(
						nearest, nextClusterList.get(i));

				nearest.setMinLat(tmp.getMinLat() - getAccDgreeFromAcc(tmp.getAcc()));
				nearest.setMaxLat(tmp.getMaxLat() + getAccDgreeFromAcc(tmp.getAcc()));
				nearest.setMinLng(tmp.getMinLng() - getAccDgreeFromAcc(tmp.getAcc()));
				nearest.setMaxLng(tmp.getMaxLng() + getAccDgreeFromAcc(tmp.getAcc()));

				nearest.addIdList(nextClusterList.get(i).getId());
				nearest.addStayTimeList(nextClusterList.get(i).getStayingTimeList());
			}else{
				beforeClusterList.add(nextClusterList.get(i));
			}
		}
		return beforeClusterList;
	}


	private static FeaturePoint judgeMaxMinLatLngData_clusterNearGps(FeaturePoint checkPoint, FeaturePoint newGps){
		FeaturePoint resultPoint = new FeaturePoint();

		if(newGps.getLat() < checkPoint.getMinLat() || checkPoint.getMinLat() == FeaturePoint.INITIAL_DEGREE){
			resultPoint.setMinLat(newGps.getLat());
			resultPoint.setAcc(newGps.getAcc());
		}else{
			resultPoint.setMinLat(checkPoint.getMinLat());
			resultPoint.setAcc(checkPoint.getAcc());
		}if(checkPoint.getMaxLat() < newGps.getLat() || checkPoint.getMaxLat() == FeaturePoint.INITIAL_DEGREE){
			resultPoint.setMaxLat(newGps.getLat());
			resultPoint.setAcc(newGps.getAcc());
		}else{
			resultPoint.setMaxLat(checkPoint.getMaxLat());
			resultPoint.setAcc(checkPoint.getAcc());
		}

		if(newGps.getLng() < checkPoint.getMinLng() || checkPoint.getMinLng() == FeaturePoint.INITIAL_DEGREE){
			resultPoint.setMinLng(newGps.getLng());
			resultPoint.setAcc(newGps.getAcc());
		}else{
			resultPoint.setMinLng(checkPoint.getMinLng());
			resultPoint.setAcc(checkPoint.getAcc());
		}if(checkPoint.getMaxLng() < newGps.getLng() || checkPoint.getMaxLng() == FeaturePoint.INITIAL_DEGREE){
			resultPoint.setMaxLng(newGps.getLng());
			resultPoint.setAcc(newGps.getAcc());
		}else{
			resultPoint.setMaxLng(checkPoint.getMaxLng());
			resultPoint.setAcc(checkPoint.getAcc());
		}

		return resultPoint;
	}

	private static FeaturePoint judgeMaxMinLatLngData_ClusterFeaturePoint(FeaturePoint checkPoint, FeaturePoint newFeaturePoint){
		FeaturePoint resultPoint = new FeaturePoint();

		if(newFeaturePoint.getMinLat() < checkPoint.getMinLat() || checkPoint.getMinLat() == FeaturePoint.INITIAL_DEGREE){
			resultPoint.setMinLat(newFeaturePoint.getMinLat());
		}else{
			resultPoint.setMinLat(checkPoint.getMinLat());
		}if(checkPoint.getMaxLat() < newFeaturePoint.getMaxLat() || checkPoint.getMaxLat() == FeaturePoint.INITIAL_DEGREE){
			resultPoint.setMaxLat(newFeaturePoint.getMaxLat());
		}else{
			resultPoint.setMaxLat(checkPoint.getMaxLat());
		}

		if(newFeaturePoint.getMinLng() < checkPoint.getMinLng() || checkPoint.getMinLng() == FeaturePoint.INITIAL_DEGREE){
			resultPoint.setMinLng(newFeaturePoint.getMinLng());
		}else{
			resultPoint.setMinLng(checkPoint.getMinLng());
		}if(checkPoint.getMaxLng() < newFeaturePoint.getMaxLng() || checkPoint.getMaxLng() == FeaturePoint.INITIAL_DEGREE){
			resultPoint.setMaxLng(newFeaturePoint.getMaxLng());
		}else{
			resultPoint.setMaxLng(checkPoint.getMaxLng());
		}

		return resultPoint;
	}


	/**
	 * gpsのaccuracyを緯度経度の大きさに変換するメソッド
	 * （注）経度３５度付近のデータを決め打ちして計算しているので日本以外では微妙
	 * @param acc
	 * @return
	 */
	private static double getAccDgreeFromAcc(float acc){

		double accDegree = 0;

		if(acc <= 8){
			accDegree = acc *  ACC_DEGREE_PER_METER; 		
		}else{
			accDegree = 8 *  ACC_DEGREE_PER_METER; 		
		}
	
		return accDegree;
	}

}

///**
// * 2つのチェックポイントのリストの中で、同一のチェックポイントをまとめるメソッド
// * TODO: クラスタリングの実装は要見直しの必要あり！！
// * @param beforeCluster
// * @param nextCluster
// * @return 
// */
//public List<FeaturePoint> clusterFeaturePoint(List<FeaturePoint> beforeClusterList, List<FeaturePoint> nextClusterList, double threshold, int routeNumber){
//
//	List<FeaturePoint> FeaturePointList = new ArrayList<FeaturePoint>();
//
//	for(FeaturePoint point: beforeClusterList) FeaturePointList.add(point);
//	for(FeaturePoint point: nextClusterList) FeaturePointList.add(point);
//
//	double centroidLat = 0;
//	double centroidLng = 0;
//
//	double hubenyDistance;
//	boolean TERMINATED = false;
//
//	while(!TERMINATED){
//		TERMINATED = true;
//		for(int i=0; i<FeaturePointList.size(); i++){
//			for(int j=i+1; j<FeaturePointList.size(); j++){
//
//				hubenyDistance = HubenyVelocityCalculator.calcDistHubeny(
//						FeaturePointList.get(i).getLat(), FeaturePointList.get(i).getLng(), 
//						FeaturePointList.get(j).getLat(), FeaturePointList.get(j).getLng());
//
//				if(hubenyDistance < threshold){
//					centroidLat = (FeaturePointList.get(i).getLat() + FeaturePointList.get(j).getLat()) / 2;
//					centroidLng = (FeaturePointList.get(i).getLng() + FeaturePointList.get(j).getLng()) / 2;
//
//					FeaturePoint newFeaturePoint = new FeaturePoint();
//					newFeaturePoint.setLat(centroidLat);
//					newFeaturePoint.setLng(centroidLng);
//
//					FeaturePoint tmp = judgeMaxMinLatLngData_ClusterFeaturePoint(FeaturePointList.get(i), FeaturePointList.get(j));
//					
//					
//					newFeaturePoint.setMinLat(tmp.getMinLat() - getAccDgreeFromAcc(tmp.getAcc()));
//					newFeaturePoint.setMaxLat(tmp.getMaxLat() + getAccDgreeFromAcc(tmp.getAcc()));
//					newFeaturePoint.setMinLng(tmp.getMinLng() - getAccDgreeFromAcc(tmp.getAcc()));
//					newFeaturePoint.setMaxLng(tmp.getMaxLng() + getAccDgreeFromAcc(tmp.getAcc()));
//
//					newFeaturePoint.setDetailedType(FeaturePointList.get(i).getDetailedType());
//
//					newFeaturePoint.setStayingTimeList(FeaturePointList.get(i).getStayingTimeList());
//			
//					
//					FeaturePointList.add(newFeaturePoint);
//					FeaturePointList.remove(i);
//					FeaturePointList.remove(j-1);
//					TERMINATED = false;
//					
//				}
//			} 
//		}
//	}
//
//	return FeaturePointList;
//
//}

//	/**
//	 * 重心法を用いてGPSデータをクラスタリングするメソッド
//	 * @param gpsList クラスタリングしたいGPSのリスト
//	 * @return cehckpointList （クラスタリングしたGPS = チェックポイント）のリスト
//	 */
//	public List<FeaturePoint> FeaturePointByCentroidMethod(List<FeaturePoint> FeaturePointList, int thresholdDistance){
//
//		double minDistance = INITIAL_DISTANCE;
//		double hubenyDistance = INITIAL_DISTANCE;
//		int minIndex1 = 0;
//		int minIndex2 = 0;
//		double centroidLat = 0;
//		double centroidLng = 0;
//
//		/* 2点間の距離が一定以下になるまでクラスタリングを続ける */
//		do{	
//			//最も近い２点をクラスタとしてまとめる
//			if(minDistance != INITIAL_DISTANCE){
//
//				FeaturePoint cluster = new FeaturePoint();
//
//				//新しく作成したクラスタに重心をとった緯度経度を入れる
//				centroidLat = (FeaturePointList.get(minIndex1).getLat() + FeaturePointList.get(minIndex2).getLat()) / 2;
//				centroidLng = (FeaturePointList.get(minIndex1).getLng() + FeaturePointList.get(minIndex2).getLng()) / 2;
//
//				cluster.setLat(centroidLat);
//				cluster.setLng(centroidLng);
//
//				//クラスタに含まれているGPSListのインデックスを統合する
//				List<Integer> indexList1 = FeaturePointList.get(minIndex1).getIndexList();
//				List<Integer> indexList2 = FeaturePointList.get(minIndex2).getIndexList();
//
//				//indexリストを昇順に挿入
//				for(int i: indexList1){
//					if(cluster.getIndexList().size() == 0){
//						cluster.addIndexList(i);
//					}else{
//						for(int j=0; j<cluster.getIndexList().size(); j++){
//
//							if(i < cluster.getIndexList().get(j)){
//								cluster.addIndexList(j, i);
//								break;
//							}
//							if(j == cluster.getIndexList().size() -1){
//								cluster.addIndexList(i);
//								break;
//							}
//						}
//					}
//				}
//
//				for(int i: indexList2){
//					if(cluster.getIndexList().size() == 0){
//						cluster.addIndexList(i);
//					}
//					for(int j=0; j<cluster.getIndexList().size(); j++){
//						if(i < cluster.getIndexList().get(j)){
//							cluster.addIndexList(j, i);
//							break;
//						}
//						if(j == cluster.getIndexList().size() -1){
//							cluster.addIndexList(i);
//							break;
//						}
//					}
//				}
//
//				//統合したクラスタの元のクラスタを削除
//				FeaturePointList.remove(minIndex1);
//				FeaturePointList.remove(minIndex2 - 1);
//				FeaturePointList.add(cluster);
//			}
//
//			minDistance = INITIAL_DISTANCE;
//
//			for(int i=0; i<FeaturePointList.size()-1; i++){
//				for(int j=i+1; j<FeaturePointList.size(); j++){
//					hubenyDistance = HubenyVelocityCalculator.calcDistHubeny(
//							FeaturePointList.get(i).getLat(), 
//							FeaturePointList.get(i).getLng(), 
//							FeaturePointList.get(j).getLat(), 
//							FeaturePointList.get(j).getLng());
//
//					if(hubenyDistance < minDistance){
//						minDistance = hubenyDistance;
//						minIndex1 = i;
//						minIndex2 = j;
//					}
//				}
//			}
//			//System.out.println("minDistance :" + minDistance);
//		}while(minDistance < thresholdDistance);
//
//		return FeaturePointList;
//	}

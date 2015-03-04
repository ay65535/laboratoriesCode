package jp.ac.ritsumei.cs.ubi.zukky.BRM.transhipment;

import java.util.ArrayList;
import java.util.List;

import jp.ac.ritsumei.cs.ubi.zukky.BRM.featurePoint.FeaturePoint;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.featurePoint.FeaturePoint.DetailedType;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.kubiwa.Gps;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.transport.Transportation;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.transport.Transportation.TransportationType;


/**
 * 乗り換えポイントを抽出するクラス
 * @author zukky
 *
 */
public class TranshipmentPointMaker{

	private final double ACC_SECOND_PER_METER = 0.039435287;
	private final double ACC_DEGREE_PER_METER = ACC_SECOND_PER_METER / 3600;
	
	/**
	 * 乗り換えポイントを抽出するメソッド
	 * @param gpsList
	 * @param transportationList
	 * @return
	 */
	public List<FeaturePoint> getTranshipmentPoint(List<Gps> gpsList, List<Transportation> transportationList, int routeNumber){
		List<FeaturePoint> transhipmentList = new ArrayList<FeaturePoint>();

		boolean firstTransport = true;
		boolean firstTime = true;
		
		for(Transportation ts: transportationList){
			firstTime = true;
			if(!firstTransport){
				FeaturePoint featurePoint = new FeaturePoint();
				for(Gps gps: gpsList){
					if(ts.getStartTime().getTime() < gps.getTime().getTime() && firstTime){
						firstTime = false;
						featurePoint.setLat(gps.getLat());
						featurePoint.setLng(gps.getLng());

						//accuracyの矩形で囲む
						featurePoint.setMaxLat(gps.getLat() + getAccDgreeFromAcc(gps.getAcc()));
						featurePoint.setMinLat(gps.getLat() - getAccDgreeFromAcc(gps.getAcc()));
						featurePoint.setMaxLng(gps.getLng() + getAccDgreeFromAcc(gps.getAcc()));
						featurePoint.setMinLng(gps.getLng() - getAccDgreeFromAcc(gps.getAcc()));

						featurePoint.setTime(gps.getTime());
						featurePoint.setArrivalTime(gps.getTime());
						featurePoint.setDepartureTime(gps.getTime());

						featurePoint.setDetailedType(judgeTranshipmentType(ts));
				
						transhipmentList.add(featurePoint);
					}
				}
			}
			firstTransport = false;
		}
		
		return transhipmentList;
	}
	
	public List<FeaturePoint> giveTpId(List<FeaturePoint> transhipmentPointList){
		
		for(int i=0; i<transhipmentPointList.size(); i++){
			transhipmentPointList.get(i).setId(i);
		}
		
		return transhipmentPointList;
	}
	

	/**
	 * 乗り換えポイントの種類を判別するメソッド
	 * @param ts
	 * @return
	 */
	private DetailedType judgeTranshipmentType(Transportation ts){
		
		if(ts.getType().equals(TransportationType.BUS)){
			return DetailedType.BUS_STOP;
		}else if(ts.getType().equals(TransportationType.TRAIN)){
			return DetailedType.STATION;
		}else if(ts.getType().equals(TransportationType.BYCYECLE)){
			return DetailedType.CYCLE_PARKING;
		}
		return DetailedType.UNKNOWN;
	}
	
	/**
	 * gpsのaccuracyを緯度経度の大きさに変換するメソッド
	 * （注）経度３５度付近のデータを決め打ちして計算しているので日本以外では微妙
	 * @param acc
	 * @return
	 */
	private double getAccDgreeFromAcc(float acc){
		
		double accDegree = 0;
		
		if(acc <= 8){
			accDegree = acc *  ACC_DEGREE_PER_METER; 		
		}else{
			accDegree = 8 *  ACC_DEGREE_PER_METER; 		
		}
		return accDegree;
	}
	
	
}

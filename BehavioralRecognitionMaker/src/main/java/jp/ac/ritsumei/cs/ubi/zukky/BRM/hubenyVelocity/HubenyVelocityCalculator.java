package jp.ac.ritsumei.cs.ubi.zukky.BRM.hubenyVelocity;


import java.util.ArrayList;
import java.util.List;

import jp.ac.ritsumei.cs.ubi.zukky.BRM.kubiwa.Gps;

/* 著作権表示の書き方については，要確認の必要あり */
/*	Portions are:
 * 	Copyright © 2007-2012 やまだらけ
 *
 */

/**
 * ヒュベニ速度を計算するクラス
 * @author zukky
 *
 */
public class HubenyVelocityCalculator {

	public static final double BESSEL_A = 6377397.155;
	public static final double BESSEL_E2 = 0.00667436061028297;
	public static final double BESSEL_MNUM = 6334832.10663254;

	public static final double GRS80_A = 6378137.000;
	public static final double GRS80_E2 = 0.00669438002301188;
	public static final double GRS80_MNUM = 6335439.32708317;

	public static final double WGS84_A = 6378137.000;
	public static final double WGS84_E2 = 0.00669437999019758;
	public static final double WGS84_MNUM = 6335439.32729246;

	public static final int BESSEL = 0;
	public static final int GRS80 = 1;
	public static final int WGS84 = 2;

	public static double deg2rad(double deg){
		return deg * Math.PI / 180.0;
	}

	public static double calcDistHubeny(double lat1, double lng1,
			double lat2, double lng2,
			double a, double e2, double mnum){
		double my = deg2rad((lat1 + lat2) / 2.0);
		double dy = deg2rad(lat1 - lat2);
		double dx = deg2rad(lng1 - lng2);

		double sin = Math.sin(my);
		double w = Math.sqrt(1.0 - e2 * sin * sin);
		double m = mnum / (w * w * w);
		double n = a / w;

		double dym = dy * m;
		double dxncos = dx * n * Math.cos(my);

		return Math.sqrt(dym * dym + dxncos * dxncos);
	}

	public static double calcDistHubeny(double lat1, double lng1,
			double lat2, double lng2){
		return calcDistHubeny(lat1, lng1, lat2, lng2,
				GRS80_A, GRS80_E2, GRS80_MNUM);
	}

	public static double calcDistHubery(double lat1, double lng1,
			double lat2, double lng2, int type){
		switch(type){
		case BESSEL:
			return calcDistHubeny(lat1, lng1, lat2, lng2,
					BESSEL_A, BESSEL_E2, BESSEL_MNUM);
		case WGS84:
			return calcDistHubeny(lat1, lng1, lat2, lng2,
					WGS84_A, WGS84_E2, WGS84_MNUM);
		default:
			return calcDistHubeny(lat1, lng1, lat2, lng2,
					GRS80_A, GRS80_E2, GRS80_MNUM);
		}
	}

	public double calculateHubenyVelocity(Gps gps1, Gps gps2){

		double lat1 = gps1.getLat();
		double lng1 = gps1.getLng();
		double lat2 = gps2.getLat();
		double lng2 = gps2.getLng();

		//ヒュベニの公式を用いてヒュベニ距離を算出
		double distance = calcDistHubeny(lat1, lng1, lat2, lng2);

		/* ２点間の移動時間をミリ秒で算出 */
		//		  System.out.println("gps2.getTime() " + gps2.getTime());
		//		  System.out.println("gps1.getTime() " + gps1.getTime());

		Long diffMilliSec = gps2.getTime().getTime() - gps1.getTime().getTime();

		/* ミリ秒を秒に変換 */
		Long diffSec = diffMilliSec / 1000L;


		double hubeneyVelocity = 0.0;

		if(distance != 0.0){
			hubeneyVelocity = distance / diffSec;
		}


		//時速に変換
		hubeneyVelocity = hubeneyVelocity * 3.6;

		return hubeneyVelocity;

	}



	/**
	 * ヒュベニ速度を計算し、取得するメソッド
	 * @param gpsList
	 * @param smoothingRate
	 * @return resultHubenyVelocityList 引数にとったgpsListに対応するヒュベニ速度のリスト
	 */
	public List<HubenyVelocity> getHubenyVelocityList(List<Gps> gpsList, double smoothingRate){

		List<HubenyVelocity> resultHubenyVelocityList = new ArrayList<HubenyVelocity>();

		Gps previousGpsData = new Gps();
		Gps nextGpsData = new Gps();
		double velocity = 0.0;
		double velocityBefore = 0.0;

		HubenyVelocityCalculator velocityCalculator = new HubenyVelocityCalculator();

		for(int i=0; i<gpsList.size(); i++){
			HubenyVelocity hubenyVelocity = new HubenyVelocity();

			nextGpsData = gpsList.get(i);

			if(i != 0){
				velocity
				= velocityCalculator.calculateHubenyVelocity(previousGpsData, nextGpsData);

				if(1 < i){
					velocity = smoothingHubenyVelocity(velocityBefore, velocity, smoothingRate);
					velocityBefore = velocity;
				}

				hubenyVelocity.setStartTime(previousGpsData.getTime());
				hubenyVelocity.setEndTime(nextGpsData.getTime());

				hubenyVelocity.setVelocity(velocity);

				resultHubenyVelocityList.add(hubenyVelocity);
			}
			previousGpsData = nextGpsData;
		}

		return resultHubenyVelocityList;
	}

	/**
	 * ヒュベニ速度が一定以下のポイントを抽出するメソッド
	 * @param gpsList 解析したいGPSデータのリスト
	 * @param HubenyVelocityThreshold 抽出したい速度の閾値
	 * @return resultGpsList ヒュベニ速度が一定以下のGPSデータのリスト
	 */
	public List<Gps> getGpsListFilteringHubeny(List<Gps> gpsList, double hubenyVelocityThreshold, double smoothingRate){

		List<Gps> resultGpsList = new ArrayList<Gps>();

		int newestIndex = 0;

		Gps previousGpsData = new Gps();
		Gps nextGpsData = new Gps();
		double hubenyVelocity = 0.0;
		double hubenyVelocityBefore = 0.0;

		HubenyVelocityCalculator velocityCalculator = new HubenyVelocityCalculator();

		for(int i=0; i<gpsList.size(); i++){
			nextGpsData = gpsList.get(i);
			if(i != 0){
				hubenyVelocity
				= velocityCalculator.calculateHubenyVelocity(previousGpsData, nextGpsData);

				if(1 < i){
					hubenyVelocity = smoothingHubenyVelocity(hubenyVelocityBefore, hubenyVelocity, smoothingRate);
					//System.out.println("hubenyVelocity " + hubenyVelocity);
					hubenyVelocityBefore = hubenyVelocity;
				}

				if(hubenyVelocity < hubenyVelocityThreshold){
					newestIndex = resultGpsList.size() -1;

					//取得したデータの重複を避ける
					if(newestIndex <= 0){
						resultGpsList.add(previousGpsData);
						resultGpsList.add(nextGpsData);
					}else if(!resultGpsList.get(newestIndex).getTime().equals(previousGpsData.getTime())){
						resultGpsList.add(previousGpsData);
						resultGpsList.add(nextGpsData);
					}else{
						resultGpsList.add(nextGpsData);
					}	
				}	
			}
			previousGpsData = nextGpsData;
		}
		return resultGpsList;
	}

	/**
	 * ヒュベニ速度をスムージングするメソッド
	 * @param HubeneyVelocityBefore
	 * @param HubeneyVelocityNext
	 * @param smoothingRate
	 * @return
	 */
	private double smoothingHubenyVelocity(double HubeneyVelocityBefore, double HubeneyVelocityNext, double smoothingRate){
		double HubeneyVelocity = (1.0 - smoothingRate) * HubeneyVelocityBefore + HubeneyVelocityNext * smoothingRate;	  
		return HubeneyVelocity;
	}

}

/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule.plan;

import java.util.ArrayList;
import java.util.List;

public class GeoPointUtils {
	private static final double BESSEL_A = 6377397.155;
	private static final double BESSEL_E2 = 0.00667436061028297;
	private static final double BESSEL_MNUM = 6334832.10663254;

	public static final double GRS80_A = 6378137.000;
	private static final double GRS80_E2 = 0.00669438002301188;
	private static final double GRS80_MNUM = 6335439.32708317;

	private static final double WGS84_A = 6378137.000;	//�����a
	private static final double WGS84_E2 = 0.00669437999019758;	//��ꗣ�S��
	private static final double WGS84_MNUM = 6335439.32729246;	

	private static final int BESSEL = 0;
	public static final int GRS80 = 1;
	private static final int WGS84 = 2;
	
	private boolean doDraw;
	private String areaColor;
	
	/**
	 * 	�P��	��ϊ����郁�\�b�h(�x�����W�A��)
	 * @param deg
	 * @return rad
	 */
	public static double convertDegToRad(double deg){
		return deg * Math.PI / 180.0;
	}
	
	/**
	 * �ړ��O�Ղ̎w�肳�ꂽ��Ԃ̗ݐ�2�_�ԋ������v�Z���郁�\�b�h
	 * 
	 * @param trajectory �ړ��O��
	 * @param start �ړ��O�Ղ�start�Ԗڂ̗v�f����v�Z���n�߂�
	 * @param end �ړ��O�Ղ�end�Ԗڂ̗v�f�܂Ōv�Z����
	 * @return �ݐϋ���(m)
	 */
	public static double calculateTotalDistance(ArrayList<MyGeoPoint> trajectory, int start, int end ){
		double distance = 0;
		if(-1 < start && start < trajectory.size() && 0 < end && end < trajectory.size() && start < end){
			for(int i = start ; i < end ; i++) {
				distance += calcDistanceHubery(
						trajectory.get(i).getLatitude(), trajectory.get(i).getLongtitude(), 
						trajectory.get(i+1).getLatitude(), trajectory.get(i+1).getLongtitude(), GRS80);
			}
		}
		return distance;
	}

	/**
	 * �q���x�j�̌����ŋ������v�Z���镔��
	 * @param lat1 �n�_�̈ܓx
	 * @param lng1 �n�_�̌o�x
	 * @param lat2 �I�_�̈ܓx
	 * @param lng2 �I�_�̌o�x
	 * @param a
	 * @param e2
	 * @param mnum
	 * @return
	 */
	public static double calcDistOfHubeny(double lat1, double lng1, double lat2, double lng2, double a, double e2, double mnum){
		double my = convertDegToRad( (lat1 + lat2) / 2.0);
		double dy = convertDegToRad( lat1 - lat2 );
		double dx = convertDegToRad( lng1 - lng2 );

		double sin = Math.sin(my);
		double w = Math.sqrt(1.0 - e2 * sin * sin);
		double m = mnum / (w * w * w);
		double n = a / w;

		double dym = dy * m;
		double dxncos = dx * n * Math.cos(my);

		return Math.sqrt(dym * dym + dxncos * dxncos);
	}

	/**
	 * 3���(BESSEL_MNUM, WGS84_MNUM, GRS80_MNUM)����I���ł���BGRS80_MNUM�����B
	 * @param lat1 �n�_�̈ܓx
	 * @param lng1 �n�_�̌o�x
	 * @param lat2 �I�_�̈ܓx
	 * @param lng2 �I�_�̌o�x
	 * @param type �v�Z�@
	 * @return 2�_�ԋ���(m)
	 */
	public static double calcDistanceHubery(double lat1, double lng1, double lat2, double lng2, int type){
		switch(type){
		case BESSEL:
			return calcDistOfHubeny(lat1, lng1, lat2, lng2, BESSEL_A, BESSEL_E2, BESSEL_MNUM);
		case WGS84:
			return calcDistOfHubeny(lat1, lng1, lat2, lng2, WGS84_A, WGS84_E2, WGS84_MNUM);
		case GRS80:
			return calcDistOfHubeny(lat1, lng1, lat2, lng2, GRS80_A, GRS80_E2, GRS80_MNUM);
		default:
			return -1.0;
		}
	}
	
	public static double[] calculateRect(double lat, double lng, int radius){
		double latlng[] = new double[4];
		latlng[0] = 
			(-1 * radius / GeoPointUtils.GRS80_A + GeoPointUtils.convertDegToRad(lat)) * 
			(180 / Math.PI);
		latlng[1] = 
			(-1 * radius / GeoPointUtils.GRS80_A * Math.cos(GeoPointUtils.convertDegToRad(lat)) + GeoPointUtils.convertDegToRad(lng)) * 
			(180 / Math.PI);
		latlng[2] = 
			(radius / GeoPointUtils.GRS80_A + GeoPointUtils.convertDegToRad(lat)) * 
			(180 / Math.PI);
		latlng[3] = 
			(radius / GeoPointUtils.GRS80_A * Math.cos(GeoPointUtils.convertDegToRad(lat)) + GeoPointUtils.convertDegToRad(lng)) * 
			(180 / Math.PI);
		return latlng;
	}
	
	public boolean getDoDraw() {
		return doDraw;
	}

	public void setDoDraw(boolean doDraw) {
		this.doDraw = doDraw;
	}

	public String getAreaColor() {
		return areaColor;
	}

	public void setAreaColor(String areaColor) {
		this.areaColor = areaColor;
	}
	
	public static long getTime(List<MyGeoPoint> trajectory){
		if(trajectory == null || trajectory.isEmpty()){
			return -1;
		}
		
		return (trajectory.get(trajectory.size() - 1).getTimestamp().getTime() - 
		trajectory.get(0).getTimestamp().getTime()) / 1000;
	}
	
	public static long calcArivalAverage(List<List<MyGeoPoint>> trajectories, int reject){
		if(trajectories == null || trajectories.isEmpty()){
			return 0;
		}
		
		int divide = 0;
		if(0 <= reject && reject < trajectories.size()){
			divide = trajectories.size() - 1;
		}else{
			divide = trajectories.size();
		}
		
		long sum = 0;
		for(int i = 0 ; i < trajectories.size() ; i++){
			if(reject != i){
				List<MyGeoPoint> t = trajectories.get(i);
//				System.out.println(i + " : " + getTime(t));
				sum += getTime(t);
			}
		}
		return sum / divide;
	}
	
	public static double calcAraivalStandardDeviation(long average, 
			List<List<MyGeoPoint>> trajectories, int reject){
		if(trajectories == null || trajectories.isEmpty()){
			return 0;
		}
		
		int divide = 0;
		if(0 <= reject && reject < trajectories.size()){
			divide = trajectories.size() - 1;
		}else{
			divide = trajectories.size();
		}
		
		double sum = 0;
		for(int i = 0 ; i < trajectories.size() ; i++){
			if(reject != i){
				List<MyGeoPoint> t = trajectories.get(i);
				long diff = average - getTime(t);
//				System.out.println(i + " : " + diff);
				sum += diff * diff;
			}
		}

		return Math.sqrt(sum / divide);
	}
	
	public static double calcHuberySpeed(MyGeoPoint p1, MyGeoPoint p2){
		long nowTime = p1.getTimestamp().getTime();
		long nextTime = p2.getTimestamp().getTime();
		
		double dist = GeoPointUtils.calcDistanceHubery(p1.lat, p1.lng, 
				p2.lat, p2.lng, GeoPointUtils.GRS80);
		
		return dist / ((nextTime - nowTime) / 1000);
	}
}
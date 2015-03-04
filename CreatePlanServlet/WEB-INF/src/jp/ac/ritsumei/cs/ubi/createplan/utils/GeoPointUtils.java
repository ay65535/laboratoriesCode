/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.createplan.utils;

public class GeoPointUtils {
	private static final double BESSEL_A = 6377397.155;
	private static final double BESSEL_E2 = 0.00667436061028297;
	private static final double BESSEL_MNUM = 6334832.10663254;

	public static final double GRS80_A = 6378137.000;
	private static final double GRS80_E2 = 0.00669438002301188;
	private static final double GRS80_MNUM = 6335439.32708317;

	private static final double WGS84_A = 6378137.000;
	private static final double WGS84_E2 = 0.00669437999019758;
	private static final double WGS84_MNUM = 6335439.32729246;	

	private static final int BESSEL = 0;
	public static final int GRS80 = 1;
	private static final int WGS84 = 2;

	public static boolean isOverlap(double latlngA[], double latlngB[]){
		if(latlngA == null || latlngB == null){
			return false;
		}
		if(latlngA.length < 4 || latlngB.length < 4){
			return false;
		}
		
		return !(latlngA[0] > latlngB[2] || latlngA[0] > latlngB[3] || latlngA[2] < latlngB[0] || latlngA[3] < latlngB[1]);
	}

	public static double convertDegToRad(double deg){
		return deg * Math.PI / 180.0;
	}

	private static double calcDistOfHubeny(double lat1, double lng1, double lat2, double lng2, double a, double e2, double mnum){
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
}
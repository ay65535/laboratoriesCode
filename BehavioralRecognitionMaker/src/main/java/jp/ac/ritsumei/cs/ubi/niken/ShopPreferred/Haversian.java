package jp.ac.ritsumei.cs.ubi.niken.ShopPreferred;
public class Haversian {
	
	public static double calcDistance(double lat2, double lng2, double lat1, double lng1){
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;
	    double meterConversion = 1609;
	    double distance = dist * meterConversion;
	   	return distance;
	}
	
	
}
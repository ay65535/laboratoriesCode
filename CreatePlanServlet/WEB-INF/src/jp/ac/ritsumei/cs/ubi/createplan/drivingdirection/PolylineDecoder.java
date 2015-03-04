/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.createplan.drivingdirection;

import java.util.ArrayList;
import java.util.List;

import jp.ac.ritsumei.cs.ubi.createplan.utils.MyGeoPoint;


public class PolylineDecoder {
	public static String encodeLatLng(double lat, double lng){
		return encoderPosi((int)(lat * 1E6)).toString() + 
		encoderPosi((int)(lng * 1E6)).toString();
	}
	
	public static StringBuffer encoderPosi(int num){
		ArrayList<Byte> bit =new ArrayList<Byte>();
		StringBuffer ret = new StringBuffer();
		num = num << 1;

		if(num <0){
			num =~ num;
		}
		for(int i = 0 ; i < 6 ; i++){ 
			bit.add((byte)(num&0x1f));
			num=num>>>5;
		}
		for(int i=bit.size()-1;i>=0;i--){
			byte n=bit.get(i);
			if(n==0){
				bit.remove(i);
			}else{
				break;
			}
		}
		for(int i=0;i<bit.size()-1;i++){
			bit.set(i, (byte) (bit.get(i)|0x20));
		}
		for(int i=0;i<bit.size();i++){
			bit.set(i, (byte) (bit.get(i)+63));
			if(bit.get(i)=='\\'){
				bit.add(i+1, (byte) '\\'); 
				i++;
			}
		}
		for(int i=0;i<bit.size();i++){
			ret.append((char)(bit.get(i).byteValue()));
		}
		return ret;
	}
	
	public static List<MyGeoPoint> decodePoly(String encoded) {
		List<MyGeoPoint> poly = new ArrayList<MyGeoPoint>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;
		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = (result & 1) != 0 ? ~(result >> 1) : (result >> 1);
			lat += dlat;
			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = (result & 1) != 0 ? ~(result >> 1) : (result >> 1);
			lng += dlng;
			MyGeoPoint p = new MyGeoPoint(0, 
					((double) lat / 1E5), ((double) lng / 1E5), 0, 0);
			poly.add(p);
		}
		return poly;
	}
}

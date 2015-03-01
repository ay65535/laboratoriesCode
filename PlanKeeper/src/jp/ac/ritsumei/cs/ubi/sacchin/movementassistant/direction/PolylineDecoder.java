/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.direction;

import java.util.ArrayList;
import java.util.List;

import jp.ac.ritsumei.cs.ubi.sacchin.movementassistant.utils.MyGeoPoint;

/**
 * Google Directions API で取得した情報の中で，交差点と交差点を結ぶ軌跡はencodeされている．
 * このクラスは，それを地図上にプロットするためのデコーダー．
 * @author sacchin
 *
 */
public class PolylineDecoder {
	
	/**
	 * ある緯度経度をエンコードするメソッド．
	 * @param lat 緯度
	 * @param lng 経度
	 * @return エンコード語の文字列
	 */
	public static String encodeLatLng(double lat, double lng){
		return encoderPosi((int)(lat * 1E6)).toString() + 
		encoderPosi((int)(lng * 1E6)).toString();
	}
	
	/**
	 * 緯度経度を整数に直したものをエンコードするメソッド．
	 * @param num 緯度 * 1E6 と 経度 * 1E6をつなげた整数．
	 * @return エンコードされた文字列．
	 */
	public static StringBuffer encoderPosi(int num){
		ArrayList<Byte> bit =new ArrayList<Byte>();
		StringBuffer ret = new StringBuffer();
		num = num << 1;

		//負値の場合はビット反転
		if(num <0){
			num =~ num;
		}
		//5bit毎に分割
		for(int i = 0 ; i < 6 ; i++){ 
			bit.add((byte)(num&0x1f));
			num=num>>>5;
		}
		//不要な0データ削除
		for(int i=bit.size()-1;i>=0;i--){
			byte n=bit.get(i);
			if(n==0){
				bit.remove(i);
			}else{
				break;
			}
		}
		//0x20論理和（最終データは除く）
		for(int i=0;i<bit.size()-1;i++){
			bit.set(i, (byte) (bit.get(i)|0x20));
		}
		//63加算
		for(int i=0;i<bit.size();i++){
			bit.set(i, (byte) (bit.get(i)+63));
			//バックスラッシュ時の処理
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
	
	/**
	 * エンコードされた文字列から緯度経度のリストへデコードするメソッド
	 * @param encoded エンコードされた文字列．
	 * @return 緯度経度のリスト
	 */
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

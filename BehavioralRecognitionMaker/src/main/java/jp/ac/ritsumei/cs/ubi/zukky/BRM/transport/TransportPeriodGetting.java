package jp.ac.ritsumei.cs.ubi.zukky.BRM.transport;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jp.ac.ritsumei.cs.ubi.zukky.BRM.featurePoint.Clustering;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.featurePoint.FeaturePoint;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.kubiwa.BlackholeConnector;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.kubiwa.Gps;

/**
 * 移動区間を決めうちで取得するクラス
 * （たくちゃんの研究が完成するまでの仮クラス）
 * TODO　１つ目の移動区間が格納されてないっぽい
 * @author zukky
 *
 */
public class TransportPeriodGetting {

	public static void main(String[] args) {

	}

	/**
	 * 
	 * @param sTime
	 * @param eTime
	 * @return
	 * @throws IOException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public Timestamp[][] getTransportFromstartToend(Timestamp sTime, Timestamp eTime, FeaturePoint startPoint, 
			FeaturePoint endPoint, int devid) throws IOException, ClassNotFoundException, SQLException{
		BlackholeConnector connector = new BlackholeConnector("");
		connector.createConnection();
//		KubiwaHelper kubiwaHelper = new KubiwaHelper();

		//移動開始地点の矩形
		double startLatLow = startPoint.getMinLat();
		double startLatHigh = startPoint.getMaxLat();
		double startLngLow = startPoint.getMinLng();
		double startLngHigh = startPoint.getMaxLng();

		//移動終了地点の矩形
		double endLatLow = endPoint.getMinLat();
		double endLatHigh = endPoint.getMaxLat();
		double endLngLow = endPoint.getMinLng();
		double endLngHigh = endPoint.getMaxLng();
		

		List<Gps> gpsStartList = connector.selectGpsPlace(devid, sTime, eTime, startLatLow, startLatHigh, startLngLow, startLngHigh);
		List<Gps> gpsEndList = connector.selectGpsPlace(devid, sTime, eTime, endLatLow, endLatHigh, endLngLow, endLngHigh);		
//		List<Gps> gpsStartList = kubiwaHelper.getGpsPlace(devId, sTime, eTime, startLatLow, startLatHigh, startLngLow, startLngHigh);
//		List<Gps> gpsEndList = kubiwaHelper.getGpsPlace(devId, sTime, eTime, endLatLow, endLatHigh, endLngLow, endLngHigh);		
			
		List<FeaturePoint> featurePointStartList = getFeaturePointList(gpsStartList);		
		List<FeaturePoint> featurePointEndList = getFeaturePointList(gpsEndList);
		
		System.out.println(featurePointStartList.size() + ", " + featurePointEndList.size());
		
		Timestamp[][] timeArray = new Timestamp[featurePointStartList.size()+1][2];
		int i=0;
		for(FeaturePoint startGps: featurePointStartList){

			for(FeaturePoint endGps: featurePointEndList){
				
				long startTime = startGps.getTime().getTime();
				long endTime = endGps.getTime().getTime();
				
				long secTime = (endTime - startTime) /1000;
				
				if(0 < secTime && secTime < 7200){
					timeArray[i][0] = startGps.getTime();
					timeArray[i][1] = endGps.getTime();
					i++;
				}
			}

		}
			
		return timeArray;
	}
	
	
	private List<FeaturePoint> getFeaturePointList(List<Gps> gpsList){
		List<FeaturePoint> resultFpList = new ArrayList<FeaturePoint>();
		FeaturePoint tmpFpPoint;
		long TresholdTime = 3600; //２時間
		long time = 0;
		int startIndex = 0;
		int endIndex = 0;
		boolean newCluster = true;
		
		for(int i=0; i+1 <gpsList.size(); i++){
					
			if(newCluster){
				startIndex = i;
				newCluster = false;
			}
			
			time = ((gpsList.get(i+1).getTime().getTime() - gpsList.get(i).getTime().getTime()) /1000);
			
			if(TresholdTime < time){
				
				endIndex = i;
				List<FeaturePoint> tmpList = new ArrayList<FeaturePoint>();
				
				if(startIndex == endIndex){
					tmpList.add(new FeaturePoint(gpsList.get(startIndex)));
				}else{
					for(int j=startIndex; j<endIndex; j++){
						tmpList.add(new FeaturePoint(gpsList.get(j)));
					}
					tmpList = Clustering.clusterNearGpsByCentroidMethod(tmpList, 3000, 0);
				}
				tmpFpPoint = tmpList.get(0);
				resultFpList.add(tmpFpPoint);
				newCluster = true;
			}	
			
			if(i+1 == gpsList.size()-1){
				List<FeaturePoint> tmpList = new ArrayList<FeaturePoint>();
				tmpList.add(new FeaturePoint(gpsList.get(startIndex)));
				tmpFpPoint = tmpList.get(0);
				resultFpList.add(tmpFpPoint);
				newCluster = true;
			}
			
		}	
		return resultFpList;
	}
	
}

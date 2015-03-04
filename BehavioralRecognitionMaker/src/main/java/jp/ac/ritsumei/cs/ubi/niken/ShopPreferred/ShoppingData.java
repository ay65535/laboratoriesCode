package jp.ac.ritsumei.cs.ubi.niken.ShopPreferred;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
//import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import javax.swing.JOptionPane;  // import class JOptionPane


public class ShoppingData {
	//	String accUser;
	//	int numAccUser;
	private double latitude2;
	private double longitude2;
	private String lendTime;
	private double latitude1;
	private double longitude1;
	private String lstartTime;
	private double speed;
	private double duration;
	private double distance;
	private double accuracy;
	private String truncLat;
	private String truncLong;
	private double stay;
	private double avgspeed;
	private double number;
	private String cluster;

	public ShoppingData(double number, String cluster, 
			double lat2, double lon2, double lat1, double lon1, 
			String lstartTime, String lendTime, double spd, double distance, double accuracy) {
		this.setNumber(number);
		this.latitude2 = lat2;
		this.longitude2 = lon2;
		this.lendTime = lendTime;
		this.latitude1 = lat1;
		this.longitude1 = lon1;
		this.lstartTime = lstartTime;
		this.speed = spd;
		this.distance = distance;
		//	this.duration = duration;		
		this.accuracy = accuracy;
		this.cluster = cluster;
	}



	public void setLatitude2(double latitude2) {
		this.latitude2 = latitude2;
	}
	public double getLatitude2() {
		return latitude2;
	}
	public void setLongitude2(double longitude2) {
		this.longitude2 = longitude2;
	}
	public double getLongitude2() {
		return longitude2;
	}
	public void setLatitude1(double latitude1) {
		this.latitude1 = latitude1;
	}

	public double getLatitude1() {
		return latitude1;
	}
	public void setLongitude1(double longitude1) {
		this.longitude1 = longitude1;
	}

	public double getLongitude1() {
		return longitude1;
	}
	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getSpeed() {
		return speed;
	}
	public void setDuration(double duration) {
		this.duration = duration;
	}
	public double getDuration() {
		return duration;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getDistance() {
		return distance;
	}

	public void setLendTime(String lendTime) {
		this.lendTime = lendTime;
	}

	public String getLendTime() {
		return lendTime;
	}

	public void setLstartTime(String lstartTime) {
		this.lstartTime = lstartTime;
	}

	public String getLstartTime() {
		return lstartTime;
	}


	public static void saveShoppingData(ArrayList<ShoppingData> shoppingData, String filename){
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(filename));
			writer.append("IdxCluster");
			writer.append(",");
			writer.append("Cluster Candidate");
			writer.append(",");
			writer.append("Latitude2");
			writer.append(",");
			writer.append("Longitude2");
			writer.append(",");
			writer.append("Latitude1");
			writer.append(",");
			writer.append("Longitude1");
			writer.append(",");
			writer.append("StartTime");
			writer.append(",");
			writer.append("EndTime");
			writer.append(",");
			writer.append("AvgSpeed");
			writer.append(",");
			writer.append("Distance");
			writer.append(",");
			writer.append("Accuracy");
			writer.append(",");
			writer.append("Stay");
			writer.append('\n');		

			for (ShoppingData sd : shoppingData) {

				writer.append(String.valueOf(sd.number));
				writer.append(",");
				writer.append(String.valueOf(sd.cluster));
				writer.append(",");
				writer.append(String.valueOf(sd.latitude2));
				writer.append(",");
				writer.append(String.valueOf(sd.longitude2));
				writer.append(",");
				writer.append(String.valueOf(sd.latitude1));
				writer.append(",");
				writer.append(String.valueOf(sd.longitude1));
				writer.append(",");
				writer.append(String.valueOf(sd.lstartTime));
				writer.append(",");
				writer.append(String.valueOf(sd.lendTime));
				writer.append(",");
				writer.append(String.valueOf(sd.speed));
				writer.append(",");
				writer.append(String.valueOf(sd.distance));
				writer.append(",");
				writer.append(String.valueOf(sd.accuracy));
				writer.append(",");
				writer.append(String.valueOf(sd.stay));
				writer.append("\n");
			}

			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
	}

	public static ArrayList<ShoppingData> convertShoppingData(ArrayList<RawData> rawData) {

		ArrayList<ShoppingData> shopData = new ArrayList<ShoppingData>();

		int number = 0;
		
		for(int i = 0; i < rawData.size(); i++) {
			if (i != rawData.size() - 1) {
				
				RawData rdata1 = rawData.get(i);
				RawData rdata2 = rawData.get(i+1);
				long lstartTime = rdata1.getStartTime().getTime();
				long lendTime = rdata2.getStartTime().getTime();
				double duration = (lendTime - lstartTime)/1000;
				double lat2 = rdata2.getLatitude();
				double lat1 = rdata1.getLatitude();
				double trunclat1 = Math.floor(lat1*1000)/1000;
				String x1 = Double.toString(trunclat1);
				double lng2 = rdata2.getLongitude();
				double lng1 = rdata1.getLongitude();
				double trunclng1 = Math.floor(lng1*1000)/1000;
				String x2 = Double.toString(trunclng1);
				String cluster = x1+x2;
				//		double number = IdxCluster.getIdxcluster(shopData);		
				double distance = Haversian.calcDistance(lat2, lng2, lat1, lng1);
				final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				if (duration != 0){
					double speed = (double)(distance/duration);
					//		if (lost_speed <= 5 && found_speed<=5 ){
					//			if ( distance <=150){
					//				if(rdata1.getKetepatan()<=64){
					//					if (!recentStr.equals(cluster)) {
					//						recentStr = cluster;
					//						number++;
					//					} 
					
					
					shopData.add(new ShoppingData(number, cluster, 
							rdata2.getLatitude(), rdata2.getLongitude(), 
							rdata1.getLatitude(),rdata1.getLongitude(), sdf.format(lstartTime),sdf.format(lendTime),speed, 
							distance, rdata1.getKetepatan()));
				}
			}
		}
		//	}
		//	}
		//}

		return shopData;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	public double getAccuracy() {
		return accuracy;
	}

	public void setTruncLat(String truncLat) {
		this.truncLat = truncLat;
	}

	public String getTruncLat() {
		return truncLat;
	}

	public void setTruncLong(String truncLong) {
		this.truncLong = truncLong;
	}

	public String getTruncLong() {
		return truncLong;
	}



	public void setStay(double stay) {
		this.stay = stay;
	}



	public double getStay() {
		return stay;
	}



	public void setAvgspeed(double avgspeed) {
		this.avgspeed = avgspeed;
	}



	public double getAvgspeed() {
		return avgspeed;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}



	public String getCluster() {
		return cluster;
	}

	public void setNumber(double number) {
		this.number = number;
	}



	public double getNumber() {
		return number;
	}


}

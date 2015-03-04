package jp.ac.ritsumei.cs.ubi.niken.ShopPreferred;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class CheckSpeed {

		private double latitude2;
		private double longitude2;
		private double avgspeed;
		private String truncLat;
		private String truncLong;
		
		public CheckSpeed(String truncLat, String truncLong, 
				double lat2, double lon2, double avgspeed) {
			this.latitude2 = lat2;
			this.longitude2 = lon2;
			this.avgspeed = avgspeed;
			this.truncLat = truncLat;
			this.truncLong = truncLong;
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
			public static void saveCheckSpeed(ArrayList<CheckSpeed> checkSpeed, String filename){
			BufferedWriter writer;
			try {
				writer = new BufferedWriter(new FileWriter(filename));
				writer.append("Cluster Candidate");
				writer.append(",");
				writer.append("Latitude2");
				writer.append(",");
				writer.append("Longitude2");
				writer.append(",");
				writer.append("AverageSpeed");
				writer.append('\n');		
				
				for (CheckSpeed sd : checkSpeed) {
					
					writer.append(String.valueOf(sd.truncLat));
					writer.append(String.valueOf(sd.truncLong));
					writer.append(",");
					writer.append(String.valueOf(sd.latitude2));
					writer.append(",");
					writer.append(String.valueOf(sd.longitude2));
					writer.append(",");
					writer.append(String.valueOf(sd.avgspeed));
					writer.append("\n");
				}
				
				writer.flush();
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 	
		}
		
		public static ArrayList<CheckSpeed> convertCheckSpeed(ArrayList<RawData> rawData) {
			
			ArrayList<CheckSpeed> checkSpeed = new ArrayList<CheckSpeed>();
			
			for(int i = 0; i < rawData.size(); i++) {
				if (i != rawData.size() - 1) {
					
					RawData rdata1 = rawData.get(i);
					RawData rdata2 = rawData.get(i+1);
					long lstartTime = rdata1.getEndTime().getTime();
					long lendTime = rdata2.getEndTime().getTime();
					double duration = (lendTime - lstartTime)/1000;
					double lat2 = rdata2.getLatitude();
					double lat1 = rdata1.getLatitude();
					double trunclat1 = Math.floor(lat1*1000)/1000;
					String x1 = Double.toString(trunclat1);
					double lng2 = rdata2.getLongitude();
					double lng1 = rdata1.getLongitude();
					double trunclng1 = Math.floor(lng1*1000)/1000;
					String x2 = Double.toString(trunclng1);
					double distance = Haversian.calcDistance(lat2, lng2, lat1, lng1);
	
	
						double speed = (double)(distance/1000)*(3600/duration);
								
							checkSpeed.add(new CheckSpeed(x1,x2,rdata2.getLatitude(), rdata2.getLongitude(), speed));
								
				}
			}
			
			
			return checkSpeed;
		}	

}

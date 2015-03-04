package jp.ac.ritsumei.cs.ubi.niken.ShopPreferred;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class SignLocation {
	
	private ArrayList<ShoppingData> shoppingData;
	private ArrayList<ShoppingData> clusteredShoppingData;

	
	
	public SignLocation(ArrayList<ShoppingData> shoppingData){
		this.shoppingData = shoppingData;
		this.setClusteredShoppingData(new ArrayList<ShoppingData>());
	}
	
	public void calcSignLocation(){
		
		ArrayList<ShoppingData> sameCluster = null;
		for (int i = 0; i < shoppingData.size(); i++){
			sameCluster = new ArrayList<ShoppingData>();
			if (!existInClusteredData(shoppingData.get(i))) {
				sameCluster.add(shoppingData.get(i));
				for (int j = 0; j < shoppingData.size(); j++) {
					if (j != i) {
						if (isSame(sameCluster.get(0), shoppingData.get(j))) {
							sameCluster.add(shoppingData.get(j));
							
						}
					}
				}
				double stay = getStayDuration(sameCluster); 
				double averageSpeed = avgSpeed(sameCluster);
				ShoppingData centroid = getCentroid(sameCluster);
				centroid.setStay(stay);
				centroid.setAvgspeed(averageSpeed);
				getClusteredShoppingData().add(centroid);
			}
		}
		
	//	ShoppingData.saveShoppingData(getClusteredShoppingData(), "C:\\data\\1year\\Output\\Vino\\P120427\\apus3.csv");
	//	recalculate distance among truncates.
		
		recluster(getClusteredShoppingData());
		ShoppingData.saveShoppingData(getClusteredShoppingData(), "C:\\data\\1year\\Output\\Vino\\P120427\\SOV120410.csv");
	}

	
	private double getStayDuration(ArrayList<ShoppingData> stay){
		double stayDuration = 0;
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			double last = sdf.parse(stay.get(stay.size() - 1).getLendTime()).getTime();
			double first = sdf.parse(stay.get(0).getLstartTime()).getTime();
			stayDuration = (last-first)/1000;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return stayDuration;
	}
	private double avgSpeed(ArrayList<ShoppingData> cluster1){
		double averageSpeed = 0;
		for (ShoppingData sd : cluster1){
			 averageSpeed+= sd.getSpeed()/cluster1.size();
		}		
		return averageSpeed;
	}
	
	private ShoppingData getCentroid(ArrayList<ShoppingData> cluster) {
		
		int pos = 0;
		for (int i = 0; i < cluster.size();i++){
			if (i != cluster.size()-1) {
			ShoppingData data1 = cluster.get(i);
			ShoppingData data2 = cluster.get(i+1);
			int a = 0;
			int b = 0;
			a = (int) data1.getAccuracy();
			b = (int) data2.getAccuracy();
			
			if (a<b){
				pos = cluster.indexOf(data1);
			} else { 
				pos = cluster.indexOf(data2);}
		}
		}	
		if (pos<0){
			return null;
		}
		else {
			ShoppingData data = cluster.get(pos);
			data.setLstartTime(cluster.get(0).getLstartTime());
			data.setLendTime(cluster.get(cluster.size()-1).getLendTime());
			return data;
			}
				
	}
	
	private void recluster(ArrayList<ShoppingData> clusteredData) {
			for(int i = 0; i < clusteredData.size() - 1; i++) {
				for(int j = i + 1; j < clusteredData.size(); j++) {
					ShoppingData data1 = clusteredData.get(i);
					ShoppingData data2 = clusteredData.get(j);
					double lat1 = data1.getLatitude1();
					double lng1 = data1.getLongitude1();
					double lat2 = data2.getLatitude1();
					double lng2 = data2.getLongitude1();
					double distance = Haversian.calcDistance(lat2, lng2, lat1, lng1);
					System.out.println("size: " + clusteredData.size()  + ", i: " + i + ", j: " + j);
					if (distance <= 100){
						ArrayList<ShoppingData> list = new ArrayList<ShoppingData>();
						list.add(data1);
						list.add(data2);
						ShoppingData newData = getCentroid(list);
						
						System.out.println("Lat1: " +data1.getLatitude1() + ", Long1: " +data1.getLongitude1());
						System.out.println("Lat2: " +data2.getLatitude1() + ", Long2: " +data2.getLongitude1());
						//System.out.println("new size: " + clusteredData.size());
						clusteredData.remove(data1);
						clusteredData.remove(data2);
						clusteredData.add(newData);
						System.out.println("Lat: " +newData.getLatitude1() + ", Long: " +newData.getLongitude1());
						System.out.println("Distance: " +distance);
						recluster(clusteredData);
					}	
				}
			}				
	}
	
	
	private boolean isSame(ShoppingData data1, ShoppingData data2) {
		if ((data1.getNumber()== data2.getNumber()))    {
			return true;
		}
		return false;
	}
	
	private boolean existInClusteredData(ShoppingData data) {
		for (ShoppingData sd : getClusteredShoppingData()) {
			if (isSame(sd, data)) {
				return true;
			}
		}
		return false;
	}


	public void setClusteredShoppingData(ArrayList<ShoppingData> clusteredShoppingData) {
		this.clusteredShoppingData = clusteredShoppingData;
	}

	public ArrayList<ShoppingData> getClusteredShoppingData() {
		return clusteredShoppingData;
	}

	
	
}

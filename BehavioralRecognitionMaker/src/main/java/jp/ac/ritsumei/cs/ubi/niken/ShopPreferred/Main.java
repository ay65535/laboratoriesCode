package jp.ac.ritsumei.cs.ubi.niken.ShopPreferred;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.util.UnixTimeTransport;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*	ArrayList<RawData> raws = RawData.getRawData("C:\\data\\1year\\RawData\\Vino\\P120427\\Vin120410trip.csv", 0);
		ArrayList<ShoppingData> shops = ShoppingData.convertShoppingData(raws);
	//	ArrayList<CheckSpeed> speeds = CheckSpeed.convertCheckSpeed(raws);

	//	SignLocation signLocation = new SignLocation(shops);
	//  signLocation.calcSignLocation();
	//	ArrayList<ShoppingData> clustered = signLocation.getClusteredShoppingData();


	//	ShoppingData.saveShoppingData(shops, "C:\\data\\1year\\Output\\Niken\\201204\\ONik120409c.csv");
		ShoppingData.saveShoppingData(shops, "C:\\data\\1year\\Output\\Niken\\cek2.csv");
	//	CheckSpeed.saveCheckSpeed(, "C:\\data\\1year\\Output\\Vino\\201203\\OVin111118cekspeed.csv"); */


		UnixTimeTransport utt = new UnixTimeTransport();
		Long unixCurrentTime = utt.getCurrentUnixTime();
		Long unixTime = utt.getUnixTime("2007/02/07 10:00:00");
		
		System.out.println("unix current time " + unixCurrentTime);
		System.out.println("unix time + " + unixTime);
		
//		final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
//		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
//		AccCluster acc = new AccCluster();
//		WalkerStateDetector wsd = acc.MakeCluster(9, 1333983600000L, 1334059200000L, 41, 45);
//
//		for (WalkerState w :wsd.getCluster()){
//			long startTime = w.getStartTime();
//			long endTime = w.getEndTime();
//			System.out.println(w.getType());
//			System.out.println(sdf.format(startTime));
//			System.out.println(sdf.format(endTime));		
//		}

		//	System.out.println("Total: " + clustered.size());
	}

}





//ArrayList<Haversian> location = Haversian.calcHaversian(shops);

/*for (ShoppingData sd : shops) {
	System.out.println("Lat:" + sd.getLatitude2() + 
			" Lon:" + sd.getLongitude2() + 
			" Duration:" + sd.getDuration()/1000 + "s");
}*/

/*for (ShoppingData sd : shops) {
	System.out.println(sd.getSpeed());
}*/
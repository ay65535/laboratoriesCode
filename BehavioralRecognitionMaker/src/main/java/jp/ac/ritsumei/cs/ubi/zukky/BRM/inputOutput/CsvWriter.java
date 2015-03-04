package jp.ac.ritsumei.cs.ubi.zukky.BRM.inputOutput;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.List;

import jp.ac.ritsumei.cs.ubi.zukky.BRM.featurePoint.FeaturePoint;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.hubenyVelocity.HubenyVelocity;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.kubiwa.Gps;

/**
 * 指定したデータをCSVファイルに書き込むクラス
 * @author zukky
 *
 */
public class CsvWriter {


	/**
	 * TODO: ジェネリクスとか勉強して下記の関数を書き直す
	 */

	public void writeRouteDataTest(List<List> routeDataList, String filePath){

		try {
			for(int i=0; i<routeDataList.size(); i++){

				List<FeaturePoint> routeData = routeDataList.get(i);
				PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(filePath + "_" + i + ".csv")));

				System.out.println("csvRouteDataList: " + routeData.size());
				for(FeaturePoint fp: routeData){					
					printWriter.println(fp.getMinLat() + "," + fp.getMinLng() + "," + fp.getMaxLat() + "," + fp.getMaxLng() + "," + "blue");
				}
				printWriter.close();

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ファイル書き込みエラーです");
		}
	}
	
	
	/**
	 * ちゃんたく用にlat,lngのペアのみをCSVファイルに書き込む
	 * @param gpsList
	 * @param filePath
	 */
	public void writeLatLngData(List<Gps> gpsList, String filePath){

		try {
			PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));

			for(Gps gps: gpsList){
				printWriter.println(gps.getLat() + "," + gps.getLng());
			}

			printWriter.close();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ファイル書き込みエラーです");
		}
	}

	public void writeTestRectangleData(Gps gps1, Gps gps2, String filePath){

		try {
			PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));

			printWriter.println(gps1.getLat() + "," + gps1.getLng() + "," + gps2.getLat() + "," + gps2.getLng()  + ","  + "blue");

			printWriter.close();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ファイル書き込みエラーです");
		}
	}

	/**
	 * ちゃんたく用にlat,lngのペアのみをCSVファイルに書き込む
	 * @param checkpointList
	 * @param filePath
	 */
	public void writeLatLngCheckpointData(List<FeaturePoint> FeaturePointList, String filePath){

		try {
			PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));

			for(FeaturePoint featurePoint: FeaturePointList){
				printWriter.println(featurePoint.getLat() + "," + featurePoint.getLng());
			}

			printWriter.close();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ファイル書き込みエラーです");
		}
	}

	/**
	 * ちゃんたく用にlat,lngのペアのみをCSVファイルに書き込む
	 * @param checkpointList
	 * @param filePath
	 */
	public void writeRectangleData(List<FeaturePoint> featurePointList, String filePath, String color){

		try {
			PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));

			for(FeaturePoint checkpoint: featurePointList){
				double lat = checkpoint.getLat();
				double lng = checkpoint.getLng();

//				double minLat = lat - 0.0001;
//				double maxLat = lat + 0.0001;
//
//				double minLng = lng - 0.0001;
//				double maxLng = lng + 0.0001;
				
				double minLat = checkpoint.getMinLat();
				double maxLat = checkpoint.getMaxLat();
				double minLng = checkpoint.getMinLng();
				double maxLng = checkpoint.getMaxLng();
				

				printWriter.println(minLat + "," + minLng + "," + maxLat + "," + maxLng + "," + color);
			}

			printWriter.close();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ファイル書き込みエラーです");
		}
	}

	
	public void writeRectangleData(List<String> resultList, String filePath){
		try {
			PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));

			for(String result: resultList){
				printWriter.println(result);
			}

			printWriter.close();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ファイル書き込みエラーです");
		}
	}

	public void writeRequiredTimeTest(Timestamp[][] time, String filePath){

		try {
			PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));

			int arrayLength = 0;
			for(int i=0; time[i][0] != null; i++){
				arrayLength++;
			}
				
				
			long[] timeArrayAscend = new long[arrayLength]; 
			
			System.out.println("time.length: " + time.length);
		
			
			for(int i=0; time[i][0] != null; i++){
				System.out.println("time[i][1].getTime " + time[i][1].getTime());
				timeArrayAscend[i] = (time[i][1].getTime() - time[i][0].getTime());
				System.out.println("i :" + i);
			}
			
			java.util.Arrays.sort(timeArrayAscend);

			long count = 0;
			long sumTime = 0;
			
			
			for(int i=0; i<timeArrayAscend.length; i++){
				printWriter.println("【" + i + "】所要時間: " + (timeArrayAscend[i] / 1000));
				sumTime = sumTime + (timeArrayAscend[i] / 1000);
				count++;
			}
			
			
			long averageTime = (sumTime / count);
			
			printWriter.println("averageTime " + averageTime);

			printWriter.close();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ファイル書き込みエラーです");
		}

	}

	public void writeHubenyVelocity(List<Double> hubenyVelocityList, String filePath){

		try {
			PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));

			for(double hubenyVelocity: hubenyVelocityList){
				printWriter.println(hubenyVelocity);
			}

			printWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ファイル書き込みエラーです");
		}
	}
	
	public void writeHubenyVelocity2(List<HubenyVelocity> hubenyVelocityList, String filePath){

		try {
			PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));
			for(HubenyVelocity hubenyVelocity: hubenyVelocityList){
				printWriter.println(hubenyVelocity.getStartTime() + "," + hubenyVelocity.getVelocity());
			}

			printWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ファイル書き込みエラーです");
		}
	}

}

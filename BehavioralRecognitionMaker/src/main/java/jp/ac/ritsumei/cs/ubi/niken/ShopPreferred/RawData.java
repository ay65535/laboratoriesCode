package jp.ac.ritsumei.cs.ubi.niken.ShopPreferred;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;


public class RawData {
	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
	
	private Date startTime;
	private double latitude;
	private double longitude;
	private Date endTime;
	private double speed;
//	private int userid;
	private double ketepatan;

	private static BufferedReader reader;
	
	public RawData(Date startTime, double latitude, double longitude, double ketepatan, double speed, Date endTime) {
		this.startTime = startTime;
		this.latitude = latitude;
		this.longitude = longitude;
		this.setKetepatan(ketepatan);
		this.speed = speed;
	//	this.setUserid(userid);
		this.endTime = endTime;
	}
	
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public void setKetepatan(double ketepatan) {
		this.ketepatan = ketepatan;
	}

	public double getKetepatan() {
		return ketepatan;
	}
	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getSpeed() {
		return speed;
	}
	
	public Date getEndTime() {
		return endTime;
	}
	
	public static ArrayList<RawData> getRawData(String fileName, int threshold) {
		ArrayList<RawData> rawData = new ArrayList<RawData>();
		
		//function for converting raw data to clean data
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		
		long time = Long.MIN_VALUE;
		try {
			reader = new BufferedReader(new FileReader(fileName));
			String line  = null;
			int i = 0;
			while((line = reader.readLine()) != null) {
				StringTokenizer token = new StringTokenizer(line, ",");
				Date startTime = sdf.parse(token.nextToken());
				double lat = Double.parseDouble(token.nextToken());
				double lon = Double.parseDouble(token.nextToken());
				float akurat = Float.parseFloat(token.nextToken());
				float cepat = Float.parseFloat(token.nextToken());
	//			int userid = Integer.parseInt(token.nextToken());
			    Date endTime = sdf.parse(token.nextToken()); 
			    
			    long lstart = startTime.getTime();
			    boolean insert = true;
				if (i == 0) {
					time = lstart;
					insert = true;
				} else {
					if (lstart - time >= 10000) {
						time = lstart;
						insert = true;
					} else {
						insert = false;
					}
				}
				
				if (insert) {
					rawData.add(new RawData(startTime, lat, lon, akurat, cepat, endTime));
				}
				i++;
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return rawData;
	}

//	public void setUserid(int userid) {
//		this.userid = userid;
//	}

//	public int getUserid() {
//		return userid;
//	}

		
}

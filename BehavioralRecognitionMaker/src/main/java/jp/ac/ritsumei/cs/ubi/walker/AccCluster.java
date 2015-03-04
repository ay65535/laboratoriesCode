/**
 * Copyright (C) 2008-2012 Nishio Laboratory All Rights Reserved
 */
package jp.ac.ritsumei.cs.ubi.walker;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;






import com.google.gson.Gson;

/**
 * This class determine the WORKING, STAYING, DESCENDING, ASENDING part.
 * In other word, this library separate the period in 4 state.
 * @author dany
 */
public class AccCluster{
	int port = -1;

	/**
	 * Use this constructor when you want to connect to the blackhole database
	 * using port forwarding.
	 * 
	 * @param port
	 */
	public AccCluster(int port){
		this.port = port;
	}

	public AccCluster(){

	}
	Connection connMysql;
	static class Data {
		long t;
		float x, y, z;
	}

	final List<SensorEventListener> listeners =
			new ArrayList<SensorEventListener>();
	long millis;

	long nanos;
	//public static void main(String[] args){
	//	AccCluster ac = new AccCluster();
	//	
	//	ac.MakeCluster("9000","46",1289538360000L,1289546580000L);
	//}

	/**
	 * This method is used for delimit the 4 state.
	 */
	public WalkerStateDetector MakeCluster(int devid, String startTime, String endTime, int thresh, int sec){

		ElevatorWalkerStateDetector wsd = new ElevatorWalkerStateDetector(sec);
		StepDetecter sd = new StepDetecter(thresh);
		sd.addStepListener(wsd);
		listeners.add(sd);
		listeners.add(wsd);
		connect();

		try {
			execute(devid, startTime, endTime);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File file = new File("/Users/zukky/Desktop/csv/accCluster.txt");

		PrintWriter pw;
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			for(WalkerState ws :wsd.getCluster()){
				//System.out.println(ws.type+","+ws.startTime+","+ws.endTime+","+ws.steps);
				
				pw.println(ws.type+","+new Timestamp(ws.startTime)+","+new Timestamp(ws.endTime)+","+ws.steps);
			}
			pw.close();
			System.out.println("finish");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return wsd;
	}

	private void execute(int devid, String startTime, String finishTime) throws SQLException{
		PreparedStatement stmt;
		ResultSet rs;
		UnixTimeTransport utt = new UnixTimeTransport();
		long toTime    = utt.getUnixTime(startTime);
		long endTime   = utt.getUnixTime(finishTime); 
		
		
		//long toTime = startTime;
		long delta = 30 * 60000;
		boolean isFirst = true, isLast = false;
		stmt = connMysql.prepareStatement("SELECT * FROM "
				+ "accelerometer WHERE time BETWEEN "
				+ "? AND ? "
				+ "AND devid=? ORDER BY time ASC");
		
		for(long fromTime = toTime ;; fromTime += delta+1000){
			connect();
			toTime = fromTime + delta;				
			if( toTime >= endTime){
				toTime = endTime;
				isLast = true;
			}
			//System.out.println("fromTime:"+fromTime+" toTime:"+toTime);

			stmt.setTimestamp(1, new Timestamp(fromTime));
			stmt.setTimestamp(2, new Timestamp(toTime));
			stmt.setInt(3, devid);
			rs = stmt.executeQuery();
		
			try {
				readFromDB(rs, isFirst, isLast);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(connMysql != null){
					try{
						connMysql.close();
					}catch(SQLException e){
					}
				}
			}
			if(isLast){
				connMysql.close();
				break;
			}
		}
	}

	private void connect(){
		//String urlMysql = "jdbc:mysql://localhost:"+3306+"/blackhole";
		String urlMysql = "jdbc:mysql://exp-www.ubi.cs.ritsumei.ac.jp/blackhole";
		String urlPortFoward = "jdbc:mysql://localhost:" + this.port + "/blackhole";
		String pdMysql = "n0pr1vacy";
		String userMysql = "kubiwauser";
		try {
			if(this.port == -1){
				this.connMysql = DriverManager.getConnection(urlMysql, userMysql, pdMysql);
			}
			else{
				this.connMysql = DriverManager.getConnection(urlPortFoward, userMysql, pdMysql);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	void setBaseTime(long millis, long nanos) {
		this.millis = millis;
		this.nanos = nanos;
	}
	long getTimeMillis(long t) {
		return millis + (t - nanos) / 1000000;
	}
	void readFromDB(ResultSet rs, boolean isFirst, boolean isLast) 
			throws SQLException {
		Gson gson = new Gson();
		long last = Long.MAX_VALUE;
		while (rs.next()) {
			String value = rs.getString("value");
			if (value == null) {
				continue;
			}
			//時刻補正
			Data[] data = gson.fromJson(
					rs.getString("value"), Data[].class);
			int i=0;
			for (Data d : data) {				
				long time = rs.getTimestamp("time").getTime();
				if (d.t < last || 5000 < time - getTimeMillis(d.t)) {
					setBaseTime(time, data[data.length - 1].t);
				}

				if(true == rs.isLast() && i == data.length - 1 && isLast){
					SensorEvent event = new SensorEvent(
							d.x, d.y, d.z, getTimeMillis(d.t), d.t, true);

					for (SensorEventListener l : listeners) {
						l.onSensorChanged(event);
					}
				}
				else if(rs.isFirst() && i == 0 && isFirst){
					isFirst = false;
					SensorEvent event = new SensorEvent(
							true, d.x, d.y, d.z, getTimeMillis(d.t), d.t);

					for (SensorEventListener l : listeners) {
						l.onSensorChanged(event);
					}
				}
				else{
					SensorEvent event = new SensorEvent(
							d.x, d.y, d.z, getTimeMillis(d.t), d.t);

					for (SensorEventListener l : listeners) {
						l.onSensorChanged(event);
					}
				}
				last = d.t;
				i++;
			}
		}
	}
}

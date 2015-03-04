package jp.ac.ritsumei.cs.ubi.niken.ShopPreferred;


import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
//import java.text.SimpleDateFormat;
import java.io.*;

//import jp.ac.ritsumei.cs.ubi.walker.AccCluster;

public class mining {

	public static void main(String[] args) throws SQLException, IOException {

		String user = "kubiwauser";
		String pass = "n0pr1vacy";
		String url = "jdbc:mysql://exp-www/blackhole";
		String SQL_Q1 = "select * from gps where devid = 97 && time > '2012-05-28 00:00:0.0' && time < '2012-05-29 00:00:0.0' && acc < 257;";
//		String SQL_Q1 = "select time,lat,lng,acc,speed,provider_time from LocationLog where devid = 97 && time > '2012-05-10 09:00:0.0' && time < '2012-05-11 00:00:0.0' and provider='gps' order by time;";
		

		Connection conn = ConnectDB.DBConnect(user, pass, url);
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(SQL_Q1);
	//	final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fileName = "120528"; 
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(
				"C:\\data\\1year\\rawdata\\Niken\\GPS\\201205\\Nik" + fileName + ".csv")));
		try {
		int counter = 0;
		while (rs.next()) { 
			try {
				System.out.println("getting data " + counter++);
				Timestamp time = rs.getTimestamp("time");
				double lat = rs.getDouble("lat");
				double lng = rs.getDouble("lng");
				float acc = rs.getFloat("acc");
				float speed = rs.getFloat("speed");
				Timestamp ltime = rs.getTimestamp("ltime");
			
				StringBuffer line = new StringBuffer();
				line.append(time);
				line.append(",");
				line.append(lat);
				line.append(",");
				line.append(lng);
				line.append(",");
				line.append(acc);
				line.append(",");
				line.append(speed);
				line.append(",");
				line.append(ltime);
				
				writer.println(line);
				
			} catch (Exception ex) {
				System.out.println(ex.toString());
			}
		}
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
		writer.close();
		conn.close();
	}

}
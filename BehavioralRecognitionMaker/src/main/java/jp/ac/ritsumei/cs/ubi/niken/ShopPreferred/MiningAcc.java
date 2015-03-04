package jp.ac.ritsumei.cs.ubi.niken.ShopPreferred;


import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
//import java.text.SimpleDateFormat;
import java.io.*;

public class MiningAcc {

	public static void main(String[] args) throws SQLException, IOException {

		String user = "kubiwauser";
		String pass = "n0pr1vacy";
		String url = "jdbc:mysql://exp-www/blackhole";
//		String SQL_Q1 = "select * from gps where devid = 29 && time > '2010-10-1 19:40:0.0' && time < '2010-11-3 20:10:0.0';";
		String SQL_Q1 = "select * from gps where devid = 69 && time > '2011-11-18 00:00:0.0' && time < '2011-11-20 00:00:0.0' order by time;";
		

		Connection conn = ConnectDB.DBConnect(user, pass, url);

		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(SQL_Q1);
	//final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fileName = "apus"; 
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(
				"C:\\data\\1year\\rawdata\\niken\\" + fileName + ".csv")));
		try {
		int counter = 0;
		while (rs.next()) { 
			try {
				System.out.println("getting data " + counter++);
				Timestamp time = rs.getTimestamp("time");
				String nilai = rs.getString("value");
				int devid = rs.getInt("devid");
						
			
				StringBuffer line = new StringBuffer();
				line.append(time);
				line.append(",");
				line.append(nilai);
				line.append(",");
				line.append(devid);			
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
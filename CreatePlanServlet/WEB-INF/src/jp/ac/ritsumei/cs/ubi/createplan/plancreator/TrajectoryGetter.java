/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.createplan.plancreator;

import java.io.IOException;
import java.sql.SQLException;

import jp.ac.ritsumei.cs.ubi.createplan.database.BlackholeConnector;

import org.json.JSONArray;
import org.json.JSONException;

public class TrajectoryGetter {
	public static JSONArray getTrajectory(int year, int month, int day, 
			int hour, int minute, int interval, int devid) throws SQLException, JSONException {
		
		BlackholeConnector connector = null;
		try {
			connector = new BlackholeConnector(
					"jdbc:mysql://exp-www.ubi.cs.ritsumei.ac.jp:3306/blackhole?useUnicode=true&characterEncoding=utf8", 
					"kubiwauser", "n0pr1vacy");
			connector.createConnection();
			
			return connector.selectTrajectory(year, month, day, hour, minute, interval, devid);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			connector.close();
		}
		return null;
	}
}

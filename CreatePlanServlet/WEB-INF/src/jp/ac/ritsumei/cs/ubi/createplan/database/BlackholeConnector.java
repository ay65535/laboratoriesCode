/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.createplan.database;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BlackholeConnector extends SqlConnector{
	final String TRAJECTORY_SELECT_SQL = 
			"select * from LocationLog where devid = ? and provider = 'gps' and time between ";

	public BlackholeConnector(String dataBaseUrl, String dataBaseUser, String dataBasePassword) 
			throws IOException{
		super(dataBaseUrl, dataBaseUser, dataBasePassword);
	}

	//'2012-10-00 23:59:59'
	public String parseToBetweenSection(Calendar c){
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		return "'" + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":00'";
	}

	public JSONArray selectTrajectory(int year, int month, int day, 
			int hour, int minute, int interval, int devid) throws SQLException, JSONException{
		PreparedStatement statement = null;
		ResultSet resultset = null;

		Calendar c = Calendar.getInstance();
		c.set(year, month, day, hour, minute);
		String from = parseToBetweenSection(c);
		c.add(Calendar.MINUTE, interval);
		String to = parseToBetweenSection(c);

		if(from.isEmpty() || to.isEmpty()){
			return null;
		}

		try {
			statement = (PreparedStatement) connection.prepareStatement(TRAJECTORY_SELECT_SQL + 
					from + " and " + to + " order by time asc");
			statement.setInt(1, devid);

			resultset = statement.executeQuery();

			JSONArray jArray = new JSONArray();
			while(resultset.next()){
				JSONObject dropInSite = new JSONObject();
				dropInSite.put("time", resultset.getTimestamp("time"));
				dropInSite.put("lat", resultset.getDouble("lat"));
				dropInSite.put("lng", resultset.getDouble("lng"));
				dropInSite.put("acc", resultset.getFloat("acc"));
				dropInSite.put("speed", resultset.getDouble("speed"));

				jArray.put(dropInSite);
			}
			return jArray;

		}finally{
			if(statement != null && !statement.isClosed()){
				statement.close();
			}
			if(resultset != null && !resultset.isClosed()){
				resultset.close();
			}
		}
	}

}

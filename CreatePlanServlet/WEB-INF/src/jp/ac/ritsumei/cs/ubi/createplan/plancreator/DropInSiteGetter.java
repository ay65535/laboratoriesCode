/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.createplan.plancreator;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import jp.ac.ritsumei.cs.ubi.createplan.database.MovementResultConnector;
import jp.ac.ritsumei.cs.ubi.takuchan.dropInSiteMaker.DropInSiteGenerator;

import org.json.JSONArray;
import org.json.JSONException;

public class DropInSiteGetter {
	
	public static boolean insertLabel(int devid, int id, String label, PrintWriter pw) 
			throws IOException, SQLException, ClassNotFoundException{
		MovementResultConnector connector = null;
		try {
			connector = new MovementResultConnector(
					"jdbc:mysql://localhost:3306/movement_result?useUnicode=true&characterEncoding=utf8", 
					"plancheckeruser", "n0pr1vacy");
			connector.createConnection();

			String savedLabel = connector.selectDropInSiteLabel(devid, id);
			if(savedLabel == null || savedLabel.isEmpty()){
				return connector.insertDropInSiteLabel(devid, id, label);
			}else if(!savedLabel.equals(label)){
				return connector.updatetDropInSiteLabel(devid, id, label);
			}
			return false;
		} finally {
			connector.close();
		}
	}

	public static JSONArray getDropInSites(int day, int devid)
			throws SQLException, JSONException {
		
		MovementResultConnector connector = null;
		try {
			connector = new MovementResultConnector(
					"jdbc:mysql://localhost:3306/movement_result?useUnicode=true&characterEncoding=utf8", 
					"plancheckeruser", "n0pr1vacy");
			connector.createConnection();
			
			JSONArray dropInSites = connector.selectDropInSites(devid);
			if(dropInSites == null || dropInSites.length() == 0){
				dropInSites = createDropInSites(day, devid);
				for(int i = 0 ; i < dropInSites.length() ; i++){
					connector.insertDropInSite(dropInSites.getJSONObject(i), devid);
				}
				dropInSites = connector.selectDropInSites(devid);
			}
			return dropInSites;
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			connector.close();
		}
		
		return null;
	}

	public static JSONArray createDropInSites(int day, int devid)
			throws SQLException, JSONException {
		DropInSiteGenerator gen = new DropInSiteGenerator();

		Calendar cl = Calendar.getInstance();
		cl.add(Calendar.DAY_OF_YEAR, day);

		JSONArray jArray = gen.getDropInSiteJSON(
				new Timestamp(cl.getTimeInMillis()), 
				new Timestamp(System.currentTimeMillis()), devid, true);

		return jArray;
	}
}

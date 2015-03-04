/*
 * Copyright (C) 2008-2013 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.createplan.setting;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

import jp.ac.ritsumei.cs.ubi.createplan.database.MovementResultConnector;

public class MiningSettingsManager {
	public static boolean insertSetting(int devid, int interval, Timestamp start) 
			throws IOException, SQLException, ClassNotFoundException{
		MovementResultConnector connector = null;
		try {
			connector = new MovementResultConnector(
					"jdbc:mysql://localhost:3306/movement_result?useUnicode=true&characterEncoding=utf8", 
					"plancheckeruser", "n0pr1vacy");
			connector.createConnection();

			int saved = connector.selectMiningSetting(devid);
			
			if(saved < 1){
				return connector.insertMiningSetting(devid, interval, start);
			}else{
				return connector.updatetMiningSetting(devid, interval, start);
			}
		} finally {
			connector.close();
		}
	}
}

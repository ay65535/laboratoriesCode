/*
 * Copyright (C) 2008-2012 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConnector {
	protected Connection connection;
	
	String dataBaseUrl = "mysql.url";
	String dataBaseUser = "mysql.user";
	String dataBasePassword = "mysql.passwd";

	public SqlConnector(String dataBaseUrl, String dataBaseUser, String dataBasePassword)
			throws IOException{
		this.dataBaseUrl = dataBaseUrl;
		this.dataBaseUser = dataBaseUser;
		this.dataBasePassword = dataBasePassword;
	}
	
	public void createConnection() throws ClassNotFoundException, SQLException{
		if(this.connection == null){
			Class.forName("com.mysql.jdbc.Driver");
			this.connection = (Connection) 
					DriverManager.getConnection(dataBaseUrl, dataBaseUser, dataBasePassword);
		}
	}
	
	public void close() throws SQLException {
		if(connection != null){
			connection.close();
			connection = null;
		}
	}
	
	public boolean isClosed() throws SQLException{
		if(connection == null){
			return true;
		}
		return connection.isClosed();
	}
}
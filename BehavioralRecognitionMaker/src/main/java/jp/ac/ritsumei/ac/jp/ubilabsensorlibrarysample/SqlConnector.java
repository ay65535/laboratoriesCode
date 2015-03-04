package jp.ac.ritsumei.ac.jp.ubilabsensorlibrarysample;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SqlConnector {
	
	private Properties dataBasePropaty;
	protected Connection connection;

	public SqlConnector(String propatyName) throws IOException{
//		this.dataBasePropaty = new Properties();
//		dataBasePropaty.load( new FileInputStream(propatyName) );
	}
	
	public void createConnection() throws ClassNotFoundException, SQLException{
		this.connection = getConnection();
	}
	
	private Connection getConnection() throws ClassNotFoundException, SQLException{
//		String dataBaseUrl = dataBasePropaty.getProperty("kubiwa.mysql.url");
//		dataBaseUrl = "jdbc:mysql://localhost:"+3306+"/blackhole";
		String dataBaseUrl = "jdbc:mysql://exp-www.ubi.cs.ritsumei.ac.jp:3306/blackhole";
		
		String dataBaseUser = "kubiwauser";
		String dataBasePassword = "n0pr1vacy";
		Class.forName("com.mysql.jdbc.Driver");
		return (Connection) DriverManager.getConnection(dataBaseUrl, dataBaseUser, dataBasePassword);
	}
	
	public String getDBName(){
		return dataBasePropaty.getProperty("kubiwa.mysql.table");
	}
	
	public void close() throws SQLException {
		if(connection != null){
			connection.close();
		}
	}
	
	public boolean isClosed() throws SQLException{
		if(connection == null){
			return true;
		}
		return connection.isClosed();
	}
}

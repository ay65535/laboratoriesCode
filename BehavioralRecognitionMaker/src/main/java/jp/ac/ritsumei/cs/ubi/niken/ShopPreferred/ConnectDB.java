package jp.ac.ritsumei.cs.ubi.niken.ShopPreferred;

import java.sql.*;


public class ConnectDB {

	  public static Connection DBConnect(String user,String pass ,String url){
	
		  	    try {
		  		Class.forName("com.mysql.jdbc.Driver");
		  		
		  		//Connection conn = DriverManager.getConnection(url,user,pass); 
		  		
		  		Connection conn = DriverManager.getConnection(url, user,pass);
		  		return conn;
		  		
		  		
		  	    } catch (ClassNotFoundException e) {
		  		System.out.println("connect error"+ e);
		  	    } catch (SQLException e) { 
		  		System.out.println("connect error"+ e);
		  	    }
				return null;

		  	  	  
	  }		  


}	
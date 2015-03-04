package jp.ac.ritsumei.cs.ubi.zukky.BRM.kubiwa;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.PreparedStatement;

public class BlackholeConnector extends SqlConnector{

	public BlackholeConnector(String propatyName) throws IOException{
		super(propatyName);
	}

	public List<Gps> selectGps(int devId, Timestamp sTime, Timestamp eTime) throws SQLException {
		PreparedStatement statement = null;
		ResultSet resultset = null;
		List<Gps> r = new ArrayList<Gps>();
		try {
			String sql = "select * from LocationLog where devid = ? and time between ? and ? order by time asc";
			statement = (PreparedStatement) connection.prepareStatement(sql);
			statement.setInt(1, devId);
			statement.setTimestamp(2, sTime);
			statement.setTimestamp(3, eTime);

//			System.out.println(statement);
			resultset = statement.executeQuery();
			while(resultset.next()){
				Gps g = new Gps(resultset.getDouble("lat"), resultset.getDouble("lng"));
				g.setDevId(devId);
				g.setAcc(resultset.getFloat("acc"));
				g.setlTime(resultset.getTimestamp("time"));
				g.setSpeed(resultset.getFloat("speed"));
				g.setTime(resultset.getTimestamp("provider_time"));
				r.add(g);
			}
			return r;
		}finally{
			if(statement != null && !statement.isClosed()){
				statement.close();
			}
			if(resultset != null && !resultset.isClosed()){
				resultset.close();
			}
		}
	}

	public List<Gps> selectGpsPlace(int devId, Timestamp sTime, Timestamp eTime, 
			double latLow, double latHigh, double lngLow, double lngHigh) throws SQLException{
		PreparedStatement statement = null;
		ResultSet resultset = null;
		List<Gps> r = new ArrayList<Gps>();
		try {
			String sql = "select * from LocationLog where devid = ? and (time between ? and ?) " +
					"and (lat between ? and ?) " +
					"and (lng between ? and ?) order by time asc";
			statement = (PreparedStatement) connection.prepareStatement(sql);
			statement.setInt(1, devId);
			statement.setTimestamp(2, sTime);
			statement.setTimestamp(3, eTime);
			statement.setDouble(4, latLow);
			statement.setDouble(5, latHigh);
			statement.setDouble(6, lngLow);
			statement.setDouble(7, lngHigh);
			
//			System.out.println(statement);
			resultset = statement.executeQuery();
			while(resultset.next()){
				Gps g = new Gps(resultset.getDouble("lat"), resultset.getDouble("lng"));
				g.setDevId(devId);
				g.setAcc(resultset.getFloat("acc"));
				g.setlTime(resultset.getTimestamp("time"));
				g.setSpeed(resultset.getFloat("speed"));
				g.setTime(resultset.getTimestamp("provider_time"));
				r.add(g);
			}
			return r;
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

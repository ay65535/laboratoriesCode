package jp.ac.ritsumei.ac.jp.ubilabsensorlibrarysample;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jp.ac.ritsumei.cs.ubi.zukky.BRM.kubiwa.Acceleration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class BlackholeConnector extends SqlConnector{

	public BlackholeConnector(String propatyName) throws IOException{
		super(propatyName);
	}

	public List<Acceleration> selectAcc(String sql) throws SQLException, JSONException{
		Statement statement = null;
		ResultSet resultset = null;
		List<Acceleration> r = new ArrayList<Acceleration>();
		try {
			statement = (Statement) connection.createStatement();
			resultset = statement.executeQuery(sql);
			while(resultset.next()){
				String text = resultset.getString("value");
				Timestamp time = resultset.getTimestamp("time");
				JSONArray a = new JSONArray(text);
				for(int i = 0 ; i < a.length() ; i++){
					JSONObject s = a.getJSONObject(i);
					
					Acceleration acc = new Acceleration(new Acceleration(time.getTime() + s.getLong("t"),
							(float)s.getDouble("x"), (float)s.getDouble("y"), (float)s.getDouble("z")));
					
					//2012_11_25 tによる時刻補正を行うとうまくいかない。補正自体が間違ってる可能性大
					//acc.setTime(time.getTime() + (s.getLong("t")/1000000000));
					acc.setTime(time.getTime());

					r.add(acc);
					
					
//					System.out.println("time.getTime()" + time.getTime());
//					System.out.println("s.getLong(t) " + s.getLong("t"));
				}
			}
//			resultset.next();
//			System.out.println(sql);
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

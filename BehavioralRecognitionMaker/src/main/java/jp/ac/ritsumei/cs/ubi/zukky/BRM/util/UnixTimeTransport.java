package jp.ac.ritsumei.cs.ubi.zukky.BRM.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 他の形式のデータをUnixTimeに変換するためのクラス
 * @author zukky
 *
 */
public class UnixTimeTransport {

	/**
	 * 
	 * @param time: unixTimeに変換したい時刻を格納
	 * (ただし，formatは[yyyy/MM/dd HH:mm:ss] か [yyyy-MM-dd HH:mm:ss]のいずれかで指定すること)
	 */
	public Long getUnixTime(String time){
		
		Long unixTime = null;
		
		if(time.indexOf('/') != -1){
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		    Date date;
			try {
				date = sdf.parse(time);
				unixTime = date.getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("このフォーマットではUnixTimeに変換できません");
				System.err.println("yyyy/MM/dd HH:mm:ss, もしくは yyyy/MM/dd HH:mm:ss の形式で指定してください");
			}
		}else if(time.indexOf('-') != -1){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Date date;
			try {
				date = sdf.parse(time);
				unixTime = date.getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("このフォーマットではUnixTimeに変換できません");
				System.err.println("yyyy/MM/dd HH:mm:ss, もしくは yyyy/MM/dd HH:mm:ss の形式で指定してください");
			}
		}else{
			System.err.println("このフォーマットではUnixTimeに変換できません");
			System.err.println("yyyy/MM/dd HH:mm:ss, もしくは yyyy/MM/dd HH:mm:ss の形式で指定してください");
		}
		
		return unixTime;
		
	}
	
	/**
	 * 現在時刻をunixTimeで取得する
	 * @return currentUnixTime: 現在のunixTime
	 */
	public Long getCurrentUnixTime(){
		Long currentUnixTime = System.currentTimeMillis() / 1000L;
		
		return currentUnixTime;
	}
	
}
package jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule.database.MovementResultConnector;
import jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule.plan.DropInSite;
import jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule.plan.TransitionBetweenDropInSite;
import jp.ac.ritsumei.cs.ubi.takuchan.dropInSiteMaker.DropInSiteGenerator;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.featurePoint.FeaturePoint;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.main.BehaviorRecognitionMaker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 立ち寄りポイントと移動実績をマイニングするクラス．
 * ずっきーとたくちゃんのプログラムをjar化したものを呼び出してます．
 * テーブルから，各ユーザの設定を読み込んでそれにしたがってマイニングする．
 * @author sacchin
 *
 */
public class MovementResultModule {
	/**
	 * デフォルトのマイニング期間（日）
	 */
	private final int DEFAULT_MINING_INTERVAL = 14;

	/**
	 * マイニング結果を格納するDBへのコネクター．
	 */
	private MovementResultConnector connector = null;

	/**
	 * 各ユーザマイニングの設定．
	 */
	private Map<Integer, Map<Integer, Timestamp>> settings = null;

	/**
	 * コンストラクタ．
	 */
	public MovementResultModule(){
		try {
			connector = new MovementResultConnector(
					"jdbc:mysql://localhost:3306/movement_result?useUnicode=true&characterEncoding=utf8", 
					"plancheckeruser", "n0pr1vacy");
			connector.createConnection();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * MovementResultConnectorをcloseするメソッド．
	 */
	public void close(){
		try {
			if(connector != null && !connector.isClosed()){
				connector.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * DBからユーザの設定をすべて読み込むメソッド．
	 * @return 全ユーザの設定情報．
	 * @throws SQLException
	 */
	public Map<Integer, Map<Integer, Timestamp>> loadAllSettings() throws SQLException{
		connector.checkConection();
		if(connector == null || connector.isClosed()){
			return null;
		}
		try {
			settings = connector.selectAllMiningSetting();
			return settings;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 移動実績を更新するメソッド．
	 * すべての移動実績を削除した後に，各ユーザの設定にしたがって移動実績を生成＆格納．
	 * @return マイニングに使用したユーザの設定．
	 * @throws SQLException
	 */
	public Map<Integer, Map<Integer, Timestamp>> updateMovementResult() throws SQLException{
		connector.checkConection();
		if(connector == null || settings == null || connector.isClosed()){
			return null;
		}
		try {
			for(Integer devid : settings.keySet()){
				Map<Integer, Timestamp> setting = settings.get(devid);
				Timestamp endTime = setting.values().iterator().next();
				if(endTime == null){
					continue;
				}
				Integer miningInterval = setting.keySet().iterator().next();
				if(miningInterval == null){
					continue;
				}

				JSONArray dropInSites = connector.selectDropInSites(devid);
				HashMap<Long, DropInSite> dropInSitesMap = new HashMap<Long, DropInSite>();
				for(int i = 0 ; i < dropInSites.length() ; i++){
					DropInSite site = DropInSite.createDropInSite(dropInSites.getJSONObject(i));
					dropInSitesMap.put(site.getSiteId(), site);
				}

				if(miningInterval <= 0){
					connector.insertMiningSetting(devid.intValue(), DEFAULT_MINING_INTERVAL);
					createMovementResultsAndInsert(endTime, -1 * DEFAULT_MINING_INTERVAL, devid, dropInSitesMap, connector);
				}else{
					createMovementResultsAndInsert(endTime, -1 * miningInterval, devid, dropInSitesMap, connector);
				}
			}
			return settings;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 立ち寄りポイントを更新するメソッド．
	 * すべての立ち寄りポイントを削除した後に，各ユーザの設定にしたがって立ち寄りポイントを生成＆格納．
	 * @throws SQLException
	 */
	public void updateDropInSite() throws SQLException{
		connector.checkConection();
		if(connector == null || settings == null || connector.isClosed()){
			return;
		}
		try {
			for(Integer devid : settings.keySet()){
				Map<Integer, Timestamp> setting = settings.get(devid);
				Timestamp endTime = setting.values().iterator().next();
				if(endTime == null){
					continue;
				}
				Integer miningInterval = setting.keySet().iterator().next();
				if(miningInterval == null){
					continue;
				}
				
				JSONArray dropInSites = null;
				if(miningInterval <= 0){
					connector.insertMiningSetting(devid.intValue(), DEFAULT_MINING_INTERVAL);
					dropInSites = createDropInSites(endTime, -1 * DEFAULT_MINING_INTERVAL, devid.intValue());
				}else{
					dropInSites = createDropInSites(endTime, -1 * miningInterval, devid.intValue());
				}
				if(dropInSites == null){
					System.out.println(devid.intValue() + "'s dropInSites is null!");
					continue;
				}
				connector.deleteDropInSites(devid);
				for(int i = 0 ; i < dropInSites.length() ; i++){
					connector.insertDropInSite(dropInSites.getJSONObject(i), devid);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * すべての立ち寄りポイントを削除した後に，１人のユーザの移動実績を生成し格納するメソッド．
	 * 立ち寄りポイント間の遷移すべての移動実績を作成する．
	 * @param interval マイニング期間．
	 * @param devid ユーザのデバイスID．
	 * @param dropInSites 立ち寄りポイント
	 * @param connector DBのコネクター．
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private void createMovementResultsAndInsert(Timestamp endTime, int interval, int devid, HashMap<Long, DropInSite> dropInSites, 
			MovementResultConnector connector) throws SQLException, ClassNotFoundException, IOException{
		connector.checkConection();
		if(connector == null || connector.isClosed()){
			return;
		}
		int delNum1 = connector.deleteMovementResults(devid);
		int delNum2 = connector.deleteMovementLogs(devid);

		//通常は，データベースから読み込んできた期間を利用する
		Calendar cl = Calendar.getInstance();
		cl.setTimeInMillis(endTime.getTime());
		cl.add(Calendar.DAY_OF_YEAR, interval);
		cl.set(Calendar.HOUR_OF_DAY, 5);
		cl.set(Calendar.MINUTE, 0);
		cl.set(Calendar.SECOND, 0);
		Timestamp startTime = new Timestamp(cl.getTimeInMillis());

		//評価用に期間を固定		
//		Calendar cl = Calendar.getInstance();
//		cl.set(Calendar.MONTH, Calendar.JANUARY);
//		cl.set(Calendar.DAY_OF_MONTH, 21);
//		cl.set(Calendar.HOUR_OF_DAY, 5);
//		cl.set(Calendar.MINUTE, 0);
//		cl.set(Calendar.SECOND, 0);
//		Timestamp startTime = new Timestamp(cl.getTimeInMillis());
//		cl.add(Calendar.DAY_OF_MONTH, 14);
//		Timestamp endTime = new Timestamp(cl.getTimeInMillis());
		
		System.out.println("create" + devid + "'s MovementResult " + 
				startTime + " - " + endTime + " and delNum1 = " + delNum1 + ", delNum2 = " + delNum2);
		BehaviorRecognitionMaker brm = new BehaviorRecognitionMaker();
		for(Long dropInSiteID : dropInSites.keySet()){
			DropInSite fromSite = dropInSites.get(dropInSiteID);
			for(TransitionBetweenDropInSite transitions : fromSite.getTransitions()){
				Timestamp[][] travelTimes = transitions.getTravelTimes();

//				FeaturePoint startPoint = creatFeaturePoint(startTime, fromSite);
				DropInSite toSite = dropInSites.get(transitions.getToID());
				if(toSite == null){
					System.out.println(devid + "'s tosite from " + fromSite.getSiteId() + " is null");
					continue;
				}
//				FeaturePoint endPoint = creatFeaturePoint(endTime, fromSite);

				brm.getBehaviorRecognition(travelTimes, devid);
				JSONArray movementResult = brm.getMovementResult();
				int mrId = insertMovementResults(devid, connector, movementResult, 
						fromSite.getSiteId(), toSite.getSiteId());

				List<JSONArray> pathList = brm.getPathList();
				for(JSONArray path : pathList){
					insertMovementLogs(devid, connector, path, mrId);
				}
			}
		}
	}

	/**
	 * 時刻と立ち寄りポイントからFeaturePointを作成するクラス．
	 * 移動実績を作成するための始点と終点のために．
	 * @param time マイニングの開始時刻またはマイニングの終了時刻．
	 * @param site 始点または終点．
	 * @return 立ち寄りポイントから生成したFeaturePoint．
	 */
	public FeaturePoint creatFeaturePoint(Timestamp time, DropInSite site){
		FeaturePoint point = new FeaturePoint();
		point.setArrivalTime(time);
		point.setDepartureTime(time);
		double[] rect = site.getAccuracyRect();
		point.setMinMaxLatLng(rect[0], rect[1], rect[2], rect[3]);
		return point;
	}

	/**
	 * 移動実績をDBに格納するメソッド
	 * @param devid デバイスID．
	 * @param connector DBのコネクター．
	 * @param movementResult 移動実績．
	 * @param fromId 移動の始点である立ち寄りポイントのID．
	 * @param toId 移動の終点である立ち寄りポイントのID．
	 * @return DBの移動実績ID．
	 * @throws SQLException
	 */
	private int insertMovementResults(int devid, MovementResultConnector connector,
			JSONArray movementResult, long fromId, long toId) throws SQLException{
		connector.checkConection();
		if(movementResult == null || movementResult.length() == 0 ||
				connector == null || connector.isClosed()){
			return -1;
		}
		try {
			connector.insertMovemetResult(movementResult, devid, fromId, toId);
			return connector.selectMovemetResultID(devid, fromId, toId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 過去に移動した単一の移動実績を格納する．
	 * @param devid ユーザのデバイスID．
	 * @param connector DBへのコネクタ．
	 * @param path 単一の移動実績．
	 * @param mrId DBの移動実績ID．
	 * @throws SQLException
	 */
	private void insertMovementLogs(int devid, MovementResultConnector connector,
			JSONArray path, int mrId) throws SQLException{
		connector.checkConection();
		if(path == null || path.length() == 0 ||
				connector == null || connector.isClosed()){
			return;
		}
		try {
			connector.insertMovemetLog(path, devid, mrId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * マイニング期間とデバイスIDから立ち寄りポイントを生成するメソッド．
	 * @param day マイニング期間
	 * @param devid デバイスID
	 * @return 立ち寄りポイント
	 * @throws SQLException
	 * @throws JSONException
	 */
	private JSONArray createDropInSites(Timestamp endTime, int day, int devid)
			throws SQLException, JSONException {
		if(devid < 0){
			System.out.println("day=" + day + ", devid=" + devid );
			return null;
		}

		//通常は，データベースから読み込んできた期間を利用する
		Calendar cl = Calendar.getInstance();
		cl.setTimeInMillis(endTime.getTime());
		cl.add(Calendar.DAY_OF_YEAR, day);
		System.out.println("create" + devid + "'s DropInSites" + 
				new Timestamp(cl.getTimeInMillis()) + " - " + endTime);
		DropInSiteGenerator gen = new DropInSiteGenerator();
		JSONArray jArray = gen.getDropInSiteJSON(
				new Timestamp(cl.getTimeInMillis()), 
				endTime, devid, true);

		//評価用に期間を固定		
//		Calendar cl = Calendar.getInstance();
//		cl.set(Calendar.MONTH, Calendar.JANUARY);
//		cl.set(Calendar.DAY_OF_MONTH, 21);
//		cl.set(Calendar.HOUR_OF_DAY, 5);
//		cl.set(Calendar.MINUTE, 0);
//		cl.set(Calendar.SECOND, 0);
//		Timestamp start = new Timestamp(cl.getTimeInMillis());
//
//		cl.add(Calendar.DAY_OF_MONTH, 14);
//		Timestamp end = new Timestamp(cl.getTimeInMillis());
//		System.out.println("create" + devid + "'s DropInSites" + start + " - " + end);
//
//		DropInSiteGenerator gen = new DropInSiteGenerator();
//		JSONArray jArray = gen.getDropInSiteJSON(start, end, devid, true);
		return jArray;
	}

	/**
	 * 立ち寄りポイント間の遷移情報は，たくちゃん固有のIDで表現されているので，DBに格納されているIDに置換するメソッド．
	 * @throws SQLException
	 */
	public void updateTransitions() throws SQLException{
		connector.checkConection();
		if(connector == null|| settings == null || connector.isClosed()){
			return;
		}
		try {
			for(Integer devid : settings.keySet()){
				JSONArray dropInSites = connector.selectDropInSites(devid.intValue());

				for(int i = 0 ; i < dropInSites.length() ; i++){
					JSONObject dropInSite = dropInSites.getJSONObject(i);
					JSONArray newTransition = createNewTransition(connector, 
							devid, new JSONArray(dropInSite.getString("transition")));

					if(newTransition == null){
						System.out.println("newTransition is null!");
						continue;
					}
					connector.updatetTransition(dropInSite.getInt("id"), devid, newTransition.toString());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 立ち寄りポイント間の遷移情報は，たくちゃん固有のIDで表現されているので，DBに格納されているIDに置換するメソッド．
	 * @param connector DBのコネクター．
	 * @param devid ユーザのデバイスID
	 * @param oldTransition たくちゃん固有IDで表現された遷移情報
	 * @return DBのIDに置換した遷移情報．
	 * @throws JSONException
	 * @throws SQLException
	 */
	private static JSONArray createNewTransition(MovementResultConnector connector,
			Integer devid, JSONArray oldTransition) throws JSONException,
			SQLException {
		connector.checkConection();
		if(connector == null || connector.isClosed() || devid < 0 || oldTransition == null){
			return null;
		}

		JSONArray newTransition = new JSONArray();
		for(int j = 0 ; j < oldTransition.length() ; j++){
			JSONObject oldTo = oldTransition.getJSONObject(j);
			JSONArray traveltimes = oldTo.getJSONArray("travelTimes");
			int count = oldTo.getInt("count");
			int id = connector.selectDropInSiteID(oldTo.getString("to"), devid);
			JSONObject newTo = new JSONObject();
			newTo.put("to", id);
			newTo.put("count", count);
			newTo.put("travelTimes", traveltimes);
			newTransition.put(newTo);
		}
		return newTransition;
	}
}

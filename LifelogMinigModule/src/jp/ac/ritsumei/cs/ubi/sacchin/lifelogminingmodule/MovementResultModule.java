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
 * �������|�C���g�ƈړ����т��}�C�j���O����N���X�D
 * �������[�Ƃ��������̃v���O������jar���������̂��Ăяo���Ă܂��D
 * �e�[�u������C�e���[�U�̐ݒ��ǂݍ���ł���ɂ��������ă}�C�j���O����D
 * @author sacchin
 *
 */
public class MovementResultModule {
	/**
	 * �f�t�H���g�̃}�C�j���O���ԁi���j
	 */
	private final int DEFAULT_MINING_INTERVAL = 14;

	/**
	 * �}�C�j���O���ʂ��i�[����DB�ւ̃R�l�N�^�[�D
	 */
	private MovementResultConnector connector = null;

	/**
	 * �e���[�U�}�C�j���O�̐ݒ�D
	 */
	private Map<Integer, Map<Integer, Timestamp>> settings = null;

	/**
	 * �R���X�g���N�^�D
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
	 * MovementResultConnector��close���郁�\�b�h�D
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
	 * DB���烆�[�U�̐ݒ�����ׂēǂݍ��ރ��\�b�h�D
	 * @return �S���[�U�̐ݒ���D
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
	 * �ړ����т��X�V���郁�\�b�h�D
	 * ���ׂĂ̈ړ����т��폜������ɁC�e���[�U�̐ݒ�ɂ��������Ĉړ����т𐶐����i�[�D
	 * @return �}�C�j���O�Ɏg�p�������[�U�̐ݒ�D
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
	 * �������|�C���g���X�V���郁�\�b�h�D
	 * ���ׂĂ̗������|�C���g���폜������ɁC�e���[�U�̐ݒ�ɂ��������ė������|�C���g�𐶐����i�[�D
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
	 * ���ׂĂ̗������|�C���g���폜������ɁC�P�l�̃��[�U�̈ړ����т𐶐����i�[���郁�\�b�h�D
	 * �������|�C���g�Ԃ̑J�ڂ��ׂĂ̈ړ����т��쐬����D
	 * @param interval �}�C�j���O���ԁD
	 * @param devid ���[�U�̃f�o�C�XID�D
	 * @param dropInSites �������|�C���g
	 * @param connector DB�̃R�l�N�^�[�D
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

		//�ʏ�́C�f�[�^�x�[�X����ǂݍ���ł������Ԃ𗘗p����
		Calendar cl = Calendar.getInstance();
		cl.setTimeInMillis(endTime.getTime());
		cl.add(Calendar.DAY_OF_YEAR, interval);
		cl.set(Calendar.HOUR_OF_DAY, 5);
		cl.set(Calendar.MINUTE, 0);
		cl.set(Calendar.SECOND, 0);
		Timestamp startTime = new Timestamp(cl.getTimeInMillis());

		//�]���p�Ɋ��Ԃ��Œ�		
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
	 * �����Ɨ������|�C���g����FeaturePoint���쐬����N���X�D
	 * �ړ����т��쐬���邽�߂̎n�_�ƏI�_�̂��߂ɁD
	 * @param time �}�C�j���O�̊J�n�����܂��̓}�C�j���O�̏I�������D
	 * @param site �n�_�܂��͏I�_�D
	 * @return �������|�C���g���琶������FeaturePoint�D
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
	 * �ړ����т�DB�Ɋi�[���郁�\�b�h
	 * @param devid �f�o�C�XID�D
	 * @param connector DB�̃R�l�N�^�[�D
	 * @param movementResult �ړ����сD
	 * @param fromId �ړ��̎n�_�ł��闧�����|�C���g��ID�D
	 * @param toId �ړ��̏I�_�ł��闧�����|�C���g��ID�D
	 * @return DB�̈ړ�����ID�D
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
	 * �ߋ��Ɉړ������P��̈ړ����т��i�[����D
	 * @param devid ���[�U�̃f�o�C�XID�D
	 * @param connector DB�ւ̃R�l�N�^�D
	 * @param path �P��̈ړ����сD
	 * @param mrId DB�̈ړ�����ID�D
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
	 * �}�C�j���O���Ԃƃf�o�C�XID���痧�����|�C���g�𐶐����郁�\�b�h�D
	 * @param day �}�C�j���O����
	 * @param devid �f�o�C�XID
	 * @return �������|�C���g
	 * @throws SQLException
	 * @throws JSONException
	 */
	private JSONArray createDropInSites(Timestamp endTime, int day, int devid)
			throws SQLException, JSONException {
		if(devid < 0){
			System.out.println("day=" + day + ", devid=" + devid );
			return null;
		}

		//�ʏ�́C�f�[�^�x�[�X����ǂݍ���ł������Ԃ𗘗p����
		Calendar cl = Calendar.getInstance();
		cl.setTimeInMillis(endTime.getTime());
		cl.add(Calendar.DAY_OF_YEAR, day);
		System.out.println("create" + devid + "'s DropInSites" + 
				new Timestamp(cl.getTimeInMillis()) + " - " + endTime);
		DropInSiteGenerator gen = new DropInSiteGenerator();
		JSONArray jArray = gen.getDropInSiteJSON(
				new Timestamp(cl.getTimeInMillis()), 
				endTime, devid, true);

		//�]���p�Ɋ��Ԃ��Œ�		
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
	 * �������|�C���g�Ԃ̑J�ڏ��́C���������ŗL��ID�ŕ\������Ă���̂ŁCDB�Ɋi�[����Ă���ID�ɒu�����郁�\�b�h�D
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
	 * �������|�C���g�Ԃ̑J�ڏ��́C���������ŗL��ID�ŕ\������Ă���̂ŁCDB�Ɋi�[����Ă���ID�ɒu�����郁�\�b�h�D
	 * @param connector DB�̃R�l�N�^�[�D
	 * @param devid ���[�U�̃f�o�C�XID
	 * @param oldTransition ���������ŗLID�ŕ\�����ꂽ�J�ڏ��
	 * @return DB��ID�ɒu�������J�ڏ��D
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

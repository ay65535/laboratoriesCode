package jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import org.json.JSONException;

import jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule.database.BlackholeConnector;
import jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule.plan.ArrivalTimeEstimater;
import jp.ac.ritsumei.cs.ubi.sacchin.lifelogminingmodule.plan.MyGeoPoint;

/**
 * �}�C�j���O���Ǘ�����N���X�D
 * ���݂́C�������|�C���g�����ƈړ����ѐ������܂܂�Ă���D
 * CloudStack�ŉ^�p���Ă܂��D
 * @author sacchin
 *
 */
public class MiningModuleManager {

	/**
	 * ���C���N���X�D
	 * cron�Ŗ����T���ɉ񂵂Ă܂��D
	 * @param args
	 */
	public static void main(String[] args){
		if(args != null && 0 < args.length){
			if(args[0].equals("-m")){
				try {
					int devid = Integer.parseInt(args[1]);
					makeCSVFile(devid);
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}else if(args[0].equals("-e")){
				try {
					int devid = Integer.parseInt(args[1]);
					String sTime = args[2];
					String eTime = args[3];
					estimate(devid, sTime, eTime);
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
			return;
		}

		minignAndTweet();
	}
	
	private static void estimate(int devid, String sTime, String eTime){
		BlackholeConnector bConnector = null;
		try {
			bConnector = new BlackholeConnector(
					"jdbc:mysql://exp-www.ubi.cs.ritsumei.ac.jp:3306/blackhole?useUnicode=true&characterEncoding=utf8", 
					"kubiwauser", "n0pr1vacy");
			bConnector.createConnection();
			ArrayList<MyGeoPoint> p = bConnector.selectTrajectory(devid, sTime, eTime);
			if(p == null || p.isEmpty()){
				System.out.println("---There are no gps data!---");
				return;
			}
			
			ArrivalTimeEstimater.estimate1(devid, p);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			if(bConnector != null){
				try {
					bConnector.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void makeCSVFile(int devid) {
		MovementResultModule movementResultModule = new MovementResultModule();
		Map<Integer, Map<Integer, Timestamp>> result = null;
		try {
			result = movementResultModule.loadAllSettings();
			if(result == null){
				System.out.println("loadAllSettings return null");
			}
//			CsvFileMaker cfm = new CsvFileMaker(result);
//			cfm.writeS(devid);
//			cfm.writeDropInSites(devid);
//			cfm.writeMovementLogs(devid);
//			cfm.writeMovementResult(devid);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(result != null){
				movementResultModule.close();
			}
		}
	}

	private static void minignAndTweet() {
		String tweetStr = getLifelogTweetString();

		try {
			long start = System.currentTimeMillis();
			Map<Integer, Map<Integer, Timestamp>> result = mining();
			long diffSec = (System.currentTimeMillis() - start) / 1000;

			if(result == null){
				tweetStr += "�Ȃɂ��̃G���[�Ń}�C�j���O�ł��Ȃ������E�E�E�B";
			}else{
				tweetStr += result.size() + "�l�����I����āA���Ȃ��̂�" + result.get(29) + "�����Ń}�C�j���O������I";
			}
			tweetStr += diffSec + "�b�ŏI������́B";
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println(tweetStr);
		SimpleTweeter.tweet(tweetStr);
	}

	/**
	 * ���ׂẴ}�C�j���O������N���X�D
	 * @return �}�C�j���O���ʂ̃e�L�X�g�D��Ƀc�C�[�g�̂��߁D
	 * @throws SQLException
	 */
	private static Map<Integer, Map<Integer, Timestamp>> mining() throws SQLException {
		MovementResultModule movementResultModule = new MovementResultModule();
		Map<Integer, Map<Integer, Timestamp>> result = movementResultModule.loadAllSettings();
		if(result == null){
			System.out.println("loadAllSettings return null");
		}
		try {
			movementResultModule.updateDropInSite();
			movementResultModule.updateTransitions();
			movementResultModule.updateMovementResult();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			movementResultModule.close();
		}
		return null;
	}

	/**
	 * �����̃��C�t���O�̃��R�[�h�����J�E���g�������ʂ��e�L�X�g�ɂ��郁�\�b�h�D
	 * ��Ƀc�C�[�g���邽�߁D
	 * @return
	 */
	private static String getLifelogTweetString() {
		try {
			int[] lifelogCount = getLifelogCount();
			if(lifelogCount == null){
				return "�����́A���܂������𐔂���Ȃ������E�E�E�B";
			}

			String tweetStr = "";
			if(0 <= lifelogCount[0] && lifelogCount[0] < 1000){
				tweetStr += "�����́A" + lifelogCount[0] + "�����������Ȃ������ˁE�E�E�B";
			}else if(1000 <= lifelogCount[0] && lifelogCount[0] < 5000){
				tweetStr += "�����́A" + lifelogCount[0] + "�����炢�������ˁB";
			}else if(5000 <= lifelogCount[0]){
				tweetStr += "�����́A" + lifelogCount[0] + "���������Ă��I";
			}
			return tweetStr + "�ʒu����" + lifelogCount[1] + "�s�������B";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "�����́A���܂������𐔂���Ȃ������B";
	}

	/**
	 * �����̃��C�t���O�̃��R�[�h�����J�E���g����N���X�D
	 * ��Ƀc�C�[�g���邽�߁D
	 * @return �J�E���g���ʁD
	 * @throws SQLException
	 */
	private static int[] getLifelogCount() throws SQLException {
		BlackholeConnector connector = null;
		try {
			connector = new BlackholeConnector(
					"jdbc:mysql://exp-www.ubi.cs.ritsumei.ac.jp/blackhole?useUnicode=true&characterEncoding=utf8", 
					"kubiwauser", "n0pr1vacy");
			connector.createConnection();

			Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH) + 1;
			int day = c.get(Calendar.DAY_OF_MONTH);

			int count[] = new int[2];
			count[0] = connector.selectStepCounts(year, month, day);
			count[1] = connector.selectLocationCounts(year, month, day);

			return count;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(connector != null && !connector.isClosed()){
				connector.close();
			}
		}
		return null;
	}
}

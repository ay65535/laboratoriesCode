package jp.ac.ritsumei.cs.ubi.zukky.BRM.transport;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jp.ac.ritsumei.cs.ubi.walker.WalkerState;
import jp.ac.ritsumei.cs.ubi.walker.WalkerState.Type;
import jp.ac.ritsumei.cs.ubi.walker.WalkerStateDetector;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.hubenyVelocity.*;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.kubiwa.Gps;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.transport.Transportation.TransportationType;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.util.Util;

/**
 * 交通手段を判定するクラス
 * @author zukky
 *
 */
public class JudgementTransport {

	/**
	 * 移動手段を判定するメソッド（ダニーさんの歩行判定プログラムを利用）
	 * @param gpsList
	 * @param wsd
	 * @return
	 */
	public List<Transportation> judgeTransport(List<Gps> gpsList, WalkerStateDetector wsd){
		HubenyVelocityCalculator hvCalculator = new HubenyVelocityCalculator();
		List<Transportation> transportationList = new ArrayList<Transportation>();	
		List<HubenyVelocity> hubenyVelocityList = hvCalculator.getHubenyVelocityList(
				gpsList, Util.getSmoothingRate());
		List<WalkerState> walkerStateList = wsd.getCluster();

		transportationList = convertWalkerStateToTransportation(walkerStateList);
		transportationList = judgeWalkingZone(transportationList, hubenyVelocityList);		
		transportationList = judgeAnotherTransport(transportationList, hubenyVelocityList);

		return transportationList;
	}

	/**
	 * 移動手段を判定するメソッド（シュンの歩行判定プログラムを利用）
	 * @param gpsList
	 * @param wsd
	 * @return
	 */
	public List<Transportation> judgeTransport(List<Gps> gpsList, List<Transportation> stepZoneList){
		HubenyVelocityCalculator hvCalculator = new HubenyVelocityCalculator();
		List<Transportation> transportationList = new ArrayList<Transportation>();	
		List<HubenyVelocity> hubenyVelocityList = hvCalculator.getHubenyVelocityList(
				gpsList, Util.getSmoothingRate());


		transportationList = judgeWalkingZone(stepZoneList, hubenyVelocityList);
//		for(Transportation t : transportationList){
//			System.out.println(t.getStartTime() + " - [" + t.getType() + "] - " + t.getEndTime());
//		}
		transportationList = gatherSameTransportTransportationType(transportationList);

		transportationList = judgeAnotherTransport(transportationList, hubenyVelocityList);		

		return transportationList;
	}


	/**
	 * ステップ区間を判定した後に、最高速度を元に具体的な移動手段を判定
	 * @param transportationList
	 * @param HubenyVelocityList
	 * @return
	 */
	private List<Transportation> judgeAnotherTransport(List<Transportation> transportationList, List<HubenyVelocity>HubenyVelocityList){

		transportationList = judgeTransportationType(transportationList, HubenyVelocityList);
		transportationList = gatherSameTransportTransportationType(transportationList);

		//		for(Transportation ts: transportationList){
		//			System.out.println("another: " +ts.getType()  +", "+ ts.getStartTime()+", "+  ts.getEndTime());
		//		}

		transportationList = removeWalkingBetweenSameTransportation(transportationList);
		transportationList = gatherSameTransportTransportationType(transportationList);


		for(Transportation ts: transportationList){
			if((ts.getEndTime().getTime() - ts.getStartTime().getTime()) /1000 < 300){
				ts.setType(TransportationType.WALKING);
			}
		}

		transportationList = gatherSameTransportTransportationType(transportationList);
		transportationList = removeWalkingBetweenSameTransportation(transportationList);
		transportationList = gatherSameTransportTransportationType(transportationList);

		return transportationList;

	}

	/**
	 *  具体的な移動手段（電車、バス、歩行）を最高速度を元に判定
	 * TODO: 自転車判定の追加
	 * @param transportationList
	 * @param gpsList
	 * @return
	 */
	private List<Transportation> judgeTransportationType(List<Transportation> transportationList, List<HubenyVelocity> hubenyVelocityList){

		List<Transportation> resultTsList = new ArrayList<Transportation>();

		for(Transportation ts: transportationList){
			//１つの移動手段の区間内におけるgpsListを抽出
			ts = judgeTransportationType(hubenyVelocityList, ts);
			resultTsList.add(new Transportation(ts));
		}

		return resultTsList;
	}

	/**
	 *  具体的な移動手段（電車、バス、歩行）を最高速度を元に判定
	 * TODO: 自転車判定の追加
	 * @param transportationList
	 * @param gpsList
	 * @return
	 */
	private Transportation judgeTransportationType(List<HubenyVelocity> hubenyVelocityList, Transportation ts){

		JudgementTransport judgeTransport = new JudgementTransport();

		double maxVelocity = judgeTransport.extractMaxHubeny(hubenyVelocityList, ts.getStartTime(), ts.getEndTime());

		if(!ts.getType().equals(TransportationType.WALKING)){
			if(maxVelocity < 15){
				ts.setType(TransportationType.WALKING);
			}else if(maxVelocity < 60){
				ts.setType(TransportationType.BUS);
			}else{
				ts.setType(TransportationType.TRAIN);
			}	
		}
		return ts;
	}

	/**
	 * 歩行区間を抽出するメソッド
	 * @param tsList
	 * @return resultTsList 
	 */
	private List<Transportation> judgeWalkingZone(List<Transportation> tsList, List<HubenyVelocity> hubenyVelocityList){
		JudgementTransport judgementTransport = new JudgementTransport();

		for(Transportation ts: tsList){

			if(ts.getType().equals(TransportationType.WALKING)){

				double maxVelocity = judgementTransport.extractMaxHubeny(hubenyVelocityList, ts.getStartTime(), ts.getEndTime());

				if(maxVelocity < 15){
					ts.setType(TransportationType.WALKING);
				}else{
					ts.setType(TransportationType.UNKNOWN);
				}
			}
		}			
		return tsList;
	}


	/**
	 * 隣り合った同じ交通手段をまとめるメソッド
	 * @param transportationList
	 * @return
	 */
	private List<Transportation> gatherSameTransportTransportationType(List<Transportation> transportationList){

		List<Transportation> resultTsList = new ArrayList<Transportation>();

		Transportation tsBefore = new Transportation();
		boolean firstTime = true;

		for(Transportation ts: transportationList){
			if(firstTime){
				resultTsList.add(new Transportation(ts));
				firstTime = false;
			}else if(!(ts.getType().equals(tsBefore.getType()))){
				resultTsList.add(new Transportation(ts));
			}else{
				resultTsList.get(resultTsList.size()-1).setEndTime(ts.getEndTime());
			}
			tsBefore.setType(ts.getType());
		}

		return resultTsList;
	}

	/**
	 * 同一移動手段に囲まれた歩行区間を削除するメソッド
	 * @param transportationList
	 * @return
	 */
	private List<Transportation> removeWalkingBetweenSameTransportation(List<Transportation> transportationList){

		for(int i=0; i<transportationList.size(); i++){

			if(2 <= i){
				if(!(transportationList.get(i).getType().equals(TransportationType.WALKING))){
					if(transportationList.get(i-2).getType().equals(transportationList.get(i).getType())){

						//System.out.println(transportationList.get(i).getType());
						transportationList.get(i-2).setEndTime(transportationList.get(i).getEndTime());
						transportationList.remove(i);
						transportationList.remove(i-1);

						i = i-1;
					}
				}
			}
		}
		return transportationList;
	}


	/**
	 * WalkerStateクラスをTransportationクラスに変換するメソッド
	 * @param wsList
	 * @return
	 */
	private List<Transportation> convertWalkerStateToTransportation(List<WalkerState> wsList){

		List<Transportation> resultTsList = new ArrayList<Transportation>();

		for(WalkerState ws: wsList){
			Transportation ts = new Transportation();

			Timestamp startTime = new Timestamp(ws.getStartTime());
			Timestamp endTime = new Timestamp(ws.getEndTime());

			startTime.setNanos(0);
			endTime.setNanos(0);

			ts.setStartTime(startTime);
			ts.setEndTime(endTime);

			if(ws.getType().equals(Type.WALKING)){
				ts.setType(TransportationType.WALKING);
			}else{
				ts.setType(TransportationType.UNKNOWN);
			}

			resultTsList.add(new Transportation(ts));
		}			
		return resultTsList;
	}

	/**
	 * 計算したHubenyVelocityの中から、指定した区間内の最高速度を検出するメソッド
	 * @param hvList 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	private double extractMaxHubeny(List<HubenyVelocity> hvList, Timestamp startTime, Timestamp endTime){

		List<HubenyVelocity> devideHvList = new ArrayList<HubenyVelocity>();

		for(HubenyVelocity hv: hvList){
			if(startTime.getTime() < hv.getStartTime().getTime() && hv.getEndTime().getTime() < endTime.getTime()){
				devideHvList.add(hv);
			}
		}

		HubenyVelocity maxVelocity = new HubenyVelocity();
		maxVelocity.setVelocity(0);
		for(HubenyVelocity hv: devideHvList){
			if(maxVelocity.getVelocity() < hv.getVelocity()){
				maxVelocity = hv;
			}
		}
		return maxVelocity.getVelocity();
	}


	/**
	 * 以下のコメントアウトしたメソッドは、一定区間で区切ったgpsListを用いて計算を行う
	 * そのため、全体のgpslistを用いて計算を行った場合とスムージング具合が変化するため、値が変わる
	 * （具体的には、以下のメソッドの方がスムージングが甘くなるため、全体的に速度が速くなる）
	 */

	//	/**
	//	 * 平均速度を算出するメソッド
	//	 * （一定区間で区切ったgpsListを用いて計算を行うので、スムージング具合が全体のgpsListを使った場合と変わるため、値が変わる）
	//	 * @param gpsList
	//	 * @return
	//	 */
	//	private double calculateAverageHubeny(List<Gps> gpsList){
	//		HubenyVelocityCalculator hubenyVelocityCaluculator = new HubenyVelocityCalculator();
	//		Util util = new Util();
	//		List<HubenyVelocity> hubenyVelocityList = hubenyVelocityCaluculator.getHubenyVelocityList(gpsList, util.getSmoothingRate());
	//
	//		double sumVelocity = 0.0;
	//		for(HubenyVelocity hv: hubenyVelocityList){
	//			sumVelocity = sumVelocity + hv.getVelocity();
	//		}
	//
	//		double averageVelocity = sumVelocity / hubenyVelocityList.size();	
	//		return averageVelocity;
	//	}
	//	
	//	/**
	//	 * 最高速度を計算するメソッド
	//	 * （一定区間で区切ったgpsListを用いて計算を行うので、スムージング具合が全体のgpsListを使った場合と変わるため、値が変わる）
	//	 * 
	//	 * @param gpsList
	//	 * @param ts
	//	 * @return
	//	 */
	//	private double calculateMaxHubeny(List<Gps> gpsList, Transportation ts){
	//		
	//		HubenyVelocityCalculator hubenyVelocityCaluculator = new HubenyVelocityCalculator();
	//		Util util = new Util();
	//		
	//		List<HubenyVelocity> hubenyVelocityList = hubenyVelocityCaluculator.getHubenyVelocityList(gpsList, util.getSmoothingRate());
	//				
	//		HubenyVelocity maxVelocity = new HubenyVelocity();
	//		maxVelocity.setVelocity(0);
	//		for(HubenyVelocity hv: hubenyVelocityList){
	//			if(maxVelocity.getVelocity() < hv.getVelocity()){
	//				maxVelocity = hv;
	//			}
	//		}
	//		
	//		return maxVelocity.getVelocity();
	//	}

}
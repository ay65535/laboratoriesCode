package jp.ac.ritsumei.cs.ubi.zukky.BRM.main;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import jp.ac.ritsumei.ac.jp.ubilabsensorlibrarysample.StepCounterMain;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.featurePoint.CheckPointCandidateMaker;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.featurePoint.FeaturePoint;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.featurePoint.Clustering;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.json.Json_Mode;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.json.Json_Route;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.json.Json_Transportation;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.kubiwa.BlackholeConnector;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.kubiwa.Gps;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.route.PathSegment;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.route.Path;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.transhipment.TranshipmentPointMaker;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.transport.JudgementTransport;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.transport.Transportation;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.transport.Transportation.TransportationType;

public class BehaviorRecognitionMaker {
	
	private BlackholeConnector connector = null;;
//	private KubiwaHelper kubiwaHelper = new KubiwaHelper();
	private TranshipmentPointMaker transhipMaker = new TranshipmentPointMaker();
	private JudgementTransport judgementTransport = new JudgementTransport();
	private CheckPointCandidateMaker checkPointMaker = new CheckPointCandidateMaker();

	public BehaviorRecognitionMaker(){
		try {
			this.connector = new BlackholeConnector("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ユーザの移動について解析したい区間のデータを期間を指定して解析
	 * TODO 戻り値をJsonに変更
	 * @param sTime 解析開始時間
	 * @param eTime 解析終了時間
	 * @param startPoint 移動のスタート地点
	 * @param endPoint 移動のゴール地点
	 * @throws IOException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public void getBehaviorRecognition(Timestamp[][] time, int devid) 
//			public void getBehaviorRecognition(Timestamp sTime, Timestamp eTime, FeaturePoint startPoint, 
//					FeaturePoint endPoint, int devid) 
			throws IOException, ClassNotFoundException, SQLException{
		List<FeaturePoint> transhipmentPointList = new ArrayList<FeaturePoint>();
		List<FeaturePoint> transhipmentClusterList = new ArrayList<FeaturePoint>();
		List<FeaturePoint> busClusterList = new ArrayList<FeaturePoint>();
		List<FeaturePoint> walkingClusterList = new ArrayList<FeaturePoint>();
		List<FeaturePoint> bycycleClusterList = new ArrayList<FeaturePoint>();
		List<FeaturePoint> trainClusterList = new ArrayList<FeaturePoint>();
		List<FeaturePoint> allCheckPointCandidateList = new ArrayList<FeaturePoint>();

		List<Path> pathDataList = new ArrayList<Path>();

//		Timestamp[][] time = transportPeriodGetting.getTransportFromstartToend(
//				sTime, eTime, startPoint, endPoint, devid);
		List<Timestamp> startTimeList = new ArrayList<Timestamp>();
		List<Timestamp> endTimeList = new ArrayList<Timestamp>();
		for(int i=0; i < time.length ; i++){
			startTimeList.add(time[i][0]);
			endTimeList.add(time[i][1]);
		}

		StepCounterMain stepConter = new StepCounterMain();
		connector.createConnection();
		
		List<Gps> gpsList = null;
		//ルートごとにループを回して判定
		for(int i = 0 ; i < startTimeList.size() ; i++){
			System.out.println("time(" + startTimeList.size() + ") " + time[i][0]);

			gpsList = connector.selectGps(devid, startTimeList.get(i), endTimeList.get(i));
//			gpsList = kubiwaHelper.getGps(29, startTimeList.get(i), endTimeList.get(i));
			List<Transportation> transportationList = 
					stepConter.judgeStepZone(startTimeList.get(i), endTimeList.get(i), devid, 2000, 0);
			transportationList = judgementTransport.judgeTransport(gpsList, transportationList);
			
			
			//////////////*乗り換えポイントを判別*////////////////////////////////////////////////////////////////////////////////////
			transhipmentPointList = transhipMaker.getTranshipmentPoint(gpsList, transportationList, i);
			transhipmentPointList = giveUniqueId(transhipmentPointList, i + 1, 0);

			if(transhipmentClusterList.size() == 0){
				for(FeaturePoint transhipmentPoint: transhipmentPointList){
					transhipmentClusterList.add(transhipmentPoint);
				}
			}else{
				transhipmentClusterList = Clustering.clusterFeaturePoint(
						transhipmentClusterList, transhipmentPointList,40, i);	
			}
			//////////////*乗り換えポイントを判別処理終了*//////////////////////////////////////////////////////////////////////////////////
			

			//マージしたポイントのIDに応じて、元のリストのIDを更新
			updateId(transhipmentPointList, transhipmentClusterList);

			Path path = new Path();
			path.setTransportationList(transportationList);
			FeaturePoint startPoint = new FeaturePoint();
			startPoint.setTime(startTimeList.get(i));
			startPoint.setDepartureTime(startTimeList.get(i));
			startPoint.setArrivalTime(startTimeList.get(i));
			path.setStartPoint(startPoint);
			FeaturePoint endPoint = new FeaturePoint();
			endPoint.setTime(endTimeList.get(i));
			endPoint.setDepartureTime(endTimeList.get(i));
			endPoint.setArrivalTime(endTimeList.get(i));
			path.setEndPoint(endPoint);
			path.setPathID(i+1);
			
			if(transportationList.size() != transhipmentPointList.size() + 1){
				System.out.println("[check] " + transportationList.size() + " - " +
						transhipmentPointList.size());
				continue;
			}

			int checkpointNumber = 0;
			/*ルートセグメント（１つの移動手段）ごとにチェックポイント候補を判定*/
			for(int j=0; j<transportationList.size(); j++){
				PathSegment pathSegment = new PathSegment();
				if(transhipmentPointList.isEmpty()){
					pathSegment.setTsStartPoint(startPoint);
					pathSegment.setTsEndPoint(endPoint);
					pathSegment.setTravelTime(endPoint.getTime().getTime() - 
							startPoint.getTime().getTime());
				}else if(j==0){
					pathSegment.setTsStartPoint(startPoint);
					pathSegment.setTsEndPoint(transhipmentPointList.get(j));
					pathSegment.setTravelTime(transhipmentPointList.get(j).getTime().getTime() - 
							startPoint.getTime().getTime());
				}else if(j==transportationList.size()-1){
					pathSegment.setTsStartPoint(transhipmentPointList.get(j-1));
					pathSegment.setTsEndPoint(endPoint);
					pathSegment.setTravelTime(endPoint.getTime().getTime() - 
							transhipmentPointList.get(j-1).getTime().getTime());
				}else{
					pathSegment.setTsStartPoint(transhipmentPointList.get(j-1));
					pathSegment.setTsEndPoint(transhipmentPointList.get(j));
					pathSegment.setTravelTime(transhipmentPointList.get(j).getTime().getTime() - 
							transhipmentPointList.get(j-1).getTime().getTime());
				}
				if(pathSegment.getTravelTime() < 0 || 1800000 < pathSegment.getTravelTime()){
					System.out.println("[check] " + transportationList.size() + " - " +
							transhipmentPointList.size() + " " +
							transhipmentPointList.isEmpty() + "[" + i + "][" + j + "] - " +
							pathSegment.getTsStartPoint().getTime() + " - " +
							pathSegment.getTsEndPoint().getTime() + 
							", travelTime = " + pathSegment.getTravelTime());
				}

				List<FeaturePoint> checkPointCandidateList = new ArrayList<FeaturePoint>();
				Transportation ts = transportationList.get(j);
				if(ts.getType().equals(TransportationType.WALKING)){
					System.out.println("walking");
					if(walkingClusterList.isEmpty()){
						walkingClusterList.addAll(checkPointCandidateList);
					}else{
						walkingClusterList = Clustering.clusterFeaturePoint(
								walkingClusterList, checkPointCandidateList, 20, i);	
					}
				}else if(ts.getType().equals(TransportationType.BUS)){
					System.out.println("bus");
					checkPointCandidateList = checkPointMaker.makeCheckPoint(ts.getStartTime(), ts.getEndTime(), 4.5, 20, gpsList, TransportationType.BUS, i);
					checkPointCandidateList = giveUniqueId(checkPointCandidateList, i+1, checkpointNumber);
					if(busClusterList.isEmpty()){
						busClusterList.addAll(checkPointCandidateList);
					}else{
						busClusterList = Clustering.clusterFeaturePoint(
								busClusterList, checkPointCandidateList, 20, i);	
					}
					updateId(checkPointCandidateList, busClusterList);
				}else if(ts.getType().equals(TransportationType.BYCYECLE)){
					System.out.println("bycycle");
					checkPointCandidateList = checkPointMaker.makeCheckPoint(ts.getStartTime(), ts.getEndTime(), 3.5, 10, gpsList, TransportationType.BYCYECLE, i);
					checkPointCandidateList = giveUniqueId(checkPointCandidateList, i+1, checkpointNumber);
					if(bycycleClusterList.isEmpty()){
						bycycleClusterList.addAll(checkPointCandidateList);
					}else{
						bycycleClusterList = Clustering.clusterFeaturePoint(
								bycycleClusterList, checkPointCandidateList, 35, i);	
					}
					updateId(checkPointCandidateList, bycycleClusterList);
				}if(ts.getType().equals(TransportationType.TRAIN)){
					System.out.println("train");
					checkPointCandidateList = checkPointMaker.makeCheckPoint(ts.getStartTime(), ts.getEndTime(), 10, 50, gpsList, TransportationType.TRAIN, i);
					checkPointCandidateList = giveUniqueId(checkPointCandidateList, i+1, checkpointNumber);
					if(trainClusterList.isEmpty()){
						trainClusterList.addAll(checkPointCandidateList);
					}else{
						trainClusterList = Clustering.clusterFeaturePoint(
								trainClusterList, checkPointCandidateList,50, i);	
					}
					updateId(checkPointCandidateList, trainClusterList);
				}
				pathSegment.setPathID(i+1);
				checkpointNumber = checkpointNumber + checkPointCandidateList.size();

				pathSegment.setCheckPointCandidateList(checkPointCandidateList);
				pathSegment.setType(ts.getType());

				path.addPathSegmentList(pathSegment);
			}
			pathDataList.add(path);
		}

		allCheckPointCandidateList = margeCheckPointCandidateList(allCheckPointCandidateList, walkingClusterList);
		allCheckPointCandidateList = margeCheckPointCandidateList(allCheckPointCandidateList, busClusterList);
		allCheckPointCandidateList = margeCheckPointCandidateList(allCheckPointCandidateList, bycycleClusterList);
		allCheckPointCandidateList = margeCheckPointCandidateList(allCheckPointCandidateList, trainClusterList);

		
		//ID情報を元に緯度経度を最新のデータに更新する
		pathDataList = updateFeaturePointLatLng(pathDataList, allCheckPointCandidateList, transhipmentClusterList);
		
		/////////////* ここからルート判定 */////////////////////////////////////////////////////////////////////////////
		List<Json_Transportation> jTransportationList = new ArrayList<Json_Transportation>();

		for(Path path: pathDataList){
			int jTransportationId = -1;
			Json_Transportation pathTransportation = new Json_Transportation();
			pathTransportation.setTransportationListString(path.getTransportationList());

			boolean sameTransportation = false;
			for(Json_Transportation jTransporation: jTransportationList){		
				if(pathTransportation.getTransportationListString().equals(
						jTransporation.getTransportationListString())){
					jTransportationId = jTransporation.getjTransportationId();
					sameTransportation = true;
				}
			}

			if(!sameTransportation){
				Json_Transportation jTransportation = new Json_Transportation();
				jTransportation.setTransportationList(path.getTransportationList());
				jTransportation.setTransportationListString(path.getTransportationList());
				jTransportation.setjTransportationId(jTransportationList.size());

				for(Transportation transporation: path.getTransportationList()){
					Json_Mode mode = new Json_Mode();
					mode.setMode(transporation.getType().toString());
					jTransportation.addModeList(mode);
				}
				jTransportationList.add(jTransportation);
				jTransportationId = jTransportationList.size()-1;
			}

			int segmentCount = 0;
			List<Json_Mode> modeList = jTransportationList.get(jTransportationId).getModeList();

			for(PathSegment pathSegment: path.getPathSegmentList()){
				Json_Mode jmode = modeList.get(segmentCount);
				boolean sameRoute = false;
				for(Json_Route jRoute : jmode.getRouteList()){
					/*ルート判定*/
					sameRoute = isSameRoute(pathSegment, jRoute, gpsList);
					if(sameRoute){
						jRoute = updateRouteStatictics(jRoute, pathSegment);
						break;
					}
				}
				//同一ルートが存在しない場合
				if(!sameRoute){
					System.out.println("notsame");
					jmode.addRoute(new Json_Route(pathSegment));
					for(FeaturePoint featurePoint :jmode.getRouteList().get(jmode.getRouteList().size()-1).getCheckPointCandidateList()){
						featurePoint.setNumberPassages(1);
						featurePoint.setNumberStop(1);
					}
				}
				segmentCount++;
			}
		}
		
		if(paths == null){
			paths = new ArrayList<JSONArray>();
		}else{
			paths.clear();
		}
		for(Path path : pathDataList){
			paths.add(JSONConverter.convertToJSON(path));
		}
		
		movementResult = JSONConverter.convertToJSON(jTransportationList);
	}
	
	List<JSONArray> paths = new ArrayList<JSONArray>();
	public List<JSONArray> getPathList(){
		return paths;
	}
	
	JSONArray movementResult;
	public JSONArray getMovementResult(){
		return movementResult;
	}

	/**
	 * チェックポイント候補のリストをマージするメソッド
	 * @param checkPointCandidateList
	 * @return マージしたチェックポイント候補のリスト
	 */
	private List<FeaturePoint> margeCheckPointCandidateList(List<FeaturePoint> clusterList, List<FeaturePoint> checkPointCandidateList){
		for(FeaturePoint checkPointCandidate : checkPointCandidateList){
			clusterList.add(checkPointCandidate);
		}
		return clusterList;
	}

	/**
	 * 同一ルートか判定を行うメソッド
	 * @param pathSegment
	 * @param jRoute
	 * @param gpsList
	 * @return
	 */
	private boolean isSameRoute(PathSegment pathSegment, Json_Route jRoute, List<Gps>gpsList){
		boolean isSameRoute = true;

		if(pathSegment.getTsStartPoint().getId() == jRoute.getTsStartPoint().getId()
				&& pathSegment.getTsEndPoint().getId() == jRoute.getTsEndPoint().getId()){

			List<Gps> pathSegmentGpsList = getGpsPeriod(pathSegment.getTsStartPoint().getDepartureTime(), 
					pathSegment.getTsEndPoint().getArrivalTime(), gpsList);

			for(FeaturePoint cp: jRoute.getCheckPointCandidateList()){
				for(Gps gps: pathSegmentGpsList){
					if(cp.getMinLat() <= gps.getLat() && gps.getLat() <= cp.getMaxLat()
							&& cp.getMinLng() <= gps.getLng() && gps.getLng() <= cp.getMaxLng()){
						isSameRoute = true;
						break;
					}else{
						isSameRoute = false;
					}
				}			
			}
		}else{
			isSameRoute = false;
		}
		return isSameRoute;
	}

	/**
	 * 滞在確率や滞在時間を算出するメソッド
	 */
	private Json_Route updateRouteStatictics(Json_Route jRoute, PathSegment pathSegment){
		List<Integer> idList = new ArrayList<Integer>();
		for(FeaturePoint pathCheckPoint: pathSegment.getCheckPointCandidateList()){
			boolean sameCheckPoint = false;
			for(FeaturePoint jRouteCheckPoint: jRoute.getCheckPointCandidateList()){
				if(jRouteCheckPoint.getId() == pathCheckPoint.getId()){
					jRouteCheckPoint.setSumStayingTime(jRouteCheckPoint.getSumStayingTime() + pathCheckPoint.getStayingTime());
					jRouteCheckPoint.setNumberStop(jRouteCheckPoint.getNumberStop() +1);
					sameCheckPoint = true;
				}
				jRouteCheckPoint.setNumberPassages(jRouteCheckPoint.getNumberPassages() + 1);
				jRouteCheckPoint.setProbability((float) ((float)jRouteCheckPoint.getNumberStop() / (float)jRouteCheckPoint.getNumberPassages()));
			}
			if(!sameCheckPoint){
				idList.add(pathCheckPoint.getId());
			}
		}

		for(int id: idList){
			for(FeaturePoint featurePoint: pathSegment.getCheckPointCandidateList()){
				if(featurePoint.getId() == id){
					//ちゃんと値渡しできているか注意！！
					jRoute.addCheckPointCandidateList(new FeaturePoint(featurePoint));
				}
			}
		}
		jRoute.addTravelTime(pathSegment.getTravelTime());
		return jRoute;
	}

	/**
	 * 指定した区間のGPS情報を取得するメソッド
	 * @param sTime
	 * @param eTime
	 * @param gpsList
	 * @return
	 */
	private List<Gps> getGpsPeriod(Timestamp sTime, Timestamp eTime, List<Gps> gpsList){
		List<Gps> resultGpsList = new ArrayList<Gps>();
		for(Gps gps: gpsList){
			if(sTime.getTime() <= gps.getTime().getTime() && gps.getTime().getTime() <= eTime.getTime()){
				resultGpsList.add(gps);
			}
		}
		return resultGpsList;
	}

	/**
	 * チェックポイント候補、乗り換えポイントを更新した時にマージしたIDを更新するメソッド
	 * @return
	 */
	private List<FeaturePoint> updateId(List<FeaturePoint> fpPointList, List<FeaturePoint> clusterList){
		for(FeaturePoint fpPoint: fpPointList){
			for(FeaturePoint clusterPoint: clusterList){
				List<Integer> checkPointIdList = clusterPoint.getIdList();
				for(int id: checkPointIdList){
					if(fpPoint.getId() == id){
						fpPoint.setId(clusterPoint.getId());
					}
				}
			}	
		}
		return fpPointList;
	}

	/**
	 * 特徴点のデータを更新するメソッド
	 * @param pathDataList
	 * @param checkPointCandidateList
	 * @param trashipmentPointList
	 * @return
	 */
	private List<Path> updateFeaturePointLatLng(List<Path> pathDataList, List<FeaturePoint> checkPointCandidateList, List<FeaturePoint> trashipmentPointList){
		for(Path path: pathDataList){
			for(PathSegment pathSegment: path.getPathSegmentList()){
				//チェックポイントの緯度経度情報をIDを元に更新
				for(int i=0; i<pathSegment.getCheckPointCandidateList().size(); i++){
					for(FeaturePoint checkPointCandidate : checkPointCandidateList){
						if(pathSegment.getCheckPointCandidateList().get(i).getId() == checkPointCandidate.getId()){
							pathSegment.getCheckPointCandidateList().get(i).setLat(checkPointCandidate.getLat());
							pathSegment.getCheckPointCandidateList().get(i).setLng(checkPointCandidate.getLng());
							pathSegment.getCheckPointCandidateList().get(i).setMaxLat(checkPointCandidate.getMaxLat());
							pathSegment.getCheckPointCandidateList().get(i).setMinLat(checkPointCandidate.getMinLat());
							pathSegment.getCheckPointCandidateList().get(i).setMaxLng(checkPointCandidate.getMaxLng());
							pathSegment.getCheckPointCandidateList().get(i).setMinLng(checkPointCandidate.getMinLng());
						}
					}
				}
			}
		}
		return pathDataList;
	}

	/**
	 * 乗り換えポイントチェックポイント候補にユニークIDを与えるメソッド
	 * @param featurePointList
	 * @param routeId
	 * @return
	 */
	private List<FeaturePoint> giveUniqueId(List<FeaturePoint> featurePointList, int pathId, int checkpointNumber){
		for(int i=0; i<featurePointList.size(); i++){
			featurePointList.get(i).setId(pathId*10000 + checkpointNumber + i);
		}
		return featurePointList;
	}
}

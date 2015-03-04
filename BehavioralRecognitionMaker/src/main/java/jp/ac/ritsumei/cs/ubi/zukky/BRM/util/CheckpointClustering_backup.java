package jp.ac.ritsumei.cs.ubi.zukky.BRM.util;
//package jp.ac.ritsumei.cs.ubi.zukky.checkpointModule.util;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import jp.ac.ritsumei.cs.ubi.zukky.checkpointModule.kubiwa.Gps;
//
///**
// * 同一滞在地のGPSをクラスタリングするクラス
// * @author zukky
// *
// */
public class CheckpointClustering_backup {
//
//	private final static int INITIAL_DISTANCE = 10000;
//	private final static int DISTANCE_THRESHOLD = 10;
//	private final static Long STAYING_TIME_THRESHOLD = (long) 9;
//	private final static int CHECKPOINT_DISTANCE_THRESHOLD = 10;
//
//	/**
//	 * 重心法を用いてGPSデータをクラスタリングするメソッド
//	 * @param gpsList クラスタリングしたいGPSのリスト
//	 * @return cehckpointList （クラスタリングしたGPS = チェックポイント）のリスト
//	 */
//	public List<Checkpoint> clusterGpsByCentroidMethod(List<Gps> gpsList){
//		List<Checkpoint> checkpointList = new ArrayList<Checkpoint>();
//		List<ClusterGPS> clusterList = new ArrayList<ClusterGPS>();
//
//		double minDistance = INITIAL_DISTANCE;
//		double hubenyDistance = INITIAL_DISTANCE;
//		int minIndex1 = 0;
//		int minIndex2 = 0;
//		double centroidLat = 0;
//		double centroidLng = 0;
//
//		for(int i=0; i<gpsList.size(); i++){
//			Gps gps = gpsList.get(i);
//			ClusterGPS cluster = new ClusterGPS(gps);
//			cluster.addIndexList(i);
//			clusterList.add(cluster);
//		}
//		
//
//		/* 2点間の距離が一定以下になるまでクラスタリングを続ける */
//		do{	
//			//最も近い２点をクラスタとしてまとめる
//			if(minDistance != INITIAL_DISTANCE){
//				
//				ClusterGPS cluster = new ClusterGPS();
//
//				//新しく作成したクラスタに重心をとった緯度経度を入れる
//				centroidLat = (clusterList.get(minIndex1).getLat() + clusterList.get(minIndex2).getLat()) / 2;
//				centroidLng = (clusterList.get(minIndex1).getLng() + clusterList.get(minIndex2).getLng()) / 2;
//
//				cluster.setLat(centroidLat);
//				cluster.setLng(centroidLng);
//
//				//クラスタに含まれているGPSListのインデックスを統合する
//				List<Integer> indexList1 = clusterList.get(minIndex1).getIndexList();
//				List<Integer> indexList2 = clusterList.get(minIndex2).getIndexList();
//
//				
//				//indexリストを昇順に挿入
//				
//				for(int i: indexList1){
//					if(cluster.getIndexList().size() == 0){
//						cluster.addIndexList(i);
//					}else{
//						for(int j=0; j<cluster.getIndexList().size(); j++){
//							
//							if(i < cluster.getIndexList().get(j)){
//								cluster.addIndexList(j, i);
//								break;
//							}
//							if(j == cluster.getIndexList().size() -1){
//								cluster.addIndexList(i);
//								break;
//							}
//						}
//					}
//				}
//				
//				for(int i: indexList2){
//					if(cluster.getIndexList().size() == 0){
//						cluster.addIndexList(i);
//					}
//					for(int j=0; j<cluster.getIndexList().size(); j++){
//						if(i < cluster.getIndexList().get(j)){
//							cluster.addIndexList(j, i);
//							break;
//						}
//						if(j == cluster.getIndexList().size() -1){
//							cluster.addIndexList(i);
//							break;
//						}
//					}
//				}
//	
//				//統合したクラスタの元のクラスタを削除
//				clusterList.remove(minIndex1);
//				clusterList.remove(minIndex2 - 1);
//
//				clusterList.add(cluster);
//				
//				for(ClusterGPS c: clusterList){
//					//System.out.println("c is " + c.getIndexList());
//				}
//			}
//
//			minDistance = INITIAL_DISTANCE;
//
//			for(int i=0; i<clusterList.size()-1; i++){
//				for(int j=i+1; j<clusterList.size(); j++){
//					hubenyDistance = HubenyVelocityCalculator.calcDistHubeny(
//							clusterList.get(i).getLat(), 
//							clusterList.get(i).getLng(), 
//							clusterList.get(j).getLat(), 
//							clusterList.get(j).getLng());
//
//					if(hubenyDistance < minDistance){
//						minDistance = hubenyDistance;
//						minIndex1 = i;
//						minIndex2 = j;
//					}
//				}
//			}
//		}while(minDistance < DISTANCE_THRESHOLD);
//
//		//クラスタリングが終了したら残ったクラスタをチェックポイントとして抽出
//		for(ClusterGPS cluster: clusterList){
//			Checkpoint checkpoint = new Checkpoint();
//			checkpoint.setLat(cluster.getLat());
//			checkpoint.setLng(cluster.getLng());
//
//			Long stayingTime = calculateStayingTime(cluster, gpsList);
//
//			//System.out.println("stayingTime " + stayingTime);
//			checkpoint.addStayingTime(stayingTime);
//			if(stayingTime > STAYING_TIME_THRESHOLD){
//				checkpointList.add(checkpoint);
//			}
//		}
//
//		return checkpointList;
//	}
//
//	Long calculateStayingTime(ClusterGPS cluster, List<Gps> gpsList){
//
//		int startIndex = cluster.getIndexList().get(0);
//		int lastIndex = cluster.getIndexList().get(cluster.getIndexList().size()-1);
//
//		Long diffMilliSec = gpsList.get(lastIndex).getTime().getTime() - gpsList.get(startIndex).getTime().getTime();
//
//		/* ミリ秒を秒に変換 */
//		Long diffSec = diffMilliSec / 1000;
//
//		return diffSec;
//	}
//	
//	/**
//	 * 2つのチェックポイントのリストの中で、同一のチェックポイントをまとめるメソッド
//	 * TODO: クラスタリングの実装は要見直しの必要あり！！
//	 * @param beforeCluster
//	 * @param nextCluster
//	 * @return 
//	 */
//	List<Checkpoint> clusterCheckpoint(List<Checkpoint> beforeClusterList, List<Checkpoint> nextClusterList){
//		List<Checkpoint> resultClusterList = new ArrayList<Checkpoint>();
//		List<Integer> deleteListBeforeCluster = new ArrayList<Integer>();
//		List<Integer> deleteListNextCluster = new ArrayList<Integer>();
//		double centroidLat = 0;
//		double centroidLng = 0;
//		boolean deleteFlug = false;
//
//		double hubenyDistance;
//		
//		for(int i=0; i<beforeClusterList.size(); i++){
//			Checkpoint beforeCluster = beforeClusterList.get(i);
//			for(int j=0; j<nextClusterList.size(); j++){
//				Checkpoint nextCluster = nextClusterList.get(j);
//				hubenyDistance = HubenyVelocityCalculator.calcDistHubeny(
//						beforeCluster.getLat(), beforeCluster.getLng(), 
//						nextCluster.getLat(), nextCluster.getLng());
//				
//				//System.out.println("hubenyDistance " + hubenyDistance);
//				
//				
//				if(hubenyDistance < CHECKPOINT_DISTANCE_THRESHOLD){
//					Checkpoint checkpoint = new Checkpoint();
//					
//					centroidLat = (beforeCluster.getLat() + nextCluster.getLat()) / 2;
//					centroidLng = (beforeCluster.getLng() + nextCluster.getLng()) / 2;
//					
//					checkpoint.setLat(centroidLat);
//					checkpoint.setLng(centroidLng);
//					
//					for(Long stayingTime: beforeCluster.getStayingTime()){
//						checkpoint.addStayingTime(stayingTime);	
//					}
//					
//					for(Long stayingTime: nextCluster.getStayingTime()){
//						checkpoint.addStayingTime(stayingTime);	
//					}
//							
//					resultClusterList.add(checkpoint);
//					deleteListBeforeCluster.add(i);
//					deleteListNextCluster.add(j);
//				}
//			}
//		}
//		
//		
//		for(int i=0; i<beforeClusterList.size(); i++){
//			deleteFlug = false;
//			Checkpoint checkpoint = beforeClusterList.get(i);
//			for(int j:deleteListBeforeCluster){
//				if(i == j){
//					deleteFlug = true;
//				}
//			}
//			
//			if(!deleteFlug){
//				resultClusterList.add(checkpoint);
//			}
//		}
//		
//		for(int i=0; i<nextClusterList.size(); i++){
//			deleteFlug = false;
//			Checkpoint checkpoint = nextClusterList.get(i);
//			
//			for(int j:deleteListNextCluster){
//				if(i == j){
//					deleteFlug = true;
//				}
//			}
//			
//			if(!deleteFlug){
//				resultClusterList.add(checkpoint);
//			}
//		}
//		
//		System.out.println("deleteListBeforeCluster" + deleteListBeforeCluster.size());
//		System.out.println("deleteListNextCluster" + deleteListNextCluster.size());
//		System.out.println("beforeClusterList" + beforeClusterList.size());
//		System.out.println("nextClusterList" + nextClusterList.size());
//
//
//		
//		System.out.println("resultClusterList.size() " + resultClusterList.size());
//		return resultClusterList;
//	}
//
//	
//
}

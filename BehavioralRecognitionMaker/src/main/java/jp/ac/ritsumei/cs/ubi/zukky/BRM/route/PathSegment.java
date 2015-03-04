package jp.ac.ritsumei.cs.ubi.zukky.BRM.route;

import java.util.List;

import jp.ac.ritsumei.cs.ubi.zukky.BRM.featurePoint.FeaturePoint;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.transport.Transportation.TransportationType;



/**
 * 乗り換えポイント間の移動情報を保持するクラス
 * @author zukky
 *
 */
public class PathSegment {
	
	private int pathID;
	private int segmentID;
	private int routeID;
	private TransportationType type;
	private List<FeaturePoint> checkPointcandidateList;
	private FeaturePoint tsStartPoint;
	private FeaturePoint tsEndPoint;
	private long travelTime = 0;
	
	public FeaturePoint getTsStartPoint() {
		return tsStartPoint;
	}
	public void setTsStartPoint(FeaturePoint tsStartPoint) {
		this.tsStartPoint = tsStartPoint;
	}
	public FeaturePoint getTsEndPoint() {
		return tsEndPoint;
	}
	public void setTsEndPoint(FeaturePoint tsEndPoint) {
		this.tsEndPoint = tsEndPoint;
	}
	public TransportationType getType() {
		return type;
	}
	public void setType(TransportationType type) {
		this.type = type;
	}
	public int getPathID() {
		return pathID;
	}
	public void setPathID(int pathID) {
		this.pathID = pathID;
	}
	public int getSegmentID() {
		return segmentID;
	}
	public void setSegmentID(int segmentID) {
		this.segmentID = segmentID;
	}
	public List<FeaturePoint> getCheckPointCandidateList() {
		return checkPointcandidateList;
	}
	public void setCheckPointCandidateList(List<FeaturePoint> checkPointCandidateList) {
		this.checkPointcandidateList = checkPointCandidateList;
	}
	public void add(FeaturePoint checkPointCandidate){
		this.checkPointcandidateList.add(checkPointCandidate);
	}
	public int getRouteID() {
		return routeID;
	}
	public void setRouteID(int routeID) {
		this.routeID = routeID;
	}
	public long getTravelTime() {
		return travelTime;
	}
	public void setTravelTime(long travelTime) {
		this.travelTime = travelTime;
	}
}

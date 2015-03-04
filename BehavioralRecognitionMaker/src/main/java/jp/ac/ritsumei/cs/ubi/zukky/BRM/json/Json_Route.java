package jp.ac.ritsumei.cs.ubi.zukky.BRM.json;

import java.util.ArrayList;
import java.util.List;

import jp.ac.ritsumei.cs.ubi.zukky.BRM.featurePoint.FeaturePoint;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.route.PathSegment;

public class Json_Route {
	
	private int jRouteId;
	private FeaturePoint tsStartPoint;
	private FeaturePoint tsEndPoint;
	private List<FeaturePoint> checkPointCandidateList = new ArrayList<FeaturePoint>();
	private List<Long> travelTimes = null;
	
	
	public Json_Route(PathSegment pathSegment){
		this.tsStartPoint = pathSegment.getTsStartPoint();
		this.tsEndPoint = pathSegment.getTsEndPoint();
		this.checkPointCandidateList = pathSegment.getCheckPointCandidateList();
		long travelTime = pathSegment.getTravelTime();
		this.travelTimes = new ArrayList<Long>();
		travelTimes.add(travelTime);
	}
	
	public void addTravelTime(long travelTime){
		if(travelTimes != null){
			travelTimes.add(travelTime);
		}
	}
	
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
	public List<FeaturePoint> getCheckPointCandidateList() {
		return checkPointCandidateList;
	}
	public void setCheckPointCandidateList(List<FeaturePoint> checkPointCandidateList) {
		this.checkPointCandidateList = checkPointCandidateList;
	}
	public void addCheckPointCandidateList(FeaturePoint checkPointCandidate){
		this.checkPointCandidateList.add(checkPointCandidate);
	}
	public int getjRouteId() {
		return jRouteId;
	}
	public void setjRouteId(int jRouteId) {
		this.jRouteId = jRouteId;
	}

	public List<Long> getTravelTimes() {
		return travelTimes;
	}
}

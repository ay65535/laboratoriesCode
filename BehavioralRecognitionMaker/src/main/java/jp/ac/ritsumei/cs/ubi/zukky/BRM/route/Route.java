package jp.ac.ritsumei.cs.ubi.zukky.BRM.route;

import java.util.ArrayList;
import java.util.List;

import jp.ac.ritsumei.cs.ubi.zukky.BRM.featurePoint.FeaturePoint;

public class Route {

	private int pathId;
	private int segmentId;
	private List<FeaturePoint> checkPointList = new ArrayList<FeaturePoint>();
//	private FeaturePoint tsStartPoint;
//	private FeaturePoint tsEndPoint;
	
	public int getPathId() {
		return pathId;
	}
	public void setPathId(int pathId) {
		this.pathId = pathId;
	}
	public int getSegmentId() {
		return segmentId;
	}
	public void setSegmentId(int segmentId) {
		this.segmentId = segmentId;
	}
	public List<FeaturePoint> getCheckPointList() {
		return checkPointList;
	}
	public void setCheckPointList(List<FeaturePoint> checkPointList) {
		this.checkPointList = checkPointList;
	}
	
}

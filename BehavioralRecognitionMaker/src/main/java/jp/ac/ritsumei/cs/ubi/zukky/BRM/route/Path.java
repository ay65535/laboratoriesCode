package jp.ac.ritsumei.cs.ubi.zukky.BRM.route;

import java.util.ArrayList;
import java.util.List;

import jp.ac.ritsumei.cs.ubi.zukky.BRM.featurePoint.FeaturePoint;
import jp.ac.ritsumei.cs.ubi.zukky.BRM.transport.Transportation;

/**
 * 1回の移動情報を格納するクラス
 * @author zukky
 *
 */
public class Path {

	private int pathID;
	private List<Transportation> transportationList = new ArrayList<Transportation>();
	private List<PathSegment> pathSegmentList = new ArrayList<PathSegment>();
	private FeaturePoint startPoint;
	private FeaturePoint endPoint;
	
	public FeaturePoint getStartPoint() {
		return startPoint;
	}
	public void setStartPoint(FeaturePoint startPoint) {
		this.startPoint = startPoint;
	}
	public FeaturePoint getEndPoint() {
		return endPoint;
	}
	public void setEndPoint(FeaturePoint endPoint) {
		this.endPoint = endPoint;
	}
	public int getPathID() {
		return pathID;
	}
	public void setPathID(int pathID) {
		this.pathID = pathID;
	}
	public List<Transportation> getTransportationList() {
		return transportationList;
	}
	public void setTransportationList(List<Transportation> transportationList) {
		this.transportationList = transportationList;
	}
	public void addTransportationList(Transportation transportation){
		this.transportationList.add(transportation);
	}
	public List<PathSegment> getPathSegmentList() {
		return pathSegmentList;
	}
	public void setPathSegmentList(List<PathSegment> pathSegmentList) {
		this.pathSegmentList = pathSegmentList;
	}
	public void addPathSegmentList(PathSegment PathSegment){
		this.pathSegmentList.add(PathSegment);
	}
	
}

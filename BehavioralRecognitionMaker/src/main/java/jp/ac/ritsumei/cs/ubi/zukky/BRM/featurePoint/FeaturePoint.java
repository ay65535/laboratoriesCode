package jp.ac.ritsumei.cs.ubi.zukky.BRM.featurePoint;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import jp.ac.ritsumei.cs.ubi.zukky.BRM.kubiwa.Gps;

/**
 * 乗換えポイント、チェックポイント候補などをまとめて管理するクラス
 * @author zukky
 */
public class FeaturePoint extends Gps {

	public static final double INITIAL_DEGREE = 10000000;
	public static final int INITIAL_ID = 1000000000;

	public enum Type{
		TRANSHIPMENT_POINT, CHECKPOINT_CANDIDATE
	}

	public enum DetailedType{
		BUS_STOP, STATION, CYCLE_PARKING, TRAFFIC_LIGHT, UNKNOWN, 
	}

	//private List<Type> TypeList = new ArrayList<Type>();
	private Type type;
	private DetailedType detailedType;
	private int id = INITIAL_ID;
	private double lat;
	private double lng;
	private double maxLat = INITIAL_DEGREE;
	private double minLat = INITIAL_DEGREE;
	private double maxLng = INITIAL_DEGREE;
	private double minLng = INITIAL_DEGREE;

	private List<Long> stayTimeList = new ArrayList<Long>();
	private long stayingTime = 0;
	private long averageStayingTime = 0;
	private long sumStayingTime = 0;
	private float probability;
	private int numberPassages = 0;
	private int numberStop = 0;

	private List<Long> stayingTimeList = new ArrayList<Long>();
	private Map<Integer, Long> stayingTimeMap = new HashMap<Integer, Long>();

	//arrivalTime と　departureTime もMap型に
	private Timestamp arrivalTime;
	private Timestamp departureTime;
	private List<Integer> idList = new ArrayList<Integer>();


	public FeaturePoint(Gps gps) {
		this.setAcc(gps.getAcc());
		this.setDevId(gps.getDevId());
		this.setLat(gps.getLat());
		this.setLng(gps.getLng());
		this.setlTime(gps.getlTime());
		this.setSpeed(gps.getSpeed());
		this.setTime(gps.getTime());
		this.setArrivalTime(gps.getTime());
		this.setDepartureTime(gps.getTime());
		this.stayingTimeList.add(gps.getlTime().getTime());
	} 

	public FeaturePoint(FeaturePoint featurePoint){
		this.setId(featurePoint.getId());
		this.setAcc(featurePoint.getAcc());
		this.setDevId(featurePoint.getDevId());
		this.setLat(featurePoint.getLat());
		this.setLng(featurePoint.getLng());
		this.setlTime(featurePoint.getlTime());
		this.setSpeed(featurePoint.getSpeed());
		this.setTime(featurePoint.getTime());
		this.setArrivalTime(getTime());
		this.setDepartureTime(getTime());
		this.setStayingTime(featurePoint.getStayingTime());
		this.setSumStayingTime(featurePoint.getSumStayingTime());
		this.setAverageStayingTime(featurePoint.getSumStayingTime());
		this.setProbability(featurePoint.getProbability());
		this.setNumberPassages(featurePoint.getNumberPassages());
		this.setNumberStop(featurePoint.getNumberStop());
		this.stayingTimeList.add(featurePoint.getlTime().getTime());
	}

	public FeaturePoint(){

	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public double getMaxLat() {
		return maxLat;
	}
	public void setMaxLat(double maxLat) {
		this.maxLat = maxLat;
	}
	public double getMinLat() {
		return minLat;
	}
	public void setMinLat(double minLat) {
		this.minLat = minLat;
	}
	public double getMaxLng() {
		return maxLng;
	}
	public void setMaxLng(double maxLng) {
		this.maxLng = maxLng;
	}
	public double getMinLng() {
		return minLng;
	}
	public void setMinLng(double minLng) {
		this.minLng = minLng;
	}
	public Timestamp getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(Timestamp arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public Timestamp getDepartureTime() {
		return departureTime;
	}
	public void setDepartureTime(Timestamp departureTime) {
		this.departureTime = departureTime;
	}
	public DetailedType getDetailedType() {
		return detailedType;
	}
	public void setDetailedType(DetailedType detailedType) {
		this.detailedType = detailedType;
	}

	/**
	 * 矩形を囲むための最大値と最小値をセットするメソッド
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 */
	public void setMinMaxLatLng(double lat1, double lng1, double lat2, double lng2){
		if(lat1 <= lat2){
			minLat = lat1;
			maxLat = lat2;
		}else{
			minLat = lat2;
			maxLat = lat1;
		}

		if(lng1 <= lng2){
			minLng = lng1;
			maxLng = lng2;
		}else{
			minLng = lng2;
			maxLng = lng1;
		}
	}

	/**
	 * 矩形を囲むための最大値と最小値をセットするメソッド
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 */
	public void setMinMaxLatLng(Gps gps1, Gps gps2){

		if(gps1.getLat() <= gps2.getLat()){
			minLat = gps1.getLat();
			maxLat = gps2.getLat();
		}else{
			minLat = gps2.getLat();
			maxLat = gps1.getLat();
		}

		if(gps1.getLng() <= gps2.getLng()){
			minLng = gps1.getLng();
			maxLng = gps2.getLng();
		}else{
			minLng = gps2.getLng();
			maxLng = gps1.getLng();
		}
	}

	public Map<Integer, Long> getStayingTimeMap() {
		return stayingTimeMap;
	}

	public void setStayingTimeMap(Map<Integer, Long> stayingTimeMap) {
		this.stayingTimeMap = stayingTimeMap;
	}
	public void putStayingTimeMap(int routeId, long stayingTime){
		this.stayingTimeMap.put(routeId, stayingTime);
	}

	public List<Integer> getIdList() {
		return idList;
	}

	public void setIdList(List<Integer> IdList) {
		this.idList = IdList;
	}
	public void addIdList(int routeId){
		this.idList.add(routeId);
	}

	public long getStayingTime() {
		return stayingTime;
	}

	public void setStayingTime(long stayingTime) {
		this.stayingTime = stayingTime;
	}

	public List<Long> getStayingTimeList() {
		return stayingTimeList;
	}

	public void setStayingTimeList(List<Long> stayingTimeList) {
		this.stayingTimeList = stayingTimeList;
	}
	public void addStayingTime(long stayingTime){
		this.stayingTimeList.add(stayingTime);
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public long getAverageStayingTime() {
		return averageStayingTime;
	}

	public void setAverageStayingTime(long averageStayingTime) {
		this.averageStayingTime = averageStayingTime;
	}

	public long getSumStayingTime() {
		return sumStayingTime;
	}

	public void setSumStayingTime(long sumStayingTime) {
		this.sumStayingTime = sumStayingTime;
	}

	public int getNumberPassages() {
		return numberPassages;
	}

	public void setNumberPassages(int numberPassages) {
		this.numberPassages = numberPassages;
	}

	public int getNumberStop() {
		return numberStop;
	}

	public void setNumberStop(int numberStop) {
		this.numberStop = numberStop;
	}
	
	public float getProbability() {
		return probability;
	}

	public void setProbability(float probability) {
		this.probability = probability;
	}
	
	public void addStayTimeList(List<Long> times){
		if(stayingTimeList == null){
			stayingTimeList = new ArrayList<Long>();
		}else{
			stayingTimeList.addAll(times);
		}
	}

	public List<Long> getStayTimeList() {
		return stayTimeList;
	}
}
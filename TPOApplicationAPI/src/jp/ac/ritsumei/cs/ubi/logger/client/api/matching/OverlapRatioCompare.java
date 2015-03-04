/*
 * Copyright (C) 2008-2012 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.logger.client.api.matching;

import java.util.LinkedList;
import java.util.Map;

import jp.ac.ritsumei.cs.ubi.logger.client.api.sensors.SensorData;
import jp.ac.ritsumei.cs.ubi.logger.client.api.utility.Bytes;
import jp.ac.ritsumei.cs.ubi.logger.client.api.utility.MatchingConstants;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * This class detect overlap ratio event.
 * @author sacchin
 */
public class OverlapRatioCompare extends CompareOperation {
	protected static final String LOG_TAG = "OverlapRatioCompare";
	protected final int operationType;
	protected final int dataType;
	protected final int calculater;
	protected final int sensorName;
	protected final int valueName;
	protected final int attributeName;
	protected final float floatConstant;
	
	protected static final long SMOOTING_INTERVAL = 1000*60;	
	protected Map<String,Integer> passedLog;
	protected LinkedList<Float> overlapRatioQueue;
	protected LinkedList<Long> timeQueue;
	
	/**
	 * This is constructor.
	 * @param dataType MatchingConstants.DT_FLOAT
	 * @param calculater equal or smaller or larger
	 * @param constant (float)
	 */
	public OverlapRatioCompare(int dataType, int calculater, float constant){
		this(MatchingConstants.COMPARE, dataType, MatchingConstants.SN_WiFi, 
				MatchingConstants.NO_PARAM, MatchingConstants.AN_OVERLAP_RATIO, calculater, constant);
		overlapRatioQueue = new LinkedList<Float>();
		timeQueue = new LinkedList<Long>();
	}
	
	/**
	 * This is constructor.
	 * @param operationType Compare or Logic
	 * @param dataType MatchingConstants.DT_FLOAT
	 * @param sensorName means which sensor is used. 
	 * @param valueName means which value is used.
	 * @param attributeName means which attribute is used
	 * @param calculater equal or smaller or larger
	 * @param constant (float)
	 */
	public OverlapRatioCompare(int operationType, int dataType, int sensorName, 
			int valueName, int attributeName, int calculater, float constant){
		super();
		this.operationType = operationType;
		this.dataType = dataType;
		this.calculater = calculater;
		this.sensorName = sensorName;
		this.valueName = valueName;
		this.floatConstant = constant;
		this.attributeName = attributeName;
	}
	
	/**
	 * This is constructor.
	 * The values of field is read from in, because this class extends Parcelable.
	 * @param in
	 */
	public OverlapRatioCompare( Parcel in ) {
		super();
		int parameters[] = in.createIntArray();
		if( parameters!=null && parameters.length!=0 ){
			this.operationType = parameters[0];
			this.dataType = parameters[1];
			this.calculater = parameters[2];
			this.sensorName = parameters[3];
			this.valueName = parameters[4];
			this.attributeName = parameters[5];
		}else{
			this.operationType = -1;
			this.dataType = -1;
			this.calculater = -1;
			this.sensorName = -1;
			this.valueName = -1;
			this.attributeName = -1;
			Log.e(LOG_TAG, "parameters are null or 0!!");
		}
		this.floatConstant = in.readFloat();
		overlapRatioQueue = new LinkedList<Float>();
		timeQueue = new LinkedList<Long>();
	}
	
	/**
	 * return latest result.
	 */
	@Override
	public boolean evaluate() {
		return latestResult;
	}
	
	/**
	 * Smoothing sensor value(WiFi.OverlapRatio.NoParam) is compared constant(float) with calculater.
	 */
	@Override
	public boolean evaluate(SensorData latest){
    	addQueue(latest);
    	removeOverTime();
    	float overlapRatio = 0;
    	byte latestLog[] = latest.getSensorDate(getKey());
    	if( latestLog!=null ){
    		overlapRatio = Bytes.toFloat(latestLog);
        	Log.v( LOG_TAG + ",Interval", this.toString() + " | " + getKey() + " = " + overlapRatio);
    	}else{
    		overlapRatio = smoothingOverlapRatio();
        	Log.v( LOG_TAG, this.toString() + " | " + getKey() + " = " + overlapRatio);
    	}
    	if( MatchingConstants.LARGER_THAN == calculater ){
    		latestResult = overlapRatio > floatConstant;
    		return latestResult;
    	}else if( MatchingConstants.SMALLER_THAN == calculater ){
    		latestResult = overlapRatio < floatConstant;
    		return latestResult;
    	}else if( MatchingConstants.EQUAL == calculater ){
    		latestResult = overlapRatio == floatConstant;
    		return latestResult;
    	}else{
    		Log.e(LOG_TAG, "calculater is not correct!!");
    		latestResult = false;
    		return false;
    	}
	}

	/**
	 * calculate OverlapRatio and add to overlapRatioQueue
	 * @param latest
	 */
	public void addQueue(SensorData latest) {
//		Map<String,Integer> latestLog = latest.getSSIDs();
//		Log.v(LOG_TAG, latestLog.keySet().toString());
//    	if( latestLog!=null && passedLog!=null ){
//        	double nowRatio = OverlapRatio.between( latestLog, passedLog );
//        	if( nowRatio!=-1 && nowRatio!=0 ){
//            	timeQueue.add( latest.getTime( MatchingConstants.PARAM_STRING[MatchingConstants.SN_WiFi] ) );
//            	overlapRatioQueue.add( (float)nowRatio );
//            	Float f = overlapRatioQueue.getLast();
//            	if( f!=null ){
//                	Log.v( LOG_TAG, "NowRatio=" + f.toString() + ",Size=" + overlapRatioQueue.size() );
//            	}
//        	}
//    	}
//    	passedLog = latestLog;
	}
	
	/**
	 * remove values over 60s
	 */
	public void removeOverTime(){
		long latest = timeQueue.getLast();
		while( !timeQueue.isEmpty() && overlapRatioQueue.isEmpty()  ){
			long temp = timeQueue.getFirst();
			if( latest - temp > SMOOTING_INTERVAL ){
				timeQueue.removeFirst();
				overlapRatioQueue.removeFirst();
			}else{
				break;
			}
		}
	}
	
	/**
	 * smoothing float values in overlapRatioQueue.
	 * Time Interval = 60s
	 * @return smoothing value
	 */
	public float smoothingOverlapRatio(){
		if( overlapRatioQueue!=null ){
			float sum = 0;
			int count = 0;
			for( float temp : overlapRatioQueue ){
				sum += temp;
				count++;
			}
			return sum/(float)count;
		}else{
			return 0;
		}
	}

	/**
	 * for Parcelable
	 */
	public int describeContents() {
		return 0;
	}
	
	/**
	 * for Parcelable
	 */
    public static final Parcelable.Creator<OverlapRatioCompare> CREATOR = new Parcelable.Creator<OverlapRatioCompare>(){
        public OverlapRatioCompare createFromParcel(Parcel in) {
            return new OverlapRatioCompare(in);
        }
        public OverlapRatioCompare[] newArray(int size) {
            return new OverlapRatioCompare[size];
        }
    };
	
	/**
	 * for parcelable
	 * write values of field
	 */
	public void writeToParcel(Parcel out, int flags) {		
		int parameters[] = new int[6];
		parameters[0] = operationType;
		parameters[1] = dataType;
		parameters[2] = calculater;
		parameters[3] = sensorName;
		parameters[4] = valueName;
		parameters[5] = attributeName;
		out.writeIntArray(parameters);
		out.writeFloat(floatConstant);
	}

	/**
	 * return "sensorName.valueName.attributeName"
	 */
	@Override
	public String getKey() {
		return MatchingConstants.PARAM_STRING[sensorName] + "." + 
		MatchingConstants.PARAM_STRING[valueName] + "." + 
		MatchingConstants.PARAM_STRING[attributeName];
	}

	@Override
	public String toString(){
		String returnString = 
			"{" + MatchingConstants.PARAM_STRING[operationType] +
			":(" + MatchingConstants.PARAM_STRING[dataType] + ")" +
			getKey() + " " +
			MatchingConstants.PARAM_STRING[calculater] + " " + String.valueOf(floatConstant) + "}";
		return returnString;
	}
	
	/**
	 * @return constant(float)
	 */
	public float getFloatConstant(){
		return floatConstant;
	}

	/**
	 * return calculater
	 */
	@Override
	public int getCalculater() {
		return calculater;
	}

	/**
	 * return attribute
	 */
	@Override
	public int getValueName() {
		return valueName;
	}
	
	/**
	 * return dataType
	 */
	@Override
	public int getDataType() {
		return dataType;
	}
	
	/**
	 * return sensorName
	 */
	@Override
	public int getSensorName() {
		return sensorName;
	}
	
	/**
	 * return attributeName
	 */
	@Override
	public int getAttributeName() {
		return attributeName;
	}
}

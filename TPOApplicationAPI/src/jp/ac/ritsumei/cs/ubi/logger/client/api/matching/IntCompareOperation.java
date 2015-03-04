/*
 * Copyright (C) 2008-2012 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.logger.client.api.matching;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import jp.ac.ritsumei.cs.ubi.logger.client.api.sensors.SensorData;
import jp.ac.ritsumei.cs.ubi.logger.client.api.utility.Bytes;
import jp.ac.ritsumei.cs.ubi.logger.client.api.utility.MatchingConstants;

/**
 * This class detect int event.
 * @author sacchin
 */
public class IntCompareOperation extends CompareOperation implements Parcelable{
	protected static final String LOG_TAG = "IntCompareOperation";
	protected final int operationType;
	protected final int dataType;
	protected final int calculater;
	protected final int sensorName;
	protected final int valueName;
	protected final int attributeName;
	protected final int intConstant;
	
	protected int passedLog = 0;
	/**
	 * This is constructor.
	 * @param sensorName means which sensor is used. 
	 * @param valueName means which value is used.
	 * @param attributeName means which attribute is used
	 * @param calculater equal or smaller or larger
	 * @param constant (int)
	 */
	public IntCompareOperation(int sensorName, int valueName, int attributeName, int calculater, int constant){
		this(MatchingConstants.COMPARE, MatchingConstants.DT_INT, 
				sensorName, valueName, attributeName, calculater, constant);
	}
	
	/**
	 * This is constructor.
	 * @param operationType Compare or Logic
	 * @param dataType MatchingConstants.DT_INT
	 * @param sensorName means which sensor is used. 
	 * @param valueName means which value is used.
	 * @param attributeName means which attribute is used
	 * @param calculater equal or smaller or larger
	 * @param constant (int)
	 */
	public IntCompareOperation(int operationType, int dataType, int sensorName,
			int valueName, int attributeName, int calculater, int constant){
		super();
		this.operationType = operationType;
		this.dataType = dataType;
		this.calculater = calculater;
		this.sensorName = sensorName;
		this.valueName = valueName;
		this.intConstant = constant;
		this.attributeName = attributeName;
	}
	
	/**
	 * This is constructor.
	 * The values of field is read from in, because this class extends Parcelable.
	 * @param in
	 */
	public IntCompareOperation(Parcel in) {
		super();
		int parameters[] = in.createIntArray();		
		if( parameters!=null && parameters.length!=0 ){
			this.operationType = parameters[0];
			this.dataType = parameters[1];
			this.calculater = parameters[2];
			this.sensorName = parameters[3];
			this.valueName = parameters[4];
			this.intConstant = parameters[6];
			this.attributeName = parameters[7];
			this.numberOfDetection = parameters[8];
		}else{
			throw new NullPointerException("parameters[] is null.");
		}
	}
	
	/**
	 * return latest result.
	 */
	@Override
	public boolean evaluate() {
		return latestResult;
	}
	
	/**
	 * Sensor value(sensorName.valueName.attributeName) is compared constant(int) with calculater.
	 */
	@Override
	public boolean evaluate(SensorData latest){
    	int sensorValue = getSensorValue( latest );
    	if(MatchingConstants.AN_VARIATION == attributeName){
    		sensorValue =  Math.abs(passedLog - sensorValue);
    	}
    	passedLog = getSensorValue( latest );
//    	Log.v( LOG_TAG, this.toString() + " | " + getKey() + " = " + sensorValue);
    	if( MatchingConstants.LARGER_THAN == calculater ){
    		latestResult = sensorValue > intConstant;
    		return latestResult;
    	}else if( MatchingConstants.SMALLER_THAN == calculater ){
    		latestResult = sensorValue < intConstant;
    		return latestResult;
    	}else if( MatchingConstants.EQUAL == calculater ){
    		latestResult = sensorValue == intConstant;
    		return latestResult;
    	}else{
    		Log.e(LOG_TAG, "calculater is not correct!!");
    		latestResult = false;
    		return false;
    	}
	}
	
	/**
	 * return int sensor value corresponding to "sensorName.valueName.attributeName"
	 * @param latest All sensor values
	 * @return sensor value
	 */
	public int getSensorValue(SensorData latest){
		String key = getKey();
		byte temp[] = latest.getSensorDate(key);
		if(temp!=null){
			return Bytes.toInt(temp);
		}
		return 0;
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
    public static final Parcelable.Creator<IntCompareOperation> CREATOR = new Parcelable.Creator<IntCompareOperation>(){
        public IntCompareOperation createFromParcel(Parcel in) {
            return new IntCompareOperation(in);
        }
        public IntCompareOperation[] newArray(int size) {
            return new IntCompareOperation[size];
        }
    };
	
	/**
	 * for parcelable
	 * write values of field
	 */
	public void writeToParcel(Parcel out, int flags) {
		int parameters[] = new int[9];
		parameters[0] = operationType;
		parameters[1] = dataType;
		parameters[2] = calculater;
		parameters[3] = sensorName;
		parameters[4] = valueName;
		parameters[6] = intConstant;
		parameters[7] = attributeName;
		parameters[8] = numberOfDetection;
		out.writeIntArray(parameters);
	}

	/**
	 * return "sensorName.valueName.attributeName"
	 */
	@Override
	public String getKey() {
		return MatchingConstants.PARAM_STRING[sensorName] + "." + 
		MatchingConstants.PARAM_STRING[valueName] + "." + 
		MatchingConstants.PARAM_STRING[MatchingConstants.NO_PARAM];
	}

	@Override
	public String toString(){
		String returnString = 
			"{" + MatchingConstants.PARAM_STRING[operationType] +
			":(" + MatchingConstants.PARAM_STRING[dataType] + ")" + getKey() + " " +
			MatchingConstants.PARAM_STRING[calculater] + " " + String.valueOf(intConstant) + "}";
		return returnString;
	}
	
	/**
	 * @return constant(int)
	 */
	public int getIntConstant(){
		return intConstant;
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

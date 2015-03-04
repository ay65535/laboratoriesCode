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
 * This class detect float event.
 * @author sacchin
 */
public class FloatCompareOperation extends CompareOperation implements Parcelable{
	protected static final String LOG_TAG = "FloatCompareOperation";
	protected final int operationType;
	protected final int dataType;
	protected final int calculater;
	protected final int sensorName;
	protected final int valueName;
	protected final int attributeName;
	protected final float floatConstant;
	
	protected float passedLog = 0;
	/**
	 * This is constructor.
	 * @param sensorName means which sensor is used. 
	 * @param valueName�@means which value is used.
	 * @param attributeName means which attribute is used
	 * @param calculater equal or smaller or larger
	 * @param constant (float)
	 */
	public FloatCompareOperation(int sensorName, int valueName, int attributeName, int calculater, float constant){
		this(MatchingConstants.COMPARE, MatchingConstants.DT_FLOAT, 
				sensorName, valueName, attributeName, calculater, constant);
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
	public FloatCompareOperation(int operationType, int dataType, int sensorName, 
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
	public FloatCompareOperation(Parcel in) {
		super();
		int parameters[] = in.createIntArray();
		if( parameters!=null && parameters.length!=0 ){
			this.operationType = parameters[0];
			this.dataType = parameters[1];
			this.calculater = parameters[2];
			this.sensorName = parameters[3];
			this.valueName = parameters[4];
			this.attributeName = parameters[5];
			this.numberOfDetection = parameters[6];
		}else{
			throw new NullPointerException("parameters[] is null.");
		}
		this.floatConstant = in.readFloat();
	}
	
	/**
	 * return latest result.
	 */
	@Override
	public boolean evaluate() {
		return latestResult;
	}
	
	/**
	 * Sensor value(sensorName.attribute.columQualifier) is compared constant(float) with calculater.
	 */
	@Override
	public boolean evaluate(SensorData latest){
    	float sensorValue = getSensorValue( latest );
    	if(MatchingConstants.AN_VARIATION == attributeName){
    		sensorValue =  Math.abs(passedLog - sensorValue);
    	}
    	passedLog = getSensorValue( latest );
//    	Log.v( LOG_TAG, this.toString() + " | " + getKey() + " = " + sensorValue);
    	if( MatchingConstants.LARGER_THAN == calculater ){
    		latestResult = sensorValue > floatConstant;
    		return latestResult;
    	}else if( MatchingConstants.SMALLER_THAN == calculater ){
    		latestResult = sensorValue < floatConstant;
    		return latestResult;
    	}else if( MatchingConstants.EQUAL == calculater ){
    		latestResult = sensorValue == floatConstant;
    		return latestResult;
    	}else{
    		Log.e(LOG_TAG, "calculater is not correct!!");
    		latestResult = false;
    		return false;
    	}
	}
	
	/**
	 * return float sensor value corresponding to "sensorName.valueName.attributeName"
	 * @param latest All sensor values
	 * @return sensor value
	 */
	public float getSensorValue(SensorData latest){
		String key = getKey();
		byte temp[] = latest.getSensorDate(key);
		if(temp!=null){
			return Bytes.toFloat(temp);
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
    public static final Parcelable.Creator<FloatCompareOperation> CREATOR = new Parcelable.Creator<FloatCompareOperation>(){
        public FloatCompareOperation createFromParcel(Parcel in) {
            return new FloatCompareOperation(in);
        }
        public FloatCompareOperation[] newArray(int size) {
            return new FloatCompareOperation[size];
        }
    };
	
	/**
	 * for parcelable
	 * write values of field
	 */
	public void writeToParcel(Parcel out, int flags) {		
		int parameters[] = new int[7];
		parameters[0] = operationType;
		parameters[1] = dataType;
		parameters[2] = calculater;
		parameters[3] = sensorName;
		parameters[4] = valueName;
		parameters[5] = attributeName;
		parameters[6] = numberOfDetection;
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
		MatchingConstants.PARAM_STRING[MatchingConstants.NO_PARAM];
	}

	@Override
	public String toString(){
		String returnString = 
			"{" + MatchingConstants.PARAM_STRING[operationType] +
			":(" + MatchingConstants.PARAM_STRING[dataType] + ")" + getKey() + " " +
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

/*
 * Copyright (C) 2008-2012 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.logger.client.api.matching;

import jp.ac.ritsumei.cs.ubi.logger.client.api.sensors.SensorData;
import jp.ac.ritsumei.cs.ubi.logger.client.api.utility.MatchingConstants;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class detect String event.
 * @author sacchin
 */
public class StringCompareOperation extends CompareOperation implements Parcelable {
	protected static final String LOG_TAG = "StringCompareOperation";
	protected final int operationType;
	protected final int dataType;
	protected final int calculater;
	protected final int sensorName;
	protected final int valueName;
	protected final int attributeName;
	protected final String stringConstant;
	
	/**
	 * This is constructor.
	 * @param sensorName means which sensor is used. 
	 * @param valueName means which value is used.
	 * @param attributeName means which attribute is used
	 * @param calculater equal or smaller or larger
	 * @param constant (String)
	 */
	public StringCompareOperation(int sensorName, int valueName, int attributeName, int calculater, String constant){
		this( MatchingConstants.COMPARE, MatchingConstants.DT_STRING, 
				sensorName, valueName, attributeName, calculater, constant);
	}
	
	/**
	 * This is constructor.
	 * @param operationType�@Compare or Logic
	 * @param dataType MatchingConstants.DT_STRING
	 * @param sensorName means which sensor is used. 
	 * @param valueName means which value is used.
	 * @param attributeName means which attribute is used
	 * @param calculater equal or smaller or larger
	 * @param constant (String)
	 */
	public StringCompareOperation(int operationType, int dataType, int sensorName,
			int valueName, int attributeName, int calculater, String constant ){
		super();
		this.operationType = operationType;
		this.dataType = dataType;
		this.calculater = calculater;
		this.sensorName = sensorName;
		this.valueName = valueName;
		this.stringConstant = constant;
		this.attributeName = attributeName;
	}
	
	/**
	 * This is constructor.
	 * The values of field is read from in, because this class extends Parcelable.
	 * @param in
	 */
	public StringCompareOperation( Parcel in ) {
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
		String temp = in.readString();
		this.stringConstant = temp;
	}
	
	/**
	 * return latest result.
	 */
	@Override
	public boolean evaluate() {
		return latestResult;
	}
	
	/**
	 * Sensor value(sensorName.attribute.columQualifier) is compared constant(String) with calculater.
	 */
	public boolean evaluate( SensorData latest ){
		return latestResult;
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
    public static final Parcelable.Creator<StringCompareOperation> CREATOR = new Parcelable.Creator<StringCompareOperation>(){
        public StringCompareOperation createFromParcel(Parcel in) {
            return new StringCompareOperation(in);
        }
        public StringCompareOperation[] newArray(int size) {
            return new StringCompareOperation[size];
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
		out.writeString(stringConstant);
	}

	/**
	 * return "sensorName.attribute.NoParam" or "sensorName.attribute.stringConstant"
	 */
	@Override
	public String getKey() {
		return MatchingConstants.PARAM_STRING[sensorName] + "." + 
		MatchingConstants.PARAM_STRING[valueName] + "." + stringConstant;
	}

	@Override
	public String toString(){
		String returnString = 
			"{" + MatchingConstants.PARAM_STRING[operationType] +
			":(" + MatchingConstants.PARAM_STRING[dataType] + ")" + getKey() + " " +
			MatchingConstants.PARAM_STRING[calculater] + " " + stringConstant + "}";
		return returnString;
	}
	
	/**
	 * @return constant(String)
	 */
	public String getStringConstant(){
		return stringConstant;
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
	
	public void setLatestResult(boolean result){
		this.latestResult = result;
	}
}

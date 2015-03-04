/*
 * Copyright (C) 2008-2012 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.logger.client.api.matching;

import jp.ac.ritsumei.cs.ubi.logger.client.api.sensors.SensorData;
import jp.ac.ritsumei.cs.ubi.logger.client.api.utility.Bytes;
import jp.ac.ritsumei.cs.ubi.logger.client.api.utility.MatchingConstants;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class detect whether user is in tne latlng Area.
 * @author sacchin
 */
public class LatLngCompareOperation extends CompareOperation implements Parcelable {
	protected static final String LOG_TAG = "LatLngCompareOperation";
	protected final int operationType;
	protected final int dataType;
	protected final int calculater;
	protected final int sensorName;
	protected final int valueName;
	protected final int attributeName;
	protected final double latlngConstant[];
	
	/**
	 * This is constructor.
	 * @param dataType int,float,double
	 * @param calculater equal or smaller or larger
	 * @param constant (double[4])
	 */
	public LatLngCompareOperation(int dataType, int calculater, double constant[]){
		this(MatchingConstants.COMPARE, dataType, MatchingConstants.SN_LOCATION, 
				MatchingConstants.VN_LAT_LNG, MatchingConstants.NO_PARAM, calculater, constant);
	}

	/**
	 * This is constructor.
	 * @param operationType Compare or Logic
	 * @param dataType MatchingConstants.DT_DOUBLE
	 * @param sensorName MatchingConstants.SN_LOCATION
	 * @param valueName MatchingConstants.VN_LAT_LNG
	 * @param attributeName to use database
	 * @param calculater equal or smaller or larger
	 * @param constant (double[4])
	 */
	public LatLngCompareOperation(int operationType, int dataType, int sensorName,
			int valueName, int attributeName, int calculater, double constant[]){
		super();
		this.operationType = operationType;
		this.dataType = dataType;
		this.calculater = calculater;
		this.sensorName = sensorName;
		this.valueName = valueName;
		this.attributeName = attributeName;
		this.latlngConstant = constant;
	}
	
	/**
	 * This is constructor.
	 * The values of field is read from in, because this class extends Parcelable.
	 * @param in
	 */
	public LatLngCompareOperation( Parcel in ) {
		super();
		int parameters[] = in.createIntArray();
		double latlng[] = in.createDoubleArray();	
		if( parameters!=null && parameters.length!=0 && latlng!=null && latlng.length!=0 ){
			this.operationType = parameters[0];
			this.dataType = parameters[1];
			this.calculater = parameters[2];
			this.sensorName = parameters[3];
			this.valueName = parameters[4];
			this.attributeName = parameters[5];
			this.numberOfDetection = parameters[6];
			this.latlngConstant = new double[4];
			this.latlngConstant[0] = latlng[0];
			this.latlngConstant[1] = latlng[1];
			this.latlngConstant[2] = latlng[2];
			this.latlngConstant[3] = latlng[3];
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
	 * Sensor value(Location.Lat.NoParam,Location.Lng.NoParam) is compared constant(double[4]) with calculater.
	 */
	@Override
	public boolean evaluate( SensorData latest ){
		String latKey = MatchingConstants.PARAM_STRING[sensorName] + "." +
		MatchingConstants.PARAM_STRING[MatchingConstants.VN_LAT]+ "." + MatchingConstants.PARAM_STRING[attributeName];
		String lngKey = MatchingConstants.PARAM_STRING[sensorName] + "." +
		MatchingConstants.PARAM_STRING[MatchingConstants.VN_LNG]+ "." + MatchingConstants.PARAM_STRING[attributeName];
		byte latTemp[] = latest.getSensorDate(latKey);
		byte lngTemp[] = latest.getSensorDate(lngKey);
		if(latTemp!=null && lngTemp!=null){
			double lat = Bytes.toDouble(latTemp);
			double lng = Bytes.toDouble(lngTemp);
//	    	Log.v( LOG_TAG, this.toString() + " | " + getKey() + " = " + lat + "," + lng);
			if( MatchingConstants.SMALLER_THAN == calculater ){
				if( latlngConstant[0] < lat && latlngConstant[1] < lng &&
						latlngConstant[2] > lat && latlngConstant[3] > lng ){
					latestResult = true;
					return true;
				}    		
			}else if( MatchingConstants.LARGER_THAN == calculater ){
				if( !(latlngConstant[0] < lat && latlngConstant[1] < lng &&
						latlngConstant[2] > lat && latlngConstant[3] > lng ) ){
					latestResult = true;
					return true;
				}
			}else if( MatchingConstants.EQUAL == calculater ){
				if( latlngConstant[0] == lat && latlngConstant[1] == lng &&
						latlngConstant[2] == lat && latlngConstant[3] == lng ){
					latestResult = true;
					return true;
				}
			}
		}
		latestResult = false;
		return false;
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
    public static final Parcelable.Creator<LatLngCompareOperation> CREATOR = new Parcelable.Creator<LatLngCompareOperation>(){
        public LatLngCompareOperation createFromParcel(Parcel in) {
            return new LatLngCompareOperation(in);
        }
        public LatLngCompareOperation[] newArray(int size) {
            return new LatLngCompareOperation[size];
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
		double latlng[] = new double[4];
		latlng[0] = latlngConstant[0];
		latlng[1] = latlngConstant[1];
		latlng[2] = latlngConstant[2];
		latlng[3] = latlngConstant[3];
		out.writeDoubleArray(latlng);
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
			":(" + MatchingConstants.PARAM_STRING[dataType] + ")" + getKey() + " " +
			MatchingConstants.PARAM_STRING[calculater] + " (" +
			latlngConstant[0] + "," + latlngConstant[1] + "," + latlngConstant[2] + "," + latlngConstant[3] + ")}";
		return returnString;
	}
	
	/**
	 * @return constant(double[4])
	 */
	public double[] getLatLngConstant(){
		return latlngConstant;
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

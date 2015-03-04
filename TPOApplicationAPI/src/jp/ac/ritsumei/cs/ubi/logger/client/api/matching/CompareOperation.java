/*
 * Copyright (C) 2008-2012 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.logger.client.api.matching;

import android.os.Parcelable;
import jp.ac.ritsumei.cs.ubi.logger.client.api.utility.MatchingConstants;

/**
 * This class detect Compare event.
 * 
 * The sensorName corresponded with family of Hbase.
 * The valueName corresponded with columnFamily of Hbase.
 * The attributeName corresponded with columQualifier of Hbase.
 * @author sacchin
 */
public abstract class CompareOperation extends Predicate implements Parcelable{

	/**
	 * This is factory method.
	 * Please see the MatchingConstatns.class
	 * @param dataType must be "MatchingConstants.DT_INT"
	 * @param sensorName means which sensor is used. 
	 * @param valueName means which value is used.
	 * @param operator "equal" or "smaller" or "larger"
	 * 
	 * @param constant (int)
	 */
	public static CompareOperation create(int dataType, int sensorName, 
			int valueName, int operator, int constant){
		if(MatchingConstants.DT_INT == dataType){
			return new IntCompareOperation(sensorName, valueName, MatchingConstants.NO_PARAM, operator, constant);
		}else{
			throw new NullPointerException("Arguments is not correct!");
		}
	}
	
	/**
	 * This is factory method.
	 * Please see the MatchingConstatns.class
	 * @param dataType must be "MatchingConstants.DT_INT"
	 * @param sensorName means which sensor is used. 
	 * @param valueName means which value is used.
	 * @param attributeName means which attribute is used
	 * @param operator "equal" or "smaller" or "larger"
	 * 
	 * @param constant (int)
	 */
	public static CompareOperation create(int dataType, int sensorName, 
			int valueName, int attributeName, int operator, int constant){
		if(MatchingConstants.DT_INT == dataType){
			return new IntCompareOperation(sensorName, valueName, attributeName, operator, constant);
		}else{
			throw new NullPointerException("Arguments is not correct!");
		}
	}
	
	/**
	 * This is factory method.
	 * Please see the MatchingConstatns.class
	 * @param dataType must be "MatchingConstants.DT_FLOAT"
	 * @param sensorName means which sensor is used.
	 * @param valueName means which  is used.
	 * @param operator "equal" or "smaller" or "larger"
	 * @param constant value which compare with raw data(float)
	 * 
	 * @return instance of FloatCompareOperation
	 */
	public static CompareOperation create( int dataType, int sensorName, 
			int valueName, int operator, float constant ){
		if(MatchingConstants.DT_FLOAT == dataType){
			return new FloatCompareOperation(sensorName, valueName, MatchingConstants.NO_PARAM, operator, constant);
		}else{
			throw new NullPointerException("Arguments is not correct!");
		}
	}
	
	/**
	 * This is factory method.
	 * Please see the MatchingConstatns.class
	 * @param dataType must be "MatchingConstants.DT_FLOAT"
	 * @param sensorName means which sensor is used.
	 * @param valueName means which  is used.
	 * @param attributeName means which attribute is used
	 * @param operator "equal" or "smaller" or "larger"
	 * @param constant value which compare with raw data(float)
	 * 
	 * @return instance of FloatCompareOperation
	 */
	public static CompareOperation create( int dataType, int sensorName, 
			int valueName, int attributeName, int operator, float constant ){
		if(MatchingConstants.DT_FLOAT == dataType && MatchingConstants.AN_OVERLAP_RATIO == attributeName){
			return new OverlapRatioCompare(dataType, operator, constant);
		}else if(MatchingConstants.DT_FLOAT == dataType){
			return new FloatCompareOperation(sensorName, valueName, MatchingConstants.NO_PARAM, operator, constant);
		}else{
			throw new NullPointerException("Arguments is not correct!");
		}
	}
	
	/**
	 * This is factory method.
	 * Please see the MatchingConstatns.class
	 * @param dataType must be "MatchingConstants.DT_DOUBLE"
	 * @param sensorName means which sensor is used.
	 * @param valueName means which  is used.
	 * @param operator "equal" or "smaller" or "larger"
	 * @param constant value which compare with raw data(double)
	 * 
	 * @return instance of DoubleCompareOperation
	 */
	public static CompareOperation create(int dataType, int sensorName, 
			int valueName, int operator, double constant){
		if(MatchingConstants.DT_DOUBLE == dataType){
			return new DoubleCompareOperation(sensorName, valueName, MatchingConstants.NO_PARAM, operator, constant);
		}else{
			throw new NullPointerException("Arguments is not correct!");
		}
	}
	
	/**
	 * This is factory method.
	 * Please see the MatchingConstatns.class
	 * @param dataType must be "MatchingConstants.DT_DOUBLE"
	 * @param sensorName means which sensor is used.
	 * @param valueName means which  is used.
	 * @param attributeName means which attribute is used
	 * @param operator "equal" or "smaller" or "larger"
	 * @param constant value which compare with raw data(double)
	 * 
	 * @return instance of DoubleCompareOperation
	 */
	public static CompareOperation create(int dataType, int sensorName, 
			int valueName, int attributeName, int operator, double constant){
		if(MatchingConstants.DT_DOUBLE == dataType){
			return new DoubleCompareOperation(sensorName, valueName, attributeName, operator, constant);
		}else{
			throw new NullPointerException("Arguments is not correct!");
		}
	}
	
	/**
	 * This is factory method.
	 * Please see the MatchingConstatns.class
	 * @param dataType must be "MatchingConstants.DOUBLE"
	 * @param sensorName must be "MatchingConstants.LAT_LNG"
	 * @param valueName mean which attribute is used.
	 * @param operator "equal" or "smaller" or "larger"
	 * @param constant value which compare with raw data(double[smallLat, smallLng, largeLat, largeLng])
	 * @return instance of LatLngCompareOperation
	 */
	public static CompareOperation create( int dataType, int sensorName, 
			int valueName, int operator, double constant[] ){
		if( MatchingConstants.DT_DOUBLE == dataType && MatchingConstants.VN_LAT_LNG == valueName ){
			return new LatLngCompareOperation(dataType, operator, constant);
		}else{
			throw new NullPointerException("Arguments is not correct!");
		}
	}
	
//	/**
//	 * This is factory method.
//	 * Please see the MatchingConstatns.class
//	 * @param sensorName must be "MatchingConstants.SN_ACCELEROMETER"
//	 * @param valueName must be "MatchingConstants.VN_VECTOR"
//	 * @param attributeName must be "MatchingConstants.AN_STEP_DETECTER"
//	 * @return instance of StepCompareOperation
//	 */
//	public static CompareOperation create(int sensorName, int valueName, int attributeName){
//		if( MatchingConstants.SN_ACCELEROMETER == sensorName && 
//				MatchingConstants.VN_VECTOR == valueName &&
//				MatchingConstants.AN_STEP_DETECTER == attributeName ){
//			Log.v("CompareOperation.create", "create LatLngCompareOperation.");
//			return new StringCompareOperation(sensorName, valueName, attributeName);
//		}else{
//			throw new NullPointerException("Arguments is not correct!");
//		}
//	}
	
	/**
	 * This is factory method.
	 * Please see the MatchingConstatns.class
	 * @param dataType must be "MatchingConstants.DT_STRING"
	 * @param sensorName means which sensor is used.
	 * @param valueName means which  is used.
	 * @param operator "equal" or "smaller" or "larger"
	 * @param constant value which compare with raw data(String)
	 * 
	 * @return instance of StringCompareOperation
	 */
	public static CompareOperation create(int dataType, int sensorName, 
			int valueName, int operator, String constant){
		if( MatchingConstants.DT_STRING == dataType ){
			return new StringCompareOperation(sensorName, valueName, MatchingConstants.NO_PARAM, operator, constant);
		}else{
			throw new NullPointerException("Arguments is not correct!");
		}
	}
	
	public abstract int getDataType();
	public abstract String toString();

	/**
	 * set parent(parent is LogicOperation)
	 */
	@Override
	public void setParent(LogicOperation parent) {
		this.parent = parent;
	}
	
	/**
	 * return MatchingConstants.COMPARE
	 */
	@Override
	public int getOperationType(){
		return MatchingConstants.COMPARE;
	}
}
/*
 * Copyright (C) 2008-2012 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.logger.client.api.matching;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import jp.ac.ritsumei.cs.ubi.logger.client.api.sensors.SensorData;
import jp.ac.ritsumei.cs.ubi.logger.client.api.utility.MatchingConstants;

/**
 * This class calculate or
 * @author sacchin
 */
public class LogicOrOperation extends LogicOperation implements Parcelable {
	protected static final String LOG_TAG_OR = "LogicOrOperation";	
	protected final int operationType;
	protected final int calculater;
	protected final List<Predicate> operations;

	/**
	 * This is constructor.
	 * @param getOerations ComapareOperation or LogicOperation
	 */
	public LogicOrOperation( List<Predicate> operations ) {
		super();
		this.operationType = MatchingConstants.LOGIC;
		this.calculater = MatchingConstants.OR;
		this.operations = operations;
	}
	
	/**
	 * This is constructor.
	 * The values of field is read from in, because this class extends Parcelable.
	 * @param in
	 */
	public LogicOrOperation( Parcel in ) {
		super();
		int parameters[] = in.createIntArray();
		if( parameters!=null && parameters.length!=0 ){
			this.operationType = parameters[0];
			this.calculater = parameters[1];
			this.numberOfDetection = parameters[2];
		}else{
			throw new NullPointerException("parameters is null or 0.");
		}
		ArrayList<Predicate> temp = new ArrayList<Predicate>();
		Parcelable pArray[] = in.readParcelableArray(Predicate.class.getClassLoader());
		if( pArray!=null ){
			for( Parcelable p : pArray ){
				temp.add( (Predicate)p );
			}
		}else{
			throw new NullPointerException("Parcelable pArray[] is null.");
		}
		this.operations = temp;
	}
	
	/**
	 * if oneself is true, call parent evaluate
	 * @return true or false
	 */
	@Override
	public boolean evaluate(){
//		Log.v(LOG_TAG_OR, this.toString());
		ArrayList<Predicate> a = (ArrayList<Predicate>) this.operations;
		for( Predicate temp : a ){
			if( temp.getLatestResult() ){
				this.latestResult = true;
				return true;
			}
		}
		this.latestResult = false;
		return false;
	}

	/**
	 * evaluate all operations
	 * @param latest all sensor values
	 * @return if all operations is false, return false.
	 */
	@Override
	public boolean evaluate( SensorData latest ){
//		Log.v(LOG_TAG_OR, this.toString());
		ArrayList<Predicate> a = (ArrayList<Predicate>) this.operations;
		for( Predicate temp : a ){
			if( temp.evaluate( latest ) ){
				return true;
			}			
		}
		return false;
	}
	
	/**
	 * for Parcelable
	 */
	public static final Parcelable.Creator<LogicOrOperation> CREATOR = new Parcelable.Creator<LogicOrOperation>(){
		public LogicOrOperation createFromParcel(Parcel in) {
			return new LogicOrOperation(in);
		}
		public LogicOrOperation[] newArray(int size) {
			return new LogicOrOperation[size];
		}
	};

	/**
	 * for Parcelable
	 */
	public int describeContents() {
		return 0;
	}

	/**
	 * for parcelable
	 * write values of field
	 */
	public void writeToParcel(Parcel out, int flags) {
		int parameters[] = new int[2];
		parameters[0] = operationType;
		parameters[1] = calculater;
		out.writeIntArray(parameters);
		Predicate rray[] = (Predicate[])operations.toArray(new Predicate[0]);
		out.writeParcelableArray(rray, 0);
	}
	
	@Override
	public String toString(){
		String returnString = "{" + MatchingConstants.PARAM_STRING[operationType] +
			":(" + MatchingConstants.PARAM_STRING[calculater] + ")";
		if( operations!=null ){
			ArrayList<Predicate> a = (ArrayList<Predicate>) this.operations;
			for( Predicate temp : a ){
				if( temp!=null ){
					returnString += temp.toString() + ",";
				}
			}			
		}
		return returnString + "}";
	}
	
	/**
	 * return MatchingConstants.PARAM_STRING[MatchingConstants.OR]
	 */
	@Override
	public String getKey() {
		return MatchingConstants.PARAM_STRING[MatchingConstants.OR];
	}

	/**
	 * return calculater
	 */
	@Override
	public int getCalculater() {
		return calculater;
	}



	/**
	 * return operations
	 */
	@Override
	public List<Predicate> getOperations() {
		return operations;
	}
	
	/**
	 * set parent(parent is LogicOperation)
	 * and call operations.setParent(this)
	 */
	@Override
	public void setParent( LogicOperation parent ){
		this.parent = parent;
		for( Predicate temp : operations ){
			temp.setParent(this);
		}
	}
	
	/**
	 * return sensorName
	 */
	@Override
	public int getSensorName() {
		return MatchingConstants.NO_PARAM;
	}
	
	/**
	 * return MatchingConstants.LOGIC
	 */
	@Override
	public int getValueName() {
		return MatchingConstants.LOGIC;
	}
	
	/**
	 * return attributeName
	 */
	@Override
	public int getAttributeName() {
		return -1;
	}
}
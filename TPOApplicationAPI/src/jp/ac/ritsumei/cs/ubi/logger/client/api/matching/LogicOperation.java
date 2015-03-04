/*
 * Copyright (C) 2008-2012 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.logger.client.api.matching;

import java.util.ArrayList;
import java.util.List;

import jp.ac.ritsumei.cs.ubi.logger.client.api.utility.MatchingConstants;

import android.os.Parcelable;

/**
 * This class
 * @author sacchin
 */
public abstract class LogicOperation extends Predicate implements Parcelable{
	
	/**
	 * This is constructor.
	 */
	public LogicOperation(){
		super();
	}

	/**
	 * This is factory method.
	 * @param getOerations ComapareOperation or LogicOperation
	 * @return instance of LogicAndOperation
	 */
	public static LogicAndOperation and( Predicate ...getOerations ){
		ArrayList<Predicate> operations = new ArrayList<Predicate>();
		for( Predicate temp : getOerations ){
			if( temp!=null ){
				operations.add(temp);
			}else{
				throw new NullPointerException("getOerations has null!!!");
			}
		}
		return new LogicAndOperation( operations );
	}

	/**
	 * This is factory method.
	 * @param getOerations ComapareOperation or LogicOperation
	 * @return instance of LogicOrOperation
	 */
	public static LogicOrOperation or( Predicate ...getOerations ){
		ArrayList<Predicate> operations = new ArrayList<Predicate>();
		for( Predicate temp : getOerations ){
			if( temp!=null ){
				operations.add(temp);
			}else{
				throw new NullPointerException("getOerations has null!!!");
			}
		}
		return new LogicOrOperation( operations );
	}
	
	public abstract List<Predicate> getOperations();
	
	/**
	 * return MatchingConstants.LOGIC
	 */
	@Override
	public int getOperationType(){
		return MatchingConstants.LOGIC;
	}
}
/*
 * Copyright (C) 2008-2012 Ritsumeikan University Nishio Laboratory All Rights Reserved.
 */
package jp.ac.ritsumei.cs.ubi.logger.client.api.matching;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class is requests to detect events 
 * @author sacchin
 */
public class EventDetectionRequest implements Parcelable{
	protected static final String LOG_TAG = "Notification";
	protected final Intent reply;
	protected final Predicate predicate;
	protected final String intentKey;
	
	/**
	 * This is constructor.
	 * @param reply
	 * @param predicate
	 */
	public EventDetectionRequest(Intent reply, Predicate predicate){
		this.reply = reply;
		this.predicate = predicate;
		this.intentKey = predicate.toString();
	}

	/**
	 * This is constructor.
	 * The values of field is read from in, because this class extends Parcelable.
	 * @param in
	 */
	public EventDetectionRequest( Parcel in ) {
		super();
		this.reply = in.readParcelable( Intent.class.getClassLoader() );
		this.predicate = (Predicate) in.readParcelable( LogicAndOperation.class.getClassLoader() );
		this.intentKey = in.readString();
	}

	/**
	 * for parcelable
	 */
	public int describeContents() {
		return 0;
	}

	/**
	 * for parcelable
	 */
	public static final Parcelable.Creator<EventDetectionRequest> CREATOR = new Parcelable.Creator<EventDetectionRequest>(){
		public EventDetectionRequest createFromParcel(Parcel in) {
			return new EventDetectionRequest(in);
		}
		public EventDetectionRequest[] newArray(int size) {
			return new EventDetectionRequest[size];
		}
	};

	/**
	 * for parcelable
	 * write values of field
	 */
	public void writeToParcel(Parcel out, int flags) {
		out.writeParcelable(reply, 0);		
		out.writeParcelable(predicate, 0);
		out.writeString(intentKey);
	}

	@Override
	public String toString(){
		return "{" + reply.toString() + "," + predicate.toString() + "}";
	}
	
	/**
	 * return reply Intent
	 * @return
	 */
	public Intent getReply() {
		return reply;
	}

	/**
	 * return matching predicate
	 * @return
	 */
	public Predicate getPredicate() {
		return predicate;
	}

	/**
	 * return key of Intent
	 * @return
	 */
	public String getIntentKey() {
		return intentKey;
	}
}

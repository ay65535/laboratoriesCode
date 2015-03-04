package jp.ac.ritsumei.cs.ubi.zukky.BRM.json;

import java.util.ArrayList;
import java.util.List;

import jp.ac.ritsumei.cs.ubi.zukky.BRM.transport.Transportation;



public class Json_Transportation{

	private int jTransportationId;
	private List<Json_Mode> modeList = new ArrayList<Json_Mode>();
	private List<Transportation> transportationList = new ArrayList<Transportation>();
	private String transportationListString;

	public List<Json_Mode> getModeList() {
		return modeList;
	}

	public void setModeList(List<Json_Mode> modeList) {
		this.modeList = modeList;
	}
	
	public void addModeList(Json_Mode mode){
		this.modeList.add(mode);
	}
	
	public List<Transportation> getTransportationList() {
		return transportationList;
	}

	public void setTransportationList(List<Transportation> transportationList) {
		this.transportationList = transportationList;
	}

	public String getTransportationListString() {
		return transportationListString;
	}

	public void setTransportationListString(List<Transportation> transportationList) {
		this.transportationListString = toString(transportationList);
	}
		
	
	private String toString(List<Transportation> transportationList){
		String resultString = "";
		
		for(Transportation transportation: transportationList){
			resultString = resultString + transportation.getType().toString();
		}
		return resultString;
	}

	public int getjTransportationId() {
		return jTransportationId;
	}

	public void setjTransportationId(int jTransportationId) {
		this.jTransportationId = jTransportationId;
	}
	
}

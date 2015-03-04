package jp.ac.ritsumei.cs.ubi.zukky.BRM.json;

import java.util.ArrayList;
import java.util.List;

public class Json_Mode {

	private int jModeId;
	private String mode;
	private List<Json_Route> routeList = new ArrayList<Json_Route>();

	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public List<Json_Route> getRouteList() {
		return routeList;
	}
	public void setRouteList(List<Json_Route> routeList) {
		this.routeList = routeList;
	}
	public void addRoute(Json_Route jRoute){
		this.routeList.add(jRoute);
	}
	
	
	
	public int getjModeId() {
		return jModeId;
	}
	public void setjModeId(int jModeId) {
		this.jModeId = jModeId;
	}
	

}

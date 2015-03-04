package jp.ac.ritsumei.cs.ubi.zukky.BRM.kubiwa;

public class KubiwaUser {
	
	private int id;
	private String uname;
	private String password;
	private String address;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String name) {
		this.uname = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	
	public KubiwaUser(){
		super();
	}
	
	public KubiwaUser(int id){
		this.id=id;
	}
}

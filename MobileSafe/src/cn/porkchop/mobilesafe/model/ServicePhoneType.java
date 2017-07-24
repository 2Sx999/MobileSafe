package cn.porkchop.mobilesafe.model;

import java.util.List;

public class ServicePhoneType {
	private int id;
	private String type;
	private List<ServicePhoneDetail> list;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<ServicePhoneDetail> getList() {
		return list;
	}
	public void setList(List<ServicePhoneDetail> list) {
		this.list = list;
	}
	@Override
	public String toString() {
		return "ServicePhoneType [id=" + id + ", type=" + type + ", list="
				+ list + "]";
	}


}

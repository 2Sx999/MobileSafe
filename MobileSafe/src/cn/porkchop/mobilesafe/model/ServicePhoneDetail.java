package cn.porkchop.mobilesafe.model;

public class ServicePhoneDetail {
	private int id;
	private String number;
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ServicePhoneDetail [id=" + id + ", number=" + number
				+ ", name=" + name + "]";
	}

	

}

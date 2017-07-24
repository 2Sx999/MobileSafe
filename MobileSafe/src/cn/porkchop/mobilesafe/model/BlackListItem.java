package cn.porkchop.mobilesafe.model;

public class BlackListItem {

	private String phone;
	private int id;
	private int mode;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	@Override
	public String toString() {
		return "BlackListItem [phone=" + phone + ", id=" + id + ", mode="
				+ mode + "]";
	}

}

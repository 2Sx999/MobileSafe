package cn.porkchop.mobilesafe.model;

import android.graphics.drawable.Drawable;

public class AppInfo {
	private int uid;
	private Drawable icon;// 图标
	private String appName;// app名字
	private boolean isSystem;// 是否是系统软件
	private boolean isSD;// 是否安装在sd卡中
	private String packName;// app包名
	private long size;// 占用的大小
	private String sourceDir;// 安装路径
	private long memSize;// 占用的内存大小
	private boolean isChecked;
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public boolean isSystem() {
		return isSystem;
	}
	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}
	public boolean isSD() {
		return isSD;
	}
	public void setSD(boolean isSD) {
		this.isSD = isSD;
	}
	public String getPackName() {
		return packName;
	}
	public void setPackName(String packName) {
		this.packName = packName;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getSourceDir() {
		return sourceDir;
	}
	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}
	public long getMemSize() {
		return memSize;
	}
	public void setMemSize(long memSize) {
		this.memSize = memSize;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	@Override
	public String toString() {
		return "AppInfo [uid=" + uid + ", icon=" + icon + ", appName="
				+ appName + ", isSystem=" + isSystem + ", isSD=" + isSD
				+ ", packName=" + packName + ", size=" + size + ", sourceDir="
				+ sourceDir + ", memSize=" + memSize + ", isChecked="
				+ isChecked + "]";
	}

	
}

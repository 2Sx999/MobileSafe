package cn.porkchop.mobilesafe.activity;

import cn.porkchop.mobilesafe.R;
import cn.porkchop.mobilesafe.service.BlackListInterceptService;
import cn.porkchop.mobilesafe.service.ShowIncomingNumberLocationService;
import cn.porkchop.mobilesafe.util.DataUtil;
import cn.porkchop.mobilesafe.util.SPUtil;
import cn.porkchop.mobilesafe.util.ServiceUtil;
import cn.porkchop.mobilesafe.util.SettingConstant;
import cn.porkchop.mobilesafe.view.LocationStyleDialog;
import cn.porkchop.mobilesafe.view.SettingCenterItem;
import cn.porkchop.mobilesafe.view.SettingCenterItem.OnToggleChangedListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SettingCenterActivity extends Activity {
	private SettingCenterItem sci_autoupdate;
	private SettingCenterItem sci_blacklistintercept;
	private SettingCenterItem sci_incomingnumberlocation;
	protected SettingCenterItem sci_locationstyle;
	private LocationStyleDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
		initEvent();
	}

	private void initEvent() {
		OnToggleChangedListener onToggleChangedListener = new OnToggleChangedListener() {

			@Override
			public void onToggleChanged(View v) {
				switch (v.getId()) {
				case R.id.settingcenteritem_settingcenter_autoupdate:
					if (sci_autoupdate.isToggleOn()) {
						// 如果当前是开启,那就关闭,并且关闭自动更新
						sci_autoupdate.setToggle(false);
						SPUtil.putBoolean(SettingCenterActivity.this,
								SettingConstant.FILENAME,
								SettingConstant.ISAUTOUPDATE, false);
					} else {
						sci_autoupdate.setToggle(true);
						SPUtil.putBoolean(SettingCenterActivity.this,
								SettingConstant.FILENAME,
								SettingConstant.ISAUTOUPDATE, true);
					}
					break;
				case R.id.settingcenteritem_settingcenter_blacklistintercept:
					if (sci_blacklistintercept.isToggleOn()) {
						// 如果当前是开启,那就关闭,并且关闭黑名单拦截服务
						sci_blacklistintercept.setToggle(false);
						stopService(new Intent(SettingCenterActivity.this,
								BlackListInterceptService.class));
					} else {
						sci_blacklistintercept.setToggle(true);
						startService(new Intent(SettingCenterActivity.this,
								BlackListInterceptService.class));
					}
					break;
				case R.id.settingcenteritem_settingcenter_incomingnumberlocation:
					if (sci_incomingnumberlocation.isToggleOn()) {
						// 如果当前是开启,那就关闭,并且关闭来点归属地显示服务
						sci_incomingnumberlocation.setToggle(false);
						stopService(new Intent(SettingCenterActivity.this,
								ShowIncomingNumberLocationService.class));
					} else {
						sci_incomingnumberlocation.setToggle(true);
						startService(new Intent(SettingCenterActivity.this,
								ShowIncomingNumberLocationService.class));
					}
					break;
				case R.id.settingcenteritem_settingcenter_locationstyle:

					mDialog.show();
					break;
				}

			}
		};
		sci_autoupdate.setOnToggleChangedListener(onToggleChangedListener);
		sci_blacklistintercept
				.setOnToggleChangedListener(onToggleChangedListener);
		sci_incomingnumberlocation
				.setOnToggleChangedListener(onToggleChangedListener);
		sci_locationstyle.setOnToggleChangedListener(onToggleChangedListener);

	}

	private void initData() {
		// 回显是否自动更新
		sci_autoupdate.setToggle(SPUtil.getBoolean(this,
				SettingConstant.FILENAME, SettingConstant.ISAUTOUPDATE, false));
		// 回显黑名单拦截服务是否开启
		sci_blacklistintercept.setToggle(ServiceUtil.isServiceRunning(this,
				"cn.porkchop.mobilesafe.service.BlackListInterceptService"));
		// 判断服务是否开启
		sci_incomingnumberlocation
				.setToggle(ServiceUtil
						.isServiceRunning(this,
								"cn.porkchop.mobilesafe.service.ShowIncomingNumberLocationService"));
		// 回显保存的样式
		sci_locationstyle.setDesc("归属地样式("
				+ LocationStyleDialog.styleNames[SPUtil.getInt(this,
						SettingConstant.FILENAME,
						SettingConstant.LOCATIONSTYLE, 2)] + ")");
	}

	private void initView() {
		setContentView(R.layout.activity_settingcenter);
		sci_autoupdate = (SettingCenterItem) findViewById(R.id.settingcenteritem_settingcenter_autoupdate);
		sci_blacklistintercept = (SettingCenterItem) findViewById(R.id.settingcenteritem_settingcenter_blacklistintercept);
		sci_incomingnumberlocation = (SettingCenterItem) findViewById(R.id.settingcenteritem_settingcenter_incomingnumberlocation);
		sci_locationstyle = (SettingCenterItem) findViewById(R.id.settingcenteritem_settingcenter_locationstyle);
		mDialog = new LocationStyleDialog(SettingCenterActivity.this,
				sci_locationstyle);
	}
}

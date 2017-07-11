package cn.porkchop.mobilesafe.activity;

import cn.porkchop.mobilesafe.R;
import cn.porkchop.mobilesafe.receiver.MyDeviceAdminReceiver;
import cn.porkchop.mobilesafe.service.LostFindService;
import cn.porkchop.mobilesafe.util.DataUtil;
import cn.porkchop.mobilesafe.util.SPUtil;
import cn.porkchop.mobilesafe.util.ServiceUtil;
import cn.porkchop.mobilesafe.util.SettingConstant;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class LostFindStep4Activity extends LostFindBaseActivity {
	private TextView tv_prompt;
	private CheckBox cb_isprotecting;
	DevicePolicyManager mDPM;
	ComponentName mDeviceAdminSample;

	@Override
	public void initView() {
		setContentView(R.layout.activity_lostfindstep4);
		cb_isprotecting = (CheckBox) findViewById(R.id.cb_lostfindstep4_isprotecting);
		tv_prompt = (TextView) findViewById(R.id.tv_lostfindstep4_prompt);
	}

	@Override
	protected void initData() {
		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mDeviceAdminSample = new ComponentName(this,
				MyDeviceAdminReceiver.class);
		// 根据服务是否开启,来确定是否打勾
		if (ServiceUtil.isServiceRunning(this,
				"cn.porkchop.mobilesafe.service.LostFindService")) {
			cb_isprotecting.setChecked(true);
		}
	}

	@Override
	public void startPreviousPage(View v) {
		startPage(LostFindStep3Activity.class);
		startPreviousPageAnimation();
	}

	@Override
	public void initEvent() {
		cb_isprotecting
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Intent service = new Intent(LostFindStep4Activity.this,
								LostFindService.class);
						if (isChecked) {
							if (mDPM.isAdminActive(mDeviceAdminSample)) {
								// 激活设备管理,开启服务
								startService(service);
								tv_prompt.setText("防盗保护已开启");
							} else {
								// 没有激活设备管理,打开激活管理的界面
								Intent intent = new Intent(
										DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
								intent.putExtra(
										DevicePolicyManager.EXTRA_DEVICE_ADMIN,
										mDeviceAdminSample);
								intent.putExtra(
										DevicePolicyManager.EXTRA_ADD_EXPLANATION,
										"激活设备管理");
								startActivityForResult(intent, 0);
							}
						} else {
							stopService(service);
							tv_prompt.setText("防盗保护已关闭");
						}
					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!mDPM.isAdminActive(mDeviceAdminSample)) {
			DataUtil.showToast(this, "请先激活设备管理");
			cb_isprotecting.setChecked(false);
		} else {
			Intent service = new Intent(this, LostFindService.class);
			startService(service);
			tv_prompt.setText("防盗保护已开启");
		}
	}

	@Override
	public void startNextPage(View v) {
		if (cb_isprotecting.isChecked()) {
			SPUtil.putBoolean(this, SettingConstant.FILENAME,
					SettingConstant.ISLOSTFINDSTEPFINISHED, true);
			SPUtil.putBoolean(this, SettingConstant.FILENAME,
					SettingConstant.ISBOOTWITHSERVICE, true);
			startPage(LostFindActivity.class);
			startNextPageAnimation();
		} else {
			DataUtil.showToast(this, "请开启防盗保护");
		}
	}
}

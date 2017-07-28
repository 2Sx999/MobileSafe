package cn.porkchop.mobilesafe.activity;

import cn.porkchop.mobilesafe.R;
import cn.porkchop.mobilesafe.util.DataUtil;
import cn.porkchop.mobilesafe.util.SmsUtil;
import cn.porkchop.mobilesafe.util.SmsUtil.SmsBackupRestoreListener;
import cn.porkchop.mobilesafe.view.SettingCenterItem;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class AdvancedToolsActivity extends Activity {
	private SettingCenterItem sci_findphonelocation;
	private SettingCenterItem sci_findservicenumber;
	private SettingCenterItem sci_smsbackup;
	private SettingCenterItem sci_smsrestore;
	private ProgressDialog mProgressDialog;
	private SmsBackupRestoreListener mSmsBackupRestoreListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
		initEvent();
	}

	private void initEvent() {
		mSmsBackupRestoreListener = new SmsBackupRestoreListener() {

			@Override
			public void setProgress(int currentProgress) {
				mProgressDialog.setProgress(currentProgress);
			}

			@Override
			public void setMax(int max) {
				mProgressDialog.setMax(max);
			}

			@Override
			public void onShow() {
				mProgressDialog.show();
			}

			@Override
			public void onDismiss() {
				mProgressDialog.dismiss();
			}
		};

		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.settingcenteritem_advancedtools_findphonelocation:
					startActivity(new Intent(AdvancedToolsActivity.this,
							FindPhoneLocationActivity.class));
					break;
				case R.id.settingcenteritem_advancedtools_findservicenumber:
					startActivity(new Intent(AdvancedToolsActivity.this,
							FIndServiceNumberActivity.class));
					break;
				case R.id.settingcenteritem_advancedtools_smsbackup:
					// 短信备份
					try {
						SmsUtil.smsBackup(AdvancedToolsActivity.this,
								mSmsBackupRestoreListener);
					} catch (Exception e) {
						DataUtil.showToast(AdvancedToolsActivity.this,
								e.getMessage());
					}
					break;
				case R.id.settingcenteritem_advancedtools_smsrestore:
					// 短信恢复
					try {
						SmsUtil.smsRestore(AdvancedToolsActivity.this,
								mSmsBackupRestoreListener);
					} catch (Exception e) {
						DataUtil.showToast(AdvancedToolsActivity.this,
								e.getMessage());
					}
					break;
				}
			}
		};
		sci_findphonelocation.setOnClickListener(listener);
		sci_findservicenumber.setOnClickListener(listener);
		sci_smsbackup.setOnClickListener(listener);
		sci_smsrestore.setOnClickListener(listener);

	}

	private void initData() {

	}

	private void initView() {
		setContentView(R.layout.activity_advancedtools);
		sci_findphonelocation = (SettingCenterItem) findViewById(R.id.settingcenteritem_advancedtools_findphonelocation);
		sci_findservicenumber = (SettingCenterItem) findViewById(R.id.settingcenteritem_advancedtools_findservicenumber);
		sci_smsbackup = (SettingCenterItem) findViewById(R.id.settingcenteritem_advancedtools_smsbackup);
		sci_smsrestore = (SettingCenterItem) findViewById(R.id.settingcenteritem_advancedtools_smsrestore);
		// 只生成一个progressdialog
		mProgressDialog = new ProgressDialog(AdvancedToolsActivity.this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	}
}

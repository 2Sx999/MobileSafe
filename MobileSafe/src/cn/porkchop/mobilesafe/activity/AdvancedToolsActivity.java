package cn.porkchop.mobilesafe.activity;

import cn.porkchop.mobilesafe.R;
import cn.porkchop.mobilesafe.view.SettingCenterItem;
import android.app.Activity;
import android.content.Intent;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class AdvancedToolsActivity extends Activity {
	private SettingCenterItem sci_findphonelocation;
	private SettingCenterItem sci_findservicenumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
		initEvent();
	}

	private void initEvent() {
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
				}
			}
		};
		sci_findphonelocation.setOnClickListener(listener);
		sci_findservicenumber.setOnClickListener(listener);
	}

	private void initData() {

	}

	private void initView() {
		setContentView(R.layout.activity_advancedtools);
		sci_findphonelocation = (SettingCenterItem) findViewById(R.id.settingcenteritem_advancedtools_findphonelocation);
		sci_findservicenumber = (SettingCenterItem) findViewById(R.id.settingcenteritem_advancedtools_findservicenumber);
	}
}

package cn.porkchop.mobilesafe.activity;

import cn.porkchop.mobilesafe.R;
import cn.porkchop.mobilesafe.util.SPUtil;
import cn.porkchop.mobilesafe.util.SettingConstant;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LostFindActivity extends Activity {
	private TextView tv_safenumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (SPUtil.getBoolean(this, SettingConstant.FILENAME,
				SettingConstant.ISLOSTFINDSTEPFINISHED, false)) {
			// 设置已经完成,初始化界面
			initView();
			initData();
		} else {
			// 未设置,进入第一步的界面
			Intent intent = new Intent(this, LostFindStep1Activity.class);
			startActivity(intent);
			finish();
		}
	}

	private void initData() {
		// 回显安全号码
		tv_safenumber.setText(SPUtil.getString(this, SettingConstant.FILENAME,
				SettingConstant.SAFENUMBER, ""));
	}

	public void startStep1(View v) {
		Intent intent = new Intent(this, LostFindStep1Activity.class);
		startActivity(intent);
		finish();
	}

	private void initView() {
		setContentView(R.layout.activity_lostfind);
		tv_safenumber = (TextView) findViewById(R.id.tv_lostfind_safenumber);
	}
}

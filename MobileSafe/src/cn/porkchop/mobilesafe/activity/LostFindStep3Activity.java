package cn.porkchop.mobilesafe.activity;

import cn.porkchop.mobilesafe.R;
import cn.porkchop.mobilesafe.util.DataUtil;
import cn.porkchop.mobilesafe.util.SPUtil;
import cn.porkchop.mobilesafe.util.SettingConstant;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

public class LostFindStep3Activity extends LostFindBaseActivity {
	private EditText et_safenumber;

	@Override
	public void initView() {
		setContentView(R.layout.activity_lostfindstep3);
		et_safenumber = (EditText) findViewById(R.id.et_lostfindstep3_safenumber);
	}

	@Override
	protected void initData() {
		// 把保存的安全号码回显到界面上
		et_safenumber.setText(SPUtil.getString(this, SettingConstant.FILENAME,
				SettingConstant.SAFENUMBER, ""));
	}

	@Override
	public void startPreviousPage(View v) {
		startPage(LostFindStep2Activity.class);
		startPreviousPageAnimation();
	}

	@Override
	public void startNextPage(View v) {
		if (TextUtils.isEmpty(et_safenumber.getText().toString())) {
			// 没输入号码
			DataUtil.showToast(this, "请输入安全号码!");
		} else {
			// 保存并进入下一界面
			SPUtil.putString(this, SettingConstant.FILENAME,
					SettingConstant.SAFENUMBER, et_safenumber.getText()
							.toString());
			startPage(LostFindStep4Activity.class);
			startNextPageAnimation();
		}
	}

	public void chooseNumber(View v) {
		Intent intent = new Intent(this, ContactListActivity.class);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 把选择的号码放到输入框
		if (data != null) {
			String number = data.getStringExtra("number");
			et_safenumber.setText(number);
		}
	}
}

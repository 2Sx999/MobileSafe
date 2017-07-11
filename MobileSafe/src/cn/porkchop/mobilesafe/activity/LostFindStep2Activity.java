package cn.porkchop.mobilesafe.activity;

import cn.porkchop.mobilesafe.R;
import cn.porkchop.mobilesafe.util.DataUtil;
import cn.porkchop.mobilesafe.util.SPUtil;
import cn.porkchop.mobilesafe.util.SettingConstant;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

public class LostFindStep2Activity extends LostFindBaseActivity {
	private Button bt_bindsim;

	@Override
	public void initView() {
		setContentView(R.layout.activity_lostfindstep2);
		bt_bindsim = (Button) findViewById(R.id.bt_lostfindstep2_bindsim);
	}

	@Override
	public void startPreviousPage(View v) {
		startPage(LostFindStep1Activity.class);
		startPreviousPageAnimation();
	}

	@Override
	public void startNextPage(View v) {
		if (TextUtils.isEmpty(SPUtil.getString(this, SettingConstant.FILENAME,
				SettingConstant.SIMNUMBER, ""))) {
			// 如果没有绑定,就不跳转到下个页面,并提示绑定
			DataUtil.showToast(this, "请绑定sim卡再进入下个设置页面");
		} else {
			startPage(LostFindStep3Activity.class);
			startNextPageAnimation();

		}
	}

	@Override
	/**
	 * 初始化锁的图片
	 */
	protected void initData() {
		Drawable drawable = null;
		if (TextUtils.isEmpty(SPUtil.getString(this, SettingConstant.FILENAME,
				SettingConstant.SIMNUMBER, ""))) {
			// 如果没有绑定
			drawable = getResources().getDrawable(R.drawable.unlock);
		} else {
			// 如果绑定过了
			drawable = getResources().getDrawable(R.drawable.lock);
		}
		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight()); // 设置边界
		bt_bindsim.setCompoundDrawables(null, null, drawable, null);// 画在右边

	}

	public void bindSim(View v) {
		Drawable drawable = null;
		if (TextUtils.isEmpty(SPUtil.getString(this, SettingConstant.FILENAME,
				SettingConstant.SIMNUMBER, ""))) {
			// 如果没有绑定,就绑定
			// 获得sim卡号
			TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			// 保存卡号
			SPUtil.putString(this, SettingConstant.FILENAME,
					SettingConstant.SIMNUMBER, tm.getSimSerialNumber());
			drawable = getResources().getDrawable(R.drawable.lock);
			DataUtil.showToast(this, "sim卡绑定成功!" + tm.getSimSerialNumber());
		} else {
			// 如果绑定过了,就解除绑定
			DataUtil.showToast(this, "sim卡解绑成功!");
			// 取出绑定的卡号
			SPUtil.putString(this, SettingConstant.FILENAME,
					SettingConstant.SIMNUMBER, "");
			drawable = getResources().getDrawable(R.drawable.unlock);
		}
		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight()); // 设置边界
		bt_bindsim.setCompoundDrawables(null, null, drawable, null);// 画在右边
	}
}

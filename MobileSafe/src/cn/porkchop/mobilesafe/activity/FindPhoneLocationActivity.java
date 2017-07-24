package cn.porkchop.mobilesafe.activity;

import cn.porkchop.mobilesafe.R;
import cn.porkchop.mobilesafe.dao.AddressDao;
import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

public class FindPhoneLocationActivity extends Activity {
	private EditText et_number;
	private TextView tv_location;
	AddressDao dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}

	private void initData() {
		dao = new AddressDao(this);
	}

	private void initView() {
		setContentView(R.layout.activity_findphonelocation);
		et_number = (EditText) findViewById(R.id.et_findphonelocation_number);
		tv_location = (TextView) findViewById(R.id.tv_findphonelocation_location);
	}

	/**
	 * 查询归属地,如果没有填写就查询,就抖动文本框并且震动手机
	 * 
	 * @author nanamiporkchop
	 * @time 2017-7-23 下午4:23:36
	 * @param v
	 */
	public void query(View v) {
		String number = et_number.getText().toString();
		if (TextUtils.isEmpty(number)) {
			// 抖动效果
			Animation animation = AnimationUtils.loadAnimation(this,
					R.anim.shake);
			et_number.startAnimation(animation);
			// 震动效果
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(new long[] { 300, 100, 200, 300, 200, 200 }, 1);
			return;
		}
		try {
			String location = dao.getLocation(number);
			tv_location.setText("归属地:" + location);
		} catch (Exception e) {
			tv_location.setText("归属地:未知");
		}
	}
}

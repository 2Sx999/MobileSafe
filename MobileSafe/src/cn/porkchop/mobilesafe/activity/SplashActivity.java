package cn.porkchop.mobilesafe.activity;

import java.security.interfaces.RSAKey;

import cn.porkchop.mobilesafe.R;
import cn.porkchop.mobilesafe.R.id;
import cn.porkchop.mobilesafe.R.layout;
import cn.porkchop.mobilesafe.R.menu;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SplashActivity extends Activity {

	private TextView tv_versioncode;
	private TextView tv_versionname;
	private RelativeLayout linearLayout;
	private int mVersionCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initView();
		initData();
		initAnimation();
	}


	private void initView() {
		setContentView(R.layout.activity_splash);
		tv_versioncode = (TextView) findViewById(R.id.tv_splash_versioncode);
		tv_versionname = (TextView) findViewById(R.id.tv_splash_versionname);
		linearLayout = (RelativeLayout) findViewById(R.id.linearLayout);
	}

	private void initData() {
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			mVersionCode = packageInfo.versionCode;
			String versionName = packageInfo.versionName;
			//显示版本信息到ui
			tv_versionname.setText(versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 集合动画
	 * 
	 * @author nanamiporkchop
	 * @time 2017-6-17 下午11:20:26
	 */
	private void initAnimation() {
		AnimationSet as = new AnimationSet(false);

		// 旋转动画
		RotateAnimation ra = new RotateAnimation(0.0f, 360.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ra.setDuration(2000);
		ra.setFillAfter(true);

		// 缩放动画
		ScaleAnimation sa = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		sa.setDuration(2000);
		sa.setFillAfter(true);

		// 透明度动画
		AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
		aa.setDuration(2000);
		aa.setFillAfter(true);

		// 添加到集合
		as.addAnimation(aa);
		as.addAnimation(sa);
		as.addAnimation(ra);

		// 目标view设置动画
		linearLayout.setAnimation(as);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

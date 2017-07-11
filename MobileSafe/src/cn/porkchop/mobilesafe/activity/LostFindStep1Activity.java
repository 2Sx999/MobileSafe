package cn.porkchop.mobilesafe.activity;

import cn.porkchop.mobilesafe.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class LostFindStep1Activity extends LostFindBaseActivity {
	@Override
	public void initView() {
		setContentView(R.layout.activity_lostfindstep1);
	}

	@Override
	public void startPreviousPage(View v) {

	}

	@Override
	public void startNextPage(View v) {
		startPage(LostFindStep2Activity.class);
		startNextPageAnimation();
	}
}

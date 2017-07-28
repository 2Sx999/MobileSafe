package cn.porkchop.mobilesafe.view;

import cn.porkchop.mobilesafe.R;
import cn.porkchop.mobilesafe.util.SPUtil;
import cn.porkchop.mobilesafe.util.SettingConstant;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LocationToast {
	private WindowManager mWindowManager;
	private LayoutParams mParams;
	Context mContext;
	private View mView;
	private TextView tv_location;
	protected float downX;
	protected float downY;

	public LocationToast(Context context) {
		mContext = context;
		initData();
	}

	private void initData() {
		mWindowManager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		mParams = new WindowManager.LayoutParams();
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.format = PixelFormat.TRANSLUCENT;
		// 去掉toast原装动画
		mParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		mParams.setTitle("Toast");
		mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		/* | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE */;
		// 设置gravity
		mParams.gravity = Gravity.LEFT | Gravity.TOP;
		mParams.x = SPUtil.getInt(mContext, SettingConstant.FILENAME,
				SettingConstant.PHONELOCATIONTOASTX, 0);
		mParams.y = SPUtil.getInt(mContext, SettingConstant.FILENAME,
				SettingConstant.PHONELOCATIONTOASTY, 0);

	}

	public void show(String location) {
		mView = View.inflate(mContext, R.layout.locationtoast, null);
		tv_location = (TextView) mView.findViewById(R.id.tv_mytoast_location);
		LinearLayout ll_root = (LinearLayout) mView
				.findViewById(R.id.ll_locationtoast_root);
		tv_location.setText(location);
		ll_root.setBackgroundResource(LocationStyleDialog.bgColors[SPUtil
				.getInt(mContext, SettingConstant.FILENAME,
						SettingConstant.LOCATIONSTYLE, 2)]);
		mView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					downX = event.getRawX();
					downY = event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					float x = event.getRawX();
					float y = event.getRawY();
					mParams.x += x - downX;
					mParams.y += y - downY;
					System.out.println(event.getX() + ":" + event.getY());
					if (mParams.x < 0) {
						mParams.x = 0;
					} else if (mParams.x > mWindowManager.getDefaultDisplay()
							.getWidth() - mView.getWidth()) {
						mParams.x = mWindowManager.getDefaultDisplay()
								.getWidth() - mView.getWidth();
					}
					if (mParams.y < 0) {
						mParams.y = 0;
					} else if (mParams.y > mWindowManager.getDefaultDisplay()
							.getHeight() - mView.getHeight()) {
						mParams.y = mWindowManager.getDefaultDisplay()
								.getHeight() - mView.getHeight();
					}
					mWindowManager.updateViewLayout(mView, mParams);
					// 当前位置变为新的起始位置
					downX = x;
					downY = y;
					break;
				case MotionEvent.ACTION_UP:
					SPUtil.putInt(mContext, SettingConstant.FILENAME,
							SettingConstant.PHONELOCATIONTOASTX, mParams.x);
					SPUtil.putInt(mContext, SettingConstant.FILENAME,
							SettingConstant.PHONELOCATIONTOASTY, mParams.y);
					break;
				}
				return true;
			}
		});
		mWindowManager.addView(mView, mParams);
	}

	public void hide() {
		if (mView != null) {
			if (mView.getParent() != null) {
				mWindowManager.removeView(mView);
			}
			mView = null;
		}
	}
}

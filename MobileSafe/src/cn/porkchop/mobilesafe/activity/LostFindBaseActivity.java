package cn.porkchop.mobilesafe.activity;

import cn.porkchop.mobilesafe.R;
import cn.porkchop.mobilesafe.util.DataUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public abstract class LostFindBaseActivity extends Activity {
	private GestureDetector mGD;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initEvent();
		initData();
		initGesture();
	}

	public void initEvent() {
		// TODO Auto-generated method stub
		
	}

	protected void initData() {
		// TODO Auto-generated method stub
		
	}

	private void initGesture() {
		mGD = new GestureDetector(this, new OnGestureListenerWrapper() {
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {

				if (Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY()
						- e2.getY())
						&& Math.abs(velocityX) > 50) {
					// 横着的位移大于竖着的位移
					// 而且横着的移动速度达到50,就换页
					if (velocityX > 0) {
						startPreviousPage(null);
					} else {
						startNextPage(null);
					}
					return true;

				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGD.onTouchEvent(event);
		return true;
	}

	protected void startPage(Class<? extends Context> clazz) {
		Intent intent = new Intent(this, clazz);
		startActivity(intent);
		finish();
	}

	protected abstract void initView();

	protected abstract void startPreviousPage(View v);

	protected abstract void startNextPage(View v);

	/**
	 * 切换到下一个界面的动画
	 * 
	 * @author nanamiporkchop
	 * @time 2017-7-4 上午11:42:45
	 */
	protected void startNextPageAnimation() {
		// 如果界面没切换,就不会执行
		overridePendingTransition(R.anim.nextpage_enter_anim,
				R.anim.nextpage_exit_anim);
	}

	/**
	 * 切换到上一个界面的动画
	 * 
	 * @author nanamiporkchop
	 * @time 2017-7-4 上午11:43:36
	 */
	protected void startPreviousPageAnimation() {
		overridePendingTransition(R.anim.previouspage_enter_anim,
				R.anim.previouspage_exit_anim);
	}

	private class OnGestureListenerWrapper implements OnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			return false;
		}

	}

}

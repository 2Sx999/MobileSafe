package cn.porkchop.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import cn.porkchop.mobilesafe.R;
import cn.porkchop.mobilesafe.dao.BlackListDao;
import cn.porkchop.mobilesafe.db.BlackListDB;
import cn.porkchop.mobilesafe.model.BlackListItem;
import cn.porkchop.mobilesafe.util.DataUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class BlackListActivity extends Activity {
	private static final int LOADCOUNT = 10;
	private static final int SUCCESS = 0;
	protected static final int NODATA = 1;

	private BlackListDao dao = new BlackListDao(this);
	private List<BlackListItem> list = new ArrayList<BlackListItem>();

	private LinearLayout ll_progressbar;
	private ListView lv_data;
	private ImageView iv_nodata;
	private PopupWindow mPopupwindow;
	private ScaleAnimation mPopupWindowAnimation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initEvent();
		initPopupWindow();
		initAddDialog();
		loadMoreAndShowProgressBar(true);
	}

	private void initPopupWindow() {
		mContentView = View.inflate(this, R.layout.popupwindow_blacklist_add,
				null);
		TextView tv_manual = (TextView) mContentView
				.findViewById(R.id.tv_popupwindow_blacklist_manual);
		TextView tv_callog = (TextView) mContentView
				.findViewById(R.id.tv_popupwindow_blacklist_calllog);
		TextView tv_smslog = (TextView) mContentView
				.findViewById(R.id.tv_popupwindow_blacklist_smslog);
		TextView tv_friendlog = (TextView) mContentView
				.findViewById(R.id.tv_popupwindow_blacklist_friendslog);
		// 设置不同的点击事件
		OnClickListener onClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.tv_popupwindow_blacklist_manual:
					et_phone.setText("");
					mAlertDialog.show();
					break;
				case R.id.tv_popupwindow_blacklist_calllog:
					startActivityForResult(new Intent(BlackListActivity.this, CallLogListActivity.class), 0);
					break;
				case R.id.tv_popupwindow_blacklist_smslog:
					startActivityForResult(new Intent(BlackListActivity.this, SmsListActivity.class), 0);
					break;
				case R.id.tv_popupwindow_blacklist_friendslog:
					startActivityForResult(new Intent(BlackListActivity.this, ContactListActivity.class), 0);
					break;
				}
				mPopupwindow.dismiss();
			}
		};
		tv_manual.setOnClickListener(onClickListener);
		tv_callog.setOnClickListener(onClickListener);
		tv_smslog.setOnClickListener(onClickListener);
		tv_friendlog.setOnClickListener(onClickListener);
		mPopupwindow = new PopupWindow(mContentView, 180,
				LayoutParams.WRAP_CONTENT);
		mPopupwindow.setFocusable(true);
		mPopupwindow
				.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		mPopupwindow.setOutsideTouchable(true);
		// 设置动画
		mPopupWindowAnimation = new ScaleAnimation(1.0f, 1.0f, 0.0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.0f);
		mPopupWindowAnimation.setDuration(400);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			String number = data.getStringExtra("number");
			et_phone.setText(number);
			mAlertDialog.show();
		}
	}

	public void AddImageClick(View v) {
		mPopupwindow.showAsDropDown(v);
		mContentView.startAnimation(mPopupWindowAnimation);
	}

	/**
	 * 初始化添加的对话框
	 * 
	 * @author nanamiporkchop
	 * @time 2017-7-16 下午5:44:55
	 */
	private void initAddDialog() {
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.dialog_blacklist_add, null);
		// 获得组件
		et_phone = (EditText) view.findViewById(R.id.et_blacklist_dialog_phone);
		final CheckBox cb_sms = (CheckBox) view
				.findViewById(R.id.cb_blacklist_dialog_sms);
		final CheckBox cb_phone = (CheckBox) view
				.findViewById(R.id.cb_blacklist_dialog_phone);
		Button bt_add = (Button) view
				.findViewById(R.id.bt_blacklist_dialog_add);
		Button bt_cancel = (Button) view
				.findViewById(R.id.bt_blacklist_dialog_cancel);
		bt_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String phone = et_phone.getText().toString();
				if (TextUtils.isEmpty(phone)) {
					DataUtil.showToast(BlackListActivity.this, "号码不能为空");
					return;
				}
				if (cb_phone.isChecked() == false
						&& cb_sms.isChecked() == false) {
					DataUtil.showToast(BlackListActivity.this, "至少选择一种拦截模式");
					return;
				}
				int mode = 0;
				if (cb_phone.isChecked()) {
					mode |= BlackListDB.PHONE_MODE;
				}
				if (cb_sms.isChecked()) {
					mode |= BlackListDB.SMS_MODE;
				}
				dao.insertOrUpdate(phone, mode);
				list.clear();
				loadMoreAndShowProgressBar(true);
				mAlertDialog.dismiss();
				// 因为是复用了alertdialog,所以每次选择后初始化checkbox
				cb_sms.setChecked(false);
				cb_phone.setChecked(false);
			}
		});
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mAlertDialog.dismiss();
				// 初始化checkbox
				cb_sms.setChecked(false);
				cb_phone.setChecked(false);
			}
		});
		ab.setView(view);
		mAlertDialog = ab.create();
	}

	private void initEvent() {
		lv_data.setOnScrollListener(new OnScrollListener() {
			/*
			 * SCROLL_STATE_FLING 惯性滑动（手松开） SCROLL_STATE_TOUCH_SCROLL 按住滑动（手没松开）
			 * SCROLL_STATE_IDLE 停止
			 */
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState != OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
						&& lv_data.getLastVisiblePosition() >= list.size() - 1) {
					loadMoreAndShowProgressBar(false);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
	}

	private void initView() {
		setContentView(R.layout.activity_blacklist);
		iv_nodata = (ImageView) findViewById(R.id.iv_blacklist_nodata);
		lv_data = (ListView) findViewById(R.id.lv_blacklist_data);
		ll_progressbar = (LinearLayout) findViewById(R.id.ll_progressbar_root);

		mMyadapter = new MyAdapter();
		lv_data.setAdapter(mMyadapter);
	}

	/**
	 * 加载更多数据,并且显示加载的进度条
	 * 
	 * @author nanamiporkchop
	 * @time 2017-7-16 下午12:10:06
	 * @param isMoveToFirstItem
	 *            是否移动到第一条数据
	 */
	private void loadMoreAndShowProgressBar(final boolean isMoveToFirstItem) {
		// 显示进度条
		ll_progressbar.setVisibility(View.VISIBLE);
		// 隐藏listview
		lv_data.setVisibility(View.GONE);
		new Thread() {
			public void run() {
				list.addAll(dao.findMore(list.size(), LOADCOUNT));
				if (list.size() == 0) {
					handler.obtainMessage(NODATA).sendToTarget();
				} else {
					SystemClock.sleep(500);
					handler.obtainMessage(SUCCESS, isMoveToFirstItem)
							.sendToTarget();
				}
			}
		}.start();
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SUCCESS:
				ll_progressbar.setVisibility(View.GONE);
				lv_data.setVisibility(View.VISIBLE);
				iv_nodata.setVisibility(View.GONE);
				mMyadapter.notifyDataSetChanged();
				if ((Boolean) msg.obj.equals(true)) {
					lv_data.smoothScrollToPosition(0);
				}
				break;
			case NODATA:
				ll_progressbar.setVisibility(View.GONE);
				lv_data.setVisibility(View.GONE);
				iv_nodata.setVisibility(View.VISIBLE);
				break;
			}
		}
	};
	private MyAdapter mMyadapter;
	private EditText et_phone;
	private AlertDialog mAlertDialog;
	private View mContentView;

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public BlackListItem getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = View.inflate(BlackListActivity.this,
						R.layout.item_blacklist_listview, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_delete = (ImageView) convertView
						.findViewById(R.id.iv_blacklist_item_delete);
				viewHolder.tv_phone = (TextView) convertView
						.findViewById(R.id.tv_blacklist_item_phone);
				viewHolder.tv_mode = (TextView) convertView
						.findViewById(R.id.tv_blacklist_item_mode);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			// 赋值
			final BlackListItem item = getItem(position);
			viewHolder.tv_phone.setText(item.getPhone());
			if (item.getMode() == BlackListDB.ALL_MODE) {
				viewHolder.tv_mode.setText("短信拦截与电话拦截");
			} else if (item.getMode() == BlackListDB.PHONE_MODE) {
				viewHolder.tv_mode.setText("电话拦截");
			} else if (item.getMode() == BlackListDB.SMS_MODE) {
				viewHolder.tv_mode.setText("短信拦截");
			}
			viewHolder.iv_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dao.deleteByPhone(item.getPhone());
					list.remove(item);
					mMyadapter.notifyDataSetChanged();
					Toast.makeText(BlackListActivity.this, "删除成功", 0).show();
				}
			});
			return convertView;
		}
	}

	private class ViewHolder {
		ImageView iv_delete;
		TextView tv_phone;
		TextView tv_mode;
	}

}

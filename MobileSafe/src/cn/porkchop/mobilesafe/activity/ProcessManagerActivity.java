package cn.porkchop.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.porkchop.mobilesafe.R;
import cn.porkchop.mobilesafe.model.AppInfo;
import cn.porkchop.mobilesafe.service.LockScreenClearProcessService;
import cn.porkchop.mobilesafe.util.ProcessInfoUtil;
import cn.porkchop.mobilesafe.util.SPUtil;
import cn.porkchop.mobilesafe.util.ServiceUtil;
import cn.porkchop.mobilesafe.util.SettingConstant;
import cn.porkchop.mobilesafe.view.ProgressTextView;
import cn.porkchop.mobilesafe.view.SettingCenterItem;
import cn.porkchop.mobilesafe.view.SettingCenterItem.OnToggleChangedListener;

public class ProcessManagerActivity extends Activity {
	protected static final int SUCCESS = 0;
	private ProgressTextView progressTextView_memory;
	private ListView lv_processes;
	private TextView tv_category;
	private LinearLayout ll_root;
	private long mAvailMem;
	private long mTotalMem;
	private List<AppInfo> userApplist = new ArrayList<AppInfo>();
	private List<AppInfo> systemApplist = new ArrayList<AppInfo>();
	private MyAdapter mAdapter;
	private SlidingDrawer slidingdrawer_processsetting;
	private ImageView iv_drawerarraw1;
	private ImageView iv_drawerarraw2;
	private SettingCenterItem settingcenteritem_showsystemprocess;
	private SettingCenterItem settingcenteritem_clearprocesswhenlockscreen;
	private AlphaAnimation maa2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
		initAnimation();
		initEvent();
		showUpArrow();
	}

	private void initData() {
		settingcenteritem_clearprocesswhenlockscreen
				.setToggle(ServiceUtil
						.isServiceRunning(getApplicationContext(),
								"cn.porkchop.mobilesafe.service.LockScreenClearProcessService"));
		settingcenteritem_showsystemprocess.setToggle(SPUtil.getBoolean(
				getApplicationContext(), SettingConstant.FILENAME,
				SettingConstant.ISSHOWSYSTEMPROCESS, true));
	}

	private void initAnimation() {
		maa1 = new AlphaAnimation(0.5f, 1.0f);
		maa1.setDuration(500);
		maa1.setRepeatCount(Animation.INFINITE);
		maa2 = new AlphaAnimation(1.0f, 0.5f);
		maa2.setDuration(500);
		maa2.setRepeatCount(Animation.INFINITE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		initDataAndShowProgressBar();
	}

	@SuppressWarnings("deprecation")
	private void initEvent() {
		// 滑动改变类别的文本框内容
		lv_processes.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem <= userApplist.size()) {
					tv_category.setText("用户软件(" + userApplist.size() + ")");
				} else {
					tv_category.setText("系统软件(" + systemApplist.size() + ")");
				}
			}
		});
		// 点击listview改变checkbox状态
		lv_processes.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0 || position == userApplist.size() + 1) {
					return;
				}
				ViewHolder holder = (ViewHolder) view.getTag();
				if (position <= userApplist.size()) {
					userApplist.get(position - 1).setChecked(
							!holder.cb_isclean.isChecked());
				} else {
					systemApplist.get(position - 2 - userApplist.size())
							.setChecked(!holder.cb_isclean.isChecked());
				}
				holder.cb_isclean.setChecked(!holder.cb_isclean.isChecked());
			}

		});
		// 抽屉的事件
		slidingdrawer_processsetting
				.setOnDrawerOpenListener(new OnDrawerOpenListener() {
					@Override
					public void onDrawerOpened() {
						showDownArrow();
					}
				});
		slidingdrawer_processsetting
				.setOnDrawerCloseListener(new OnDrawerCloseListener() {
					@Override
					public void onDrawerClosed() {
						showUpArrow();
					}
				});
		// settingcenteritem的事件
		settingcenteritem_clearprocesswhenlockscreen
				.setOnToggleChangedListener(new OnToggleChangedListener() {

					@Override
					public void onToggleChanged(View v) {
						if (settingcenteritem_clearprocesswhenlockscreen
								.isToggleOn()) {
							Intent intent = new Intent(getApplicationContext(),
									LockScreenClearProcessService.class);
							stopService(intent);
							settingcenteritem_clearprocesswhenlockscreen
									.setToggle(false);
						} else {
							Intent intent = new Intent(getApplicationContext(),
									LockScreenClearProcessService.class);
							startService(intent);
							settingcenteritem_clearprocesswhenlockscreen
									.setToggle(true);
						}
					}
				});
		settingcenteritem_showsystemprocess
				.setOnToggleChangedListener(new OnToggleChangedListener() {

					@Override
					public void onToggleChanged(View v) {
						if (settingcenteritem_showsystemprocess.isToggleOn()) {
							SPUtil.putBoolean(getApplicationContext(),
									SettingConstant.FILENAME,
									SettingConstant.ISSHOWSYSTEMPROCESS, false);
							settingcenteritem_showsystemprocess
									.setToggle(false);
						} else {
							SPUtil.putBoolean(getApplicationContext(),
									SettingConstant.FILENAME,
									SettingConstant.ISSHOWSYSTEMPROCESS, true);
							settingcenteritem_showsystemprocess.setToggle(true);
						}
						mAdapter.notifyDataSetChanged();
					}
				});
	}

	private void showDownArrow() {
		iv_drawerarraw1.clearAnimation();
		iv_drawerarraw2.clearAnimation();
		iv_drawerarraw1.setImageResource(R.drawable.drawer_arrow_down);
		iv_drawerarraw2.setImageResource(R.drawable.drawer_arrow_down);
	}

	private void showUpArrow() {
		iv_drawerarraw1.setImageResource(R.drawable.drawer_arrow_up);
		iv_drawerarraw2.setImageResource(R.drawable.drawer_arrow_up);
		// 设置动画
		iv_drawerarraw1.setAnimation(maa1);
		iv_drawerarraw2.setAnimation(maa2);
	}

	/**
	 * 负责显示listview的数据
	 * 
	 * @author nanamiporkchop
	 * @time 2017-8-7 下午6:52:10
	 */
	private void initDataAndShowProgressBar() {
		ll_root.setVisibility(View.VISIBLE);
		lv_processes.setVisibility(View.GONE);
		new Thread() {
			public void run() {
				List<AppInfo> list = ProcessInfoUtil
						.getAllRunningAppInfo(getApplicationContext());
				// 根据是否是系统应用分类
				userApplist.clear();
				systemApplist.clear();
				for (AppInfo appInfo : list) {
					if (appInfo.isSystem()) {
						systemApplist.add(appInfo);
					} else {
						userApplist.add(appInfo);
					}
				}
				mHandler.obtainMessage(SUCCESS).sendToTarget();
			}
		}.start();
	}

	private void initView() {
		setContentView(R.layout.activity_processmanager);
		progressTextView_memory = (ProgressTextView) findViewById(R.id.progresstextview_processmassage_memory);
		lv_processes = (ListView) findViewById(R.id.lv_processmanager_processes);
		tv_category = (TextView) findViewById(R.id.tv_processmanager_category);
		ll_root = (LinearLayout) findViewById(R.id.ll_progressbar_root);
		slidingdrawer_processsetting = (SlidingDrawer) findViewById(R.id.slidingDrawer_processmanager_processsetting);
		iv_drawerarraw1 = (ImageView) findViewById(R.id.iv_processmanager_drawerarrow1);
		iv_drawerarraw2 = (ImageView) findViewById(R.id.iv_processmanager_drawerarrow2);
		settingcenteritem_showsystemprocess = (SettingCenterItem) findViewById(R.id.settingcenteritem_processmanager_showsystemprocess);
		settingcenteritem_clearprocesswhenlockscreen = (SettingCenterItem) findViewById(R.id.settingcenteritem_processmanager_clearprocesswhenlockscreen);
		// 设置adapter
		mAdapter = new MyAdapter();
		lv_processes.setAdapter(mAdapter);
	}

	/**
	 * 点击clean调用的方法
	 * 
	 * @author nanamiporkchop
	 * @time 2017-8-6 下午5:40:17
	 * @param v
	 */
	public void clean(View v) {
		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		long releasedMemorySize = 0;
		int processCount = 0;
		for (int i = 0; i < userApplist.size(); i++) {
			AppInfo appInfo = userApplist.get(i);
			if (appInfo.getPackName().equals(getPackageName())) {
				continue;
			}
			if (appInfo.isChecked() == true) {
				activityManager.killBackgroundProcesses(appInfo.getPackName());
				userApplist.remove(i--);
				processCount++;
				releasedMemorySize += appInfo.getMemSize();
			}
		}
		// 不显示系统进程的话就不清理选中的系统进程
		if (SPUtil.getBoolean(getApplicationContext(),
				SettingConstant.FILENAME, SettingConstant.ISSHOWSYSTEMPROCESS,
				false)) {
			for (int i = 0; i < systemApplist.size(); i++) {
				AppInfo appInfo = systemApplist.get(i);
				if (appInfo.isChecked() == true) {
					activityManager.killBackgroundProcesses(appInfo
							.getPackName());
					systemApplist.remove(i--);
					processCount++;
					releasedMemorySize += appInfo.getMemSize();
				}
			}
		}
		mAvailMem += releasedMemorySize;
		// 提示用户的toast
		Toast.makeText(
				getApplicationContext(),
				"结束了"
						+ processCount
						+ "个进程,共释放"
						+ Formatter.formatFileSize(getApplicationContext(),
								releasedMemorySize) + "内存", Toast.LENGTH_SHORT)
				.show();
		// 设置进度条
		progressTextView_memory.setText("剩余内存:"
				+ Formatter.formatFileSize(getApplicationContext(), mAvailMem)
				+ "/总内存:"
				+ Formatter.formatFileSize(getApplicationContext(), mTotalMem));
		progressTextView_memory
				.setProgress((int) ((mTotalMem * 1.0 - mAvailMem) / mTotalMem * 100));
		// 设置顶部textview
		tv_category.setText("用户软件(" + userApplist.size() + ")");
		mAdapter.notifyDataSetChanged();
	}

	Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS:
				ll_root.setVisibility(View.GONE);
				lv_processes.setVisibility(View.VISIBLE);
				mAdapter.notifyDataSetChanged();
				MemoryInfo memoryInfo = ProcessInfoUtil
						.getMemoryInfo(getApplicationContext());
				mAvailMem = memoryInfo.availMem;
				mTotalMem = memoryInfo.totalMem;
				// 设置进度条
				progressTextView_memory.setText("剩余内存:"
						+ Formatter.formatFileSize(getApplicationContext(),
								memoryInfo.availMem)
						+ "/总内存:"
						+ Formatter.formatFileSize(getApplicationContext(),
								memoryInfo.totalMem));
				progressTextView_memory
						.setProgress((int) ((memoryInfo.totalMem - memoryInfo.availMem)
								* 1.0 / memoryInfo.totalMem * 100));
				// 设置顶部textview
				tv_category.setText("用户软件(" + userApplist.size() + ")");
				break;
			}
		}
	};
	private AlphaAnimation maa1;

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (SPUtil.getBoolean(getApplicationContext(),
					SettingConstant.FILENAME,
					SettingConstant.ISSHOWSYSTEMPROCESS, true)) {
				return userApplist.size() + 1 + systemApplist.size() + 1;
			} else {
				return userApplist.size() + 1;
			}
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position == 0) {
				TextView textView = new TextView(getApplicationContext());
				textView.setBackgroundColor(Color.parseColor("#33000000"));
				textView.setText("用户软件(" + userApplist.size() + ")");
				return textView;
			} else if (position == userApplist.size() + 1) {
				TextView textView = new TextView(getApplicationContext());
				textView.setBackgroundColor(Color.parseColor("#33000000"));
				textView.setText("系统软件(" + systemApplist.size() + ")");
				return textView;
			} else {
				ViewHolder holder;
				if (convertView == null || convertView instanceof TextView) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.item_processmanager_listview, null);
					holder = new ViewHolder();
					holder.iv_icon = (ImageView) convertView
							.findViewById(R.id.iv_lv_processmanager_icon);
					holder.tv_appname = (TextView) convertView
							.findViewById(R.id.tv_lv_processmanager_appname);
					holder.tv_usingmemory = (TextView) convertView
							.findViewById(R.id.tv_lv_processmanager_usingmemory);
					holder.cb_isclean = (CheckBox) convertView
							.findViewById(R.id.cb_lv_processmanager_isclean);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				// 获得appInfo
				AppInfo appInfo = null;
				if (position <= userApplist.size()) {
					appInfo = userApplist.get(position - 1);
				} else {
					appInfo = systemApplist.get(position - 2
							- userApplist.size());
				}
				holder.tv_appname.setText(appInfo.getAppName());
				holder.iv_icon.setImageDrawable(appInfo.getIcon());
				holder.tv_usingmemory.setText(Formatter.formatFileSize(
						getApplicationContext(), appInfo.getMemSize()));
				holder.cb_isclean.setChecked(appInfo.isChecked());
				// 设置自己隐藏
				if (getPackageName().equals(appInfo.getPackName())) {
					holder.cb_isclean.setVisibility(View.GONE);
				} else {
					holder.cb_isclean.setVisibility(View.VISIBLE);
				}
				return convertView;
			}
		}
	}

	public void selectAll(View v) {
		for (int i = 0; i < userApplist.size(); i++) {
			AppInfo appInfo = userApplist.get(i);
			appInfo.setChecked(true);
		}
		for (int i = 0; i < systemApplist.size(); i++) {
			AppInfo appInfo = systemApplist.get(i);
			appInfo.setChecked(true);
		}
		mAdapter.notifyDataSetChanged();
	}

	public void selectInvert(View v) {
		for (int i = 0; i < userApplist.size(); i++) {
			AppInfo appInfo = userApplist.get(i);
			appInfo.setChecked(!appInfo.isChecked());
		}
		for (int i = 0; i < systemApplist.size(); i++) {
			AppInfo appInfo = systemApplist.get(i);
			appInfo.setChecked(!appInfo.isChecked());
		}
		mAdapter.notifyDataSetChanged();
	}

	private class ViewHolder {
		ImageView iv_icon;
		TextView tv_appname;
		TextView tv_usingmemory;
		CheckBox cb_isclean;
	}
}

package cn.porkchop.mobilesafe.activity;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import com.stericson.RootTools.RootTools;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import cn.porkchop.mobilesafe.R;
import cn.porkchop.mobilesafe.model.AppInfo;
import cn.porkchop.mobilesafe.util.AppInfoUtil;
import cn.porkchop.mobilesafe.util.SPUtil;
import cn.porkchop.mobilesafe.view.ProgressTextView;
import cn.sharesdk.onekeyshare.OnekeyShare;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AppManagerActivity extends Activity {
	protected static final int SUCCESS = 0;
	private MyAdapter mAdapter;
	private ProgressTextView progressTextView_rommess;
	private StickyListHeadersListView lv_apps;
	private List<AppInfo> allApplist = new ArrayList<AppInfo>();
	private List<AppInfo> systemAppList = new ArrayList<AppInfo>();
	private List<AppInfo> userAppList = new ArrayList<AppInfo>();
	private LinearLayout ll_progressbar;
	private PopupWindow mPopupWindow;
	private AppInfo mAppInfo;
	private AppRemoveReceiver mReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initDataAndShowProgressBar();
		initPopupWindow();
		initEvent();
		registerAppRemoveReceiver();
	}

	private void registerAppRemoveReceiver() {
		mReceiver = new AppRemoveReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		registerReceiver(mReceiver, filter);
	}

	private class AppRemoveReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			initDataAndShowProgressBar();
		}
	}

	private void initEvent() {
		lv_apps.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mAppInfo = allApplist.get(position);
				mPopupWindow.showAsDropDown(view, view.getWidth(),
						-view.getHeight());
			}
		});
	}

	private void initPopupWindow() {
		View view = View.inflate(getApplicationContext(),
				R.layout.popupwindow_appmanager_functions, null);
		LinearLayout ll_uninstall = (LinearLayout) view
				.findViewById(R.id.ll_popupwindow_appmanager_uninstall);
		LinearLayout ll_launch = (LinearLayout) view
				.findViewById(R.id.ll_popupwindow_appmanager_launch);
		LinearLayout ll_share = (LinearLayout) view
				.findViewById(R.id.ll_popupwindow_appmanager_share);
		LinearLayout ll_setting = (LinearLayout) view
				.findViewById(R.id.ll_popupwindow_appmanager_setting);
		OnClickListener onClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.ll_popupwindow_appmanager_uninstall:
					if (mAppInfo.isSystem()) {
						// 系统app
						try {
							RootTools.sendShell("mount -o remount rw /system",
									5000);
							RootTools.sendShell(
									"rm -r " + mAppInfo.getSourceDir(), 5000);
							RootTools.sendShell("mount -o remount r /system",
									5000);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						// 用户app
						/*
						 * <intent-filter> <action
						 * android:name="android.intent.action.VIEW" /> <action
						 * android:name="android.intent.action.DELETE" />
						 * <category
						 * android:name="android.intent.category.DEFAULT" />
						 * <data android:scheme="package" /> </intent-filter>
						 */
						Intent intent = new Intent(
								"android.intent.action.DELETE");
						intent.addCategory("android.intent.category.DEFAULT");
						intent.setData(Uri.parse("package:"
								+ mAppInfo.getPackName()));
						startActivity(intent);
					}
					break;
				case R.id.ll_popupwindow_appmanager_launch:
					// 启动app
					Intent launchIntentForPackage = getPackageManager()
							.getLaunchIntentForPackage(mAppInfo.getPackName());
					startActivity(launchIntentForPackage);
					break;
				case R.id.ll_popupwindow_appmanager_share:
					// 分享app
					OnekeyShare oks = new OnekeyShare();
					// 关闭sso授权
					oks.disableSSOWhenAuthorize();
					// 分享时Notification的图标和文字 2.5.9以后的版本不 调用此方法
					// oks.setNotification(R.drawable.ic_launcher,
					// getString(R.string.app_name));
					// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
					oks.setTitle("ShareSDK测试");
					// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
					oks.setTitleUrl("http://porkchop.cn");
					// text是分享文本，所有平台都需要这个字段
					oks.setText("我正在分享" + mAppInfo.getAppName());
					// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
					oks.setImageUrl("http://porkchop.cn/static/userImages/20170727021358.jpg");// 确保SDcard下面存在此张图片
					// url仅在微信（包括好友和朋友圈）中使用
					oks.setUrl("http://porkchop.cn");
					// comment是我对这条分享的评论，仅在人人网和QQ空间使用
					oks.setComment("测试评论文本");
					// site是分享此内容的网站名称，仅在QQ空间使用
					oks.setSite("porkchop的博客");
					// siteUrl是分享此内容的网站地址，仅在QQ空间使用
					oks.setSiteUrl("http://porkchop.cn");
					oks.setFilePath(mAppInfo.getSourceDir());
					// 启动分享GUI
					oks.show(getApplicationContext());
					break;
				case R.id.ll_popupwindow_appmanager_setting:
					Intent intent = new Intent(
							"android.settings.APPLICATION_DETAILS_SETTINGS");
					intent.setData(Uri.parse("package:"
							+ mAppInfo.getPackName()));
					startActivity(intent);
					break;
				}
				mPopupWindow.dismiss();
			}
		};
		// 设置点击事件
		ll_uninstall.setOnClickListener(onClickListener);
		ll_launch.setOnClickListener(onClickListener);
		ll_share.setOnClickListener(onClickListener);
		ll_setting.setOnClickListener(onClickListener);
		mPopupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		// 获取焦点 保证内容可以点击
		mPopupWindow.setFocusable(true);
		// 设置背景
		mPopupWindow
				.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		mPopupWindow.setOutsideTouchable(true);
		// 设置动画样式
		mPopupWindow.setAnimationStyle(R.style.PopupWindow);
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SUCCESS:
				ll_progressbar.setVisibility(View.GONE);
				mAdapter.notifyDataSetChanged();
				progressTextView_rommess.setText("手机可用容量:"
						+ Formatter.formatFileSize(getApplicationContext(),
								AppInfoUtil.getPhoneAvailMem())
						+ "/总容量"
						+ Formatter.formatFileSize(getApplicationContext(),
								AppInfoUtil.getPhoneTotalMem()));
				int romProgress = (int) ((AppInfoUtil.getPhoneTotalMem() * 1.0 - AppInfoUtil
						.getPhoneAvailMem()) / AppInfoUtil.getPhoneTotalMem() * 100);
				progressTextView_rommess.setProgress(romProgress);
				break;
			}
		}
	};

	private void initDataAndShowProgressBar() {
		ll_progressbar.setVisibility(View.VISIBLE);
		new Thread() {
			@Override
			public void run() {
				allApplist.clear();
				systemAppList.clear();
				userAppList.clear();
				allApplist = AppInfoUtil
						.getAllInstalledAppInfo(AppManagerActivity.this);
				// 进行系统app与用户app的分类,配合adapter的显示和使用
				for (AppInfo item : allApplist) {
					if (item.isSystem()) {
						systemAppList.add(item);
					} else {
						userAppList.add(item);
					}
				}
				allApplist.clear();
				allApplist.addAll(userAppList);
				allApplist.addAll(systemAppList);
				mHandler.obtainMessage(SUCCESS).sendToTarget();
			}
		}.start();
	}

	private void initView() {
		setContentView(R.layout.activity_appmanager);
		progressTextView_rommess = (ProgressTextView) findViewById(R.id.progresstextview_appmanager_rommess);
		lv_apps = (StickyListHeadersListView) findViewById(R.id.listview_appmanager_apps);
		ll_progressbar = (LinearLayout) findViewById(R.id.ll_progressbar_root);
		// 设置适配器
		mAdapter = new MyAdapter();
		lv_apps.setAdapter(mAdapter);
	}

	private class MyAdapter extends BaseAdapter implements
			StickyListHeadersAdapter {

		@Override
		public int getCount() {
			return allApplist.size();
		}

		@Override
		public AppInfo getItem(int position) {
			return allApplist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_appmanager_listview, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) convertView
						.findViewById(R.id.iv_lv_appmanager_icon);
				holder.tv_appname = (TextView) convertView
						.findViewById(R.id.tv_lv_appmanager_appname);
				holder.tv_applocation = (TextView) convertView
						.findViewById(R.id.tv_lv_appmanager_applocation);
				holder.tv_appsize = (TextView) convertView
						.findViewById(R.id.tv_lv_appmanager_appsize);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			AppInfo appInfo = getItem(position);
			holder.iv_icon.setImageDrawable(appInfo.getIcon());
			holder.tv_appname.setText(appInfo.getAppName());
			holder.tv_applocation.setText(appInfo.isSD() ? "sd存储" : "rom存储");
			holder.tv_appsize.setText(Formatter.formatFileSize(
					getApplicationContext(), appInfo.getSize()));
			return convertView;
		}

		@Override
		public View getHeaderView(int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = new TextView(getApplicationContext());
				convertView.setBackgroundColor(Color.parseColor("#33000000"));

			}
			AppInfo appInfo = getItem(position);
			if (appInfo.isSystem()) {
				((TextView) convertView).setText("系统软件(" + systemAppList.size()
						+ ")");
			} else {
				((TextView) convertView).setText("用户软件(" + userAppList.size()
						+ ")");
			}
			return convertView;
		}

		@Override
		public long getHeaderId(int position) {
			if (getItem(position).isSystem()) {
				return 0;
			} else {
				return 1;
			}
		}

		private class ViewHolder {
			ImageView iv_icon;
			TextView tv_appname;
			TextView tv_applocation;
			TextView tv_appsize;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
}

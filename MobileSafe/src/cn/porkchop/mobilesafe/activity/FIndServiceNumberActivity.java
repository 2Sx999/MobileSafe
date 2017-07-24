package cn.porkchop.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import cn.porkchop.mobilesafe.R;
import cn.porkchop.mobilesafe.dao.ServiceNumberDao;
import cn.porkchop.mobilesafe.model.ServicePhoneDetail;
import cn.porkchop.mobilesafe.model.ServicePhoneType;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FIndServiceNumberActivity extends Activity {
	protected static final int SUCCESS = 0;
	private LinearLayout ll_progressbar;
	private ExpandableListView elv_servicenumbers;
	private MyAdapter mMyAdapter;
	List<ServicePhoneType> list = new ArrayList<ServicePhoneType>();
	ServiceNumberDao serviceNumberDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initDataAndShowProgressBar();
		initEvent();
	}

	private void initEvent() {
		// 点击后拨打电话
		elv_servicenumbers.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				final ServicePhoneDetail detail = list.get(groupPosition).getList()
						.get(childPosition);

				AlertDialog.Builder ab = new AlertDialog.Builder(
						FIndServiceNumberActivity.this);// context Activity类型
				// 设置不可以取消
				// ab.setCancelable(false);
				ab.setTitle("提醒");
				ab.setMessage("是否要打电话给" + detail.getName() + ":"
						+ detail.getNumber());
				ab.setPositiveButton("拨打", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(Intent.ACTION_CALL);
						intent.setData(Uri.parse("tel:" + detail.getNumber()));
						startActivity(intent);
					}
				}).setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {

					}
				});
				ab.show();// 显示对话框

				return true;
			}
		});
	}

	private void initDataAndShowProgressBar() {
		serviceNumberDao = new ServiceNumberDao(this);
		ll_progressbar.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				list = serviceNumberDao.getAllServicePhoneType();
				SystemClock.sleep(1000);
				Message message = handler.obtainMessage();
				message.what = SUCCESS;
				handler.sendMessage(message);
			}
		}.start();
	}

	private void initView() {
		setContentView(R.layout.activity_findservicenumber);
		ll_progressbar = (LinearLayout) findViewById(R.id.ll_progressbar_root);
		elv_servicenumbers = (ExpandableListView) findViewById(R.id.elv_findservicenumber_servicenumbers);

		mMyAdapter = new MyAdapter();
		elv_servicenumbers.setAdapter(mMyAdapter);
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS:
				ll_progressbar.setVisibility(View.GONE);
				elv_servicenumbers.setVisibility(View.VISIBLE);
				mMyAdapter.notifyDataSetChanged();
				break;
			}
		}
	};

	private class MyAdapter extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {
			return list.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return list.get(groupPosition).getList().size();
		}

		@Override
		public ServicePhoneType getGroup(int groupPosition) {
			return list.get(groupPosition);
		}

		@Override
		public ServicePhoneDetail getChild(int groupPosition, int childPosition) {
			return list.get(groupPosition).getList().get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = new TextView(FIndServiceNumberActivity.this);
				((TextView) convertView).setTextSize(23);
				((TextView) convertView).setTextColor(Color.BLACK);
			}
			ServicePhoneType type = getGroup(groupPosition);
			if (isExpanded) {
				((TextView) convertView).setText("- " + type.getType());
			} else {
				((TextView) convertView).setText("+" + type.getType());
			}
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = new TextView(FIndServiceNumberActivity.this);
				((TextView) convertView).setTextSize(18);
				((TextView) convertView).setTextColor(Color.BLACK);
			}
			ServicePhoneDetail detail = getChild(groupPosition, childPosition);
			((TextView) convertView).setText("\t\t" + detail.getName() + ":"
					+ detail.getNumber());
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}
}

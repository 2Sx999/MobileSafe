package cn.porkchop.mobilesafe.activity;

import java.util.List;

import cn.porkchop.mobilesafe.R;
import cn.porkchop.mobilesafe.dao.ContactDao;
import cn.porkchop.mobilesafe.model.Contact;
import cn.porkchop.mobilesafe.util.DataUtil;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public abstract class BaseListActivity extends ListActivity {
	private ListView listView;
	private MyAdapter myAdapter;
	List<Contact> list;
	private final int SUCCESS = 0;
	private final int NODATA = 1;
	private ProgressDialog progressDialog;;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initEvent();
		initData();
	}

	/**
	 * 绑定点击后回显的号码的事件
	 * 
	 * @author nanamiporkchop
	 * @time 2017-7-9 上午11:59:38
	 */
	public void initEvent() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 获得选中的号码并返回
				Contact contact = list.get(position);
				Intent intent = new Intent();
				intent.putExtra("number", contact.getPhone());
				setResult(0, intent);
				finish();
			}
		});
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Contact getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				// 得到视图
				convertView = View.inflate(BaseListActivity.this,
						R.layout.item_contactlist_listview, null);
				// 得到视图的组件
				viewHolder.tx_name = (TextView) convertView
						.findViewById(R.id.tv_contactlist_name);
				viewHolder.tx_phone = (TextView) convertView
						.findViewById(R.id.tv_contactlist_phone);
				// viewholder与convertview绑定,只需一次获得组件,提高效率
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			// 设置值
			viewHolder.tx_name.setText(getItem(position).getName());
			viewHolder.tx_phone.setText(getItem(position).getPhone());
			return convertView;
		}

		private class ViewHolder {
			TextView tx_name;
			TextView tx_phone;

		}
	}

	/**
	 * 从dao得到联系人数据
	 * 
	 * @author nanamiporkchop
	 * @time 2017-7-9 上午11:59:18
	 */
	public void initData() {
		new Thread() {
			@Override
			public void run() {
				// 得到数据
				list = getListData();
				// 模拟加载2秒
				SystemClock.sleep(800);
				if(list.size()==0){
					myHandler.obtainMessage(NODATA).sendToTarget();
					return;
				}
				myHandler.obtainMessage(SUCCESS).sendToTarget();
				/*
				 * 等价于上面一行 Message msg = Message.obtain(); msg.what=SUCCESS;
				 * myHandler.sendMessage(msg);
				 */
			}
		}.start();
	}

	/**
	 * 得到数据list,由子类实现
	 * 
	 * @author nanamiporkchop
	 * @time 2017-7-8 下午8:25:37
	 * @return
	 */
	public abstract List<Contact> getListData();

	private Handler myHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS:
				// 数据加载成功,取消progressdialog,并且设置适配器到视图中
				progressDialog.dismiss();
				listView.setAdapter(myAdapter);
				break;
			case NODATA:
				//没有数据,直接关闭当前页面并提示
				//progressDialog.dismiss();
				DataUtil.showToast(BaseListActivity.this, "没有数据");
				finish();
			}
		}
	};

	/**
	 * 显示progressdialog
	 * @author nanamiporkchop
	 * @time 2017-7-9 下午12:00:47
	 */
	public void initView() {
		listView = getListView();
		myAdapter = new MyAdapter();
		progressDialog = ProgressDialog.show(this, "注意", "正在拼命加载中");
	}
}

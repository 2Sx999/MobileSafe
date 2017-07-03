package cn.porkchop.mobilesafe.activity;

import com.nineoldandroids.animation.ObjectAnimator;

import cn.porkchop.mobilesafe.R;
import cn.porkchop.mobilesafe.util.DataUtil;
import cn.porkchop.mobilesafe.util.Md5Util;
import cn.porkchop.mobilesafe.util.SPUtil;
import cn.porkchop.mobilesafe.util.SettingConstant;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends Activity {
	private static final String[] titles = new String[] { "手机防盗", "通讯卫士",
			"软件管家", "进程管理", "流量统计", "病毒查杀", "缓存清理", "高级工具" };
	private static final String[] descriptions = new String[] { "手机丢失好找",
			"防骚扰防监听", "方便管理软件", "保持手机通畅", "注意流量超标", "手机安全保障", "手机快步如飞",
			"特性处理更好" };
	private static final int[] icons = new int[] { R.drawable.sjfd,
			R.drawable.srlj, R.drawable.rjgj, R.drawable.jcgl, R.drawable.lltj,
			R.drawable.bdcs, R.drawable.hcql, R.drawable.gjgj };
	private ImageView iv_logo;
	private GridView gv_tools;
	private AlertDialog mAD;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initView();
		initAnimation();
		initData();
		initEvent();
	}

	private void initAnimation() {
		ObjectAnimator animator = ObjectAnimator.ofFloat(iv_logo, // 谁将要执行这个动画
																	// 一般放控件的对象
				"rotationY", // 以x方向为中轴线旋转的动画
				0, 60, 90, 120, 180, 240, 300, 360); // 从0位置，旋转到360位置

		animator.setDuration(2000);
		animator.setRepeatCount(ObjectAnimator.INFINITE);

		animator.start();
	}

	private void initEvent() {
		gv_tools.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:// 手机防盗界面
					if (TextUtils.isEmpty(SPUtil.getString(HomeActivity.this,
							SettingConstant.FILENAME, SettingConstant.PASSWORD,
							""))) {
						// 如果没有设置过密码
						showPasswordDialog(false);
					} else {
						// 如果已经设置过密码
						showPasswordDialog(true);
					}
					break;
				}
			}
		});
	}

	/**
	 * 使用自定义对话框
	 * 
	 * @author nanamiporkchop
	 * @time 2017-7-2 下午10:00:45
	 * @param b
	 *            是否设置过密码
	 */
	protected void showPasswordDialog(final boolean b) {

		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		// 获取自定义对话框的布局文件
		View view = View.inflate(this, R.layout.dialog_setpassword, null);
		final EditText et_password = (EditText) view
				.findViewById(R.id.et_dialog_password);
		final EditText et_repassword = (EditText) view
				.findViewById(R.id.et_dialog_repassword);
		TextView tv_title = (TextView) view.findViewById(R.id.tv_dialog_title);
		Button btn_cancel = (Button) view.findViewById(R.id.bt_dialog_cancel);
		final Button btn_confrim = (Button) view
				.findViewById(R.id.bt_dialog_confirm);

		// 设置过密码,更改标题
		if (b == true) {
			tv_title.setText("输入密码");
			et_repassword.setVisibility(View.GONE);
		}

		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v == btn_confrim) {
					// 点击确定按钮
					if (b == true) {// 设置过密码,判断和以前保存的是否一致
						String password = et_password.getText().toString();
						String truePassword = SPUtil.getString(
								HomeActivity.this, SettingConstant.FILENAME,
								SettingConstant.PASSWORD, "");
						if (Md5Util.getMd5String(password).equals(truePassword)) {
							//密码正确,进入下一个界面
							Intent intent = new Intent(getApplicationContext(), LostFindActivity.class);
							startActivity(intent);
							mAD.dismiss();
						} else {
							DataUtil.showToast(HomeActivity.this, "密码错误");
						}
					} else {// 没设置过密码,就设置密码
						String password = et_password.getText().toString();
						String repassword = et_repassword.getText().toString();
						// 判断有没有输入
						if (TextUtils.isEmpty(password)
								|| TextUtils.isEmpty(repassword)) {
							DataUtil.showToast(HomeActivity.this, "请输入密码");
						} else {
							// 判断输入是否一致
							if (password.equals(repassword)) {
								SPUtil.putString(HomeActivity.this,
										SettingConstant.FILENAME,
										SettingConstant.PASSWORD,
										Md5Util.getMd5String(password));
								DataUtil.showToast(HomeActivity.this, "设置成功");
								mAD.dismiss();
							} else {
								DataUtil.showToast(HomeActivity.this, "密码不一致!");
							}
						}
					}

				} else {
					// 点击取消按钮
					mAD.dismiss();
				}
			}
		};
		// 绑定事件
		btn_cancel.setOnClickListener(listener);
		btn_confrim.setOnClickListener(listener);
		// 因为AlertDialog.Builder没有dissmiss方法,所以需要产生对应的alertdialog,从而使用dismiss方法
		ab.setView(view);
		mAD = ab.create();
		mAD.show();
	}

	private void initView() {
		setContentView(R.layout.activity_home);
		iv_logo = (ImageView) findViewById(R.id.iv_home_logo);
		gv_tools = (GridView) findViewById(R.id.gv_home_tools);
	}

	/**
	 * 设置每个菜单选项的adapter
	 * 
	 * @author nanamiporkchop
	 * @time 2017-7-3 下午12:33:54
	 */
	private void initData() {
		gv_tools.setAdapter(new BaseAdapter() {

			@Override
			public long getItemId(int arg0) {
				return 0;
			}

			@Override
			public Object getItem(int arg0) {
				return null;
			}

			@Override
			public int getCount() {
				return titles.length;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// 得到gridview中的子view
				View view = View.inflate(HomeActivity.this,
						R.layout.item_home_gridview, null);
				// 图标
				ImageView iv_icon = (ImageView) view
						.findViewById(R.id.iv_item_gridview_home_icon);
				// 标题
				TextView tv_title = (TextView) view
						.findViewById(R.id.tv_item_home_gridview_title);
				// 描述
				TextView tv_description = (TextView) view
						.findViewById(R.id.tv_item_home_gridview_description);

				// 赋值数据
				iv_icon.setImageResource(icons[position]);
				tv_title.setText(titles[position]);
				tv_description.setText(descriptions[position]);
				return view;
			}
		});
	}
}

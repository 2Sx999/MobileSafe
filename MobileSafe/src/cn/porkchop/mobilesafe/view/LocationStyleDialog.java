package cn.porkchop.mobilesafe.view;

import javax.crypto.spec.IvParameterSpec;

import cn.porkchop.mobilesafe.R;
import cn.porkchop.mobilesafe.receiver.MyDeviceAdminReceiver;
import cn.porkchop.mobilesafe.util.SPUtil;
import cn.porkchop.mobilesafe.util.SettingConstant;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class LocationStyleDialog extends Dialog {
	public static final String[] styleNames = new String[] { "半透明", "活力橙",
			"卫士蓝", "金属灰", "苹果绿" };
	public static final int[] bgColors = new int[] {
			R.drawable.call_locate_white, R.drawable.call_locate_orange,
			R.drawable.call_locate_blue, R.drawable.call_locate_gray,
			R.drawable.call_locate_green };
	private SettingCenterItem sci_locationStyle;
	private ListView lv_style;

	public LocationStyleDialog(Context context, int theme) {
		super(context, theme);
		Window window = getWindow();
		LayoutParams params = window.getAttributes();
		params.gravity = Gravity.BOTTOM;
		window.setAttributes(params);
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return styleNames.length;
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
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(getContext(),
						R.layout.item_locationstyledialog_listview, null);
				holder = new ViewHolder();
				holder.v_color = convertView
						.findViewById(R.id.v_locationstyledialog_color);
				holder.tv_name = (TextView) convertView
						.findViewById(R.id.tv_locationstyledialog_name);
				holder.iv_select = (ImageView) convertView
						.findViewById(R.id.iv_locationstyledialog_select);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.v_color.setBackgroundResource(bgColors[position]);
			holder.tv_name.setText(styleNames[position]);
			if (position == SPUtil.getInt(getContext(),
					SettingConstant.FILENAME, SettingConstant.LOCATIONSTYLE, 2)) {
				holder.iv_select.setVisibility(View.VISIBLE);
			} else {
				holder.iv_select.setVisibility(View.GONE);
			}
			return convertView;
		}

		private class ViewHolder {
			View v_color;
			TextView tv_name;
			ImageView iv_select;
		}

	}

	public LocationStyleDialog(Context context,
			SettingCenterItem sci_locationStyle) {
		this(context, R.style.LocationStyle);
		this.sci_locationStyle = sci_locationStyle;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initEvent();
	}

	private void initEvent() {
		lv_style.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				SPUtil.putInt(getContext(), SettingConstant.FILENAME,
						SettingConstant.LOCATIONSTYLE, position);
				sci_locationStyle.setDesc("归属地样式("
						+ LocationStyleDialog.styleNames[position] + ")");
				dismiss();
			}
		});
	}

	private void initView() {
		setContentView(R.layout.dialog_locationstyle_view);
		lv_style = (ListView) findViewById(R.id.lv_locationstyle_style);
		MyAdapter adapter = new MyAdapter();
		lv_style.setAdapter(adapter);
	}
}

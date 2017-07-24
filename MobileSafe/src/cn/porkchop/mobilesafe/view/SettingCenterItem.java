package cn.porkchop.mobilesafe.view;

import cn.porkchop.mobilesafe.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SettingCenterItem extends RelativeLayout {

	private View itemView;
	private TextView tv_desc;
	private ImageView iv_toggle;
	OnToggleChangedListener onToggleChangedListener;
	private boolean mIsShowToggle;

	public interface OnToggleChangedListener {
		void onToggleChanged(View v);
	}

	public void setOnToggleChangedListener(
			OnToggleChangedListener onToggleChangedListener) {
		this.onToggleChangedListener = onToggleChangedListener;
	}

	public SettingCenterItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
		initData(attrs);
		initEvent(context);
	}

	private void initEvent(Context context) {
		// 点击以后,调用方法
		itemView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onToggleChangedListener != null) {
					onToggleChangedListener
							.onToggleChanged(SettingCenterItem.this);
				}
			}
		});
	}

	private void initData(AttributeSet attrs) {
		// 从布局文件取出值
		String desc = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/cn.porkchop.mobilesafe",
				"desc");
		String bgNumber = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/cn.porkchop.mobilesafe",
				"bgSelector");
		// 赋值到自定义控件的view中
		tv_desc.setText(desc);
		switch (Integer.parseInt(bgNumber)) {
		case 0:// first
			itemView.setBackgroundResource(R.drawable.iv_first_selector);
			break;
		case 1:// middle
			itemView.setBackgroundResource(R.drawable.iv_middle_selector);
			break;
		case 2:// last
			itemView.setBackgroundResource(R.drawable.iv_last_selector);
			break;
		}
		mIsShowToggle = attrs.getAttributeBooleanValue(
				"http://schemas.android.com/apk/res/cn.porkchop.mobilesafe",
				"isShowToggle", true);
		if (!mIsShowToggle) {
			iv_toggle.setVisibility(View.GONE);
		}
	}

	private void initView() {
		itemView = View.inflate(getContext(),
				R.layout.item_custumized_view_settingcenter, this);
		tv_desc = (TextView) itemView
				.findViewById(R.id.tv_settingcenter_item_desc);
		iv_toggle = (ImageView) itemView
				.findViewById(R.id.iv_settingcenter_item_toggle);
	}

	/**
	 * 开关是否开启,如果设置不显示开关,直接返回false
	 * 
	 * @author nanamiporkchop
	 * @time 2017-7-23 下午2:57:47
	 * @return
	 */
	public boolean isToggleOn() {
		if (!mIsShowToggle) {
			return false;
		}
		if (((Integer) iv_toggle.getTag()).equals(R.drawable.on)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 设置是否开启功能,只有设置显示开关才执行
	 * 
	 * @author nanamiporkchop
	 * @time 2017-7-18 下午6:22:05
	 * @param b
	 */
	public void setToggle(boolean b) {
		if (mIsShowToggle) {
			if (b) {
				iv_toggle.setTag(R.drawable.on);
				iv_toggle.setImageDrawable(getResources().getDrawable(
						R.drawable.on));
			} else {
				iv_toggle.setTag(R.drawable.off);
				iv_toggle.setImageDrawable(getResources().getDrawable(
						R.drawable.off));

			}
		}
	}
}

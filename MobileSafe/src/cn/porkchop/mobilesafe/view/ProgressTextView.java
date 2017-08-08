package cn.porkchop.mobilesafe.view;

import cn.porkchop.mobilesafe.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProgressTextView extends RelativeLayout {

	private ProgressBar pb_progress;
	private TextView tv_message;

	public ProgressTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {
		View rootView = View.inflate(getContext(),
				R.layout.view_customized_messageprogress, this);
		tv_message = (TextView) rootView
				.findViewById(R.id.tv_progresstext_message);
		pb_progress = (ProgressBar) rootView
				.findViewById(R.id.pb_progresstext_progressscale);
		pb_progress.setMax(100);
	}
	
	public void setProgress(int progress){
		pb_progress.setProgress(progress);
	}
	
	public void setText(String text){
		tv_message.setText(text);
	}
}

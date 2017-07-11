package cn.porkchop.mobilesafe.util;

import java.util.TooManyListenersException;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class DataUtil {
	public static void showDebugInfo(final Activity context, final String info) {
		context.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(context, info, 0).show();
			}
		});

		System.out.println(info);
	}

	public static void showToast(final Activity context, final String info) {
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, info, 0).show();
			}
		});
	}

}

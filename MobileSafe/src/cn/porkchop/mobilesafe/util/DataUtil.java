package cn.porkchop.mobilesafe.util;

import java.util.TooManyListenersException;

import android.content.Context;
import android.widget.Toast;

public class DataUtil {
	public static void showDebugInfo(Context context, String info) {
		Toast.makeText(context, info, 0).show();
		System.err.println("info");
	}
	
}

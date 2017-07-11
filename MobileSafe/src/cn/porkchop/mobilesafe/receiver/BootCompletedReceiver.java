package cn.porkchop.mobilesafe.receiver;

import cn.porkchop.mobilesafe.service.LostFindService;
import cn.porkchop.mobilesafe.util.SPUtil;
import cn.porkchop.mobilesafe.util.SettingConstant;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;

public class BootCompletedReceiver extends BroadcastReceiver {

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		if (SPUtil.getBoolean(context, SettingConstant.FILENAME,
				SettingConstant.ISBOOTWITHSERVICE, false)) {
			// 如果设置为开机自动启动服务
			Intent service = new Intent(context, LostFindService.class);
			context.startService(service);
		}else{
			return;
		}
		TelephonyManager tm = (TelephonyManager)context.getSystemService(context.TELEPHONY_SERVICE);
		String simNumber = tm.getSimSerialNumber();
		if(!SPUtil.getString(context, SettingConstant.FILENAME, SettingConstant.SIMNUMBER, "").equals(simNumber)){
			//sim卡与原来的卡不一样,就发短信
			String safeNumber = SPUtil.getString(context, SettingConstant.FILENAME, SettingConstant.SAFENUMBER, "110");
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(safeNumber, null, "sim has changed", null, null);
		}
	}

}

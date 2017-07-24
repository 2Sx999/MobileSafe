package cn.porkchop.mobilesafe.service;

import cn.porkchop.mobilesafe.dao.AddressDao;
import cn.porkchop.mobilesafe.util.DataUtil;
import cn.porkchop.mobilesafe.view.MyToast;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class ShowIncomingNumberLocationService extends Service {

	private TelephonyManager mTelephonyManager;
	private PhoneStateListener listener;
	AddressDao addressDao;
	private MyToast mToast;
	private OutCallReceiver mReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		addressDao = new AddressDao(getApplicationContext());
		registerPhoneStateListener();
		registerOutCallReceiver();
		mToast = new MyToast(getApplicationContext());
	}

	private void registerOutCallReceiver() {
		mReceiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(mReceiver, filter);
	}

	private void registerPhoneStateListener() {
		mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				switch (state) {
				case TelephonyManager.CALL_STATE_IDLE:// 一注册电话监听,就会执行
					mToast.hide();
					break;
				case TelephonyManager.CALL_STATE_RINGING:
					String location = addressDao.getLocation(incomingNumber);
					mToast.show(location);
					break;
				}
			}
		};
		mTelephonyManager
				.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	private class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String number = intent.getStringExtra(intent.EXTRA_PHONE_NUMBER);
			mToast.show(addressDao.getLocation(number));
		}
		
	}

	@Override
	public void onDestroy() {
		mTelephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE);
		unregisterReceiver(mReceiver);
	}
}

package com.example.bluetoothcaller.Activity;

import com.example.bluetoothcaller.Service.onHFPCurrentCall;
import com.example.bluetoothcaller.Service.onHFPNumber;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OutgoingFinishActivity extends Activity implements onHFPNumber {

	private Button mHangUp;
	private TextView mNumber;
	private TextView mName;
	private TextView mTime;
	private static final int MESSAGE_GET_NAME =					0x0001;
	private BtApplication mBtApplication;
	private Context mContext;
	
	private Handler mHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case MESSAGE_GET_NAME:
				Intent home = new Intent(mContext, MainActivity.class);
				home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(home);
				break;
			};
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth_outcomingfinish);
		mBtApplication = (BtApplication)getApplication();
		mContext = this;
		findViewById(R.id.outgoing_hang_up_btn).setVisibility(View.GONE);
		mNumber = (TextView) findViewById(R.id.outgoingfinish_phone_number);
		mName = (TextView) findViewById(R.id.outgoingfinish_name);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (mBtApplication.mBluetoothCallService != null) {
			mBtApplication.mBluetoothCallService.registerHFPInterface(this);
			mBtApplication.mBluetoothCallService.requestForLastCallNumber();
			int sec = mBtApplication.mBluetoothCallService.requestLastDuringTime();
			if (sec/3600%60 == 0) {
				mNumber.setText(String.format("%02d:%02d", sec/60%60, sec%60));
			} else {
				mNumber.setText(String.format("%02d:%02d:%02d", sec/3600%60, sec/60%60, sec%60));
			}
			Message msg = new Message();
			msg.what = MESSAGE_GET_NAME;
			mHandler.sendMessageDelayed(msg, 2000);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (mBtApplication.mBluetoothCallService != null) {
			mBtApplication.mBluetoothCallService.unregisterHFPCurrent();
		}
	}

	@Override
	public void onHFPNumber(String number, String name) {
		// TODO Auto-generated method stub
		Log.d("onflate", "onHFPNumber finish" + number + "?" +  " name" + name);
		if (mNumber != null) {
			mNumber.setText(number);
		}
		
		if (mName != null) {
			mName.setText(name);
		}
	}
}

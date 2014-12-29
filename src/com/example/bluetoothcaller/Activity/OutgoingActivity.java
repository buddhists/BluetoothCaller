package com.example.bluetoothcaller.Activity;

import com.example.bluetoothcaller.Service.onHFPCurrentCall;
import com.example.bluetoothcaller.Service.onHFPNumber;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OutgoingActivity extends Activity implements onHFPNumber {

	private Button mHangUp;
	private TextView mNumber;
	private TextView mName;
	private static final int MESSAGE_GET_NAME =					0x0001;
	private BtApplication mBtApplication;
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.outgoing_hang_up_btn:
				if (mBtApplication.mBluetoothCallService != null) {
					mBtApplication.mBluetoothCallService.CancelCall();
				}
				break;
			}
		}
	};
	
	private Handler mHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case MESSAGE_GET_NAME:
				mBtApplication.mBluetoothCallService.requestForCurrentCallNumber();
				break;
			};
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth_outcoming);
		mBtApplication = (BtApplication)getApplication();
		mHangUp = (Button) findViewById(R.id.outgoing_hang_up_btn);
		mNumber = (TextView) findViewById(R.id.outgoing_phone_number);
		mName = (TextView) findViewById(R.id.outgoing_name);
		mHangUp.setOnClickListener(mOnClickListener);

		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (mBtApplication.mBluetoothCallService != null) {
			Log.d("onflate", "service is not null");
			mBtApplication.mBluetoothCallService.registerHFPInterface(this);
			mBtApplication.mBluetoothCallService.requestForCurrentCallNumber();
			Message msg = new Message();
			msg.what = MESSAGE_GET_NAME;
			mHandler.sendMessageDelayed(msg, 100);
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
	public void onHFPNumber(String name, String num) {
		// TODO Auto-generated method stub
		Log.d("onflate","name"+name+"num"+num);
		if (mNumber != null) {
			mNumber.setText(num);
		}
		
		if (mName != null) {
			mName.setText(name);
		}
	}

}

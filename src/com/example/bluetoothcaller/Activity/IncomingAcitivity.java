package com.example.bluetoothcaller.Activity;

import com.example.bluetoothcaller.Service.onHFPNumber;

import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

public class IncomingAcitivity extends Activity implements onHFPNumber {

	private TextView mName;
	private TextView mNumber;
	private Button mHangUp;
	private Button mAnswer;
	private Context mContext;
	private BtApplication mBtApplication;
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.hang_up_btn:
				mBtApplication.mBluetoothCallService.CancelCall();
				break;
			case R.id.answer_btn:
				mBtApplication.mBluetoothCallService.AnswerCall();
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mBtApplication = (BtApplication)getApplication();
		setContentView(R.layout.bluetooth_incoming);
		mName = (TextView) findViewById(R.id.name);
		mNumber = (TextView) findViewById(R.id.phone_number);
		mHangUp = (Button) findViewById(R.id.hang_up_btn);
		mAnswer = (Button) findViewById(R.id.answer_btn);
		mHangUp.setOnClickListener(mOnClickListener);
		mAnswer.setOnClickListener(mOnClickListener);

	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (mBtApplication.mBluetoothCallService != null) {
			Log.d("onflate", "service is not null");
			mBtApplication.mBluetoothCallService.registerHFPInterface(this);
			mBtApplication.mBluetoothCallService.requestForCurrentCallNumber();
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (mBtApplication.mBluetoothCallService != null) {
			mBtApplication.mBluetoothCallService.unregisterHFPInterface();
		}
	}
	
	@Override
	public void onHFPNumber(String number, String name) {
		// TODO Auto-generated method stub
		if (mNumber != null) {
			mNumber.setText(number);
		}
		
		if (mName != null) {
			mName.setText(name);
		}
	}
	
	
}

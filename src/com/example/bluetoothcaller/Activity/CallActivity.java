package com.example.bluetoothcaller.Activity;

import com.example.bluetoothcaller.Service.onHFPCurrentCall;
import com.example.bluetoothcaller.Service.onHFPNumber;
import com.example.bluetoothcaller.View.KeyboardView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class CallActivity extends Activity implements onHFPCurrentCall{

	private TextView mTimer;
	private TextView mName;
	private KeyboardView mKeyboardView;
	private BtApplication mBtApplication;
	private LinearLayout mLinearLayout;
	private boolean isKeyShow = false;
	private ImageView mImageView;
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.end_call_btn:
				if (mBtApplication.mBluetoothCallService != null) {
					mBtApplication.mBluetoothCallService.CancelCall();
				}
				break;
			case R.id.voice_btn:
				if (mBtApplication.mBluetoothCallService != null) {
					if (mBtApplication.mBluetoothCallService.isSCOConnected()) 
						v.setBackgroundResource(R.drawable.bluetooth_voice_cellphone);
					else
						v.setBackgroundResource(R.drawable.bluetooth_voice_vehicle);
					mBtApplication.mBluetoothCallService.TransferVoice();
				}
				break;
			case R.id.keyboard_btn:
				if (mLinearLayout != null) {
					if (isKeyShow) {
						isKeyShow = false;
						mLinearLayout.removeAllViews();
						mLinearLayout.addView(mImageView);
					} else {
						isKeyShow = true;
						mLinearLayout.removeAllViews();
						//LinearLayout.LayoutParams layoutTop = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
//								LayoutParams.MATCH_PARENT);
						mLinearLayout.addView(mKeyboardView.createView(R.layout.bluetooth_main_keyboard));
						mKeyboardView.setDTMF();
					}
				}
				break;
			}
		}
	};
	
	private Handler mHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case R.id.key0_id:
				if (mBtApplication.mBluetoothCallService != null) {
					mBtApplication.mBluetoothCallService.DTMF('0');
				}
				break;
			case R.id.key1_id:
				if (mBtApplication.mBluetoothCallService != null) {
					mBtApplication.mBluetoothCallService.DTMF('1');
				}
				break;
			case R.id.key2_id:
				if (mBtApplication.mBluetoothCallService != null) {
					mBtApplication.mBluetoothCallService.DTMF('2');
				}
				break;
			case R.id.key3_id:
				if (mBtApplication.mBluetoothCallService != null) {
					mBtApplication.mBluetoothCallService.DTMF('3');
				}
				break;
			case R.id.key4_id:
				if (mBtApplication.mBluetoothCallService != null) {
					mBtApplication.mBluetoothCallService.DTMF('4');
				}
				break;
			case R.id.key5_id:
				if (mBtApplication.mBluetoothCallService != null) {
					mBtApplication.mBluetoothCallService.DTMF('5');
				}
				break;
			case R.id.key6_id:
				if (mBtApplication.mBluetoothCallService != null) {
					mBtApplication.mBluetoothCallService.DTMF('6');
				}
				break;
			case R.id.key7_id:
				if (mBtApplication.mBluetoothCallService != null) {
					mBtApplication.mBluetoothCallService.DTMF('7');
				}
				break;
			case R.id.key8_id:
				if (mBtApplication.mBluetoothCallService != null) {
					mBtApplication.mBluetoothCallService.DTMF('8');
				}
				break;
			case R.id.key9_id:
				if (mBtApplication.mBluetoothCallService != null) {
					mBtApplication.mBluetoothCallService.DTMF('9');
				}
				break;
			case R.id.key10_id:
				if (mBtApplication.mBluetoothCallService != null) {
					mBtApplication.mBluetoothCallService.DTMF('*');
				}
				break;
			case R.id.key11_id:
				if (mBtApplication.mBluetoothCallService != null) {
					mBtApplication.mBluetoothCallService.DTMF('#');
				}
				break;
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth_caller);
		findViewById(R.id.end_call_btn).setOnClickListener(mOnClickListener);
		findViewById(R.id.voice_btn).setOnClickListener(mOnClickListener);
		findViewById(R.id.keyboard_btn).setOnClickListener(mOnClickListener);
		mTimer = (TextView) findViewById(R.id.timer);
		mName = (TextView) findViewById(R.id.call_name);
		mKeyboardView = new KeyboardView(this, mBtApplication, mHandler);
		mLinearLayout = (LinearLayout)findViewById(R.id.call_keyboard);
		mImageView = (ImageView)findViewById(R.id.iconCaller);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (mBtApplication.mBluetoothCallService != null) {
			Log.d("onflate", "service is not null");
			mBtApplication.mBluetoothCallService.registerHFPCurrent(this);
			mBtApplication.mBluetoothCallService.requestForCurrentCallNumber();
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
	public void onHFPNumber(String name) {
		// TODO Auto-generated method stub
		if (mName != null) {
			mName.setText(name);
		}
	}

	@Override
	public void onHFPDuringTime(int sec) {
		// TODO Auto-generated method stub
		if (mTimer != null) {
			mTimer.setText(String.format("%02d:%02d:%02d", sec/3600%60, sec/60%60, sec%60));
		}
	}
	

}

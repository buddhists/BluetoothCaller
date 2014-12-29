package com.example.bluetoothcaller.View;

import com.example.bluetoothcaller.Activity.BtApplication;
import com.example.bluetoothcaller.Activity.Constants;
import com.example.bluetoothcaller.Activity.IncomingAcitivity;
import com.example.bluetoothcaller.Activity.OutgoingActivity;
import com.example.bluetoothcaller.Activity.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


public class KeyboardView extends InflateView{
	
	private final static String TAG = "InflateView";
	private StringBuffer mNumber;
	private TextView mTelNum;
	private TextView mTelName;
	private float mNumSize = 0;
	private static final int MESSAGE_FIND_NAME = 0x0001;
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.key0_id:
				mNumber.append('0');
				if (mHandler != null) {
					mHandler.sendEmptyMessage(R.id.key0_id);
				}
				mFindHandler.sendEmptyMessage(MESSAGE_FIND_NAME);
				break;
			case R.id.key1_id:
				mNumber.append('1');
				if (mHandler != null) {
					mHandler.sendEmptyMessage(R.id.key1_id);
				}
				mFindHandler.sendEmptyMessage(MESSAGE_FIND_NAME);
				break;
			case R.id.key2_id:
				mNumber.append('2');
				if (mHandler != null) {
					mHandler.sendEmptyMessage(R.id.key2_id);
				}
				mFindHandler.sendEmptyMessage(MESSAGE_FIND_NAME);
				break;
			case R.id.key3_id:
				mNumber.append('3');
				if (mHandler != null) {
					mHandler.sendEmptyMessage(R.id.key3_id);
				}
				mFindHandler.sendEmptyMessage(MESSAGE_FIND_NAME);
				break;
			case R.id.key4_id:
				mNumber.append('4');
				if (mHandler != null) {
					mHandler.sendEmptyMessage(R.id.key4_id);
				}
				mFindHandler.sendEmptyMessage(MESSAGE_FIND_NAME);
				break;
			case R.id.key5_id:
				mNumber.append('5');
				if (mHandler != null) {
					mHandler.sendEmptyMessage(R.id.key5_id);
				}
				mFindHandler.sendEmptyMessage(MESSAGE_FIND_NAME);
				break;
			case R.id.key6_id:
				mNumber.append('6');
				if (mHandler != null) {
					mHandler.sendEmptyMessage(R.id.key6_id);
				}
				mFindHandler.sendEmptyMessage(MESSAGE_FIND_NAME);
				break;
			case R.id.key7_id:
				mNumber.append('7');
				if (mHandler != null) {
					mHandler.sendEmptyMessage(R.id.key7_id);
				}
				mFindHandler.sendEmptyMessage(MESSAGE_FIND_NAME);
				break;
			case R.id.key8_id:
				mNumber.append('8');
				if (mHandler != null) {
					mHandler.sendEmptyMessage(R.id.key8_id);
				}
				mFindHandler.sendEmptyMessage(MESSAGE_FIND_NAME);
				break;
			case R.id.key9_id:
				mNumber.append('9');
				if (mHandler != null) {
					mHandler.sendEmptyMessage(R.id.key9_id);
				}
				mFindHandler.sendEmptyMessage(MESSAGE_FIND_NAME);
				break;
			case R.id.key10_id:
				mNumber.append('*');
				if (mHandler != null) {
					mHandler.sendEmptyMessage(R.id.key10_id);
				}
				mFindHandler.sendEmptyMessage(MESSAGE_FIND_NAME);
				break;
			case R.id.key11_id:
				mNumber.append('#');
				if (mHandler != null) {
					mHandler.sendEmptyMessage(R.id.key11_id);
				}
				mFindHandler.sendEmptyMessage(MESSAGE_FIND_NAME);
				break;
			case R.id.del_id:
				int len = mNumber.length();
				if (len > 0) {
					mNumber.deleteCharAt(len-1);
				}
				mFindHandler.sendEmptyMessage(MESSAGE_FIND_NAME);
				break;
			case R.id.keyboard_dail:
				/*
				Intent intentCall = new Intent(mContext, OutgoingActivity.class);
				intentCall.putExtra(Constants.KEY_CALLER_NUMBER, mNumber.toString());
				intentCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intentCall);*/
				if (mBtApplication.mBluetoothCallService != null && (mNumber.length() != 0)) {
					mBtApplication.mBluetoothCallService.Dail(mNumber.toString());
				}
				break;
			}
		}
	};
	
	private Handler mFindHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_FIND_NAME:
				findNameByNumber();
				break;
			}
		}
	};
	
	private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.del_id:
				int len = mNumber.length();
				if (len > 0) {
					mNumber.delete(0, len);
					mTelNum.setText(mNumber);
				}
				break;
			}
			return true;
		}
		
	};
	
	
	
	public KeyboardView(Context context, BtApplication arg, Handler arg1) {
		super(context, arg, arg1);
		mNumber = new StringBuffer();
	}
	
	protected void initComponent() {
		mView.findViewById(R.id.key0_id).setOnClickListener(mOnClickListener);
		mView.findViewById(R.id.key1_id).setOnClickListener(mOnClickListener);
		mView.findViewById(R.id.key2_id).setOnClickListener(mOnClickListener);
		mView.findViewById(R.id.key3_id).setOnClickListener(mOnClickListener);
		mView.findViewById(R.id.key4_id).setOnClickListener(mOnClickListener);
		mView.findViewById(R.id.key5_id).setOnClickListener(mOnClickListener);
		mView.findViewById(R.id.key6_id).setOnClickListener(mOnClickListener);
		mView.findViewById(R.id.key7_id).setOnClickListener(mOnClickListener);
		mView.findViewById(R.id.key8_id).setOnClickListener(mOnClickListener);
		mView.findViewById(R.id.key9_id).setOnClickListener(mOnClickListener);
		mView.findViewById(R.id.key10_id).setOnClickListener(mOnClickListener);
		mView.findViewById(R.id.key11_id).setOnClickListener(mOnClickListener);
		mView.findViewById(R.id.del_id).setOnClickListener(mOnClickListener);
		mView.findViewById(R.id.del_id).setOnLongClickListener(mOnLongClickListener);
		mView.findViewById(R.id.keyboard_dail).setOnClickListener(mOnClickListener);
		mTelNum = (TextView) mView.findViewById(R.id.input_id);
		mNumSize = mTelNum.getTextSize();
		mTelName = (TextView) mView.findViewById(R.id.keyboard_dail_name);
	}
	
	private void findNameByNumber() {
		int len = mNumber.length();
		if (len > 17) {
			mNumber.delete(17, len);
		}
		
		Paint paint = mTelNum.getPaint();
		float size = 0;
		float textsize = mTelNum.getTextSize();
		//while (true) {
			size = paint.measureText(mNumber.toString());
			Log.d("onflate", " sz " + size);
			if (size >= mTelNum.getWidth()) {
				textsize -= 3;
				Log.d("onflate", " 1sz " + textsize);
				paint.setTextSize(textsize);
				mTelNum.setTextSize(textsize);
				//break;
			} else {
				textsize++;
				if (textsize > mNumSize) {
					textsize = mNumSize;
				}
			}
		//}
		//mTelNum.setTextSize(size);
		if (len > 17) {
			mTelNum.setText(mNumber.toString()+"...");
		} else {
			mTelNum.setText(mNumber);
		}
		Log.d("onflate", "size " + size + " " + mTelNum.getWidth() + " " + textsize);
		if (mBtApplication.mBluetoothCallService != null) {
			/*String name = mBtApplication.mBluetoothCallService.getContactName(mNumber.toString());
			Log.d("onflate", "" + name);
			if (!mNumber.toString().equals(name)) {
				mTelName.setText(name);
			} else {
				mTelName.setText(null);
			}*/
		}
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		
	}
	
	public void setDTMF() {
		mTelNum.setTextSize(20);
		mView.findViewById(R.id.keyboard_dail).setVisibility(View.GONE);
		mView.findViewById(R.id.keyboard_dail_name).setVisibility(View.GONE);
		mView.findViewById(R.id.del_id).setVisibility(View.GONE);
		((LinearLayout.LayoutParams)mView.findViewById(R.id.key_row_1).getLayoutParams()).setMargins(10, 10, 0, 10);
		((LinearLayout.LayoutParams)mView.findViewById(R.id.key_row_2).getLayoutParams()).setMargins(10, 0, 0, 10);
		((LinearLayout.LayoutParams)mView.findViewById(R.id.key_row_3).getLayoutParams()).setMargins(10, 0, 0, 10);
		((LinearLayout.LayoutParams)mView.findViewById(R.id.key_row_4).getLayoutParams()).setMargins(10, 0, 0, 0);
	}
	
	public void setNormal() {
		mTelNum.setTextSize(45);
		mView.findViewById(R.id.keyboard_dail).setVisibility(View.VISIBLE);
		mView.findViewById(R.id.keyboard_dail_name).setVisibility(View.VISIBLE);
	}
}

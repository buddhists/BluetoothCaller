package com.example.bluetoothcaller.View;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.bluetoothcaller.Activity.BtApplication;
import com.example.bluetoothcaller.Activity.R;

public class UnconnnectedView extends InflateView {

	private TextView mDeviceName;
	private String mName = null;
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()) {
			case R.id.unconnected_click_here:
				mHandler.sendEmptyMessage(v.getId());
				break;
			}
		}
		
	};
	
	public UnconnnectedView(Context context, BtApplication arg, Handler arg1) {
		super(context, arg, arg1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initComponent() {
		// TODO Auto-generated method stub
		mView.getBackground().setAlpha(200);
		mView.setClickable(true);
		mView.findViewById(R.id.unconnected_click_here).setOnClickListener(mOnClickListener);
		mDeviceName = (TextView) mView.findViewById(R.id.unconnected_device_name);
	}
	
	public void updateName() {
		if (mBtApplication.mBluetoothCallService != null) {
			mName = mBtApplication.mBluetoothCallService.getBluetoothName();
		}
		Log.d("onflate","Name is " + mName);
		if (mDeviceName != null) {
			mDeviceName.setText(mContext.getResources().getString(R.string.device_name) + mName);
		}
	}

}

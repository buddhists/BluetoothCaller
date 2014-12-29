package com.example.bluetoothcaller.View;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.bluetoothcaller.Activity.BtApplication;
import com.example.bluetoothcaller.Activity.R;

public class UnKnowView extends InflateView {
	
	private TextView mTitle;

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()) {
			case R.id.know_text:
				mHandler.sendEmptyMessage(v.getId());
				break;
			}
		}
		
	};
	
	public UnKnowView(Context context, BtApplication arg, Handler arg1) {
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
		mView.setClickable(true);
		mView.findViewById(R.id.know_text).setOnClickListener(mOnClickListener);
		mTitle = (TextView)mView.findViewById(R.id.unknown_title);
	}

	public void updateName() {
		String name = null;
		if (mBtApplication.mBluetoothCallService != null) {
			name = mBtApplication.mBluetoothCallService.getBluetoothName();
		}
		Log.d("onflate","Name is " + name);
		if (mTitle != null) {
			mTitle.setText(mContext.getResources().getString(R.string.unknown_text_I) + name 
							+mContext.getResources().getString(R.string.unknown_text_II));
		}
	}
	
}

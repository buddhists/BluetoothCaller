package com.example.bluetoothcaller.View;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.example.bluetoothcaller.Activity.BtApplication;
import com.example.bluetoothcaller.Activity.R;

public class NotifyView extends InflateView {

	private TextView mContent;
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()) {
			case R.id.cancel_btn:
				mHandler.sendEmptyMessage(R.id.cancel_btn);
				break;
			case R.id.confirm_btn:
				mHandler.sendEmptyMessage(R.id.confirm_btn);
				break;
			}
		}
		
	};
	
	public NotifyView(Context context, BtApplication arg, Handler arg1) {
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
		mContent = (TextView)mView.findViewById(R.id.notify_context);
		mView.findViewById(R.id.cancel_btn).setOnClickListener(mOnClickListener);
		mView.findViewById(R.id.confirm_btn).setOnClickListener(mOnClickListener);
	}

	public void setContext(String arg) {
		if (mContent != null && arg != null) {
			mContent.setText(arg);
		}
	}
	
}

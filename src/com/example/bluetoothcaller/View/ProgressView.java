package com.example.bluetoothcaller.View;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.bluetoothcaller.Activity.BtApplication;
import com.example.bluetoothcaller.Activity.R;

public class ProgressView extends InflateView implements onPimProgress {

	private ProgressBar mProgressBar;
	private TextView mName;
	private final static int ORIGINAL_VALUE = 100;
	private int max = ORIGINAL_VALUE;
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()) {
			case R.id.know_text:
				//mHandler.sendEmptyMessage(v.getId());
				break;
			}
		}
		
	};
	
	public ProgressView(Context context, BtApplication arg, Handler arg1) {
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
		mProgressBar = (ProgressBar)mView.findViewById(R.id.progressbar);
		mProgressBar.setMax(max);
		mName = (TextView)mView.findViewById(R.id.progress_text);
	}

	public void setTitle(String arg) {
		if (arg != null && mName != null) {
			mName.setText(arg); 
		}
	}
	
	public void finish() {
		mProgressBar.setProgress(max);
	}
	
	public void reset() {
		max = ORIGINAL_VALUE;
		mProgressBar.setMax(max);
		mProgressBar.setProgress(0);
	}
	
	@Override
	public void onPimProgress(int sum) {
		// TODO Auto-generated method stub
		if (mProgressBar != null) {
			if (sum > 0) {
				if (sum  >= (max - 20) ) {
					max ++;
					mProgressBar.setMax(max);
				}
				mProgressBar.setProgress(sum);
			}
		}
	}
	
}

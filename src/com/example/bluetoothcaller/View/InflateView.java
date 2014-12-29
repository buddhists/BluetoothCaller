package com.example.bluetoothcaller.View;

import com.example.bluetoothcaller.Activity.BtApplication;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;


public abstract class InflateView {
	
	private final static String TAG = "InflateView"; 
	protected Context mContext;
	protected View mView;
	protected Handler mHandler;
	protected BtApplication mBtApplication;
	
	public InflateView(Context context, BtApplication arg, Handler arg1) {
		mContext = context;
		mView = null;
		mBtApplication = arg;
		mHandler = arg1;
	}
	
	@SuppressWarnings("finally")
	public View createView(int id) {
		if (mView == null) {
			LayoutInflater LayoutInflater = 
					(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
			try {
				mView = LayoutInflater.inflate(id, null);
				initComponent();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				return mView;
			}
		}
		
		return mView;
	}
	
	public abstract void release();
	
	protected abstract void initComponent();
}

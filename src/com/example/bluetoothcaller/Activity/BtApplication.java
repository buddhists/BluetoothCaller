package com.example.bluetoothcaller.Activity;

import java.util.ArrayList;

import com.example.bluetoothcaller.Service.BluetoothLeService;
import com.example.bluetoothcaller.Service.onHFPNumber;

import android.os.IBinder;
import android.util.Log;
import android.app.Application;
import android.bluesoleil.IVTPimData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

public class BtApplication extends Application {
	public static BluetoothLeService mBluetoothCallService = null;
	private ArrayList<onBTServiceConnectionListener> mArrayList;
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBluetoothCallService = ((BluetoothLeService.BluetoothBinder) service).getService();
			mBluetoothCallService.StartBluetooth();
			if (mArrayList != null) {
				int vol = mArrayList.size();
				for (int i = 0; i < vol; i++) {
					mArrayList.get(i).onServiceConnected();
				}
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBluetoothCallService = null;
			mBluetoothCallService.StopBluetooth();
			if (mArrayList != null) {
				int vol = mArrayList.size();
				for (int i = 0; i < vol; i++) {
					mArrayList.get(i).onServiceDisconnected();
				}
			}
		}

	};
	
	@Override
	public void onCreate() {
		super.onCreate();
		mArrayList = new ArrayList<onBTServiceConnectionListener>();
		Intent startBluetooth = new Intent(this, BluetoothLeService.class);
		startService(startBluetooth);
		bindService(startBluetooth, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	public void registerBTServiceConnectionListener(onBTServiceConnectionListener arg) {
		if (!mArrayList.contains(arg)) {
			mArrayList.add(arg);
		}
	}
	
	public void unregisterBTServiceConnectionListener(onBTServiceConnectionListener arg) {
		int index = mArrayList.indexOf(arg);
		if (index != -1) {
			mArrayList.remove(index);
		}
	}
}

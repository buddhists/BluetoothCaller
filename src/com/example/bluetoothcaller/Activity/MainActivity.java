package com.example.bluetoothcaller.Activity;

import com.example.bluetoothcaller.Service.onPimState;
import com.example.bluetoothcaller.View.CalllogsView;
import com.example.bluetoothcaller.View.ContactsView;
import com.example.bluetoothcaller.View.FinderView;
import com.example.bluetoothcaller.View.KeyboardView;
import com.example.bluetoothcaller.View.NotifyView;
import com.example.bluetoothcaller.View.ProgressView;
import com.example.bluetoothcaller.View.UnKnowView;
import com.example.bluetoothcaller.View.UnconnnectedView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.bluesoleil.IVTPimData;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements onBTServiceConnectionListener, onBluetoothChanged, onPimState {

	private KeyboardView mKeyboardView;
	private CalllogsView mCalllogsView;
	private ContactsView mContactsView;
	private UnconnnectedView mUnconnnectedView;
	private UnKnowView mUnKnowView;
	private BtApplication mBtApplication;
	private LinearLayout mContextLinearLayout;
	private FrameLayout mFrameLayout;
	private PopupWindow mPopWindow;
	private ProgressView mProgressView;
	private ProgressView mProgressCallView;
	private NotifyView mNotifyView;
	private int forcusId = -1;
	private boolean isContactsDownloading = false;
	private boolean isCalllogDownloading = false;
	private static final int MESSAGE_WINDOW_DISMISS = 0x0001;
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.siderbar_key:
				updateForcusButton(R.id.siderbar_key);
				if (mContextLinearLayout != null) {
					mContextLinearLayout.removeAllViews();
					mContextLinearLayout.addView(mKeyboardView.createView(R.layout.bluetooth_main_keyboard));
				}
				break;
			case R.id.siderbar_calllog:
				updateForcusButton(R.id.siderbar_calllog);
				if (mContextLinearLayout != null) {
					mContextLinearLayout.removeAllViews();
					//LinearLayout.LayoutParams layoutTop = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
					//		LayoutParams.MATCH_PARENT);
					mContextLinearLayout.addView(mCalllogsView.createView(R.layout.bluetooth_main_calllog));
				}
				break;
			case R.id.siderbar_contacts:
				updateForcusButton(R.id.siderbar_contacts);
				if (mContextLinearLayout != null) {
					mContextLinearLayout.removeAllViews();
					mContextLinearLayout.addView(mContactsView.createView(R.layout.bluetooth_main_contacts));
				}
				break;
			case R.id.contacts_clear:
				if (mPopWindow != null && !mPopWindow.isShowing()) {
					mPopWindow.setContentView(mNotifyView.createView(R.layout.bluetooth_del_notify));
					mPopWindow.update();
					if (forcusId == R.id.siderbar_contacts) {
						mNotifyView.setContext(getResources().getString(R.string.del_all_contacts));
					} else if (forcusId == R.id.siderbar_calllog) {
						mNotifyView.setContext(getResources().getString(R.string.del_all_calllog));
					}
					mPopWindow.setFocusable(true);
					mPopWindow.setTouchable(true);
					mPopWindow.setOutsideTouchable(true);
					mPopWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.notify_bk));
					mPopWindow.showAtLocation(mContextLinearLayout, Gravity.CENTER, 0, 0);
				}
				break;
			}
		}
		
	};
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case R.id.know_text:
				if (mFrameLayout.getChildCount() > 1) {
					mFrameLayout.removeView(mUnKnowView.createView(R.layout.bluetooth_unknow));
					mUnKnowView.updateName();
					updateBluetoothFlag();
				}
				break;
			case R.id.unconnected_click_here:
				if (mFrameLayout.getChildCount() > 1) {
					mFrameLayout.removeView(mUnconnnectedView.createView(R.layout.bluetooth_unconnected));
					mFrameLayout.addView(mUnKnowView.createView(R.layout.bluetooth_unknow));
					mUnKnowView.updateName();
				}
				break;
			case R.id.cancel_btn:
				if (mPopWindow != null && mPopWindow.isShowing()) {
					mPopWindow.dismiss();
				}
				break;
			case R.id.confirm_btn:
				if (mPopWindow != null && mPopWindow.isShowing()) {
					mPopWindow.dismiss();
				}
				if (forcusId == R.id.siderbar_contacts) {
					mContactsView.release();
				} else if (forcusId == R.id.siderbar_calllog) {
					mCalllogsView.release();
				}
				break;
			case MESSAGE_WINDOW_DISMISS:
				//if (mPopWindow != null) {
					mPopWindow.dismiss();
				//}
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBtApplication = (BtApplication)getApplication();
		setContentView(R.layout.bluetooth_main);
		findViewById(R.id.siderbar_key).setOnClickListener(mOnClickListener);
		findViewById(R.id.siderbar_calllog).setOnClickListener(mOnClickListener);
		findViewById(R.id.siderbar_contacts).setOnClickListener(mOnClickListener);
		findViewById(R.id.contacts_clear).setOnClickListener(mOnClickListener);
		mBtApplication.registerBTServiceConnectionListener(this);
		mContextLinearLayout = (LinearLayout) findViewById(R.id.context);
		mFrameLayout = (FrameLayout) findViewById(R.id.main_root);
		mKeyboardView = new KeyboardView(this, mBtApplication, mHandler);
		mContactsView = new ContactsView(this, mBtApplication, mHandler);
		mCalllogsView = new CalllogsView(this, mBtApplication, mHandler);
		mUnconnnectedView = new UnconnnectedView(this, mBtApplication, mHandler);
		mUnKnowView = new UnKnowView(this, mBtApplication, mHandler);
		mProgressView = new ProgressView(this, mBtApplication, mHandler);
		mProgressCallView = new ProgressView(this, mBtApplication, mHandler);
		mNotifyView = new NotifyView(this, mBtApplication, mHandler);
		mContextLinearLayout.addView(mKeyboardView.createView(R.layout.bluetooth_main_keyboard));
		mContactsView.registerOnPimProgress(mProgressView);
		mCalllogsView.registerOnPimProgress(mProgressCallView);
		mContactsView.createView(R.layout.bluetooth_main_contacts);
		//mFrameLayout.addView(mUnKnowView.createView(R.layout.bluetooth_unknow));
		updateForcusButton(R.id.siderbar_key);
		if (mPopWindow == null) {
			mPopWindow = new PopupWindow(mProgressView.createView(R.layout.bluetooth_progress) , 500, 250);
		}
		
		updateBluetoothFlag();
	}
	
	@Override
	public void onDestroy() {
		mBtApplication.unregisterBTServiceConnectionListener(this);
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		onServiceConnected();
		
		if (isContactsDownloading) {
			mContactsView.startDownload();
			if (forcusId != R.id.siderbar_key) {
				if (mPopWindow != null && !mPopWindow.isShowing()) {
					mPopWindow.setContentView(mProgressView.createView(R.layout.bluetooth_progress));
					mProgressView.setTitle(getResources().getString(R.string.progressing));
					mPopWindow.update();
					mPopWindow.showAtLocation(mContextLinearLayout, Gravity.CENTER, 0, 50);
					
				}
			}
		}
		
		if (isCalllogDownloading) {
			if (forcusId != R.id.siderbar_key) {
				if (mPopWindow != null && !mPopWindow.isShowing()) {
					mPopWindow.setContentView(mProgressCallView.createView(R.layout.bluetooth_progress));
					mProgressCallView.setTitle(getResources().getString(R.string.progressingcalllog));
					mPopWindow.update();
					mPopWindow.showAtLocation(mContextLinearLayout, Gravity.CENTER, 0, 50);
					
				}
			}
		}
		Log.d("onflate","onResume");
		if (mBtApplication.mBluetoothCallService != null) {
			mBtApplication.mBluetoothCallService.registerOnContactsCallback(mContactsView);
			mBtApplication.mBluetoothCallService.registerOnCalllogsCallback(mCalllogsView);

			if (mContactsView.isNull()) {
				mBtApplication.mBluetoothCallService.updatePhonebook();
			}

			Log.d("onflate","updateBluetoothFlag " + mContactsView.getContactsNumber());
			if (mCalllogsView.isNull()) {
				mBtApplication.mBluetoothCallService.updateCalllog();
			}
			
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		onServiceDisconnected();
	}
	
	@Override
	public void onRestart() {
		super.onRestart();
		Log.d("onflate","onRestart");
	}
	
	@Override
	public void onServiceConnected() {
		// TODO Auto-generated method stub
		if (mBtApplication.mBluetoothCallService != null) {
			mBtApplication.mBluetoothCallService.registerOnContactsCallback(mContactsView);
			mBtApplication.mBluetoothCallService.registerOnCalllogsCallback(mCalllogsView);
			mBtApplication.mBluetoothCallService.registerOnBluetoothChanged(this);
			mBtApplication.mBluetoothCallService.registerOnPimState(this);
			isContactsDownloading = mBtApplication.mBluetoothCallService.isContactsDownloading;
			isCalllogDownloading = mBtApplication.mBluetoothCallService.isCalllogDownloading;
			Log.d("onflate","?"+isContactsDownloading+"?"+isCalllogDownloading);
			updateForcusButton(forcusId);
			updateBluetoothFlag();
			mUnconnnectedView.updateName();
			mUnKnowView.updateName();
		}
	}

	@Override
	public void onServiceDisconnected() {
		// TODO Auto-generated method stub
		if (mBtApplication.mBluetoothCallService != null) {
			mBtApplication.mBluetoothCallService.unregisterOnContactsCallback();
			mBtApplication.mBluetoothCallService.unregisterOnCalllogsCallback();
			mBtApplication.mBluetoothCallService.unregisterOnBluetoothChanged();
			mBtApplication.mBluetoothCallService.unregisterOnPimState();
		}
	}
	
	private void updateForcusButton(int id) {
		Button forcus = (Button) findViewById(id);
		forcusId = id;
		if (forcus != null) {
			switch (id) {
			case R.id.siderbar_key:
				if (mPopWindow != null && mPopWindow.isShowing()) {
					mPopWindow.dismiss();
				}
				
				findViewById(R.id.contacts_clear).setVisibility(View.GONE);
				forcus.setBackgroundResource(R.drawable.sider_key_dn);
				((Button) findViewById(R.id.siderbar_calllog)).setBackgroundResource(R.drawable.sider_callog_up);
				((Button) findViewById(R.id.siderbar_contacts)).setBackgroundResource(R.drawable.sider_contacts_up);
				break;
			case R.id.siderbar_calllog:
				if (isCalllogDownloading || isContactsDownloading) {
					if (!isCalllogDownloading) {
						mProgressCallView.setTitle(getResources().getString(R.string.progressingcalllog));
					} else {
						mProgressView.setTitle(getResources().getString(R.string.progressing));
					}
					mPopWindow.showAtLocation(mContextLinearLayout, Gravity.CENTER, 0, 50);
				} else {
					mPopWindow.dismiss();
				}
				
				findViewById(R.id.contacts_clear).setVisibility(View.VISIBLE);
				forcus.setBackgroundResource(R.drawable.sider_callog_dn);
				((Button) findViewById(R.id.siderbar_key)).setBackgroundResource(R.drawable.sider_key_up);
				((Button) findViewById(R.id.siderbar_contacts)).setBackgroundResource(R.drawable.sider_contacts_up);
				break;
			case R.id.siderbar_contacts:
				if (isContactsDownloading || isCalllogDownloading) {
					if (!isCalllogDownloading) {
						mProgressCallView.setTitle(getResources().getString(R.string.progressingcalllog));
					} else {
						mProgressView.setTitle(getResources().getString(R.string.progressing));
					}
					mPopWindow.showAtLocation(mContextLinearLayout, Gravity.CENTER, 0, 50);
				} else {
					mPopWindow.dismiss();
				}
				findViewById(R.id.contacts_clear).setVisibility(View.VISIBLE);
				forcus.setBackgroundResource(R.drawable.sider_contacts_dn);
				((Button) findViewById(R.id.siderbar_calllog)).setBackgroundResource(R.drawable.sider_callog_up);
				((Button) findViewById(R.id.siderbar_key)).setBackgroundResource(R.drawable.sider_key_up);
				break;
			}
		}
	}

	private void updateBluetoothFlag() {
		ImageView flag = (ImageView)findViewById(R.id.bluetooth_flag);
		TextView device = (TextView)findViewById(R.id.bluetooth_device_name);
		if ((flag != null) && (device != null)) {
			if (mBtApplication.mBluetoothCallService != null && mBtApplication.mBluetoothCallService.isBluetoothOn()) {
				//device.setText(mBtApplication.mBluetoothCallService.getBluetoothName());
				if (mBtApplication.mBluetoothCallService.isHfpConnected()) {
					flag.setBackgroundResource(R.drawable.bluetooth_connect_flag);
					int count = mFrameLayout.getChildCount();
					Log.d("onflate","remove " + count);
					if (count > 1) {
						//mFrameLayout.removeView(mUnconnnectedView.createView(R.layout.bluetooth_unconnected));
						for (int i = 0; i < count; i++) {
							if (mFrameLayout.getChildAt(i).getId() != R.id.main_content_view)
								mFrameLayout.removeViewAt(i);
						}
					}
				} else {
					int count = mFrameLayout.getChildCount();
					mContactsView.onDisconnected();
					mCalllogsView.onDisconnected();
					if (count == 1) {
						mFrameLayout.addView(mUnconnnectedView.createView(R.layout.bluetooth_unconnected));
						mUnconnnectedView.updateName();
						mUnKnowView.updateName();
						if (mPopWindow != null && mPopWindow.isShowing()) {
							mPopWindow.dismiss();
						}
					}
					flag.setBackgroundResource(R.drawable.bluetooth_disconnect_flag);
				}
			} else {
				int count = mFrameLayout.getChildCount();
				if (count == 1) {
					if (mPopWindow != null && mPopWindow.isShowing()) {
						mPopWindow.dismiss();
					}
					mUnconnnectedView.updateName();
					mUnKnowView.updateName();
					mFrameLayout.addView(mUnconnnectedView.createView(R.layout.bluetooth_unconnected));
				}
				flag.setBackground(null);
			}
		}
	}
	
	@Override
	public void onBluetoothChanged() {
		// TODO Auto-generated method stub
		updateBluetoothFlag();
	}

	@Override
	public void onContactsSynFinish() {
		// TODO Auto-generated method stub
		isContactsDownloading = false;
		mProgressView.finish();
		if (forcusId != R.id.siderbar_key) {
			mPopWindow.setContentView(mProgressView.createView(R.layout.bluetooth_progress));
			mProgressView.setTitle(getResources().getString(R.string.contacts_sum)+"共"+mContactsView.getContactsNumber()+"个");
			mProgressView.finish();
			mPopWindow.update();
		}
	}

	@Override
	public void onCalllogSynFinish() {
		// TODO Auto-generated method stub
		isCalllogDownloading = false;
		mCalllogsView.stopDownload();
		if (forcusId != R.id.siderbar_key) {
			mPopWindow.setContentView(mProgressCallView.createView(R.layout.bluetooth_progress));
			mProgressCallView.setTitle(getResources().getString(R.string.calllog_sum)+"共"+mContactsView.getContactsNumber()+"个");
			mProgressView.finish();
			
		}
		onPimDisconnected();
		/*
		if (mContextLinearLayout != null) {
			mContextLinearLayout.removeAllViews();
			if (forcusId == R.id.siderbar_contacts)
				mContextLinearLayout.addView(mContactsView.createView(R.layout.bluetooth_main_contacts));
			else if (forcusId == R.id.siderbar_calllog)
				mContextLinearLayout.addView(mCalllogsView.createView(R.layout.bluetooth_main_calllog));
		}*/
	}

	@Override
	public void onContactsSynStart() {
		// TODO Auto-generated method stub
		isContactsDownloading = true;
		mProgressView.reset();
		mContactsView.release();
		mContactsView.startDownload();
		if (mBtApplication.mBluetoothCallService.isHfpConnected()) {
			if (forcusId != R.id.siderbar_key) {
				if (mPopWindow != null && !mPopWindow.isShowing()) {
					mPopWindow.setContentView(mProgressView.createView(R.layout.bluetooth_progress));
					mProgressView.setTitle(getResources().getString(R.string.progressing));
					mPopWindow.update();
					mPopWindow.showAtLocation(mContextLinearLayout, Gravity.CENTER, 0, 50);
					
				}
			}
		}
	}

	@Override
	public void onCalllogSynStart() {
		// TODO Auto-generated method stub
		isCalllogDownloading = true;
		mCalllogsView.release();
		mContactsView.stopDownload();
		//mCalllogsView.startDownload();
		if (mBtApplication.mBluetoothCallService.isHfpConnected()) {
			if (forcusId != R.id.siderbar_key) {
				if (mPopWindow != null && !mPopWindow.isShowing()) {
					mPopWindow.setContentView(mProgressCallView.createView(R.layout.bluetooth_progress));
					mProgressCallView.setTitle(getResources().getString(R.string.progressingcalllog));
					mPopWindow.update();
					mPopWindow.showAtLocation(mContextLinearLayout, Gravity.CENTER, 0, 50);
					
				}
			}
		}
	}

	@Override
	public void onPimDisconnected() {
		// TODO Auto-generated method stub
		if (mPopWindow != null && mPopWindow.isShowing()) {
			mPopWindow.dismiss();
		}
	}
}

/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.bluetoothcaller.Service;


import java.util.ArrayList;

import com.example.bluetoothcaller.Activity.CallActivity;
import com.example.bluetoothcaller.Activity.IncomingAcitivity;
import com.example.bluetoothcaller.Activity.MainActivity;
import com.example.bluetoothcaller.Activity.OutgoingActivity;
import com.example.bluetoothcaller.Activity.OutgoingFinishActivity;
import com.example.bluetoothcaller.Activity.R;
import com.example.bluetoothcaller.Activity.onBluetoothChanged;

import android.app.Service;
import android.bluesoleil.IVTBluetoothBased;
import android.bluesoleil.IVTBluetoothDef;
import android.bluesoleil.IVTBluetoothHfp;
import android.bluesoleil.IVTBluetoothPim;
import android.bluesoleil.IVTPimData;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.text.format.Time;
import android.util.Log;


/**
 * Provides Bluetooth Headset and Handsfree profile, as a service in the
 * Bluetooth application.
 * 
 * @hide
 */
public class BluetoothLeService extends Service implements IVTBluetoothPim.IVTBluetoothPimCallback{
	private static final boolean DBG = false;
	private static final String TAG = "BluetoothCallService";
	private final IBinder mBinder = new BluetoothBinder();
	private Context mContext;
	private HandlerThread mHandlerThread;
	private Handler mHandler;
	private onContactsCallback mOnContactsCallback;
	private onCalllogsCallback mOnCalllogsCallback;
	private onBluetoothChanged mOnBluetoothChanged;
	private onPimState mOnPimState;
	private IVTBluetoothBased mIVTBluetoothBase;
	private IVTBluetoothHfp mIVTBluetoothHfp;
	private IVTBluetoothPim mIVTBluetoothPim;
	public boolean isContactsDownloading = false;
	public boolean isCalllogDownloading = false;
	public ArrayList<IVTPimData> mArrayCalllogList;
	public ArrayList<IVTPimData> mArrayContactList;
	
	private onHFPNumber mOnHFPNumber;
	private onHFPCurrentCall mOnHFPCurrentCall;
	/**callsetp illustrates the state of establishing an incoming call
	 * 0 indicates idle
	 * 1 indicates incoming call
	 * call illustrates the state of call
	 * 0 indicates idle
	 * 1 indicates the state of call */
	private int callsetup = CALLSETUP_IDLE;
	private int call = CALL_IDLE;
	private int calltype = CALL_TYPE_IN;
	
	/** Local Bluetooth Messages */
	private static final int MESSAGE_START_BLUETOOTH =					0x0001;
	private static final int MESSAGE_STOP_BLUETOOTH =					0x0002;
	/** HFP Messages */
	private static final int MESSAGE_ANSWER_CALL =						0x0101;
	private static final int MESSAGE_CANCEL_CALL =						0x0102;
	private static final int MESSAGE_DAIL =								0x0103;
	private static final int MESSAGE_DTMF = 							0x0105;
	private static final int MESSAGE_TRANSFER_VOICE =					0x0104;
	/** PBAP Messages */
	private static final int MESSAGE_GET_CALLLOG =						0x0201;
	private static final int MESSAGE_GET_PHONEBOOK =					0x0202;
	private static final int MESSAGE_SEND_PHONEBOOK_ITEM =				0x0203;
	private static final int MESSAGE_SEND_CALLLOG_ITEM =				0x0204;
	/** constants for the HFP */
	private static final int CALLSETUP_IDLE = 							0x0000;
	private static final int CALLSETUP_ING = 							0x0001;
	private static final int CALL_IDLE =								0x0000;
	private static final int CALL_ING =									0x0001;
	private static final int CALL_TYPE_IN =								0x0000;
	private static final int CALL_TYPE_OUT =							0x0001;
	
	private String mLastAddr = null;
	private String mCurrentAddr = null;
	private String mLastNumber = null;
	private String mCurrentNumber = null;
	private long mStart = 0;
	private int mlastTime = 0;
	private boolean mAudioSCO = false;

	private Handler mTimerHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_SEND_PHONEBOOK_ITEM:
				if (mOnContactsCallback != null) {
					mOnContactsCallback.onContacts((IVTPimData)msg.obj);
				}
				break;
			case MESSAGE_SEND_CALLLOG_ITEM:
				if (mOnCalllogsCallback != null) {
					mOnCalllogsCallback.onCalllogs((IVTPimData)msg.obj);
				}
				break;
			}
		}
	};
	
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (call != CALL_IDLE) {
				if (mOnHFPCurrentCall != null) {
					mOnHFPCurrentCall.onHFPNumber(getContactName(mCurrentNumber));
					mlastTime = (int) ((System.currentTimeMillis() - mStart)/1000);
					mOnHFPCurrentCall.onHFPDuringTime(mlastTime);
				}
			} else {
				mStart = System.currentTimeMillis();
			}
			mTimerHandler.postDelayed(this,850);
		}
		
	};
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(IVTBluetoothDef.BRDSDK_HFP_EV_RINGING)) {
				callsetup = CALLSETUP_ING;
			} else if (action.equals(IVTBluetoothDef.BRDSDK_HFP_EV_CLIP)) {
				if (call == CALL_IDLE) {
					mCurrentNumber = intent.getStringExtra(IVTBluetoothDef.BRDSDK_HFP_EV_CLIP_EXTRA_NUMBER);
					Intent intentCall = new Intent(mContext, IncomingAcitivity.class);
					intentCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intentCall);
				}
			} else if (action.equals(IVTBluetoothDef.BRDSDK_HFP_EV_CLCC)) {
				Log.d("onflate","BRDSDK_HFP_EV_CLCC");
				//if (call == CALL_IDLE) {
					mCurrentNumber = intent.getStringExtra(IVTBluetoothDef.BRDSDK_HFP_EV_CLCC_EXTRA_NUMBER);
					Log.d("onflate","BRDSDK_HFP_EV_CLCC_EXTRA_NUMBER " + mCurrentNumber);
					if (mOnHFPNumber != null) {
						if (mCurrentNumber == null) {
							mOnHFPNumber.onHFPNumber("", "");
						} else {
							mOnHFPNumber.onHFPNumber(mCurrentNumber, getContactName(mCurrentNumber));
						}
					}
			} else if (action.equals(IVTBluetoothDef.BRDSDK_HFP_EV_STANDBY)) {
				if (call == CALL_ING) {
					if (mOnHFPNumber != null) {
						mOnHFPNumber.onHFPNumber("", "");
					}
					Intent home = new Intent(mContext, OutgoingFinishActivity.class);
					home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(home);
					IVTPimData calllog = new IVTPimData(IVTPimData.TYPE_CALL_LOG);
					calllog.getCallLog().setType(calltype);
					Time time = new Time();
					time.setToNow();
					calllog.getCallLog().setTime(time.toString().substring(0, 15));
					calllog.getCallLog().setName(getContactName(mCurrentNumber));
					calllog.getCallLog().setTeleNumber(mCurrentNumber);
					if (mOnCalllogsCallback != null) {
						mOnCalllogsCallback.onCalllogs(calllog);
					}
				} else if (callsetup == CALLSETUP_ING) {
					mlastTime = 0;
					IVTPimData calllog = new IVTPimData(IVTPimData.TYPE_CALL_LOG);
					calllog.getCallLog().setType(calltype);
					Time time = new Time();
					time.setToNow();
					calllog.getCallLog().setTime(time.toString().substring(0, 15));
					calllog.getCallLog().setName(getContactName(mCurrentNumber));
					calllog.getCallLog().setTeleNumber(mCurrentNumber);
					if (mOnCalllogsCallback != null) {
						mOnCalllogsCallback.onCalllogs(calllog);
					}
					Intent home = new Intent(mContext, OutgoingFinishActivity.class);
					home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(home);
				}
				calltype= CALL_TYPE_IN;
				call = CALL_IDLE;
				callsetup = CALLSETUP_IDLE;
				mLastNumber = mCurrentNumber;
				mCurrentNumber = null;
			} else if (action.equals(IVTBluetoothDef.BRDSDK_HFP_EV_ONGOINGCALL)) {
				callsetup = CALLSETUP_IDLE;
				call = CALL_ING;
				Intent intentCall = new Intent(mContext, CallActivity.class);
				intentCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intentCall);
			} else if (action.equals(IVTBluetoothDef.BRDSDK_CONNECTION_LINK_LOST)) {
				Log.d("onflate","Link lost");
				if (call == CALL_ING) {
					if (mOnHFPNumber != null) {
						mOnHFPNumber.onHFPNumber("", "");
					}
					Intent home = new Intent(mContext, OutgoingFinishActivity.class);
					home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(home);
				} else if (callsetup == CALLSETUP_ING) {
					Intent home = new Intent(mContext, OutgoingFinishActivity.class);
					home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(home);
				}
				call = CALL_IDLE;
				callsetup = CALLSETUP_IDLE;
				mLastNumber = mCurrentNumber;
				mCurrentNumber = null;
			}
			else if (action.equals(IVTBluetoothDef.BRDSDK_HFP_EV_OUTGOINGCALL)) {
				callsetup = CALLSETUP_ING;
				calltype = CALL_TYPE_OUT;
				Intent intentCall = new Intent(mContext, OutgoingActivity.class);
				intentCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intentCall);
			/** When HFP connected, this service starts to get phonebook */
			} else if (action.equals(IVTBluetoothDef.BRDSDK_BLUETOOTH_CONNECTION_CHANGED)) {
				if (mOnBluetoothChanged != null) {
					mOnBluetoothChanged.onBluetoothChanged();
				}
				int service_class = intent.getIntExtra(IVTBluetoothDef.BRDSDK_BLUETOOTH_DEVICE_SERVICE, 0);
				int event = intent.getIntExtra(IVTBluetoothDef.BRDSDK_BLUETOOTH_CONNECTION_EVENT, 0);
				mCurrentAddr = intent.getStringExtra(IVTBluetoothDef.BRDSDK_BLUETOOTH_DEVICE_ADDRESS);
				if (service_class == IVTBluetoothDef.BRDSDK_CLS_HANDSFREE) {
					if (event == IVTBluetoothDef.BRDSDK_CONNECTION_EVENT_CONNECTED) {
						//if (!mLastAddr.equals(mCurrentAddr)) {
							GetPhonebook();
							isContactsDownloading = true;
							mArrayContactList.clear();
							if (mOnPimState != null) {
								mOnPimState.onContactsSynStart();
							}
						//}
					} else if (event == IVTBluetoothDef.BRDSDK_CONNECTION_EVENT_DISCONNECTED) {
						if (call == CALL_ING) {
							if (mOnHFPNumber != null) {
								mOnHFPNumber.onHFPNumber("", "");
							}
							Intent home = new Intent(mContext, OutgoingFinishActivity.class);
							home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(home);
						} else if (callsetup == CALLSETUP_ING) {
							Intent home = new Intent(mContext, OutgoingFinishActivity.class);
							home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(home);
						}
						call = CALL_IDLE;
						callsetup = CALLSETUP_IDLE;
						mLastNumber = mCurrentNumber;
						mCurrentNumber = null;
					}
				} else if (event == IVTBluetoothDef.BRDSDK_CLS_PBAP_PSE) {
					if (mOnPimState != null) {
						mOnPimState.onPimDisconnected();
					}
				}
			} else if (action.equals(IVTBluetoothDef.BRDSDK_PIM_SYNC_FINISHED)) {
				int result = intent.getIntExtra(IVTBluetoothDef.BRDSDK_PIM_SYNC_RESULT, -1);
				if (result == IVTBluetoothDef.BRDSDK_PIM_SYNC_CONTACT_SUCCESS || 
						result == IVTBluetoothDef.BRDSDK_PIM_SYNC_CONTACT_FAILED) {
					//if (!mLastAddr.equals(mCurrentAddr)) {
						isContactsDownloading = false;
						if (mOnPimState != null) {
							mOnPimState.onContactsSynFinish();
						}
						GetCalllog();
						isCalllogDownloading = true;
						mArrayCalllogList.clear();
						if (mOnPimState != null) {
							mOnPimState.onCalllogSynStart();
						}
					//}
				} else if (result == IVTBluetoothDef.BRDSDK_PIM_SYNC_CALLLOG_SUCCESS ||
						result == IVTBluetoothDef.BRDSDK_PIM_SYNC_CALLLOG_FAILED) {
					//if (!mLastAddr.equals(mCurrentAddr)) {
						isCalllogDownloading = false;
						if (mOnPimState != null) {
							mOnPimState.onCalllogSynFinish();
						}
					//}
				}
			} else if (action.equals(IVTBluetoothDef.BRDSDK_BLUETOOTH_STATE_CHANGED)) {
				if (mOnBluetoothChanged != null) {
					mOnBluetoothChanged.onBluetoothChanged();
				}
			} else if (action.equals(IVTBluetoothDef.BRDSDK_HFP_EV_AUDIO_CONN_ESTABLISHED)) {
				mAudioSCO = true;
			} else if (action.equals(IVTBluetoothDef.BRDSDK_HFP_EV_AUDIO_CONN_RELEASED)) {
				mAudioSCO = false;
			}
		}
	};
	
	public class BluetoothBinder extends Binder {
		public BluetoothLeService getService() {
			return BluetoothLeService.this;
		}
	}

	public BluetoothLeService() {
		// TODO Auto-generated constructor stub
		mIVTBluetoothBase = new IVTBluetoothBased();
		mIVTBluetoothHfp = new IVTBluetoothHfp(this);
		mContext = this;
		mArrayCalllogList = new ArrayList<IVTPimData>();
		mArrayContactList = new ArrayList<IVTPimData>();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(IVTBluetoothDef.BRDSDK_HFP_EV_RINGING);
		filter.addAction(IVTBluetoothDef.BRDSDK_HFP_EV_CLIP);
		filter.addAction(IVTBluetoothDef.BRDSDK_HFP_EV_CLCC);
		filter.addAction(IVTBluetoothDef.BRDSDK_HFP_EV_ONGOINGCALL);
		filter.addAction(IVTBluetoothDef.BRDSDK_HFP_EV_OUTGOINGCALL);
		filter.addAction(IVTBluetoothDef.BRDSDK_HFP_EV_STANDBY);
		filter.addAction(IVTBluetoothDef.BRDSDK_HFP_EV_AUDIO_CONN_RELEASED);
		filter.addAction(IVTBluetoothDef.BRDSDK_HFP_EV_AUDIO_CONN_ESTABLISHED);
		filter.addAction(IVTBluetoothDef.BRDSDK_BLUETOOTH_CONNECTION_CHANGED);
		filter.addAction(IVTBluetoothDef.BRDSDK_CONNECTION_LINK_LOST);
		filter.addAction(IVTBluetoothDef.BRDSDK_PIM_SYNC_FINISHED);
		filter.addAction(IVTBluetoothDef.BRDSDK_BLUETOOTH_STATE_CHANGED);
		filter.setPriority(1000);
		registerReceiver(mReceiver, filter);
		
		mHandlerThread = new HandlerThread("workThread");
		if (!mHandlerThread.isAlive()) {
			mHandlerThread.start();
		}

		mHandler = new Handler(mHandlerThread.getLooper()) {

			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MESSAGE_START_BLUETOOTH:
					if (mIVTBluetoothBase != null) {
						mIVTBluetoothBase.BRDSDK_StartBluetooth();
						String addr = mIVTBluetoothBase.BRDSDK_GetLocalDeviceAddress();
						int len = addr.length();
						String name = "Mobnote-" + addr.substring(len - 5, len -3) + addr.substring(len -2);
						mIVTBluetoothBase.BRDSDK_SetLocalName(name);
					}
					break;
				case MESSAGE_STOP_BLUETOOTH:
					if (mIVTBluetoothBase != null) {
						mIVTBluetoothBase.BRDSDK_StopBluetooth();
					}
					break;
				case MESSAGE_ANSWER_CALL:
					if (mIVTBluetoothHfp != null) {
						mIVTBluetoothHfp.BRDSDK_AnswerCall();
					}
					break;
				case MESSAGE_CANCEL_CALL:
					if (mIVTBluetoothHfp != null) {
						mIVTBluetoothHfp.BRDSDK_CancelCall();
					}
					break;
				case MESSAGE_DAIL:
					if (mIVTBluetoothHfp != null) {
						mIVTBluetoothHfp.BRDSDK_Dial((String)msg.obj);
					}
					break;
				case MESSAGE_TRANSFER_VOICE:
					if (mIVTBluetoothHfp != null) {
						mIVTBluetoothHfp.BRDSDK_Transfer();
					}
					break;
				case MESSAGE_GET_CALLLOG:
					if (mIVTBluetoothPim != null) {
						mIVTBluetoothPim.BRDSDK_GetCallLogs();
					}
					break;
				case MESSAGE_GET_PHONEBOOK:
					if (mIVTBluetoothPim != null) {
						mIVTBluetoothPim.BRDSDK_GetContacts();
					}
					break;
				case MESSAGE_DTMF:
					if (mIVTBluetoothHfp != null) {
						mIVTBluetoothHfp.BRDSDK_TransmitDTMF((char)msg.arg1);
					}
					break;
				}
			}
		};
		
		mTimerHandler.postDelayed(runnable, 1000);
		return super.onStartCommand(intent, flags, startId);
	}
	
	public void registerHFPInterface(onHFPNumber arg) {
		mOnHFPNumber = arg;
	}
	
	public void unregisterHFPInterface() {
		mOnHFPNumber = null;
	}
	
	public void registerHFPCurrent(onHFPCurrentCall arg) {
		mOnHFPCurrentCall = arg;
	}
	
	public void unregisterHFPCurrent() {
		mOnHFPCurrentCall = null;
	}
	
	public void registerOnContactsCallback(onContactsCallback arg) {
		mOnContactsCallback = arg;
	}
	
	public void unregisterOnContactsCallback() {
		mOnContactsCallback = null;
	}
	
	public void registerOnCalllogsCallback(onCalllogsCallback arg) {
		mOnCalllogsCallback = arg;
	}
	
	public void unregisterOnCalllogsCallback() {
		//mOnCalllogsCallback = null;
	}
	
	public void registerOnBluetoothChanged(onBluetoothChanged arg) {
		mOnBluetoothChanged = arg;
	}
	
	public void unregisterOnBluetoothChanged() {
		mOnBluetoothChanged = null;
	}
	
	public void registerOnPimState(onPimState arg) {
		mOnPimState = arg;
	}
	
	public void unregisterOnPimState() {
		mOnPimState = null;
	}
	
	public boolean isSCOConnected() {
		return mAudioSCO;
	}
	
	public boolean isBluetoothOn() {
		boolean ret = false;
		if (mIVTBluetoothBase != null) {
			ret = mIVTBluetoothBase.BRDSDK_IsBluetoothOn();
		}
		return ret;
	}
	
	public boolean isHfpConnected() {
		boolean ret = false;
		if (mIVTBluetoothHfp != null) {
			ret = mIVTBluetoothHfp.BRDSDK_IsConnected();
		}
		return ret;
	}
	
	public String getBluetoothName() {
		String ret = null;
		if (mIVTBluetoothBase != null) {
			ret = mIVTBluetoothBase.BRDSDK_GetLocalName();
		}
		return ret;
	}
	
	public void StartBluetooth() {
		Message msg = new Message();
		msg.setTarget(mHandler);
		msg.what = MESSAGE_START_BLUETOOTH;
		msg.sendToTarget();
	}
	
	public void StopBluetooth() {
		Message msg = new Message();
		msg.setTarget(mHandler);
		msg.what = MESSAGE_STOP_BLUETOOTH;
		msg.sendToTarget();
	}
	
	public void AnswerCall() {
		Message msg = new Message();
		msg.setTarget(mHandler);
		msg.what = MESSAGE_ANSWER_CALL;
		msg.sendToTarget();
	}
	
	public void CancelCall() {
		Message msg = new Message();
		msg.setTarget(mHandler);
		msg.what = MESSAGE_CANCEL_CALL;
		msg.sendToTarget();
	}
	
	public void Dail(String num) {
		Message msg = new Message();
		msg.setTarget(mHandler);
		msg.obj = num;
		msg.what = MESSAGE_DAIL;
		msg.sendToTarget();
	}
	
	public void DTMF(char arg) {
		Message msg = new Message();
		msg.setTarget(mHandler);
		msg.arg1 = arg;
		msg.what = MESSAGE_DTMF;
		msg.sendToTarget();
	}
	
	public void TransferVoice() {
		Message msg = new Message();
		msg.setTarget(mHandler);
		msg.what = MESSAGE_TRANSFER_VOICE;
		msg.sendToTarget();
	}
	
	public void GetPhonebook() {
		if (mIVTBluetoothPim == null) {
			mIVTBluetoothPim = new IVTBluetoothPim(this);
		}
		
		Message msg = new Message();
		msg.setTarget(mHandler);
		msg.what = MESSAGE_GET_PHONEBOOK;
		msg.sendToTarget();
	}
	
	public void updatePhonebook() {
		if (mIVTBluetoothPim == null) {
			mIVTBluetoothPim = new IVTBluetoothPim(this);
		}
		
		if (mOnContactsCallback != null) {
			for (int i = 0 ; i < mArrayContactList.size(); i++) {
				mOnContactsCallback.onContacts(mArrayContactList.get(i));
			}
		}
	}
	
	public void updateCalllog() {
		if (mIVTBluetoothPim == null) {
			mIVTBluetoothPim = new IVTBluetoothPim(this);
		}
		
		if (mOnCalllogsCallback != null) {
			for (int i = 0 ; i < mArrayCalllogList.size(); i++) {
				mOnCalllogsCallback.onCalllogs(mArrayCalllogList.get(i));
			}
		}
	}
	
	public void GetCalllog() {
		if (mIVTBluetoothPim == null) {
			mIVTBluetoothPim = new IVTBluetoothPim(this);
		}
		
		Message msg = new Message();
		msg.setTarget(mHandler);
		msg.what = MESSAGE_GET_CALLLOG;
		msg.sendToTarget();
	}
	
	public int requestLastDuringTime() {
		return mlastTime;
	}
	
	public void requestForCurrentCallNumber() {
		if (mOnHFPNumber != null) {
			Log.d("onflate","BRDSDK_HFP_EV_CLIP 1");
			Log.d("onflate","" + mCurrentNumber);
			if (mCurrentNumber == null) {
				mOnHFPNumber.onHFPNumber("", "");
			} else {
				mOnHFPNumber.onHFPNumber(mCurrentNumber, getContactName(mCurrentNumber));
			}
		}
	}
	
	public void requestForLastCallNumber() {
		if (mOnHFPNumber != null) {
			Log.d("onflate","BRDSDK_HFP_EV_CLIP 1");
			Log.d("onflate","" + mCurrentNumber);
			if (mLastNumber == null) {
				mOnHFPNumber.onHFPNumber("", "");
			} else {
				mOnHFPNumber.onHFPNumber(mLastNumber, getContactName(mLastNumber));
			}
		}
	}
	
	@Override
	public void onCalllogSync(IVTPimData arg0) {
		// TODO Auto-generated method stub
		mArrayCalllogList.add(arg0);
		Message msg = new Message();
		msg.what = MESSAGE_SEND_CALLLOG_ITEM;
		msg.obj = arg0;
		mTimerHandler.sendMessage(msg);
	}

	public String getContactName(String number) {
		String ret = number;
		for (int i = 0; i < mArrayContactList.size(); i++) {
			if (mArrayContactList.get(i).getContact().getCellNumber().size() > 0) {
				if (mArrayContactList.get(i).getContact().getCellNumber().get(0).equals(number)) {
					ret = mArrayContactList.get(i).getContact().getName();
				}
			}
			if (mArrayContactList.get(i).getContact().getHomeNumber() .size() > 0) {
				if (mArrayContactList.get(i).getContact().getHomeNumber().get(0).equals(number)) {
					ret = mArrayContactList.get(i).getContact().getName();
				}
			}
			if (mArrayContactList.get(i).getContact().getWorkNumber().size() > 0) {
				if (mArrayContactList.get(i).getContact().getWorkNumber().get(0).equals(number)) {
					ret = mArrayContactList.get(i).getContact().getName();
				}
			}
		}
		/*
		Cursor cur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				null, null, null, null);
		while (cur.moveToNext()) {
			int indexPeopleName = cur.getColumnIndex(Phone.DISPLAY_NAME);
			int indexPhoneNum = cur.getColumnIndex(Phone.NUMBER);
			String strPeopleName = cur.getString(indexPeopleName);
			String strPhoneNum = cur.getString(indexPhoneNum);
			if (strPhoneNum.equals(number)) {
				ret = strPeopleName;
				break;
			}
		}
		
		if (!cur.isClosed()) {
			cur.close();
		}*/
		return ret;
	}
	
	@Override
	public void onContactSync(IVTPimData arg0) {
		// TODO Auto-generated method stub
		mArrayContactList.add(arg0);
		/*
		if (!getContactName(arg0.getContact().getCellNumber().get(0)).equals(arg0.getContact().getName())) {
			Log.d("onflate","add contacts");
			ContentValues values = new ContentValues();
			Uri rawContactUri = mContext.getContentResolver().insert(RawContacts.CONTENT_URI, values);
			long rawContactId = ContentUris.parseId(rawContactUri);
			values.clear();
			values.put(Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
			values.put(StructuredName.GIVEN_NAME, arg0.getContact().getName());
			mContext.getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
	
			values.clear();
			values.put(Data.RAW_CONTACT_ID, rawContactId);
			values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
			values.put(Phone.NUMBER, arg0.getContact().getCellNumber().get(0));
			values.put(Phone.TYPE, Phone.TYPE_MOBILE);
			mContext.getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
		}*/
		
		
		Message msg = new Message();
		msg.what = MESSAGE_SEND_PHONEBOOK_ITEM;
		msg.obj = arg0;
		mTimerHandler.sendMessage(msg);
	}

	@Override
	public void onSmsSync(IVTPimData arg0) {
		// TODO Auto-generated method stub
		
	}
}
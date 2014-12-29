package com.example.bluetoothcaller.View;

import java.util.ArrayList;

import android.bluesoleil.IVTPimData;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.example.bluetoothcaller.Activity.BtApplication;
import com.example.bluetoothcaller.Activity.R;
import com.example.bluetoothcaller.Service.onCalllogsCallback;
import com.example.bluetoothcaller.View.ContactsView.BluetoothDeviceAdapter;

public class CalllogsView extends InflateView implements onCalllogsCallback {

	private ListView contacts_list;
	private BluetoothDeviceAdapter mBluetoothDeviceAdapter = null;
	private Time t;
	private Time t_now;
	private onPimProgress mOnPimProgress;
	private PopupWindow mPopWindow;
	private NotifyView mNotifyView;
	private int mSelectedIndex = -1;
	
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ListView listView = (ListView)parent;
			String num = ((IVTPimData)listView.getAdapter().getItem(position)).getCallLog().getTeleNumber();
			if (mBtApplication.mBluetoothCallService != null && (num.length() != 0)) {
				mBtApplication.mBluetoothCallService.Dail(num);
			}
		}
	};
	
	private OnItemLongClickListener mOnItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub
			mSelectedIndex = arg2;
			mPopWindow.setFocusable(true);
			mPopWindow.setTouchable(true);
			mPopWindow.setOutsideTouchable(true);
			mPopWindow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.notify_bk));
			mPopWindow.showAtLocation(contacts_list, Gravity.CENTER, 0, 0);
			return true;
		}
		
	};
	
	private Handler mHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case R.id.cancel_btn:
				if (mPopWindow != null && mPopWindow.isShowing()) {
					mPopWindow.dismiss();
				}
				break;
			case R.id.confirm_btn:
				if (mPopWindow != null && mPopWindow.isShowing()) {
					mPopWindow.dismiss();
				}
				
				if (mSelectedIndex != -1) {
					mBluetoothDeviceAdapter.mArrayList.remove(mSelectedIndex);
					mBluetoothDeviceAdapter.notifyDataSetChanged();
				}
				break;
			}
		}
	};
	
	class BluetoothDeviceAdapter extends BaseAdapter {
		private Context mContext;
		public ArrayList<IVTPimData> mArrayList;
		
		public BluetoothDeviceAdapter(Context context) {  
			mContext = context;
			mArrayList = new ArrayList<IVTPimData>();
		}
	
		@Override
		public boolean areAllItemsEnabled() {  
			return false;  
		}
		
		
		public Object getItem(int position) {  
			return mArrayList.get(position);  
		}
	
		public long getItemId(int position) {  
			return position;  
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = ((LayoutInflater) mContext.getSystemService(
						Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.bluetooth_main_calllog_item, null);
			}
			
			int type = mArrayList.get(position).getCallLog().getType();
			if (type == 0)
				((ImageView)convertView.findViewById(R.id.calllogs_item_type)).setImageResource(R.drawable.call_in);
			else if (type == 1)
				((ImageView)convertView.findViewById(R.id.calllogs_item_type)).setImageResource(R.drawable.call_out);
			else if (type == 2)
				((ImageView)convertView.findViewById(R.id.calllogs_item_type)).setImageResource(R.drawable.call_unrece);
			String name = mArrayList.get(position).getCallLog().getName();
			if (name.equals("")) {
				name = mArrayList.get(position).getCallLog().getTeleNumber();
			}
			
			if (name.equals("")) {
				name = "无号码";
			}
			((TextView)convertView.findViewById(R.id.calllogs_item_number)).
			setText(name);
			
			
			String time = mArrayList.get(position).getCallLog().getTime();
			if (time.length() > 14) {
				t.parse(time.substring(0, 14));
			}
			String time_show = String.format("%d-%d-%d  %02d:%02d", t.year, t.month+1, t.monthDay, t.hour, t.minute);
			if ((t.month == t_now.month)) { 
				if (t_now.monthDay == (t.monthDay + 1)) {
					time_show = String.format(" 昨天 %02d:%02d",  t.hour, t.minute);
				} else if (t_now.monthDay == t.monthDay) {
					time_show = String.format("今天 %02d:%02d",  t.hour, t.minute);
				}
			}
			((TextView)convertView.findViewById(R.id.calllogs_item_time)).
			setText(time_show);
			

			return convertView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mArrayList.size();
		}
		
	}
	
	public CalllogsView(Context context, BtApplication arg, Handler arg1) {
		super(context, arg, arg1);
		// TODO Auto-generated constructor stub
		mBluetoothDeviceAdapter = new BluetoothDeviceAdapter(context);
		t = new Time();
		t_now = new Time("GMT+8");
		t_now.setToNow();
		mNotifyView = new NotifyView(context, arg, mHandler);
	}

	public boolean isNull() {
		return (mBluetoothDeviceAdapter.mArrayList.size() == 0)?true:false;
	}
	
	@Override
	public void release() {
		// TODO Auto-generated method stub
		Log.d("onflate","release is ?");
		if (mBluetoothDeviceAdapter != null) {
			mBluetoothDeviceAdapter.mArrayList.clear();
			mBluetoothDeviceAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void initComponent() {
		// TODO Auto-generated method stub
		contacts_list = (ListView) mView.findViewById(R.id.calllogs_list);
		contacts_list.setAdapter(mBluetoothDeviceAdapter);
		contacts_list.setOnItemClickListener(mOnItemClickListener);
		contacts_list.setOnItemLongClickListener(mOnItemLongClickListener);
		if (mPopWindow == null) {
			mPopWindow = new PopupWindow(mNotifyView.createView(R.layout.bluetooth_del_notify) , 500, 250);
			mNotifyView.setContext(mContext.getResources().getString(R.string.del_calllog));
		}
	}

	public void registerOnPimProgress(onPimProgress arg) {
		mOnPimProgress = arg;
	}
	
	public void unregisterOnPimProgress() {
		mOnPimProgress = null;
	}
	
	@Override
	public void onCalllogs(IVTPimData arg0) {
		// TODO Auto-generated method stub
		if (mOnPimProgress != null) {
			mOnPimProgress.onPimProgress(mBluetoothDeviceAdapter.getCount());
		}
		if (mBluetoothDeviceAdapter != null) {
			int count = mBluetoothDeviceAdapter.mArrayList.size();
			int index = count;
			String time = arg0.getCallLog().getTime();
			if (time.length() > 14) {
				t.parse(time.substring(0, 14));
			}
			for (int i = 0; i < count; i++) {
				Time tmp = new Time();
				String tmp_time = mBluetoothDeviceAdapter.mArrayList.get(i).getCallLog().getTime();
				if (tmp_time.length() > 14) {
					tmp.parse(tmp_time.substring(0, 14));
				}
				if (t.year > tmp.year) {
					index = i;
					break;
				} else if (t.year == tmp.year) {
					if (t.month > tmp.month) {
						index = i;
						break;
					} else if (t.month == tmp.month) {
						if (t.monthDay > tmp.monthDay) {
							index = i;
							break;
						} else if (t.monthDay == tmp.monthDay) {
							if (t.hour > tmp.hour) {
								index = i;
								break;
							} else if (t.hour == tmp.hour) {
								if (t.minute > tmp.minute) {
									index = i;
									break;
								} else if (t.minute == tmp.minute) {
									if (t.second > tmp.second) {
										index = i;
										break;
									}
								}
							}
						}
					}
				}
			}
			mBluetoothDeviceAdapter.mArrayList.add(index, arg0);
			mBluetoothDeviceAdapter.notifyDataSetChanged();
		}
	}

	public void onDisconnected() {
		if (mPopWindow != null && mPopWindow.isShowing()) {
			mPopWindow.dismiss();
		}
	}
	
	public void startDownload() {
		if (contacts_list != null) {
			//contacts_list.setVisibility(View.INVISIBLE);
		}
	}
	
	public void stopDownload() {
		if (contacts_list != null) {
			//contacts_list.setVisibility(View.VISIBLE);
		}

		mBluetoothDeviceAdapter.notifyDataSetChanged();
	}
}


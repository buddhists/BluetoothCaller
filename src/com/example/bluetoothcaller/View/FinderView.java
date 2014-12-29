package com.example.bluetoothcaller.View;

import java.util.ArrayList;

import android.bluesoleil.IVTPimData;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.bluetoothcaller.Activity.BtApplication;
import com.example.bluetoothcaller.Activity.R;
import com.example.bluetoothcaller.View.ContactsView.Sort;

public class FinderView extends InflateView {

	private ListView mListView;
	private BluetoothDeviceAdapter mBluetoothDeviceAdapter;
	
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ListView listView = (ListView)parent;
			IVTPimData sort = (IVTPimData)listView.getAdapter().getItem(position);
			String num = sort.getContact().getTelephoneList().get(0).getNumber();
			Log.d("onflate","11");
			if (mBtApplication.mBluetoothCallService != null && (num.length() != 0)) {
				Log.d("onflate","12");
				mBtApplication.mBluetoothCallService.Dail(num);
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
					Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.bluetooth_finder_item, null);
			}
			((TextView)convertView.findViewById(R.id.finder_item_name)).
			setText(((IVTPimData)getItem(position)).getContact().getName());
			return convertView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mArrayList.size();
		}
		
	}
	
	public FinderView(Context context, BtApplication arg, Handler arg1) {
		super(context, arg, arg1);
		// TODO Auto-generated constructor stub
		mBluetoothDeviceAdapter = new BluetoothDeviceAdapter(context);
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		mBluetoothDeviceAdapter.mArrayList.clear();
	}

	@Override
	protected void initComponent() {
		// TODO Auto-generated method stub
		mListView = (ListView)mView.findViewById(R.id.finder_list);
		mListView.setAdapter(mBluetoothDeviceAdapter);
		mListView.setOnItemClickListener(mOnItemClickListener);
	}
	
	public void add(IVTPimData arg) {
		mBluetoothDeviceAdapter.mArrayList.add(arg);
		mBluetoothDeviceAdapter.notifyDataSetChanged();
	}
}

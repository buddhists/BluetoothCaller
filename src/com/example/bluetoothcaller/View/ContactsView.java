package com.example.bluetoothcaller.View;

import java.util.ArrayList;
import com.example.bluetoothcaller.Activity.BtApplication;
import com.example.bluetoothcaller.Activity.R;
import com.example.bluetoothcaller.Service.onContactsCallback;

import android.app.Activity;
import android.bluesoleil.IVTPimData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


public class ContactsView extends InflateView implements onContactsCallback , onAssort{
	
	private final static String TAG = "ContactsView";
	private ListView contacts_list;
	private BluetoothDeviceAdapter mBluetoothDeviceAdapter = null;
	private int count = 0;
	private onPimProgress mOnPimProgress;
	private PopupWindow mPopFinderWindow;
	private NotifyView mNotifyView;
	private EditText mEditText;
	private FinderView mFinderView;
	private AssortView mAssortView;
	private int mSelectedIndex = -1;
	private int mSelectIndex = -1;
	private String[] assort = { "A", "B", "C", "D", "E", "F", "G",
			"H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
			"U", "V", "W", "X", "Y", "Z" };

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			switch (view.getId()) {
			case R.id.search_id:
				if (contacts_list != null) {
					contacts_list.setSelection(mSelectIndex);
				}
				break;
			}
		}
		
	};
	
	private TextWatcher mTextWatcher = new TextWatcher() {
		private CharacterParser characterParser = null;
		
		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
			if (characterParser == null) {
				characterParser = CharacterParser.getInstance();
			}
			
			mFinderView.release();
			if (arg0.length() > 0 ) {
				boolean isSet = false;
				String pinyin = characterParser.getSelling(arg0.toString());
				String sortString = pinyin.substring(0, 1).toUpperCase();
				for (int i = 0; i < mBluetoothDeviceAdapter.getCount(); i++) {
					if (((Sort)(mBluetoothDeviceAdapter.getItem(i))).type == Sort.TYPE_HEADER) {
						if (((Sort)(mBluetoothDeviceAdapter.getItem(i))).alpha.equals(sortString)) {
							int count = ((Sort)(mBluetoothDeviceAdapter.getItem(i))).SetNum;
							mSelectIndex = i;
							if (count != 0) {
								for (int j = 1; j <= count; j++) {
									if (!isSet && ((Sort)(mBluetoothDeviceAdapter.getItem(i+j))).data.getContact().getName().subSequence(0, 1).equals(arg0.toString().substring(0, 1))) {
										mSelectIndex = i+j;
										isSet = true;
									}
									mFinderView.add(((Sort)(mBluetoothDeviceAdapter.getItem(i+j))).data);
								}
								if (mPopFinderWindow != null && !mPopFinderWindow.isShowing()) {
									mPopFinderWindow.setFocusable(true);
									mPopFinderWindow.setTouchable(true);
									mPopFinderWindow.setOutsideTouchable(true);
									mPopFinderWindow.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
									mPopFinderWindow.showAsDropDown(mEditText, 0, 10);
								}
								break;
							}
						}
					}
				}
			} else {
				mSelectIndex = 0;
				if (mPopFinderWindow != null && mPopFinderWindow.isShowing()) {
					mPopFinderWindow.dismiss();
				}
			}
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
		}
		
	};
	
	private Handler mHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case R.id.cancel_btn:
				if (mPopFinderWindow != null && mPopFinderWindow.isShowing()) {
					mPopFinderWindow.dismiss();
				}
				break;
			case R.id.confirm_btn:
				if (mPopFinderWindow != null && mPopFinderWindow.isShowing()) {
					mPopFinderWindow.dismiss();
				}
				
				if (mSelectedIndex != -1) {
					mBluetoothDeviceAdapter.mArrayList.remove(mSelectedIndex);
					mBluetoothDeviceAdapter.notifyDataSetChanged();
				}
				break;
			}
		}
	};
	
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ListView listView = (ListView)parent;
			Sort sort = (Sort)listView.getAdapter().getItem(position);
			if (sort.type == Sort.TYPE_DATA) {
				String num = sort.data.getContact().getTelephoneList().get(0).getNumber();
				if (mBtApplication.mBluetoothCallService != null && (num.length() != 0)) {
					mBtApplication.mBluetoothCallService.Dail(num);
				}
			}
			
			if (mPopFinderWindow != null && mPopFinderWindow.isShowing()) {
				mPopFinderWindow.dismiss();
			}
		}
	};
	
	private OnItemLongClickListener mOnItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub
			if (mBluetoothDeviceAdapter.mArrayList.get(arg2).type == Sort.TYPE_HEADER) {
				return true;
			}
			mSelectedIndex = arg2;
			mPopFinderWindow.setFocusable(true);
			mPopFinderWindow.setTouchable(true);
			mPopFinderWindow.setOutsideTouchable(true);
			mPopFinderWindow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.notify_bk));
			mPopFinderWindow.setContentView(mNotifyView.createView(R.layout.bluetooth_del_notify));
			mNotifyView.setContext(mContext.getResources().getString(R.string.del_contacts));
			mPopFinderWindow.showAtLocation(contacts_list, Gravity.CENTER, 0, 0);
			return true;
		}
		
	};
	
	class Sort
	{
		public final static int TYPE_DATA = 0x0001;
		public final static int TYPE_HEADER = 0x0002;
		public int type = TYPE_DATA;
		public String alpha;
		public IVTPimData data;
		public int SetNum = 0;
	}
	
	class BluetoothDeviceAdapter extends BaseAdapter {
		private Context mContext;
		private ArrayList<Sort> mArrayList;
		private CharacterParser characterParser;
		
		public BluetoothDeviceAdapter(Context context) {  
			mContext = context;
			characterParser = CharacterParser.getInstance();
			mArrayList = new ArrayList<Sort>();
			for (int i = 0; i < assort.length; i++) {
				Sort sort = new Sort();
				sort.type = Sort.TYPE_HEADER;
				sort.alpha = assort[i];
				mArrayList.add(sort);
			}
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
			if (mArrayList.get(position).type == Sort.TYPE_DATA) {
				//if (convertView == null) {
					convertView = ((LayoutInflater) mContext.getSystemService(
							Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.bluetooth_main_contacts_item, null);
				//}
				String name = mArrayList.get(position).data.getContact().getName();
				String num = null;
				if (mArrayList.get(position).data.getContact().getTelephoneList() != null) {
					num = mArrayList.get(position).data.getContact().getTelephoneList().get(0).getNumber();
				}
				if (name == null || num == null) {
					convertView.setVisibility(View.GONE);
				} else {
					convertView.setVisibility(View.VISIBLE);
					((TextView)convertView.findViewById(R.id.contacts_item_name)).
					setText(name);
					((TextView)convertView.findViewById(R.id.contacts_item_number)).
					setText(num);
				}
			} else if (mArrayList.get(position).type == Sort.TYPE_HEADER) {
				//if (convertView == null) {
				convertView = ((LayoutInflater) mContext.getSystemService(
						Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.bluetooth_main_contacts_alpha_item, null);

				((TextView)convertView.findViewById(R.id.alpha_name)).
				setText(mArrayList.get(position).alpha);
				//}
				if (mArrayList.get(position).SetNum == 0) {
					//convertView.destroyDrawingCache();
					convertView.setVisibility(View.INVISIBLE);
					convertView.setVisibility(View.GONE);
				} else {					
					convertView.setVisibility(View.VISIBLE);
				}
			}
			return convertView;
		}

		public void addData(IVTPimData arg) {
			String pinyin = characterParser.getSelling(arg.getContact().getName());
			String sortString = pinyin.substring(0, 1).toUpperCase();
			int index = mArrayList.size();
			for (int i = 0; i < mArrayList.size(); i++) {
				if (mArrayList.get(i).type == Sort.TYPE_HEADER) {
					if (mArrayList.get(i).alpha.equals(sortString)) {
						mArrayList.get(i).SetNum++;
						index = i+1;
						break;
					}
				}
			}
			Sort tmp = new Sort();
			tmp.data = arg;
			tmp.type = Sort.TYPE_DATA;
			mArrayList.add(index, tmp);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mArrayList.size();
		}
		
	}
	
	public ContactsView(Context context, BtApplication arg, Handler arg1) {
		super(context, arg, arg1);
		mBluetoothDeviceAdapter = new BluetoothDeviceAdapter(context);
		mFinderView = new FinderView(context, arg, arg1);
		mNotifyView = new NotifyView(context, arg, mHandler);
	}
	
	protected void initComponent() {
		contacts_list = (ListView) mView.findViewById(R.id.contacts_list);
		mAssortView = (AssortView) mView.findViewById(R.id.assort);
		contacts_list.setAdapter(mBluetoothDeviceAdapter);
		contacts_list.setOnItemClickListener(mOnItemClickListener);
		contacts_list.setOnItemLongClickListener(mOnItemLongClickListener);
		mEditText = ((EditText) mView.findViewById(R.id.input_id));
		mEditText.addTextChangedListener(mTextWatcher);
		mView.findViewById(R.id.search_id).setOnClickListener(mOnClickListener);
		if (mPopFinderWindow == null) {
			mPopFinderWindow = new PopupWindow(mFinderView.createView(R.layout.bluetooth_finder) , 500, 250);
		}
		
		if (mAssortView != null) {
			mAssortView.registerOnAssort(this);
		}
	}

	public void registerOnPimProgress(onPimProgress arg) {
		mOnPimProgress = arg;
	}
	
	public void unregisterOnPimProgress() {
		mOnPimProgress = null;
	}
	
	public void onDisconnected() {
		if (mPopFinderWindow != null && mPopFinderWindow.isShowing()) {
			mPopFinderWindow.dismiss();
		}
	}
	
	public void startDownload() {
		if (contacts_list != null) {
			contacts_list.setVisibility(View.INVISIBLE);
		}
	}
	
	public void stopDownload() {
		if (contacts_list != null) {
			contacts_list.setVisibility(View.VISIBLE);
		}
		mBluetoothDeviceAdapter.notifyDataSetChanged();
	}
	
	public boolean isNull() {
		return (mBluetoothDeviceAdapter.mArrayList.size() <= assort.length)?true:false;
	}
	
	public int getContactsNumber() {
		return mBluetoothDeviceAdapter.getCount() - assort.length;
	}
	
	@Override
	public void onContacts(IVTPimData arg0) {
		// TODO Auto-generated method stub
		if (mBluetoothDeviceAdapter != null) {
			mBluetoothDeviceAdapter.addData(arg0);
			//mBluetoothDeviceAdapter.notifyDataSetChanged();
			if (mOnPimProgress != null) {
				mOnPimProgress.onPimProgress(mBluetoothDeviceAdapter.getCount() - assort.length);
			}
		}
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		if (mBluetoothDeviceAdapter != null) {
			mBluetoothDeviceAdapter.mArrayList.clear();
			for (int i = 0; i < assort.length; i++) {
				Sort sort = new Sort();
				sort.type = Sort.TYPE_HEADER;
				sort.alpha = assort[i];
				mBluetoothDeviceAdapter.mArrayList.add(sort);
			}
			mBluetoothDeviceAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onAssort(String alpha) {
		// TODO Auto-generated method stub
		for (int i = 0; i < mBluetoothDeviceAdapter.mArrayList.size(); i++) {
			if (mBluetoothDeviceAdapter.mArrayList.get(i).type == Sort.TYPE_HEADER) {
				if (mBluetoothDeviceAdapter.mArrayList.get(i).alpha.equals(alpha)) {
					contacts_list.setSelection(i);
					break;
				}
			}
		}
	}
}

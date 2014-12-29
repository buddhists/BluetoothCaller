package com.example.bluetoothcaller.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class AssortView extends Button {
	private onAssort mOnAssort;

	public interface OnTouchAssortListener {
		public void onTouchAssortListener(String s);
		public void onTouchAssortUP();
	}

	public AssortView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public AssortView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public AssortView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public void registerOnAssort(onAssort arg) {
		mOnAssort = arg;
	}
	
	public void unregisterOnAssort() {
		
	}
	
	// index resources
	private String[] assort = { "A", "B", "C", "D", "E", "F", "G",
			"H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
			"U", "V", "W", "X", "Y", "Z" };
	private Paint paint = new Paint();
	// finger index
	private int selectIndex = -1;
	// ��ĸ������
	private OnTouchAssortListener onTouch;


	public void setOnTouchAssortListener(OnTouchAssortListener onTouch) {
		this.onTouch = onTouch;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		int height = getHeight();
		int width = getWidth();
		int interval = height / assort.length;

		for (int i = 0, length = assort.length; i < length; i++) {
			// soft the edge of the pictures
			paint.setAntiAlias(true);
			paint.setColor(Color.parseColor("#323232"));
			if (i == selectIndex) {
				// selectIndex indicates the index pointed by your finger
				paint.setColor(Color.WHITE);
				paint.setFakeBoldText(true);
				//paint.setTextSize(20);
			}
			// 
			float xPos = width / 2 - paint.measureText(assort[i]) / 2;
			// ������ĸ��Y���
			float yPos = interval * i + interval;
			canvas.drawText(assort[i], xPos, yPos, paint);
			paint.reset();
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		float y = event.getY();
		int index = (int) (y / getHeight() * assort.length);
		if (index >= 0 && index < assort.length) {

			switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE:
				// ����ı�
				if (selectIndex != index) {
					selectIndex = index;
					if (mOnAssort != null) {
						mOnAssort.onAssort(assort[selectIndex]);
					}

				}
				break;
			case MotionEvent.ACTION_DOWN:
				selectIndex = index;
				if (mOnAssort != null) {
					mOnAssort.onAssort(assort[selectIndex]);
				}

				break;
			case MotionEvent.ACTION_UP:
				if (onTouch != null) {
					onTouch.onTouchAssortUP();
				}
				selectIndex = -1;
				break;
			}
		} else {
			selectIndex = -1;
			if (onTouch != null) {
				onTouch.onTouchAssortUP();
			}
		}
		invalidate();

		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}
}

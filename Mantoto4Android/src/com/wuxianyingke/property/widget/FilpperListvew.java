package com.wuxianyingke.property.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * �Զ���FipperListView
 * 
 * @author: wll
 */
public class FilpperListvew extends ListView {
	private float myLastX = -1;
	private float myLastY = -1;
	private boolean delete = false;
	// �Զ���Ļ���ɾ������
	private FilpperDeleteListener filpperDeleterListener;

	public FilpperListvew(Context context) {
		super(context);
	}

	public FilpperListvew(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// ��õ�һ�����x����
			myLastX = ev.getX(0);
			myLastY = ev.getY(0);
			break;

		case MotionEvent.ACTION_MOVE:
			// �õ����һ���������
			float deltaX = ev.getX(ev.getPointerCount() - 1) - myLastX;
			float deltaY = Math
					.abs(ev.getY(ev.getPointerCount() - 1) - myLastY);
			// ���Ի���ɾ�������������򻬶�����100����ֱ��С��50
			if (deltaX > 100.0 && deltaY < 50) {
				delete = true;
			}
			break;

		case MotionEvent.ACTION_UP:
			if (delete && filpperDeleterListener != null) {
				filpperDeleterListener.filpperDelete(myLastX, myLastY);
			}
			reset();
			break;
		}
		return super.onTouchEvent(ev);
	}

	public void reset() {
		delete = false;
		myLastX = -1;
		myLastY = -1;
	}

	public void setFilpperDeleteListener(FilpperDeleteListener f) {
		filpperDeleterListener = f;
	}

	// �Զ���Ľӿ�
	public interface FilpperDeleteListener {
		public void filpperDelete(float xPosition, float yPosition);
	}

}

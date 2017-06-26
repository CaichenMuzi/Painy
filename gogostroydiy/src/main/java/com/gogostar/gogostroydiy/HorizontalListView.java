package com.gogostar.gogostroydiy;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Scroller;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Administrator on 2017/3/27.
 * <p>
 * 自定义可航向滑动的ListView
 */

public class HorizontalListView extends AdapterView<ListAdapter> {

	// 适配器
	protected ListAdapter mAdapter;
	// 整型变量
	private int mLeftViewIndex = -1;
	private int mRightViewIndex = 0;
	protected int mCurrentX;
	protected int mNextX;
	private int mMaxX = Integer.MAX_VALUE;
	private int mDisplayOffset = 0;
	//　滚动工具类
	protected Scroller mScroller;
	// 手势监听类
	private GestureDetector mGesture;
	// View队列
	private Queue<View> mRemovedViewQueue = new LinkedList<View>();
	// 监听事件
	private OnItemSelectedListener mOnItemSelected;
	private OnItemClickListener mOnItemClicked;
	private OnItemLongClickListener mOnItemLongClicked;
	private boolean mDataChanged = false;

	/**
	 * 构造函数
	 *
	 * @param context 当前页面
	 * @param attrs   属性
	 */
	public HorizontalListView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	/**
	 * 定义变量
	 */
	private synchronized void initView() {
		mLeftViewIndex = -1;
		mRightViewIndex = 0;
		mDisplayOffset = 0;
		mCurrentX = 0;
		mNextX = 0;
		mMaxX = Integer.MAX_VALUE;
		mScroller = new Scroller(getContext());
		mGesture = new GestureDetector(getContext(), mOnGesture);
	}

	@Override
	public void setOnItemSelectedListener(OnItemSelectedListener listener) {
		mOnItemSelected = listener;
	}

	@Override
	public void setOnItemClickListener(OnItemClickListener listener) {
		mOnItemClicked = listener;
	}

	@Override
	public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
		mOnItemLongClicked = longClickListener;
	}

	/**
	 * 观察者模式
	 */
	private DataSetObserver mDataObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			synchronized ((HorizontalListView.this)) {
				mDataChanged = true;
			}
			invalidate();
			requestLayout();
		}

		@Override
		public void onInvalidated() {
			reset();
			invalidate();
			requestLayout();
		}
	};

	@Override
	public ListAdapter getAdapter() {
		return mAdapter;
	}

	@Override
	public View getSelectedView() {
		return null;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		if (mAdapter != null) {
			mAdapter.unregisterDataSetObserver(mDataObserver);
		}
		mAdapter = adapter;
		mAdapter.registerDataSetObserver(mDataObserver);
		reset();
	}

	/**
	 * 定义方法reset（同步）
	 */
	private synchronized void reset() {
		initView();
		removeAllViewsInLayout();
		requestLayout();
	}

	@Override
	public void setSelection(int position) {

	}

	/**
	 * 定义addAndMeasureChild方法
	 *
	 * @param child   视图控件View
	 * @param viewPos 视图index
	 */
	private void addAndMeasureChild(final View child, int viewPos) {
		// 获取View的LayoutParams
		LayoutParams params = child.getLayoutParams();
		if (params == null) {
			params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		}

		// 配置控件布局
		addViewInLayout(child, viewPos, params, true);
		child.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST), MeasureSpec
				.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));
	}

	@Override
	protected synchronized void onLayout(boolean changed, int left, int top, int right, int
			bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (mAdapter == null) {
			return;
		}

		// 如果数据改变，重新加载布局
		if (mDataChanged) {
			int oldCurrentX = mCurrentX;
			initView();
			removeAllViewsInLayout();
			mNextX = oldCurrentX;
			mDataChanged = false;
		}

		// 计算currX，currY，并检测是否已完成“滚动”
		if (mScroller.computeScrollOffset()) {
			int scrollX = mScroller.getCurrX();
			mNextX = scrollX;
		}

		if (mNextX <= 0) {
			mNextX = 0;
			mScroller.forceFinished(true);
		}
		if (mNextX >= mMaxX) {
			mNextX = mMaxX;
			mScroller.forceFinished(true);
		}

		int dx = mCurrentX - mNextX;

		removeNonVisibleItems(dx);
		fillList(dx);
		positionItems(dx);

		mCurrentX = mNextX;

		if (!mScroller.isFinished()) {
			post(new Runnable() {
				@Override
				public void run() {
					requestLayout();
				}
			});
		}
	}

	private void fillList(final int dx) {
		int edge = 0;
		View child = getChildAt(getChildCount() - 1);
		if (child != null) {
			edge = child.getRight();
		}
		fillListRight(edge, dx);

		edge = 0;
		child = getChildAt(0);
		if (child != null) {
			edge = child.getLeft();
		}
		fillListLeft(edge, dx);
	}

	private void fillListRight(int rightEdge, final int dx) {
		while (rightEdge + dx < getWidth() && mRightViewIndex < mAdapter.getCount()) {
			View child = mAdapter.getView(mRightViewIndex, mRemovedViewQueue.poll(), this);
			addAndMeasureChild(child, -1);
			rightEdge += child.getMeasuredWidth();

			if (mRightViewIndex == mAdapter.getCount() - 1) {
				mMaxX = mCurrentX + rightEdge - getWidth();
			}

			if (mMaxX < 0) {
				mMaxX = 0;
			}
			mRightViewIndex++;
		}
	}

	private void fillListLeft(int leftEdge, final int dx) {
		while (leftEdge + dx > 0 && mLeftViewIndex >= 0) {
			View child = mAdapter.getView(mLeftViewIndex, mRemovedViewQueue.poll(), this);
			addAndMeasureChild(child, 0);
			leftEdge -= child.getMeasuredWidth();
			mLeftViewIndex--;
			mDisplayOffset -= child.getMeasuredWidth();
		}
	}

	private void removeNonVisibleItems(final int dx) {
		View child = getChildAt(0);
		while (child != null && child.getRight() + dx <= 0) {
			mDisplayOffset += child.getMeasuredWidth();
			mRemovedViewQueue.offer(child);
			removeViewInLayout(child);
			mLeftViewIndex++;
			child = getChildAt(0);
		}

		child = getChildAt(getChildCount() - 1);
		while (child != null && child.getLeft() + dx >= getWidth()) {
			mRemovedViewQueue.offer(child);
			removeViewInLayout(child);
			mRightViewIndex--;
			child = getChildAt(getChildCount() - 1);
		}
	}

	private void positionItems(final int dx) {
		if (getChildCount() > 0) {
			mDisplayOffset += dx;
			int left = mDisplayOffset;
			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				int childWidth = child.getMeasuredWidth();
				child.layout(left, 0, left + childWidth, child.getMeasuredHeight());
				left += childWidth + child.getPaddingRight();
			}
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean handled = super.dispatchTouchEvent(ev);
		handled |= mGesture.onTouchEvent(ev);
		return handled;
	}

	protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		synchronized (HorizontalListView.this) {
			mScroller.fling(mNextX, 0, (int) -velocityX, 0, 0, mMaxX, 0, 0);
		}
		requestLayout();

		return true;
	}

	protected boolean onDown(MotionEvent e) {
		mScroller.forceFinished(true);
		return true;
	}

	private GestureDetector.OnGestureListener mOnGesture = new GestureDetector
			.SimpleOnGestureListener() {
		@Override
		public boolean onDown(MotionEvent e) {
			return HorizontalListView.this.onDown(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			return HorizontalListView.this.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			synchronized (HorizontalListView.this) {
				mNextX += (int) distanceX;
			}
			requestLayout();

			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			for (int i = 0; i < getChildCount(); i++) {
				View child = getChildAt(i);
				if (isEventWithinView(e, child)) {
					if (mOnItemClicked != null) {
						mOnItemClicked.onItemClick(HorizontalListView.this, child, mLeftViewIndex
								+ 1 + i, mAdapter.getItemId(mLeftViewIndex + 1 + i));
					}
					if (mOnItemSelected != null) {
						mOnItemSelected.onItemSelected(HorizontalListView.this, child,
								mLeftViewIndex + 1 + i, mAdapter.getItemId(mLeftViewIndex + 1 +
										i));
					}
					break;
				}
			}
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				View child = getChildAt(i);
				if (isEventWithinView(e, child)) {
					if (mOnItemLongClicked != null) {
						mOnItemLongClicked.onItemLongClick(HorizontalListView.this, child,
								mLeftViewIndex + 1 + i, mAdapter.getItemId(mLeftViewIndex + 1 +
										i));
					}
					break;
				}
			}
		}

		private boolean isEventWithinView(MotionEvent e, View child) {
			Rect viewRect = new Rect();
			int[] childPosition = new int[2];
			child.getLocationOnScreen(childPosition);
			int left = childPosition[0];
			int right = left + child.getWidth();
			int top = childPosition[1];
			int bottom = top + child.getHeight();
			viewRect.set(left, top, right, bottom);
			return viewRect.contains((int) e.getRawX(), (int) e.getRawY());
		}
	};
}
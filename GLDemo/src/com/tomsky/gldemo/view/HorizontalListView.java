package com.tomsky.gldemo.view;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Scroller;

public class HorizontalListView extends AdapterView<ListAdapter> {
	
	public boolean mAlwaysOverrideTouch = true;
	protected ListAdapter mAdapter;
	private int mFirstPosition = 0;
	
	private int mLeftViewIndex = -1;
	private int mRightViewIndex = 0;
	protected int mCurrentX;
	protected int mNextX;
	private int mMaxX = Integer.MAX_VALUE;
	private int mDisplayOffset = 0;
	protected Scroller mScroller;
	private GestureDetector mGesture;
	private Queue<View> mRemovedViewQueue = new LinkedList<View>();
	private OnItemSelectedListener mOnItemSelected;
	private OnItemClickListener mOnItemClicked;
	private OnItemLongClickListener mOnItemLongClicked;
	private boolean mDataChanged = false;
	protected OnLayoutChangeListener mOnLayoutChangeListener;

    public interface OnLayoutChangeListener {
        public static final int SCROLL_STATE_IDLE = 0;
        public static final int SCROLL_STATE_TOUCH_SCROLL = 1;
        public static final int SCROLL_STATE_FLING = 2;

        public void layout(int x);
    }	
	
	private VelocityTracker mVelocityTracker;
	
	private boolean mIsBeingDragged;
	private int mActivePointerId = INVALID_POINTER;
	private float mLastMotionX;
	private static final int INVALID_POINTER = -1;
	
	private int mTouchSlop;
	
	public HorizontalListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	
	private synchronized void initView() {
		mFirstPosition = 0;
		
		mLeftViewIndex = -1;
		mRightViewIndex = 0;
		mDisplayOffset = 0;
		mCurrentX = 0;
		mNextX = 0;
		mMaxX = Integer.MAX_VALUE;
		mScroller = new Scroller(getContext());
		mGesture = new GestureDetector(getContext(), mOnGesture);
		
		
		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
	}
	
	private synchronized void refrushView() {
		mRightViewIndex = mLeftViewIndex + 1;
		mMaxX = Integer.MAX_VALUE;
	}
	
	@Override
	public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
		mOnItemSelected = listener;
	}
	
	@Override
	public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
		mOnItemClicked = listener;
	}
	
	@Override
	public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
		mOnItemLongClicked = listener;
	}
	
	public void setOnLayoutChangeListener(OnLayoutChangeListener listener) {
		mOnLayoutChangeListener = listener;
	}
	
	@Override
	public int getCount(){
		return mAdapter == null ? 0 : mAdapter.getCount();
	}
	
	public int getTotalLength(){
		if(mAdapter==null || this.getChildAt(0)==null)
			return 0;
		else{
			return  mAdapter.getCount()*getChildAt(0).getWidth();
		}
	}
		
	private DataSetObserver mDataObserver = new DataSetObserver() {
		
		@Override
		
		public void onChanged() {
			synchronized(HorizontalListView.this){
				mDataChanged = true;
			}
			invalidate();
			requestLayout();
		}
		
		@Override
		public void onInvalidated() {
			reset();
		}
		
	};
	
	@Override
	public ListAdapter getAdapter() {
		return mAdapter;
	}
	
	@Override
	public View getSelectedView() {
		//TODO: implement
		return null;
	}
	
	@Override
	public void setAdapter(ListAdapter adapter) {
        if (mAdapter != null && mDataObserver != null) {
            mAdapter.unregisterDataSetObserver(mDataObserver);
        }
		mAdapter = adapter;
		if (mAdapter != null) {
			mAdapter.registerDataSetObserver(mDataObserver);
			reset();
		} else {
			removeAllViewsInLayout();
			requestLayout();
		}
	}
	
	private synchronized void reset(){
		initView();
		removeAllViewsInLayout();
		requestLayout();
	}
	
	@Override
	public void setSelection(int position) {
		//TODO: implement
	}
	
	private void addAndMeasureChild(final View child, int viewPos) {
		LayoutParams params = child.getLayoutParams();
		if(params == null) {
			params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		}
		
		addViewInLayout(child, viewPos, params,  true);
		child.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST),
				MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));
	}
	
	@Override
	protected synchronized void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

        invalidate();
		if(mAdapter == null){
			initView();
			return;
		}
		
		if(mDataChanged){
			mNextX = mCurrentX;
			refrushView();
			removeAllViewsInLayout();
		}
		
		if(mScroller.computeScrollOffset()){
			int scrollx = mScroller.getCurrX();
			mNextX = scrollx;
		}
		
		if(mNextX <= 0){
			mNextX = 0;
			mScroller.forceFinished(true);
		}
		if(mNextX >= mMaxX) {
			mNextX = mMaxX;
			mScroller.forceFinished(true);
		}
		
		int dx = mCurrentX - mNextX;
		
		removeNonVisibleItems(dx);
		fillList(dx);
		positionItems(dx);
		mCurrentX = mNextX;
		
		mDataChanged = false;

		if (mOnLayoutChangeListener != null) {
			mOnLayoutChangeListener.layout(mNextX);
		}
		
		if(!mScroller.isFinished()){
			post(new Runnable(){
				@Override
				public void run() {
					requestLayout();
				}
			});
			
		}
	}
	
	@Override
	public void removeAllViewsInLayout() {
//		int count = getChildCount();
//		View child = null;
//		for (int i = 0; i < count; i++) {
//			child = getChildAt(i);
//			if (child != null) {
//				removeViewInLayout(child);
//				mRemovedViewQueue.offer(child);
//			}
//		}
		super.removeAllViewsInLayout();
	}
	
	private void fillList(final int dx) {
		if (mDataChanged) {
			fillListRight(mDisplayOffset, dx);
			fillListLeft(mDisplayOffset, dx);
		} else {
			int edge = 0;
			View child = getChildAt(getChildCount()-1);
			if(child != null) {
				edge = child.getRight();
			}
			fillListRight(edge, dx);
			
			edge = 0;
			child = getChildAt(0);
			if(child != null) {
				edge = child.getLeft();
			}
			fillListLeft(edge, dx);
		}
		
		
	}
	
	private void fillListRight(int rightEdge, final int dx) {
		while(rightEdge + dx < getWidth() && mRightViewIndex < mAdapter.getCount()) {
			View child = mAdapter.getView(mRightViewIndex, mRemovedViewQueue.poll(), this);
			addAndMeasureChild(child, -1);
			rightEdge += child.getMeasuredWidth();
			if(mRightViewIndex == mAdapter.getCount()-1) {
				mMaxX = mCurrentX + rightEdge - getWidth();
			}
			
			if (mMaxX < 0) {
				mMaxX = 0;
			}
			mRightViewIndex++;
		}
		
	}
	
	private void fillListLeft(int leftEdge, final int dx) {
		while(leftEdge + dx > 0 && mLeftViewIndex >= 0) {
			View child = mAdapter.getView(mLeftViewIndex, mRemovedViewQueue.poll(), this);
			addAndMeasureChild(child, 0);
			leftEdge -= child.getMeasuredWidth();
			mLeftViewIndex--;
			mDisplayOffset -= child.getMeasuredWidth();
		}
	}
	
	private void removeNonVisibleItems(final int dx) {
		View child = getChildAt(0);
		while(child != null && child.getRight() + dx <= 0) {
			mDisplayOffset += child.getMeasuredWidth();
			removeViewInLayout(child);
			mRemovedViewQueue.offer(child);
			mLeftViewIndex++;
			child = getChildAt(0);
			
		}
		
		child = getChildAt(getChildCount()-1);
		while(child != null && child.getLeft() + dx >= getWidth()) {
			mRemovedViewQueue.offer(child);
			removeViewInLayout(child);
			mRightViewIndex--;
			child = getChildAt(getChildCount()-1);
		}
	}
	
	private void positionItems(final int dx) {
		if(getChildCount() > 0){
			mDisplayOffset += dx;
			int left = mDisplayOffset;
			for(int i=0;i<getChildCount();i++){
				View child = getChildAt(i);
				int childWidth = child.getMeasuredWidth();
				child.layout(left, 0, left + childWidth, child.getMeasuredHeight());
				left += childWidth;
			}
		}
	}
	
	public void onPause(Map<String, Integer> map) {
		int count = getChildCount();
		View child = null;
		String key = null;
		for (int i = 0; i < count; i++) {
			child = getChildAt(i);
//			if (child != null) {
//				Object obj = child.getTag();
//				if (obj != null && obj instanceof LockScreenIconChannelViewHolder) {
//					View eachView = ((LockScreenIconChannelViewHolder) obj).mIcon;
//					if (eachView != null) {
//						LockScreenIconViewHolder eachViewHolder = (LockScreenIconViewHolder) eachView.getTag();
//						key = (String) eachViewHolder.mThumbnailIV.getTag();
//						if (key != null) {
//							Integer keyCount = map.get(key);
//							if (keyCount == null) {
//								map.put(key, Integer.valueOf(1));
//							} else {
//								map.put(key, Integer.valueOf(keyCount.intValue() + 1));
//							}
//							eachViewHolder.mThumbnailIV.setTag(null);
//						}
//					}
//				}
//			
//			}
		}
		mRemovedViewQueue.clear();
	}
	
	public void onStop() {
		removeAllViewsInLayout();
	}
	
	public synchronized void scrollTo(int x) {
		mScroller.startScroll(mNextX, 0, x - mNextX, 0);
		requestLayout();
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean handled = onInterceptTouchEvent(ev);//super.dispatchTouchEvent(ev);
//		Trace.d("@@@@", "dispatch touch event and event action = " + ev.getAction() + " && handled = " + handled);
		handled |= mGesture.onTouchEvent(ev);
//		Trace.d("@@@@", " && handled = " + handled);
		return handled;
	}
	
	protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		synchronized(HorizontalListView.this){
			mScroller.fling(mNextX, 0, (int)-velocityX, 0, 0, mMaxX, 0, 0);
		}
		requestLayout();
		
		return true;
	}
	
	protected boolean onDown(MotionEvent e) {
		mScroller.forceFinished(true);
		return true;
	}
	private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {
		
		@Override
		public boolean onDown(MotionEvent e) {
			return HorizontalListView.this.onDown(e);
		}
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return HorizontalListView.this.onFling(e1, e2, velocityX, velocityY);
		}
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			
			synchronized(HorizontalListView.this){
				mNextX += (int)distanceX;
			}
			requestLayout();
			
			return true;
		}
		
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			for(int i=0;i<getChildCount();i++){
				View child = getChildAt(i);
				if (isEventWithinView(e, child)) {
					if(mOnItemClicked != null){
						mOnItemClicked.onItemClick(HorizontalListView.this, 
								child, mLeftViewIndex + 1 + i, mAdapter.getItemId( mLeftViewIndex + 1 + i ));
					}
					if(mOnItemSelected != null){
						mOnItemSelected.onItemSelected(HorizontalListView.this, 
								child, mLeftViewIndex + 1 + i, mAdapter.getItemId( mLeftViewIndex + 1 + i ));
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
						mOnItemLongClicked.onItemLongClick(HorizontalListView.this, 
								child, mLeftViewIndex + 1 + i, mAdapter.getItemId(mLeftViewIndex + 1 + i));
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
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE) && (mIsBeingDragged)) {
			return true;
		}
		
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_MOVE: {
			final int activePointerId = mActivePointerId;
			if (activePointerId == INVALID_POINTER) {
				// If we don't have a valid id, the touch down wasn't on content.
				break;
			}
			
			final int pointerIndex = ev.findPointerIndex(activePointerId);
			final float x = ev.getX(pointerIndex);
			final int xDiff = (int) Math.abs(x - mLastMotionX);
			if (xDiff > mTouchSlop) {
				mIsBeingDragged = true;
				mLastMotionX = x;
				initVelocityTrackerIfNotExists();
				mVelocityTracker.addMovement(ev);
				ViewParent parent = getParent();
				if (parent != null) parent.requestDisallowInterceptTouchEvent(true);
			}
			break;
		}
		
		case MotionEvent.ACTION_DOWN: {
			if ((mIsBeingDragged = !mScroller.isFinished())) {
				final ViewParent parent = getParent();
				if (parent != null) {
					parent.requestDisallowInterceptTouchEvent(true);
				}
			}
			
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionX = ev.getX();
			mActivePointerId = ev.getPointerId(0);
			initOrResetVelocityTracker();
			mVelocityTracker.addMovement(ev);
			mIsBeingDragged = !mScroller.isFinished();
			break;
		}
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			/* Release the drag */
			mIsBeingDragged = false;
			mActivePointerId = INVALID_POINTER;
			break;
        case MotionEvent.ACTION_POINTER_DOWN: {
            final int index = ev.getActionIndex();
            mLastMotionX = ev.getX(index);
            mActivePointerId = ev.getPointerId(index);
            break;
        }
        case MotionEvent.ACTION_POINTER_UP:
            onSecondaryPointerUp(ev);
            mLastMotionX = (int) ev.getX(ev.findPointerIndex(mActivePointerId));
            break;
		}
		return mIsBeingDragged;
	}

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
                MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            // TODO: Make this decision more intelligent.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastMotionX = ev.getX(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }
	
	private void initVelocityTrackerIfNotExists() {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
	}
	private void initOrResetVelocityTracker() {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		} else {
			mVelocityTracker.clear();
		}
	}

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
}

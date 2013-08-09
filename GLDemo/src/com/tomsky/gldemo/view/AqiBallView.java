package com.tomsky.gldemo.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tomsky.gldemo.R;

public class AqiBallView extends View {
	
	private boolean mHaveFrame = false;
	private boolean mAdjustViewBounds = false;
	private int mMaxWidth = Integer.MAX_VALUE;
    private int mMaxHeight = Integer.MAX_VALUE;
    
	private int mDrawableWidth;
	private int mDrawableHeight;
	
	private float mCenterX, mCenterY; // View的中心点
	private float mBallTop, mBallLeft, mShadowTop; // 球的位置，阴影的位置
	private float mReadyDegrees;
	private volatile boolean isReadyRotate;
	
	// 静止状态
	private Drawable mOutterDrawable; // 球的外框
	private Drawable mBallDrawable;	  // 球
	private Drawable mShadowDrawable; // 球的阴影
	private Drawable mBallPressedDrawable; // 球的按住效果
	
	// 动态
	private Drawable mReadyBall; 		//准备动画外框，顺时针旋转
	private Drawable mReadyTrackBall;	//准备动画小点，逆时针旋转
	
	private static final int BALL_MODE_NORMAL = 0; // 初始状态
	private static final int BALL_MODE_READY = 1;  // 准备动画状态
	private static final int BALL_MODE_CLEAN = 2;  // 清洁状态
	
	private int mBallMode = BALL_MODE_NORMAL;      // 当前状态，默认为初始状态
	
	private Drawable mOverflowDrawable; // 溢出动画
	private boolean mOverflow;	// 是否开启溢出动画
	private float mOverflowScale = 1.0f; //溢出动画scale值
	
	private ColorFilter mColorFilter;
	
	public AqiBallView(Context context) {
		super(context);
		init();
	}
	public AqiBallView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public AqiBallView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		initDrawable();
		int w;
        int h;
        
        // Desired aspect ratio of the view's contents (not including padding)
        float desiredAspect = 0.0f;
        
        // We are allowed to change the view's width
        boolean resizeWidth = false;
        
        // We are allowed to change the view's height
        boolean resizeHeight = false;
        
        final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        if (mOutterDrawable == null) {
            // If no drawable, its intrinsic size is 0.
            mDrawableWidth = -1;
            mDrawableHeight = -1;
            w = h = 0;
        } else {
            w = mDrawableWidth;
            h = mDrawableHeight;
            if (w <= 0) w = 1;
            if (h <= 0) h = 1;

            // We are supposed to adjust view bounds to match the aspect
            // ratio of our drawable. See if that is possible.
            if (mAdjustViewBounds) {
                resizeWidth = widthSpecMode != MeasureSpec.EXACTLY;
                resizeHeight = heightSpecMode != MeasureSpec.EXACTLY;
                
                desiredAspect = (float) w / (float) h;
            }
        }
        
        int pleft = mPaddingLeft;
        int pright = mPaddingRight;
        int ptop = mPaddingTop;
        int pbottom = mPaddingBottom;

        int widthSize;
        int heightSize;

        if (resizeWidth || resizeHeight) {
            /* If we get here, it means we want to resize to match the
                drawables aspect ratio, and we have the freedom to change at
                least one dimension. 
            */

            // Get the max possible width given our constraints
            widthSize = resolveAdjustedSize(w + pleft + pright, mMaxWidth, widthMeasureSpec);

            // Get the max possible height given our constraints
            heightSize = resolveAdjustedSize(h + ptop + pbottom, mMaxHeight, heightMeasureSpec);

            if (desiredAspect != 0.0f) {
                // See what our actual aspect ratio is
                float actualAspect = (float)(widthSize - pleft - pright) /
                                        (heightSize - ptop - pbottom);
                
                if (Math.abs(actualAspect - desiredAspect) > 0.0000001) {
                    
                    boolean done = false;
                    
                    // Try adjusting width to be proportional to height
                    if (resizeWidth) {
                        int newWidth = (int)(desiredAspect * (heightSize - ptop - pbottom)) +
                                pleft + pright;
                        if (newWidth <= widthSize) {
                            widthSize = newWidth;
                            done = true;
                        } 
                    }
                    
                    // Try adjusting height to be proportional to width
                    if (!done && resizeHeight) {
                        int newHeight = (int)((widthSize - pleft - pright) / desiredAspect) +
                                ptop + pbottom;
                        if (newHeight <= heightSize) {
                            heightSize = newHeight;
                        }
                    }
                }
            }
        } else {
            /* We are either don't want to preserve the drawables aspect ratio,
               or we are not allowed to change view dimensions. Just measure in
               the normal way.
            */
            w += pleft + pright;
            h += ptop + pbottom;
                
            w = Math.max(w, getSuggestedMinimumWidth());
            h = Math.max(h, getSuggestedMinimumHeight());

            widthSize = myResolveSizeAndState(w, widthMeasureSpec, 0);
            heightSize = myResolveSizeAndState(h, heightMeasureSpec, 0);
        }

        setMeasuredDimension(widthSize, heightSize);
	}
	
	private void configureBounds() {
        if (mOutterDrawable == null || !mHaveFrame) {
            return;
        }
        int vwidth = getWidth();
        int vheight = getHeight();
        mCenterX = vwidth * 0.5f;
        mCenterY = vheight * 0.5f;
        mOutterDrawable.setBounds(0, 0, vwidth, vheight);
        
        if (mBallDrawable != null) {
        	int ballWidth = mBallDrawable.getIntrinsicWidth();
        	int ballHeight = mBallDrawable.getIntrinsicHeight();
        	mBallLeft = (vwidth - ballWidth) * 0.5f;
        	mBallTop = (vheight - ballHeight) * 0.5f;
        	mBallDrawable.setBounds(0, 0, ballWidth, ballHeight);
        	if (mBallPressedDrawable != null) {
        		mBallPressedDrawable.setBounds(0, 0, ballWidth, ballHeight);
        	}
        }
        if (mShadowDrawable != null) {
        	int shadowWidth = mShadowDrawable.getIntrinsicWidth();
        	int shadowHeight = mShadowDrawable.getIntrinsicHeight();
        	mShadowTop = vheight - shadowHeight;
        	mShadowDrawable.setBounds(0, 0, shadowWidth, shadowHeight);
        }
        if (mReadyBall != null) {
        	mReadyBall.setBounds(0, 0, vwidth, vheight);
        }
        if (mReadyTrackBall != null) {
        	mReadyTrackBall.setBounds(0, 0, vwidth, vheight);
        }
        if (mOverflowDrawable != null) {
        	mOverflowDrawable.setBounds(0, 0, vwidth, vheight);
        }
	}
	
	private int resolveAdjustedSize(int desiredSize, int maxSize,
			int measureSpec) {
		int result = desiredSize;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		switch (specMode) {
		case MeasureSpec.UNSPECIFIED:
			/*
			 * Parent says we can be as big as we want. Just don't be larger
			 * than max size imposed on ourselves.
			 */
			result = Math.min(desiredSize, maxSize);
			break;
		case MeasureSpec.AT_MOST:
			// Parent says we can be as big as we want, up to specSize.
			// Don't be larger than specSize, and don't be larger than
			// the max size imposed on ourselves.
			result = Math.min(Math.min(desiredSize, specSize), maxSize);
			break;
		case MeasureSpec.EXACTLY:
			// No choice. Do what we are told.
			result = specSize;
			break;
		}
		return result;
	}
	
    public static int myResolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize =  MeasureSpec.getSize(measureSpec);
        switch (specMode) {
        case MeasureSpec.UNSPECIFIED:
            result = size;
            break;
        case MeasureSpec.AT_MOST:
            if (specSize < size) {
                result = specSize | MEASURED_STATE_TOO_SMALL;
            } else {
                result = size;
            }
            break;
        case MeasureSpec.EXACTLY:
            result = specSize;
            break;
        }
        return result | (childMeasuredState&MEASURED_STATE_MASK);
    }
    
	@Override
	protected boolean setFrame(int left, int top, int right, int bottom) {
		boolean changed = super.setFrame(left, top, right, bottom);
        mHaveFrame = true;
        configureBounds();
        return changed;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawCanvas(canvas);
		
	}
	
	private void drawCanvas(Canvas canvas) {
		// TODO: 根据质量做colorfilter颜色变换，或者在线程里
		if (mBallMode == BALL_MODE_NORMAL) { // 初始状态
			if (mOutterDrawable != null) {
//			ColorFilter filter = new LightingColorFilter(Color.RED, Color.BLACK);
			mOutterDrawable.setColorFilter(mColorFilter);
			mOutterDrawable.draw(canvas);
			}
		} else if (mBallMode == BALL_MODE_READY) { // 准备动画状态
			if (mOverflow) {
				canvas.save();
				canvas.scale(mOverflowScale, mOverflowScale, mCenterX, mCenterY);
//				mOverflowDrawable.setColorFilter(mColorFilter);
				mOverflowDrawable.draw(canvas);
				canvas.restore();
			}
			canvas.save();
			canvas.rotate(mReadyDegrees, mCenterX, mCenterY);
			mReadyBall.setColorFilter(mColorFilter);
			mReadyBall.draw(canvas);
			canvas.restore();
			
			canvas.save();
			canvas.rotate(-mReadyDegrees, mCenterX, mCenterY);
			mReadyTrackBall.setColorFilter(mColorFilter);
			mReadyTrackBall.draw(canvas);
			canvas.restore();
		}
		
		if (mBallDrawable != null) {
			canvas.save();
			canvas.translate(mBallLeft, mBallTop);
			if (isPressed()) {
				mBallPressedDrawable.draw(canvas);
			} else {
				mBallDrawable.draw(canvas);
			}
			canvas.restore();
		}
		
		if (mShadowDrawable != null) {
			canvas.save();
			canvas.translate(mBallLeft, mShadowTop);
			mShadowDrawable.draw(canvas);
			canvas.restore();
		}
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		boolean result = super.onTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			invalidate();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
        	invalidate();
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
        	invalidate();
        }
		return result;
	}
	
	private void init() {
		Resources resources = getContext().getResources();
//		// 设置总高度
//		mMaxHeight = resources.getDimensionPixelSize(R.dimen.aqi_bg_max_height);
//		mAdjustViewBounds = true;
		
		setClickable(true);
		
		mColorFilter = new PorterDuffColorFilter(Color.BLUE,  PorterDuff.Mode.MULTIPLY);
		
		mReadyBall = resources.getDrawable(R.drawable.aqi_ready_ball);
		mReadyTrackBall = resources.getDrawable(R.drawable.aqi_ready_trackball);
		mOverflowDrawable = resources.getDrawable(R.drawable.aqi_overflow);
	}
	
	/**
	 * 初始化Background
	 */
	private void initDrawable() {
		Resources res = getContext().getResources();
		if (mOutterDrawable == null) {
			mOutterDrawable = res.getDrawable(R.drawable.aqi_normal_ball);
			mDrawableWidth = mOutterDrawable.getIntrinsicWidth();
			mDrawableHeight = mOutterDrawable.getIntrinsicHeight();
			configureBounds();
		}
		if (mBallDrawable == null) {
			mBallDrawable = res.getDrawable(R.drawable.aqi_center_ball);
		}
		if (mBallPressedDrawable == null) {
			mBallPressedDrawable = res.getDrawable(R.drawable.aqi_center_ball_pressed);
		}
		if (mShadowDrawable == null) {
			mShadowDrawable = res.getDrawable(R.drawable.aqi_ball_shadow);
		}
	}
	
	public synchronized void startReadyAnimation() {
		mBallMode = BALL_MODE_READY;
		if (isReadyRotate) return;
		isReadyRotate = true;
		new Thread() {
			public void run() {
				while(isReadyRotate) {
					mReadyDegrees += 5;
					if (mReadyDegrees == 360) {
						mReadyDegrees = 0;
					}
					if (mOverflow) {
						if (mOverflowScale > 1.2f) {
							mOverflow = false;
						} else {
							mOverflowScale += 0.1f;
						}
					}
					postInvalidate();
					try {
						Thread.sleep(60);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				postInvalidate();
			}
		}.start();
	}
	
	public synchronized void stopReadyAnimation() {
		isReadyRotate = false;
		mBallMode = BALL_MODE_NORMAL;
		mReadyDegrees = 0;
		mOverflow = false;
	}
	
	public synchronized void startClean() {
		if (mOverflow) return;
		mOverflow = true;
		mOverflowScale = 0.3f;
		// TODO:设定净化效果
		
	}
}

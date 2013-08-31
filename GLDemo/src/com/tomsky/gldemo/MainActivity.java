package com.tomsky.gldemo;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;

import com.tomsky.gldemo.es.MyGLRenderWithProjection;
import com.tomsky.gldemo.view.FirstGLSurfaceView;

public class MainActivity extends Activity {

	private GLSurfaceView mGLView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
		
//		mGLView = new MyGLSurfaceView(this);
//		mGLView = new GLSurfaceView(this);
//		mGLView.setEGLConfigChooser(false);
//		mGLView.setRenderer(new SquareRenderer(this, 7));
//		mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
//		mGLView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		
		mGLView = new FirstGLSurfaceView(this);
		setContentView(mGLView);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLView.onResume();
	}
	
	class MyGLSurfaceView extends GLSurfaceView {

		private final MyGLRenderWithProjection mRenderer;
		
		public MyGLSurfaceView(Context context) {
			super(context);
			
			// Create an OpenGL ES 2.0 context
			setEGLContextClientVersion(2);
			
			mRenderer = new MyGLRenderWithProjection();
			// Set the Renderer for drawing on the GLSurfaceView
			setRenderer(mRenderer);
			
			setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		}
		
		private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	    private float mPreviousX;
	    private float mPreviousY;

	    @Override
	    public boolean onTouchEvent(MotionEvent e) {
	        // MotionEvent reports input details from the touch screen
	        // and other input controls. In this case, you are only
	        // interested in events where the touch position changed.

	        float x = e.getX();
	        float y = e.getY();

	        switch (e.getAction()) {
	            case MotionEvent.ACTION_MOVE:

	                float dx = x - mPreviousX;
	                float dy = y - mPreviousY;

	                // reverse direction of rotation above the mid-line
	                if (y > getHeight() / 2) {
	                  dx = dx * -1 ;
	                }

	                // reverse direction of rotation to left of the mid-line
	                if (x < getWidth() / 2) {
	                  dy = dy * -1 ;
	                }

	                mRenderer.mAngle += (dx + dy) * TOUCH_SCALE_FACTOR;  // = 180.0f / 320
	                requestRender();
	        }

	        mPreviousX = x;
	        mPreviousY = y;
	        return true;
	    }
	}
}

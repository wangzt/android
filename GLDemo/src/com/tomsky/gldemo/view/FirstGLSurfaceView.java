package com.tomsky.gldemo.view;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

public class FirstGLSurfaceView extends GLSurfaceView {

	public FirstGLSurfaceView(Context context) {
		super(context);
		setEGLContextClientVersion(2);
		setRenderer(new MyGL20Render());
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	class MyGL20Render implements GLSurfaceView.Renderer {

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			GLES20.glViewport(0, 0, width, height);
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		}
		
	}
}

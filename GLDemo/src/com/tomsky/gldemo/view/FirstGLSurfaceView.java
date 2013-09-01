package com.tomsky.gldemo.view;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class FirstGLSurfaceView extends GLSurfaceView {

	public FirstGLSurfaceView(Context context) {
		super(context);
		setRenderer(new MyGL10Render());
//		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	class MyGL10Render implements GLSurfaceView.Renderer {
		private FloatBuffer mFVertexBuffer;
		
		private FloatBuffer mColorBuffer;
		
//		private ShortBuffer mIndexBuffer;
		
		private int one = 0x10000;
		private IntBuffer mQuaterVertexBuffer;
		private IntBuffer mQuaterColorBuffer;
		private ShortBuffer mQuaterIndexBuffer;
		
		private float mRotateTri = 0.0f;
		private float mRotateQua = 0.0f;
		
		public MyGL10Render() {
			float[] coords = {
					// 前侧面
					0.0f, 1.0f, 0f, // 上顶点
					-1.0f, -1.0f, 1.0f, // 左下顶点
					1.0f,  -1.0f, 1.0f, // 右下顶点
					
					// 右侧面
					0.0f, 1.0f, 0f,
					1.0f, -1.0f, 1.0f,
					1.0f, -1.0f, -1.0f,
					
					// 后侧面
					0.0f, 1.0f, 0f,
					1.0f, -1.0f, -1.0f,
					-1.0f, -1.0f, -1.0f,
					
					// 左侧面
					0.0f, 1.0f, 0f,
					-1.0f, -1.0f, -1.0f,
					-1.0f, -1.0f, 1.0f
			};
			ByteBuffer vbb = ByteBuffer.allocateDirect(coords.length * 4);
			vbb.order(ByteOrder.nativeOrder());
			mFVertexBuffer = vbb.asFloatBuffer();
			for (int i = 0; i < coords.length; i++) {
				mFVertexBuffer.put(coords[i]);
			}
			
			float[] colors = {
					1.0f, 0f, 0f, 1.0f,
					0f, 1.0f, 0f, 1.0f,
					0f, 0f, 1.0f, 1.0f,
					
					1.0f, 0f, 0f, 1.0f,
					0f, 1.0f, 0f, 1.0f,
					0f, 0f, 1.0f, 1.0f,
					
					1.0f, 0f, 0f, 1.0f,
					0f, 1.0f, 0f, 1.0f,
					0f, 0f, 1.0f, 1.0f,
					
					1.0f, 0f, 0f, 1.0f,
					0f, 1.0f, 0f, 1.0f,
					0f, 0f, 1.0f, 1.0f
			};
			ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
			cbb.order(ByteOrder.nativeOrder());
			mColorBuffer = cbb.asFloatBuffer();
			
			for (int i = 0; i < colors.length; i++) {
				mColorBuffer.put(colors[i]);
			}
			
			mFVertexBuffer.position(0);
			mColorBuffer.position(0);
			
//			ByteBuffer ibb = ByteBuffer.allocateDirect(3 * 2);
//			ibb.order(ByteOrder.nativeOrder());
//			mIndexBuffer = ibb.asShortBuffer();
//			short[] indecesArray = {0,1,2};
//			for (int i = 0; i < indecesArray.length; i++) {
//				mIndexBuffer.put(indecesArray[i]);
//			}
//			mIndexBuffer.position(0);
			
			int[] quater = {
				one, one, 0,
				-one, one, 0,
				one, -one, 0,
				-one, -one, 0
			};
			int[] quaterColor = {
				one, 0, 0, 0,
				one, one, 0, 0,
				one, one, one, 0,
				0, one, one, 0
			};
			ByteBuffer qbb = ByteBuffer.allocateDirect(quater.length * 4);
			qbb.order(ByteOrder.nativeOrder());
			mQuaterVertexBuffer = qbb.asIntBuffer();
			mQuaterVertexBuffer.put(quater);
			mQuaterVertexBuffer.position(0);
			
			ByteBuffer qcbb = ByteBuffer.allocateDirect(quaterColor.length * 4);
			qcbb.order(ByteOrder.nativeOrder());
			mQuaterColorBuffer = qcbb.asIntBuffer();
			mQuaterColorBuffer.put(quaterColor);
			mQuaterColorBuffer.position(0);
			
			ByteBuffer qibb = ByteBuffer.allocateDirect(6 * 2);
			qibb.order(ByteOrder.nativeOrder());
			mQuaterIndexBuffer = qibb.asShortBuffer();
			short[] index = {0, 1, 2,  1, 2, 3};
			mQuaterIndexBuffer.put(index);
			mQuaterIndexBuffer.position(0);
		}
		
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			gl.glDisable(GL10.GL_DITHER);
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); // 效率优先
			gl.glClearColor(0, 0, 0, 0);
			gl.glShadeModel(GL10.GL_SMOOTH); // 启用阴影平滑
			gl.glClearDepthf(1.0f); // 启用深度缓存
			
			gl.glEnable(GL10.GL_DEPTH_TEST); // 启用深度测试
			gl.glDepthFunc(GL10.GL_LEQUAL); // 深度测试类型为小于等于
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			float ratio = (float) width/height;
			// 设置OpenGL场景的大小，（0，0）表示窗口内部视口的左下角，（w，h）指定了视口的大小
			gl.glViewport(0, 0, width, height);
			
			// 设置投影矩阵
			gl.glMatrixMode(GL10.GL_PROJECTION);
			//重置投影矩阵
			gl.glLoadIdentity();
			
			// 设置视口的大小
			gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
			
			// 以下两句告诉opengl es，以后所有的变换都将影响这个模型（我们绘制的模型）
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			gl.glDisable(GL10.GL_DITHER);
			
			// 清除屏幕和深度缓存
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
			// 重置当前的模型观察矩阵
			gl.glLoadIdentity();
			
			// 以下两步为绘制颜色与顶点前必做操作
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			
			// 左移1.5单位，并移入屏幕6.0单位
			gl.glTranslatef(-1.5f, 0.0f, -6.0f);
			gl.glRotatef(mRotateTri, 0.0f, 1.0f, 0.0f);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mFVertexBuffer);
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
//			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
			for (int i = 0; i < 4; i++) {
				gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, i * 3, 3);
			}
			
			// 以下要显示单色，需要关闭color_array
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
			
			gl.glLoadIdentity();
			// 右移3单位
			gl.glTranslatef(1.5f, 0.0f, -6.0f);
			gl.glRotatef(mRotateQua, 0.0f, 0.0f, 1.0f);
			gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
			gl.glVertexPointer(3, GL10.GL_FIXED, 0, mQuaterVertexBuffer);
//			gl.glColorPointer(4, GL10.GL_FIXED, 0, mQuaterColorBuffer);
//			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
			gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 6, GL10.GL_UNSIGNED_SHORT, mQuaterIndexBuffer);
			
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			
			mRotateTri += 0.5f;
			if (mRotateTri == 360) {
				mRotateTri = 0f;
			}
			mRotateQua += 0.5f;
			if (mRotateQua == 360) {
				mRotateQua = 0f;
			}
		}
		
	}
}

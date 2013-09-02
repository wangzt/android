package com.tomsky.gldynamic;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;

import com.tomsky.gldynamic.model.Background;

public class WeatherRenderer implements Renderer {

	private Background mBackground;
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // 清除屏幕为黑色
		
		gl.glEnable(GL10.GL_BLEND); // 允许混合
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA); // 使用像素算法，需要开启GL_BLEND
		
		gl.glDisable(GL10.GL_DITHER); // 混色,禁用颜色元素或索引将在被写入颜色缓冲区之前进行dither
		gl.glEnable(GL10.GL_TEXTURE_2D); // 当前活动纹理单元为二维纹理
		gl.glShadeModel(GL10.GL_SMOOTH); // 阴影平滑,光滑着色模式
		gl.glClearDepthf(1.0f);	// 设置深度缓存
		gl.glEnable(GL10.GL_DEPTH_TEST); // 做深度比较和更新深度缓存
		gl.glDepthFunc(GL10.GL_LEQUAL); // 如果引入的depth值小于或等于参照值则通过
		
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST); // 告诉系统对透视进行修正
		
		gl.glOrthof(-1, 1, -1, 1, 1, 10); // 正交投影
		
		mBackground = new Background();
		mBackground.loadTexture(gl);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if (height == 0) { // Prevent A Divide By Zero By
			height = 1; // Making Height Equal One
		}
		
		gl.glViewport(0, 0, width, height); // Reset The Current Viewport
		gl.glMatrixMode(GL10.GL_PROJECTION); // Select The Projection Matrix
		gl.glLoadIdentity(); // Reset The Projection Matrix		
		gl.glOrthof(-1, 1, -1, 1, 1, 10);
		gl.glMatrixMode(GL10.GL_MODELVIEW); // Select The Modelview Matrix
		gl.glLoadIdentity();

	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glLoadIdentity(); // Reset The Current Modelview Matrix
		gl.glTranslatef(0.0f, 0.0f, -6.0f); 
		
		mBackground.draw(gl);

	}

}

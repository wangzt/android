package com.tomsky.gldynamic.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLUtils;
import android.util.Log;

import com.tomsky.gldynamic.R;
import com.tomsky.gldynamic.WeatherApplication;
import com.tomsky.gldynamic.utils.OpenGLUtils;

/**
 * 背景
 * @author wangzhitao
 *
 */
public class Background {

	int mTextureBgId;
	
	/** The buffer holding the vertices */
	private FloatBuffer vertexBuffer;
	private FloatBuffer textureBuffer;
	private ShortBuffer indicesBuffer;
	
	// 顶点坐标
	private float vertices[] = { 
			-1.0f, -1.0f, 0.0f, // Bottom Left
			1.0f, -1.0f, 0.0f, // Bottom Right
			-1.0f, 1.0f, 0.0f, // Top Left
			1.0f, 1.0f, 0.0f // Top Right
	};
	// 纹理数组
	private float texture[] = { 
			0.0f, 1.0f, 0.0f, 
			1.0f, 1.0f, 0.0f, 
			0.0f, 0.0f, 0.0f, 
			1.0f, 0.0f, 0.0f };

	private short indices[] = { 0, 1, 2, 2, 1, 3 };
	
	public Background() {
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		vertexBuffer = byteBuf.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
		
		byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		textureBuffer = byteBuf.asFloatBuffer();
		textureBuffer.put(texture);
		textureBuffer.position(0);
		
		byteBuf = ByteBuffer.allocateDirect(indices.length *2);
		byteBuf.order(ByteOrder.nativeOrder());
		indicesBuffer = byteBuf.asShortBuffer();
		indicesBuffer.put(indices);
		indicesBuffer.position(0);
	}
	
	public void loadTexture(GL10 gl) {
		mTextureBgId = OpenGLUtils.loadTexture(WeatherApplication.getInstance(), R.drawable.bg_fine_day, gl, mTextureBgId);
		Log.e("wzt", "background loadTexture,mTextureBgId:"+mTextureBgId);
	}
	
	public void draw(GL10 gl) {
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glFrontFace(GL10.GL_CCW);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureBgId);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glTexCoordPointer(3, GL10.GL_FLOAT, 0, textureBuffer);
		gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 6, GL10.GL_UNSIGNED_SHORT, indicesBuffer );

		// Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
	}
	
}

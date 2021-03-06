package com.tomsky.gldemo.es;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

public class SimpleRectangleRenderer extends AbstractRenderer {

	private final static int VERTS = 4;
	
	private FloatBuffer mFVertexBuffer;
	
	private ShortBuffer mIndexBuffer;
	
	public SimpleRectangleRenderer(Context context) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(VERTS * 3 * 4);
		vbb.order(ByteOrder.nativeOrder());
		mFVertexBuffer = vbb.asFloatBuffer();
		
		ByteBuffer ibb = ByteBuffer.allocateDirect(6 * 2);
		ibb.order(ByteOrder.nativeOrder());
		mIndexBuffer = ibb.asShortBuffer();
		
		float[] coords = {
				-0.5f, -0.5f, 0,
				0.5f,  -0.5f, 0,
				0.5f,  0.5f, 0,
				-0.5f, 0.5f, 0
		};
		for (int i = 0; i < coords.length; i++) {
			mFVertexBuffer.put(coords[i]);
		}
		
		short[] indecesArray = {0,1,2, 0,2,3};
		for (int i = 0; i < indecesArray.length; i++) {
			mIndexBuffer.put(indecesArray[i]);
		}
		
		mFVertexBuffer.position(0);
		mIndexBuffer.position(0);
		
	}
	
	@Override
	protected void draw(GL10 gl) {
		gl.glColor4f(0.0f, 1.0f, 0f, 0.5f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mFVertexBuffer);
		gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
	}

}

package com.tomsky.gldemo.es;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

public class SimpleTriangleRenderer extends AbstractRenderer {

	private final static int VERTS = 3;
	
	private FloatBuffer mFVertexBuffer;
	
	private ShortBuffer mIndexBuffer;
	
	public SimpleTriangleRenderer(Context context) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(VERTS * 3 * 4);
		vbb.order(ByteOrder.nativeOrder());
		mFVertexBuffer = vbb.asFloatBuffer();
		
		ByteBuffer ibb = ByteBuffer.allocateDirect(VERTS * 2);
		ibb.order(ByteOrder.nativeOrder());
		mIndexBuffer = ibb.asShortBuffer();
		
		float[] coords = {
				-0.5f, -0.5f, 0,
				0.5f,  -0.5f, 0,
				0.0f, 0.5f, 0
		};
		for (int i = 0; i < coords.length; i++) {
			mFVertexBuffer.put(coords[i]);
		}
		
		short[] indecesArray = {0,1,2};
		for (int i = 0; i < indecesArray.length; i++) {
			mIndexBuffer.put(indecesArray[i]);
		}
		
		mFVertexBuffer.position(0);
		mIndexBuffer.position(0);
		
	}
	
	@Override
	protected void draw(GL10 gl) {
		gl.glColor4f(1.0f, 0f, 0f, 0.5f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mFVertexBuffer);
		gl.glDrawElements(GL10.GL_TRIANGLES, VERTS, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
	}

}

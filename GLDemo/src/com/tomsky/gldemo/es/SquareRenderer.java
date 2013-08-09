package com.tomsky.gldemo.es;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.tomsky.gldemo.es.model.RegularPolygon;

public class SquareRenderer extends AbstractRenderer {

	private FloatBuffer mFVertexBuffer;
	
	private ShortBuffer mIndexBuffer;
	
	private int numOfIndices = 0;
	
	private int sides = 4;
	
	public SquareRenderer(Context context, int side) {
		this.sides = side;
		prepareBuffers(sides);
	}
	
	private void prepareBuffers(int sides) {
		RegularPolygon t = new RegularPolygon(0,0,0,0.5f,sides);
		this.mFVertexBuffer = t.getVertexBuffer();
		this.mIndexBuffer = t.getIndexBuffer();
		this.numOfIndices = t.getNumberOfIndices();
		this.mFVertexBuffer.position(0);
		this.mIndexBuffer.position(0);
	}
	
	@Override
	protected void draw(GL10 gl) {
		prepareBuffers(sides);
		gl.glColor4f(0.0f, 1.0f, 0f, 0.5f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mFVertexBuffer);
		gl.glDrawElements(GL10.GL_TRIANGLES, this.numOfIndices, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
	}

}

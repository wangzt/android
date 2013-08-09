package com.tomsky.gldemo.es.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.util.Log;

public class RegularPolygon {

	// Space to hold (x,y,z) of the center and the radius
	private float cx, cy, cz, r;
	
	private int sides;
	
	// coordinate array: (x,y) vertex points
	private float[] xarray = null;
	private float[] yarray = null;
	
	// texture array: (x,y) also called (s,t) points
	// where the figure is going to be mapped to a texture bitmap
	private float[] sarray = null;
	private float[] tarray = null;
	
	public RegularPolygon(float incx, float incy, float incz,
			float inr, int insides) {
		cx = incx;
		cy = incy;
		cz = incz;
		r = inr;
		sides = insides;
		
		xarray = new float[sides];
		yarray = new float[sides];
		
		sarray = new float[sides];
		tarray = new float[sides];
		
		calcArrays();
		
		calcTextureArrays();
	}
	
	private void calcArrays() {
		float[] xmarray = this.getXMultiplierArray();
		float[] ymarray = this.getYMultiplierArray();
		
		for (int i = 0; i < sides; i++) {
			float curm = xmarray[i];
			float xcoord = cx + r * curm;
			xarray[i] = xcoord;
		}
		this.printArray(xarray, "xarray");
		
		for (int i = 0; i < sides; i++) {
			float curm = ymarray[i];
			float ycoord = cy + r * curm;
			yarray[i] = ycoord;
		}
		this.printArray(yarray, "yarray");
	}
	
	private void calcTextureArrays() {
		float[] xmarray = this.getXMultiplierArray();
		float[] ymarray = this.getYMultiplierArray();
		
		for (int i = 0; i < sides; i++) {
			float curm = xmarray[i];
			float xcoord = 0.5f + 0.5f * curm;
			sarray[i] = xcoord;
		}
		this.printArray(xarray, "xarray");
		
		for (int i = 0; i < sides; i++) {
			float curm = ymarray[i];
			float ycoord = 0.5f + 0.5f * curm;
			tarray[i] = ycoord;
		}
		this.printArray(yarray, "yarray");
	}
	
	public FloatBuffer getVertexBuffer() {
		int vertices = sides + 1;
		int coordinates = 3;
		int floatsize = 4;
		int spacePerVertex = coordinates * floatsize;
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(spacePerVertex * vertices);
		vbb.order(ByteOrder.nativeOrder());
		FloatBuffer fVertexBuffer = vbb.asFloatBuffer();
		
		fVertexBuffer.put(cx);
		fVertexBuffer.put(cy);
		fVertexBuffer.put(0.0f);
		
		int totalPuts = 3;
		for (int i = 0; i < sides; i++) {
			fVertexBuffer.put(xarray[i]);
			fVertexBuffer.put(yarray[i]);
			fVertexBuffer.put(0.0f);
			totalPuts += 3;
		}
		Log.d("wzt", "total puts:"+totalPuts);
		
		return fVertexBuffer;
	}
	
	public FloatBuffer getTextureBuffer() {
		int vertices = sides + 1;
		int coordinates = 2;
		int floatsize = 4;
		int spacePerVertex = coordinates * floatsize;
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(spacePerVertex * vertices);
		vbb.order(ByteOrder.nativeOrder());
		FloatBuffer fTextureBuffer = vbb.asFloatBuffer();
		
		// Put the first coordinate (s,t)
		fTextureBuffer.put(0.5f);
		fTextureBuffer.put(0.5f);
		
		int totalPuts = 2;
		for (int i = 0; i < sides; i++) {
			fTextureBuffer.put(sarray[i]);
			fTextureBuffer.put(tarray[i]);
			totalPuts += 2;
		}
		Log.d("wzt", "Total texture puts:"+totalPuts);
		return fTextureBuffer;
	}
	
	public ShortBuffer getIndexBuffer() {
		short[] iarray = new short[sides * 3];
		ByteBuffer ibb = ByteBuffer.allocateDirect(sides * 3 * 2);
		ibb.order(ByteOrder.nativeOrder());
		ShortBuffer indexBuffer = ibb.asShortBuffer();
		for (int i = 0; i < sides; i++) {
			short index1 = 0;
			short index2 = (short)(i+1);
			short index3 = (short)(i+2);
			if (index3 == sides + 1) {
				index3 = 1;
			}
			indexBuffer.put(index1);
			indexBuffer.put(index2);
			indexBuffer.put(index3);
			
			iarray[i*3 + 0]=index1;
			iarray[i*3 + 1]=index2;
			iarray[i*3 + 2]=index3;
		}
		this.printShortArray(iarray, "index array");
		return indexBuffer;
	}
	
	private float[] getXMultiplierArray() {
		float[] angleArray = getAngleArrays();
		float[] xmultiplierArray = new float[sides];
		for (int i = 0; i < angleArray.length; i++) {
			float curAngle = angleArray[i];
			float sinValue = (float)Math.cos(Math.toRadians(curAngle));
			float absSinValue = Math.abs(sinValue);
			if(isXPositiveQuadrant(curAngle)) {
				sinValue = absSinValue;
			} else {
				sinValue = -absSinValue;
			}
			xmultiplierArray[i] = this.getApproxValue(sinValue);
		}
		this.printArray(xmultiplierArray, "xmultiplierArray");
		return xmultiplierArray;
	}
	
	private float[] getYMultiplierArray() {
		float[] angleArray = getAngleArrays();
		float[] ymultiplierArray = new float[sides];
		for (int i = 0; i < angleArray.length; i++) {
			float curAngle = angleArray[i];
			float sinValue = (float)Math.sin(Math.toRadians(curAngle));
			float absSinValue = Math.abs(sinValue);
			if (isYPositiveQuadrant(curAngle)) {
				sinValue = absSinValue;
			} else {
				sinValue = -absSinValue;
			}
			ymultiplierArray[i] = this.getApproxValue(sinValue);
		}
		this.printArray(ymultiplierArray, "ymultiplierArray");
		return ymultiplierArray;
	}
	
	private boolean isXPositiveQuadrant(float angle) {
		if ((0 <= angle) && (angle <= 90)) return true;
		if ((angle < 0) && (angle >= -90)) return true;
		return false;
	}
	
	private boolean isYPositiveQuadrant(float angle) {
		if ((0 <= angle) && (angle <= 90)) return true;
		if ((angle < 180) && (angle > 90)) return true;
		return false;
	}
	
	private float[] getAngleArrays() {
		float[] angleArray = new float[sides];
		float commonAngle = 360.0f/sides;
		float halfAngle = commonAngle/2.0f;
		float firstAngle = 360.0f - (90+halfAngle);
		angleArray[0] = firstAngle;
		
		float curAngle = firstAngle;
		for (int i = 0; i < sides; i++) {
			float newAngle = curAngle - commonAngle;
			angleArray[i] = newAngle;
			curAngle = newAngle;
		}
		this.printArray(angleArray, "angleArray");
		return angleArray;
	}
	
	private float getApproxValue(float f) {
		return (Math.abs(f) < 0.001f) ? 0: f;
	}
	
	public int getNumberOfIndices() {
		return sides * 3;
	}
	
	public static void test() {
		RegularPolygon triangle = new RegularPolygon(0,0,0,1,3);
	}
	
	private void printArray(float array[], String tag) {
		StringBuilder sb = new StringBuilder(tag);
		for (int i = 0; i < array.length; i++) {
			sb.append(";").append(array[i]);
		}
		Log.d("wzt", sb.toString());
	}
	
	private void printShortArray(short array[], String tag) {
		StringBuilder sb = new StringBuilder(tag);
		for (int i = 0; i < array.length; i++) {
			sb.append(";").append(array[i]);
		}
		Log.d("wzt", "tag:"+sb.toString());
	}
}

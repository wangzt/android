package com.tomsky.gldynamic.utils;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

public class OpenGLUtils {

	public static int loadTexture(Context context, int resId, GL10 gl, int textureId) {
		Bitmap bitmap = null;
		
		try {
			bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
			bitmap = Bitmap.createScaledBitmap(bitmap, pow2(bitmap.getWidth()), pow2(bitmap.getHeight()), true);
			if (textureId == 0) {
				int[] textures = new int[1];
				gl.glGenTextures(1, textures, 0);
				textureId = textures[0];
				gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
				gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR); //GL_NEAREST
				gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
				GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
			} else {
				gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
				gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR); //GL_NEAREST
				gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
				GLUtils.texSubImage2D(GL10.GL_TEXTURE_2D, 0, 0, 0, bitmap);
			}
		} catch (Exception e) {
		} catch (OutOfMemoryError oom) {
			Log.e("wzt", "loadTexture occur OOM.");
		} finally {
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
			}
			bitmap = null;
		}
		return textureId;
		
	}
	
	public static int pow2(int size) {
		int small = (int) (Math.log((double)size) / Math.log(2.0f));
		if ((1 << small) >= size) {
			return 1 << small;
		} else {
			return 1 << (small+1);
		}
	}
}

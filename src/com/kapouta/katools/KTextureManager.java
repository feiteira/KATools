package com.kapouta.katools;

import java.io.IOException;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

public class KTextureManager {

	private Context context;
	private GL10 gl;
	HashMap<String, KTexture> images = new HashMap<String, KTexture>();

	int[] m_textures = null;

	public KTextureManager(Context context) {
		this.context = context;
	}

	/**
	 * DO NOT use this on multi-threaded apps
	 * 
	 * @param filepath
	 * @return
	 */
	public void addAsset(String filepath) {
		addAsset(filepath, 0f);
	}

	/**
	 * DO NOT use this on multi-threaded apps
	 * 
	 * @param filepath
	 * @return
	 */
	public void addAsset(String filepath, float z) {
		if (!images.containsKey(filepath)) {
			KTexture texData = new KTexture(filepath, z);
			images.put(filepath, texData);
		}
	}

	public void update(GL10 gl) {
		this.gl = gl;
		m_textures = new int[images.size()];

		gl.glGenTextures(images.size(), m_textures, 0);

		Object[] kpaths = images.keySet().toArray();

		for (int cnt = 0; cnt < kpaths.length; cnt++) {
			// get gl id
			{
				Bitmap bitmap = null;
				String filename = kpaths[cnt].toString();
				try {

					bitmap = BitmapFactory.decodeStream(context.getAssets()
							.open(filename));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}

				gl.glBindTexture(GL10.GL_TEXTURE_2D, m_textures[cnt]);

				// create nearest filtered texture
				gl.glTexParameterf(GL10.GL_TEXTURE_2D,
						GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
				gl.glTexParameterf(GL10.GL_TEXTURE_2D,
						GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

				// Use Android GLUtils to specify a two-dimensional texture
				// image
				// from our bitmap
				GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
				// Clean up
				bitmap.recycle();

				images.get(filename).id = m_textures[cnt];
			}
		}
	}

	/**
	 * Call only after update
	 * 
	 * @param filepath
	 * @return
	 */

	public KTexture getTexture(String path) {
		return images.get(path);
	}

}

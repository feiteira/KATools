package com.kapouta.katools;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

public class GLTextureManager {
	static final float[] SQUARE_TEX_VERTEXES = {
			// cenas
			-1.0f, -1.0f, 0.0f, // V1 - bottom left
			-1.0f, 1.0f, 0.0f, // V2 - top left
			1.0f, -1.0f, 0.0f, // V3 - bottom right
			1.0f, 1.0f, 0.0f // V4 - top right
	};

	static final float TEX_COORDINATES[] = {
			// Mapping coordinates for the vertices
			0.0f, 1.0f, // top left (V2)
			0.0f, 0.0f, // bottom left (V1)
			1.0f, 1.0f, // top right (V4)
			1.0f, 0.0f // bottom right (V3)
	};

	private Context context;
	private GL10 gl;
	HashMap<String, TextureData> images = new HashMap<String, TextureData>();

	int[] m_textures = null;

	public GLTextureManager(Context context) {
		this.context = context;
	}

	class TextureData {
		public int id;
		public float[] vertexes;
		public FloatBuffer buffer = null;
		public FloatBuffer texture; // buffer holding the texture
									// coordinates

		public TextureData() {
		}

		public void setDepth(float z) {
			for (int zvertex = 2; zvertex < vertexes.length; zvertex += 3) {
				vertexes[zvertex] = z;
			}
		}

		public void makeBuffer() {
			ByteBuffer vbb2 = ByteBuffer.allocateDirect(
			// (# of coordinate values * 4 bytes per float)
					SQUARE_TEX_VERTEXES.length * 4);
			vbb2.order(ByteOrder.nativeOrder());// use the device hardware's
												// native
												// byte order
			buffer = vbb2.asFloatBuffer(); // create a floating point buffer
											// from
											// the ByteBuffer
			buffer.put(SQUARE_TEX_VERTEXES); // add the coordinates to the
												// FloatBuffer

			buffer.position(0); // set the buffer to read the first
								// coordinate
		}

		public void draw(GL10 gl) {
			// bind the previously generated texture
			gl.glBindTexture(GL10.GL_TEXTURE_2D, id);

			// Point to our buffers
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

			// Set the face rotation
			gl.glFrontFace(GL10.GL_CW);

			// Point to our vertex buffer
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, buffer);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texture);

			// Draw the vertices as triangle strip
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertexes.length / 3);

			// Disable the client state before leaving
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}
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
		TextureData texData = new TextureData();

		ByteBuffer byteBuffer = ByteBuffer
				.allocateDirect(TEX_COORDINATES.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		texData.texture = byteBuffer.asFloatBuffer();
		texData.texture.put(TEX_COORDINATES);
		texData.texture.position(0);

		texData.vertexes = SQUARE_TEX_VERTEXES;
		texData.setDepth(z);
		texData.makeBuffer();
		images.put(filepath, texData);
	}

	public void update(GL10 gl) {
		this.gl = gl;
		m_textures = new int[images.size()];

		gl.glGenTextures(images.size(), m_textures, 0);

		String[] kpaths = (String[]) images.keySet().toArray();
		for (int cnt = 0; cnt < kpaths.length; cnt++) {
			// get gl id
			{
				Bitmap bitmap = null;
				try {
					bitmap = BitmapFactory.decodeStream(context.getAssets()
							.open(kpaths[cnt]));
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
			}
			// get buffer

			// get
		}
	}

	/**
	 * Call only after update
	 * 
	 * @param filepath
	 * @return
	 */
	public int getAssetId(String filepath) {
		return images.get(filepath).id;
	}

	/**
	 * Call only after update
	 * 
	 * @param filepath
	 * @return
	 */
	public void setTexture(int id) {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, id);
	}

	/**
	 * Call only after update
	 * 
	 * @param filepath
	 * @return
	 */
	public void setTexture(String path) {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, images.get(path).id);
	}

	public TextureData getTexture(String path) {
		return images.get(path);
	}

}

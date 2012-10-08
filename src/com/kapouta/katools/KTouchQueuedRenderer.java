package com.kapouta.katools;

import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public abstract class KTouchQueuedRenderer implements Renderer, OnTouchListener {

	Vector<MotionEvent> events = null;

	public abstract void onDrawFrame(GL10 gl, MotionEvent event);

	public KTouchQueuedRenderer() {
		events = new Vector<MotionEvent>();
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		if (events.size() > 0)
			this.onDrawFrame(gl, events.remove(0));
		else
			this.onDrawFrame(gl, null);
	}

	@Override
	public void onSurfaceChanged(GL10 arg0, int arg1, int arg2) {

	}

	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {

	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		events.add(event);
		return true;
	}

}

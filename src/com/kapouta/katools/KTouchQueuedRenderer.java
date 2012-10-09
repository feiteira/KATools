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
		if (events.size() > 0) {
			MotionEvent curr_event = events.remove(0);

			switch (curr_event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				this.onTouchDown(curr_event.getX(), curr_event.getY());
				break;
			case MotionEvent.ACTION_MOVE:
				this.onTouchMove(curr_event.getX(), curr_event.getY());
				break;
			case MotionEvent.ACTION_UP:
				this.onTouchUp(curr_event.getX(), curr_event.getY());
				break;
			case MotionEvent.ACTION_POINTER_2_DOWN:
				int pid0 = curr_event.getPointerId(0);
				int pid1 = curr_event.getPointerId(1);
				this.onTouchUp(curr_event.getX(pid0), curr_event.getY(pid0));
				this.onDualTouchDown(curr_event.getX(pid0),
						curr_event.getY(pid0), curr_event.getX(pid1),
						curr_event.getY(pid1));
				break;
			case MotionEvent.ACTION_POINTER_2_UP:
				break;
				default:
				
			}

		}
		this.onDrawFrame(gl, null);
	}

	@Override
	public void onSurfaceChanged(GL10 arg0, int arg1, int arg2) {

	}

	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {

	}

	public void onTouchDown(float x, float y) {
	}

	public void onTouchMove(float x, float y) {
	}

	public void onTouchUp(float x, float y) {
	}

	public void onDualTouchDown(float x1, float y1, float x2, float y2) {
	}

	public void onDualTouchMove(float x1, float y1, float x2, float y2) {
	}

	public void onDualTouchUp(float x1, float y1, float x2, float y2) {
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		events.add(event);
		return true;
	}

}

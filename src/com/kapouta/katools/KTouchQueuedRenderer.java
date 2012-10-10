package com.kapouta.katools;

import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public abstract class KTouchQueuedRenderer implements Renderer, OnTouchListener {

	Vector<MotionEventData> events = null;
	private boolean two_pointers_down;

	public KTouchQueuedRenderer() {
		events = new Vector<MotionEventData>();
		this.two_pointers_down = false;

	}

	@Override
	public synchronized void onDrawFrame(GL10 gl) {
		if (events.size() > 0) {
			MotionEventData curr_event = events.remove(0);

			switch (curr_event.action) {
			case MotionEvent.ACTION_DOWN:
				this.onTouchDown(curr_event.x1, curr_event.y1);
				break;
			case MotionEvent.ACTION_MOVE:
				this.onTouchMove(curr_event.x1, curr_event.y1);
				break;
			case MotionEvent.ACTION_UP:
				this.onTouchUp(curr_event.x1, curr_event.y1);
				break;
			case MotionEvent.ACTION_POINTER_2_DOWN: {
				this.two_pointers_down = true;
				this.onTouchUp(curr_event.x1, curr_event.y1);
				this.onDualTouchDown(curr_event.x1, curr_event.y1, curr_event.x2, curr_event.y2);
				break;
			}
			case MotionEvent.ACTION_POINTER_1_UP:
			case MotionEvent.ACTION_POINTER_2_UP: {
				this.two_pointers_down = false;
				this.onDualTouchUp(curr_event.x1, curr_event.y1, curr_event.x2, curr_event.y2);
				break;
			}
			default:
				System.out.println("Unknown: " + curr_event);
			}
		}
	}

	@Override
	public void onSurfaceChanged(GL10 arg0, int arg1, int arg2) {

	}

	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {

	}

	public void onTouchDown(float x, float y) {
		System.out.println("One DOWN ( " + x + " , " + y + " )");
	}

	public void onTouchMove(float x, float y) {
		System.out.println("One MOVE ( " + x + " , " + y + " )");

	}

	public void onTouchUp(float x, float y) {
		System.out.println("One UP ( " + x + " , " + y + " )");

	}

	public void onDualTouchDown(float x1, float y1, float x2, float y2) {
		System.out.println("Two DOWN ( " + x1 + " , " + y1 + " ; " + x2 + " , " + y2 + " ) ");

	}

	public void onDualTouchMove(float x1, float y1, float x2, float y2) {
		System.out.println("Two MOVE ( " + x1 + " , " + y1 + " ; " + x2 + " , " + y2 + " ) ");

	}

	public void onDualTouchUp(float x1, float y1, float x2, float y2) {
		System.out.println("Two UP ( " + x1 + " , " + y1 + " ; " + x2 + " , " + y2 + " ) ");

	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if (MotionEvent.ACTION_MOVE != event.getAction() || events.size() == 0)
			events.add(new MotionEventData(event));
		System.out.println(":: " + event);
		return true;
	}

	protected class MotionEventData {
		float x1, y1, x2, y2;
		int action;

		public MotionEventData(MotionEvent event) {
			int pid0 = event.getPointerId(0);
			int pid1 = event.getPointerId(1);
			x1 = event.getX(pid0);
			y1 = event.getY(pid0);
			x2 = event.getX(pid1);
			y2 = event.getY(pid1);
			action = event.getAction();
		}

		public String toString() {
			return "A[" + action + "]  P1( " + x1 + " , " + y1 + " )  P2 " + x2 + " , " + y2 + " )";
		}

	}

}

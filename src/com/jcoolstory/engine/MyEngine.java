package com.jcoolstory.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

abstract public class MyEngine {
	protected static int FRAMERATE = 20;
	protected SurfaceHolder mHolder;
	protected Context mContext;
	protected boolean wait = true;
	protected boolean canRun = true;
	protected boolean rotate = true;
	protected int Width;

	protected int Height;
	protected long deltaTime;
	private Handler mHandler;
	long lastTime = 0;
	
	protected float totalusetime;

	public MyEngine(SurfaceHolder holder, Context context, int w, int h) {
		mHolder = holder;
		mContext = context;
		Width = w;
		Height = h;
		mHandler = new Handler();

	}

	public void pausePainting() {
		Log.d("TAG", "pausePainting");
		canRun = false;
	}

	public void resumePainting() {
		Log.d("TAG", "resumePainting");
		canRun = true;
		mHandler.postDelayed(mDrawHandler, 200);
	}

	public void stopPainting() {
		canRun = false;
		onDestroy();
	}

	public void setSurfaceSize(int width, int height) {
		Width = width;
		Height = height;
	}
	
	public Bitmap saveFrame(Bitmap bitmap) {
		
		
		Canvas c = new Canvas(bitmap);
		DrawFrame(c);
		
		return bitmap;
	}

	protected void drawFrame() {
		// TODO Auto-generated method stub
			Canvas c = null;
			try {

				c = mHolder.lockCanvas();

				if (c != null) {
					synchronized (mHolder) {
						DrawFrame(c);
					}
				}
			} finally {
				if (c != null)
					mHolder.unlockCanvasAndPost(c);
			}
		mHandler.removeCallbacks(mDrawHandler);
		totalusetime = System.currentTimeMillis() - lastTime;
		deltaTime = 1000 / FRAMERATE - (System.currentTimeMillis() - lastTime);
		if (canRun) {
			mHandler.postDelayed(mDrawHandler, deltaTime);
		}
	}

	private final Runnable mDrawHandler = new Runnable() {
		public void run() {
			lastTime = System.currentTimeMillis();
			update(0);
			drawFrame();
		}
	};

	public void setFrame(int frame) {
		FRAMERATE = frame;
	}

	abstract public void DrawFrame(Canvas canvas);

	abstract public void update(float deltaTime);

	public void doTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

	}

	abstract public void onDestroy();
}
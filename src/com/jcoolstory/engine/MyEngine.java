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
		// Log.d("TAG","MyEngine");
		mHolder = holder;
		mContext = context;
		Width = w;
		Height = h;
		mHandler = new Handler();

	}

	public void pausePainting() {
		Log.d("TAG", "pausePainting");
		canRun = false;
		// synchronized (this) {
		// this.notify();
		// }
	}

	public void resumePainting() {
		Log.d("TAG", "resumePainting");
		canRun = true;
		// drawFrame();
		mHandler.postDelayed(mDrawHandler, 200);
		// synchronized (this) {
		// this.notify();
		// }
	}

	public void stopPainting() {
		// Log.d("TAG","stopPainting");
		canRun = false;
		onDestroy();
		// synchronized (this) {
		// this.notify();
		// }
	}

	public void setSurfaceSize(int width, int height) {
		Width = width;
		Height = height;
		// Log.d("TAG","Width : " + width + "Height : " + height);
		// synchronized (this) {
		// this.notify();
		// }
		// if (Width >Height)
		// {
		// rotate = true;
		// }
		// else
		// {
		// rotate = false;
		// }
	}

	// public void doTouchEvent(MotionEvent event)
	// {
	// wait = false;
	// synchronized (this) {
	// this.notify();
	// }
	// }
	// public void run()
	// {
	// canRun = true;
	// Canvas canvas =null;
	// try {
	// while (canRun)
	// {
	// // canvas = mHolder.lockCanvas();
	// // Bitmap bit = Bitmap.createBitmap(500, 500, Config.RGB_565);
	// // Drawable d = new BitmapDrawable();
	// // d.setBounds(0, 0, Height, Height);
	// // Region arg0 = new Region();
	// // View view = new View(mContext);
	// //
	// // mHolder.getSurface().setTransparentRegionHint(arg0);
	// // d.draw(canvas);
	// // canvas.setBitmap(bit);
	// // int flags;
	// // Parcel p = new Parcel;
	// //
	// // bit.writeToParcel(p, flags);
	// try
	// {
	// synchronized(mHolder)
	// {
	//
	// DrawFrame(canvas);
	// // //Log.d("SNOWBOARD", "run");
	// }
	// }finally
	// {
	// if (canvas != null)
	// mHolder.unlockCanvasAndPost(canvas);
	// }
	//
	// synchronized (this) {
	// if (wait) {
	// try {
	// //Log.d("TAG", "wait");
	// wait();
	// } catch (Exception e) {
	// // nothing
	// }
	// }
	// }
	// Thread.sleep(1000/24);
	//
	// }
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	public Bitmap saveFrame(Bitmap bitmap) {
		
		
		Canvas c = new Canvas(bitmap);
		DrawFrame(c);
		
		return bitmap;
	}

	protected void drawFrame() {
		// TODO Auto-generated method stub
//		if (canRun) {
			Canvas c = null;
			try {

				c = mHolder.lockCanvas();
				// Surface view = mHolder.getSurface();

				if (c != null) {
					// draw something
					synchronized (mHolder) {
						DrawFrame(c);
					}
				}
			} finally {
				if (c != null)
					mHolder.unlockCanvasAndPost(c);
			}

//		}

		// Reschedule the next redraw
		mHandler.removeCallbacks(mDrawHandler);
		// long delay = ;
		totalusetime = System.currentTimeMillis() - lastTime;
		deltaTime = 1000 / FRAMERATE - (System.currentTimeMillis() - lastTime);
		// temp = temp - delay;
		if (canRun) {
			mHandler.postDelayed(mDrawHandler, deltaTime);
		}

		// Log.d(TAG, "drawFrame");
	}

	private final Runnable mDrawHandler = new Runnable() {
		public void run() {
			lastTime = System.currentTimeMillis();
			update(0);
			drawFrame();
			// Log.d(TAG,
			// "----------------------mDrawSheep--------------------------" +
			// this.toString());

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
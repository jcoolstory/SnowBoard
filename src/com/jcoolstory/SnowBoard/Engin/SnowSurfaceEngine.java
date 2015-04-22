package com.jcoolstory.SnowBoard.Engin;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;

import android.graphics.MaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.GestureDetector.OnGestureListener;

import com.jcoolstory.SnowBoardFree.R;
import com.jcoolstory.engine.FPSCounter;
import com.jcoolstory.engine.MyEngine;

public class SnowSurfaceEngine extends MyEngine {
	class Crystal {
		public float x;
		public float y;
		public int rad;
		public float offsetX = 0;
		public float offsetY = 0;
		public int rotate = 0;
		public float velocityX;
		public float velocityY;
		public float yRange = 0;
		public boolean touched = false;
		public boolean visible = true;

		public void step() {
			if (!touched)
				return;

			x += offsetX;
			offsetX *= 0.9;
			rotate -= (rad * 10);

			y += offsetY;
			offsetY *= 0.9;
			rotate += (rad * 10);
			if (Math.abs(offsetX) < 1.2f || Math.abs(offsetY) < 1.2f) {
				touched = false;
				yRange = y;
				createeffect(this, true);
				y = Height + 2;
			}
		}

	}

	enum FlakeState {
		Rain, Snowstorm, SnowFallFast, Normal;
	}

	private float idle;

	private float drawtotalusetime;

	private float debugdelay;

	private float drawdebugdelay;

	private float raing = 5;

	private Paint mDrayPaint;

	public SnowSurfaceEngine(SurfaceHolder holder, Context context, int w, int h) {
		super(holder, context, w, h);
		// TODO Auto-generated constructor stub
		fpsCounter = new FPSCounter();

		mRnd = new Random(System.currentTimeMillis());
		initSizes(w, h);

		for (int i = 0; i < config.mFlakeMax; i++) {
			Crystal cr = new Crystal();
			cr.x = mRnd.nextInt(Width);
			cr.y = mRnd.nextInt(Height);
			cr.rad = mRnd.nextInt(3);
			cr.velocityX = (mRnd.nextFloat() - 0.5f) * 5f;
			cr.velocityY = (mRnd.nextFloat()) * ((cr.rad + 1));
			cr.yRange = Height * 2;
			mCrystalList.add(cr);
		}

		mTouchList = new ArrayList<RectF>();

		loadPaints();
		loadGraphics();

	}

	public void accelerate(int i) {
		// TODO Auto-generated method stub

	}

	public void deaccelerate() {

	}

	public void deProxi() {
		// TODO Auto-generated method stub
	}

	public void doProxi() {
		// TODO Auto-generated method stub
	}

	public void doTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		super.doTouchEvent(event);
		mGestureDetector.onTouchEvent(event);
	}

	public void drawSnowEffect(Canvas canvas) {
		int size = (int) (mDefaultRound * 0.7f);
		for (SnowEffect e : effectList) {
			if (e.isEnabled()) {
				canvas.drawBitmap(mEffectsBitmap,
						mSnowEffectRegions[1][e.currentFrame], new RectF(e.x
								- size, e.y - size, e.x + size, e.y + size),
						mPaint);
			}

		}
	}

	public void drawRainEffect(Canvas canvas) {
		for (SnowEffect e : rainList) {
			if (e.isEnabled()) {
				canvas.drawBitmap(mEffectsBitmap,
						mSnowEffectRegions[1][e.currentFrame], new RectF(
								e.x - 64, e.y - 64, e.x + 64, e.y + 64), mPaint);
			}
		}
	}

	public void drawFlake(Canvas canvas) {

		if (flakeState == FlakeState.Rain) {
			canvas.concat(myWorld.DisplayMatrix);
			float size = mDefaultRound / 5;
			for (Crystal cr : mCrystalList) {

				canvas.drawBitmap(mEffectsBitmap,
						mSnowEffectRegions[0][mRnd.nextInt(4)], new RectF(cr.x
								- size, cr.y - size, cr.x + size, cr.y + size),
						mPaint);
			}
		} else {

			if (myWorld.isRotating()) {

				canvas.concat(myWorld.DisplayMatrix);
				for (Crystal cr : mCrystalList) {

					canvas.drawBitmap(snows[cr.rad], cr.x, cr.y, mPaint);
				}

			} else {
				for (Crystal cr : mCrystalList) // 36 - 39
				{

					canvas.drawBitmap(snows[cr.rad], cr.x, cr.y, mPaint);
				}
				drawSnowEffect(canvas);
			}
		}

	}

	public void present(Canvas canvas) {
		float deley = 0;
		long last = System.nanoTime();
		canvas.clipRect(myWorld.DisplayRect);
		if (config.mDrawBackgroundEnabled && mBackgroundBitmap != null) // 39
																		// -//
																		// 58
		{
			canvas.drawBitmap(mBackgroundBitmap, null, myWorld.BaseRectF, null);
		} else {
			canvas.drawRGB(0, 0, 0);
		}

		try {
				canvas.drawBitmap(mBaseLayer, myWorld.BackLayerRect,
						myWorld.DisplayRect, null);
		} catch (Exception NullPointerException) {
			// TODO: handle exception
			throw new NullPointerException();

		}
		drawFlake(canvas);

		deley = (System.nanoTime() - last) / 1000000.0f;
		boolean temp = false;
		if (fpsCounter.logFrame()) {
			drawpresentdeley = deley;
			drawstepdelay = stepdelay;
			idle = deltaTime;
			drawtotalusetime = totalusetime;
			drawdebugdelay = debugdelay;
			temp = true;
		}
		last = System.nanoTime();

		if (DEBUG) {
			Paint paint = new Paint();
			paint.setColor(Color.WHITE);
			paint.setTextSize(20);

			canvas.drawText(String.format("Draw : %5.1f ms", drawpresentdeley),
					Width / 2, Height / 3, paint);
			canvas.drawText(String.format("update : %5.1f ms ", drawstepdelay),
					Width / 2, Height / 3 + 20, paint);
			canvas.drawText(String.format("debug : %5.1f ms ", drawdebugdelay),
					Width / 2, Height / 3 + 40, paint);
			canvas.drawText(
					String.format("total : %5.1f ms", drawtotalusetime),
					Width / 2, Height / 3 + 60, paint);
			canvas.drawText(String.format("idle : %5.1f ms", idle), Width / 2,
					Height / 3 + 80, paint);
			canvas.drawText(String.format("tpf : %5.1f ", 1000f / FRAMERATE),
					Width / 2, Height / 3 + 100, paint);
			canvas.drawText("flake amount :" + mCrystalList.size(), Width / 2,
					Height / 3 + 120, paint);
			canvas.drawText(String.format("wind : %5.1f ms ", myWorld.wind),
					Width / 2, Height / 3 + 140, paint);
			canvas.drawText(String.format("fps : %d ", fpsCounter.getFrame()),
					Width / 2, Height / 3 + 160, paint);

		}
		deley = (System.nanoTime() - last) / 1000000.0f;
		debugdelay = deley;
	}

	@Override
	public void DrawFrame(Canvas canvas) {
		// TODO Auto-generated method stub
		try {
			present(canvas);
		} catch (NullPointerException e) {
			// TODO: handle exception
			SharedPreferences.Editor editor = mContext.getSharedPreferences(
					SnowBoard.SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit();
			editor.putString("back_bit_setting", "1");

			editor.commit();

		}

	}

	public void drawDestSnow() {
		if (config.mLockSnowOnGround)
			return;
		int x = mRnd.nextInt(Width * 2);
		int y = mRnd.nextInt(Height);
		// drawDestSnow(x,y);
		int rad = mDefaultRound / 2 + mRnd.nextInt(mDefaultRound - 5);
		RectF rect = new RectF(x - rad, y - rad, x + rad, y + rad);
		if (mGroundLayerCanvas != null) {

			mGroundLayerCanvas.drawOval(rect, mBaseBlurPaint);
			mGroundLayerCanvas.drawOval(rect, mSnowPaint);
		}
	}

	public void drawDestSnow(float x, float y) {
		if (config.mLockSnowOnGround)
			return;

		x = myWorld.BackLayerRect.left + x;
		y = myWorld.BackLayerRect.top + y;
		int rad = mDefaultRound / 2 + mRnd.nextInt(mDefaultRound - 5);
		RectF rect = new RectF(x - rad, y - rad, x + rad, y + rad);
		if (mGroundLayerCanvas != null) {

			mGroundLayerCanvas.drawOval(rect, mBaseBlurPaint);
			mGroundLayerCanvas.drawOval(rect, mSnowPaint);
		}
	}

	private void eraseRound() {
		// TODO Auto-generated method stub

		int x = mRnd.nextInt(Width);
		int y = mRnd.nextInt(Height);
		eraseRound(x, y);
	}

	private void eraseRound(int x, int y) {
		// TODO Auto-generated method stub
		if (config.mLockSnowOnGround)
			return;
		x = myWorld.BackLayerRect.left + x;
		y = myWorld.BackLayerRect.top + y;
		int rad = mDefaultRound * 3 + mRnd.nextInt(mDefaultRound * 2);
		Rect rect = new Rect(x - rad / 2, y - rad / 2, x + rad / 2, y + rad / 2);
		RectF rectf = new RectF(rect);
		Paint paint = new Paint();
		MaskFilter maskfilter = new BlurMaskFilter(rad * 0.6f, Blur.NORMAL);
		Xfermode xfer = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

		paint.setMaskFilter(maskfilter);
		paint.setXfermode(xfer);

		if (mGroundLayerCanvas != null) {

			mGroundLayerCanvas.drawOval(rectf, paint);
		}
	}

	public void initSizes(int w, int h) {
		Width = w;
		Height = h;

		float fw = w;
		mBaseRatio = fw / EnginConfig.BASE_WIDTH;
		if (DEBUG)
			Log.d("TAG", "ratio : " + mBaseRatio);
		int gapw = 0;
		int gaph = 0;
		if (w > h) {
			gaph = w - h;
			gaph /= 2;
		} else {
			gapw = h - w;
			gapw /= 2;
		}

		mDefaultRound = (int) (BASE_RAD * mBaseRatio);
		config.mFlakeMax *= mBaseRatio;
		mTouchRadDefault = mDefaultRound / 2;
		mTouchRAD = mTouchRadDefault;
		myWorld.init(w, h);

		touchoval = new RectF();

	}

	public void loadGraphics() {

		int size = 1;
		snows = new Bitmap[3];

		for (int i = 0; i < 3; i++) {
			Bitmap bitmap = Bitmap.createBitmap(3 * size + i * size, 3 * size
					+ i * size, Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);

			RectF oval = new RectF(0, 0, 3 * size + i * size, 3 * size + i
					* size);
			canvas.drawOval(oval, mPaint);
			snows[i] = bitmap;
		}

		Options opts = new BitmapFactory.Options();

		opts.inScaled = false;
		mFlakesBitmap = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.base_snow8, opts);
		mFlakeRegions = new Rect[4];
		mFlakeRegions[0] = new Rect(0, 0, 32, 32);
		mFlakeRegions[1] = new Rect(64, 0, 96, 32);
		mFlakeRegions[2] = new Rect(128, 0, 128, 64);
		mFlakeRegions[3] = new Rect(192, 0, 256, 64);
		mEffectsBitmap = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.snoweffect1_64, opts);

		mSnowEffectRegions[0][0] = new Rect(0, 0, 16, 16);
		mSnowEffectRegions[0][1] = new Rect(16, 0, 32, 16);
		mSnowEffectRegions[0][2] = new Rect(32, 0, 48, 16);
		mSnowEffectRegions[0][3] = new Rect(48, 0, 64, 16);
		mSnowEffectRegions[1][0] = new Rect(0, 16, 32, 48);
		mSnowEffectRegions[1][1] = new Rect(32, 16, 64, 48);
		mSnowEffectRegions[1][2] = new Rect(64, 16, 96, 48);
		mSnowEffectRegions[1][3] = new Rect(96, 16, 128, 48);

	}

	public void loadPaints() {

		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);

		mBaseBlurPaint = new Paint();

		mBaseBlurPaint.setColor(Color.WHITE);

		BlurMaskFilter blur = new BlurMaskFilter(mDefaultRound,
				BlurMaskFilter.Blur.OUTER);

		mBaseBlurPaint.setMaskFilter(blur);

		mSnowPaint = new Paint();
		mSnowPaint.setColor(Color.WHITE);
		mSnowPaint.setAlpha(200);
		mSnowPaint.setAntiAlias(true);
		myWorld.DisplayRect = new Rect(0, 0, Width, Height);
		mTouchPaint = new Paint();
		Xfermode xfer = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
		mTouchPaint.setXfermode(xfer);
		BlurMaskFilter touceblur = new BlurMaskFilter(
				(float) (mDefaultRound * 0.2), BlurMaskFilter.Blur.NORMAL);
		mTouchPaint.setMaskFilter(touceblur);
		mTouchPaint.setAlpha(100);
		mBorderPaint = new Paint();
		mBorderPaint.setColor(Color.RED);
		mBorderPaint.setStyle(Style.STROKE);

		mFlakePaint = new Paint();
		mFlakePaint.setAntiAlias(true);
		BlurMaskFilter touceblur1 = new BlurMaskFilter(3,
				BlurMaskFilter.Blur.INNER);
		mFlakePaint.setMaskFilter(touceblur1); // 36

		mErasePaint = new Paint();
		BlurMaskFilter touceblur2 = new BlurMaskFilter(10, Blur.NORMAL);
		mErasePaint.setAlpha(5);
		mErasePaint.setMaskFilter(touceblur2); // 36

		mPaintmodePaint = new Paint();
		mPaintmodePaint.setColor(mSnowPaint.getColor());
		MaskFilter maskfilter = new BlurMaskFilter(5.0f, Blur.NORMAL);
		mPaintmodePaint.setMaskFilter(maskfilter);

		mDrayPaint = new Paint();
		mDrayPaint.setColor(Color.parseColor("#01FFFFFF"));

		mDrayPaint.setXfermode(xfer);
	}

	public void offset(float xOffset, float yOffset, int xPixelOffset,
			int yPixelOffset) {
		// TODO Auto-generated method stub
	}

	public void rotate(boolean rotate, float z) {
		// TODO Auto-generated method stub

		if (rotate) {
			if (z < 0) {
				doProxi();

			} else if (z > 300) {
				if (myWorld.RotateDst != MyWorld.ANGLE_ZERO) {
					myWorld.rotatingStart(MyWorld.ANGLE_ZERO);
					deaccelerate();
				}
				flakeState = FlakeState.Normal;
			} else if (z > 250) {
				if (myWorld.RotateDst != MyWorld.ANGLE_RIGHT) {
					myWorld.rotatingStart(MyWorld.ANGLE_RIGHT);
					accelerate(2);

					flakeState = FlakeState.Snowstorm;

				}
				deProxi();
			} else if (z > 150) {
				deProxi();
			} else if (z > 60) {
				if (myWorld.RotateDst != MyWorld.ANGLE_LEFT) {
					myWorld.rotatingStart(MyWorld.ANGLE_LEFT);
					accelerate(2);

					deProxi();
					flakeState = FlakeState.Rain;

				}
			}
		} else if (z > 0) {
			if (myWorld.RotateDst != MyWorld.ANGLE_ZERO) {
				myWorld.rotatingStart(MyWorld.ANGLE_ZERO);
				deaccelerate();
			}
			flakeState = FlakeState.Normal;
		}

		else {
		}
		if (DEBUG)
			Log.d("TAG", "rotate : " + rotate + "z :" + z);
	}

	public void setBackbround(Bitmap bitmap) {
		synchronized (this) {
			if (bitmap == null) {
				config.mDrawBackgroundEnabled = false;
			} else {

				Bitmap temp = mBackgroundBitmap;
	
				mBackgroundBitmap = bitmap.copy(Config.RGB_565, false);
	
				if (bitmap != null)
					bitmap.recycle();
	
				if (temp != null)
					temp.recycle();
				if (mBaseLayer != null)
					temp.recycle();
				mBaseLayer = Bitmap.createBitmap(myWorld.BackLayerRect.width(),
						myWorld.BackLayerRect.height(), Config.ARGB_8888);
	
				mGroundLayerCanvas = new Canvas(mBaseLayer);

			}
		}
	}

	public void setBackground(boolean back) {
		// TODO Auto-generated method stub
		config.mDrawBackgroundEnabled = back;

	}

	public void setFlakeSize(int size) {
		// int size = 1;
		snows = new Bitmap[3];
		Paint paint = new Paint();
		// Xfermode xp = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);
		// paint.setXfermode(xp);
		paint.setColor(mFlakeColor);

		mFlakeSize = size * 6;
		// config.mFlakeMax = 125 - size * 5;
		mGroundRate = 0.1f + (size / 20f);

		// synchronized (mList) {
		// // mList.clear();
		// // for (int i = 0; i < config.mFlakeMax; i++) {
		// // Crystal cr = new Crystal();
		// // cr.x = mRnd.nextInt(Width);
		// // cr.y = mRnd.nextInt(Height);
		// // cr.rad = mRnd.nextInt(3);
		// // cr.velocityX = (mRnd.nextFloat() - 0.5f) * 3f;
		// // cr.velocityY = (mRnd.nextFloat()) * 1f;
		// // cr.rotate = mRnd.nextInt(360);
		// // mList.add(cr);
		// // }
		// }

		// int i = 1;
		// for (int i = 0 ; i < 3 ; i++)
		// {
		// Bitmap temp = null;
		// Canvas canvas = null;
		// RectF oval = null;
		// if (size > 4) {
		// temp =
		// BitmapFactory.decodeResource(mContext.getResources(),R.drawable.base_snow1);
		// Bitmap bitmap = Bitmap.createBitmap(mFlakeSize / 3, mFlakeSize / 3,
		// Config.ARGB_8888);
		// canvas = new Canvas(bitmap);
		//
		// oval = new RectF(0, 0, mFlakeSize / 3, mFlakeSize / 3);
		// canvas.drawBitmap(temp, null, oval, mFlakePaint);
		// // canvas.drawOval(oval, paint);
		// snows[0] = bitmap;
		// temp.recycle();
		// } else {
		// // temp = BitmapFactory.decodeResource(mContext.getResources(),
		// // R.drawable.base_snow5);
		// Bitmap bitmap = Bitmap.createBitmap(mFlakeSize / 2, mFlakeSize / 2,
		// Config.ARGB_8888);
		// canvas = new Canvas(bitmap);
		//
		// oval = new RectF(0, 0, mFlakeSize / 2, mFlakeSize / 2);
		// // canvas.drawBitmap(temp, null,oval, null);
		// canvas.drawOval(oval, mFlakePaint);
		// snows[0] = bitmap;
		// }
		// if (size > 4) {
		// temp = BitmapFactory.decodeResource(mContext.getResources(),
		// R.drawable.snowflake03);
		// Bitmap bitmap1 = Bitmap.createBitmap((int) (mFlakeSize * 3 /
		// 4.0),(int) (mFlakeSize * 3 / 4.0), Config.ARGB_8888);
		// canvas = new Canvas(bitmap1);
		//
		// oval = new RectF(0, 0, (int) (mFlakeSize * 3 / 4.0),
		// (int) (mFlakeSize * 3 / 4.0));
		// canvas.drawBitmap(temp, null, oval, mFlakePaint);
		// // canvas.drawOval(oval, paint);
		// snows[1] = bitmap1;
		// temp.recycle();
		// } else {
		// Bitmap bitmap1 = Bitmap.createBitmap((int) (mFlakeSize * 3 /
		// 4.0),(int) (mFlakeSize * 3 / 4.0), Config.ARGB_8888);
		// canvas = new Canvas(bitmap1);
		//
		// oval = new RectF(0, 0, (int) (mFlakeSize * 3 / 4.0),
		// (int) (mFlakeSize * 3 / 4.0));
		// // canvas.drawBitmap(temp, null,oval, null);
		// canvas.drawOval(oval, mFlakePaint);
		// snows[1] = bitmap1;
		// }
		//
		// if (size > 4) {
		// temp = BitmapFactory.decodeResource(mContext.getResources(),
		// R.drawable.snowflake02);
		//
		// Bitmap bitmap2 = Bitmap.createBitmap(mFlakeSize, mFlakeSize,
		// Config.ARGB_8888);
		// canvas = new Canvas(bitmap2);
		//
		// oval = new RectF(0, 0, mFlakeSize, mFlakeSize);
		// canvas.drawBitmap(temp, null, oval, mFlakePaint);
		// // canvas.drawOval(oval, paint);
		// snows[2] = bitmap2;
		// temp.recycle();
		// } else {
		//
		// Bitmap bitmap2 = Bitmap.createBitmap(mFlakeSize, mFlakeSize,
		// Config.ARGB_8888);
		// canvas = new Canvas(bitmap2);
		//
		// oval = new RectF(0, 0, mFlakeSize, mFlakeSize);
		// // canvas.drawBitmap(temp, null,oval, null);
		// canvas.drawOval(oval, mFlakePaint);
		// snows[2] = bitmap2;
		// }

		// Bitmap temp = null;
		Canvas canvas = null;
		RectF oval = null;
		if (size > 4) {
			// temp =
			// BitmapFactory.decodeResource(mContext.getResources(),R.drawable.base_snow1);
			Bitmap bitmap = Bitmap.createBitmap(mFlakeSize / 3, mFlakeSize / 3,
					Config.ARGB_8888);
			canvas = new Canvas(bitmap);

			oval = new RectF(0, 0, mFlakeSize / 3, mFlakeSize / 3);
			canvas.drawBitmap(mFlakesBitmap, mFlakeRegions[0], oval,
					mFlakePaint);
			// canvas.drawOval(oval, paint);
			snows[0] = bitmap;
			// temp.recycle();
		} else {
			// temp = BitmapFactory.decodeResource(mContext.getResources(),
			// R.drawable.base_snow5);
			Bitmap bitmap = Bitmap.createBitmap(mFlakeSize / 2, mFlakeSize / 2,
					Config.ARGB_8888);
			canvas = new Canvas(bitmap);

			oval = new RectF(0, 0, mFlakeSize / 2, mFlakeSize / 2);
			// canvas.drawBitmap(temp, null,oval, null);
			canvas.drawOval(oval, mFlakePaint);
			snows[0] = bitmap;
		}
		if (size > 3) {
			// temp =
			// BitmapFactory.decodeResource(mContext.getResources(),R.drawable.snowflake03);
			Bitmap bitmap1 = Bitmap.createBitmap((int) (mFlakeSize * 3 / 4.0),
					(int) (mFlakeSize * 3 / 4.0), Config.ARGB_8888);
			canvas = new Canvas(bitmap1);

			oval = new RectF(0, 0, (int) (mFlakeSize * 3 / 4.0),
					(int) (mFlakeSize * 3 / 4.0));
			canvas.drawBitmap(mFlakesBitmap, mFlakeRegions[1], oval,
					mFlakePaint);
			// canvas.drawOval(oval, paint);
			snows[1] = bitmap1;
			// temp.recycle();
		} else {
			Bitmap bitmap1 = Bitmap.createBitmap((int) (mFlakeSize * 3 / 4.0),
					(int) (mFlakeSize * 3 / 4.0), Config.ARGB_8888);
			canvas = new Canvas(bitmap1);

			oval = new RectF(0, 0, (int) (mFlakeSize * 3 / 4.0),
					(int) (mFlakeSize * 3 / 4.0));
			// canvas.drawBitmap(temp, null,oval, null);
			canvas.drawOval(oval, mFlakePaint);
			snows[1] = bitmap1;
		}

		if (size > 2) {
			// temp =
			// BitmapFactory.decodeResource(mContext.getResources(),R.drawable.snowflake02);

			Bitmap bitmap2 = Bitmap.createBitmap(mFlakeSize, mFlakeSize,
					Config.ARGB_8888);
			canvas = new Canvas(bitmap2);

			oval = new RectF(0, 0, mFlakeSize, mFlakeSize);
			canvas.drawBitmap(mFlakesBitmap, mFlakeRegions[3], oval,
					mFlakePaint);
			// canvas.drawOval(oval, paint);
			snows[2] = bitmap2;
			// temp.recycle();
		} else {

			Bitmap bitmap2 = Bitmap.createBitmap(mFlakeSize, mFlakeSize,
					Config.ARGB_8888);
			canvas = new Canvas(bitmap2);

			oval = new RectF(0, 0, mFlakeSize, mFlakeSize);
			// canvas.drawBitmap(temp, null,oval, null);
			canvas.drawOval(oval, mFlakePaint);
			snows[2] = bitmap2;
		}
		// }
		setSnowFlakeColor(mFlakeColor);
	}

	public void setFlakeTouchable(boolean enable) {
		// TODO Auto-generated method stub
		config.mFlakeTouch = enable;
	}

	public void setScale(int i) {
		// TODO Auto-generated method stub
		mWidthScale = i;
		if (mWidthScale == 1) {
			myWorld.BackLayerRect.offsetTo(0, 0);
		}
	}

	public void setSnowFlakeColor(int color) {
		// TODO Auto-generated method stub
		mFlakeColor = color;
		Xfermode xp = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);
		Paint paint = new Paint();
		paint.setXfermode(xp);
		paint.setColor(color);
		mPaintmodePaint.setColor(color);

		int red = Color.red(color);
		int green = Color.green(color);
		int blue = Color.blue(color);

		Canvas canvas = new Canvas(snows[0]);
		paint.setColor(Color.rgb(red - 50, green - 50, blue - 50));
		canvas.drawPaint(paint);

		paint.setColor(Color.rgb(red - 25, green - 25, blue - 25));
		canvas.setBitmap(snows[1]);
		canvas.drawPaint(paint);
		paint.setColor(Color.rgb(red, green, blue));
		canvas.setBitmap(snows[2]);
		canvas.drawPaint(paint);
	}

	public void setPaintMode(boolean mode) {
		config.paintMode = mode;
	}

	public void setSnowGroundColor(int color) {
		// TODO Auto-generated method stub
		mSnowPaint.setColor(color);
		mBaseBlurPaint.setColor(color);
		mSnowPaint.setAlpha(100);
	}

	public void RainStep() {
		effectList.clear();
		myWorld.windGenerate();

		myWorld.viewanglestep();

		if (!myWorld.mRotating) {
			myWorld.velcotiyStep();

			for (Crystal cr : mCrystalList) {
				if (myWorld.BaseRectF.contains(cr.x, cr.y)) {

					cr.x += (myWorld.wind + myWorld.VelocityX + cr.velocityX);
					cr.y += mRnd.nextFloat()
							+ (down + 1 + cr.rad + cr.velocityY) * raing;
					cr.rotate += cr.velocityX * 2;

				} else {
					if (cr.x < 0) {
						cr.x = cr.x + Width;
					} else if (cr.x > Width) {
						cr.x = cr.x % Width;
					} else {

						cr.x = mRnd.nextInt(Width);
						cr.y = mRnd.nextInt(20);
						cr.yRange = 20 + Height;

					}
				}
			}
		}
		if (mRnd.nextInt(20) < 2)
			eraseRound();

		mTouchList.clear();
		// if ( mRnd.nextInt(5) < 2)
		// mGroundLayerCanvas.drawPaint( mDrayPaint);
	}

	public void StandardStep() {
		
		if (!config.visibleFlakes) {
			for (Crystal cr : mCrystalList) {
				if (myWorld.BaseRectF.contains(cr.x, cr.y)) {
					cr.x += cr.velocityX;
					cr.y += mRnd.nextFloat() + down + 1 + cr.rad + cr.velocityY
							+ 5;
					createeffect(cr);
				} else {
					if (cr.x < 0) {
						cr.x = cr.x + Width;

					} else if (cr.x > Width) {
						cr.x = cr.x % Width;

					} else {

						cr.x = mRnd.nextInt(Width);
						cr.y = mRnd.nextInt(20);
						cr.yRange = 20 + mRnd.nextInt(Height + Height);
					}
				}
			}
			effectstep();
		} else {

			myWorld.windGenerate();
			myWorld.viewanglestep();
			float wind_down =  Math.abs(myWorld.wind / 2);
			if (!myWorld.mRotating) {
				myWorld.velcotiyStep();
				if (mRnd.nextInt(500) < 2)
					eraseRound();
				for (Crystal cr : mCrystalList) {
					for (RectF rectf : mTouchList) {

						if (rectf.contains(cr.x, cr.y)) {
							if (!cr.touched) {
								cr.touched = true;
								cr.offsetX = (5 + mRnd.nextInt(5)
										* (1 + cr.rad))
										* (mRnd.nextBoolean() ? 1 : -1);// Trnd.nextInt(30)
								cr.offsetY = (10 * mBaseRatio + mRnd.nextInt(5)* (1 + cr.rad))* -1;// Trnd.nextInt(30)
							}
						}
					}
					if (myWorld.BaseRectF.contains(cr.x, cr.y)) {

						cr.step();
						float acc = 1f + (cr.rad * 0.5f);

						cr.x += myWorld.wind * acc + myWorld.VelocityX
								+ cr.velocityX;
						cr.y += mRnd.nextFloat() + wind_down + down + 1 + cr.rad + cr.velocityY;
						cr.rotate += cr.velocityX * 2;

						createeffect(cr);
					} else {
						if (cr.x < 0) {
							cr.x = cr.x + Width;

						} else if (cr.x > Width) {
							cr.x = cr.x % Width;

						} else {

							cr.x = mRnd.nextInt(Width);
							cr.y = mRnd.nextInt(20);
							cr.yRange = 20 + mRnd.nextInt(Height + Height);
						}

					}
				}

			}
			effectstep();
		}

		mTouchList.clear();
	}

	public void createeffect(Crystal cr) {
		if (cr.y > cr.yRange) {

			// if (Math.random() < 0.5)
			{
				// if (Math.random() < 0.5)
				if (cr.rad == 2 && mRnd.nextBoolean()) {
					SnowEffect se = new SnowEffect();
					se.durtaion = 20f;
					se.x = cr.x;
					se.y = cr.y;
					if (effectList.size() > 20)
						effectList.remove(0);
					effectList.add(se);
				}

			}
			cr.y = Height;
		}
	}

	private void createeffect(Crystal crystal, boolean b) {
		// TODO Auto-generated method stub
		SnowEffect se = new SnowEffect();
		se.durtaion = 20f;
		se.x = crystal.x;
		se.y = crystal.y;
		if (effectList.size() > 20)
			effectList.remove(0);
		effectList.add(se);
	}

	public void effectstep() {
		SnowEffect e;

		for (int i = 0; i < effectList.size(); i++) {

			e = effectList.get(i);
			e.step();
			if (!e.isEnabled()) {
				drawDestSnow(e.x, e.y);
				effectList.remove(e);
				i--;
				continue;
			}
		}
		if (myWorld.RotateDst == MyWorld.ANGLE_RIGHT) {
			// for (int i = 0; i < 1; i++)
			drawDestSnow();
		} else if (myWorld.RotateDst == MyWorld.ANGLE_LEFT) {
			SnowEffect se = new SnowEffect();
			se.durtaion = 10f;
			se.x = mRnd.nextInt(Width);

			se.y = mRnd.nextInt(Height);
			if (rainList.size() < 1)
				rainList.add(se);
		}
	}

	public void touchUpdate(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		float x1 = x + myWorld.BackLayerRect.left;
		float y1 = y + myWorld.BackLayerRect.top;
		touchoval.set(x1 - mTouchRAD, y1 - mTouchRAD, x1 + mTouchRAD, y1
				+ mTouchRAD);
		RectF rectf = new RectF(x - mTouchRAD * 2, y - mTouchRAD * 2, x
				+ mTouchRAD * 2, y + mTouchRAD * 2);
		synchronized (mGroundLayerCanvas) {
			if (!config.mLockSnowOnGround) {
				if (config.paintMode) {
					mGroundLayerCanvas.drawOval(touchoval, mPaintmodePaint);
				} else {
					mGroundLayerCanvas.drawOval(touchoval, mTouchPaint);
				}
			}

		}
		// synchronized (mGroundLayerCanvas) {
		if (config.mFlakeTouch) {
			// mat.setScale(16f, 16f, rectf.centerX(), rectf.centerY());

			if (myWorld.isRotating()) {
				// mat.reset();
				Matrix mat = new Matrix();
				mat.setRotate(-myWorld.angle, myWorld.BaseRectF.centerX(),
						myWorld.BaseRectF.centerY());
				mat.mapRect(rectf);
			}
			// Log.d("Snow", rectf.toString());
			mTouchList.add(rectf);

		}
		// }

	}

	@Override
	public void update(float deltaTime) {
		// TODO Auto-generated method stub
		long last = System.nanoTime();
		switch (flakeState) {
		case Rain:
			RainStep();
			break;

		case Snowstorm:
		case Normal:
		case SnowFallFast:
			StandardStep();
			break;
		default:
			break;
		}

		stepdelay = (System.nanoTime() - last) / 1000000.0f;

	}

	class SnowEffect {
		float durtaion;
		float x;
		float y;
		int currentFrame;
		int currentTime = 0;

		// Rect[][] rect = new Rect[3][4];
		public SnowEffect() {

		}

		public void step() {
			currentTime++;
		}

		public boolean isEnabled() {
			currentFrame = (int) (currentTime / durtaion * 4);
			if (currentTime < durtaion)
				return true;
			else
				return false;
		}
	}

	public void setThickness(int thickness) {
		// TODO Auto-generated method stub
		mTouchRAD = thickness;
		BlurMaskFilter touceblur = new BlurMaskFilter(mTouchRAD * 0.8f,
				BlurMaskFilter.Blur.NORMAL);
		mTouchPaint.setMaskFilter(touceblur);
	}

	public void setSwipe(boolean swipe) {
		config.swipe = swipe;
	}

	public void setBatterySaveMode(boolean mode) {
		// TODO Auto-generated method stub
		config.visibleFlakes = !mode;

	}

	public void setSwipLockMode(boolean mode) {
		// TODO Auto-generated method stub
		config.swipe = !mode;
	}

	public void setDebugModeSetting(boolean mode) {
		// TODO Auto-generated method stub
		DEBUG = mode;
	}

	class EnginConfig {
		public boolean visibleFlakes = false;
		public boolean paintMode;
		final static float BASE_WIDTH = 480.0f;
		final static float BASE_HEIGHT = 800.0f;
		boolean mDrawBackgroundEnabled = false;
		float frame;

		private int mFlakeMax = 70;
		private boolean mLockSnowOnGround = false;
		private boolean mFlakeTouch = true;
		private boolean swipe;
		private boolean selfswipe = false;

	}

	GestureDetector mGestureDetector = new GestureDetector(
			new OnGestureListener() {

				public boolean onDown(MotionEvent e) {
					// TODO Auto-generated method stub

					return false;
				}

				public boolean onFling(MotionEvent e1, MotionEvent e2,
						float velocityX, float velocityY) {
					// TODO Auto-generated method stub
					if (Math.abs(velocityX) > 500
							&& myWorld.RotateDst == MyWorld.ANGLE_ZERO) {
						myWorld.VelocityX = 50 * (velocityX < 0.0f ? -1 : 1);
						// if (!config.selfswipe)
						// {
						// int to1 = 0;
						// if (velocityX <0.0f)
						// {
						// to1 =0;
						// }
						// else
						// {
						// to1 = snowboardWorld.mBackLayerRect.width();
						// }
						//
						// final int to =to1;
						// final int range = to -
						// snowboardWorld.mBackLayerRect.left;
						// Thread th = new Thread(new Runnable() {
						//
						// public void run() {
						// // TODO Auto-generated method stub
						// long start = System.currentTimeMillis();
						// long curreunt = start;
						// long end = start + 1000;
						// while(end > curreunt)
						// {
						// long gap = end - curreunt;
						// float ratio = gap / 1000.0f;
						// snowboardWorld.mBackLayerRect.offset( (int)
						// (range*ratio), 0);
						// Log.d("TAG",snowboardWorld.mBackLayerRect.toString());
						// try {
						// Thread.sleep(200);
						// } catch (InterruptedException e) {
						// // TODO Auto-generated catch block
						// e.printStackTrace();
						// }
						// curreunt = System.currentTimeMillis();
						// }
						// }
						// });
						// th.start();
						// }
					}
					return false;
				}

				public void onLongPress(MotionEvent e) {
					// TODO Auto-generated method stub

				}

				public boolean onScroll(MotionEvent e1, MotionEvent e2,
						float distanceX, float distanceY) {
					// TODO Auto-generated method stub
					touchUpdate(e2);

					return false;
				}

				public void onShowPress(MotionEvent e) {
					// TODO Auto-generated method stub

				}

				public boolean onSingleTapUp(MotionEvent e) {
					// TODO Auto-generated method stub

					return false;
				}
			});

	public void setSnowOnGround(boolean flag) {
		config.mLockSnowOnGround = flag;

	}

	// ///////////////////////////
	public MyWorld myWorld = new MyWorld();

	private FPSCounter fpsCounter;
	long lastTime = 0;
	long delay = 0;

	private Paint mPaint;
	private Paint mSnowPaint;
	private Paint mBaseBlurPaint;
	private Paint mTouchPaint;
	private Paint mBorderPaint;

	private Paint mFlakePaint;
	private Paint mErasePaint;

	private Canvas mGroundLayerCanvas;

	private int BASE_RAD = 20;

	private int mDefaultRound = 0;

	// private int mLQW = 0;
	//
	// private int mLQH = 0;

	private int scale = 1;

	private float down_default = 0.3f;

	private float down = down_default;

	private ArrayList<Crystal> mCrystalList = new ArrayList<Crystal>();
	private ArrayList<SnowEffect> effectList = new ArrayList<SnowEffect>();
	private ArrayList<SnowEffect> rainList = new ArrayList<SnowEffect>();

	// private int down_half = -1;

	private Random mRnd;

	// private int mOrientZ = 0;
	private int mTouchRAD;

	private RectF touchoval;

	private Bitmap[] snows;
	private Bitmap mBackgroundBitmap;
	private Bitmap mBaseLayer;
	private Bitmap mFlakesBitmap;
	private Bitmap mEffectsBitmap;

	private Rect[] mFlakeRegions;

	private int mFlakeColor;
	private int mWidthScale;

	private ArrayList<RectF> mTouchList;

	private int mFlakeSize;
	private float mGroundRate = 0.5f;
	private EnginConfig config = new EnginConfig();
	private Rect[][] mSnowEffectRegions = new Rect[3][4];
	FlakeState flakeState = FlakeState.Normal;

	private int mTouchRadDefault;
	private Paint mPaintmodePaint;

	private float stepdelay;
	private float drawpresentdeley;
	private float drawstepdelay;
	private boolean DEBUG = false;

	private float mBaseRatio;

	private boolean lastswipe;

	private boolean landscape;

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
	}

	public void setWH(int width, int height) {
		// TODO Auto-generated method stub
		myWorld.BackLayerRect.right = width;
		myWorld.BackLayerRect.left = 0;
		// config.swipe = false;
		myWorld.BackLayerRect.bottom = height;
		myWorld.DisplayRect.right = width;

		myWorld.DisplayRect.bottom = height;
		if (width > height) {
			// lastswipe = config.swipe;
			// config.swipe = false;
			landscape = true;
		} else {
			landscape = false;
			// config.swipe = lastswipe;
		}

	}

	public void setWind(int index, int value) {
		// TODO Auto-generated method stub
		switch (index) {
		case 1:
			myWorld.windFreq = value * 1000;
			break;
		case 2:
			myWorld.wind_changer = value;
			break;
		case 3:
			myWorld.wind_max = value;
			break;
		default:
			break;
		}
	}
}
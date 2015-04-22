package com.jcoolstory.SnowBoard.Engin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.jcoolstory.SnowBoardFree.R;


import android.content.Context;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import android.graphics.Color;

import android.graphics.Bitmap.Config;


import android.os.Build;
import android.os.Environment;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.widget.Toast;

public class SnowBoard extends WallpaperService {
	public static String SHARED_PREFS_NAME = "snowbaordfreev2";
	public static String TAG = "snowbaordtag";
	public static String CAHCE_FILE_NAME="wallpapercache.ch";
	@Override
	public Engine onCreateEngine() {
		// TODO Auto-generated method stub
		return new SnowEngine(this);
		
	}

	class SnowEngine extends Engine {
		private int Height = 0;
		private boolean mVisible = true;
		SnowSurfaceEngine myThread = null;
		Context mContext;

		private SharedPreferences mPrefs;

		private Boolean bBack;
		private int Width;
		private OrientationEventListener orienListener;

		public SnowEngine(Context context) {
			super();
			// TODO Auto-generated constructor stub

			mContext = context;
		}
		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			// TODO Auto-generated method stub
			super.onCreate(surfaceHolder);

			Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
					.getDefaultDisplay();

			Width = display.getWidth();
			Height = display.getHeight();

			myThread = new SnowSurfaceEngine(surfaceHolder, mContext, Width,
					Height);
			mPrefs = SnowBoard.this.getSharedPreferences(SHARED_PREFS_NAME, 0);
			mPrefs.registerOnSharedPreferenceChangeListener(shared);
			
			int version = Integer.parseInt(Build.VERSION.SDK);
			if (version < 10)
				setTouchEventsEnabled(true);

			orienListener = new OrientationEventListener(mContext) {

				@Override
				public void onOrientationChanged(int orientation) {
					// TODO Auto-generated method stub
					Log.d("TAG","orientation : " +  orientation);
					myThread.rotate(true, orientation);
					
				}
			};
			
		}
		@Override
		public void onOffsetsChanged(float xOffset, float yOffset,
				float xOffsetStep, float yOffsetStep, int xPixelOffset,
				int yPixelOffset) {
			// TODO Auto-generated method stub
			super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep,
					xPixelOffset, yPixelOffset);


			if (myThread != null) {
				myThread.offset(xOffset, yOffset, xPixelOffset, yPixelOffset);
			}
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			// TODO Auto-generated method stub

			super.onSurfaceChanged(holder, format, width, height);
			myThread.setWH(width,height);
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			super.onSurfaceCreated(holder);
//			Log.d("TAG", "onSurfaceCreated");
			shared.onSharedPreferenceChanged(mPrefs, null);
			myThread.resumePainting();
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			super.onSurfaceDestroyed(holder);
			// Log.d("TAG", "onSurfaceDestroyed" );
			unSaredPreference();
			myThread.stopPainting();
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			// TODO Auto-generated method stub
			super.onVisibilityChanged(visible);
	//		Log.d("TAG", "onVisibilityChanged");
			
			myThread.rotate(true, 350);
			if (visible) {
				myThread.resumePainting();
				// registerSensor();
				mVisible = true;
				// setTouchEventsEnabled(mVisible);
				orienListener.enable();
			} else {

				myThread.pausePainting();
				// unregisterSensor();
				mVisible = false;
				// setTouchEventsEnabled(mVisible);
				orienListener.disable();
			}
			
		}
		

		@Override
		public void setTouchEventsEnabled(boolean enabled) {
			// TODO Auto-generated method stub
			super.setTouchEventsEnabled(enabled);
		}

		@Override
		public void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
		}

		@Override
		public void onTouchEvent(MotionEvent event) {
			// TODO Auto-generated method stub
			myThread.doTouchEvent(event);

		}

		public void unSaredPreference() {
			mPrefs.unregisterOnSharedPreferenceChangeListener(shared);
		}

		OnSharedPreferenceChangeListener shared = new OnSharedPreferenceChangeListener() {

			public void onSharedPreferenceChanged(
					SharedPreferences sharedPreferences, String key) {
				// TODO Auto-generated method stub
				
				myThread.pausePainting();

				if ("savepicture".equals(key))
				{
					String flag = sharedPreferences.getString("savepicture","-1");
					if ("1".equals(flag))
					{
						SharedPreferences.Editor editor = getSharedPreferences(
								SnowBoard.SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit();
						editor.putString("savepicture", "-1");
						editor.commit();
						if (saveFrame())
						{
							
						}
					}
				}
				if (key == null || key.equals("snowflakecolor")) {

					int color = sharedPreferences.getInt("snowflakecolor",
							Color.WHITE);
					myThread.setSnowFlakeColor(color);
				}
				if (key == null || key.equals("snowgroundcolor")) {

					int color = sharedPreferences.getInt("snowgroundcolor",
							Color.WHITE);
					myThread.setSnowGroundColor(color);
				}
				if (key == null || key.equals("lock_setting")) {

					boolean lock = sharedPreferences.getBoolean("lock_setting",
							false);
					myThread.setSnowOnGround(lock);
				}
				if (key == null || key.equals("background_setting")) {

					bBack = sharedPreferences.getBoolean("background_setting",
							true);
					myThread.setBackground(bBack);
				}
				if (key == null || key.equals("flake_touchable_setting")) {
					boolean enable = sharedPreferences.getBoolean(
							"flake_touchable_setting", true);
					myThread.setFlakeTouchable(enable);
				}
				if (key == null || key.equals("frame_setting")) {
					int frame = sharedPreferences.getInt("frame_setting", 20);
					myThread.setFrame(frame);
				}
				if (key == null || key.equals("flake_size_setting")) {
					int size = sharedPreferences
							.getInt("flake_size_setting", 5);
					myThread.setFlakeSize(size);
				}
				if (key == null || key.equals("paint_mode_setting")) {
					boolean mode = sharedPreferences
							.getBoolean("paint_mode_setting", false);
					myThread.setPaintMode(mode);
				}
				if (key == null || key.equals("batter_save_mode_setting")) {
					boolean mode = sharedPreferences
							.getBoolean("batter_save_mode_setting", false);
					myThread.setBatterySaveMode(mode);
					if (mode)
						myThread.setFrame(10);
					else
					{
						int frame = sharedPreferences.getInt("frame_setting", 20);
						myThread.setFrame(frame);
					}
				}
				if (key == null || key.equals("swip_lock_setting")) {
					boolean mode = sharedPreferences
							.getBoolean("swip_lock_setting", false);
					myThread.setSwipLockMode(mode);
				}
				if (key == null || key.equals("debug_mode_setting")) {
					boolean mode = sharedPreferences
					.getBoolean("debug_mode_setting", false);
					myThread.setDebugModeSetting(mode);
				}
				if (key == null || key.equals("back_bit_setting")) {
					String index = sharedPreferences.getString(
							"back_bit_setting", "1");
					setBackground(key,index);
				
				}
				if (key == null || key.equals("finger_thickness_setting")) {
					int thickness = sharedPreferences.getInt(
							"finger_thickness_setting", 13);
					myThread.setThickness(thickness);
				}
				if (key == null || key.equals("wind_cycle_setting")) {
					int index = sharedPreferences.getInt(
							"wind_cycle_setting", 5);
					myThread.setWind(1,index);
				
				}
				if (key == null || key.equals("wind_change_setting")) {
					int index = sharedPreferences.getInt(
							"wind_change_setting", 5);
					myThread.setWind(2,index);
				
				}
				if (key == null || key.equals("wind_max_setting")) {
					int index  = sharedPreferences.getInt(
							"wind_max_setting", 5);
					myThread.setWind(3,index);
				
				}
			//	myThread.resumePainting();
			}
		};
		public void setDefaultBackground()
		{
			
			SharedPreferences.Editor editor = getSharedPreferences(
					SnowBoard.SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit();
			editor.putString("back_bit_setting","1");

			editor.commit();
		}
		public void setBackground(String key,String index)
		{
			
	//		Log.d("TAG", "SetBitmap" );
			try {

				if ("0".equals(index)) {
	
					File toFile = getBaseContext().getCacheDir();
					Bitmap bitmap;
//					Log.d("TAG", "X:" + toFile							+ "Y:" + toFile.toString());
					try {
						toFile = new File(toFile.getAbsolutePath()
								+ "/" + SnowBoard.CAHCE_FILE_NAME);						
				
							bitmap = BitmapFactory.decodeFile(toFile
									.toString());
//							Log.d("Snow", "X:" + bitmap.getWidth()	+ "Y:" + bitmap.getHeight());
							
							myThread.setBackbround(bitmap);						

					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(getApplicationContext(), "image not found", Toast.LENGTH_SHORT).show();
						setDefaultBackground();
					}
				} else if ("1".equals(index)) {

					Bitmap temp = BitmapFactory.decodeResource(
							getResources(), R.drawable.background1);
					myThread.setBackbround(temp);
					
				} else if ("2".equals(index)) {
					Bitmap temp = BitmapFactory.decodeResource(
							getResources(), R.drawable.background2);
					myThread.setBackbround(temp);
					
				} else if ("3".equals(index)) {
					Bitmap temp = BitmapFactory.decodeResource(
							getResources(), R.drawable.background3);
					myThread.setBackbround(temp);
					
				}
				else if ("4".equals(index)) {
					Bitmap temp = BitmapFactory.decodeResource(
							getResources(), R.drawable.background4);
					myThread.setBackbround(temp);
					
				}else if ("-1".equals(index)){
					
				}
				else{
					setDefaultBackground();
				}
				
			}
			catch (OutOfMemoryError e) {
				// TODO: handle exception
				e.printStackTrace();
				Toast.makeText(mContext, "Out of Memory",
						Toast.LENGTH_SHORT).show();
				setDefaultBackground();
			}
			catch (Exception e) {
				// TODO: handle exception			
				e.printStackTrace();
				//setDefaultBackground();
			}
			
		}
		public boolean saveFrame() {
			// TODO Auto-generated method stub
			Bitmap bit = null;
			OutputStream stream = null;
			try {
				Bitmap bitmap = Bitmap.createBitmap(Width, Height, Config.RGB_565);
				bit = myThread.saveFrame(bitmap);			
				
				String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp/"  +String.valueOf(System.currentTimeMillis() +".jpg");
				File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp/");
				if (!dir.exists())
				{					
					dir.mkdir();
				}			
				
				Toast.makeText(mContext, "Try save file ", Toast.LENGTH_LONG).show();
				stream = new FileOutputStream(fileName);
				bit.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				stream.flush();
				stream.close();
				Toast.makeText(mContext, "Success : " + fileName, Toast.LENGTH_LONG).show();
				SharedPreferences.Editor editor = getSharedPreferences(
						SnowBoard.SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit();
				editor.putString("CapturePath", fileName);
				editor.commit();
		        
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				Toast.makeText(mContext, "Save Fail / File not found ", Toast.LENGTH_LONG).show();
				SharedPreferences.Editor editor = getSharedPreferences(
						SnowBoard.SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit();
				editor.putString("CapturePath", "-1");
				editor.commit();
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(mContext, "Save Fail / IOException", Toast.LENGTH_LONG).show();
				SharedPreferences.Editor editor = getSharedPreferences(
						SnowBoard.SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit();
				editor.putString("CapturePath", "-1");
				editor.commit();
				
				return false;
			} catch (Exception e) {
				// TODO: handle exception
				Toast.makeText(mContext, "Save Fail ", Toast.LENGTH_LONG).show();
				SharedPreferences.Editor editor = getSharedPreferences(
						SnowBoard.SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit();
				editor.putString("CapturePath", "-1");
				editor.commit();
				return false;
			}
			finally{
				if (bit != null)
					bit.recycle();	
			}
			
			return true; 
		}
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		
		super.onLowMemory();
		Toast.makeText(this, "LowMemory", Toast.LENGTH_SHORT).show();
	}
}

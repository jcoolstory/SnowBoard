package com.jcoolstory.main;

import com.jcoolstory.SnowBoardFree.SnowBoardPrefer;

import android.app.Activity;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class launcher extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		try
		{
		 WallpaperManager wm = WallpaperManager.getInstance(this);
		 WallpaperInfo wi = wm.getWallpaperInfo();
		    if (wi ==null)
		    {
				String str = WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER;
				Intent intent = new Intent(str);
				Toast.makeText(this, "Please Select \"SnowBoard\"", Toast.LENGTH_SHORT).show();
				startActivity(intent);
				
				finish();
		    }
		    else
		    {
		    	if (!wi.getPackageName().equals(getPackageName()))
		    	{
					String str = WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER;
					Intent intent = new Intent(str);
					Toast.makeText(this, "Please Select \"SnowBoard\"", Toast.LENGTH_SHORT).show();
					startActivity(intent);
					finish();
		    	}
		    	else
		    	{
		    		
		    		String str = getPackageName();
		    		try {
		    			Intent intent = new Intent(launcher.this,SnowBoardPrefer.class);

						startActivity(intent);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		
		    	}
		    }
		}
		catch(Exception e){
			
		}
		finish();
	}

}
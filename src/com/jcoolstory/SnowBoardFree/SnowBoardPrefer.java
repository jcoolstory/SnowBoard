package com.jcoolstory.SnowBoardFree;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.jcoolstory.SnowBoard.Engin.SnowBoard;

import com.jcoolstory.main.ColorDialog;
import com.jcoolstory.main.SeekDialog;

public class SnowBoardPrefer extends PreferenceActivity implements
		OnSharedPreferenceChangeListener, ColorDialog.OnClickListener, SeekDialog.OnClickListener
		 {
	public static final int CHANGED = 5;
	public static final int NO_CHANGED = 4;
	public static final String STORE = "GOOGLE";
	public static final String GOOGLE_PRO_LINK = "market://details?id=com.jcoolstory.SnowBoard";
	private static final String MY_AD_UNIT_ID = "a14ee8956a03def";
	public String lastImagePref = "1";
	private AdView adView;
	private RelativeLayout linear;
	private String TSTORE_PRO_LINK;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// TODO Auto-generated method stub
		getPreferenceManager().setSharedPreferencesName(
				SnowBoard.SHARED_PREFS_NAME);
		// getPreferenceManager().

		addPreferencesFromResource(R.xml.snow_prefer);
		
		getPreferenceManager().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);	

		linear = (RelativeLayout)View.inflate(this, R.layout.banner  , null );
		
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		addContentView(linear,params  );

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (adView == null)
		{
			AdRequest ar = null;
			
			adView = new AdView(this, AdSize.BANNER, MY_AD_UNIT_ID);
			
			adView.setAdListener(admobListener);
			linear.addView(adView);
			
			ar = new AdRequest();		     
			
			adView.loadAd(ar);
		}
		
	}

	@Override
	protected void onDestroy() {
		getPreferenceManager().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
		super.onDestroy();
		if (adView != null)
		{
			adView.destroy();
		}
		
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		// TODO Auto-generated method stub
		SharedPreferences sp = getPreferenceManager().getSharedPreferences();
		if ("flake_size_setting".equals(preference.getKey())) {
			int value = sp.getInt("flake_size_setting", 5);
			new SeekDialog(this,"flake_size_setting",value,1,10,R.string.flake_size_title,this).show();
		}
		if ("frame_setting".equals(preference.getKey())) {
			int value = sp.getInt("frame_setting", 20);
			new SeekDialog(this,"frame_setting",value,5,40,R.string.frame_setting_title,this).show();
		}
				
		if ("snowflakecolor".equals(preference.getKey())) {

			View view1 = findViewById(R.id.snowflakecolor);
			int snowflakecolor = sp.getInt("snowflakecolor", Color.WHITE);
			new ColorDialog(this, view1,snowflakecolor, this).show();
		}
		if ("snowgroundcolor".equals(preference.getKey())) {

			View view2 = findViewById(R.id.snowgroundcolor);
			int snowgroundcolor = sp.getInt("snowgroundcolor", Color.WHITE);
			new ColorDialog(this, view2,snowgroundcolor, this).show();
		}
		if ("finger_thickness_setting".equals(preference.getKey())) {
			int value = sp.getInt("finger_thickness_setting", 13);
			new SeekDialog(this,"finger_thickness_setting",value,1,40,R.string.finger_thickness_setting,this).show();
		}
		if ("back_bit_setting".equals(preference.getKey()))
		{
			
			SharedPreferences sharedPreferences = getSharedPreferences(SnowBoard.SHARED_PREFS_NAME, MODE_PRIVATE);
			lastImagePref = sharedPreferences.getString("back_bit_setting", "1");
			
		}
		if ("include_image_setting".equals(preference.getKey()))
		{
			Intent intent = new Intent();
			intent.setClass(SnowBoardPrefer.this, SetImageActivity.class);
			startActivityForResult(intent, 5);
		}
		if ("gotomarket".equals(preference.getKey()))
		{
			String link = GOOGLE_PRO_LINK;
			if ("TSTORE".equals(STORE))
			{
				link = TSTORE_PRO_LINK;
			}
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
			try
			{
				startActivity(intent);
			}catch (ActivityNotFoundException e) {
				// TODO: handle exception
				
			}
			
			
		}
		if ("sendmail".equals(preference.getKey())) {
			Uri uri = Uri.parse("mailto:jcoolstory@gmail.com");

			String str = getAppInfor(this);
			
			Intent it = new Intent(Intent.ACTION_SENDTO, uri);
			it.putExtra(Intent.EXTRA_TEXT, str);
			it.putExtra(Intent.EXTRA_SUBJECT, "feedback snowboard");
			startActivity(it);
		}
		if ("savepicture".equals(preference.getKey())) {

			SharedPreferences.Editor editor = getSharedPreferences(
					SnowBoard.SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit();
			editor.putString("savepicture", "1");
			boolean result = editor.commit();
			if (result)
			{
				
				SharedPreferences pr = getPreferenceManager()
						.getSharedPreferences();
				String path = pr.getString("CapturePath", null);
				if (path != null || !"-1".equals(path)) {
					Intent sharingIntent = new Intent(
							android.content.Intent.ACTION_SEND);
	
					File file = new File(path);
					Uri uri = Uri.fromFile(file);
					sharingIntent.setType("image/*");
					sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
	
					startActivity(Intent.createChooser(sharingIntent, "Share"));
	
				}
			}

		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
	public static String getAppInfor(Context context) {
		StringBuilder sb = new StringBuilder();
		try {
			Locale systemLocale = context.getResources().getConfiguration().locale;

			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);

			sb.append("\n\n\n\n--------------------\nBuild.VERSION.SDK : ");
			sb.append(Build.VERSION.RELEASE);
			sb.append("\nApp name : ");
			sb.append(context.getApplicationInfo().loadLabel(context.getPackageManager()));
			sb.append("\nversionCode : ");
			sb.append(pi.versionCode);
			sb.append("\nversionName : ");
			sb.append(pi.versionName);
			sb.append("\nLocale : ");
			sb.append(systemLocale.getLanguage());
		} catch (NameNotFoundException e) {

		}
		return sb.toString();
	}
	public void onClick(View view, int color) {

		view.setBackgroundColor(color);
		if (R.id.snowflakecolor == view.getId())
		{
			SharedPreferences.Editor editor = getSharedPreferences(
					SnowBoard.SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit();
			editor.putInt("snowflakecolor", color);
			editor.commit();
		}
		if (R.id.snowgroundcolor == view.getId())
		{
			SharedPreferences.Editor editor = getSharedPreferences(
					SnowBoard.SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit();
			editor.putInt("snowgroundcolor", color);
			editor.commit();
		}
	}

	public void OnSave(View v) {
		SharedPreferences.Editor editor = getSharedPreferences(
				SnowBoard.SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit();
		editor.putBoolean("savepicture", true);
		editor.commit();
	}

	public void onClick(String key,int value) {
		// TODO Auto-generated method stub
		SharedPreferences.Editor editor = getSharedPreferences(
				SnowBoard.SHARED_PREFS_NAME, Context.MODE_WORLD_WRITEABLE).edit();
		if ("frame_setting".equals(key))
		{
			editor.putInt("frame_setting", value);
			editor.commit();
		}
		if ("flake_size_setting".equals(key))
		{
			editor.putInt("flake_size_setting", value);
			
			editor.commit();
		}
		if ("finger_thickness_setting".equals(key))
		{
			editor.putInt("finger_thickness_setting", value);
			
			editor.commit();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		SharedPreferences.Editor editor = getSharedPreferences(
				SnowBoard.SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit();

		switch (resultCode) {
		case 0:
		case 1:
		case NO_CHANGED:
			break;
		case CHANGED:
			editor.putString("back_bit_setting", "-1");
			editor.commit();
			editor.putString("back_bit_setting","0");
			
			editor.commit();			
			break;
		default:
			break;
		
		}
		
	}
	public void filecopy(String src, String dest)
	{
		InputStream in;
		try {
			in = new FileInputStream(src);

		   OutputStream out = new FileOutputStream(dest);
		   
		   byte[] buf = new byte[1024];
		   int len;
		   while((len = in.read(buf)) > 0){
		    out.write(buf,0,len);
		   }
		   in.close();
		   out.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	AdListener admobListener = new AdListener() {
		
		public void onReceiveAd(Ad arg0) {
			// TODO Auto-generated method stub
			
		}
		
		public void onPresentScreen(Ad arg0) {
			// TODO Auto-generated method stub
			
		}
		
		public void onLeaveApplication(Ad arg0) {
			// TODO Auto-generated method stub
			//Log.d("TAG", "onLeaveApplication");
		}
		
		public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
			// TODO Auto-generated method stub
			
		}
		
		public void onDismissScreen(Ad arg0) {
			// TODO Auto-generated method stub
			
		}
	};
}


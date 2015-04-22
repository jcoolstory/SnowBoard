package com.jcoolstory.SnowBoardPrefer;

import com.jcoolstory.SnowBoard.Engin.SnowBoard;
import com.jcoolstory.SnowBoardFree.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

public class ColorBox extends View {

	public ColorBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub

		init();
	}

	public ColorBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

		init();
	}

	public ColorBox(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		init();
	}
	public void init()
	{
		String str = null;
		switch (getId()) {
		case R.id.snowflakecolor:
			str ="snowflakecolor";
			break;
		case R.id.snowgroundcolor:
			str ="snowgroundcolor";
			break;
		default:
			break;
		};
		if (str == null)
			return;
		SharedPreferences sp =  getContext().getSharedPreferences(
				SnowBoard.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
				
				int snowflakecolor = sp.getInt(str, Color.WHITE);
				setBackgroundColor(snowflakecolor);
		
	}
}

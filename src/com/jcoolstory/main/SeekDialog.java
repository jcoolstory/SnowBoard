package com.jcoolstory.main;

import com.jcoolstory.SnowBoardFree.R;
import com.jcoolstory.main.ColorDialog.OnClickListener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SeekDialog extends AlertDialog implements OnSeekBarChangeListener, android.content.DialogInterface.OnClickListener{
	  public interface OnClickListener {
	        public void onClick(String key,int value);
	    }
	  
	private SeekBar mSeek;
	private SeekDialog.OnClickListener mListener;
	private int mValue;
	private Context mContext;
	private TextView mTextView;
	private String mKey;
	private int mMin ;
	public SeekDialog(Context context,String key,int value, int min, int max,int titleid,OnClickListener listener) {
		super(context);
		
		// TODO Auto-generated constructor stub
		mKey = key;
		mContext = context;
		mListener = listener;
		mValue = value;
		mMin = min;
		Resources res = context.getResources();
		setTitle(res.getString(titleid));
		setButton(BUTTON_POSITIVE, res.getText(android.R.string.yes), this);
		setButton(BUTTON_NEGATIVE, res.getText(android.R.string.cancel), this);
		View root = LayoutInflater.from(context).inflate(R.layout.frameseekbar, null);
		setView(root);
		mSeek = (SeekBar) root.findViewById(R.id.seekBar1);
		mTextView = (TextView) root.findViewById(R.id.preview);
		mSeek.setMax(max-min);
		mSeek.setProgress(value-min);
		mSeek.setOnSeekBarChangeListener(this);
		update();
	}

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		update();
	}

	private void update() {
		// TODO Auto-generated method stub
		mValue = mSeek.getProgress() + mMin;
		mTextView.setText(String.valueOf(mValue));
		
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		if (which == DialogInterface.BUTTON_POSITIVE) {
			mListener.onClick(mKey,mValue);
		}
		dismiss();
		
	}

}

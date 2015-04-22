package com.jcoolstory.SnowBoard.Engin;

import java.util.Random;


import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

class World {
	public Rect DisplayRect;
	float angle = 0;
	public float centerX;
	public float centerY;
	public float Height;
	public float Width;
}

public class MyWorld  extends World
{
	public static final int ANGLE_ZERO = 0;
	public static final int ANGLE_RIGHT =90;
	public static final int ANGLE_LEFT = -90;
	public float VelocityX = 100;
	public Rect DisplayRect;
	public Rect BackLayerRect;
	public Matrix DisplayMatrix;
	public RectF BaseRectF = null;
	public float wind = 0;
	private Random random = new Random();
	boolean mRotating;
	float RotateDst = 0;
	float angle = 0;
	boolean mRotate;
	public long windFreq = 5000;
	private long windLast=0;
	public float wind_max = 10;
	public float wind_changer=5f;
	public MyWorld()
	{
		DisplayMatrix = new Matrix();
	}
	public void rotatingStart(int angle) {
	// TODO Auto-generated method stub
		RotateDst = angle;
		if (angle == ANGLE_ZERO)
			mRotate = false;
		else
			mRotate = true;
		mRotating =true;
		VelocityX = 0;
	}
	public void windGenerate()
	{
		if (windFreq == 0)
			return;
		if (System.currentTimeMillis() - windLast  > windFreq)
		{
			if (random.nextFloat() < 0.1f)
			{
				
				wind *= 0.5f;
				
			}
			else
			{
				wind += (random.nextFloat() - 0.5f) * wind_changer;
				
			}
			if (Math.abs(wind) > wind_max)
			{
				wind = wind_max;
			}
			windLast = System.currentTimeMillis();
		}
			
	}
	public void init(int w, int h) {
		// TODO Auto-generated method stub
		BaseRectF = new RectF(0, 0, w, h);

		DisplayRect = new Rect(0, 0, w, h);

		BackLayerRect = new Rect(0, 0, w, h);
		centerX = BaseRectF.centerX();
		centerY = BaseRectF.centerY();
		Height = BaseRectF.height();
		Width = BaseRectF.width();

	}
	public boolean isRotating()
	{
		if (mRotate || mRotating)
			return true;
		return false;
	}
	public void velcotiyStep()
	{
		if (Math.abs(VelocityX) > 1.2f) {
			VelocityX *= 0.98;
		}
	}
	public void viewanglestep()
	{
		if (mRotating) {
			float gap = RotateDst - angle;
			if (Math.abs(gap) < 5f) {
				angle = RotateDst;
				mRotating = false;
				
			} else {
				gap = gap / 7.0f;
				angle += gap;
			}
			DisplayMatrix.setRotate(angle, centerX, centerY);
			
			float angleratio = angle / 90;
			float sizeraito = Height /  Width;
			sizeraito = 1 + (sizeraito - 1f) * Math.abs(angleratio);
			DisplayMatrix.preScale(sizeraito, sizeraito, centerX, centerY);
		}
		
		
	}
}
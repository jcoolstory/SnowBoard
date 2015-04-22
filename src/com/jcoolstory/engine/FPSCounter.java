package com.jcoolstory.engine;

import android.util.Log;

public class FPSCounter {
    long startTime = System.nanoTime();
    int frames = 0;
    int lastframe=0;
    public boolean logFrame() {
        frames++;
        if(System.nanoTime() - startTime >= 1000000000) {
         //   Log.d("FPSCounter", "fps: " + frames);
            lastframe = frames;
            frames = 0;
            startTime = System.nanoTime();
            
            
            return true;
        }
        return false;
    }

	public int getFrame() {
		// TODO Auto-generated method stub
		return lastframe;
	}
}

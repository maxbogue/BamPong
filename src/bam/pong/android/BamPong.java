package bam.pong.android;

//Author:Aniket

//import bam.pong.Engine;

import android.app.Activity;


import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;

import android.os.Bundle;
import android.util.DisplayMetrics;

public class BamPong extends Activity implements  OnKeyListener, OnTouchListener , OnClickListener {
	// private Engine En;

	private GameField gf;

	public void onCreate(Bundle state)
	{
		super.onCreate(state);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		DisplayMetrics display = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(display);

		//Height of display in pixels
		int ht = display.heightPixels;

		//Width of Display in pixels
		int wdt = display.widthPixels;

		//En=new Engine(Width,Height);        

		//New gamefield object
		gf = new GameField(this, wdt, ht);

		gf.setOnClickListener(this);

		gf.setOnTouchListener(this);

		setContentView(gf);
	}

	public boolean onKey(View v, int Code, KeyEvent e) {
		gf.en.moveLeft();
		setContentView(gf);
		return false;
	}

	public boolean onKeyDown(int Code, KeyEvent e)
	{
		switch(Code) {
		//Left movement of arrow key
		case KeyEvent.KEYCODE_DPAD_LEFT : 
			gf.en.moveLeft();
			break;
			//Right movement of the arrow key
		case KeyEvent.KEYCODE_DPAD_RIGHT : 
			gf.en.moveRight();
			break;
		}

		setContentView(gf);
		return true;
	}

	public boolean onTouch(View v,MotionEvent event)
	{  
		//Do stuff here to make the touch screen work
		return true;
	}

	public void onClick(View v) {
		gf.en.moveLeft();
		setContentView(gf);
	}
}

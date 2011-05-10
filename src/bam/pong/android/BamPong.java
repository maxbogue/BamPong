package bam.pong.android;

//Author:Aniket

//import bam.pong.Engine;

import bam.pong.Ball;
import bam.pong.Engine;
import bam.pong.Paddle;
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

public class BamPong extends Activity implements OnTouchListener {

	/** Diameter of the balls in pixels. */
	public static final int D = 15;
	
	private GameField gf;
	private Engine engine;
	private Paddle paddle;
	
	public void onCreate(Bundle state)
	{
		super.onCreate(state);

		System.err.println("test");
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		DisplayMetrics display = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(display);

		// Width and height of display in pixels.
		int w = display.widthPixels;
		int h = display.heightPixels;
		
		int pw = 80; // Paddle width.
		int ph = 25; // Paddle height.
		
		paddle = new Paddle(pw, ph, 300, w/2 - pw/2, h - ph - 30);
		
		engine = new Engine(w, h, paddle);        

		//New gamefield object
		gf = new GameField(this, engine.getBalls(), paddle);

		gf.setOnTouchListener(this);
		engine.addListener(gf);
		setContentView(gf);
		
		Ball bs[] = {
				new Ball(1, 50, 25, 200, 75, D),
				new Ball(2, 50, 75, -367, 100, D),
				new Ball(3, 25, 25, 283, -200, D),
				new Ball(4, 25, 25, 193, -100, D),
				new Ball(5, 25, 25, 90, -75, D),
			};
		for( Ball b : bs ) engine.addBall(b);
		engine.start();
	}

	public boolean onKeyDown(int k, KeyEvent e) {
		switch(k) {
		case KeyEvent.KEYCODE_DPAD_LEFT: 
			paddle.setMovement(Paddle.Movement.LEFT);
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT: 
			paddle.setMovement(Paddle.Movement.RIGHT);
			break;
		}
		return true;
	}

	public boolean onKeyUp(int k, KeyEvent e) {
		switch(k) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
		case KeyEvent.KEYCODE_DPAD_RIGHT: 
			paddle.setMovement(Paddle.Movement.NONE);
			break;
		}
		return true;
	}

	public boolean onTouch(View v,MotionEvent event) {  
		if (event.getAction() == MotionEvent.ACTION_UP) {
			paddle.setMovement(Paddle.Movement.NONE);
		} else {
			int x = (int) event.getX();
			if (x < paddle.x + paddle.w / 2) {
				paddle.setMovement(Paddle.Movement.LEFT);
			} else {
				paddle.setMovement(Paddle.Movement.RIGHT);
			}
		}
		return true;
	}


	public void onClick(View v) {
//		gf.en.moveLeft();
//		setContentView(gf);
	}

}

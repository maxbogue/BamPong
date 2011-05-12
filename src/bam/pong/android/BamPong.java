package bam.pong.android;

//Author:Aniket

//import bam.pong.Engine;

import bam.pong.Ball;
import bam.pong.Client;
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

		Client client = GUI.client;
		engine = client.engine;

		//New gamefield object
		gf = new GameField(this, engine.getBalls(), engine.getPaddle());

		gf.setOnTouchListener(this);
		engine.addListener(gf);
		setContentView(gf);
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

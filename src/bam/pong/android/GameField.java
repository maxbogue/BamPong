package bam.pong.android;


import android.content.Context;


import android.graphics.Canvas;
import android.view.View;


public class GameField extends View implements Runnable {

	// public Paddle paddle;

	// private Ball ball;
	public Engine en;   

	private int Width, Height;


	public GameField(Context context, int wdt, int ht) {
		super(context);

		this.Width = wdt;
		this.Height = ht;

		en=new Engine(context, Width, Height);      

		// paddle = new Paddle(context, Width, Height);


		//ball = new Ball(context, paddle, Width, Height);


		Thread t = new Thread(this);
		t.start();

	}

	protected void onDraw(Canvas canvas)

	{
		this.postInvalidate();


		//paddle.draw(canvas);


		//ball.draw(canvas);

		en.draw(canvas);
	}

	public void run() {
		while(true){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			en.move();
			//ball2.move();
		}
	}


}



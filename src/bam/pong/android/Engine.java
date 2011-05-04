package bam.pong.android;

/**
 Engine class for ball and paddle movement
 * @Author Aniket
 *
 */


import bam.pong.android.Ball;
import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

public class Engine extends View
{
	public final static int bouncefactor= 5;
	public final static int SPEED = 5;

	Paddle p;
	//Ball bs[];
	Ball b;
	public Engine(Context context,int wdt,int ht)
	{

		super(context);

		p=new Paddle(context,wdt,ht);

		//bs[0] =new Ball(context,p,wdt,ht,50,25);
		//bs[1]=new Ball(context,p,wdt,ht,50,10);


		b=new Ball(context,p,wdt,ht);



	}


	protected void onDraw(Canvas canvas) {

		//Draw the ball
		//for(Ball b:bs)
		b.shape.draw(canvas);

		//Draw the paddle
		p.shape.draw(canvas);
	}


	//Movement of the ball
	public void move()
	{
		//for(Ball b:bs)
		//{
		if(isOnScreen(b.x+b.speed[0], b.y+b.speed[1]))
		{
			b.x += b.speed[0];
			b.y += b.speed[1];
			b.shape.setBounds(b.x, b.y, b.x + b.diameter, b.y + b.diameter);
		}
		else if(!isOnScreen(b.x+b.speed[0], 1)){
			b.speed[0] = -b.speed[0];
		}
		else if(!isOnScreen(1, b.y+b.speed[1])){
			b.speed[1] = -b.speed[1];
		}

		if(paddleHit(b.x+b.speed[0], b.y+b.speed[1])) {
			b.speed[0] = b.newSpeed;
			b.speed[1] = -b.speed[1];
		}
		// }
	}

	private boolean isOnScreen(int x, int y) {
		//for(Ball b :bs)
		//{
		if(x >= 0 && x <= b.screenWidth-b.diameter && y >= 0 && y <= b.screenHeight-b.diameter-30){
			return true;
		}
		//}
		return false;
	}


	// Function of paddle hit when the ball hits the paddle
	private boolean paddleHit(int x, int y) {
		//for(Ball b:bs)
		//{
		if(x >= p.x - b.diameter && x <= p.x + b.diameter + p.width && y > p.y) {

			//The new speed of the ball when the paddle hits the ball.
			b.newSpeed = (x - (p.x + p.width/2))/bouncefactor;

			return true;
		}
		//}
		return false;
	}


	public void paddleTouched(float x,float y)
	{



		p.x=(int) x;
		p.shape.setBounds(p.x, p.y, p.x + p.width, p.y + p.height);


	}


	//Paddle movemnt to the left
	public void moveLeft()
	{
		if(isOnScreen(p.x-p.speed)){
			p.x-= p.speed;
			p.shape.setBounds(p.x, p.y, p.x + p.width, p.y + p.height);
		}

	}
	//PAddle movement to the right
	public void moveRight()
	{
		if(isOnScreen(p.x+p.speed)){
			p.x+= p.speed;
			p.shape.setBounds(p.x, p.y, p.x + p.width, p.y + p.height);
		}
	}

	private boolean isOnScreen(int x)

	{
		if(x >= 0 && x <= p.screenWidth-p.width){
			return true;
		}
		return false;
	}
}

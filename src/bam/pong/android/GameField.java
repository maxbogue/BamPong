package bam.pong.android;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import bam.pong.Ball;
import bam.pong.EngineListener;
import bam.pong.Paddle;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.view.View;


public class GameField extends View implements EngineListener {
	
	/** Ball diameter. */
	public final int D = 20;
	
	private Map<Ball, ShapeDrawable> balls = new HashMap<Ball, ShapeDrawable>();
	
	private ShapeDrawable paddleShape;
	
	private Paddle p;
	
	public GameField(Context context, Set<Ball> balls, Paddle paddle) {
		super(context);
		for (Ball b : balls) {
			this.balls.put(b, makeBallShape());
		}
		this.p = paddle;
		paddleShape = new ShapeDrawable(new RectShape());
		paddleShape.getPaint().setColor(0xFFFF0000);
	}

	protected void onDraw(Canvas canvas) {
//		postInvalidate();
		for (Ball b : balls.keySet()) {
			balls.get(b).setBounds((int)b.x, (int)b.y, (int)b.x + D, (int)b.y + D);
			balls.get(b).draw(canvas);
		}
		paddleShape.setBounds((int)p.x, p.y, (int)p.x + p.w, p.y + p.h);
		paddleShape.draw(canvas);
	}

	@Override
	public void fieldUpdated() {
		postInvalidate();
	}

	@Override
	public void sendBall(Ball b) {
		balls.remove(b);
	}

	@Override
	public void ballDropped(Ball b) {
		balls.remove(b);
	}

	@Override
	public void ballAdded(Ball b) {
		balls.put(b, makeBallShape());
	}

	private ShapeDrawable makeBallShape() {
		ShapeDrawable shape = new ShapeDrawable(new OvalShape());
		shape.getPaint().setColor(0xFFFFFFFF);
		return shape;
	}

}



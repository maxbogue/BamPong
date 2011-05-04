package bam.pong.android;

/**Ball class to draw the ball
 * This is to draw only one ball,make changes accordingly
 * 
 * @Author Aniket
 */



import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.View;




public class Ball extends View {
        
       
       
        public ShapeDrawable shape;
        public int x, y;
        
        //Diametre of the ball
        public int diameter = 20;
        
        public int speed[];
        public int screenWidth, screenHeight;
        public Paddle paddle;
        
        //New speed of the ball after hitting the paddle
        public int newSpeed;
        
        public Ball(Context context, Paddle paddle, int screenWidth, int screenHeight) {
                super(context);
        
               x = screenWidth/2 - diameter;
                y = screenHeight/2 - diameter;
                //this.x=x;
                //this.y=y;
                speed = new int[2];
                speed[0] = 0;
                speed[1] = 5;
                
                
                this.screenWidth = screenWidth; 
                this.screenHeight = screenHeight;

                this.paddle = paddle;
                
                shape = new ShapeDrawable(new OvalShape());
                shape.getPaint().setColor(0xffFFFFFF);
                shape.setBounds(x, y, x + diameter, y + diameter);
                

                
        }
        
       
    
    
}


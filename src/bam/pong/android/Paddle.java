package bam.pong.android;


/*Paddle class for drawing the paddle
 * /@ author Aniket
 */ 




import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.View;




public class Paddle extends View {
        
    ShapeDrawable shape;
   
    public int x = 5;
    public int y = 5;
   
    //Size of the paddle
    public int width = 80;
    public int height = 25;
    
    //The speed with which the paddle moves left or right
    int speed = 30;
    
   //Screen ht and width
    
    int screenWidth;
	int screenHeight;
    
        public Paddle(Context context, int screenWidth, int screenHeight)
        
        {
                super(context);
                
                this.screenWidth = screenWidth;
                this.screenHeight = screenHeight;
                
                //location of the paddle
                this.x = screenWidth/2 - width/2;
                
                this.y = screenHeight - height - 40;

        shape = new ShapeDrawable(new RectShape());
        
        
        shape.getPaint().setColor(0xffff0000);
        
        
        shape.setBounds(x, y, x + width, y + height);

        }
        
        
        
   

}


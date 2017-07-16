package leesw.LoneAlien;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by leesw on 16/07/2017.
 */

public class Star implements GameObject {
    private Paint starPaint;
    private float cx;
    private float cy;
    private float radius;

    private static int MAX_RADIUS = 25;

    public Star(float f) {
        starPaint = new Paint();
        starPaint.setColor(Color.argb((int)(Math.random()*80)+15,255,255,0));
        cx = (float)(Math.random()*Constants.SCREEN_WIDTH);
        cy = f - (float)(Constants.SCREEN_HEIGHT/-30);
        radius = (float)(Math.random()*MAX_RADIUS);
    }

    public void incrementY(Float y) {
        cy += y;
    }
    public int getY() {
        System.out.println(cy);
        return (int)cy;
    }
    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(cx,cy,radius,starPaint);
    }

    @Override
    public void update() {

    }
}

package leesw.LoneAlien;

import android.graphics.Canvas;

import java.util.ArrayList;

/**
 * Created by leesw on 16/07/2017.
 */

public class StarManager {
    private ArrayList<Star> stars;

    private long startTime;
    private long initTime;

    public StarManager() {
        stars = new ArrayList<>();
        startTime = initTime = System.currentTimeMillis();
        populateStars();
    }

    private void populateStars() {
        int numStars = (int)(Math.random()*100)+50;
        for (int i = 0; i < numStars; i++) {
            stars.add(0, new Star(i*(Constants.SCREEN_HEIGHT/-30)));
        }
    }

    public void draw(Canvas canvas) {
        for (Star s : stars) {
            s.draw(canvas);
        }
    }

    public void update() {
        if (startTime < Constants.INIT_TIME) {
            startTime = Constants.INIT_TIME;
        }
        int elapsedTime = (int)(System.currentTimeMillis() - startTime);
        startTime = System.currentTimeMillis();
        float speed = (float)(Constants.SCREEN_HEIGHT/(10000.0f));
        for (Star s : stars) {
            s.incrementY(speed  * elapsedTime);
        }
        if (stars.get(stars.size() - 1).getY() >= Constants.SCREEN_HEIGHT) {
            stars.remove(stars.size() - 1);
        }
        if (stars.size() <= 50) {
            populateStars();
        }
    }
}

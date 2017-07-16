package leesw.LoneAlien;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;

import java.util.ArrayList;

/**
 * Created by leesw on 07/07/2017.
 */

public class ObstacleManager {
    //higher index = lower on screen = higher y value
    private ArrayList<Obstacle> obstacles;
    private int playerGap;
    private int obstacleGap;
    private int obstacleHeight;

    private MediaPlayer pointSound;
    private SharedPreferences prefs;

    private boolean newHigh;

    private ArrayList<Integer> colors;

    private long startTime;
    private long initTime;

    public int score;

    public ObstacleManager(int playerGap, int obstacleGap, int obstacleHeight, Context context, SharedPreferences prefs) {
        // Color palette for Obstacles
        colors = new ArrayList<>();
        colors.add(Color.BLUE);
        colors.add(Color.CYAN);
        colors.add(Color.RED);
        colors.add(Color.MAGENTA);
        colors.add(Color.YELLOW);
        colors.add(Color.GREEN);

        newHigh = false;
        this.prefs = prefs;
        this.playerGap = playerGap;
        this.obstacleGap = obstacleGap;
        this.obstacleHeight = obstacleHeight;
        obstacles = new ArrayList<>();
        startTime = initTime = System.currentTimeMillis();
        populateObstacles();
        pointSound = MediaPlayer.create(context, R.raw.coin1);
    }

    public boolean playerCollide(RectPlayer player) {
        for (Obstacle ob: obstacles) {
            if (ob.playerCollide(player)) {
                return true;
            }
        }
        return false;
    }

    private void populateObstacles() {
        int currY = -5*Constants.SCREEN_HEIGHT/4;
        while(currY < 0) {
            int xStart = (int) (Math.random()*(Constants.SCREEN_WIDTH - playerGap));
            obstacles.add(new Obstacle(obstacleHeight, xStart, currY, playerGap, colors));
            currY += obstacleHeight + obstacleGap;
        }
        int currX = -5*Constants.SCREEN_WIDTH/4;
    }

    public void update() {
        if (startTime < Constants.INIT_TIME) {
            startTime = Constants.INIT_TIME;
        }
        int elapsedTime = (int)(System.currentTimeMillis() - startTime);
        startTime = System.currentTimeMillis();
        float speed = (float)((Math.sqrt(1 + (startTime - initTime)/5000.0))*Constants.SCREEN_HEIGHT/(10000.0f));
        for (Obstacle ob : obstacles) {
            ob.incrementY(speed  * elapsedTime);
        }
        if (obstacles.get(obstacles.size() - 1).getRectangle().top >=Constants.SCREEN_HEIGHT) {
            int xStart = (int) (Math.random()*(Constants.SCREEN_WIDTH - playerGap));
            int startY = obstacles.get(0).getRectangle().top - obstacleHeight - obstacleGap;
            obstacles.add(0, new Obstacle(obstacleHeight, xStart, startY, playerGap, colors));
            obstacles.remove(obstacles.size() - 1);
            score++;
            int highscore = prefs.getInt("key", 0);
            if (score >= highscore) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("key", score);
                editor.commit();
                newHigh = true;
            }
            if (score > 0 && score % 5 == 0) {
                pointSound.start();
            }
        }
    }

    public void draw(Canvas canvas) {
        for (Obstacle ob : obstacles) {
            ob.draw(canvas);
        }
        Paint paint = new Paint();
        paint.setTextSize(100);
        paint.setColor(Color.WHITE);
        canvas.drawText("" + score, 100, 100 + paint.descent() - paint.ascent(), paint);
        if (newHigh) {
            paint = new Paint();
            paint.setTextSize(50);
            paint.setColor(Color.WHITE);
            canvas.drawText("Highscore!", 30,50+ paint.descent() - paint.ascent(), paint);
        }
    }
}

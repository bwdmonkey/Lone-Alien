package leesw.LoneAlien;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.view.MotionEvent;

import java.io.File;
import java.util.Locale;

/**
 * Created by leesw on 11/07/2017.
 */

public class GameplayScene implements Scene {
    private Rect r = new Rect();

    private RectPlayer player;
    private Point playerPoint;
    private Point clickPoint;
    private ObstacleManager obstacleManager;

    private Context context;

    private StarManager starManager;

    private long frameTime;

    private boolean movingPlayer = false;

    private boolean gameOver = false;
    private long gameOverTime;

    private SharedPreferences prefs;

    public GameplayScene(Context aContext) {
        player = new RectPlayer(new Rect(100,100,200,200), Color.rgb(255,0,0));
        playerPoint = new Point(Constants.SCREEN_WIDTH/2,Constants.SCREEN_HEIGHT/4*3);
        clickPoint = new Point(Constants.SCREEN_WIDTH/2,Constants.SCREEN_HEIGHT/4*3);
        int obstacleGap = Constants.SCREEN_WIDTH/4;
        player.update(playerPoint);

        context = aContext;
        prefs = context.getSharedPreferences("highscore", context.MODE_PRIVATE);
        obstacleManager = new ObstacleManager(200, obstacleGap, 75, context, prefs);

        starManager = new StarManager();
        frameTime = System.currentTimeMillis();
    }
    @Override
    public void update() {
        if (!gameOver) {
            if (frameTime < Constants.INIT_TIME) {
                frameTime = Constants.INIT_TIME;
            }
            frameTime = System.currentTimeMillis();
            if (playerPoint.x < 0) {
                playerPoint.x = 0;
            } else if (playerPoint.x > Constants.SCREEN_WIDTH) {
                playerPoint.x = Constants.SCREEN_WIDTH;
            } else if (playerPoint.y < 0) {
                playerPoint.y = 0;
            } else if (playerPoint.y > Constants.SCREEN_HEIGHT) {
                playerPoint.y = Constants.SCREEN_HEIGHT;
            }

            player.update(playerPoint);
            obstacleManager.update();
            starManager.update();

            if (obstacleManager.playerCollide(player)) {
                gameOver = true;
                gameOverTime = System.currentTimeMillis();
                stopSong();
                MediaPlayer deathSound = MediaPlayer.create(context, R.raw.explosion);
                deathSound.start();
                if (!deathSound.isPlaying()) {
                    deathSound.release();
                }

            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(Color.DKGRAY);
        player.draw(canvas);
        starManager.draw(canvas);
        obstacleManager.draw(canvas);

        if (gameOver) {
            //Backboard
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            drawRectangle(canvas, paint, 1f);
            paint.setColor(Color.CYAN);
            drawRectangle(canvas, paint, 0.9f);
            //Texts
            AssetManager am = context.getApplicationContext().getAssets();
            Typeface face = Typeface.createFromAsset(am,"fonts/font.ttf");
            paint.setTextSize(Constants.SCREEN_HEIGHT/10);
            paint.setColor(Color.rgb(255,20,147));
            paint.setTypeface(face);
            int highScore = prefs.getInt("key", 0);
            drawCenterScore(canvas, paint, "Score: " + obstacleManager.getScore());
            drawCenterText(canvas, paint, "Game Over");
            drawCenterHS(canvas, paint, "Highscore: "+highScore);
        }
    }

    @Override
    public void terminate() {
        SceneManager.ACTIVE_SCENE = 0;
    }

    @Override
    public void receiveTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!gameOver) {
                    clickPoint.set((int)event.getX(), (int)event.getY());
                    movingPlayer = true;
                }
                if (gameOver && System.currentTimeMillis() - gameOverTime >= 500) {
                    reset();
                    gameOver = false;
                    playBG();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (movingPlayer && !gameOver) {
                    int xMoved = (int)(event.getX() - clickPoint.x);
                    int yMoved = (int)(event.getY() - clickPoint.y);
                    playerPoint.set(playerPoint.x + xMoved,playerPoint.y + yMoved);
                    clickPoint.set((int)event.getX(), (int)event.getY());
                }
                break;
            case MotionEvent.ACTION_UP:
                movingPlayer = false;
                break;
        }
    }

    public void reset() {
        playerPoint = new Point(Constants.SCREEN_WIDTH/2,Constants.SCREEN_HEIGHT/4*3);
        player.update(playerPoint);
        int obstacleGap = Constants.SCREEN_WIDTH/4;
        obstacleManager = new ObstacleManager(200, obstacleGap, 75, context, prefs);
        movingPlayer = false;
    }

    private void drawCenterScore(Canvas canvas, Paint paint, String text) {
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom - (float)(paint.getTextSize() * 1.1);
        canvas.drawText(text, x, y, paint);
    }
    private void drawCenterText(Canvas canvas, Paint paint, String text) {
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom;
        canvas.drawText(text, x, y, paint);
    }
    private void drawCenterHS(Canvas canvas, Paint paint, String text) {
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom + (float)(paint.getTextSize() * 1.1);
        canvas.drawText(text, x, y, paint);
    }
    private void drawRectangle(Canvas canvas, Paint paint, float scale) {
        int cHeight = Constants.SCREEN_HEIGHT;
        int cWidth = Constants.SCREEN_WIDTH;
        Point center = new Point(cWidth/2, cHeight/2);
        int rectW = (int)(cWidth/4*3*scale);
        int rectH = (int)(cHeight/3*scale);
        int left = center.x - (rectW / 2);
        int top = center.y - (rectH / 2);
        int right = center.x + (rectW / 2);
        int bottom = center.y + (rectH / 2);
        Rect rect = new Rect(left, top, right, bottom);
        canvas.drawRect(rect,paint);
    }

    private void playBG() {
        AudioPlay.playAudio(context, R.raw.closemymouth);
    }
    private void stopSong() {
        AudioPlay.stopAudio();
    }
}

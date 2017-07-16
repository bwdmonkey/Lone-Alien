package leesw.LoneAlien;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.view.MotionEvent;

/**
 * Created by leesw on 11/07/2017.
 */

public class GameplayScene implements Scene{

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

    private MediaPlayer bgSong;

    private SharedPreferences prefs;

    public GameplayScene(Context aContext) {
        player = new RectPlayer(new Rect(100,100,200,200), Color.rgb(255,0,0));
        playerPoint = new Point(Constants.SCREEN_WIDTH/2,Constants.SCREEN_HEIGHT/4*3);
        clickPoint = new Point(Constants.SCREEN_WIDTH/2,Constants.SCREEN_HEIGHT/4*3);
        player.update(playerPoint);

        context = aContext;
        prefs = context.getSharedPreferences("myPrefsKey", context.MODE_PRIVATE);
        obstacleManager = new ObstacleManager(200, 350, 75, context, prefs);

        starManager = new StarManager();

        bgSong = MediaPlayer.create(context, R.raw.closemymouth);
        bgSong.setLooping(true);
        bgSong.start();
        frameTime = System.currentTimeMillis();
    }
    @Override
    public void update() {
        if (!gameOver) {
            if (frameTime < Constants.INIT_TIME) {
                frameTime = Constants.INIT_TIME;
            }

            int elapsedTime = (int)(System.currentTimeMillis() - frameTime);
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
                MediaPlayer deathSound = MediaPlayer.create(context, R.raw.explosion);
                deathSound.start();
                bgSong.pause();
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
            Paint paint = new Paint();
            paint.setTextSize(100);
            paint.setColor(Color.LTGRAY);
            int highScore = prefs.getInt("key", 0);
            drawCenterText(canvas, paint, "Game Over");
            drawCenterScore(canvas, paint, "High: "+highScore);
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
                    bgSong = MediaPlayer.create(context, R.raw.closemymouth);
                    bgSong.setLooping(true);
                    bgSong.start();
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

        obstacleManager = new ObstacleManager(200, 350, 75, context, prefs);
        movingPlayer = false;
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
    private void drawCenterScore(Canvas canvas, Paint paint, String text) {
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom + (float)(paint.getTextSize() * 1.2);
        canvas.drawText(text, x, y, paint);
    }
}

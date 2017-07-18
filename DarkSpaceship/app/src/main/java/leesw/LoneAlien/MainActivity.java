package leesw.LoneAlien;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
    GamePanel gamepanel;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constants.SCREEN_WIDTH = dm.widthPixels;
        Constants.SCREEN_HEIGHT = dm.heightPixels;

        gamepanel = new GamePanel(this);
        setContentView(gamepanel);
    }

    @Override
    protected void onPause() {
        AudioPlay.stopAudio();
        gamepanel.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        AudioPlay.playAudio(this, R.raw.closemymouth);
        gamepanel = new GamePanel(this);
        setContentView(gamepanel);
        super.onResume();
    }
}
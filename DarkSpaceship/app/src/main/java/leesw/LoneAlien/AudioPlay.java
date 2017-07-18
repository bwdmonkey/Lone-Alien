package leesw.LoneAlien;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

/**
 * Created by leesw on 18/07/2017.
 */

public class AudioPlay {
    public static MediaPlayer mediaPlayer;
    public static boolean isPlayingAudio=false;
    public static void playAudio(Context c, int id){
        mediaPlayer = MediaPlayer.create(c,id);
        mediaPlayer.setLooping(true);
        if(!mediaPlayer.isPlaying())
        {
            isPlayingAudio=true;
            mediaPlayer.start();
        }
    }
    public static void stopAudio(){
        isPlayingAudio=false;
        mediaPlayer.release();
    }
}

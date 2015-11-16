package itworx.com.e_inspector;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import java.io.IOException;

/**
 * Created by karim on 11/5/2015.
 */
public class PlayAudioService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public PlayAudioService() {
    }

    boolean isPLAYING;
    MediaPlayer mp = new MediaPlayer();

    public void playAudio(String audioFile) {
        if (!isPLAYING) {
            isPLAYING = true;
            try {
                mp.setDataSource(audioFile);
                mp.prepare();
                mp.start();
            } catch (IOException e) {
                Log.e("", "prepare() failed:" + e.getMessage());
            }
        } else {
            isPLAYING = false;
            stopPlaying();
        }
    }

    public void stopAudio() {
        stopPlaying();
    }

    private void stopPlaying() {
        if (mp != null) {
            mp.stop();
            mp.release();
        }
    }


}

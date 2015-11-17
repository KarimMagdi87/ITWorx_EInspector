package itworx.com.e_inspector;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.EventLogTags;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

public class CaseDetailsActivity extends Activity {

    ImageView imageView;
    TextView etDesc;
    String audioFile;
    Button btnPlay, btnStop;
    public static final String TITLE = "title";
    public static final String PublishContent = "PublishContent";
    public static final String DOESLIKE = "DOESLIKE";
    public static final String LIKES = "LIKES";
    public static final String IMAGE = "image";
    public static final String Location = "location";
    public static final String DATE = "date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_details);
        imageView = (ImageView) findViewById(R.id.imageView);
        btnPlay = (Button) findViewById(R.id.buttonPlay);
        btnStop = (Button) findViewById(R.id.buttonStop);

        etDesc = (TextView) findViewById(R.id.tv_news_alldetails);
       TextView tv_title =  (TextView) findViewById(R.id.tv_news_title);

        Intent intent = getIntent();
        if (intent != null) {
            String image_uri = intent.getStringExtra(IMAGE);
            String desc = intent.getStringExtra(PublishContent);
            String lat = intent.getStringExtra("lat");
            String lon = intent.getStringExtra("lon");
            audioFile = intent.getStringExtra("audio");
            String title  = intent.getStringExtra(TITLE);

            if (audioFile == null || audioFile.equals("")) {
                btnPlay.setEnabled(false);
                btnStop.setEnabled(false);
            } else {
                btnPlay.setEnabled(true);
                btnStop.setEnabled(false);
            }


            if (image_uri != null)
                Picasso.with(this)
                        .load(image_uri)
                        .placeholder(R.drawable.picasedefault)
                        .error(R.drawable.picasedefault)
                        .into(imageView);
            if (desc != null)
                etDesc.setText(desc);
            if(title!=null)
                tv_title.setText(title);
        }

    }

    public void update(View view){finish();}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_case_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    boolean isPLAYING;
    MediaPlayer mp = new MediaPlayer();

    public void playAudio(View v) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (!isPLAYING) {
                    isPLAYING = true;
                    try {
                        Log.d("", "audiofile:" + audioFile);
                        mp = new MediaPlayer();
                        mp.setDataSource(audioFile);
                        mp.prepare();
                        mp.start();
                    } catch (IOException e) {
                        Log.e("", "prepare() failed:" + e.getMessage());
                    }
                } else {
                    isPLAYING = false;
                    try {
                        stopPlaying();
                    } catch (Exception ex) {
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                btnStop.setEnabled(true);
                btnPlay.setEnabled(false);
            }
        }.execute();
    }

    public void stopAudio(View v) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                stopPlaying();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                btnStop.setEnabled(false);
                btnPlay.setEnabled(true);
            }
        }.execute();


    }

    private void stopPlaying() {
        if (mp != null) {
            try {
                isPLAYING = false;
                mp.stop();

                mp.reset();
                mp.release();
            } catch (Exception ex) {
            }
        }
    }
}

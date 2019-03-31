package nupa.estudioplaya;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.VideoView;
import android.os.Handler;
public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash_screen);
        setContentView(R.layout.estudio_playa_splash);

        //Inicio el servicio
        final Intent i = new Intent(this, StreamClass.class);
        startService(i);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, Movistar_Activity.class));
                finish();
            }
        },SPLASH_TIME_OUT);


      /*  try{

            VideoView videoHolder=(VideoView)findViewById(R.id.videoView2);

            Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
                    + R.raw.splash);
            videoHolder.setVideoURI(video);

            videoHolder.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer mp) {
                    jump();
                }

            });

            videoHolder.start();

        } catch(Exception ex) {
            jump();
        }*/
    }


    private void jump() {
//it is safe to use this code even if you
//do not intend to allow users to skip the splash
        if(isFinishing())
            return;
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
      //  jump();
        return super.onTouchEvent(event);
    }

}

package nupa.estudioplaya;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Image;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import nupa.estudioplaya.StreamClass.MyBinder;
import java.io.IOException;

public class MainActivity extends AppCompatActivity /* extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener*/ {

    StreamClass streamClass;
    ImageButton btnPlayStop, btnFacebook, btnTwiiter, btnInstagram,btnNupa;
    Switch videoSwitch;
    CustomPageAdapter mCustomPagerAdapter;
    ViewPager mViewPager;
    TextView cargando;
    public static String FACEBOOK_URL = "https://www.facebook.com/estudioplaya/ ";
    public static String FACEBOOK_PAGE_ID = "205248466232030";
    ImageView estudioLogo;


    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    private static final String ACTION_STRING_ACTIVITY = "ToActivity";

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            super.onBackPressed();
            stopService(new Intent(this,StreamClass.class));
            unbindService(mServiceConnection);
            return;
        }
        else { Toast.makeText(getBaseContext(), "Presione nuevamente para salir", Toast.LENGTH_SHORT).show(); }

        mBackPressed = System.currentTimeMillis();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_final);

        cargando=(TextView)findViewById(R.id.txtCargando);
        estudioLogo=(ImageView)findViewById(R.id.imgEstudioLogo);

        if(serviceMPReceiver !=null){
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_ACTIVITY);
            registerReceiver(serviceMPReceiver, intentFilter);
        }

        if(closeReceiver != null) {
            IntentFilter intenFilterClose=new IntentFilter("com.nupa.estudioplaya.finish");
            registerReceiver(closeReceiver, intenFilterClose);
        }

        if (playReceiver!=null){
            IntentFilter intentFilterPlay = new IntentFilter("com.nupa.estudioplaya.play");
            registerReceiver(playReceiver,intentFilterPlay);
        }

        if (stopReceiver!=null){
            IntentFilter intentFilterStop = new IntentFilter("com.nupa.estudioplaya.Stop");
            registerReceiver(stopReceiver,intentFilterStop);
        }

        btnPlayStop=(ImageButton)findViewById(R.id.imgPlayStop);
        btnPlayStop.setEnabled(false);

        //Bindeo el servicio con mi actividad
        final Intent i = new Intent(this, StreamClass.class);
        bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);

        //Listeners de play, Stop y video
        btnPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(streamClass.isPlaying()){
                    streamClass.StopRadio();
                    changeIconsToBlack();
                }else{

                    streamClass.StartRadio();
                    btnPlayStop.setEnabled(false);
                    cargando.setVisibility(View.VISIBLE);
                    changeIconsToWhite();
                }

            }
        });

       /* videoSwitch=(Switch)findViewById(R.id.switchVideo);
        videoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if(isChecked){
                    streamClass.StopRadio();

                    Intent youTube = new Intent(getApplicationContext(),YouTube_Fragment_Activity.class);
                    startActivity(youTube);
                    videoSwitch.setChecked(false);

                }else
                {

                }

            }
        });*/

        mCustomPagerAdapter = new CustomPageAdapter(this);
        mViewPager=(ViewPager)findViewById(R.id.pager);
        mViewPager.setAdapter(mCustomPagerAdapter);


        //Abro Facebook de Estudio Playa
        btnFacebook=(ImageButton)findViewById(R.id.imgFacebook);
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewFacebook=new Intent(Intent.ACTION_VIEW);
                String facebookURL=getFacebookPageURL(getApplicationContext());
                viewFacebook.setData(Uri.parse(facebookURL));
                startActivity(viewFacebook);
            }
        });


        //Abro Twitter de estudio Playa
        btnTwiiter=(ImageButton)findViewById(R.id.imgTwitter);
        btnTwiiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=450761327")));

                }catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/EstudioPLaya")));
                }
            }
        });


        btnInstagram=(ImageButton)findViewById(R.id.imgInstagram);
        btnInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/estudioplaya/")));

                }catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/estudioplaya/")));
                }
            }

        });

        btnNupa=(ImageButton)findViewById(R.id.imgNupa);
        btnNupa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://nupa.com.ar")));
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(serviceMPReceiver);
        unregisterReceiver(closeReceiver);
        stopService(new Intent(this,StreamClass.class));
        //unbindService(mServiceConnection);
        streamClass.cancelNotification();
    }

    public String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

     ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyBinder myBinder =(MyBinder)service;
            streamClass=myBinder.getServerInstance();
            if (streamClass.isPlaying()){
                changeIconsToWhite();
                cargando.setVisibility(View.INVISIBLE);
                btnPlayStop.setEnabled(true);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private BroadcastReceiver serviceMPReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            cargando.setVisibility(View.INVISIBLE);
            btnPlayStop.setEnabled(true);
            changeIconsToWhite();
        }
    };

    private BroadcastReceiver closeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    private BroadcastReceiver playReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            btnPlayStop.setEnabled(false);
            cargando.setVisibility(View.VISIBLE);
            changeIconsToWhite();
        }
    };

    private BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            changeIconsToBlack();
        }
    };

    private void changeIconsToWhite(){
        btnPlayStop.setImageResource(R.drawable.pause_button);
        btnTwiiter.setImageResource(R.drawable.twitter_white_icon);
        btnFacebook.setImageResource(R.drawable.facebook_white_icon);
        btnInstagram.setImageResource(R.drawable.instagram_white_icon);
        estudioLogo.setImageResource(R.drawable.estudio_playa_logo_stroke);
    }

    private void changeIconsToBlack(){
        btnPlayStop.setImageResource(R.drawable.play_button_black);
        btnTwiiter.setImageResource(R.drawable.twitter_black_icon);
        btnFacebook.setImageResource(R.drawable.facebook_black_icon);
        btnInstagram.setImageResource(R.drawable.instagram_black_icon);
        estudioLogo.setImageResource(R.drawable.estudio_playa_black_logo);

    }
}

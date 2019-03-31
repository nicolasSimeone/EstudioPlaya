package nupa.estudioplaya;

import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

public class YouTube_Fragment_Activity extends AppCompatActivity {

    public static final String API_KEY = "AIzaSyA1DwL9V7GBaE1NoTs_ZtWpovaSwCQymBo";
    public static final String VIDEO_ID = "ZEk_4qyyBq0";
    StreamClass streamClass;
    YouTubePlayerFragment playerFragment;
    ImageButton btnPlayStop, btnFacebook, btnTwiiter, btnInstagram;
    public static String FACEBOOK_URL = "https://www.facebook.com/estudioplaya/ ";
    public static String FACEBOOK_PAGE_ID = "205248466232030";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        streamClass.StartRadio();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_youtube_player_fragment);

        //Initializing and adding YouTubePlayerFragment
        android.app.FragmentManager fm = getFragmentManager();
        String tag = YouTubePlayerFragment.class.getSimpleName();
        //YouTubePlayerFragment playerFragment = (YouTubePlayerFragment) fm.findFragmentByTag(tag);

        playerFragment=(YouTubePlayerFragment)fm.findFragmentById(R.id.youTubeFragment);

        if (playerFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            playerFragment = YouTubePlayerFragment.newInstance();
            ft.add(android.R.id.content, playerFragment, tag);
            ft.commit();

        }


        final Intent i = new Intent(this, StreamClass.class);
        bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);

        playerFragment.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                //youTubePlayer.cueVideo(VIDEO_ID);
                youTubePlayer.loadVideo(VIDEO_ID);
                youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(YouTube_Fragment_Activity.this, "Error while initializing YouTubePlayer.", Toast.LENGTH_SHORT).show();
            }
        });

        Switch radioSwitch =(Switch) findViewById(R.id.switchRadio);
        radioSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    streamClass.StartRadio();
                    finish();
                }
            }
        });

        //Abro Facebook de Estudio Playa
        btnFacebook=(ImageButton)findViewById(R.id.imgFacebookYT);
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
        btnTwiiter=(ImageButton)findViewById(R.id.imgTwitterYT);
        btnTwiiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=328747494")));

                }catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/peladocqcok")));
                }
            }
        });


        btnInstagram=(ImageButton)findViewById(R.id.imgInstagramYT);
        btnInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/peladocqcok/")));

                }catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/peladocqcok/")));
                }
            }

        });
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StreamClass.MyBinder myBinder =(StreamClass.MyBinder)service;
            streamClass=myBinder.getServerInstance();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}


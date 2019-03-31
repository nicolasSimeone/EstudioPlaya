package nupa.estudioplaya;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.media.RemoteController;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Sebastian Faro on 25/10/2016.
 */
public class StreamClass extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, AudioManager.OnAudioFocusChangeListener{

    MediaPlayer mMediaPlayer = new MediaPlayer();
    //String URL ="http://rfcmedia.streamguys1.com/classicrock.mp3";
    String URL ="http://audio.telpin.com.ar/estudioplaya";
    private IBinder mBinder = new MyBinder();
    private static final int NOTIFICATION_ID = 1;
    public boolean isRunning=false;
    public static final String KEY_PLAY = "com.nupa.estudioplaya.StreamClass.StartRadio";
    public static final String KEY_STOP = "com.nupa.estudioplaya.StreamClass.StopRadio";
    public static final String KEY_CLOSE = "com.nupa.estudioplaya.StreamClass.onDestroy";
    private String ACTION_STRING_ACTIVITY = "ToActivity";
    public BroadcastReceiver receiver, call_receiver;
    private AudioManager mAudioManager;

    @Override
    public void onCreate() {
        super.onCreate();

        //Seteo el media Player
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.reset();

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Arranca el servicio e inicio la notificación y preparo la radio
        initNotification();
        prepareRadio();
        return START_STICKY;
    }

    public void prepareRadio(){

        //Inicializo el media player y uso una AsyncTask para que cargue en segundo plano el streaming
           try {
               mMediaPlayer.setDataSource(URL);
               mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
               mMediaPlayer.setOnPreparedListener(this);
               mMediaPlayer.prepareAsync();
          }
           catch(IOException e){
                e.printStackTrace();
            }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        mMediaPlayer.release();
        cancelNotification();
        isRunning=false;
        unregisterReceiver(receiver);
        mAudioManager.abandonAudioFocus(this);
    }

    //Metodo que me perimte bindear la actividad con el Servicio
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //Resultado de la AsyncTask, empieza a reproducir el media Player
    @Override
    public void onPrepared(MediaPlayer mp) {
        if (!mp.isPlaying()){
            mp.start();
            isRunning = true;
            sendBroadcast();
            createNotification(2); //el 2 indica que tiene que generar la notificación con el icono de play
        }
    }

    public void StopRadio(){
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        createNotification(1); //el 1 indica que tiene que usar el icono de stop
    }

    //Metodo de Start utilizado desde la actividad que llame al servicio
    public void StartRadio(){
        prepareRadio();
    }

    //Metodo para saber si el mediaPlayer esta corriendo
    public boolean isPlaying(){
        return mMediaPlayer.isPlaying();
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("Error1","Error not valid playback");
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("Error2","Error server died");
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("Error3","Unknow");
                break;
        }

        switch (extra){
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                Log.d("Error4","Time out");
                break;
        }
        return false;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if(focusChange<=0) {
            mMediaPlayer.setVolume(0,0);
        } else {
            mMediaPlayer.setVolume(1,1);
        }
    }

    public class MyBinder extends Binder{
       public StreamClass getServerInstance(){
            return StreamClass.this;
        }
    }

    //Metodo para iniciar las notificaciones
    private void initNotification(){

        //Acciones posibles desde la barra de notificaciones
        IntentFilter filter = new IntentFilter();
        filter.addAction(KEY_PLAY);
        filter.addAction(KEY_STOP);
        filter.addAction(KEY_CLOSE);

        createNotification(0); //El 0 indica que utiliza el icono de cargar

        //Receiver que escucha las acciones de la barra de notificaciones
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Utilizo la key que envia el boton de la barra de notificaciones para saber que acción realizar
                if (intent.getAction().equals(KEY_PLAY)) {
                    prepareRadio();
                    createNotification(0);
                    sendBroadcastPlay();
                }else if (intent.getAction().equals(KEY_STOP)){
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                    createNotification(1);
                    sendBroadcastStop();

                }else if(intent.getAction().equals(KEY_CLOSE)){
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    stopForeground(true);
                    stopService(new Intent(getApplicationContext(),StreamClass.class));
                    cancelNotification();
                    sendBroadcastFinish(); //Envio una notificación a la actividad para cerrarla
                }
            }
        };

        registerReceiver(receiver,filter); //registro el receiver

    }


    //Metodo de creación de notificaciones
    private void createNotification(int stopPlay){

        //Intnet con acciones de la barra
        Intent play = new Intent(KEY_PLAY);
        final PendingIntent playIntent = PendingIntent.getBroadcast(this, 0, play, 0);

        final Intent stop = new Intent(KEY_STOP);
        final PendingIntent stopIntent = PendingIntent.getBroadcast(this, 0, stop, 0);

        Intent close =new Intent(KEY_CLOSE);
        final PendingIntent closeIntent=PendingIntent.getBroadcast(this,0,close,0);

        PendingIntent pi=PendingIntent.getActivity(getApplicationContext(),0,
                new Intent(getApplicationContext(),MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT); //Intent que se ejecuta cuando se toca en el cuerpo de la notifiacion

        //Genero la notificación
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_radio_white_24dp)
                .setContentTitle("Estudio Playa")
                .setContentIntent(pi)
                .setDeleteIntent(closeIntent);

        //Dependiendo la acción cambio los iconos correspondientes
        if(stopPlay==0){
            mBuilder.addAction(R.drawable.ic_radio_white_24dp,"Cargando",stopIntent);
        }else if (stopPlay==1){
            mBuilder.addAction(R.drawable.ic_play_arrow_black_24dp,"Play",playIntent);
        }else
        {
            mBuilder.addAction(R.drawable.ic_stop_black_24dp,"Stop",stopIntent);
        }

        mBuilder.addAction(R.drawable.ic_close_black_24dp,"Close",closeIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public void cancelNotification() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    //Envio mensaje a la actividad
    private void sendBroadcast() {
        Intent new_intent = new Intent();
        new_intent.setAction(ACTION_STRING_ACTIVITY);
        sendBroadcast(new_intent);
    }

    //Mensaje de finalización del servicio
    private void sendBroadcastFinish(){
        Intent finishIntent= new Intent();
        finishIntent.setAction("com.nupa.estudioplaya.finish");
        sendBroadcast(finishIntent);
    }

    private void sendBroadcastPlay(){
        Intent playStopIntent = new Intent();
        playStopIntent.setAction("com.nupa.estudioplaya.play");
        sendBroadcast(playStopIntent);
    }

    private void sendBroadcastStop(){
        Intent playStopIntent = new Intent();
        playStopIntent.setAction("com.nupa.estudioplaya.Stop");
        sendBroadcast(playStopIntent);
    }


}

package com.gmail.hookmailua.waivz.app.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.gmail.hookmailua.waivz.app.entities.Audiotrack;
import com.gmail.hookmailua.waivz.app.fragments.MFragment;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class PlayerService
        extends
        Service
        implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {

    private final IBinder mBinder = new LocalBinder();

    WifiManager.WifiLock wifiLock;
    private MediaPlayer mediaPlayer;
    private int curPosition;
    private List<Audiotrack> trackList;
    private int trackId;
    private Random randomGenerator;
    private boolean isRandom;

    public PlayerService() {
        Log.i("mTag", "PlayerService, constructor");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");

        randomGenerator = new Random();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("mTag", "PlayerService, onStartCommand");

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Sat if playing is random
     */
    public boolean switchRandom(){
        this.isRandom = !isRandom;
        return isRandom;
    }

    /**
     * Resuming playing track if it was paused, otherwise do nothing
     */
    public void resume() {
        Log.i("mTag", "PlayerService, resume()");

        if (mediaPlayer != null) {
            if (curPosition != 0 && !mediaPlayer.isPlaying()) {
                mediaPlayer.seekTo(curPosition);
                mediaPlayer.start();
            }
        }
    }

    /**
     * Playing specified track from received playlist
     *
     * @param playList Collection with tracks
     * @param trackId  Id of track in specified playlist that we want to play
     */
    public void play(List<Audiotrack> playList, int trackId) {
        Log.i("mTag", "PlayerService, play()");

        if (playList != null) {
            trackList = playList;

            if (trackId >= 0 && trackId < playList.size()) {
                this.trackList = playList;
                this.trackId = trackId;

                if (this.trackId >= 0 && this.trackId < trackList.size()) {
                    startPlaying(trackList.get(this.trackId).getUrl());
                }
            } else {
                Log.i("mTag", "PlayerService, play(), trackId incorrect");
            }
        } else {
            Log.i("mTag", "PlayerService, play(), tracklist is null");
        }
    }

    /**
     * Set player on pause
     */
    public void pause() {
        Log.i("mTag", "PlayerService, pause()");

        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                curPosition = mediaPlayer.getCurrentPosition();
                mediaPlayer.pause();
            }
        }
    }

    /**
     * Skip to next track
     */
    public void skipNext() {
        Log.i("mTag", "PlayerService,skipNext()");

        if (mediaPlayer != null && trackList != null) {
            if (trackId >= 0 && trackId < trackList.size() - 1) {
                trackId++;
                startPlaying(trackList.get(trackId).getUrl());
            }
        }
    }

    /**
     * Skip to random track
     */
    private void skipRandomNext() {
        Log.i("mTag", "PlayerService,skipRandomNext()");

        if (mediaPlayer != null && trackList != null) {

            if (trackId >= 0 && trackId < trackList.size()) {

                trackId = randomGenerator.nextInt(trackList.size() - 1);

                startPlaying(trackList.get(trackId).getUrl());
            }
        }
    }

    /**
     * Skip to previous track
     */
    public void skipPrevious() {
        Log.i("mTag", "PlayerService, skipPrevious()");

        if (mediaPlayer != null && trackList != null) {
            if (trackId > 0 && trackId < trackList.size()) {
                trackId--;
                startPlaying(trackList.get(trackId).getUrl());
            }
        }
    }

    /**
     * Release resources after previous playback, create new instance of MediaPlayer and prepare it.
     *
     * @param URL URL of track that we want to play
     */
    private void startPlaying(String URL) {
        Log.i("mTag", "PlayerService, startPlaying()");

        if (!URL.equals("")) {
            Uri trackUri = Uri.parse(URL);

            //Release resources after previous playback
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            try {
                mediaPlayer.setDataSource(this, trackUri);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.i("mTag", "PlayerService, onCompletion()");

        stopForeground(true);

        if (isRandom) {
            skipRandomNext();
        } else {
            skipNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Log.i("mTag", "PlayerService, onError()");

        return false;
    }

    public class LocalBinder extends Binder {
        public PlayerService getService() {
            Log.i("mTag", "PlayerService, LocalBinder, getService()");
            // Return this instance of LocalService so clients can call public methods
            return PlayerService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i("mTag", "PlayerService, onDestroy()");

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        stopForeground(true);
        wifiLock.release();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.i("mTag", "PlayerService, onPrepared()");

        mediaPlayer.start();

        Intent intent = new Intent(MFragment.SET_SONG_TITLE);
        intent.putExtra(MFragment.SONG_TITLE,
                trackList.get(trackId).getArtist()+ " - " + trackList.get(trackId).getTitle());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        wifiLock.acquire();

        //todo create Notification
    }
}

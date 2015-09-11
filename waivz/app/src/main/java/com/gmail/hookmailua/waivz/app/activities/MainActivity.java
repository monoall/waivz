package com.gmail.hookmailua.waivz.app.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.gmail.hookmailua.waivz.app.entities.Audiotrack;
import com.gmail.hookmailua.waivz.app.fragments.LoginFragment;
import com.gmail.hookmailua.waivz.app.fragments.MFragment;
import com.gmail.hookmailua.waivz.app.R;
import com.gmail.hookmailua.waivz.app.services.PlayerService;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.*;

import java.util.List;

public class MainActivity
        extends AppCompatActivity
        implements MFragment.CallbackInterface {

    private static final String M_FRAGMENT = "com.gmail.hookmailua.waivz.app.Fragments.MFragment";
    private static final String LOGIN_FRAGMENT = "com.gmail.hookmailua.waivz.app.Fragments.LoginFragment";

    private LoginFragment loginFragment;
    private MFragment mFragment;

    private PlayerService mService;
    private boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("mTag", "MainActivity, OnCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, PlayerService.class));

        if (!VKSdk.isLoggedIn()) {
            loadLoginFragment();
        } else {
            loadFirstFragment();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent playerIntent = new Intent(this, PlayerService.class);
        bindService(playerIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("mTag", "MainActivity, OnActivityResult");

        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // ѕользователь успешно авторизовалс€
                loadFirstFragment();
            }

            @Override
            public void onError(VKError error) {
                // ѕроизошла ошибка авторизации (например, пользователь запретил авторизацию)
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void loadLoginFragment() {
        Log.i("mTag", "MainActivity, loadLoginFragment");

        loginFragment = (LoginFragment) getFragmentManager().findFragmentByTag(LOGIN_FRAGMENT);

        if (loginFragment == null) {
            loginFragment = new LoginFragment();
            Log.i("mTag", "MainActivity, loadLoginFragment, created new fragment");
        } else {
            Log.i("mTag", "MainActivity, loadLoginFragment, fragment restored");
        }

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, loginFragment, LOGIN_FRAGMENT)
                .commit();
    }

    private void loadFirstFragment() {
        Log.i("mTag", "MainActivity, loadFirstFragment");

        mFragment = (MFragment) getFragmentManager().findFragmentByTag(M_FRAGMENT);

        if (mFragment == null) {
            mFragment = new MFragment();
            Log.i("mTag", "MainActivity, lodFirstFragment, created new fragment");
        } else {
            Log.i("mTag", "MainActivity, lodFirstFragment, fragment restored");
        }

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, mFragment, M_FRAGMENT)
                .commit();
    }

    public void logOut(MenuItem item) {
        loadLoginFragment();

        VKSdk.logout();
    }

    public void logIn(View view) {
        VKSdk.login(this, "friends", "audio");
    }

    public void plrBtnCntrlr(View view) {
        if (mBound) {
            switch (view.getId()) {
                case R.id.pc_pause:
                    mService.pause();
                    break;
                case R.id.pc_play:
                    mService.resume();
                    break;
                case R.id.pc_skip_next:
                    mService.skipNext();
                    break;
                case R.id.pc_skip_prev:
                    mService.skipPrevious();
                    break;
                /*
                case R.id.pc_shuffle:
                    boolean res = mService.switchRandom();
                    Log.i("mTag", "MainActivity, rand mode is: " + res);

                    break;
                    */
                case R.id.text:
                    LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MFragment.PC_TO_LARGE));
                    break;
            }
        }
    }

    @Override
    public void playMeASong(List<Audiotrack> trackList, int trackId) {
        if (mBound) {
            mService.play(trackList, trackId);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PlayerService.LocalBinder binder = (PlayerService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}

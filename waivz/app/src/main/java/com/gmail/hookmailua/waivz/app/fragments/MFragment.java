package com.gmail.hookmailua.waivz.app.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.ChangeBounds;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.hookmailua.waivz.app.entities.Audiotrack;
import com.gmail.hookmailua.waivz.app.listAdapters.ListAdapter;
import com.gmail.hookmailua.waivz.app.R;
import com.gmail.hookmailua.waivz.app.requests.RequestsToVK;

import java.util.ArrayList;
import java.util.List;

public class MFragment
        extends Fragment {

    public static final String SET_SONG_TITLE = "com.gmail.hookmailua.waivz.app.fragments.MFragment.SET_SONG_TITLE";
    public static final String SONG_TITLE = "com.gmail.hookmailua.waivz.app.fragments.MFragment.SONG_TITLE";
    public static final String PC_TO_LARGE = "com.gmail.hookmailua.waivz.app.fragments.MFragment.PC_TO_LARGE";

    private ListAdapter listAdapter;
    private final List<Audiotrack> data = new ArrayList<Audiotrack>();
    private CallbackInterface activity;
    private View rootView;

    private Scene smallPcScene;
    private Scene largePcScene;
    private ViewGroup sceneRoot;
    private Transition toLargeTransition;
    private Transition toSmallTransition;

    private TextView text;

    private String songTitle;
    private BroadcastReceiver pcToLargeReceiver, setSongTitleReceiver;

    public MFragment() {
        // Required empty public constructor
    }

    public interface CallbackInterface {
        void playMeASong(List<Audiotrack> trackList, int trackId);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.activity = (CallbackInterface) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement CallbackInterface!");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pcToLargeReceiver = new PcToLargeReceiver();
        setSongTitleReceiver = new SongTitleReceiver();

        LocalBroadcastManager
                .getInstance(getActivity())
                .registerReceiver(setSongTitleReceiver, new IntentFilter(SET_SONG_TITLE));

        LocalBroadcastManager
                .getInstance(getActivity())
                .registerReceiver(pcToLargeReceiver, new IntentFilter(PC_TO_LARGE));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(pcToLargeReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(setSongTitleReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("mTag", "MFragment, OnCreateView");

        return inflater.inflate(R.layout.fragment_m, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.i("mTag", "MFragment, OnViewCreated");

        super.onViewCreated(view, savedInstanceState);

        initRV(view);

        RequestsToVK.requestUserTracks(data, listAdapter);

        text = (TextView) view.findViewById(R.id.text);

        rootView = view;

        initScenes(view);

    }

    private void setSongTitle(String title) {
        if (title != null) {
            this.songTitle = title;
            Log.i("mTag", "MFragment, setSonTitle(), title = " + title);
            text.setText(title);
        }
    }

    private void pcToLarge() {
        TransitionManager.go(largePcScene, toSmallTransition);

        text = (TextView) rootView.findViewById(R.id.text);
        text.setText(songTitle);
    }

    private void pcToSmall() {
        TransitionManager.go(smallPcScene, toSmallTransition);

        text = (TextView) rootView.findViewById(R.id.text);
        text.setText(songTitle);
    }

    /**
     * Create scenes from resources for player control animation
     */
    private void initScenes(View fragmentView) {
        sceneRoot = (ViewGroup) fragmentView.findViewById(R.id.controls_toolbar);

        smallPcScene = Scene.getSceneForLayout(sceneRoot, R.layout.small_pc_scene, getActivity());
        largePcScene = Scene.getSceneForLayout(sceneRoot, R.layout.large_pc_scene, getActivity());

        toLargeTransition = new ChangeBounds();
        toSmallTransition = new ChangeBounds();
    }

    //Initiate RecyclerView
    @SuppressWarnings("deprecation")
    private void initRV(View view) {
        Log.i("mTag", "MFragment, initRV");

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        listAdapter = new ListAdapter(getActivity().getApplicationContext(), data, this);
        recyclerView.setAdapter(listAdapter);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                pcToSmall();
            }
        });
    }

    public void playMeASong(List<Audiotrack> trackList, int trackId) {
        Log.i("mTag", "ACTIVITY IS NULL: " + String.valueOf(activity == null));
        pcToLarge();
        activity.playMeASong(trackList, trackId);
    }

    private class SongTitleReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String songTitle = intent.getStringExtra(SONG_TITLE);

            if (songTitle != null) {
                setSongTitle(songTitle);
            }
        }
    }

    private class PcToLargeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            pcToLarge();
        }
    }
}

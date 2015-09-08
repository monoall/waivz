package com.gmail.hookmailua.waivz.app.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.hookmailua.waivz.app.entities.Audiotrack;
import com.gmail.hookmailua.waivz.app.listAdapters.ListAdapter;
import com.gmail.hookmailua.waivz.app.R;
import com.gmail.hookmailua.waivz.app.requests.RequestsToVK;

import java.util.ArrayList;
import java.util.List;

public class MFragment
        extends Fragment {

    private ListAdapter listAdapter;
    private final List<Audiotrack> data = new ArrayList<Audiotrack>();
    private CallbackInterface activity;

    public MFragment() {
        // Required empty public constructor
    }

    public interface CallbackInterface {
        void playMeASong(List<Audiotrack> trackList, int trackId);
    }

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
    }

    //Initiate RecyclerView
    private void initRV(View view) {
        Log.i("mTag", "MFragment, initRV");

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        listAdapter = new ListAdapter(getActivity().getApplicationContext(), data, this);
        recyclerView.setAdapter(listAdapter);
    }

    public void playMeASong(List<Audiotrack> trackList, int trackId){
        Log.i("mTag", "ACTIVITY IS NULL: " + String.valueOf(activity == null));
        activity.playMeASong(trackList, trackId);
    }
}

package com.gmail.hookmailua.waivz.app;

import android.app.Application;
import android.util.Log;
import com.gmail.hookmailua.waivz.app.entities.Audiotrack;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    private List<Audiotrack> data;

    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
                // VKAccessToken is invalid
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);
    }

    public void storeData(List<Audiotrack> data){
        Log.i("mTag", "DATA STORED");
        this.data = data;
    }

    public List<Audiotrack> restoreData(){
        Log.i("mTag", "DATA RESTORED");
        return data == null ? new ArrayList<Audiotrack>() : data;
    }
}


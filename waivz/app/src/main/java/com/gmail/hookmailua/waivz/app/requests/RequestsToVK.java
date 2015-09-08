package com.gmail.hookmailua.waivz.app.requests;

import android.util.Log;
import com.gmail.hookmailua.waivz.app.entities.Audiotrack;
import com.gmail.hookmailua.waivz.app.listAdapters.ListAdapter;
import com.google.gson.Gson;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RequestsToVK {

    /**
     * Make asynchronous request to VK api for user Audiotracks, parse response and update dataset
     *
     * @param oldData Collection that will be filled with new data
     */
    public static void requestUserTracks(final List<Audiotrack> oldData, final ListAdapter listAdapter) {
        VKRequest request = VKApi.audio().get();
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                Log.i("mTag", "MFragment, requestUserTracks, onComplete");
                super.onComplete(response);

                JSONObject jResponse = response.json;
                try {
                    JSONArray jsonArray = jResponse.getJSONObject("response").getJSONArray("items");
                    List<Audiotrack> newData = new ArrayList<Audiotrack>();
                    Gson gson = new Gson();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        Audiotrack audiotrack = gson.fromJson(jsonArray.getJSONObject(i).toString(), Audiotrack.class);
                        newData.add(audiotrack);
                    }

                    Log.i("mTag", "MFragment, requestUserTracks, onComplete, newData size is:" + newData.size());

                    oldData.clear();
                    oldData.addAll(newData);
                    listAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();

                    onError(null);
                }
            }

            @Override
            public void onError(VKError error) {
                Log.i("mTag", "MFragment, requestUserTracks, onError");
                super.onError(error);

                //todo load saved data from previous request
            }
        });
    }

}

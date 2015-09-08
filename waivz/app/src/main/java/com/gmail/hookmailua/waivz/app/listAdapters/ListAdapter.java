package com.gmail.hookmailua.waivz.app.listAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.gmail.hookmailua.waivz.app.R;
import com.gmail.hookmailua.waivz.app.entities.Audiotrack;
import com.gmail.hookmailua.waivz.app.fragments.MFragment;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<Audiotrack> data;
    private Context context;
    private MFragment parrent;

    public ListAdapter(Context context, List<Audiotrack> data, MFragment parrent) {
        Log.i("mTag", "ListAdapter, ListAdapter");
        this.context = context;
        this.data = data;
        this.parrent = parrent;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View rootView;
        private TextView text1;
        private TextView text2;

        public ViewHolder(View itemView) {
            super(itemView);

            rootView = itemView.findViewById(R.id.list_item_root);
            text1 = (TextView) itemView.findViewById(R.id.text1);
            text2 = (TextView) itemView.findViewById(R.id.text2);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.i("mTag", "ListAdapter, onCreateViewHolder, i = " + i);
        View v = LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Log.i("mTag", "ListAdapter, OnBindViewHolder, i = " + i);
        final int pos = i;
        final List<Audiotrack> preparedData = data;
        viewHolder.text1.setText(data.get(i).getArtist());
        viewHolder.text2.setText(data.get(i).getTitle());
        viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("mTag", "ListAdapter, item pressed");

                parrent.playMeASong(preparedData, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

}

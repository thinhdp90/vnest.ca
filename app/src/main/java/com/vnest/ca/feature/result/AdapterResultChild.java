package com.vnest.ca.feature.result;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vnest.ca.R;
import com.vnest.ca.entity.Poi;
import com.vnest.ca.util.NavigationUtil;

import java.util.List;

public class AdapterResultChild extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Poi> poiList;

    public AdapterResultChild(List<Poi> poiList) {
        this.poiList = poiList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChildViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ChildViewHolder) holder).onBind(poiList.get(position));
    }

    @Override
    public int getItemCount() {
        return poiList.size();
    }

    static class ChildViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName;
        private TextView itemAddress;
        private TextView itemDistance;
        private ImageView imageView;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemAddress = itemView.findViewById(R.id.item_address);
            itemDistance = itemView.findViewById(R.id.item_distance);
            imageView = itemView.findViewById(R.id.imageView);
        }

        public void onBind(Poi poi) {
            itemName.setText(poi.getTitle());
            itemAddress.setText(poi.getAddress());
            itemDistance.setText(((int) poi.getDistance()) + "m");
            Glide.with(imageView).load(poi.getImg())
                    .into(imageView);
            itemView.setOnClickListener( view1 -> {
                NavigationUtil.displayPointToMap(poi,itemView.getContext());
//                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + poi.getGps().getLatitude() + "," + poi.getGps().getLongitude());
//                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                mapIntent.setPackage("com.google.android.apps.maps");
//                itemView.getContext().startActivity(mapIntent);
            });
        }
    }
}

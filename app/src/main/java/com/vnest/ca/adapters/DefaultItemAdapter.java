package com.vnest.ca.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.vnest.ca.R;
import com.vnest.ca.entity.Poi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class DefaultItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<String> defaultList = new ArrayList<>();
    private ItemClickListener itemClickListener;

    public DefaultItemAdapter(ArrayList<String> defaultList) {
        this.defaultList = defaultList;
    }

    public ArrayList<String> getDefaultList() {
        return defaultList;
    }

    public void setDefaultList(ArrayList<String> defaultList) {
        this.defaultList = defaultList;
    }

    public DefaultItemAdapter(ItemClickListener itemClickListener) {
        setDefaultList();
        this.itemClickListener = itemClickListener;
    }

    public void setDefaultList() {
        String[] defItems = {"Open \"Bang Kieu\" Playlist",
                "Open \"VOV giao thong\"",
                "\"Navigation\" to nearest ATM",
                "Open \"Bich Phuong\" via \"Zing MP3\"",
                "Open Google Maps",
                "\"Navigation\" to 22 Ngo 151 Ton That Tung Dong Da Ha Noi",
                "Open \"Youtube\"",
                "\"Navigation\" to nearest VPBank",
                "See more..."};
        defaultList.addAll(Arrays.asList(defItems));
    }

    public void setListAfterProcess(List<Poi> pois) {
        pois.forEach(new Consumer<Poi>() {
            @Override
            public void accept(Poi poi) {
                defaultList.clear();
                defaultList.add(poi.getTitle());
                notifyDataSetChanged();
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_btn_main_def, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).onBind(defaultList.get(position), itemClickListener, position);
    }

    @Override
    public int getItemCount() {
        return defaultList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private Button itemName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_names);

        }

        public void onBind(final String item, final ItemClickListener itemClickListener, final int position) {
            itemName.setText(item);
            itemName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClickListener(position, item);
                }
            });
        }
    }

    public interface ItemClickListener {
        public void onItemClickListener(int position, String name);
    }
}

package ai.kitt.snowboy.feature.result;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import ai.kitt.snowboy.entity.Poi;
import ai.kitt.snowboy.util.NavigationUtil;

import java.util.List;

import ai.kitt.snowboy.R;

public class AdapterResultChild extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Poi> poiList;
    private Boolean isCollapse;

    public AdapterResultChild(List<Poi> poiList) {
        this.poiList = poiList;
        isCollapse = poiList.size() > 3;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        if (viewType == R.layout.item_load_more) {
            return new LoadMoreViewHolder(view);
        }
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (isCollapse && position == 3) {
            ((LoadMoreViewHolder) holder).onBind();
            return;
        }
        ((ChildViewHolder) holder).onBind(poiList.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        if (isCollapse && position == 3) return R.layout.item_load_more;
        return R.layout.item_result;
    }

    @Override
    public int getItemCount() {
        return isCollapse ? 4 : poiList.size();
    }

    static class ChildViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName;
        private TextView itemAddress;
        private TextView itemDistance;
        private ImageView imageView;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemAddress = itemView.findViewById(R.id.total_item);
            itemDistance = itemView.findViewById(R.id.item_distance);
            imageView = itemView.findViewById(R.id.imageView);
        }

        @SuppressLint("SetTextI18n")
        public void onBind(Poi poi) {
            try {
                itemName.setText(poi.getTitle());
                itemAddress.setText(poi.getAddress());
                itemDistance.setText(((int) poi.getDistance()) + "m");
                Glide.with(imageView).load(poi.getImg())
                        .into(imageView);
                itemView.setOnClickListener(view1 -> {
                    NavigationUtil.displayPointToMap(poi, itemView.getContext());
                });
            } catch (Exception e) {

            }

        }
    }

    class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        private TextView totalItem;

        public LoadMoreViewHolder(@NonNull View itemView) {
            super(itemView);
            totalItem = itemView.findViewById(R.id.total_item);
        }

        @SuppressLint("SetTextI18n")
        public void onBind() {
            totalItem.setText(poiList.size() - 3 + "+");
            itemView.setOnClickListener(v -> {
                isCollapse = false;
                notifyDataSetChanged();
            });
        }
    }
}

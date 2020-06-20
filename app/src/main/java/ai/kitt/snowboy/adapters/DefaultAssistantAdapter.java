package ai.kitt.snowboy.adapters;

import android.graphics.drawable.Drawable;
import android.speech.tts.TextToSpeech;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import ai.kitt.snowboy.R;

public class DefaultAssistantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Pair<Integer, String>> defList = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public DefaultAssistantAdapter(OnItemClickListener onItemClickListener) {
        setDefaultList();
        this.onItemClickListener = onItemClickListener;
    }

    public void setDefaultList() {
        Pair<Integer, String>[] defItems = new Pair[]{new Pair<>(R.drawable.ic_ytb, "Youtube Assistant"),
                new Pair<>(R.drawable.ic_nav, "Navigation Assistant"),
                new Pair<>(R.drawable.ic_fuel, "Fuel History"),
                new Pair<>(R.drawable.ic_mtn, "Maintain Schedule"),
                new Pair<>(R.drawable.ic_add, "Add more")
        };
        defList.addAll(Arrays.asList(defItems));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_background_side_left, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).onBind(defList.get(position), onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return defList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView itemName;
        private AppCompatImageView itemIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_names);
            itemIcon = itemView.findViewById(R.id.item_icon);
        }

        public void onBind(final Pair<Integer, String> item, final OnItemClickListener onItemClick) {
            itemName.setText(item.second);
            itemIcon.setImageDrawable(itemView.getContext().getDrawable(item.first));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.onClick(item.second, getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onClick(String text, int position);
    }
}

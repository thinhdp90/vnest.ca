package ai.kitt.snowboy.feature.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

import ai.kitt.snowboy.R;

public class AdapterHomeItemDefault extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String[] defItems = {"Chỉ đường đến hồ hoàn kiếm",
            "Chỉ đường đến bến xe mỹ đình",
            "Chỉ đường đến Landmark 72",
            "Chỉ đường đến chợ Bến Thành",
            "Chỉ đường đến nhà thờ Đức Bà",
            "Mở bài hãy trao cho anh",
            "Mở bài Sầu tím thiệp hồng",
            "Mở bài tàu anh qua núi",
            "Mở Video"};

    private ArrayList<String> defaultList = new ArrayList<>();

    public ItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private ItemClickListener itemClickListener;
    private Context context;
    private TextToSpeech textToSpeech;
    private OnProcessingText processing_text;

    public AdapterHomeItemDefault(ArrayList<String> defaultList) {
        this.defaultList = defaultList;
    }

    public AdapterHomeItemDefault(Context context, TextToSpeech textToSpeech, OnProcessingText onProcessingText) {
        this.context = context;
        this.textToSpeech = textToSpeech;
        this.processing_text = onProcessingText;
        setDefaultList();
    }

    public ArrayList<String> getDefaultList() {
        return defaultList;
    }

    public void setDefaultList(ArrayList<String> defaultList) {
        this.defaultList = defaultList;
    }

    public AdapterHomeItemDefault(ItemClickListener itemClickListener) {
        setDefaultList();
        this.itemClickListener = itemClickListener;
    }

    public void setDefaultList() {
        defaultList.addAll(Arrays.asList(defItems));
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
            itemName.setOnClickListener(v -> itemClickListener.onItemClickListener(position, item));
        }
    }

    public interface ItemClickListener {
        void onItemClickListener(int position, String name);
    }

    public interface OnProcessingText {
        void process(String text);
    }
}

package com.vnest.ca.feature.home;

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

import com.vnest.ca.R;
import com.vnest.ca.ViewPortType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class AdapterHomeItemDefault extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String[] defItems = {"Chỉ đường đến hồ hoàn kiếm",
            "Chỉ đường đến bến xe mỹ đình",
            "Chỉ đường đến Landmark 72",
            "Chỉ đường đến chợ Bến Thành",
            "Chỉ đường đến nhà thờ Đức Bàm nhạc",
            "Mở bài hãy trao cho anh",
            "Mở bài Sầu tím thiệp hồng",
            "Mở bài tàu anh qua núi",
            "Video"};

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
        setDefaultListener(context);
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

    public void setDefaultList(ViewPortType viewPortType) {
        defaultList.clear();
        ArrayList<String> items = new ArrayList(Arrays.asList(defaultList));
        switch (viewPortType) {
            case PHONE:
                break;
            case TABLET_W1280XH480:
                Arrays.asList(defaultList).subList(0, 4).forEach(new Consumer<ArrayList<String>>() {
                    @Override
                    public void accept(ArrayList<String> strings) {
                        defaultList.addAll(strings);
                    }
                });
                defaultList.add(items.get(items.size() - 1));
                break;
            default:
                defaultList = items;
        }
    }

    public void setDefaultListener(Context context) {
        itemClickListener = new ItemClickListener() {
            public void onItemClickListener(int position, String name) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String link;
                switch (position) {
                    case 0:
                        link = "https://zingmp3.vn/album/Nhung-Bai-Hat-Hay-Nhat-Cua-Bang-Kieu-Bang-Kieu/ZWZ9DAEI.html";
                        intent.setData(Uri.parse(link));
                        intent.setPackage("com.zing.mp3");
                        try {
                            context.startActivity(intent);
                        } catch (Exception e) {
                            textToSpeech.speak("Bạn chưa cài đặt mp3!", TextToSpeech.QUEUE_FLUSH, null);
                            intent.setPackage("com.android.chrome");
                            context.startActivity(intent);
                        }
                        break;
                    case 1:
                        intent = new Intent(Intent.ACTION_VIEW);
                        link = "https://vovgiaothong.vn/";
                        intent.setData(Uri.parse(link));
                        context.startActivity(intent);
//                            search("https://vovgiaothong.vn/");
                        break;
                    case 2:
                        processing_text.process("Tìm ATM gần nhất");
                        break;
                    case 3:
                        link = "https://zingmp3.vn/tim-kiem/artist.html?q=B%C3%ADch%20Ph%C6%B0%C6%A1ng";
                        intent.setData(Uri.parse(link));
                        intent.setPackage("com.zing.mp3");
                        try {
                            context.startActivity(intent);
                        } catch (Exception e) {
                            textToSpeech.speak("Bạn chưa cài đặt mp3!", TextToSpeech.QUEUE_FLUSH, null);
                        }
                        break;
                    case 4:
                        intent.setPackage("com.google.android.apps.maps");
                        context.startActivity(intent);
                        break;
                    case 5:
                        String query = "so 22 Ngo 151 Ton That Tung Dong Da Ha Noi";
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + query);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        context.startActivity(mapIntent);
                        break;
                    case 6:
                        intent = new Intent(Intent.ACTION_VIEW);
                        Uri uri = Uri.parse("https://www.youtube.com/");
                        intent.setData(uri);
                        intent.setPackage("com.google.android.youtube");
                        context.startActivity(intent);
                        break;
                    case 7:
                        processing_text.process("Tìm ATM VPBank gần nhất");
                        break;
                    default:
                        textToSpeech.speak("Hiện tại bạn chưa thể sử dụng chức năng này", TextToSpeech.QUEUE_FLUSH, null);
                        break;
                }
            }
        };
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

    public interface OnProcessingText {
        void process(String text);
    }
}

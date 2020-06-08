package com.vnest.ca.feature.result;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vnest.ca.R;

import java.util.ArrayList;

import javax.xml.transform.Result;

public class AdapterResult extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ResultItem> mListItem = new ArrayList<>();

    public ArrayList<ResultItem> getListItem() {
        return mListItem;
    }

    public void setListItem(ArrayList<ResultItem> mListItem) {
        this.mListItem = mListItem;
    }

    public void addItem(ResultItem resultItem) {
        mListItem.add(resultItem);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            case R.layout.item_user:
                viewHolder = new UserViewHolder(view);
                break;
            case R.layout.item_assistant:
                viewHolder = new AssistantViewHolder(view);
                break;
            case R.layout.item_result:
                viewHolder = new ResultViewHolder(view);
                break;
            default:
                throw new IllegalArgumentException("No item view type found!");
        }
        ;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((BindViewHolder) holder).onBind(mListItem.get(position));
    }

    @Override
    public int getItemCount() {
        return mListItem.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mListItem.get(position).getItemViewType();
    }


    static class UserViewHolder extends RecyclerView.ViewHolder implements BindViewHolder {

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
        }


        @Override
        public void onBind(ResultItem item) {

        }
    }

    static class AssistantViewHolder extends RecyclerView.ViewHolder implements BindViewHolder {

        public AssistantViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void onBind(ResultItem item) {

        }
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder implements BindViewHolder {

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
        }


        @Override
        public void onBind(ResultItem item) {

        }
    }

    interface BindViewHolder {
        public void onBind(ResultItem item);
    }
}

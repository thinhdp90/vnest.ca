package com.vnest.ca.feature.result;

import com.vnest.ca.R;

public class ItemAssistant implements ResultItem {
    private boolean isFromUser;

    public ItemAssistant(boolean isFromUser) {
        this.isFromUser = isFromUser;
    }

    @Override
    public int getItemViewType() {
        return isFromUser ? R.layout.item_user : R.layout.item_assistant;
    }
}

package com.vnest.ca.feature.result;

import com.vnest.ca.R;

public class ItemAssistant implements ResultItem {
    private boolean isFromUser;
    private String text;

    public boolean isFromUser() {
        return isFromUser;
    }

    public void setFromUser(boolean fromUser) {
        isFromUser = fromUser;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ItemAssistant(String text, boolean isFromUser) {
        this.isFromUser = isFromUser;
        this.text = text;
    }

    @Override
    public int getItemViewType() {
        return isFromUser ? R.layout.item_user : R.layout.item_mess_from_assistant;
    }
}

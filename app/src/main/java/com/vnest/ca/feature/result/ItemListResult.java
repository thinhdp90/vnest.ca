package com.vnest.ca.feature.result;

import com.vnest.ca.R;
import com.vnest.ca.entity.Poi;

import java.util.ArrayList;
import java.util.List;

public class ItemListResult implements ResultItem {
    private List<Poi> poiList;

    public ItemListResult(List<Poi> poiList) {
        this.poiList = poiList;
    }

    public List<Poi> getPoiList() {
        return poiList;
    }

    public void setPoiList(List<Poi> poiList) {
        this.poiList = poiList;
    }

    @Override
    public int getItemViewType() {
        return R.layout.item_result;
    }
}

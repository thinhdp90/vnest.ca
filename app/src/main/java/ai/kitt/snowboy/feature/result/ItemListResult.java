package ai.kitt.snowboy.feature.result;



import ai.kitt.snowboy.entity.Poi;

import java.util.List;

import ai.kitt.snowboy.R;

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
        return R.layout.item_list_result;
    }
}

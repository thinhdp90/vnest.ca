package ai.kitt.snowboy.feature.settings.selectmaps

import ai.kitt.snowboy.R
import ai.kitt.snowboy.util.AppUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_maps.view.*

class Adapter(val defAppId: String, val onCLick: (item: MapItems) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        val listItems = arrayOf(MapItems(AppUtil.MAPS_GOOGLE_MAP_APP_ID, "Google map"), MapItems(AppUtil.MAPS_NATIVEL_APP_ID, "Navitel"), MapItems(AppUtil.MAPS_VIET_MAP_APP_ID, "Vietmap"))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_maps, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = 3

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ViewHolder
        holder.onBind(listItems[position], onCLick)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun onBind(item: MapItems, onCLick: (item: MapItems) -> Unit) {
            itemView.labelView.text = item.name
            itemView.icon.visibility = if (item.mapId.equals(defAppId, ignoreCase = true)) View.VISIBLE else View.GONE
            itemView.setOnClickListener {
                onCLick(item)
            }
        }
    }
}
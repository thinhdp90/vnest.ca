package ai.kitt.snowboy.feature.settings.selectmaps

import ai.kitt.snowboy.R
import ai.kitt.snowboy.base.BaseFragment
import ai.kitt.snowboy.database.sharepreference.VnestSharePreference
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_maps.view.*

class FragmentMaps : BaseFragment(R.layout.fragment_maps) {

    val adapter by lazy {
        Adapter(VnestSharePreference.getInstance(requireContext()).mapAppId) {
            VnestSharePreference.getInstance(requireContext()).saveMapAppId(it.mapId)
            requireActivity().onBackPressed()
        }
    }

    override fun initView(view: View) {
        view.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        view.recyclerView.adapter = adapter
    }

    override fun initAction(view: View) {
        (requireActivity() as AppCompatActivity).setSupportActionBar(view.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
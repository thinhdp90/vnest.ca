package ai.kitt.vnest.feature.screensettings.selectmaps

import ai.kitt.vnest.R
import ai.kitt.vnest.base.BaseFragment
import ai.kitt.vnest.basedata.database.sharepreference.VnestSharePreference
import ai.kitt.vnest.feature.screensettings.FragmentSettings
import ai.kitt.vnest.feature.screensettings.SettingsViewModel
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_maps.view.*

class FragmentSelectMaps : BaseFragment(R.layout.fragment_maps) {

    val adapter by lazy {
        Adapter(VnestSharePreference.getInstance(requireContext()).mapAppId) {
            viewModel.settingLiveData.postValue(true)
            VnestSharePreference.getInstance(requireContext()).saveMapAppId(it.mapId)
            requireActivity().onBackPressed()
        }
    }
    private val viewModel by lazy { ViewModelProvider(requireActivity()).get(SettingsViewModel::class.java) }

    override fun initView(view: View) {
        view.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        view.recyclerView.adapter = adapter
    }

    override fun initAction(view: View) {
        (requireActivity() as AppCompatActivity).setSupportActionBar(view.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun gotoSettings() {
        val fragment = requireActivity().supportFragmentManager.findFragmentByTag(FragmentSettings.TAG) ?: FragmentSettings()
        requireActivity().supportFragmentManager
                .beginTransaction()
                .remove(this)
                .replace(R.id.fragment_container, fragment, FragmentSettings.TAG)
                .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }
}
package ai.kitt.snowboy.feature.settings

import ai.kitt.snowboy.R
import ai.kitt.snowboy.activities.MainActivity
import ai.kitt.snowboy.base.BaseFragment
import ai.kitt.snowboy.database.sharepreference.VnestSharePreference
import ai.kitt.snowboy.databinding.FragmentSettingsBinding
import ai.kitt.snowboy.feature.settings.selectmaps.FragmentMaps
import ai.kitt.snowboy.util.AppUtil
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class FragmentSettings : BaseFragment(R.layout.fragment_settings) {
    companion object {
        const val TAG = "settings"
        fun startThis() {

        }
    }

    val mapDef by lazy { VnestSharePreference.getInstance(requireContext()).mapAppId }
    private val viewModel by lazy { ViewModelProvider(requireActivity()).get(SettingsViewModel::class.java) }

    private lateinit var binding: FragmentSettingsBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, resLayout, container, false)
        binding.maps.detailText = when (VnestSharePreference.getInstance(requireContext()).mapAppId) {
            AppUtil.MAPS_NATIVEL_APP_ID -> getString(R.string.maps_navitel_app_name)
            AppUtil.MAPS_GOOGLE_MAP_APP_ID -> getString(R.string.google_map_app_name)
            else -> getString(R.string.maps_viet_map_app_name)
        }
        return binding.root
    }

    override fun initView(view: View) {
        binding.maps.detailText = when (mapDef) {
            AppUtil.MAPS_NATIVEL_APP_ID -> getString(R.string.maps_navitel_app_name)
            AppUtil.MAPS_VIET_MAP_APP_ID -> getString(R.string.maps_viet_map_app_name)
            else -> getString(R.string.google_map_app_name)
        }
    }

    @SuppressLint("RestrictedApi")
    override fun initAction(view: View) {
        binding.maps.root.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, FragmentMaps())
                    .addToBackStack("1")
                    .commit()
        }
        viewModel.settingLiveData.observe(viewLifecycleOwner, Observer {
            if(it) {
                binding.maps.detailText = when (VnestSharePreference.getInstance(requireContext()).mapAppId) {
                    AppUtil.MAPS_NATIVEL_APP_ID -> getString(R.string.maps_navitel_app_name)
                    AppUtil.MAPS_GOOGLE_MAP_APP_ID -> getString(R.string.google_map_app_name)
                    else -> getString(R.string.maps_viet_map_app_name)
                }
            }
        })
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) {
            (requireActivity() as MainActivity).startResultFragment()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
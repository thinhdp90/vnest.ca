package ai.kitt.snowboy.feature.settings

import ai.kitt.snowboy.R
import ai.kitt.snowboy.base.BaseFragment
import ai.kitt.snowboy.database.sharepreference.VnestSharePreference
import ai.kitt.snowboy.databinding.FragmentSettingsBinding
import ai.kitt.snowboy.feature.settings.selectmaps.FragmentMaps
import ai.kitt.snowboy.util.AppUtil
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment

class FragmentSettings : BaseFragment(R.layout.fragment_settings) {
    companion object {
        fun startThis() {

        }
    }

    val mapDef by lazy { VnestSharePreference.getInstance(requireContext()).mapAppId }

    private lateinit var binding: FragmentSettingsBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, resLayout, container, false)
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
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}
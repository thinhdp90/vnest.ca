package ai.kitt.snowboy.feature.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    val settingLiveData by lazy { MutableLiveData<Boolean>() }
}
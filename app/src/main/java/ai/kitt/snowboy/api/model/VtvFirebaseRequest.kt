package ai.kitt.snowboy.api.model

data class VtvFirebaseRequest(
    val installation: Installation
) {
    companion object{
        @JvmStatic
        fun getDefault()  = VtvFirebaseRequest(Installation("w:0.3.3"))
    }
}

data class Installation(
    val sdkVersion: String
)
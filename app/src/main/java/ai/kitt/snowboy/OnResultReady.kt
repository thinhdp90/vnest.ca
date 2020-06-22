package ai.kitt.snowboy

interface OnResultReady {
    fun onResults(results: ArrayList<String>)
    fun onStreamResult(partialResults: ArrayList<String>)
}
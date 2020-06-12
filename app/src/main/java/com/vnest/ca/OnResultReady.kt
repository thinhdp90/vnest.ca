package com.vnest.ca

interface OnResultReady {
    fun onResults(results: ArrayList<String>)
    fun onStreamResult(partialResults: ArrayList<String>)
}
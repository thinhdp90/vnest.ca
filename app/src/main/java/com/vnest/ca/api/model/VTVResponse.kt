package com.vnest.ca.api.model

data class VTVResponse(
        val ads_tags: String,
        val ads_time: String,
        val channel_name: String,
        val chromecast_url: String,
        val content_id: Int,
        val date: String,
        val geoname_id: Int,
        val player_type: String,
        val remoteip: String,
        val stream_info: List<StreamInfo>,
        val stream_url: List<String>
)

data class StreamInfo(
    val bandwidth: Int,
    val resolution: String
)

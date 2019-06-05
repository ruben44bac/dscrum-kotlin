package com.santiago.dscrum_k.Socket


data class attributes_response<T>(
    var attributes: T? = null
)

data class data_response<T>(
    var data: attributes_response<T>? = null
)

data class stories_response(
    var response: data_response<stories>? = null,
    var status: String? = null
)

data class stories(
    var stories: ArrayList<stories_array>? = null,
    var total: Int? = null
)

data class stories_array(
    var date_end: String? = null,
    var date_start: String? = null,
    var difficulty_id: Int? = null,
    var id: Int? = null,
    var name: String? = null,
    var team_id: Int? =null
)

data class story_detail_response(
    var data: story_detail_data? = null
)

data class story_detail_data(
    var date_end: String? = null,
    var date_start: String? = null,
    var difficulty_id: Int? = null,
    var difficulty_name: String? = null,
    var name: String? = null,
    var status: String? = null,
    var users: ArrayList<story_detail_user>? = null
)

data class story_detail_user(
    var difficulty_id: Int? = null,
    var difficulty_name: String? = null,
    var id: Int? = null,
    var name: String? = null,
    var online: Boolean? = null,
    var surname: String? = null,
    var username: String? = null
)

data class story_detail_user_online(
    var user_id: Int? = null,
    var name: String? = null,
    var username: String? = null
)
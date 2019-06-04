package com.santiago.dscrum_k.Api

class user_response {
    var id: Int = 0
    var image: String? = null
    var mail: String? = null
    var name: String? = null
    var surname: String? = null
    var team_id: Int = 0
    var username: String? = null
    var error: String? = null
}

data class login_response(
    var token: String? = null,
    var error: String? = null
)

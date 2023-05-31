package ed.maevski.remote_module.entity_token

data class TokenResponse(
    val access_token: String,
    val expires_in: Int,
    val status: String,
    val token_type: String
)
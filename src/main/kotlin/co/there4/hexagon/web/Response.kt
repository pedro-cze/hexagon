package co.there4.hexagon.web

import java.net.HttpCookie

/**
 * Check 'Request' comment. And also note that lists should be updated by engines after callback
 * is processed, this data is like a buffer that needs to be dumped to the real response.
 *
 * HTTP response context.
 */
interface Response {
    var body: Any
    var status: Int
    var contentType: String
    val cookies: MutableMap<String, HttpCookie>

    fun addHeader (name: String, value: String)

    fun addCookie (cookie: HttpCookie)
    fun removeCookie (name: String)

    fun redirect (url: String)
}
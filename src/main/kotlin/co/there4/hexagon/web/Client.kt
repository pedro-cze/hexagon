package co.there4.hexagon.web

import java.net.URL

import org.asynchttpclient.request.body.multipart.Part as AsyncHttpPart
import org.asynchttpclient.DefaultAsyncHttpClient
import org.asynchttpclient.Response
import org.asynchttpclient.cookie.Cookie

import co.there4.hexagon.web.HttpMethod.*
import org.asynchttpclient.BoundRequestBuilder

/**
 * Client to use other REST services (like the ones created with Blacksheep).
 */
class Client (val endpointUrl: URL, val useCookies: Boolean = true) {
    val endpoint = endpointUrl.toString()
    val client = DefaultAsyncHttpClient()
    val cookies: MutableMap<String, Cookie> = mutableMapOf()

    /**
     * Synchronous execution.
     */
    fun send (method: HttpMethod, url: String = "/", body: AsyncHttpPart? = null): Response {
        val request = createRequest(method, url)

        if (body != null)
            request.addBodyPart(body)

        if (useCookies)
            cookies.forEach { request.addCookie(it.value) }

        val response = request.execute().get() ?: throw RuntimeException("'null' http response")

        if (useCookies) {
            response.cookies.forEach {
                if (it.value == "" && it.path == "/")
                    cookies.remove(it.name)
                else
                    cookies[it.name] = it
            }
        }

        return response
    }

    fun get (url: String = "/", body: AsyncHttpPart? = null) = send (GET, url, body)
    fun head (url: String = "/", body: AsyncHttpPart? = null) = send (HEAD, url, body)
    fun post (url: String = "/", body: AsyncHttpPart? = null) = send (POST, url, body)
    fun put (url: String = "/", body: AsyncHttpPart? = null) = send (PUT, url, body)
    fun delete (url: String = "/", body: AsyncHttpPart? = null) = send (DELETE, url, body)
    fun trace (url: String = "/", body: AsyncHttpPart? = null) = send (TRACE, url, body)
    fun options (url: String = "/", body: AsyncHttpPart? = null) = send (OPTIONS, url, body)
    fun patch (url: String = "/", body: AsyncHttpPart? = null) = send (PATCH, url, body)

    private fun createRequest(method: HttpMethod, url: String): BoundRequestBuilder {
        val requestUrl = endpoint + url
        return when (method) {
            GET -> client.prepareGet (requestUrl)
            HEAD -> client.prepareHead (requestUrl)
            POST -> client.preparePost (requestUrl)
            PUT -> client.preparePut (requestUrl)
            DELETE -> client.prepareDelete (requestUrl)
            TRACE -> client.prepareTrace (requestUrl)
            OPTIONS -> client.prepareOptions (requestUrl)
            PATCH -> client.preparePatch (requestUrl)
        }
    }
}

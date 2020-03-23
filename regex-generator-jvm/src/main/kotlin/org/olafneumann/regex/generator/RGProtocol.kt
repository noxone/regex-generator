package org.olafneumann.regex.generator

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler
import java.net.URLStreamHandlerFactory
import java.security.Permission

class RGProtocol private constructor(url: URL) : URLConnection(url) {
    private val data: ByteArray

    init {
        val bos = ByteArrayOutputStream()

        val res = javaClass.getResource(getURL().file)
        println("load: ${getURL().file}")
        println(res)
        println(res.toExternalForm())

        res.openStream().use { it.copyTo(bos) }

        // javaClass.getResourceAsStream(res.toExternalForm()).copyTo(bos)
        data = bos.toByteArray()
    }

    override fun connect() {
        connected = true
    }

    override fun getHeaderField(name: String?): String? =
        when (name?.toLowerCase()) {
            "content-type" -> contentType
            "content-length" -> contentLength.toString()
            else -> null
        }

    override fun getContentType(): String =
        when (val extension = getURL().file.replace(Regex("^.*?([^.]+)\$"), "$1").toLowerCase()) {
            "html" -> "text/html"
            "jpg", "jpeg", "png", "gif" -> "image/$extension"
            "js" -> "application/javascript"
            else -> "text/$extension"
        }

    override fun getContentLength(): Int = data.size

    override fun getContentLengthLong(): Long = data.size.toLong()

    override fun getDoInput(): Boolean = true

    override fun getInputStream(): InputStream = ByteArrayInputStream(data)

    override fun getPermission(): Permission?  = null

    private class RGStreamHandler : URLStreamHandler() {
        override fun openConnection(url: URL?): URLConnection {
            if (url == null) {
                throw NullPointerException("input URL is null")
            }
            return RGProtocol(url)
        }
    }

    companion object {
        private const val PROTOCOL = "regexgenerator"

        private val streamHandlerFactory = URLStreamHandlerFactory { protocol ->
            when (protocol) {
                PROTOCOL -> RGStreamHandler()
                else -> null
            }
        }

        fun registerRegexGeneratorProtocol() {

            URL.setURLStreamHandlerFactory(streamHandlerFactory)
        }

        fun toRegexGeneratorProtocol(filename: String) = "$PROTOCOL://host/$filename"
    }
}
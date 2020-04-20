package org.olafneumann.regex.generator

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler
import java.net.URLStreamHandlerFactory
import java.security.Permission

class RegexGeneratorProtocol private constructor(url: URL) : URLConnection(url) {
    private val data: ByteArray
    private val extension: String

    init {
        val bos = ByteArrayOutputStream()
        val res = javaClass.getResource(getURL().file)
            ?: throw IOException("Cannot find resource ${getURL()}")

        println("load: ${getURL().file}")
        res.openStream().use { it.copyTo(bos) }

        data = bos.toByteArray()
        extension = EXTENSION_REGEX.matchEntire(getURL().file)?.groupValues?.get(1)
            ?: throw RuntimeException("Unable to recognize file extension in '${getURL().file}'")
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
        when (extension) {
            "jpg", "jpeg", "png", "gif" -> "image/$extension"
            "js" -> "application/javascript"
            else -> "text/$extension"
        }

    override fun getContentLength(): Int = data.size

    override fun getContentLengthLong(): Long = data.size.toLong()

    override fun getDoInput(): Boolean = true

    override fun getInputStream(): InputStream = ByteArrayInputStream(data)

    override fun getPermission(): Permission? = null

    private class RGStreamHandler : URLStreamHandler() {
        override fun openConnection(url: URL?): URLConnection {
            if (url == null) {
                throw NullPointerException("input URL is null")
            }
            return RegexGeneratorProtocol(url)
        }
    }

    companion object {
        private val EXTENSION_REGEX = Regex("^.*?([^.]*)\$")
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
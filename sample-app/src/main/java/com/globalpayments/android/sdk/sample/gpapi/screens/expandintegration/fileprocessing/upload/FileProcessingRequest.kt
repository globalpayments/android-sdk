package com.globalpayments.android.sdk.sample.gpapi.screens.expandintegration.fileprocessing.upload

import com.android.internal.http.multipart.FilePart
import com.android.internal.http.multipart.MultipartEntity
import com.global.api.entities.Request
import com.global.api.entities.exceptions.ApiException
import java.io.File
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class FileProcessingRequest : suspend (String, File) -> Unit {

    private val headers = mutableMapOf<String, String>()

    init {
        headers["Content-Type"] = "text/csv"
    }

    override suspend fun invoke(uploadUrl: String, fileToUpload: File) {
        val httpClient = (URL(uploadUrl.trim()).openConnection() as HttpsURLConnection).apply {
            requestMethod = Request.HttpMethod.Put.value
            connectTimeout = 30000
            doInput = true
            doOutput = true
            setHeaders(headers)
        }
        val multipartEntity = MultipartEntity(arrayOf(FilePart("file", fileToUpload)))
        httpClient.outputStream.use {
            multipartEntity.writeTo(it)
            it.flush()
        }
        val response = httpClient.responseCode
        if (response != 200) {
            throw ApiException("ERROR: status code $response");
        }
    }

    private fun HttpsURLConnection.setHeaders(headers: Map<String, String>) {
        headers.forEach { (key, value) ->
            addRequestProperty(key, value)
        }
    }
}

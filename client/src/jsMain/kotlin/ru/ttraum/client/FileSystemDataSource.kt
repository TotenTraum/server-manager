package ru.ttraum.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import ru.ttraum.shared.api.FileSystemApi
import ru.ttraum.shared.api.dto.GetFSResponse

class FileSystemDataSource(private val url: String) : FileSystemApi {
    private val client = HttpClient(Js) {
        install(ContentNegotiation) {
            json(Json)
        }
    }

    override suspend fun get(path: String, canGetFile: Boolean): GetFSResponse {
        val response = client.get(url.wrap() + "fs/"+ path.removePrefix("/"))

        when (response.status) {
            OK -> return response.body()
            else -> throw RuntimeException()
        }
    }

    override suspend fun createFile(path: String, fileName: String, bytes: ByteArray, canRewriteFile: Boolean) {
        TODO("Not yet implemented")
    }

    private fun String.wrap(): String {
        return if (this.endsWith("/"))
            this
        else "$this/"
    }
}
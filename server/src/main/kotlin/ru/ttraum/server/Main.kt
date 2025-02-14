package ru.ttraum.server

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import ru.ttraum.server.controller.FileSystemController

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    cors()
    serialization()

    val fileSystemController = FileSystemController()

    routing {
        get("fs/{path...}") {
            val path = call.parameters.getAll("path")?.reduce { a, b -> "$a/$b" }?.let { "/$it" } ?: "/"
            val response = fileSystemController.get(path, false)
            call.respond(response)
        }

        post("fs/{path...}") {
            val path = call.parameters.getAll("path")?.reduce { a, b -> "$a/$b" }?.let { "/$it" } ?: "/"
            call.receiveMultipart().readAllParts().forEach { part ->
                runCatching {
                    when (part) {
                        is PartData.FileItem -> {
                            fileSystemController.createFile(path, part.originalFileName!!, part.streamProvider().readAllBytes())
                        }
                        is PartData.FormItem -> {
                            println("form item ${part.value}")
                        }
                        is PartData.BinaryItem -> {
                            println("binary item")
                        }
                        is PartData.BinaryChannelItem -> {
                            println("binary channel")
                        }
                    }
                }
            }
            call.respondText("OK")
        }
    }
}

fun Application.serialization() {
    install(ContentNegotiation) {
        json(Json)
    }
}

fun Application.cors() {
    install(CORS) {
        anyHost()

        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Delete)

        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Allow)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.AccessControlAllowMethods)
        allowHeader(HttpHeaders.AccessControlAllowHeaders)
        allowHeader(HttpHeaders.AccessControlAllowCredentials)
        allowHeader(HttpHeaders.Accept)

        allowCredentials = true
    }
}
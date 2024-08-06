package ru.ttraum

import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.asSequence

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    routing {
        get("fs/{path...}") {
            val path = call.parameters.getAll("path")?.reduce { a, b -> "$a/$b" }?.let { "/$it" } ?: "/"
            if(File(path).isDirectory) {
                val paths = Files.list(Path.of(path)).asSequence().map { it.toAbsolutePath().toString() }
                    .reduce { a, b -> a + "\n" + b }
                call.respondText(paths)
            }else if(File(path).isFile) {
                val bytes = File(path).readBytes()
                call.respondBytes(bytes)
            }
        }
    }
}
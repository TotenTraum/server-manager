package ru.ttraum.server.controller

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.ttraum.shared.api.FileSystemApi
import ru.ttraum.shared.api.dto.GetFSResponse
import java.io.File
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.name
import kotlin.streams.asSequence

class FileSystemController : FileSystemApi {
    override suspend fun get(path: String, canGetFile: Boolean): GetFSResponse {
        val file = File(path)
        if (file.isDirectory) {
            val entries = withContext(Dispatchers.IO) {
                Files.list(Path.of(path))
            }.asSequence()
                .map { GetFSResponse.DirectoryEntry.EntryEntity(it.name, !it.isDirectory()) }
                .toList()
            return GetFSResponse.DirectoryEntry(entries)
        } else {
            val bytes = if (file.canRead()) file.readBytes() else byteArrayOf()
            return GetFSResponse.FileEntry(file.name, bytes, file.canRead())
        }
    }

    override suspend fun createFile(path: String, fileName: String, bytes: ByteArray, canRewriteFile: Boolean) {
        val file = Path.of(path, fileName).toFile()
        if (file.exists() && !canRewriteFile)
            throw FileAlreadyExistsException("Cannot create file $file")

        if (file.isDirectory)
            throw FileAlreadyExistsException("This file is already a directory")

        file.writeBytes(bytes)
    }
}
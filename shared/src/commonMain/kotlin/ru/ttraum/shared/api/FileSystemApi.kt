package ru.ttraum.shared.api

import ru.ttraum.shared.api.dto.GetFSResponse

interface FileSystemApi {
    /**
     *  get entry of a path
     */
    suspend fun get(path: String, canGetFile: Boolean): GetFSResponse

    suspend fun createFile(path: String, fileName: String, bytes: ByteArray, canRewriteFile: Boolean = false)
}
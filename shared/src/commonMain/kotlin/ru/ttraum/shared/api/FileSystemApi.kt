package ru.ttraum.shared.api

import ru.ttraum.shared.api.dto.GetFSResponse

interface FileSystemApi {
    /**
     *  get entry of a path
     */
    fun get(path: String, canGetFile: Boolean): GetFSResponse

    fun createFile(path: String, fileName: String, bytes: ByteArray, canRewriteFile: Boolean = false)
}
package ru.ttraum.shared.api.dto

import kotlinx.serialization.Serializable

@Serializable
@Suppress("unused")
sealed class GetFSResponse {
    @Serializable
    class DirectoryEntry(val entities: List<EntryEntity>) : GetFSResponse() {
        @Serializable
        class EntryEntity(val path: String, val isFile: Boolean)
    }

    @Serializable
    class FileEntry(val fileName: String, val bytes: ByteArray, val canAccess: Boolean) : GetFSResponse()
}
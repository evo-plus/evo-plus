package ru.dargen.evoplus.update

import com.google.gson.annotations.SerializedName
import ru.dargen.rest.annotation.RequestPath

@RequestPath("https://api.modrinth.com/v2/project/evoplus")
fun interface Modrinth {

    @RequestPath("version")
    fun fetchShortVersions(): List<VersionInfo>

    data class VersionInfo(
        val name: String,
        val changelog: String,
        @SerializedName("version_type") val channel: String,
        private val files: List<FileInfo>
    ) {

        val friendlyName get() = "§e${name.replace("-hf", " §cHOT")}"
        val file get() = files.first()
        val isHotFix get() = name.endsWith("hf")

        data class FileInfo(val url: String, val filename: String)

    }

}
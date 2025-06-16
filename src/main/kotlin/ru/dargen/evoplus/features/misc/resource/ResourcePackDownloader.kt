package ru.dargen.evoplus.features.misc.resource

import com.google.common.hash.Hashing.sha256
import ru.dargen.evoplus.Logger
import ru.dargen.evoplus.util.createDirectoriesIfNotExists
import ru.dargen.evoplus.util.rest.request
import java.io.InputStream
import java.net.http.HttpResponse.BodyHandlers
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.io.path.exists
import kotlin.io.path.outputStream

class ResourcePackDownloader(val url: String) {

    companion object {
        private val ServerResourcePacksFolder = Paths.get("server-resource-packs").createDirectoriesIfNotExists()
    }

    private var latestHash: String? = null

    var supplied = false
    var file = CompletableFuture<Path>()

    init {
        download()
    }

    fun supplySync(): Path {
        val file = file.get(15, TimeUnit.SECONDS)
        supplied = true
        return file
    }

    fun download() = request<InputStream>(url, "GET", handler = BodyHandlers.ofInputStream()).thenAccept {
        val hash = sha256().hashString(it.headers().firstValue("last-modified").get(), Charsets.UTF_8).toString()
        if (latestHash != null && hash == latestHash) {
            Logger.info("Resource pack up to date")
            it.body().close()
            return@thenAccept
        }

        val file = ServerResourcePacksFolder.resolve(hash)
        if (!file.exists()) {
            Logger.info("Downloading latest resource pack")
            file.outputStream().use(it.body()::transferTo)
        }
        it.body().close()

        Logger.info("Resource pack downloaded")
        latestHash = hash
        supplied = false

        if (!this.file.isDone) this.file.complete(file)
        else this.file = CompletableFuture.completedFuture(file)
    }

}
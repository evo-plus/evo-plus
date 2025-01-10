package ru.dargen.evoplus.resource.diamondworld

import com.google.common.hash.Hashing
import net.minecraft.resource.ResourcePack
import net.minecraft.resource.ResourcePackProfile
import net.minecraft.resource.ZipResourcePack
import ru.dargen.evoplus.feature.Features
import ru.dargen.evoplus.features.misc.MiscFeature
import ru.dargen.evoplus.resource.AbstractResourcePackProvider
import ru.dargen.evoplus.util.rest.request
import java.io.InputStream
import java.net.http.HttpResponse
import java.nio.file.Paths
import java.util.function.Consumer
import kotlin.io.path.exists
import kotlin.io.path.outputStream

class DiamondWorldPackProvider : AbstractResourcePackProvider("diamond-world", "DiamondWorld", "Server resource pack") {

    companion object {
        private const val URL = "https://files.diamondworld.pro/resourcePacks/DiamondWorld_Latest.zip"
    }

    override fun register(profileAdder: Consumer<ResourcePackProfile>) {
        if (Features.Initialized && MiscFeature.ResourcePackPreload) {
            super.register(profileAdder)
        }
    }

    override fun openPack(name: String): ResourcePack {
        val response = request<InputStream>(URL, "GET", handler = HttpResponse.BodyHandlers.ofInputStream()).join()
        val hash = Hashing.sha256().hashString(response.headers().firstValue("last-modified").get(), Charsets.UTF_8).toString()
        val file = Paths.get("server-resource-packs", hash)

        if (!file.exists()) file.outputStream().use(response.body()::transferTo)
        response.body().close()

        return ZipResourcePack(name, file.toFile(), true)
    }

}
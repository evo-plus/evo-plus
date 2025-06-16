package ru.dargen.evoplus.feature.render.highligh

import net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity
import net.minecraft.item.Items
import ru.dargen.evoplus.scheduler.scheduleEvery
import ru.dargen.evoplus.util.kotlin.KotlinOpens
import ru.dargen.evoplus.util.minecraft.WorldEntities
import ru.dargen.evoplus.util.minecraft.customModelData
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@KotlinOpens
abstract class DisplayHighlighter(val enabled: () -> Boolean, expire: Duration = 800.milliseconds) : Highlighter(expire) {

    protected val ids: List<Int> = emptyList()

    init {
        scheduleEvery(500, 500, unit = TimeUnit.MILLISECONDS) {
            if (enabled() && shouldProcess()) {
                WorldEntities.asSequence()
                    .filterIsInstance<ItemDisplayEntity>()
                    .filter { it.shouldRender() }
                    .forEach { it.createHighlight() }
            }
        }
    }

    fun ItemDisplayEntity.createHighlight() {
        createHighlight(x, y, z)
    }

    protected fun shouldProcess() = true

    protected fun ItemDisplayEntity.shouldRender() = customModelData?.let(ids::contains) == true

    protected val ItemDisplayEntity.customModelData
        get() = data?.itemStack?.takeIf { it.item === Items.PAPER }?.customModelData

}
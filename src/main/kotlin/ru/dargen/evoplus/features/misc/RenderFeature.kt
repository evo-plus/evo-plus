package ru.dargen.evoplus.features.misc

import net.minecraft.item.Items
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.event.resourcepack.ResourcePackProvidersEvent
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.misc.render.HealthBar
import ru.dargen.evoplus.resource.builtin.EvoPlusPackProvider
import ru.dargen.evoplus.util.format.fix
import ru.dargen.evoplus.util.selector.enumSelector
import ru.dargen.evoplus.util.selector.toSelector

object RenderFeature : Feature("render", "Визуализация", Items.REDSTONE) {

    val FullBright by settings.boolean("Полная яркость", true)
    val HighlightAvailableItems by settings.boolean("Подсветка доступных предметов", true)

    val HealthRender by settings.switcher("Режим отображения здоровья", enumSelector<HealthRenderMode>())

//    val HealthBarsRender by settings.boolean("Отображать полоску здоровья игроков", true) on { HealthBar::updateRender }
//    val HealthBarsY by settings.selector("Сдвиг полоски здоровья игроков", (0..50).toSelector()) { "${it?.div(10.0)?.fix(1)}" }
//    val HealthCountRender by settings.boolean("Отображать единицы здоровья игроков", true)

    val NoBlockParticles by settings.boolean("Отключение эффектов блока")
    val NoFire by settings.boolean("Отключение огня")
    val NoStrikes by settings.boolean("Отключение молний")
    val NoDamageShake by settings.boolean("Отключение покачивания камеры при ударе")
    val NoHandShake by settings.boolean("Отключение покачивания руки")
    val NoExcessHud by settings.boolean("Отключение ненужных элементов HUD", true)

    // фогост сказал убрать, т.к. она теперь интерактивная
//    val NoExpHud by settings.boolean("Отключение отрисовки опыта и его уровня", true)

    init {
//        HealthBar

        on<ResourcePackProvidersEvent> {
            providers.add(EvoPlusPackProvider())
        }

    }

    enum class HealthRenderMode(val displayName: String, val isDefaultHearts: Boolean = true) {

        DEFAULT("Обычный"),
        LONG("Удлинненный");

        override fun toString() = displayName

    }

}
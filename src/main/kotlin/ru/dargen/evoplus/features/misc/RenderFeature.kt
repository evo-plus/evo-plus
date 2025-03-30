package ru.dargen.evoplus.features.misc

import dev.evoplus.feature.setting.Settings.CategoryBuilder
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.misc.render.HealthBar

object RenderFeature : Feature("render", "Визуализация") {

    var FullBright = true
    var HighlightAvailableItems = true

    var NoBlockParticles = false
    var NoStrikes = false
    var NoFalling = false
    var NoDamageShake = false
    var NoHandShake = false
    var NoFire = false
    var NoExpHud = true
    var NoExcessHud = true

    var HealthRender = HealthRenderMode.DEFAULT

    override fun CategoryBuilder.setup() {
        include(HealthBar)

        switch(::FullBright, "Полная яркость", "Максимальная яркость освещения")
        switch(::HighlightAvailableItems, "Подсветка доступных предметов", "Подсвечивает доступные для получения предметы")
        selector(::HealthRender, "Режим отображения здоровья", "Выбор способа отображения здоровья")

        subcategory("render", "Отключение эффектов") {
            switch(::NoBlockParticles, "Эффекты блоков", "Убирает частицы разрушения блоков")
            switch(::NoStrikes, "Молнии", "Убирает эффект молний")
            switch(::NoFalling, "Падающие блоки", "Убирает эффект падающих блоков")
            switch(::NoDamageShake, "Покачивание камеры при ударе", "Убирает эффект покачивания камеры при ударе")
            switch(::NoHandShake, "Покачивание руки", "Убирает эффект покачивания руки")
            switch(::NoFire, "Огонь", "Убирает эффект огня на экране")
            switch(::NoExpHud, "Уровень и опыт", "Убирает отрисовку уровня и его опыта")
            switch(::NoExcessHud, "Ненужные элементы HUD", "Убирает все лишние элементы HUD")
        }
    }

    enum class HealthRenderMode(val displayName: String, val isDefaultHearts: Boolean = true) {

        DEFAULT("Обычный"),
        LONG("Удлинненный");

        override fun toString() = displayName

    }

}
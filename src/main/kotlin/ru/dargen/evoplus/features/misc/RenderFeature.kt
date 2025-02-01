package ru.dargen.evoplus.features.misc

import dev.evoplus.feature.setting.Settings.CategoryBuilder
import net.minecraft.item.Items
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.misc.render.HealthBars

object RenderFeature : Feature("render", "Визуализация") {

    var FullBright = true
    var HighlightAvailableItems = true

    var NoBlockParticles = false
    var NoFire = false 
    var NoStrikes = false
    var NoFalling = false
    var NoDamageShake = false
    var NoHandShake = false
    var NoExcessHud = true 
    var NoExpHud = true
    var NoScoreboardNumbers = true

    var HealthBarsRender = true
    var HealthRender = HealthRenderMode.DEFAULT
    var HealthBarsY = 0f
    var HealthCountRender = true

    override fun CategoryBuilder.setup() {
        switch(::FullBright, "Полная яркость", "Максимальная яркость освещения")
        switch(::HighlightAvailableItems, "Подсветка доступных предметов", "Подсвечивает доступные для получения предметы")

        subcategory("render", "Отключение эффектов") {
            switch(::NoBlockParticles, "Эффекты блоков", "Убирает частицы разрушения блоков")
            switch(::NoStrikes, "Молний", "Убирает эффект молний")
            switch(::NoFalling, "Падающие блоки", "Убирает эффект падающих блоков")
            switch(::NoDamageShake, "Покачивание камеры при ударе", "Убирает эффект покачивания камеры при ударе")
            switch(::NoHandShake, "Покачивание руки", "Убирает эффект покачивания руки")
            switch(::NoFire, "Огонь", "Убирает эффект огня на экране")
            switch(::NoExpHud, "Уровень и опыт", "Убирает отрисовку уровня и его опыта")
            switch(::NoScoreboardNumbers, "Нумерация скорборда", "Убирает нумерацию скорборда")
            switch(::NoExcessHud, "Ненужные элементы HUD", "Убирает все лишние элементы HUD")
        }

        subcategory("health-bar", "Полоска здоровья") {
            switch(::HealthBarsRender, "Полоска здоровья игроков", "Показывает полоску здоровья над игроками", action = HealthBars::updateRender)
            selector(::HealthRender, "Режим отображения здоровья", "Выбор способа отображения здоровья")
            decimal(::HealthBarsY, "Сдвиг полоски здоровья игроков", "Регулировка высоты полоски здоровья над игроком", range = 0f..5f)
            switch(::HealthCountRender, "Здоровья игроков", "Показывает числовое значение здоровья над игроком")
        }
    }

    override fun initialize() {
        HealthBars
    }

    enum class HealthRenderMode(val displayName: String, val isDefaultHearts: Boolean = true) {

        DEFAULT("Обычный"),
        LONG("Удлинненный");

        override fun toString() = displayName

    }

}
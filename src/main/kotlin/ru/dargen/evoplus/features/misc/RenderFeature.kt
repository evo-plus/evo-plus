package ru.dargen.evoplus.features.misc

import net.minecraft.item.Items
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.feature.vigilant.FeatureCategory
import ru.dargen.evoplus.feature.vigilant.enumSelector
import ru.dargen.evoplus.features.misc.render.HealthBars

object RenderFeature : Feature("render", "Визуализация", Items.REDSTONE) {

    var FullBright = true
    var HealthRender = HealthRenderMode.DEFAULT
    
    var HealthBarsRender = true
    var HealthBarsY = 0f
    var HealthCountRender = true

    var NoBlockParticles = false
    var NoFire = false 
    var NoStrikes = false
    var NoFalling = false
    var NoDamageShake = false
    var NoHandShake = false
    var NoExcessHud = true 
    var NoExpHud = true
    var NoScoreboardNumbers = true
    var HighlightAvailableItems = true


    override fun FeatureCategory.setup() {
        switch(::FullBright, "Полная яркость", "Максимальная яркость освещения")
        enumSelector(::HealthRender, "Режим отображения здоровья", "Выбор способа отображения здоровья")

        switch(::HighlightAvailableItems, "Подсветка доступных предметов", "Показывает подсветку доступных предметов")

        subcategory("Отключение рендера") {
            switch(::NoBlockParticles, "Отключение эффектов блока", "Убирает частицы разрушения блоков")
            switch(::NoStrikes, "Отключение молний", "Убирает эффект молний")
            switch(::NoFalling, "Отключение падающих блоков", "Убирает эффект падающих блоков")
            switch(::NoDamageShake, "Отключение покачивания камеры при ударе", "Убирает эффект покачивания камеры при ударе")
            switch(::NoHandShake, "Отключение покачивания руки", "Убирает эффект покачивания руки")

            switch(::NoFire, "Отключение огня", "Убирает эффект огня на экране")
            switch(::NoExpHud, "Отключение отрисовки опыта и его уровня", "Убирает эффект отрисовки опыта и его уровня")
            switch(::NoScoreboardNumbers, "Отключение нумерации скорборда", "Убирает нумерацию скорборда")
            switch(::NoExcessHud, "Отключение ненужных элементов HUD", "Убирает все лишние элементы HUD")
        }

        subcategory("Полоска здоровья") {
            switch(::HealthBarsRender, "Отображать полоску здоровья игроков", "Показывает полоску здоровья над игроками", action = HealthBars::updateRender)
            decimalSlider(::HealthBarsY, "Сдвиг полоски здоровья игроков", "Регулировка высоты полоски здоровья", min = 0F, max = 5f)
            switch(::HealthCountRender, "Отображать единицы здоровья игроков", "Показывает числовое значение здоровья")
        }
    }

    init {
        HealthBars
    }

    enum class HealthRenderMode(val displayName: String, val isDefaultHearts: Boolean = true) {

        DEFAULT("Обычный"),
        LONG("Удлинненный");

        override fun toString() = displayName

    }

}
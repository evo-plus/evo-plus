package ru.dargen.evoplus.features.game

import dev.evoplus.feature.setting.Settings.CategoryBuilder
import dev.evoplus.feature.setting.property.subscription
import net.minecraft.entity.Entity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.item.Items
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.feature.widget.widget
import ru.dargen.evoplus.features.game.widget.GoldenCrystalWidget
import ru.dargen.evoplus.features.misc.notify.NotifyWidget
import ru.dargen.evoplus.render.node.text
import ru.dargen.evoplus.scheduler.scheduleEvery
import ru.dargen.evoplus.util.math.scale
import ru.dargen.evoplus.util.minecraft.WorldEntities
import ru.dargen.evoplus.util.minecraft.customItem
import ru.dargen.evoplus.util.minecraft.equalCustomModel
import ru.dargen.evoplus.util.minecraft.printMessage


val GoldenCrystalItem = customItem(Items.PAPER, 271)

object GoldenRushFeature : Feature("golden-rush", "Золотой Кристалл") {

    var GoldenCrystalEntity: Entity? = null
        set(value) {
            field = value
            field?.isGlowing = GoldenCrystalGlowing
        }
    var GoldenCrystalNotify = false
    var GoldenCrystalMessage = true
    var GoldenCrystalGlowing = false

    var GoldenCrystalAround = false
        set(value) {
            field = value
            GoldenCrystalIndicatorText.text = if (value) "§a✔" else "§c❌"
        }
    val GoldenCrystalIndicatorText = text {
        isShadowed = true
        scale = scale(1.2, 1.2)
    }

    override fun CategoryBuilder.setup() {
        widget("golden-crystal-widget", "Золотой Кристалл", GoldenCrystalWidget)
        switch(::GoldenCrystalNotify, "Уведомление",
            "Уведомлять при появлении золотого кристалла")
        switch(::GoldenCrystalMessage, "Сообщение",
            "Отправлять сообщение в чат при появлении золотого кристалла")
        switch(::GoldenCrystalGlowing, "Подсветка",
            "Подсвечивать золотой кристалл") { GoldenCrystalEntity?.isGlowing = it }.subscription()
    }

    override fun initialize() {
        scheduleEvery(period = 10) {
            WorldEntities
                .filterIsInstance<ArmorStandEntity>()
                .find { stand -> stand.armorItems.any { it.equalCustomModel(GoldenCrystalItem) } }
                .also {
                    GoldenCrystalEntity = it
                    val previousGoldenCrystalAround = GoldenCrystalAround
                    GoldenCrystalAround = it != null

                    if (previousGoldenCrystalAround || !GoldenCrystalAround) return@scheduleEvery

                    val text = "§eВозле вас обнаружен Золотой Кристалл"
                    if (GoldenCrystalNotify) NotifyWidget.showText(text)
                    if (GoldenCrystalMessage) printMessage(text)
                }
        }
    }
}
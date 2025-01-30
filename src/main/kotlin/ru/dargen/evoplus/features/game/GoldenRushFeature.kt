package ru.dargen.evoplus.features.game

import net.minecraft.client.render.DiffuseLighting
import net.minecraft.entity.Entity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.item.Items
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.feature.vigilant.FeatureCategory
import ru.dargen.evoplus.features.misc.notify.NotifyWidget
import ru.dargen.evoplus.render.node.box.hbox
import ru.dargen.evoplus.render.node.item
import ru.dargen.evoplus.render.node.postTransform
import ru.dargen.evoplus.render.node.preTransform
import ru.dargen.evoplus.render.node.text
import ru.dargen.evoplus.scheduler.scheduleEvery
import ru.dargen.evoplus.util.math.scale
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.WorldEntities
import ru.dargen.evoplus.util.minecraft.customItem
import ru.dargen.evoplus.util.minecraft.equalCustomModel
import ru.dargen.evoplus.util.minecraft.printMessage


private val GoldenCrystalItem = customItem(Items.PAPER, 271)

object GoldenRushFeature : Feature("golden-rush", "Золотой Кристалл", GoldenCrystalItem) {

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
    val GoldenCrystalWidget by widgets.widget("Золотой Кристалл", "golden-crystal", false) {
        align = v3(.95, .26)
        +hbox {
            indent = v3()
            space = 3.0
            
            +item(GoldenCrystalItem) {
                scale = scale(.5, .5, .5)
                rotation = v3(y = 50.0)
                translation = v3(x = 12.0)

                preTransform { matrices, tickDelta -> DiffuseLighting.disableGuiDepthLighting() }
                postTransform { matrices, tickDelta -> DiffuseLighting.enableGuiDepthLighting() }
            }
            +GoldenCrystalIndicatorText
        }
    }

    override fun FeatureCategory.setup() {
        switch(::GoldenCrystalNotify, "Уведомление",
            "Уведомлять при появлении кристалла")
        switch(::GoldenCrystalMessage, "Сообщение",
            "Отправлять сообщение в чат при появлении кристалла")
        switch(::GoldenCrystalGlowing, "Подсветка",
            "Подсвечивать золотой кристалл") { GoldenCrystalEntity?.isGlowing = it }
    }

    init {
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
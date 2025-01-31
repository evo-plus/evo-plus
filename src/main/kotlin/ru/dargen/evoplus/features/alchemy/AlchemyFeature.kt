package ru.dargen.evoplus.features.alchemy

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvents
import ru.dargen.evoplus.event.inventory.InventoryClickEvent
import ru.dargen.evoplus.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.feature.vigilant.FeatureCategory
import ru.dargen.evoplus.features.alchemy.recipe.PotionRecipe
import ru.dargen.evoplus.mixin.render.hud.BossBarHudAccessor
import ru.dargen.evoplus.render.Relative
import ru.dargen.evoplus.render.context.Overlay.unaryPlus
import ru.dargen.evoplus.render.node.text
import ru.dargen.evoplus.scheduler.scheduleEvery
import ru.dargen.evoplus.util.kotlin.cast
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.*

object AlchemyFeature : Feature("alchemy", "Алхимия", Items.BREWING_STAND) {

    private val AlchemyPotionListTitle = "넉"
    private val AlchemyTimePattern = "Время: ([.\\d]+)с".toRegex()

    var PotionRecipe: PotionRecipe? = null

    var IngredientHighlight = true
    var BrewingAlertDelay = 1000
    var SoundAlert = false

    val RecipeText = text("Закрепите рецепт нажатием ПКМ в меню")
    val RecipeWidget by widgets.widget("Рецепт зелья", "recipe", enabled = false) {
        align = Relative.LeftCenter
        origin = Relative.LeftCenter
        +RecipeText
    }
    val AlertText = +text {
        enabled = false

        align = Relative.CenterTop
        origin = Relative.CenterTop
        scale = v3(3.5, 3.5, 3.5)

        translation.y += 100
    }

    override fun FeatureCategory.setup() {
        switch(::IngredientHighlight, "Подсветка ингредиентов", "Подсвечивает ингредиенты алхимии на локации")
        slider(::BrewingAlertDelay, "Время задержки перед оповещением",
            "Задержка перед оповещением при варке зелья (мс)", min = 100, max = 2000)
        switch(::SoundAlert, "Звук оповещения", "Проигрывать звук при оповещении")
    }

    init {
        AlchemyIngredientHighlight
        on<InventoryClickEvent> {
//            if (!RecipeWidget.enabled || button != 1) return@on

            val screen = CurrentScreen as? GenericContainerScreen ?: return@on
            val title = screen.title.string.uncolored()

            if (AlchemyPotionListTitle !in title) return@on

            val itemStack = CurrentScreenHandler?.getSlot(slot)?.stack ?: return@on

            val stringifyLore = itemStack.lore.map { it.string }

            if (stringifyLore.none { "Рецепт" in it }) return@on

            stringifyLore
                .dropWhile { "Рецепт" !in it }
                .dropLastWhile { "Ваш уровень мастерства" !in it }
                .drop(1)
                .dropLast(2)
                .also {
                    RecipeText.lines = listOf("§a${itemStack.name.string}:", *it.toTypedArray())
                    PotionRecipe = PotionRecipe(it.drop(1).dropLast(1))
                }
        }

        scheduleEvery(period = 10) {
            val time = Client?.inGameHud?.bossBarHud?.cast<BossBarHudAccessor>()?.bossBars?.values
                ?.map { it.name.string.uncolored().trim() }
                ?.filter { it.isNotEmpty() }
                ?.firstNotNullOfOrNull {
                    AlchemyTimePattern.find(it)
                        ?.groupValues
                        ?.getOrNull(1)
                        ?.toDoubleOrNull()
                } ?: return@scheduleEvery

            val nearestAlert = PotionRecipe?.getNearestAlert(BrewingAlertDelay / 1000.0, time) ?: run {
                AlertText.enabled = false
                return@scheduleEvery
            }

            AlertText.enabled = true
            AlertText.lines = listOf("§c$nearestAlert")

            if (SoundAlert) repeat(5) { Player?.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f) }
        }
    }
}
package ru.dargen.evoplus.core

import gg.essential.api.EssentialAPI
import gg.essential.elementa.state.v2.ReferenceHolder
import gg.essential.elementa.unstable.state.v2.effect
import gg.essential.elementa.utils.withAlpha
import gg.essential.universal.UChat
import gg.essential.universal.UDesktop
import gg.essential.universal.UKeyboard
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.*
import gg.skytils.skytilsmod.Reference
import gg.skytils.skytilsmod.Skytils
import gg.skytils.skytilsmod.Skytils.mc
import gg.skytils.skytilsmod.features.impl.dungeons.catlas.core.CatlasConfig
import gg.skytils.skytilsmod.features.impl.handlers.CommandAliases
import gg.skytils.skytilsmod.features.impl.trackers.Tracker
import gg.skytils.skytilsmod.gui.features.PotionNotificationsGui
import gg.skytils.skytilsmod.gui.features.ProtectItemGui
import gg.skytils.skytilsmod.gui.features.SpiritLeapNamesGui
import gg.skytils.skytilsmod.utils.ModChecker
import gg.skytils.skytilsmod.utils.SuperSecretSettings
import gg.skytils.skytilsmod.utils.Utils
import gg.skytils.skytilsmod.utils.startsWithAny
import gg.skytils.skytilsws.client.WSClient
import gg.skytils.vigilance.property
import net.minecraft.util.Identifier
import ru.dargen.evoplus.EvoPlus
import ru.dargen.evoplus.features.clicker.ClickerButton
import ru.dargen.evoplus.features.clicker.ClickerMode
import ru.dargen.evoplus.features.fishing.widget.FishingValueWidgetVisibleMode
import ru.dargen.evoplus.features.fishing.widget.FishingWidgetVisibleMode
import ru.dargen.evoplus.features.fishing.widget.quest.FishingWidgetQuestDescriptionMode
import ru.dargen.evoplus.features.fishing.widget.quest.FishingWidgetQuestMode
import ru.dargen.evoplus.features.misc.discord.DiscordLocationFormat
import ru.dargen.evoplus.features.misc.discord.DiscordNameFormat
import ru.dargen.evoplus.features.misc.selector.FastSelectorScreen
import ru.dargen.evoplus.render.Colors
import java.awt.Color
import java.io.File
import java.net.URI

object Config : Vigilant(
    File("./config/evo-plus/config.toml"),
    "EvoPlus (${EvoPlus.Version})"
) {
    // Alchemy
    @Property(
        type = PropertyType.SWITCH,
        name = "Подсветка ингредиентов",
        description = "Подсвечивает ингредиенты алхимии на локации",
        category = "Алхимия",
        subcategory = "Подсветка"

    )
    var ingredientHighlight = true

    @Property(
        type = PropertyType.SLIDER,
        name = "Время задержки перед оповещением",
        description = "Задержка перед оповещением при варке зелья (мс)",
        category = "Алхимия",
        subcategory = "Настройки оповещений",
        min = 100,
        max = 2000
    )
    var brewingAlertDelay = 1000

    @Property(
        type = PropertyType.SWITCH,
        name = "Звук оповещения",
        description = "Проигрывать звук при оповещении",
        category = "Алхимия",
        subcategory = "Настройки оповещений"
    )
    var soundAlert = false


    // Bosses
    @Property(
        type = PropertyType.SWITCH,
        name = "Захват босса",
        description = "Уведомляет о захвате боссов",
        category = "Боссы",
        subcategory = "Оповещения"
    )
    var notifyCapture = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Проклятие босса",
        description = "Отправляет сообщение о проклятии босса в клановый чат",
        category = "Боссы",
        subcategory = "Оповещения"
    )
    var curseMessage = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Процент здоровья босса",
        description = "Отправляет сообщение об определённом проценте здоровья босса в клановый чат",
        category = "Боссы",
        subcategory = "Оповещения"
    )
    var bossLowHealthsMessage = false

    @Property(
        type = PropertyType.SLIDER,
        name = "Оповещать о здоровье босса",
        description = "Процент здоровья босса, при котором отправляется сообщение в клановый чат",
        category = "Боссы",
        subcategory = "Настройки оповещений здоровья босса",
        min = 10,
        max = 90
    )
    var bossHealthsPercent = 50

    @Property(
        type = PropertyType.SLIDER,
        name = "Частота оповещений о здоровье босса",
        description = "Частота отправки сообщения о здоровье босса в клановый чат (в секундах)",
        category = "Боссы",
        subcategory = "Настройки оповещений здоровья босса",
        min = 5,
        max = 60
    )
    var bossHealthsCooldown = 15


    // Clan
    @Property(
        type = PropertyType.SWITCH,
        name = "К.О. для захвата босса в меню",
        description = "Отображает количество К.О. для захвата босса в меню",
        category = "Клан",
        subcategory = "Визуализация"
    )
    var inlineMenuClanScores = true


    // Clicker
//    @Property(
//        type = PropertyType.BUTTON,
//        name = "Клавиша бинда",
//        description = "Клавиша, которая включает/выключает кликер",
//        category = "Кликер",
//        subcategory = "Настройки бинда",
//        placeholder = "Нажмите клавишу для бинда"
//    )
//    var clickerBind = UKeyboard.KEY_Z

    @Property(
        type = PropertyType.SELECTOR,
        name = "Режим работы",
        description = "Выбор режима работы кликера",
        category = "Кликер",
        subcategory = "Настройки кликера",
        options = [ "Удар", "Удержание" ]
    )
    var clickerMode = 0

    @Property(
        type = PropertyType.SELECTOR,
        name = "Кнопка кликера",
        description = "Выбор кнопки мыши кликера",
        category = "Кликер",
        subcategory = "Настройки кликера",
        options = [ "ЛКМ", "ПКМ" ]
    )
    var clickerButton = 0

    @Property(
        type = PropertyType.SLIDER,
        name = "КПС",
        description = "Определённое значение кликов в секунду",
        category = "Кликер",
        subcategory = "Настройки кликера",
        min = 1,
        max = 20
    )
    var clickerCPS = 10


    // Dungeon
    @Property(
        type = PropertyType.SWITCH,
        name = "Подсветка декораций",
        description = "Подсвечивает разрушаемые декорации в данже",
        category = "Данжи",
        subcategory = "Подсветка"
    )
    var dungeonDecorationHighlight = true


    // ESP
    @Property(
        type = PropertyType.COLOR,
        name = "Подсвечивание лаки-блоков",
        description = "Подсвечивает лаки-блоки в шахтах",
        category = "Подсветка"
    )
    var luckyBlockColor = Colors.Red

    @Property(
        type = PropertyType.COLOR,
        name = "Подсвечивание осколков",
        description = "Подсвечивает золотые и алмазные осколки на шахтах и боссах",
        category = "Подсветка"
    )
    var shardsColor = Colors.Deepskyblue

    @Property(
        type = PropertyType.COLOR,
        name = "Подсвечивание бочек",
        description = "Подсвечивает бочки в шахтах",
        category = "Подсветка"
    )
    var barrelsColor = Colors.Green


    // Fishing
    @Property(
        type = PropertyType.SWITCH,
        name = "Подсветка точек клёва",
        description = "Подсвечивает точки клёва на локации",
        category = "Рыбалка",
    )
    var spotsHighlight = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Уведомления о повышенном клёве",
        description = "Уведомляет о повышенном клёве на локациях",
        category = "Рыбалка",
    )
    var higherBitingNotify = true

    @Property(
        type = PropertyType.SLIDER,
        name = "Автоматическая удочка",
        description = "Автоматически подбирает удочку (тик = 50 мс)",
        category = "Рыбалка",
        min = -1,
        max = 40
    )
    var autoHookDelay = 1

    @Property(
        type = PropertyType.SELECTOR,
        name = "Отображение квестов",
        description = "Отображает виджет квестов рыбалки при определённых условиях",
        category = "Рыбалка",
        subcategory = "Настройки виджетов",
        options = [ "Всегда", "На рыбалке", "С удочкой" ]
    )
    var questsProgressVisibleMode = 0

    @Property(
        type = PropertyType.SELECTOR,
        name = "Отображаемый тип квестов",
        description = "Отображает задания рыбалки при определённых условиях",
        category = "Рыбалка",
        subcategory = "Настройки виджетов",
        options = [ "Всегда", "При наведении", "На рыбалке", "С удочкой" ]
    )
    var questsProgressMode = 0

    @Property(
        type = PropertyType.SELECTOR,
        name = "Отображение описания квестов",
        description = "Отображает описание заданий рыбалки при определённых условиях",
        category = "Рыбалка",
        subcategory = "Настройки виджетов",
        options = [ "Всегда", "При наведении", "На рыбалке", "С удочкой" ]
    )
    var questsProgressDescriptionMode = 0

    @Property(
        type = PropertyType.SELECTOR,
        name = "Клёв на территориях",
        description = "Отображает виджет процента клёва на локациях при определённых условиях",
        category = "Рыбалка",
        subcategory = "Настройки виджетов",
        options = [ "Всегда", "На рыбалке", "С удочкой" ]
    )
    var nibblesVisibleMode = 0

    @Property(
        type = PropertyType.SELECTOR,
        name = "Отображение опыта и калорийности рыбы",
        description = "Отображает виджеты количества опыта и калорийности рыбы",
        category = "Рыбалка",
        subcategory = "Настройки виджетов",
        options = [ "Всегда", "Инвентарь" ]
    )
    var valueVisibleMode = 0


    // Game
    @Property(
        type = PropertyType.SWITCH,
        name = "Уведомление",
        description = "Уведомлять при появлении золотого кристалла",
        category = "Золотой кристалл"
    )
    var goldenCrystalNotify = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Сообщение",
        description = "Отправлять сообщение в чат при появлении золотого кристалла",
        category = "Золотой кристалл"
    )
    var goldenCrystalMessage = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Подсветка",
        description = "Подсвечивать золотой кристалл",
        category = "Золотой кристалл"
    )
    var goldenCrystalGlowing = false


    // Discord RPC
    @Property(
        type = PropertyType.SWITCH,
        name = "Отображение",
        description = "Управляет отображением статуса в Discord",
        category = "Discord RPC"
    )
    var discordRPCEnabled = true

    @Property(
        type = PropertyType.SELECTOR,
        name = "Имя",
        description = "Вид отображения имени",
        category = "Discord RPC"
    )
    var discordNameStrategy = 2

    @Property(
        type = PropertyType.SELECTOR,
        name = "Местоположение",
        description = "Вид отображения местоположения",
        category = "Discord RPC"
    )
    var discordLocationStrategy = 2

    @Property(
        type = PropertyType.SELECTOR,
        name = "Местоположение при наведении",
        description = "Вид отображения местоположения при наведении",
        category = "Discord RPC"
    )
    var discordLocationHoverStrategy = 2


    // Health Bar
    @Property(
        type = PropertyType.SWITCH,
        name = "Отображение",
        description = "Показывает полоску здоровья над игроками",
        category = "Индикатор здоровья"
    )
    var healthBarEnabled = true

    @Property(
        type = PropertyType.DECIMAL_SLIDER,
        name = "Высота полоски",
        description = "Высота полоски здоровья над игроками",
        category = "Индикатор здоровья",
        minF = 1f,
        maxF = 20f
    )
    var healthBarHeight = 0f

    @Property(
        type = PropertyType.SWITCH,
        name = "Здоровье игрока",
        description = "Показывает числовое значение здоровья над игроком",
        category = "Индикатор здоровья"
    )
    var healthBarShowHealth = true

    @Property(
        type = PropertyType.SELECTOR,
        name = "Режим отображения здоровья",
        description = "Выбор способа отображения здоровья",
        category = "Индикатор здоровья"
    )
    var healthBarRenderMode = 0


    // Misc
    @Property(
        type = PropertyType.SWITCH,
        name = "Fast-селектор",
        description = "Включение/отключение открытия Fast-селектора по нажатию на клавишу",
        category = "Прочее",
        subcategory = "Fast-селектор"
    )
    var fastSelector = true

    @Property(
        type = PropertyType.BUTTON,
        name = "Настройка Fast-селектора",
        description = "Настройка предметов, которые будут отображаться в Fast-селекторе",
        category = "Прочее",
        subcategory = "Fast-селектор",
        placeholder = "Настроить"
    )
    @Suppress("unused")
    fun openFastSelectorSettings() {
        FastSelectorScreen.open(true)
    }

    @Property(
        type = PropertyType.SWITCH,
        name = "Авто-спринт",
        description = "Включение автоматического спринта",
        category = "Прочее",
        subcategory = "Автоматические функции"
    )
    fun toggleAutoSprint() {
        if (!autoSprint) mc.player?.isSprinting = false
    }


}

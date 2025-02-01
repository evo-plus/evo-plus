package dev.evoplus.feature.setting

import com.google.gson.GsonBuilder
import dev.evoplus.feature.setting.gui.SettingsGui
import dev.evoplus.feature.setting.gui.SettingsGuiPreferences
import dev.evoplus.feature.setting.property.Category
import dev.evoplus.feature.setting.property.EmptyPropertyValue
import dev.evoplus.feature.setting.property.KPropertyValue
import dev.evoplus.feature.setting.property.Property
import dev.evoplus.feature.setting.property.PropertyMeta
import dev.evoplus.feature.setting.property.PropertyType
import dev.evoplus.feature.setting.property.PropertyValue
import dev.evoplus.feature.setting.property.attr.ButtonPropertyAttr
import dev.evoplus.feature.setting.property.attr.ColorPropertyAttr
import dev.evoplus.feature.setting.property.attr.DecimalPropertyAttr
import dev.evoplus.feature.setting.property.attr.FloatRange
import dev.evoplus.feature.setting.property.attr.NumberPropertyAttr
import dev.evoplus.feature.setting.property.attr.SelectorPropertyAttr
import dev.evoplus.feature.setting.property.attr.TextPropertyAttr
import dev.evoplus.feature.setting.property.data.CategoryData
import dev.evoplus.feature.setting.utils.SettingsFile
import gg.essential.universal.UI18n
import java.awt.Color
import java.nio.file.Path
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.reflect.KMutableProperty0

internal val PropertyGson = GsonBuilder().setPrettyPrinting().create()

abstract class Settings(
    file: Path,
    val name: String = "Settings",
    val logger: Logger = Logger.getLogger("Settings"),
) {

    internal val localizedName get() = UI18n.i18n(name)
    internal val guiPreferences = SettingsGuiPreferences()

    val categories = mutableMapOf<String, Category>()
    val enabledCategories get() = categories.filterNot { it.value.meta.hidden }

    val totalProperties
        get() = categories.values.flatMap { it.properties.values + it.categories.values.flatMap { it.properties.values } }
    val totalEnableProperties
        get() = enabledCategories.values.flatMap { it.properties.values + it.categories.values.flatMap { it.properties.values } }

    private val file = SettingsFile(file, this)

    fun initialize() {
        file.initialize()
        guiPreferences.selected = enabledCategories.keys.first()

        totalEnableProperties.forEach { property ->
            runCatching {
                property.callObserver(true)
            }.onFailure {
                logger.log(Level.SEVERE, "Error while observe init of ${property.id}", it)
            }
        }
    }

    internal fun markDirty() {
        file.dirty = true
    }

    fun gui() = SettingsGui(this)

    internal fun getCategoriesData() = enabledCategories
        .mapValues { it.value.createData(guiPreferences.subscription) }
        .filter { it.value.isNotEmpty }

    internal fun searchProperties(term: String) = CategoryData(
        PropertyMeta(""), enabledCategories.values.flatMap {
            it.searchProperties(term).filter { guiPreferences.subscription || !it.meta.subscription }.run {
                if (isEmpty()) emptyList()
                else listOf(it.createDividerItem()) + map(Property<*, *>::createItem)
            }
        }
    )

    fun category(
        id: String,
        name: String, description: String? = null,
        hidden: Boolean = false, subscription: Boolean = false,
        block: CategoryBuilder.() -> Unit,
    ): Category {
        val category = CategoryBuilder(id, PropertyMeta(name, description, hidden, subscription)).apply(block).build()
        categories[id] = category
        return category
    }

    inner class CategoryBuilder(
        private val id: String,
        private val meta: PropertyMeta,
        private val root: Boolean = true,
    ) {

        private val properties = mutableListOf<Property<*, *>>()
        private val categories by lazy { mutableMapOf<String, CategoryBuilder>() }

        internal fun build(): Category {
            return Category(
                id, meta,
                properties.associateBy { it.id },
                if (root) categories.mapValues { it.value.build() } else emptyMap()
            )
        }

        fun subcategory(
            id: String,
            name: String, description: String? = null,
            hidden: Boolean = false, subscription: Boolean = false,
            block: CategoryBuilder.() -> Unit,
        ) {
            require(root) { "Only root categories support subcategories yet" }
            categories[id] =
                CategoryBuilder(id, PropertyMeta(name, description, hidden, subscription), false).apply(block)
        }

        fun <T, A> property(
            value: PropertyValue<T>,
            id: String?, meta: PropertyMeta,
            type: PropertyType<T, A>, attr: A,
            observeInit: Boolean = true, action: (T) -> Unit = {},
        ): Property<T, A> {
            val id = id ?: value.id
            return Property(
                id, type, meta,
                attr, value,
                type.createSerializer(value, attr),
                { action(it) }, observeInit,
                this@Settings
            ).apply(properties::add)
        }

        fun <T, A> property(
            value: PropertyValue<T>,
            id: String?, name: String, description: String?,
            hidden: Boolean = false, subscription: Boolean = false,
            type: PropertyType<T, A>, attr: A,
            observeInit: Boolean = true, action: (T) -> Unit = {},
        ) = property(value, id, PropertyMeta(name, description, hidden, subscription), type, attr, observeInit, action)

        fun <T, A> property(
            value: KMutableProperty0<T>,
            id: String?, name: String, description: String?,
            hidden: Boolean = false, subscription: Boolean = false,
            type: PropertyType<T, A>, attr: A,
            observeInit: Boolean = true, action: (T) -> Unit = {},
        ) = property(
            KPropertyValue(value),
            id, name, description, hidden, subscription,
            type, attr, observeInit, action
        )

        fun checkbox(
            field: KMutableProperty0<Boolean>,
            name: String, description: String? = null, id: String? = null,
            hidden: Boolean = false, subscription: Boolean = false,
            observeInit: Boolean = true, action: (Boolean) -> Unit = {},
        ) = property(
            value = field, id = id,
            name = name, description = description,
            hidden = hidden, subscription = subscription,
            type = PropertyType.CheckBox, attr = null,
            observeInit = observeInit, action = action
        )

        fun switch(
            field: KMutableProperty0<Boolean>,
            name: String, description: String? = null, id: String? = null,
            hidden: Boolean = false, subscription: Boolean = false,
            observeInit: Boolean = true, action: (Boolean) -> Unit = {},
        ) = property(
            value = field, id = id,
            name = name, description = description,
            hidden = hidden, subscription = subscription,
            type = PropertyType.Switch, attr = null,
            observeInit = observeInit, action = action
        )

        fun text(
            field: KMutableProperty0<String>,
            name: String, description: String? = null, id: String? = null,
            hidden: Boolean = false, subscription: Boolean = false,
            placeholder: String = "", protected: Boolean = false,
            observeInit: Boolean = true, action: (String) -> Unit = {},
        ) = property(
            value = field, id = id,
            name = name, description = description,
            hidden = hidden, subscription = subscription,
            type = PropertyType.Text, attr = TextPropertyAttr(placeholder, protected),
            observeInit = observeInit, action = action
        )

        fun paragraph(
            field: KMutableProperty0<String>,
            name: String, description: String? = null, id: String? = null,
            hidden: Boolean = false, subscription: Boolean = false,
            placeholder: String = "", protected: Boolean = false,
            observeInit: Boolean = true, action: (String) -> Unit = {},
        ) = property(
            value = field, id = id,
            name = name, description = description,
            hidden = hidden, subscription = subscription,
            type = PropertyType.Text, attr = TextPropertyAttr(placeholder, protected),
            observeInit = observeInit, action = action
        )

        fun percent(
            field: KMutableProperty0<Float>,
            name: String, description: String? = null, id: String? = null,
            hidden: Boolean = false, subscription: Boolean = false,
            observeInit: Boolean = true, action: (Float) -> Unit = {},
        ) = property(
            value = field, id = id,
            name = name, description = description,
            hidden = hidden, subscription = subscription,
            type = PropertyType.PercentSlider, attr = null,
            observeInit = observeInit, action = action
        )

        fun slider(
            field: KMutableProperty0<Int>,
            name: String, description: String? = null, id: String? = null,
            hidden: Boolean = false, subscription: Boolean = false,
            range: IntProgression = 0..100,
            observeInit: Boolean = true, action: (Int) -> Unit = {},
        ) = property(
            value = field, id = id,
            name = name, description = description,
            hidden = hidden, subscription = subscription,
            type = PropertyType.Slider, attr = NumberPropertyAttr(range),
            observeInit = observeInit, action = action
        )

        fun number(
            field: KMutableProperty0<Int>,
            name: String, description: String? = null, id: String? = null,
            hidden: Boolean = false, subscription: Boolean = false,
            range: IntProgression = 0..100,
            observeInit: Boolean = true, action: (Int) -> Unit = {},
        ) = property(
            value = field, id = id,
            name = name, description = description,
            hidden = hidden, subscription = subscription,
            type = PropertyType.Slider, attr = NumberPropertyAttr(range),
            observeInit = observeInit, action = action
        )

        fun decimal(
            field: KMutableProperty0<Float>,
            name: String, description: String? = null, id: String? = null,
            hidden: Boolean = false, subscription: Boolean = false,
            range: FloatRange = 0f..10f, decimals: Int = 1,
            observeInit: Boolean = true, action: (Float) -> Unit = {},
        ) = property(
            value = field, id = id,
            name = name, description = description,
            hidden = hidden, subscription = subscription,
            type = PropertyType.DecimalSlider, attr = DecimalPropertyAttr(range, decimals),
            observeInit = observeInit, action = action
        )

        fun color(
            field: KMutableProperty0<Color>,
            name: String, description: String? = null, id: String? = null,
            hidden: Boolean = false, subscription: Boolean = false,
            alpha: Boolean = false,
            observeInit: Boolean = true, action: (Color) -> Unit = {},
        ) = property(
            value = field, id = id,
            name = name, description = description,
            hidden = hidden, subscription = subscription,
            type = PropertyType.Color, attr = ColorPropertyAttr(alpha),
            observeInit = observeInit, action = action
        )

        @Suppress("UNCHECKED_CAST")
        fun <T> selector(
            field: KMutableProperty0<T>,
            name: String, description: String? = null, id: String? = null,
            hidden: Boolean = false, subscription: Boolean = false,
            options: List<T> = listOf(), toString: (T) -> String = { it.toString() },
            observeInit: Boolean = true, action: (T) -> Unit = {},
        ) = property<Any, SelectorPropertyAttr<*>>(
            value = field as KMutableProperty0<Any>, id = id,
            name = name, description = description,
            hidden = hidden, subscription = subscription,
            type = PropertyType.Selector, attr = SelectorPropertyAttr(options, toString),
            observeInit = observeInit, action = { action(it as T) }
        )

        inline fun <reified E : Enum<E>> selector(
            field: KMutableProperty0<E>,
            name: String, description: String? = null, id: String? = null,
            hidden: Boolean = false, subscription: Boolean = false,
            noinline toString: (E) -> String = { it.toString() },
            observeInit: Boolean = true, noinline action: (E) -> Unit = {},
        ) = selector(
            field = field,
            name = name, description = description, id = id,
            subscription = subscription, hidden = hidden,
            options = enumValues<E>().toList(), toString = toString,
            observeInit = observeInit, action = action
        )

        fun button(
            name: String, description: String? = null, id: String? = null,
            hidden: Boolean = false, subscription: Boolean = false,
            text: String = "Нажать",
            observeInit: Boolean = false, action: () -> Unit = {},
        ) = property<Nothing, ButtonPropertyAttr>(
            EmptyPropertyValue(), id = id,
            name = name, description = description,
            hidden = hidden, subscription = subscription,
            type = PropertyType.Button, attr = ButtonPropertyAttr(text, action),
            observeInit = observeInit
        )

    }
}

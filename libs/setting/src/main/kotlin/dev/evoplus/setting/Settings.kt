package dev.evoplus.setting

import dev.evoplus.setting.gui.SettingsGui
import dev.evoplus.setting.property.Category
import dev.evoplus.setting.property.EmptyPropertyValue
import dev.evoplus.setting.property.KPropertyBackedPropertyValue
import dev.evoplus.setting.property.Property
import dev.evoplus.setting.property.PropertyMeta
import dev.evoplus.setting.property.PropertyType
import dev.evoplus.setting.property.PropertyValue
import dev.evoplus.setting.property.attr.ButtonPropertyAttr
import dev.evoplus.setting.property.attr.ColorPropertyAttr
import dev.evoplus.setting.property.attr.DecimalPropertyAttr
import dev.evoplus.setting.property.attr.FloatRange
import dev.evoplus.setting.property.attr.NumberPropertyAttr
import dev.evoplus.setting.property.attr.SelectorPropertyAttr
import dev.evoplus.setting.property.attr.TextPropertyAttr
import dev.evoplus.setting.property.data.CategoryData
import gg.essential.universal.UI18n
import java.awt.Color
import java.nio.file.Path
import kotlin.concurrent.fixedRateTimer
import kotlin.io.path.name
import kotlin.reflect.KMutableProperty0

abstract class Settings(val file: Path, val name: String = "Settings") {

    val localizedName get() = UI18n.i18n(name)

    protected val categories = mutableMapOf<String, Category>()
    protected val enabledCategories get() = categories.filterNot { it.value.meta.hidden }

    private var dirty = false

    fun initialize() {
        loadData()
    }

    fun loadData() {
        try {
            readData()
        } catch (e: Throwable) {
            writeData()
            println("Failed to read config data from ${file.name}")
            e.printStackTrace()
        }

        fixedRateTimer(period = 30 * 1000, daemon = true) { writeData() }

        Runtime.getRuntime().addShutdownHook(Thread { writeData() })
    }

    fun gui() = SettingsGui(this)

    fun getCategories(): List<CategoryData> {
        return enabledCategories.values.map(Category::createData).sortedBy { it.meta.name }
    }

    fun searchCategories(term: String) = enabledCategories.values.filter { it.meta.search(term) }

    fun searchProperties(term: String) = CategoryData(
        PropertyMeta(""), enabledCategories.values.flatMap {
            it.searchProperties(term).run {
                if (isEmpty()) emptyList()
                else listOf(it.createDividerItem()) + map(Property<*>::createItem)
            }
        }
    )

    fun markDirty() {
        dirty = true
    }

    private fun readData() {
//        fileConfig.load()

//        propertyCollector.getProperties().filter { it.value.writeDataToFile }.forEach {
//            val fullPath = it.attributesExt.fullPropertyPath()
//
//            var oldValue: Any? = fileConfig.get(fullPath)
//
//            if (it.attributesExt.type == PropertyType.Color) {
//                oldValue = if (oldValue is String) {
//                    val split = oldValue.split(",").map(String::toInt)
//                    if (split.size == 4) Color(split[1], split[2], split[3], split[0]) else null
//                } else {
//                    null
//                }
//            }
//
//            it.setValue(oldValue ?: it.getAsAny())
//        }
    }

    fun writeData() {
        if (!dirty) return

//        propertyCollector.getProperties().filter { it.value.writeDataToFile }.forEach {
//            val fullPath = it.attributesExt.fullPropertyPath()
//
//            var toSet = it.getAsAny()
//
//            if (toSet is Color) {
//                toSet = "${toSet.alpha},${toSet.red},${toSet.green},${toSet.blue}"
//            }
//
//            fileConfig.set(fullPath, toSet)
//        }

        // Leave until misc data is supported
        // miscData.forEach { (property, ann) ->
        //     val path = ann.prefix + "." + property.name
        //
        //     fileConfig.set(path, property.get(this))
        // }

//        fileConfig.save()

        dirty = false
    }

    fun category(
        id: String,
        name: String, description: String? = null,
        hidden: Boolean = false, subscribe: Boolean = false,
        block: CategoryBuilder.() -> Unit,
    ): Category {
        val category = CategoryBuilder(id, PropertyMeta(name, description, hidden, subscribe)).apply(block).build()
        categories[id] = category
        return category
    }

    inner class CategoryBuilder(
        private val id: String,
        private val meta: PropertyMeta,
        private val root: Boolean = true,
    ) {

        private val properties = mutableListOf<Property<*>>()
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
            hidden: Boolean = false, subscribe: Boolean = false,
            block: CategoryBuilder.() -> Unit,
        ) {
            require(root) { "Only root categories support subcategories yet" }
            categories[id] =
                CategoryBuilder(id, PropertyMeta(name, description, hidden, subscribe), false).apply(block)
        }

        @Suppress("UNCHECKED_CAST")
        fun <T, A> property(
            value: PropertyValue,
            id: String?, meta: PropertyMeta,
            type: PropertyType<A>, attr: A,
            observeInit: Boolean = true, action: (T) -> Unit = {},
        ) {
            val id = id ?: value.id
            val property = Property(id, type, meta, attr, value, { action(it as T) }, observeInit, this@Settings)
            properties.add(property)
        }

        fun <T, A> property(
            value: PropertyValue,
            id: String?, name: String, description: String?,
            hidden: Boolean = false, subscribe: Boolean = false,
            type: PropertyType<A>, attr: A,
            observeInit: Boolean = true, action: (T) -> Unit = {},
        ) = property(value, id, PropertyMeta(name, description, hidden, subscribe), type, attr, observeInit, action)

        fun <T, A> property(
            value: KMutableProperty0<T>,
            id: String?, name: String, description: String?,
            hidden: Boolean = false, subscribe: Boolean = false,
            type: PropertyType<A>, attr: A,
            observeInit: Boolean = true, action: (T) -> Unit = {},
        ) = property(
            KPropertyBackedPropertyValue(value),
            id, name, description, hidden, subscribe,
            type, attr, observeInit, action
        )

        fun checkbox(
            field: KMutableProperty0<Boolean>,
            name: String, description: String? = null, id: String? = null,
            hidden: Boolean = false, subscribe: Boolean = false,
            observeInit: Boolean = true, action: (Boolean) -> Unit = {},
        ) = property(
            value = field, id = id,
            name = name, description = description,
            hidden = hidden, subscribe = subscribe,
            type = PropertyType.CheckBox, attr = null,
            observeInit = observeInit, action = action
        )

        fun switch(
            field: KMutableProperty0<Boolean>,
            name: String, description: String? = null, id: String? = null,
            hidden: Boolean = false, subscribe: Boolean = false,
            observeInit: Boolean = true, action: (Boolean) -> Unit = {},
        ) = property(
            value = field, id = id,
            name = name, description = description,
            hidden = hidden, subscribe = subscribe,
            type = PropertyType.Switch, attr = null,
            observeInit = observeInit, action = action
        )

        fun text(
            field: KMutableProperty0<String>,
            name: String, description: String? = null, id: String? = null,
            hidden: Boolean = false, subscribe: Boolean = false,
            placeholder: String = "", protected: Boolean = false,
            observeInit: Boolean = true, action: (String) -> Unit = {},
        ) = property(
            value = field, id = id,
            name = name, description = description,
            hidden = hidden, subscribe = subscribe,
            type = PropertyType.Text, attr = TextPropertyAttr(placeholder, protected),
            observeInit = observeInit, action = action
        )

        fun paragraph(
            field: KMutableProperty0<String>,
            name: String, description: String? = null, id: String? = null,
            hidden: Boolean = false, subscribe: Boolean = false,
            placeholder: String = "", protected: Boolean = false,
            observeInit: Boolean = true, action: (String) -> Unit = {},
        ) = property(
            value = field, id = id,
            name = name, description = description,
            hidden = hidden, subscribe = subscribe,
            type = PropertyType.Text, attr = TextPropertyAttr(placeholder, protected),
            observeInit = observeInit, action = action
        )

        fun percentSlider(
            field: KMutableProperty0<Float>,
            name: String, description: String? = null, id: String? = null,
            hidden: Boolean = false, subscribe: Boolean = false,
            observeInit: Boolean = true, action: (Float) -> Unit = {},
        ) = property(
            value = field, id = id,
            name = name, description = description,
            hidden = hidden, subscribe = subscribe,
            type = PropertyType.PercentSlider, attr = null,
            observeInit = observeInit, action = action
        )

        fun slider(
            field: KMutableProperty0<Int>,
            name: String, description: String? = null, id: String? = null,
            hidden: Boolean = false, subscribe: Boolean = false,
            range: IntProgression = 0..100,
            observeInit: Boolean = true, action: (Int) -> Unit = {},
        ) = property(
            value = field, id = id,
            name = name, description = description,
            hidden = hidden, subscribe = subscribe,
            type = PropertyType.Slider, attr = NumberPropertyAttr(range),
            observeInit = observeInit, action = action
        )

        fun number(
            field: KMutableProperty0<Int>,
            name: String, description: String? = null, id: String? = null,
            hidden: Boolean = false, subscribe: Boolean = false,
            range: IntProgression = 0..100,
            observeInit: Boolean = true, action: (Int) -> Unit = {},
        ) = property(
            value = field, id = id,
            name = name, description = description,
            hidden = hidden, subscribe = subscribe,
            type = PropertyType.Slider, attr = NumberPropertyAttr(range),
            observeInit = observeInit, action = action
        )

        fun decimalSlider(
            field: KMutableProperty0<Float>,
            name: String, description: String? = null, id: String? = null,
            hidden: Boolean = false, subscribe: Boolean = false,
            range: FloatRange = 0f..10f, decimals: Int = 1,
            observeInit: Boolean = true, action: (Float) -> Unit = {},
        ) = property(
            value = field, id = id,
            name = name, description = description,
            hidden = hidden, subscribe = subscribe,
            type = PropertyType.DecimalSlider, attr = DecimalPropertyAttr(range, decimals),
            observeInit = observeInit, action = action
        )

        fun color(
            field: KMutableProperty0<Color>,
            name: String, description: String? = null, id: String? = null,
            hidden: Boolean = false, subscribe: Boolean = false,
            alpha: Boolean = false,
            observeInit: Boolean = true, action: (Color) -> Unit = {},
        ) = property(
            value = field, id = id,
            name = name, description = description,
            hidden = hidden, subscribe = subscribe,
            type = PropertyType.Color, attr = ColorPropertyAttr(alpha),
            observeInit = observeInit, action = action
        )

        fun <T> selector(
            field: KMutableProperty0<T>,
            name: String, description: String? = null, id: String? = null,
            hidden: Boolean = false, subscribe: Boolean = false,
            options: List<T> = listOf(), toString: (T) -> String = { it.toString() },
            observeInit: Boolean = true, action: (T) -> Unit = {},
        ) = property(
            value = field, id = id,
            name = name, description = description,
            hidden = hidden, subscribe = subscribe,
            type = PropertyType.Selector, attr = SelectorPropertyAttr(options, toString),
            observeInit = observeInit, action = action
        )

        inline fun <reified E : Enum<E>> selector(
            field: KMutableProperty0<E>,
            name: String, description: String? = null, id: String? = null,
            hidden: Boolean = false, subscribe: Boolean = false,
            noinline toString: (E) -> String = { it.toString() },
            observeInit: Boolean = true, noinline action: (E) -> Unit = {},
        ) = selector(
            field = field,
            name = name, description = description, id = id,
            subscribe = subscribe, hidden = hidden,
            options = enumValues<E>().toList(), toString = toString,
            observeInit = observeInit, action = action
        )

        fun button(
            name: String, description: String? = null, id: String? = null,
            hidden: Boolean = false, subscribe: Boolean = false,
            text: String = "Нажать",
            observeInit: Boolean = false, action: () -> Unit = {},
        ) = property<Nothing, ButtonPropertyAttr>(
            EmptyPropertyValue(), id = id,
            name = name, description = description,
            hidden = hidden, subscribe = subscribe,
            type = PropertyType.Button, attr = ButtonPropertyAttr(text),
            observeInit = observeInit, action = { action() }
        )

    }
}

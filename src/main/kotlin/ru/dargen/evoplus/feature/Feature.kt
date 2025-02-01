package ru.dargen.evoplus.feature

import dev.evoplus.feature.setting.Settings.CategoryBuilder
import dev.evoplus.feature.setting.property.asPropertyName
import ru.dargen.evoplus.feature.screen.FeatureElement
import ru.dargen.evoplus.feature.screen.FeaturePrompt
import ru.dargen.evoplus.feature.setting.group.SettingGroup
import ru.dargen.evoplus.feature.widget.WidgetGroup
import ru.dargen.evoplus.util.kotlin.KotlinOpens


@KotlinOpens
abstract class Feature(id: String? = null, val name: String, var description: String? = null) : FeatureElement {

    val id = id ?: javaClass.simpleName.replace("Feature", "").asPropertyName()

    val includes by lazy { mutableListOf<Feature>() }

    val settings: SettingGroup = SettingGroup(this.id, name)
    val widgets = WidgetGroup().apply(settings.value::add)

    protected fun CategoryBuilder.setup() {}

    protected fun preInitialize() {}

    protected fun initialize() {}

    internal fun preInitializeInternal() {
        preInitialize()
        includes.forEach(Feature::preInitializeInternal)
    }

    internal fun initializeInternal() {
        initialize()
        includes.forEach(Feature::initializeInternal)
    }

    internal fun setupInternal(category: CategoryBuilder?) {
        category?.subcategory(id, name, description) { setup() } ?: FeaturesSettings.category(id, name, description) {
            setup()
            includes.forEach { it.setupInternal(this) }
        }
    }

    final inline fun <reified T> config(name: String = id, value: T) = Features.config(name, value)

    protected fun <F : Feature> include(feature: F) = feature.apply(includes::add)

    override fun search(prompt: FeaturePrompt) = true
    override fun createElement(prompt: FeaturePrompt) = settings.createElement(prompt)

}
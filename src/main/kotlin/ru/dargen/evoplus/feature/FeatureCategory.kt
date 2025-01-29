package ru.dargen.evoplus.feature

import ru.dargen.evoplus.feature.setting.group.SettingGroup
import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
class FeatureCategory(id: String, name: String, val settings: SettingGroup = SettingGroup(id, name)) {

    val id get() = settings.id
    val name get() = settings.name

    final inline fun <reified T> config(name: String = id, value: T) = Features.config(name, value)

}
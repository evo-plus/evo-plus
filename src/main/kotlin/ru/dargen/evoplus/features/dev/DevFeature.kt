package ru.dargen.evoplus.features.dev

import dev.evoplus.setting.Settings.CategoryBuilder
import net.minecraft.item.Items
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.protocol.Connector
import ru.dargen.evoplus.protocol.collector.PlayerDataCollector
import ru.dargen.evoplus.render.context.Overlay
import ru.dargen.evoplus.render.context.RenderContext
import ru.dargen.evoplus.render.context.ScreenContext
import ru.dargen.evoplus.render.context.WorldContext
import ru.dargen.evoplus.render.node.Node
import ru.dargen.evoplus.render.node.text
import ru.dargen.evoplus.render.node.tick

object DevFeature : Feature("dev-env", "DevEnv", icon = Items.COMMAND_BLOCK) {

    val NodeDebugWidget by widgets.widget("Вывод компонентов", "node-debug") {
        +text {
            tick {
                text =
                    mapOf(
                        "Overlay" to Overlay,
                        "World" to WorldContext,
                        "Screen" to ScreenContext.current()
                    ).map { (name, ctx) ->
                        "$name:\n ${ctx?.let(NodeDebugMode.totalizer)?.entries?.joinToString("\n ") { "${it.key.simpleName}: ${it.value}" } ?: ""}"
                    }.joinToString("\n")

            }
        }
    }
    val NetworkWidget by widgets.widget("Статистика", "network-stats") {
        +text {
            tick {
                text = """
                    Token: ${Connector.token}
                    Server: ${Connector.server}
                    Location: ${PlayerDataCollector.location}
                    Event: ${PlayerDataCollector.event}
                """.trimIndent()
            }
        }
    }

    var NodeDebugMode = NodeDebugModeType.TOTAL

    override fun CategoryBuilder.setup() {
        selector(::NodeDebugMode, "Тип вывода компонентов")
    }

    init {

    }

    enum class NodeDebugModeType(val displayName: String, val totalizer: RenderContext.() -> Map<Class<*>, Int>) {

        TOTAL("Древо компонентов", {
            val summary = hashMapOf<Class<*>, Int>()
            fun Node.sumNodes() {
                summary.merge(javaClass, 1, Int::plus)
                children.forEach { it.sumNodes() }
            }

            children.forEach { it.sumNodes() }

            summary
        }),
        CONTEXTS("Компоненты контекстов", { children.groupBy { it.javaClass }.mapValues { it.value.size } })

    }

}
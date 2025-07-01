package ru.dargen.evoplus.features.dev

import dev.evoplus.feature.setting.Settings.CategoryBuilder
import dev.evoplus.feature.setting.property.value.Bind
import gg.essential.universal.UKeyboard
import ru.dargen.evoplus.feature.widget.WidgetEditorScreen
import ru.dargen.evoplus.render.context.RenderContext
import ru.dargen.evoplus.render.node.Node
import ru.dargen.evoplus.scheduler.after

object DevFeature : Feature("dev-env", "DevEnv") {

//    val NodeDebugWidget by widget("Вывод компонентов", "node-debug") {
//        +text {
//            tick {
//                text =
//                    mapOf(
//                        "Overlay" to Overlay,
//                        "World" to WorldContext,
//                        "Screen" to ScreenContext.current()
//                    ).map { (name, ctx) ->
//                        "$name:\n ${ctx?.let(NodeDebugMode.totalizer)?.entries?.joinToString("\n ") { "${it.key.simpleName}: ${it.value}" } ?: ""}"
//                    }.joinToString("\n")
//
//            }
//        }
//    }

//    val NetworkWidget by widget("Статистика", "network-stats") {
//        +text {
//            tick {
//                text = """
//                    Token: ${Connector.token}
//                    Server: ${Connector.server}
//                    Location: ${PlayerDataCollector.location}
//                    Event: ${PlayerDataCollector.event}
//                """.trimIndent()
//            }
//        }
//    }

    var NodeDebugMode = NodeDebugModeType.TOTAL
    var bind = Bind.key(UKeyboard.KEY_J)

    override fun CategoryBuilder.setup() {
//        widget("print-components", "Вывод компонентов", NodeDebugWidget)
        selector(::NodeDebugMode, "Тип вывода компонентов")

        button("Виджеты") { after(1) { WidgetEditorScreen.open() } }
        bind(::bind, "test", "test")
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
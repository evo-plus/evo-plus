package ru.dargen.evoplus.event.bossbar

import net.minecraft.client.gui.hud.ClientBossBar
import ru.dargen.evoplus.event.Event

data class BossBarRenderEvent(val bossBar: ClientBossBar) : Event
package ru.dargen.evoplus.event.world

import net.minecraft.world.World
import ru.dargen.evoplus.event.Event

class WorldPreLoadEvent(val world: World) : Event
class WorldPostLoadEvent(val world: World) : Event
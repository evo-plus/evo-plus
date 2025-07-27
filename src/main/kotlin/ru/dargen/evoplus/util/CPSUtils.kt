package ru.dargen.evoplus.util

import ru.dargen.evoplus.event.game.PreTickEvent
import ru.dargen.evoplus.event.on

object CPSUtils {

    private var clicks = 0
    private var cps = 0
    private var secondsClicking = 0
    private var lastTime = 0L

    init {
        on<PreTickEvent> {
            var currentTime = currentMillis

            if (currentTime - lastTime >= 1000) {
                if (cps == 0) {
                    clicks = 0
                    secondsClicking = 0
                } else {
                    lastTime = currentTime
                    secondsClicking++
                    cps = 0
                }
            }
        }
    }

    fun onAttack() {
        clicks++
        cps++
    }

}
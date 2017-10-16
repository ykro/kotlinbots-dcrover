package com.bitandik.labs.kotlinbots.dcrover

import android.util.Log
import com.bitandik.labs.kotlinbots.dcrover.Constants.Companion.BACK_LEFT
import com.bitandik.labs.kotlinbots.dcrover.Constants.Companion.BACK_RIGHT
import com.bitandik.labs.kotlinbots.dcrover.Constants.Companion.FRONT_LEFT
import com.bitandik.labs.kotlinbots.dcrover.Constants.Companion.FRONT_RIGHT
import com.bitandik.labs.kotlinbots.dcrover.Constants.Companion.TAG
import com.bitandik.labs.kotlinbots.dcrover.Constants.Companion._2W
import com.bitandik.labs.kotlinbots.dcrover.Constants.Companion._4W
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManagerService
import java.io.IOException

/**
 * Created by ykro.
 */

class DCRover(GPIOs: List<String>) {
    private var wheels: Map<Int, DCWheel> = emptyMap()
    private var goinfForward: Boolean = true
    init {
        when(GPIOs.size/2) {
            _2W -> wheels = hashMapOf(
                                    FRONT_LEFT to DCWheel(GPIOs[0], GPIOs[1]),
                                            FRONT_RIGHT to DCWheel(GPIOs[2], GPIOs[3]))

            _4W ->    wheels = hashMapOf(
                                    FRONT_LEFT to DCWheel(GPIOs[0], GPIOs[1]),
                                            FRONT_RIGHT to DCWheel(GPIOs[2], GPIOs[3]),
                                            BACK_LEFT to DCWheel(GPIOs[4], GPIOs[5]),
                                            BACK_RIGHT to DCWheel(GPIOs[6], GPIOs[7]))

            else -> Log.e(TAG, "Invalid number of GPIOs. Specify four for 2W and eight for 4W drive")
        }

    }

    fun close() {
        wheels.onEach {
            it.value.close()
        }
    }

    fun stop() {
        wheels.onEach { it.value.stop() }
    }

    fun forward() {
        goinfForward = true
        wheels.onEach { it.value.forward() }
    }

    fun backward() {
        goinfForward = false
        wheels.onEach { it.value.backward() }
    }

    fun left() {
        wheels.filter { it.key == FRONT_RIGHT || it.key == BACK_RIGHT }.onEach {
            if (goinfForward) it.value.forward()
            else it.value.backward()
        }
        wheels.filter { it.key == FRONT_LEFT || it.key == BACK_LEFT }.onEach { it.value.stop() }
    }

    fun right() {
        wheels.filter { it.key == FRONT_LEFT || it.key == BACK_LEFT }.onEach {
            if (goinfForward) it.value.forward()
            else it.value.backward()
        }
        wheels.filter { it.key == FRONT_RIGHT || it.key == BACK_RIGHT }.onEach { it.value.stop() }
    }
}

class DCWheel(gpioAPinName: String, gpioBPinName: String) {
    var gpioA: Gpio? = null
    var gpioB: Gpio? = null

    init {
        val service = PeripheralManagerService()
        try {
            gpioA = service.openGpio(gpioAPinName)
            gpioA?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)

            gpioB = service.openGpio(gpioBPinName)
            gpioB?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)

        } catch (e: IOException) {
            Log.e(Constants.TAG, "Error on PeripheralIO API", e);
        }
    }

    fun forward(){
        gpioA?.value = true
        gpioB?.value = false
    }

    fun backward() {
        gpioA?.value = false
        gpioB?.value = true
    }

    fun stop() {
        gpioA?.value = false
        gpioB?.value = false
    }

    fun close() {
        gpioA?.close()
        gpioB?.close()
    }

}

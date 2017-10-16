package com.bitandik.labs.kotlinbots.dcrover

/**
 * Created by ykro.
 */

interface WheelCallback {
    fun fwd()
    fun back()
    fun left()
    fun right()
    fun stop()
}
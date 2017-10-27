package com.bitandik.labs.kotlinbots.dcrover

import android.app.Activity
import android.os.Bundle
import android.util.Log

class MainActivity : Activity(), WheelCallback {
    private lateinit var connection: NearbyConnection

/*
    private var rover = DCRover (listOf(
                                //Front Left
                                "GPIO_33", "GPIO_34",
                                //Front Right
                                "GPIO_32", "GPIO_37",
                                //Back left
                                "GPIO_173", "GPIO_172",
                                //Back right
                                "GPIO_175", "GPIO_174"))

    private var rover = DCRover (listOf(
                                //Left
                                "GPIO_33", "GPIO_34",
                                //Right
                                "GPIO_32", "GPIO_37"))
    */

    private var rover = DCRover (listOf(
            //Front Left
            "GPIO_175", "GPIO_174",
            //Front Right
            "GPIO_173", "GPIO_172",
            //Back left
            "GPIO_32", "GPIO_37",
            //Back Right
            "GPIO_33", "GPIO_34"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
        connection = NearbyConnection(this, this)
    }

    override fun onStart() {
        super.onStart()
        connection.connect()
    }

    override fun onDestroy() {
        super.onDestroy()
        rover.close()

        connection.disconnect()
    }

    override fun fwd() {
        rover.forward()
    }

    override fun back() {
        rover.backward()
    }

    override fun left() {
        rover.left()
    }

    override fun right() {
        rover.right()
    }

    override fun stop() {
        rover.stop()
    }
}

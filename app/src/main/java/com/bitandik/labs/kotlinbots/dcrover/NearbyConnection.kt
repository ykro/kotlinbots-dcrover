package com.bitandik.labs.kotlinbots.dcrover

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

/**
 * Created by ykro.
 */

class NearbyConnection(context: Context, cmdCallback: WheelCallback) : GoogleApiClient.ConnectionCallbacks,
                         GoogleApiClient.OnConnectionFailedListener {

    val googleApiClient: GoogleApiClient = GoogleApiClient.Builder(context)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(Nearby.CONNECTIONS_API)
            .build()

    fun connect() {
        Log.d(TAG, "Connecting")
        googleApiClient.connect()
    }

    fun disconnect() {
        Log.d(TAG, "Disonnecting")
        if (googleApiClient.isConnected) {
            googleApiClient.disconnect()
        }
    }

    //[ConnectionCallbacks]
    override fun onConnected(bunde: Bundle?) {
        Log.d(TAG, "startAdvertising")
        startAdvertising()
    }

    override fun onConnectionSuspended(i: Int) {
        googleApiClient.reconnect()
    }
    //[/ConnectionCallbacks]

    //[OnConnectionFailedListener]
    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.e(TAG, "Connection failed ${connectionResult.errorMessage} ${connectionResult.resolution} ${connectionResult.errorCode}")
    }
    //[/OnConnectionFailedListener]

    private fun startAdvertising() {
        Nearby.Connections.startAdvertising(googleApiClient, null,
                NEARBY_SERVICE_ID,
                connectionLifecycleCallback,
                AdvertisingOptions(Strategy.P2P_STAR))
                .setResultCallback { result ->
                    Log.d(TAG, "startAdvertising:onResult:" + result)
                    if (result.status.isSuccess) {
                        Log.d(TAG, "Advertising succeeded ${result.status}")
                    } else {
                        Log.e(TAG, "Advertising failed ${result.status.statusCode}")
                    }
                }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String?, payload: Payload?) {
            payload?.let {
                if (it.type == Payload.Type.BYTES) {
                    val msg:String? = it.asBytes()?.let { it1 -> String(it1) }
                    when(msg) {
                        null -> Log.d(TAG, "Payload is null")
                        FWD -> cmdCallback.fwd()
                        BACK -> cmdCallback.back()
                        LEFT -> cmdCallback.left()
                        RIGHT -> cmdCallback.right()
                        STOP -> cmdCallback.stop()
                        else -> Log.d(TAG, "Unknown command received")
                    }
                }
            }
        }

        override fun onPayloadTransferUpdate(p0: String?, p1: PayloadTransferUpdate?) {}
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {

        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            Nearby.Connections.acceptConnection(googleApiClient, endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    Log.d(TAG, "Client connected $endpointId")
                    Nearby.Connections.stopAdvertising(googleApiClient)
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    Log.d(TAG, "Connection rejected from $endpointId")
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            Log.d(TAG, "Disconnected from $endpointId")
            startAdvertising()
        }
    }
}
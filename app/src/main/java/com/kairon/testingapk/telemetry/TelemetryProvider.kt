package com.kairon.testingapk.telemetry
import com.kairon.testingapk.model.GameState
interface TelemetryProvider {
    val name:String
    fun connect(sessionId:String,onState:(GameState)->Unit,onError:(Throwable)->Unit = {})
    fun disconnect()
}

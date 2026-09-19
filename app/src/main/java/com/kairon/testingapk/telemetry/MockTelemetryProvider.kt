package com.kairon.testingapk.telemetry
import com.kairon.testingapk.model.*
import kotlin.concurrent.thread
import kotlin.math.cos
import kotlin.math.sin
class MockTelemetryProvider:TelemetryProvider {
    override val name="Mock / Development"
    @Volatile private var running=false
    override fun connect(sessionId:String,onState:(GameState)->Unit,onError:(Throwable)->Unit) {
        if(running)return
        running=true
        thread(name="qa-mock-telemetry") {
            var t=0L
            while(running) {
                t+=100
                val a=t/1000f
                val bot=PlayerState("bot-01","Bot 01",EntityType.BOT,Vec3(cos(a)*35f,0f,sin(a)*35f),35f,Vec3(cos(a),0f,sin(a)),MovementState.RUNNING,100,"SMG")
                val player=PlayerState("player-01","QA Player",EntityType.PLAYER,Vec3(-20f+sin(a)*3f,0f,55f),58f,Vec3(cos(a),0f,sin(a)),MovementState.SPRINTING,92,"Rifle","Medkit")
                val vehicle=VehicleState("veh-01","QA Buggy",Vec3(15f,0f,90f),90f,12f,listOf("player-01"))
                onState(GameState(t,null,listOf(bot,player),listOf(vehicle)))
                Thread.sleep(100)
            }
        }
    }
    override fun disconnect(){running=false}
}

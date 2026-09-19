package com.kairon.testingapk.telemetry
import com.kairon.testingapk.model.*
import kotlin.concurrent.thread
import kotlin.math.cos
import kotlin.math.sin
class MockTelemetryProvider:TelemetryProvider{
 override val name="Mock / Development"
 @Volatile private var running=false
 override fun connect(sessionId:String,onState:(GameState)->Unit,onError:(Throwable)->Unit){
  if(running)return
  running=true
  thread(name="qa-mock-telemetry"){
   var t=0L
   while(running){
    t+=100;val a=t/1000f
    val bx=.30f+cos(a)*.10f;val by=.45f+sin(a)*.10f
    val px=.65f+sin(a)*.03f;val py=.40f
    val skeleton=Skeleton(mapOf("head" to Vec3(px,py-.05f,0f),"chest" to Vec3(px,py,0f),"pelvis" to Vec3(px,py+.08f,0f),"leftHand" to Vec3(px-.05f,py+.04f,0f),"rightHand" to Vec3(px+.05f,py+.04f,0f),"leftFoot" to Vec3(px-.03f,py+.18f,0f),"rightFoot" to Vec3(px+.03f,py+.18f,0f)))
    val bot=PlayerState("bot-01","Bot 01",EntityType.BOT,Vec3(cos(a)*35f,0f,sin(a)*35f),35f,Vec3(cos(a),0f,sin(a)),MovementState.RUNNING,100,"SMG",screenX=bx,screenY=by)
    val player=PlayerState("player-01","QA Player",EntityType.PLAYER,Vec3(-20f+sin(a)*3f,0f,55f),58f,Vec3(cos(a),0f,sin(a)),MovementState.SPRINTING,92,"Rifle","Medkit",skeleton,null,px,py)
    val vehicle=VehicleState("veh-01","QA Buggy",Vec3(15f,0f,90f),90f,12f,listOf("player-01"),.75f,.62f)
    onState(GameState(t,null,listOf(bot,player),listOf(vehicle)))
    Thread.sleep(100)
   }
  }
 }
 override fun disconnect(){running=false}
}

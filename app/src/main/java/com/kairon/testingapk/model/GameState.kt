package com.kairon.testingapk.model
data class Vec3(val x:Float,val y:Float,val z:Float)
enum class EntityType { PLAYER, BOT }
enum class MovementState { IDLE, WALKING, RUNNING, SPRINTING, JUMPING, VEHICLE }
data class Skeleton(val joints:Map<String,Vec3> = emptyMap())
data class PlayerState(
    val id:String,val displayName:String?=null,val type:EntityType,val position:Vec3,
    val distanceMeters:Float=0f,val direction:Vec3=Vec3(0f,0f,0f),
    val movement:MovementState=MovementState.IDLE,val health:Int=100,val weapon:String?=null,
    val item:String?=null,val skeleton:Skeleton?=null,val vehicleId:String?=null,
    val screenX:Float?=null,val screenY:Float?=null
)
data class VehicleState(val id:String,val type:String,val position:Vec3,val distanceMeters:Float=0f,val speedMps:Float=0f,val occupantIds:List<String> = emptyList(),val screenX:Float?=null,val screenY:Float?=null)
data class ItemState(val id:String,val name:String,val position:Vec3,val distanceMeters:Float=0f,val floor:String?=null,val screenX:Float?=null,val screenY:Float?=null)
data class GameState(
    val timestampMs:Long,val localPlayer:PlayerState?,val players:List<PlayerState>,
    val vehicles:List<VehicleState> = emptyList(),val items:List<ItemState> = emptyList(),
    val clientPositions:Map<String,Vec3> = emptyMap(),val authoritativePositions:Map<String,Vec3> = emptyMap()
)
data class SyncEvent(val timestampMs:Long,val entityId:String,val category:String,val detail:String,val severity:String)
data class DetectionEvent(val timestampMs:Long,val sessionId:String,val category:String,val detected:Boolean,val latencyMs:Long?)
data class TestSession(
    val id:String,val startMs:Long,val endMs:Long?=null,val gameInstance:String,
    val playerCount:Int=0,val botCount:Int=0,val vehicleCount:Int=0,val itemCount:Int=0,
    val syncEvents:List<SyncEvent> = emptyList(),val detectionEvents:List<DetectionEvent> = emptyList()
)

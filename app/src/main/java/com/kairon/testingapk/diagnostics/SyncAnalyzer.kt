package com.kairon.testingapk.diagnostics
import com.kairon.testingapk.model.*
import kotlin.math.sqrt
class SyncAnalyzer {
    private val last=mutableMapOf<String,Vec3>()
    fun analyze(state:GameState):List<SyncEvent> {
        val out=mutableListOf<SyncEvent>()
        state.authoritativePositions.forEach { (id,server) ->
            val client=state.clientPositions[id] ?: return@forEach
            val d=distance(server,client)
            if(d>2f) out += SyncEvent(state.timestampMs,id,"POSITION_DIFFERENCE","Server/client delta %.2f units".format(d),if(d>10f)"CRITICAL" else "WARNING")
        }
        state.players.forEach { p ->
            val old=last[p.id]
            if(old!=null && distance(old,p.position)>20f)
                out += SyncEvent(state.timestampMs,p.id,"POSITION_JUMP","Large movement delta detected","CRITICAL")
            last[p.id]=p.position
        }
        return out
    }
    private fun distance(a:Vec3,b:Vec3)=sqrt((a.x-b.x)*(a.x-b.x)+(a.y-b.y)*(a.y-b.y)+(a.z-b.z)*(a.z-b.z))
}

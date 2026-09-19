package com.kairon.testingapk.overlay
import android.graphics.*
import android.view.View
import com.kairon.testingapk.model.*
class OverlayRenderer(context:android.content.Context):View(context){
    var config=OverlayConfig()
    private var state:GameState?=null
    private var syncEvents:List<SyncEvent> = emptyList()
    private val p=Paint(Paint.ANTI_ALIAS_FLAG).apply{strokeWidth=3f}
    private val t=Paint(Paint.ANTI_ALIAS_FLAG).apply{textSize=26f}
    fun update(s:GameState,events:List<SyncEvent>){state=s;syncEvents=events;invalidate()}
    override fun onDraw(c:Canvas){
        val s=state?:return
        val entities=s.players.filter{it.distanceMeters<=config.rangeMeters && ((it.type==EntityType.PLAYER&&config.players)||(it.type==EntityType.BOT&&config.bots))}
        if(config.counter){t.color=Color.WHITE;t.textAlign=Paint.Align.CENTER;t.typeface=Typeface.DEFAULT_BOLD;c.drawText("Players in Range: "+entities.size,width/2f,55f,t)}
        entities.forEach { e ->
            val x=(.5f + (e.position.x/250f)).coerceIn(.05f,.95f)*width
            val y=(.5f - (e.position.z/250f)).coerceIn(.05f,.95f)*height
            val color=if(e.type==EntityType.PLAYER)Color.GREEN else Color.WHITE
            p.color=color
            if(config.direction)c.drawLine(width/2f,height/2f,x,y,p)
            if(config.skeleton){c.drawCircle(x,y,9f,p);c.drawLine(x,y,x,y+35f,p);c.drawLine(x,y+10f,x-18f,y+28f,p);c.drawLine(x,y+10f,x+18f,y+28f,p)}
            t.color=color;t.textAlign=Paint.Align.LEFT
            val label=(e.displayName?:e.id)+(if(config.distance)"  "+e.distanceMeters.toInt()+"m" else "")
            c.drawText(label,x+12f,y,t)
            if(config.weapons&&e.weapon!=null)c.drawText(e.weapon,x+12f,y+25f,t)
        }
        if(config.vehicles) s.vehicles.filter{it.distanceMeters<=config.rangeMeters}.forEach{v->
            p.color=Color.CYAN
            val x=(.5f+v.position.x/250f).coerceIn(.05f,.95f)*width
            val y=(.5f-v.position.z/250f).coerceIn(.05f,.95f)*height
            c.drawRect(x-12f,y-8f,x+12f,y+8f,p)
            t.color=Color.CYAN;t.textAlign=Paint.Align.LEFT;c.drawText(v.type+" "+v.distanceMeters.toInt()+"m",x+15f,y,t)
        }
        if(config.diagnostics&&!syncEvents.isNullOrEmpty()){t.color=Color.YELLOW;t.textAlign=Paint.Align.LEFT;c.drawText("SYNC: "+syncEvents.last().category,20f,height-40f,t)}
    }
}

package com.kairon.testingapk.overlay
import android.graphics.*
import android.view.View
import com.kairon.testingapk.model.*

class OverlayRenderer(context:android.content.Context):View(context){
 var config=OverlayConfig()
 private var state:GameState?=null
 private var syncEvents:List<SyncEvent> = emptyList()
 private val p=Paint(Paint.ANTI_ALIAS_FLAG).apply{strokeWidth=3f}
 private val t=Paint(Paint.ANTI_ALIAS_FLAG).apply{textSize=25f}
 fun update(s:GameState,events:List<SyncEvent>){state=s;syncEvents=events;invalidate()}
 override fun onDraw(c:Canvas){
  val s=state?:return
  val players=s.players.filter{it.distanceMeters<=config.rangeMeters&&((it.type==EntityType.PLAYER&&config.players)||(it.type==EntityType.BOT&&config.bots))}
  if(config.counter){t.color=Color.WHITE;t.textAlign=Paint.Align.CENTER;t.typeface=Typeface.DEFAULT_BOLD;c.drawText("Players in Range: "+players.size,width/2f,55f,t)}
  players.forEach{drawPlayer(c,it)}
  if(config.items) s.items.filter{it.distanceMeters<=config.rangeMeters}.forEach{drawItem(c,it)}
  if(config.vehicles) s.vehicles.filter{it.distanceMeters<=config.rangeMeters}.forEach{drawVehicle(c,it)}
  if(config.diagnostics&&syncEvents.isNotEmpty()){t.color=Color.YELLOW;t.textAlign=Paint.Align.LEFT;c.drawText("SYNC: "+syncEvents.last().category,20f,height-55f,t);c.drawText(syncEvents.last().detail,20f,height-25f,t)}
  if(config.debug){t.color=Color.LTGRAY;t.textAlign=Paint.Align.LEFT;c.drawText("Telemetry: "+s.timestampMs,20f,90f,t)}
 }
 private fun drawPlayer(c:Canvas,e:PlayerState){
  val xy=screen(e.screenX,e.screenY)?:return
  val x=xy.first;val y=xy.second;val color=if(e.type==EntityType.PLAYER)Color.GREEN else Color.WHITE
  p.color=color
  if(config.direction)c.drawLine(width/2f,height/2f,x,y,p)
  if(config.skeleton&&e.skeleton!=null)drawSkeleton(c,e.skeleton!!,color)
  else if(config.skeleton)c.drawCircle(x,y,7f,p)
  t.color=color;t.textAlign=Paint.Align.LEFT
  var dy=0f
  c.drawText((e.displayName?:e.id)+(if(config.distance)"  "+e.distanceMeters.toInt()+"m" else ""),x+12f,y+dy,t);dy+=25f
  if(config.weapons&&!e.weapon.isNullOrBlank()){c.drawText("Weapon: "+e.weapon,x+12f,y+dy,t);dy+=25f}
  if(config.debug)c.drawText("HP "+e.health+" • "+e.movement.name+" • "+e.vehicleId.orEmpty(),x+12f,y+dy,t)
 }
 private fun drawSkeleton(c:Canvas,s:Skeleton,color:Int){
  p.color=color
  val j=s.joints
  fun line(a:String,b:String){val pa=screen(j[a]?.x,j[a]?.y);val pb=screen(j[b]?.x,j[b]?.y);if(pa!=null&&pb!=null)c.drawLine(pa.first,pa.second,pb.first,pb.second,p)}
  listOf("head" to "chest","chest" to "pelvis","chest" to "leftHand","chest" to "rightHand","pelvis" to "leftFoot","pelvis" to "rightFoot").forEach{line(it.first,it.second)}
 }
 private fun drawItem(c:Canvas,i:ItemState){
  val xy=screen(i.screenX,i.screenY)?:return;p.color=Color.CYAN;c.drawCircle(xy.first,xy.second,6f,p)
  t.color=Color.CYAN;t.textAlign=Paint.Align.LEFT;c.drawText(i.name+" • "+i.distanceMeters.toInt()+"m"+(i.floor?.let{" • $it"}?:""),xy.first+10f,xy.second,t)
 }
 private fun drawVehicle(c:Canvas,v:VehicleState){
  val xy=screen(null,null)?:return;p.color=Color.MAGENTA;c.drawRect(xy.first-12f,xy.second-8f,xy.first+12f,xy.second+8f,p)
  t.color=Color.MAGENTA;t.textAlign=Paint.Align.LEFT;c.drawText(v.type+" • "+v.distanceMeters.toInt()+"m",xy.first+16f,xy.second,t)
  if(config.occupants&&!v.occupantIds.isNullOrEmpty())c.drawText("Occupants: "+v.occupantIds.size,xy.first+16f,xy.second+24f,t)
 }
 private fun screen(x:Float?,y:Float?):Pair<Float,Float>?{
  if(x==null||y==null)return null
  return Pair(x.coerceIn(0f,1f)*width,y.coerceIn(0f,1f)*height)
 }
}

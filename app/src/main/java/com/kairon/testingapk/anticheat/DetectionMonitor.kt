package com.kairon.testingapk.anticheat
import com.kairon.testingapk.model.DetectionEvent
class DetectionMonitor{
 private val events=mutableListOf<DetectionEvent>()
 private val starts=mutableMapOf<String,Long>()
 fun beginTest(sessionId:String,nowMs:Long=System.currentTimeMillis()){starts[sessionId]=nowMs}
 fun recordDetection(sessionId:String,category:String,detectedAtMs:Long=System.currentTimeMillis()){
  val start=starts[sessionId];val latency=start?.let{detectedAtMs-it}
  synchronized(events){events+=DetectionEvent(detectedAtMs,sessionId,category,true,latency);if(events.size>500)events.removeAt(0)}
 }
 fun snapshot():List<DetectionEvent>=synchronized(events){events.toList()}
 fun clear(sessionId:String){starts.remove(sessionId)}
}

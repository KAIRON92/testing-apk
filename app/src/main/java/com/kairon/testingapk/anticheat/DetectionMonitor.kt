package com.kairon.testingapk.anticheat
import com.kairon.testingapk.model.DetectionEvent
class DetectionMonitor {
    private val events=mutableListOf<DetectionEvent>()
    fun record(e:DetectionEvent){ synchronized(events){events+=e;if(events.size>500)events.removeAt(0)} }
    fun snapshot():List<DetectionEvent>=synchronized(events){events.toList()}
}

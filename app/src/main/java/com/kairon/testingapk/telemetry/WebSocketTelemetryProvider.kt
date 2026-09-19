package com.kairon.testingapk.telemetry
import com.google.gson.Gson
import com.kairon.testingapk.model.GameState
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
class WebSocketTelemetryProvider(private val endpoint:String):TelemetryProvider{
 override val name="Authorized WebSocket QA telemetry"
 private val gson=Gson()
 private var socket:WebSocketClient?=null
 override fun connect(sessionId:String,onState:(GameState)->Unit,onError:(Throwable)->Unit){
  socket=object:WebSocketClient(URI(endpoint)){
   override fun onOpen(handshake:ServerHandshake){send("{\"type\":\"qa_session\",\"sessionId\":\""+sessionId+"\"}")}
   override fun onMessage(message:String){runCatching{gson.fromJson(message,GameState::class.java)}.onSuccess(onState).onFailure(onError)}
   override fun onClose(code:Int,reason:String,remote:Boolean){}
   override fun onError(ex:Exception){onError(ex)}
  }.also{it.connect()}
 }
 override fun disconnect(){socket?.close();socket=null}
}
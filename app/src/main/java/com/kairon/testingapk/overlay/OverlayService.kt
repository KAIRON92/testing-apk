package com.kairon.testingapk.overlay
import android.app.*
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.kairon.testingapk.diagnostics.SyncAnalyzer
import com.kairon.testingapk.telemetry.*

class OverlayService:Service(){
 private lateinit var wm:WindowManager
 private lateinit var renderer:OverlayRenderer
 private lateinit var provider:TelemetryProvider
 private lateinit var controls:FloatingControlPanel
 private val analyzer=SyncAnalyzer()
 override fun onCreate(){
  super.onCreate()
  val nm=getSystemService(NotificationManager::class.java)
  if(Build.VERSION.SDK_INT>=26)nm.createNotificationChannel(NotificationChannel("qa","QA Overlay",NotificationManager.IMPORTANCE_LOW))
  startForeground(42,NotificationCompat.Builder(this,"qa").setContentTitle("TestingAPK QA").setContentText("Authorized debug overlay active").setSmallIcon(android.R.drawable.ic_menu_info_details).build())
  if(!Settings.canDrawOverlays(this)){stopSelf();return}
  wm=getSystemService(WINDOW_SERVICE) as WindowManager
  val type=if(Build.VERSION.SDK_INT>=26)WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE
  renderer=OverlayRenderer(this)
  wm.addView(renderer,WindowManager.LayoutParams(-1,-1,type,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,PixelFormat.TRANSLUCENT))
  controls=FloatingControlPanel(this)
  val pair=controls.create()
  val bp=WindowManager.LayoutParams(64,64,type,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT).apply{x=24;y=180}
  val pp=WindowManager.LayoutParams(420,620,type,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT).apply{x=24;y=250}
  wm.addView(pair.first,bp);wm.addView(pair.second,pp);controls.draggable(pair.first,bp)
  val settings=getSharedPreferences("qa_settings",MODE_PRIVATE)
  val useMock=settings.getBoolean("use_mock",true)
  provider=if(useMock)MockTelemetryProvider() else WebSocketTelemetryProvider(settings.getString("endpoint","ws://127.0.0.1:8080/qa/telemetry")!!)
  provider.connect("overlay-session",{s->val e=analyzer.analyze(s);android.os.Handler(mainLooper).post{renderer.update(s,e)}},{})
 }
 fun setOverlayOption(name:String,value:Boolean){
  when(name){
   "Players"->renderer.config.players=value;"Bots"->renderer.config.bots=value
   "Skeleton"->renderer.config.skeleton=value;"Distance"->renderer.config.distance=value
   "Direction"->renderer.config.direction=value;"Weapons"->renderer.config.weapons=value
   "Items"->renderer.config.items=value;"Vehicles"->renderer.config.vehicles=value
   "Vehicle occupants"->renderer.config.occupants=value;"Entity counter"->renderer.config.counter=value
   "Sync diagnostics"->renderer.config.diagnostics=value;"Debug information"->renderer.config.debug=value
  }
  renderer.invalidate()
 }
 fun updateWindow(view:android.view.View,params:WindowManager.LayoutParams){wm.updateViewLayout(view,params)}
 override fun onDestroy(){
  if(::provider.isInitialized)provider.disconnect()
  if(::renderer.isInitialized)wm.removeView(renderer)
  if(::controls.isInitialized){runCatching{wm.removeView(controls.button)};runCatching{wm.removeView(controls.panel)}}
  super.onDestroy()
 }
 override fun onBind(intent:Intent?)=null
}

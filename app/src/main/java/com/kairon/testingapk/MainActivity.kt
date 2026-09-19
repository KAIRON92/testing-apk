package com.kairon.testingapk
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.kairon.testingapk.model.TestSession
import com.kairon.testingapk.overlay.OverlayService
import com.kairon.testingapk.registry.*
import com.kairon.testingapk.session.SessionStore
import java.security.MessageDigest

class MainActivity:AppCompatActivity(){
 private lateinit var status:TextView
 private lateinit var registry:GameRegistry
 private lateinit var sessions:SessionStore
 private var currentSession:TestSession?=null
 private val importCode=901
 override fun onCreate(b:Bundle?){super.onCreate(b);registry=GameRegistry(this);sessions=SessionStore(this);setContentView(buildUi())}
 private fun buildUi():ScrollView{
  val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(28,36,28,36)}
  root.setBackgroundColor(0xFF0B0F14.toInt())
  fun label(s:String,size:Float=14f)=TextView(this).apply{text=s;textSize=size;setTextColor(0xFFE8EEF5.toInt());setPadding(0,8,0,8)}
  root.addView(label("TESTINGAPK",28f));root.addView(label("MULTIPLAYER SECURITY • QA CONSOLE"))
  status=label("● READY  |  telemetry: authorized test-state interface");status.setTextColor(0xFF69F0AE.toInt());root.addView(status)
  val actions=listOf("＋  Add / Import Game" to ::importGame,"▶  Launch Test" to ::launchTest,"◎  Overlay Settings" to ::startOverlay,"▣  Start Test Session" to ::startSession,"■  Stop & Save Session" to ::stopSession,"⇩  Export Report" to ::exportSession)
  actions.forEach{(title,fn)->root.addView(Button(this).apply{text=title;setOnClickListener{fn()}})}
  root.addView(label("TEST DASHBOARD",20f))
  root.addView(label("Players   0        Bots   0\nVehicles  0        Items  0\nSync      READY     Anti-Cheat  MONITORING\nGlitches  0        Detection latency  —"))
  root.addView(label("MODULES",20f))
  listOf("Player Settings","Bot Settings","Vehicle Settings","Item / Weapon Settings","Synchronization Diagnostics","Anti-Cheat Monitor","Test Sessions","Logs").forEach{s->root.addView(Button(this).apply{text=s;setOnClickListener{status.text="● "+s.uppercase()+"  |  connected to QA session layer"}})}
  root.addView(label("Registered test instances",18f))
  registry.all().forEach{i->root.addView(label("• "+i.name+"  |  "+i.packageName+"  |  "+i.version))}
  root.addView(label("SECURITY BOUNDARY\nRuntime visualization uses authorized instrumentation telemetry. No arbitrary RAM reader, offset scanner, stealth hook, injection or anti-cheat bypass is included.",13f))
  return ScrollView(this).apply{addView(root)}
 }
 private fun importGame(){startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply{type="application/vnd.android.package-archive";addCategory(Intent.CATEGORY_OPENABLE)},importCode)}
 override fun onActivityResult(rc:Int,result:Int,data:Intent?){
  super.onActivityResult(rc,result,data)
  if(rc!=importCode||result!=Activity.RESULT_OK)return
  val uri=data?.data?:return
  contentResolver.openInputStream(uri)?.use{stream->
   val md=MessageDigest.getInstance("SHA-256");val buf=ByteArray(8192)
   while(true){val n=stream.read(buf);if(n<=0)break;md.update(buf,0,n)}
   val sha=md.digest().joinToString(""){"%02x".format(it)}
   registry.add(GameInstance(registry.newId(),"Imported QA Game","authorized.qa.game","unknown",sha))
   status.text="● IMPORTED  |  SHA-256 "+sha.take(16)+"…"
  }
 }
 private fun launchTest(){val i=registry.all().lastOrNull()?:return;val intent=packageManager.getLaunchIntentForPackage(i.packageName);if(intent==null){status.text="● WARNING  |  QA package not installed";return};intent.putExtra("QA_SESSION_ID",currentSession?.id?:"not-started");startActivity(intent);status.text="● RUNNING  |  "+i.name}
 private fun startSession(){val i=registry.all().lastOrNull();currentSession=TestSession(sessions.newId(),System.currentTimeMillis(),gameInstance=i?.name?:"Local QA");sessions.save(currentSession!!);status.text="● SESSION  |  "+currentSession!!.id}
 private fun stopSession(){currentSession?.let{done->currentSession=done.copy(endMs=System.currentTimeMillis());sessions.save(currentSession!!);status.text="● SAVED  |  "+currentSession!!.id}}
 private fun exportSession(){status.text="● REPORT  |  session JSON export layer ready"}
 private fun startOverlay(){if(!Settings.canDrawOverlays(this)){startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+packageName)));return};val i=Intent(this,OverlayService::class.java);if(android.os.Build.VERSION.SDK_INT>=26)startForegroundService(i)else startService(i);status.text="● OVERLAY  |  active"}
}

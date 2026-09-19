package com.kairon.testingapk
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kairon.testingapk.model.TestSession
import com.kairon.testingapk.overlay.OverlayService
import com.kairon.testingapk.registry.*
import com.kairon.testingapk.session.SessionStore
import com.kairon.testingapk.ui.MainScreen
import com.kairon.testingapk.ui.SettingsScreen
import java.io.File
import java.security.MessageDigest

class MainActivity:AppCompatActivity(){
 private lateinit var status:TextView
 private lateinit var registry:GameRegistry
 private lateinit var sessions:SessionStore
 private var currentSession:TestSession?=null
 private val importCode=901
 override fun onCreate(b:Bundle?){
  super.onCreate(b);registry=GameRegistry(this);sessions=SessionStore(this)
  status=TextView(this).apply{text="● READY  |  authorized telemetry interface";textSize=14f;setTextColor(0xFF69F0AE.toInt())}
  setContentView(MainScreen(this).build(status,listOf(
   "＋  Add / Import Game" to ::importGame,"▶  Launch Test" to ::launchTest,
   "◎  Start QA Overlay" to ::startOverlay,"▣  Start Test Session" to ::startSession,
   "■  Stop & Save Session" to ::stopSession,"⇩  Export Report" to ::exportSession,
   "⚙  Telemetry Settings" to ::telemetrySettings
  )))
 }
 private fun importGame(){startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply{type="application/vnd.android.package-archive";addCategory(Intent.CATEGORY_OPENABLE)},importCode)}
 override fun onActivityResult(rc:Int,result:Int,data:Intent?){
  super.onActivityResult(rc,result,data);if(rc!=importCode||result!=Activity.RESULT_OK)return
  val uri=data?.data?:return
  try{
   val temp=File.createTempFile("qa-import-",".apk",cacheDir)
   contentResolver.openInputStream(uri).use{input->temp.outputStream().use{out->input?.copyTo(out)}}
   val md=MessageDigest.getInstance("SHA-256")
   temp.inputStream().use{input->val buf=ByteArray(8192);while(true){val n=input.read(buf);if(n<=0)break;md.update(buf,0,n)}}
   val sha=md.digest().joinToString(""){"%02x".format(it)}
   val archive=packageManager.getPackageArchiveInfo(temp.absolutePath,0)
   val pkg=archive?.packageName?:"authorized.qa.game"
   val name=archive?.applicationInfo?.loadLabel(packageManager)?.toString()?:"Imported QA Game"
   val version=archive?.versionName?:"unknown"
   registry.add(GameInstance(registry.newId(),name,pkg,version,sha));temp.delete()
   status.text="● IMPORTED  |  "+name+"  |  SHA-256 "+sha.take(16)+"…"
  }catch(e:Exception){status.text="● ERROR  |  Import failed: "+e.message}
 }
 private fun launchTest(){val i=registry.all().lastOrNull()?:run{status.text="● WARNING  |  Add a QA build first";return};val intent=packageManager.getLaunchIntentForPackage(i.packageName);if(intent==null){status.text="● WARNING  |  QA package is not installed";return};intent.putExtra("QA_SESSION_ID",currentSession?.id?:"not-started");startActivity(intent);status.text="● RUNNING  |  "+i.name}
 private fun startSession(){val i=registry.all().lastOrNull();currentSession=TestSession(sessions.newId(),System.currentTimeMillis(),gameInstance=i?.name?:"Local QA");sessions.save(currentSession!!);status.text="● SESSION  |  "+currentSession!!.id}
 private fun stopSession(){currentSession?.let{currentSession=it.copy(endMs=System.currentTimeMillis());sessions.save(currentSession!!);status.text="● SAVED  |  "+currentSession!!.id}}
 private fun exportSession(){currentSession?.let{com.kairon.testingapk.report.ReportExporter.share(this,it)}?:run{status.text="● WARNING  |  No session to export"}}
 private fun startOverlay(){if(!Settings.canDrawOverlays(this)){startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+packageName)));return};val i=Intent(this,OverlayService::class.java);if(android.os.Build.VERSION.SDK_INT>=26)startForegroundService(i)else startService(i);status.text="● OVERLAY  |  active"}
 private fun telemetrySettings(){val prefs=getSharedPreferences("qa_settings",MODE_PRIVATE);SettingsScreen(this).show("Telemetry / Development Mode",listOf("Use development mock provider" to prefs.getBoolean("use_mock",true))){name,value->if(name=="Use development mock provider")prefs.edit().putBoolean("use_mock",value).apply()}}
}

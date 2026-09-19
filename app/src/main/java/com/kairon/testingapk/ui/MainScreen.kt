package com.kairon.testingapk.ui
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.*
class MainScreen(private val context:Context){
 fun build(status:TextView,actions:List<Pair<String,()->Unit>>):ScrollView{
  val root=LinearLayout(context).apply{orientation=LinearLayout.VERTICAL;setPadding(28,36,28,36);setBackgroundColor(Color.rgb(11,15,20))}
  fun label(s:String,size:Float)=TextView(context).apply{text=s;textSize=size;setTextColor(Color.rgb(232,238,245));setPadding(0,8,0,8)}
  root.addView(label("TESTINGAPK",28f))
  root.addView(label("MULTIPLAYER SECURITY • QA CONSOLE",13f))
  root.addView(status)
  actions.forEach{(title,action)->root.addView(Button(context).apply{text=title;setOnClickListener{action()}})}
  root.addView(label("LIVE TEST DASHBOARD",20f))
  root.addView(label("Players   0        Bots   0\nVehicles  0        Items  0\nSync      READY     Anti-Cheat  MONITORING\nGlitches  0        Detection latency  —",15f))
  root.addView(label("TEST CONTROLS",20f))
  listOf("Player Settings","Bot Settings","Vehicle Settings","Item / Weapon Settings","Synchronization Diagnostics","Anti-Cheat Monitor","Test Sessions","Logs").forEach{title->
   root.addView(Button(context).apply{text=title;setOnClickListener{status.text="● "+title.uppercase()+"  |  QA session layer ready"}})
  }
  root.addView(label("SECURITY BOUNDARY\nProduction visualization consumes authorized instrumentation telemetry. No arbitrary RAM reader, offset scanner, stealth hook, injection or anti-cheat bypass is included.",13f))
  return ScrollView(context).apply{addView(root)}
 }
}

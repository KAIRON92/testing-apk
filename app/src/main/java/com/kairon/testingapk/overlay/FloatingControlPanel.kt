package com.kairon.testingapk.overlay
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.*
class FloatingControlPanel(private val service:OverlayService){
 lateinit var button:TextView
 lateinit var panel:LinearLayout
 fun create():Pair<TextView,LinearLayout>{
  button=TextView(service).apply{
   text="QA";textSize=16f;gravity=Gravity.CENTER;setTextColor(Color.WHITE)
   background=bg(0xDD17212B.toInt(),18f)
  }
  panel=LinearLayout(service).apply{
   orientation=LinearLayout.VERTICAL;setPadding(16,12,16,12);visibility=View.GONE
   background=bg(0xEE111820.toInt(),14f)
  }
  val names=listOf("Players","Bots","Skeleton","Distance","Direction","Weapons","Items","Vehicles","Vehicle occupants","Entity counter","Sync diagnostics","Debug information")
  names.forEach{name->panel.addView(Switch(service).apply{text=name;isChecked=true;setTextColor(Color.WHITE);setOnCheckedChangeListener{_,v->service.setOverlayOption(name,v)}})}
  button.setOnClickListener{panel.visibility=if(panel.visibility==View.VISIBLE)View.GONE else View.VISIBLE}
  return button to panel
 }
 private fun bg(color:Int,r:Float)=GradientDrawable().apply{setColor(color);cornerRadius=r}
 fun draggable(v:View,params:android.view.WindowManager.LayoutParams){
  var sx=0f;var sy=0f;var ox=0;var oy=0
  v.setOnTouchListener{view,event->
   when(event.action){
    MotionEvent.ACTION_DOWN->{sx=event.rawX;sy=event.rawY;ox=params.x;oy=params.y;true}
    MotionEvent.ACTION_MOVE->{params.x=ox+(event.rawX-sx).toInt();params.y=oy+(event.rawY-sy).toInt();service.updateWindow(view,params);true}
    else->false
   }
  }
 }
}

package com.kairon.testingapk.ui
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.*
class SettingsScreen(private val context:Context){
 fun show(title:String,items:List<Pair<String,Boolean>>,onChanged:(String,Boolean)->Unit){
  val box=LinearLayout(context).apply{orientation=LinearLayout.VERTICAL;setPadding(32,24,32,24);setBackgroundColor(Color.rgb(18,23,30))}
  val dialog=android.app.AlertDialog.Builder(context).setTitle(title).setView(box).setPositiveButton("Done",null).create()
  items.forEach{(name,value)->box.addView(Switch(context).apply{text=name;isChecked=value;setOnCheckedChangeListener{_,checked->onChanged(name,checked)}})}
  dialog.show()
 }
}

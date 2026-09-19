package com.kairon.testingapk.registry
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID
class GameRegistry(context:Context){
 private val p=context.getSharedPreferences("instances",Context.MODE_PRIVATE)
 private val gson=Gson()
 fun all():List<GameInstance>{val raw=p.getString("list","[]")?:"[]";return gson.fromJson(raw,object:TypeToken<List<GameInstance>>(){}.type)}
 fun add(i:GameInstance){val n=all().toMutableList();n.removeAll{it.id==i.id};n.add(i);p.edit().putString("list",gson.toJson(n)).apply()}
 fun newId()="GAME-"+UUID.randomUUID().toString().take(8).uppercase()
}

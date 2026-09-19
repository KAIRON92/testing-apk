package com.kairon.testingapk.session
import android.content.Context
import com.google.gson.GsonBuilder
import com.kairon.testingapk.model.TestSession
import java.util.UUID
class SessionStore(context:Context) {
    private val prefs=context.getSharedPreferences("qa_sessions",Context.MODE_PRIVATE)
    private val gson=GsonBuilder().setPrettyPrinting().create()
    fun newId()="QA-"+UUID.randomUUID().toString().take(8).uppercase()
    fun save(session:TestSession){prefs.edit().putString(session.id,gson.toJson(session)).apply()}
    fun all():List<TestSession>=prefs.all.values.mapNotNull{runCatching{gson.fromJson(it.toString(),TestSession::class.java)}.getOrNull()}
    fun json(session:TestSession)=gson.toJson(session)
}

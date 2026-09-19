package com.kairon.testingapk.overlay
import android.app.*
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.kairon.testingapk.diagnostics.SyncAnalyzer
import com.kairon.testingapk.telemetry.MockTelemetryProvider
class OverlayService:Service(){
    private lateinit var wm:WindowManager
    private lateinit var renderer:OverlayRenderer
    private lateinit var provider:MockTelemetryProvider
    private val analyzer=SyncAnalyzer()
    override fun onCreate(){
        super.onCreate()
        val nm=getSystemService(NotificationManager::class.java)
        if(Build.VERSION.SDK_INT>=26)nm.createNotificationChannel(NotificationChannel("qa","QA Overlay",NotificationManager.IMPORTANCE_LOW))
        startForeground(42,NotificationCompat.Builder(this,"qa").setContentTitle("TestingAPK QA").setContentText("Authorized debug overlay active").setSmallIcon(android.R.drawable.ic_menu_info_details).build())
        if(!Settings.canDrawOverlays(this)){stopSelf();return}
        wm=getSystemService(WINDOW_SERVICE) as WindowManager
        renderer=OverlayRenderer(this)
        val type=if(Build.VERSION.SDK_INT>=26)WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE
        wm.addView(renderer,WindowManager.LayoutParams(-1,-1,type,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,PixelFormat.TRANSLUCENT))
        provider=MockTelemetryProvider()
        provider.connect("dev"){s->val e=analyzer.analyze(s);android.os.Handler(mainLooper).post{renderer.update(s,e)}}
    }
    override fun onDestroy(){if(::provider.isInitialized)provider.disconnect();if(::renderer.isInitialized)wm.removeView(renderer);super.onDestroy()}
    override fun onBind(intent:Intent?)=null
}

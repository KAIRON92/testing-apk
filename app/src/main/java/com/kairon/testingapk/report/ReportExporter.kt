package com.kairon.testingapk.report
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.google.gson.GsonBuilder
import com.kairon.testingapk.model.TestSession
import java.io.File
object ReportExporter{
 fun share(context:Context,session:TestSession){
  val file=File(context.cacheDir,session.id+".json")
  file.writeText(GsonBuilder().setPrettyPrinting().create().toJson(session))
  val uri=FileProvider.getUriForFile(context,context.packageName+".files",file)
  context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply{type="application/json";putExtra(Intent.EXTRA_STREAM,uri);addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)},"Export QA report"))
 }
}
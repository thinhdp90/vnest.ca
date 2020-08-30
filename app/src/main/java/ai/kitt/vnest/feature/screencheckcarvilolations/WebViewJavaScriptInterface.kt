package ai.kitt.vnest.feature.screencheckcarvilolations

import ai.kitt.vnest.App
import ai.kitt.vnest.R
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Message
import android.util.Log
import android.webkit.JavascriptInterface
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextRecognizer
import org.jsoup.Jsoup

class WebViewJavaScriptInterface(val fragment: WebViewFragment) {
    companion object{
        const val JAVA_SCRIPT_INTERFACE_FUNC = "getHtml"
    }
    @JavascriptInterface
    fun showHTML(html: String) {
        Log.e("html", html)
        val document = Jsoup.parse(html)
        val text = document.select("#bodyPrint")[0]
        Log.e("Text", text.select("div")[0].text())
        val src = document.select(".flex")[0].select("img")[0].attr("src")

        Log.e("src",src)
        val message = Message()
        message.what = 1
        message.obj = src
        message.target = fragment.handle
        message.sendToTarget()
    }
}
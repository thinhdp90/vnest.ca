package ai.kitt.vnest.feature.screencheckcarvilolations

import ai.kitt.vnest.App
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.webkit.JavascriptInterface
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextRecognizer
import org.jsoup.Jsoup

class WebViewJavaScriptInterface() {
    companion object{
        const val JAVA_SCRIPT_INTERFACE_FUNC = "get html"
    }
    @JavascriptInterface
    fun showHTML(html: String) {
        Log.e("html", html)
        val document = Jsoup.parse(html)
        val text = document.select("#bodyPrint")[0]
        Log.e("Text", text.select("div")[0].text())
        val src = document.select(".formBSX .item .flex")[0].select("img")[0].attr("src")
        val textRecognizer = TextRecognizer.Builder(App.get())
                .build()

        Glide.with(App.get())
                .asBitmap()
                .addListener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {

                        return false
                    }

                    override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        val imageFrame = Frame.Builder()
                                .setBitmap(resource)
                                .build()
                        val textBlocks = textRecognizer.detect(imageFrame)
                        for (i in 0 until textBlocks.size()) {
                            Log.e("Text", textBlocks.get(i).value)
                        }
                        return false
                    }


                }).submit()

    }
}
package ai.kitt.vnest.feature.screencheckcarvilolations

import ai.kitt.vnest.App
import ai.kitt.vnest.R
import ai.kitt.vnest.base.BaseFragment
import ai.kitt.vnest.feature.activitymain.MainActivity
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextRecognizer
import kotlinx.android.synthetic.main.fragment_web_view.view.*

class WebViewFragment : BaseFragment(R.layout.fragment_web_view) {
    companion object {
        const val EXTRA_URL = "extra_url"

        @JvmField
        val TAG: String = WebViewFragment::class.java.name
        @JvmStatic
        fun startThis(url: String?): WebViewFragment = WebViewFragment().apply {
            val bundle = arguments ?: Bundle()
            bundle.putString(EXTRA_URL, url)
            arguments = bundle
        }
    }
    val textRecognizer = TextRecognizer.Builder(App.get())
            .build()
    val handle = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            val what = msg?.what
            if(what == 1) {
                val src = msg.obj as String
                (requireActivity() as MainActivity).speak(src,false)
                Glide.with(this@WebViewFragment)
                        .asBitmap()
                        .load(src)
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
                                    Log.e("Text", textBlocks[i].value)
                                }
                                return false
                            }

                        }).into(requireView().findViewById(R.id.image))
            }
        }
    }
    val url: String by lazy { arguments?.getString(EXTRA_URL)!! }
    override fun initView(view: View) {
        view.webView.settings.javaScriptEnabled = true
        view.webView.addJavascriptInterface(WebViewJavaScriptInterface(this), WebViewJavaScriptInterface.JAVA_SCRIPT_INTERFACE_FUNC)
        view.webView.webViewClient = WebViewClient(requireContext())
    }

    override fun initAction(view: View) {
        view.webView.loadUrl(url)
    }
}
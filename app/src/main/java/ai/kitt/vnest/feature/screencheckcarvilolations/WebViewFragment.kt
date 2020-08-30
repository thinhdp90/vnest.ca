package ai.kitt.vnest.feature.screencheckcarvilolations

import ai.kitt.vnest.R
import ai.kitt.vnest.base.BaseFragment
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_web_view.view.*

class WebViewFragment : BaseFragment(R.layout.fragment_web_view) {
    companion object {
        const val EXTRA_URL = "extra_url"

        @JvmStatic
        fun startThis(url: String?): WebViewFragment = WebViewFragment().apply {
            val bundle = arguments ?: Bundle()
            bundle.putString(EXTRA_URL, url)
            arguments = bundle
        }
    }

    val url: String by lazy { arguments?.getString(EXTRA_URL)!! }
    override fun initView(view: View) {
        view.webView.settings.javaScriptEnabled = true
        view.webView.addJavascriptInterface(WebViewJavaScriptInterface(), "HtmlReader")
        view.webView.webViewClient = WebViewClient(requireContext())
    }

    override fun initAction(view: View) {
        view.webView.loadUrl(url)
    }
}
package ai.kitt.vnest.feature.screencheckcarvilolations

import ai.kitt.vnest.R
import android.content.Context
import android.view.LayoutInflater
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog

class WebViewClient (val context: Context) : WebViewClient() {
    lateinit var progressDialog: AlertDialog
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if(!this::progressDialog.isInitialized) {
            progressDialog = AlertDialog.Builder(context)
                    .setView(LayoutInflater.from(context).inflate(R.layout.dialog_progress,null, false))
                    .setCancelable(false)
                    .show()
        } else if (!progressDialog.isShowing) {
            progressDialog.show()
        }
        return super.shouldOverrideUrlLoading(view, request)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        if(this::progressDialog.isInitialized && progressDialog.isShowing) {
            progressDialog.dismiss()
        }
        view?.loadUrl("javascript:window.${WebViewJavaScriptInterface.JAVA_SCRIPT_INTERFACE_FUNC}.showHTML(document.getElementsByTagName('body')[0].innerHTML);");
    }

}
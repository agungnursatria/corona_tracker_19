package tech.awesome.coronatrack.ui.webview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_webview.*
import tech.awesome.coronatrack.R
import tech.awesome.coronatrack.ui.base.BaseActivity
import tech.awesome.utils.ExtraConstant
import tech.awesome.utils.extension.gone
import tech.awesome.utils.extension.visible

class WebviewActivity : BaseActivity() {

    companion object {
        fun getIntent(context: Context?, url: String, title: String) : Intent =
            Intent(context, WebviewActivity::class.java).apply {
                putExtra(ExtraConstant.EXTRA_URL, url)
                putExtra(ExtraConstant.EXTRA_TITLE, title)
            }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        setupToolbar(toolbar, true)

        val title = intent.getStringExtra(ExtraConstant.EXTRA_TITLE) ?: ""
        val url = intent.getStringExtra(ExtraConstant.EXTRA_URL)

        tv_title.text = title

        webview.apply {
            loadUrl(if (!url.isNullOrBlank()) url else "about:blank")
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowContentAccess = true
                useWideViewPort = true
                allowContentAccess = true
                allowFileAccess = true
                javaScriptCanOpenWindowsAutomatically = true
            }

            webViewClient = WebViewClient()
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        pb.setProgress(newProgress, true)
                    } else {
                        pb.progress = newProgress
                    }

                    if (newProgress == 100) {
                        pb.gone()
                        pb.progress = 0
                    } else {
                        pb.visible()
                    }
                    super.onProgressChanged(view, newProgress)
                }
            }
        }


    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (webview.canGoBack()) {
                        webview.goBack()
                    } else {
                        finish()
                    }
                    return true
                }
            }

        }
        return super.onKeyDown(keyCode, event)
    }

}

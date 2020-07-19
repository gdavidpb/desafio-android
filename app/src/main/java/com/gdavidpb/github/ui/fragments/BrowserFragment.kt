package com.gdavidpb.github.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.gdavidpb.github.BuildConfig
import com.gdavidpb.github.R
import com.gdavidpb.github.utils.LiveCallback
import com.gdavidpb.github.utils.backCallback
import com.gdavidpb.github.utils.onBackPressed
import com.gdavidpb.github.utils.requireActionBar
import kotlinx.android.synthetic.main.fragment_browser.*

@SuppressLint("SetJavaScriptEnabled")
class BrowserFragment : Fragment() {

    private val args by navArgs<BrowserFragmentArgs>()

    private val callback by backCallback {
        if (webView.canGoBack())
            webView.goBack()
        else {
            remove()
            onBackPressed()
        }
    }

    private val toolbar by lazy { requireActionBar() }

    private val liveOnReceivedTitle = LiveCallback<String>(this) { title ->
        toolbar.title = title
    }

    private val liveOnPageFinished = LiveCallback<Unit>(this) {
        sWebView.isRefreshing = false

        callback.isEnabled = webView.canGoBack()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_browser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(sWebView) {
            isEnabled = false

            isRefreshing = true
        }

        with(webView) {
            settings.javaScriptEnabled = true

            webChromeClient = BrowserWebChromeClient()
            webViewClient = BrowserWebViewClient()

            loadUrl(args.url)
        }
    }

    private inner class BrowserWebChromeClient : WebChromeClient() {
        override fun onReceivedTitle(view: WebView, title: String) {
            liveOnReceivedTitle.postCallback(title)
        }
    }

    private inner class BrowserWebViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            liveOnPageFinished.postCallback(Unit)
        }

        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            return request.url.host != BuildConfig.API_HOST
        }
    }
}
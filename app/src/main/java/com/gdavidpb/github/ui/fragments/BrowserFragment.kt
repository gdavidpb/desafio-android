package com.gdavidpb.github.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.gdavidpb.github.R
import kotlinx.android.synthetic.main.fragment_browser.*

@SuppressLint("SetJavaScriptEnabled")
class BrowserFragment : Fragment() {

    private val args by navArgs<BrowserFragmentArgs>()

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

            webChromeClient = WebChromeClient()

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    sWebView.isRefreshing = false
                }
            }

            webView.loadUrl(args.url)
        }
    }
}
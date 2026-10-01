package com.jellygram

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.webkit.*
import android.widget.ProgressBar
import androidx.fragment.app.Fragment

// Вкладка TikTok: просто WebView на https://www.tiktok.com/foryou (или /explore)
// Листание вертикальных видео работает нативно внутри TikTok web.
// Плюс: не нужен TikTok API key, сессия/авторизация сохраняется в WebView.
class TikTokWebViewFragment : Fragment() {

    private var webView: WebView? = null
    private var progress: ProgressBar? = null
    private val HOME_URL = "https://www.tiktok.com/foryou"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_tiktok, container, false)
        val wv = v.findViewById<WebView>(R.id.tiktok_webview)
        progress = v.findViewById(R.id.tiktok_progress)
        webView = wv

        wv.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            mediaPlaybackRequiresUserGesture = false
            loadWithOverviewMode = true
            useWideViewPort = true
            userAgentString = userAgentString + " JellyGramTikTokTab/1.0"
        }
        // Чтобы видео/куки/авторизация жили между перезапусками
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(wv, true)

        wv.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                // Все tiktok.com открываем внутри, внешние — тоже внутри чтобы не выкидывало в браузер
                return false
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                progress?.visibility = View.GONE
            }
        }
        wv.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress < 100) {
                    progress?.visibility = View.VISIBLE
                    progress?.progress = newProgress
                } else {
                    progress?.visibility = View.GONE
                }
            }
        }

        if (savedInstanceState != null) {
            wv.restoreState(savedInstanceState)
        } else if (wv.url == null) {
            wv.loadUrl(HOME_URL)
        }
        return v
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView?.saveState(outState)
    }

    fun onBackPressed(): Boolean {
        val wv = webView ?: return false
        return if (wv.canGoBack()) { wv.goBack(); true } else false
    }

    fun pauseWebView() {
        webView?.onPause()
        // ставим JS-видео на паузу: dispatch audio focus loss
        try { webView?.loadUrl("javascript:(function(){document.querySelectorAll('video').forEach(v=>v.pause())})()") } catch (_: Exception) {}
    }

    fun resumeWebView() {
        webView?.onResume()
    }

    override fun onDestroyView() {
        // НЕ destroy() полностью — чтобы при переключении Чаты<->TikTok не терять позицию.
        // destroy делаем только при убиении фрагмента.
        webView?.onPause()
        webView = null
        super.onDestroyView()
    }
}

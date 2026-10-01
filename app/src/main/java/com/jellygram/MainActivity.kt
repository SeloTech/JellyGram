package com.jellygram

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

// Главный экран JellyGram: снизу панель [ Сообщения | TikTok ]
// Логика: переключаем фрагменты, WebView с TikTok НЕ пересоздаём чтобы не терять скролл.
class MainActivity : AppCompatActivity() {

    private var chatsFragment: Fragment? = null
    private var tiktokFragment: Fragment? = null
    private var activeFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chatsFragment = ChatsFragment()
        tiktokFragment = TikTokWebViewFragment()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, tiktokFragment!!, "tiktok")
                .hide(tiktokFragment!!)
                .add(R.id.fragment_container, chatsFragment!!, "chats")
                .commit()
            activeFragment = chatsFragment
        } else {
            chatsFragment = supportFragmentManager.findFragmentByTag("chats")
            tiktokFragment = supportFragmentManager.findFragmentByTag("tiktok")
            activeFragment = if (supportFragmentManager.findFragmentByTag("chats")?.isHidden == false) chatsFragment else tiktokFragment
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_chats -> {
                    switchTo(chatsFragment!!)
                    true
                }
                R.id.nav_tiktok -> {
                    switchTo(tiktokFragment!!)
                    true
                }
                else -> false
            }
        }
    }

    private fun switchTo(target: Fragment) {
        if (activeFragment == target) return
        supportFragmentManager.beginTransaction()
            .hide(activeFragment!!)
            .show(target)
            .commit()
        activeFragment = target

        // Удобно: когда уходим с TikTok в чаты — ставим видео на паузу
        if (target is ChatsFragment) {
            (tiktokFragment as? TikTokWebViewFragment)?.pauseWebView()
        } else {
            (tiktokFragment as? TikTokWebViewFragment)?.resumeWebView()
        }
    }

    override fun onBackPressed() {
        // Сначала отдаём Back WebView (назад по истории TikTok), только потом выходим
        if (activeFragment is TikTokWebViewFragment &&
            (activeFragment as TikTokWebViewFragment).onBackPressed()
        ) return
        super.onBackPressed()
    }
}

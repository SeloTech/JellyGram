package org.telegram.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.*;
import android.webkit.*;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;

/**
 * JellyGram: вкладка TikTok для exteraGram.
 * Положить в: TMessagesProj/src/main/java/org/telegram/ui/TikTokTabFragment.java
 *
 * Это BaseFragment exteraGram/Telegram, внутри WebView на TikTok.
 * Листание работает нативно (вертикальный свайп внутри сайта).
 */
public class TikTokTabFragment extends BaseFragment {

    private WebView webView;
    private static final String HOME = "https://www.tiktok.com/foryou";

    @Override
    public View createView(android.content.Context context) {
        actionBar.setTitle("TikTok");
        // скрываем лишнее чтобы было как в TikTok полноэкранно
        actionBar.setAddToContainer(false);

        FrameLayout root = new FrameLayout(context);
        WebView wv = new WebView(context);
        root.addView(wv, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT));
        webView = wv;

        initWebView();
        if (webView.getUrl() == null) webView.loadUrl(HOME);

        fragmentView = root;
        return root;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);

        CookieManager.getInstance().setAcceptCookie(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false; // всё внутри вкладки
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
    }

    @Override
    public boolean onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
            try { webView.loadUrl("javascript:(function(){document.querySelectorAll('video').forEach(v=>v.pause())})()"); }
            catch (Exception ignore) {}
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webView != null) webView.onResume();
    }

    // ===== Как добавить нижнюю панель в exteraGram (LaunchActivity) =====
    //
    // Вариант 1 (самый простой, рекомендуется):
    // В org.telegram.ui.LaunchActivity после init:
    //
    //   BottomNavigationView bottomNav = new BottomNavigationView(this);
    //   bottomNav.inflateMenu(R.menu.bottom_nav_menu); // nav_chats + nav_tiktok
    //   // при nav_chats -> show DialogsActivity (actionBarLayout), при nav_tiktok -> present TikTokTabFragment
    //   bottomNav.setOnItemSelectedListener(item -> {
    //       if (item.getItemId() == R.id.nav_tiktok) {
    //           if (!(actionBarLayout.getLastFragment() instanceof TikTokTabFragment))
    //               actionBarLayout.presentFragment(new TikTokTabFragment());
    //           // ставим видео на паузу при уходе смотри TikTokTabFragment.onPause()
    //           return true;
    //       } else {
    //           // вернуться к чатам: снять верхний фрагмент до DialogsActivity
    //           actionBarLayout.removeFragmentFromStack(actionBarLayout.getLastFragment());
    //           return true;
    //       }
    //   });
    //   // добавить bottomNav в drawerLayoutContainer под actionBarLayout (FrameLayout снизу)
    //
    // Вариант 2: кастомный BottomTab в DialogsActivity.createView — добавить FrameLayout контейнер
    // для WebView прямо внутри DialogsActivity и переключать View.GONE/VISIBLE по табу.
    // Так TikTok не будет пересоздаваться и скролл сохранится. См. MainActivity.switchTo() в прототипе.
}

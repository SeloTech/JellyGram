# JellyGram — Telegram-мод на базе exteraGram + вкладка TikTok снизу

Прототип уже лежит в `app/src/main/...` (чистый Android + BottomNavigationView).
Патч для настоящего exteraGram — в `exteraPatch/`.

## Что сделано

1. **Нижняя панель на главном экране**: `Сообщения | TikTok`
   - `MainActivity.kt` — переключает фрагменты без пересоздания, чтобы лента TikTok не сбрасывалась.
   - При уходе в `Чаты` видео ставится на паузу (`pauseWebView()`), при возврате — `resumeWebView()`.
   - Кнопка Back сначала листает историю TikTok, только потом выходит.
2. **Вкладка TikTok = WebView** (ты выбрал этот вариант):
   - `TikTokWebViewFragment.kt` открывает `https://www.tiktok.com/foryou`
   - Листание вертикальных видео работает нативно внутри сайта TikTok.
   - Куки/авторизация сохраняются, JS + DOM storage включены.
   - Не нужен TikTok API key.

## Как перенести в exteraGram (исходники)

exteraGram — это форк официального Telegram Android (~1 ГБ, собирается только в Android Studio с NDK).
В эту папку его целиком не клонировал, чтобы не ломать проект. Делается так:

1. Клонируешь рядом:
   ```
   git clone https://github.com/exteraSquad/exteraGram.git
   ```
2. Создаёшь файл `API_KEYS` в корне exteraGram:
   ```
   APP_ID = 33403947
   APP_HASH = 633da13382261ccdbee53e39adebf847
   ```
   ⚠️ Твои `APP_ID/HASH` сейчас светятся в чате — лучше выпусти новые на https://core.telegram.org/api/obtaining_api_id
   IP `149.154.167.40:443`, `149.154.167.50:443` и RSA-ключи хардкодить не надо — они уже есть в `TMessagesProj/.../BuildVars.java`.
3. Копируешь `exteraPatch/TMessagesProj/org/telegram/ui/TikTokTabFragment.java`
   в `exteraGram/TMessagesProj/src/main/java/org/telegram/ui/`
4. В `LaunchActivity` добавляешь `BottomNavigationView` (инструкция в комментариях `TikTokTabFragment.java`).
   Идея: `nav_chats` = обычный `DialogsActivity`, `nav_tiktok` = `presentFragment(new TikTokTabFragment())`.

## Сборка

- Прототип: открыть `android/` в Android Studio → Run.
  Нужны зависимости `com.google.android.material:material:1.12.0`.
- exteraGram: `./gradlew assembleAfatRelease` (долго, нужен signing key + MAPS key).

## Важно про правила

- WebView на tiktok.com — самый безопасный способ: ты не парсишь/не скачиваешь чужие видео, просто открываешь сайт.
- Если захочешь нативную ленту (ExoPlayer + свой сервер/канал Telegram) — это уже отдельный бэкенд и модерация.
- Для публикации в Google Play такой мод с TikTok внутри скорее всего завернут — распространять только как APK.

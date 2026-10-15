# Règles ProGuard/R8 pour la version release.
# Conserver les classes liées à la WebView/JS bridge si on en ajoute un jour :
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

package com.afriboost.app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

/**
 * Écran de démarrage natif (logo + fond bleu AfriBoost).
 * Redirige ensuite vers l'accueil natif (premier lancement) ou directement
 * vers l'app principale (lancements suivants), après un court délai.
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val prefs: SharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            val hasSeenHome = prefs.getBoolean(KEY_HAS_SEEN_HOME, false)

            val next = if (hasSeenHome) {
                Intent(this, MainActivity::class.java)
            } else {
                prefs.edit().putBoolean(KEY_HAS_SEEN_HOME, true).apply()
                Intent(this, HomeActivity::class.java)
            }
            startActivity(next)
            finish()
        }, SPLASH_DELAY_MS)
    }

    companion object {
        private const val SPLASH_DELAY_MS = 1200L
        const val PREFS_NAME = "afriboost_prefs"
        const val KEY_HAS_SEEN_HOME = "has_seen_home"
    }
}

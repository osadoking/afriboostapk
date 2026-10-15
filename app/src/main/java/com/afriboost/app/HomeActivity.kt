package com.afriboost.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afriboost.app.databinding.ActivityHomeBinding

/**
 * Vraie page d'accueil native de l'app (pas une page web), affichée au tout
 * premier lancement. Donne le choix : créer un compte, se connecter, ou
 * explorer directement les services -- chaque bouton ouvre MainActivity
 * (WebView) sur la bonne page du site.
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignup.setOnClickListener {
            openMain(SiteRoutes.SIGNUP)
        }
        binding.btnLogin.setOnClickListener {
            openMain(SiteRoutes.LOGIN)
        }
        binding.btnGuest.setOnClickListener {
            openMain(SiteRoutes.HOME)
        }
    }

    private fun openMain(path: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_START_PATH, path)
        startActivity(intent)
        finish()
    }
}

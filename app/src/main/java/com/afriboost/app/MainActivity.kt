package com.afriboost.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.afriboost.app.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Écran principal : WebView plein écran synchronisée avec afriboost.com,
 * habillée d'une navigation native en bas (Accueil / Services / Commandes /
 * Portefeuille / Profil), d'un pull-to-refresh, d'un écran hors-ligne natif,
 * de la gestion du bouton retour, de l'upload de fichiers et des deep links
 * de retour de paiement.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* Ignoré si refusé : l'app fonctionne normalement sans notifications */ }

    private val fileChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data = result.data
        val uris: Array<Uri>? = when {
            result.resultCode != RESULT_OK -> null
            data?.clipData != null -> {
                val clip = data.clipData!!
                Array(clip.itemCount) { i -> clip.getItemAt(i).uri }
            }
            data?.data != null -> arrayOf(data.data!!)
            else -> null
        }
        filePathCallback?.onReceiveValue(uris)
        filePathCallback = null
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWebView()
        setupBottomNav()
        setupSwipeRefresh()
        setupBackPress()

        binding.btnRetry.setOnClickListener { loadCurrentOrStart() }

        val startPath = intent.getStringExtra(EXTRA_START_PATH) ?: homePath()
        loadPath(startPath)

        handleDeepLink(intent)
        requestNotificationPermissionIfNeeded()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val data = intent?.data ?: return
        when {
            // afriboost://payment-return?status=...  -> retour d'une page de paiement externe
            data.scheme == "afriboost" && data.host == "payment-return" -> {
                val status = data.getQueryParameter("status")
                loadPath(SiteRoutes.WALLET + if (status != null) "?payment_status=$status" else "")
            }
            // https://www.afriboost.com/app/... -> ouvrir le chemin correspondant dans l'app
            data.host?.endsWith("afriboost.com") == true -> {
                loadPath(data.path?.removePrefix("/app") ?: SiteRoutes.HOME)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val webView = binding.webView
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            setSupportMultipleWindows(false)
            mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
            userAgentString = "$userAgentString AfriBoostApp/1.0"
            cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
        }

        android.webkit.CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(webView, true)
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url
                val host = url.host ?: ""
                // On reste dans la WebView pour le domaine afriboost.com (et sous-domaines :
                // reseller.afriboost.com, etc.). Tout le reste (paiement mobile money,
                // WhatsApp...) s'ouvre dans le navigateur/app externe du téléphone.
                return if (host.endsWith("afriboost.com")) {
                    false
                } else {
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, url))
                    } catch (e: Exception) {
                        Log.w(TAG, "Impossible d'ouvrir un gestionnaire externe pour $url", e)
                    }
                    true
                }
            }

            override fun onPageFinished(view: WebView, url: String?) {
                binding.swipeRefresh.isRefreshing = false
                showContent()
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                if (request.isForMainFrame) {
                    binding.swipeRefresh.isRefreshing = false
                    showOffline()
                }
            }

            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                // Sécurité : on ne fait jamais confiance à un certificat invalide
                handler.cancel()
                showOffline()
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView,
                callback: ValueCallback<Array<Uri>>,
                params: FileChooserParams
            ): Boolean {
                filePathCallback = callback
                val intent = params.createIntent()
                return try {
                    fileChooserLauncher.launch(intent)
                    true
                } catch (e: Exception) {
                    filePathCallback = null
                    false
                }
            }
        }
    }

    private fun setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            val path = when (item.itemId) {
                R.id.nav_home -> homePath()
                R.id.nav_services -> SiteRoutes.SERVICES
                R.id.nav_orders -> SiteRoutes.ORDERS
                R.id.nav_wallet -> SiteRoutes.WALLET
                R.id.nav_profile -> SiteRoutes.PROFILE
                else -> SiteRoutes.HOME
            }
            loadPath(path)
            true
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.afriboost_blue)
        binding.swipeRefresh.setOnRefreshListener { loadCurrentOrStart() }
    }

    private fun setupBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun loadPath(path: String) {
        if (!isNetworkAvailable()) {
            showOffline()
            return
        }
        showContent()
        binding.webView.loadUrl(SiteRoutes.full(path))
    }

    private fun loadCurrentOrStart() {
        if (!isNetworkAvailable()) {
            showOffline()
            binding.swipeRefresh.isRefreshing = false
            return
        }
        showContent()
        binding.webView.reload()
    }

    private fun showOffline() {
        binding.offlineView.visibility = View.VISIBLE
        binding.swipeRefresh.visibility = View.GONE
    }

    private fun showContent() {
        binding.offlineView.visibility = View.GONE
        binding.swipeRefresh.visibility = View.VISIBLE
    }

    /**
     * "Accueil" doit être le tableau de bord une fois connecté, et la page
     * publique sinon. On déduit la connexion de la présence d'un cookie de
     * session pour afriboost.com (posé par le site lui-même au login/signup,
     * qui restent gérés par le site -- l'app ne fait qu'observer le cookie).
     */
    private fun homePath(): String = if (isLoggedIn()) SiteRoutes.DASHBOARD else SiteRoutes.HOME

    private fun isLoggedIn(): Boolean {
        val cookies = android.webkit.CookieManager.getInstance().getCookie(SiteRoutes.BASE_URL)
        return !cookies.isNullOrBlank()
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(ConnectivityManager::class.java) ?: return true
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    companion object {
        private const val TAG = "MainActivity"
        const val EXTRA_START_PATH = "extra_start_path"
    }
}

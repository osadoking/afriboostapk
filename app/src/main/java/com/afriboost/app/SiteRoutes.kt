package com.afriboost.app

/**
 * Chemins du site utilisés par l'app (menu bas + écran d'accueil natif).
 *
 * IMPORTANT : BASE_URL et les chemins ci-dessous sont mes meilleures suppositions
 * à partir de ce que je connais du site (ex: "neworder" pour commander, "/admin"
 * existe côté admin donc les routes publiques suivent probablement un schéma
 * similaire). À confirmer/corriger avec King Osado une fois l'APK testé --
 * si une page tombe sur un 404, c'est ici qu'il faut corriger.
 */
object SiteRoutes {
    const val BASE_URL = "https://www.afriboost.com"

    const val HOME = "/"
    const val LOGIN = "/login"
    const val SIGNUP = "/signup"
    const val SERVICES = "/services"
    const val ORDERS = "/orders"
    const val WALLET = "/addfunds"
    const val PROFILE = "/profile"

    fun full(path: String): String = BASE_URL.trimEnd('/') + path
}

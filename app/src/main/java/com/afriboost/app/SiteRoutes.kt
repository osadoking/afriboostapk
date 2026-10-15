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
    // Page affichée une fois connecté quand on appuie sur "Accueil" (tableau de
    // bord). À CONFIRMER : beaucoup de scripts SMM panel utilisent "/dashboard"
    // ou simplement "/" (qui affiche le tableau de bord automatiquement si une
    // session est active) -- dis-moi laquelle est la bonne après ton test.
    const val DASHBOARD = "/dashboard"
    const val LOGIN = "/login"
    const val SIGNUP = "/signup"
    const val SERVICES = "/services"
    const val ORDERS = "/orders"
    const val WALLET = "/addfunds"
    // À CONFIRMER : sur le site, "Profil" ouvre-t-il une vraie page (ex: /profile
    // ou /settings), ou seulement un menu déroulant sur le tableau de bord ?
    const val PROFILE = "/profile"

    fun full(path: String): String = BASE_URL.trimEnd('/') + path
}

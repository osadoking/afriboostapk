# AfriBoost — App Android

Application Android native (Kotlin) habillant afriboost.com d'une vraie
expérience app : page d'accueil native, navigation en bas, écran hors-ligne,
notifications push -- pensée pour passer la revue Google Play.

## Où en est-on (Phase 1)

- [x] Structure du projet (Gradle, Kotlin, AGP 8.13, compileSdk/targetSdk 36)
- [x] Écran de démarrage (splash) natif
- [x] Page d'accueil native (premier lancement) : Créer un compte / Se
      connecter / Découvrir les services
- [x] Écran principal : WebView + navigation en bas (Accueil, Services,
      Commandes, Portefeuille, Profil) + pull-to-refresh + écran hors-ligne +
      upload de fichiers + deep links retour paiement
- [x] Compilation automatique d'un APK de test à chaque push (GitHub Actions)
- [ ] Notifications push (Firebase) — en attente de `google-services.json`
- [ ] Vrai logo (actuellement un badge provisoire)
- [ ] Icône/assets de la fiche Play Store

## Récupérer l'APK à tester

1. Va dans l'onglet **Actions** de ce dépôt GitHub
2. Ouvre le dernier run de "Build APK de test" (vert = réussi)
3. Dans "Artifacts" en bas de page, télécharge `afriboost-debug-apk`
4. Décompresse le .zip téléchargé → `app-debug.apk`
5. Transfère ce fichier sur ton Android (ou lien direct) et installe-le
   (il faudra peut-être autoriser "Sources inconnues" la première fois)

Un nouvel APK se recompile automatiquement à chaque fois que du code est
poussé sur `main`.

## ⚠️ À vérifier / corriger avec moi

**Les chemins du site sont des suppositions** (fichier `SiteRoutes.kt`) :
j'ai deviné `/login`, `/signup`, `/services`, `/orders`, `/addfunds`,
`/profile` à partir de ce que je connais du site. Teste chaque onglet de la
navigation du bas dans l'app : si un onglet tombe sur une page 404 ou la
mauvaise page, dis-moi l'URL exacte et je corrige en 30 secondes.

**Les couleurs** (`colors.xml`) sont une approximation du bleu/blanc/jaune
du site. Donne-moi les codes hexadécimaux exacts (visibles dans l'admin,
réglages de couleurs) pour que ce soit identique au pixel près.

**Le logo** est un badge provisoire (flèche de croissance). Envoie-moi le
vrai fichier logo (de préférence un PNG carré, fond transparent, haute
résolution) et je l'intègre comme icône de l'app et sur le splash.

## Mettre en place Firebase (notifications push)

1. console.firebase.google.com → "Ajouter un projet" → nomme-le "Afriboost"
2. Dans le projet : "Ajouter une application" → Android →
   package name `com.afriboost.app`
3. Télécharge `google-services.json` et envoie-le-moi (ou place-le
   directement dans `app/google-services.json` et pousse sur GitHub)
4. Je décommenterai le plugin Google Services dans les fichiers Gradle et
   finaliserai l'intégration (y compris la partie backend afriboost pour
   déclencher les notifications sur changement de statut de commande)

## Ouvrir le projet dans Android Studio (optionnel)

Ce dépôt ne contient pas le wrapper Gradle (`gradlew`) -- Android Studio le
régénère automatiquement à l'ouverture du projet. Si tu préfères tester en
local plutôt que via l'APK GitHub Actions, ouvre simplement ce dossier dans
Android Studio (version récente, supportant compileSdk 36).

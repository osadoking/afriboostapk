// Fichier racine : déclare les plugins utilisés par les sous-modules (ici "app"),
// sans les appliquer ici (apply false). Chaque module les active lui-même.
plugins {
    id("com.android.application") version "8.13.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    // Plugin Google Services (Firebase) : activé seulement une fois que
    // google-services.json sera fourni (voir app/build.gradle.kts).
    id("com.google.gms.google-services") version "4.4.2" apply false
}

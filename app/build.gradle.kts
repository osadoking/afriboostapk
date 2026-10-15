plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // Décommenter cette ligne une fois google-services.json ajouté dans app/ (voir README) :
    // id("com.google.gms.google-services")
}

android {
    namespace = "com.afriboost.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.afriboost.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            // Nom distinct pour pouvoir installer debug et release côte à côte pendant les tests
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.webkit:webkit:1.12.1")

    // Firebase (notifications push) -- actifs seulement une fois google-services.json présent
    // et le plugin com.google.gms.google-services réactivé ci-dessus.
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-messaging-ktx")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}

import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)

}

// Direcciones del backend. Se pueden cambiar en gradle.properties sin tocar el código.
//   Debug (emulador): 10.0.2.2 es el "localhost" del computador.
//   Release: la URL HTTPS de la EC2 (Caddy reparte /api/productos, /api/cotizacion, /api/reservas e /api/insumos).
fun propiedad(nombre: String, porDefecto: String): String =
    providers.gradleProperty(nombre).getOrElse(porDefecto)

val catalogoDebug = propiedad("API_CATALOGO_DEBUG", "http://10.0.2.2:8081/")
val reservasDebug = propiedad("API_RESERVAS_DEBUG", "http://10.0.2.2:8082/")
val cotizacionDebug = propiedad("API_COTIZACION_DEBUG", "http://10.0.2.2:8083/")
val insumosDebug = propiedad("API_INSUMOS_DEBUG", "http://10.0.2.2:8084/")
val apiRelease = propiedad("API_URL_RELEASE", "https://paolicias.duckdns.org/")

// Firma del APK: los datos van en keystore.properties (NO se sube a GitHub).
// Ver keystore.properties.example y el README.
val archivoFirma = rootProject.file("keystore.properties")
val firma = Properties().apply {
    if (archivoFirma.exists()) archivoFirma.inputStream().use { load(it) }
}

android {
    namespace = "com.ban.paoliciassweet"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.ban.paoliciassweet"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (archivoFirma.exists()) {
            create("release") {
                storeFile = rootProject.file(firma.getProperty("storeFile"))
                storePassword = firma.getProperty("storePassword")
                keyAlias = firma.getProperty("keyAlias")
                keyPassword = firma.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        debug {
            buildConfigField("String", "CATALOGO_URL", "\"$catalogoDebug\"")
            buildConfigField("String", "RESERVAS_URL", "\"$reservasDebug\"")
            buildConfigField("String", "COTIZACION_URL", "\"$cotizacionDebug\"")
            buildConfigField("String", "INSUMOS_URL", "\"$insumosDebug\"")
        }
        release {
            buildConfigField("String", "CATALOGO_URL", "\"$apiRelease\"")
            buildConfigField("String", "RESERVAS_URL", "\"$apiRelease\"")
            buildConfigField("String", "COTIZACION_URL", "\"$apiRelease\"")
            buildConfigField("String", "INSUMOS_URL", "\"$apiRelease\"")
            if (archivoFirma.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    // Solo para ProductoRemotoScreen (pantalla de prueba antigua, no se usa en la navegación)
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${libs.versions.lifecycleRuntimeKtx.get()}")
    implementation("androidx.compose.runtime:runtime-livedata")

}

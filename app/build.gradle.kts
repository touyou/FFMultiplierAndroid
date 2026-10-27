import com.github.triplet.gradle.androidpublisher.ReleaseStatus
import com.github.triplet.gradle.androidpublisher.ResolutionStrategy
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.play.publisher)
}

// リリース署名の実体(keystore.properties + keystore 本体)はコミットしない。雛形は keystore.properties.example。
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) {
        keystorePropertiesFile.inputStream().use { load(it) }
    }
}
val hasReleaseSigning = keystorePropertiesFile.exists()

// Play Developer API のサービスアカウント鍵。コミットせず、CI(android-release.yml)が書き出す
val playServiceAccountFile = rootProject.file("play-service-account.json")

android {
    namespace = "com.dev.touyou.ffmultiplier"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.dev.touyou.ffmultiplier"
        // Google Mobile Ads SDK 25.5 以降が API 24 を要求する
        minSdk = 24
        targetSdk = 37
        versionCode = project.property("FF_VERSION_CODE").toString().toInt()
        versionName = project.property("FF_VERSION_NAME").toString()
    }

    if (hasReleaseSigning) {
        signingConfigs {
            create("release") {
                storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            // R8 を有効にしないと Play Console に「DEX コードの最適化」の警告が出る
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

// production への push で内部テストトラックへ上げる。versionCode は Play Console の最新値から自動採番する
play {
    track.set("internal")
    releaseStatus.set(ReleaseStatus.COMPLETED)
    resolutionStrategy.set(ResolutionStrategy.AUTO)
    defaultToAppBundles.set(true)
    // AUTO の採番は assembleRelease にも差し込まれ、鍵が無いと失敗する。鍵の無い環境では GPP ごと無効にする
    enabled.set(playServiceAccountFile.exists())
    if (playServiceAccountFile.exists()) {
        serviceAccountCredentials.set(playServiceAccountFile)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.appcheck.playintegrity)
    // debug ビルドはエミュレータでも通るようデバッグ用プロバイダを使う(src/debug の AppCheckSetup)
    debugImplementation(libs.firebase.appcheck.debug)
    implementation(libs.play.services.ads)
    // 旧版がランキングの key にしていた広告 ID を読む(旧レコードの後始末用)
    implementation(libs.play.services.ads.identifier)
    // Play Services が引き込む古い fragment を Play Console が「古いバージョン」と警告するので、新しい版に揃える。
    implementation(libs.androidx.fragment)
    debugImplementation(libs.androidx.ui.tooling)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}

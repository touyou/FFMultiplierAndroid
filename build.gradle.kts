plugins {
    // AGP 9 以降は built-in Kotlin のため kotlin-android プラグインは不要
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.google.services) apply false
}

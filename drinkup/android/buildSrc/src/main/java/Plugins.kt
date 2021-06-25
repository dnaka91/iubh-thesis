import org.gradle.plugin.use.PluginDependenciesSpec

object Plugins {
    const val android = "com.android.tools.build:gradle:4.2.1"
    const val hilt = "com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}"
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Kotlin.stdlib}"
    const val navigation =
        "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.AndroidX.navigation}"
    const val rust = "gradle.plugin.org.mozilla.rust-android-gradle:plugin:0.8.3"
}

fun PluginDependenciesSpec.android(f: AndroidSpec.() -> Unit = {}) =
    AndroidSpec(this).also(f)

fun PluginDependenciesSpec.kotlin(f: KotlinSpec.() -> Unit = {}) =
    KotlinSpec(this).also(f)

fun PluginDependenciesSpec.rust() {
    id("org.mozilla.rust-android-gradle.rust-android")
}

class AndroidSpec(private val spec: PluginDependenciesSpec) {
    fun application() {
        spec.id("com.android.application")
    }

    fun hilt() {
        spec.id("dagger.hilt.android.plugin")
    }

    fun library() {
        spec.id("com.android.library")
    }

    fun navigation() {
        spec.id("androidx.navigation.safeargs.kotlin")
    }
}

class KotlinSpec(private val spec: PluginDependenciesSpec) {
    fun android() {
        spec.id("kotlin-android")
    }

    fun kapt() {
        spec.id("kotlin-kapt")
    }
}

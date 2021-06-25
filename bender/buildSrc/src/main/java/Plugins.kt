import org.gradle.plugin.use.PluginDependenciesSpec

object Plugins {
    const val android = "com.android.tools.build:gradle:4.2.1"
    const val benchmark =
        "androidx.benchmark:benchmark-gradle-plugin:${Versions.AndroidX.benchmark}"
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Kotlin.stdlib}"
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

    fun benchmark() {
        spec.id("androidx.benchmark")
    }

    fun library() {
        spec.id("com.android.library")
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

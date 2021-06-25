buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }

    dependencies {
        classpath(Plugins.navigation)
        classpath(Plugins.android)
        classpath(Plugins.hilt)
        classpath(Plugins.rust)
        classpath(Plugins.kotlin)
    }
}

subprojects {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

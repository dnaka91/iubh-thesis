buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
    dependencies {
        classpath(Plugins.android)
        classpath(Plugins.benchmark)
        classpath(Plugins.kotlin)
        classpath(Plugins.rust)
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
    delete(rootProject.file("bend/target"))
}

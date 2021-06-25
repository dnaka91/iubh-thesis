plugins {
    idea
    android().library()
    kotlin().android()
    rust()
}

android {
    compileSdkVersion(Android.compileSdk)
    buildToolsVersion(Android.buildTools)

    defaultConfig {
        minSdkVersion(Android.minSdk)
        targetSdkVersion(Android.targetSdk)
        versionCode = Android.versionCode
        versionName = Android.versionName
        ndk {
            abiFilters.addAll(setOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64"))
        }

        testInstrumentationRunner = Android.testRunner
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    ndkVersion = Android.ndk

    sourceSets {
        getByName("main") {
            java.srcDirs(file("$buildDir/generated/source/uniffi/java"))
        }
    }
}

dependencies {
    implementation(Libs.Kotlin.stdlib)
    api(Libs.jna)
    testImplementation(TestLibs.junit)
    androidTestImplementation(TestLibs.AndroidX.junit)
    androidTestImplementation(TestLibs.AndroidX.espresso)
}

cargo {
    module = "../bend/uniffi"
    libname = "uniffi_bend"
    targetDirectory = "../bend/target"
    targets = listOf("arm", "arm64", "x86", "x86_64")
    profile = "release"
}

tasks.whenTaskAdded {
    if (name.startsWith("merge") && name.endsWith("JniLibFolders")) {
        dependsOn("cargoBuild")
    }
}

val t = tasks.register<Exec>("generateUniffiBindings") {
    workingDir = projectDir
    commandLine(
        "uniffi-bindgen",
        "generate",
        rootProject.file("bend/uniffi/src/bend.udl"),
        "--language",
        "kotlin",
        "--out-dir",
        "$buildDir/generated/source/uniffi/java"
    )
}

idea.module.generatedSourceDirs.add(file("$buildDir/generated/source/uniffi/java"))

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    dependsOn(t)
}

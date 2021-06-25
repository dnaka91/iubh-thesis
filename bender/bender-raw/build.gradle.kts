plugins {
    android().library()
    kotlin {
        android()
        kapt()
    }
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
}

dependencies {
    api(Libs.jna)
    implementation(Libs.Kotlin.stdlib)
    implementation(Libs.moshi)
    testImplementation(TestLibs.junit)
    androidTestImplementation(TestLibs.AndroidX.junit)
    androidTestImplementation(TestLibs.AndroidX.espresso)
    kapt(KaptLibs.moshi)
}

cargo {
    module = "../bend/raw"
    libname = "bend_raw"
    targetDirectory = "../bend/target"
    targets = listOf("arm", "arm64", "x86", "x86_64")
    profile = "release"
}

tasks.whenTaskAdded {
    if (name.startsWith("merge") && name.endsWith("JniLibFolders")) {
        dependsOn("cargoBuild")
    }
}

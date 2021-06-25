plugins {
    android {
        benchmark()
        library()
    }
    kotlin().android()
}

android {
    compileSdkVersion(Android.compileSdk)
    buildToolsVersion(Android.buildTools)

    defaultConfig {
        minSdkVersion(Android.minSdk)
        targetSdkVersion(Android.targetSdk)
        versionCode = Android.versionCode
        versionName = Android.versionName

        testInstrumentationRunner = Android.benchmarkRunner
        testInstrumentationRunnerArgument(
            "androidx.benchmark.suppressErrors",
            "UNLOCKED,EMULATOR,ENG-BUILD"
        )
    }

    testBuildType = "release"

    buildTypes {
        getByName("release") {
            isDefault = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "benchmark-proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
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
    implementation(Libs.Kotlin.stdlib)
    testImplementation(TestLibs.junit)
    //androidTestImplementation(project(Projects.j4rs))
    androidTestImplementation(project(Projects.jni))
    androidTestImplementation(project(Projects.kt))
    androidTestImplementation(project(Projects.raw))
    androidTestImplementation(project(Projects.saferffi))
    androidTestImplementation(project(Projects.uniffi))
    androidTestImplementation(TestLibs.AndroidX.benchmark)
    androidTestImplementation(TestLibs.AndroidX.runner)
    androidTestImplementation(TestLibs.AndroidX.junit)
    androidTestImplementation(TestLibs.junit)
}

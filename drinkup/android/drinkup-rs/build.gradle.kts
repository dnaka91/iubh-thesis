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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    ndkVersion = Android.ndk
}

dependencies {
    api(project(Projects.core))
    implementation(Libs.Kotlin.stdlib)
    implementation(Libs.Kotlin.coroutines)
    implementation(Libs.moshi)
    testImplementation(TestLibs.junit)
    androidTestImplementation(TestLibs.AndroidX.junit)
    androidTestImplementation(TestLibs.AndroidX.espresso)
    kapt(KaptLibs.moshi)
    coreLibraryDesugaring(DesugaringLibs.jdk)
}

cargo {
    module = "../../java"
    libname = "drinkup_java"
    targetDirectory = "../../target"
    targets = listOf("arm", "arm64", "x86", "x86_64")
    profile = "release"
}

tasks.whenTaskAdded {
    if (name.startsWith("merge") && name.endsWith("JniLibFolders")) {
        dependsOn("cargoBuild")
    }
}

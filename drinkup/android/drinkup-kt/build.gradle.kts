plugins {
    android().library()
    kotlin {
        android()
        kapt()
    }
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
}

kapt {
    arguments {
        arg("room.incremental", "true")
        arg("room.schemaLocation", file("schemas"))
        arg("room.expandProjection", "true")
    }
}

dependencies {
    api(project(Projects.core))
    api(Libs.AndroidX.Room.ktx)
    api(Libs.splitties)
    implementation(Libs.Kotlin.stdlib)
    implementation(Libs.Kotlin.coroutines)
    implementation(Libs.AndroidX.Room.runtime)
    testImplementation(TestLibs.junit)
    androidTestImplementation(TestLibs.AndroidX.junit)
    androidTestImplementation(TestLibs.AndroidX.espresso)
    kapt(KaptLibs.AndroidX.room)
    coreLibraryDesugaring(DesugaringLibs.jdk)
}

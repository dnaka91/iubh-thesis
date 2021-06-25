plugins {
    android {
        application()
        hilt()
    }
    kotlin {
        android()
        kapt()
    }
    android().navigation()
}

android {
    compileSdkVersion(Android.compileSdk)
    buildToolsVersion(Android.buildTools)

    defaultConfig {
        applicationId = "com.github.dnaka91.drinkup"
        minSdkVersion(Android.minSdk)
        targetSdkVersion(Android.targetSdk)
        versionCode = Android.versionCode
        versionName = Android.versionName
        resConfig("en")

        testInstrumentationRunner = Android.hiltRunner
    }

    flavorDimensions("backend")

    productFlavors {
        register("kotlin") {
            applicationIdSuffix = ".kt"
            dimension = "backend"
        }

        register("rust") {
            applicationIdSuffix = ".rs"
            dimension = "backend"
        }
    }

    buildFeatures {
        viewBinding = true
    }

    signingConfigs {
        val props = signingProps()
        if (props != null) {
            create("release") {
                storeFile(props.storeFile)
                storePassword(props.storePassword)
                keyAlias(props.keyAlias)
                keyPassword(props.keyPassword)
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.findByName("release")
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

    packagingOptions {
        exclude("kotlin/**")
        exclude("META-INF/AL2.0")
        exclude("META-INF/LGPL2.1")
        exclude("META-INF/*.kotlin_module")
        exclude("META-INF/*.version")
    }

    ndkVersion = Android.ndk
}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(project(Projects.core))
    implementation(Libs.Kotlin.stdlib)
    implementation(Libs.Kotlin.coroutines)
    implementation(Libs.AndroidX.appcompat)
    implementation(Libs.AndroidX.constraintlayout)
    implementation(Libs.AndroidX.core)
    implementation(Libs.AndroidX.fragment)
    implementation(Libs.AndroidX.Lifecycle.viewmodel)
    implementation(Libs.AndroidX.Navigation.fragment)
    implementation(Libs.AndroidX.Navigation.ui)
    implementation(Libs.material)
    implementation(Libs.hilt)
    testImplementation(TestLibs.junit)
    androidTestImplementation(TestLibs.AndroidX.runner)
    androidTestImplementation(TestLibs.AndroidX.junit)
    androidTestImplementation(TestLibs.hilt)
    androidTestImplementation(TestLibs.assertk)
    androidTestImplementation(TestLibs.Kotlin.coroutines)
    kapt(KaptLibs.hilt)
    kaptAndroidTest(KaptLibs.hilt)
    coreLibraryDesugaring(DesugaringLibs.jdk)

    "kotlinImplementation"(project(Projects.kotlin))
    "rustImplementation"(project(Projects.rust))
}

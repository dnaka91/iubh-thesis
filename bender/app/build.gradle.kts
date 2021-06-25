plugins {
    android().application()
    kotlin().android()
}

android {
    compileSdkVersion(Android.compileSdk)
    buildToolsVersion(Android.buildTools)

    defaultConfig {
        applicationId = "com.github.dnaka91.bender"
        minSdkVersion(Android.minSdk)
        targetSdkVersion(Android.targetSdk)
        versionCode = Android.versionCode
        versionName = Android.versionName
        resConfig("en")
        ndk {
            abiFilters.addAll(setOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64"))
        }

        testInstrumentationRunner = Android.testRunner
    }

    flavorDimensions("binding")

    productFlavors {
        register("j4rs") {
            applicationIdSuffix = ".j4rs"
            dimension = "binding"
        }

        register("jni") {
            applicationIdSuffix = ".jni"
            dimension = "binding"
        }

        register("kt") {
            applicationIdSuffix = ".kt"
            dimension = "binding"
        }

        register("raw") {
            applicationIdSuffix = ".raw"
            dimension = "binding"
        }

        register("saferffi") {
            applicationIdSuffix = ".saferffi"
            dimension = "binding"
        }

        register("uniffi") {
            applicationIdSuffix = ".uniffi"
            dimension = "binding"
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
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
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

dependencies {
    implementation(Libs.Kotlin.stdlib)
    implementation(Libs.AndroidX.appcompat)
    implementation(Libs.AndroidX.constraintlayout)
    implementation(Libs.AndroidX.core)
    implementation(Libs.AndroidX.viewpager2)
    implementation(Libs.material)
    testImplementation(TestLibs.junit)
    androidTestImplementation(TestLibs.AndroidX.junit)
    androidTestImplementation(TestLibs.AndroidX.espresso)

    "j4rsImplementation"(project(Projects.j4rs))
    "jniImplementation"(project(Projects.jni))
    "ktImplementation"(project(Projects.kt))
    "rawImplementation"(project(Projects.raw))
    "saferffiImplementation"(project(Projects.saferffi))
    "uniffiImplementation"(project(Projects.uniffi))
}

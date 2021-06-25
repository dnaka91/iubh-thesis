object Libs {
    const val hilt = "com.google.dagger:hilt-android:${Versions.hilt}"
    const val material = "com.google.android.material:material:${Versions.material}"
    const val moshi = "com.squareup.moshi:moshi:${Versions.moshi}"
    const val splitties = "com.louiscad.splitties:splitties-preferences:${Versions.splitties}"

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:${Versions.AndroidX.appcompat}"
        const val constraintlayout =
            "androidx.constraintlayout:constraintlayout:${Versions.AndroidX.constraintlayout}"
        const val core = "androidx.core:core-ktx:${Versions.AndroidX.core}"
        const val fragment = "androidx.fragment:fragment-ktx:${Versions.AndroidX.fragment}"


        object Lifecycle {
            const val viewmodel =
                "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.AndroidX.lifecycle}"
        }

        object Navigation {
            const val fragment =
                "androidx.navigation:navigation-fragment-ktx:${Versions.AndroidX.navigation}"
            const val ui = "androidx.navigation:navigation-ui-ktx:${Versions.AndroidX.navigation}"
        }

        object Room {
            const val ktx = "androidx.room:room-ktx:${Versions.AndroidX.room}"
            const val runtime = "androidx.room:room-runtime:${Versions.AndroidX.room}"
        }
    }

    object Kotlin {
        const val coroutines =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Kotlin.coroutines}"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.Kotlin.stdlib}"
    }
}

object TestLibs {
    const val assertk = "com.willowtreeapps.assertk:assertk-jvm:${Versions.assertk}"
    const val hilt = "com.google.dagger:hilt-android-testing:${Versions.hilt}"
    const val junit = "junit:junit:${Versions.junit}"

    object AndroidX {
        const val espresso = "androidx.test.espresso:espresso-core:${Versions.AndroidX.espresso}"
        const val junit = "androidx.test.ext:junit:${Versions.AndroidX.junit}"
        const val runner = "androidx.test:runner:${Versions.AndroidX.runner}"
    }

    object Kotlin {
        const val coroutines =
            "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.Kotlin.coroutines}"
    }
}

object KaptLibs {
    const val hilt = "com.google.dagger:hilt-compiler:${Versions.hilt}"
    const val moshi = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}"

    object AndroidX {
        const val room = "androidx.room:room-compiler:${Versions.AndroidX.room}"
    }
}

object DesugaringLibs {
    const val jdk = "com.android.tools:desugar_jdk_libs:${Versions.Desugaring.jdk}"
}

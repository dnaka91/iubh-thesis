object Libs {
    const val j4rs = "io.github.astonbitecode:j4rs:${Versions.j4rs}"
    const val jna = "net.java.dev.jna:jna:${Versions.jna}@aar"
    const val material = "com.google.android.material:material:${Versions.material}"
    const val math3 = "org.apache.commons:commons-math3:${Versions.math3}"
    const val moshi = "com.squareup.moshi:moshi:${Versions.moshi}"

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:${Versions.AndroidX.appcompat}"
        const val constraintlayout =
            "androidx.constraintlayout:constraintlayout:${Versions.AndroidX.constraintlayout}"
        const val core = "androidx.core:core-ktx:${Versions.AndroidX.core}"
        const val viewpager2 = "androidx.viewpager2:viewpager2:${Versions.AndroidX.viewpager2}"
    }

    object Kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.Kotlin.stdlib}"
    }
}

object TestLibs {
    const val junit = "junit:junit:${Versions.junit}"

    object AndroidX {
        const val benchmark = "androidx.benchmark:benchmark-junit4:${Versions.AndroidX.benchmark}"
        const val espresso = "androidx.test.espresso:espresso-core:${Versions.AndroidX.espresso}"
        const val junit = "androidx.test.ext:junit:${Versions.AndroidX.junit}"
        const val runner = "androidx.test:runner:${Versions.AndroidX.runner}"
    }
}

object KaptLibs {
    const val moshi = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}"
}
import org.gradle.api.Project
import java.io.File
import java.util.*

data class Signing(
    val storeFile: File,
    val storePassword: String,
    val keyAlias: String,
    val keyPassword: String
)

private val Project.localProperties: File
    get() = rootProject.file("local.properties")

fun Project.signingProps(): Signing? {
    if (!localProperties.exists()) return null

    val props = Properties().also { it.load(localProperties.inputStream()) }

    val storeFile = props["storeFile"] as String?
    val storePassword = props["storePassword"] as String?
    val keyAlias = props["keyAlias"] as String?
    val keyPassword = props["keyPassword"] as String?

    return if (
        storeFile != null &&
        storePassword != null &&
        keyAlias != null &&
        keyPassword != null
    ) {
        Signing(rootProject.file(storeFile), storePassword, keyAlias, keyPassword)
    } else {
        null
    }
}

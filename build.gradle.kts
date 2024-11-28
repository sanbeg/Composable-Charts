import org.jetbrains.dokka.gradle.DokkaTaskPartial
import java.net.URL

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.jetbrains.dokka) apply true
}

subprojects {
    apply(plugin = "org.jetbrains.dokka")
}

subprojects {
    tasks.withType<DokkaTaskPartial>().configureEach {
        dokkaSourceSets.configureEach {
            // contains descriptions for the module and the packages
            includes.from("Module.md")

            sourceLink {
                localDirectory.set(rootProject.projectDir)
                remoteUrl.set(URL("https://github.com/sanbeg/Composable-Charts/tree/main/"))
            }

            externalDocumentationLink {
                url.set(URL("https://developer.android.com/reference/kotlin/"))
                packageListUrl.set(URL("https://developer.android.com/reference/kotlin/androidx/package-list"))
            }
        }
    }
}
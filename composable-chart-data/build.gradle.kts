plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    dokkaPlugin(libs.android.documentation.plugin)

    compileOnly(libs.compose.stable.marker)

    implementation(platform(libs.androidx.compose.bom))
    // implementation(libs.androidx.ui)

    testImplementation(libs.junit)
}
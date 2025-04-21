plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    `maven-publish`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    withSourcesJar()
    withJavadocJar()
}

dependencies {
    dokkaPlugin(libs.android.documentation.plugin)

    compileOnly(libs.compose.stable.marker)

    implementation(platform(libs.androidx.compose.bom))

    testImplementation(libs.junit)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.sanbeg"
            artifactId = "composable-chart-data"
            version = "0.1.0"

            from(components["java"])
        }
    }
}
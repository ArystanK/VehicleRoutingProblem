import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.8.0"
}

group = "kz.arctan"
version = "1.0-SNAPSHOT"
val ktorVersion: String by project
val exposedVersion: String by project
repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://maven.pkg.jetbrains.space/spc/p/sci/maven")
    maven("https://repo.kotlin.link")
}


kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("center.sciprog:maps-kt-compose:0.2.2")
                implementation("space.kscience:trajectory-kt:0.2.2")

                implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
                implementation("io.ktor:ktor-client-cio-jvm:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")
                implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
                implementation("com.google.ortools:ortools-java:9.6.2534")

                implementation("org.jetbrains.lets-plot:lets-plot-jfx:3.2.0")
                implementation("org.jetbrains.lets-plot:lets-plot-kotlin-jvm:4.4.1")

                implementation("org.openjfx:javafx-base:17:win")
                implementation("org.openjfx:javafx-swing:17:win")
                implementation("org.openjfx:javafx-graphics:17:win")

                implementation("org.slf4j:slf4j-api:1.7.25")
                implementation("com.squareup.okhttp3:okhttp:4.9.2")

                runtimeOnly("org.postgresql:postgresql:42.2.27")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "VehicleRoutingProblem"
            packageVersion = "1.0.0"
        }
    }
}

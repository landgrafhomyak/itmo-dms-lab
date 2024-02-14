plugins {
    kotlin("multiplatform")
}

val kotlinxCoroutinesVersion: String by project


kotlin {
    jvm {
        jvmToolchain(8)
        withJava()
    }
    js()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")

                implementation(project(":modules:cli-args-parser"))
                implementation(project(":modules:entity"))
                implementation(project(":modules:console"))
                implementation(project(":modules:console:default-engine"))
                implementation(project(":modules:console:ansi-styling"))
                implementation(project(":modules:storage-client-layer"))
                implementation(project(":modules:storage-client-layer:local-copy"))
                implementation(project(":modules:command"))
                implementation(project(":modules:command:universal"))

                implementation(project(":models:demo"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
    }
}

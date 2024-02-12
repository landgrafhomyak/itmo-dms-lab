plugins {
    kotlin("multiplatform")
}

val kotlinxCoroutinesVersion: String by project

kotlin {
    jvm {
        jvmToolchain(8)
        withJava()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
                implementation(project(":modules:entity"))
                implementation(project(":modules:storage-client-layer"))

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

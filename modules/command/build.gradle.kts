plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm {
        jvmToolchain(8)
        withJava()
    }
    js()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":modules:entity"))
                implementation(project(":modules:console"))
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

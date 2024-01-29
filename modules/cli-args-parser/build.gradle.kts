plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm {
        jvmToolchain(8)
        withJava()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":modules:utility"))
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

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
                implementation(project(":modules:command"))
                implementation(project(":modules:command:universal"))
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

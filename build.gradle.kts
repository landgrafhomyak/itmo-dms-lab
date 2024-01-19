plugins {
    kotlin("multiplatform") version "1.9.0" apply false
}


subprojects {
    repositories {
        mavenCentral()
        maven("https://maven.landgrafhomyak.ru/")
    }
}


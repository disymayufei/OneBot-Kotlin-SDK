val kotlin_version: String by project
val kotlinx_coroutines_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.9.22"
}

group = "cn.disy920"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$kotlinx_coroutines_version")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.17.0")
    implementation("ch.qos.logback:logback-classic:$logback_version")
}

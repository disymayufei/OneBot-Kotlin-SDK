import java.util.*

val kotlin_version: String by project
val kotlinx_coroutines_version: String by project
val logback_version: String by project

val localProperties = File(rootProject.projectDir, "local.properties")
val properties = Properties()
if (localProperties.exists()) {
    localProperties.inputStream().use { properties.load(it) }
}


plugins {
    kotlin("jvm") version "1.9.22"
    `maven-publish`
}

repositories {
    mavenCentral()
}

group = "cn.disy920"
version = "1.0.6"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$kotlinx_coroutines_version")
    implementation("org.yaml:snakeyaml:2.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.17.0")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.0")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "cn.disy920.onebot"
            artifactId = "okapi"
            this.version = version

            from(components["java"])

            artifact(sourceJarTask())
        }
    }
    repositories {
        maven {
            url = uri(properties.getProperty("artifactory_url") ?: System.getenv("ARTIFACTORY_URL"))
            credentials {
                username = properties.getProperty("artifactory_user") ?: System.getenv("ARTIFACTORY_USER")
                password = properties.getProperty("artifactory_password") ?: System.getenv("ARTIFACTORY_PASSWORD")
            }
        }
    }
}

fun sourceJarTask() = tasks.create("sourceJar", Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}


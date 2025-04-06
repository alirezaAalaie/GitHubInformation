plugins {
    kotlin("jvm") version "1.9.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    implementation(kotlin("stdlib"))

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.litote.kmongo:kmongo-coroutine:4.10.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")


    implementation("io.insert-koin:koin-core:3.4.3")

    implementation("ch.qos.logback:logback-classic:1.4.11")
    implementation("io.github.microutils:kotlin-logging:3.0.5")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

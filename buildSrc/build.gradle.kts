import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

val currentJvmTarget = "17"

repositories {
    mavenCentral()
    google()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = currentJvmTarget
}

tasks.withType<JavaCompile> {
    sourceCompatibility = currentJvmTarget
    targetCompatibility = currentJvmTarget
}

dependencies {
    implementation(libs.androidToolsBuildGradle)
    implementation(libs.kotlinGradle)
    implementation(libs.javapoet)
}
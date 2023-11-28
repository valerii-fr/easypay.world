buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        google()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
            content {
                includeModule(
                    libs.hiltAndroidGradle.get().group!!,
                    libs.hiltAndroidGradle.get().name
                )
            }
        }
    }

    dependencies {
        classpath(libs.androidToolsBuildGradle)
        classpath(libs.kotlinGradle)
        classpath(libs.hiltAndroidGradle)
    }
}
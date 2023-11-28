package fr.valerii.gradle

import org.gradle.api.JavaVersion
import java.io.File
import java.util.Properties

data class ProguardFileGeneralProperties(
    val consumerProguardFile: String,
    val defaultProguardFile: String,
    val additionalProguardFiles: String,
) {
    companion object {
        val DEFAULT = ProguardFileGeneralProperties(
            consumerProguardFile = "consumer-rules.pro",
            defaultProguardFile = "proguard-android-optimize.txt",
            additionalProguardFiles = "proguard-rules.pro",
        )
    }
}

data class RoomExportSchemeProperties(
    val enabled: Boolean,
    val projectDirForRoomSchemaLocation: File?,
) {

    constructor(projectDirForRoomSchemaLocation: File) : this(true, projectDirForRoomSchemaLocation)

    companion object DEFAULT {
        val DEFAULT = RoomExportSchemeProperties(
            enabled = false,
            projectDirForRoomSchemaLocation = null,
        )
    }
}

data class SdkVersionProperties(
    val compileSdk: Int,
    val minSdk: Int,
    val javaVersion: JavaVersion = JavaVersion.VERSION_17,
    val jvmTarget: String,
    val buildToolsVersion: String,
) {
    companion object {
        val DEFAULT = SdkVersionProperties(
            compileSdk = 33,
            minSdk = 22,
            javaVersion = JavaVersion.VERSION_17,
            jvmTarget = "17",
            buildToolsVersion = "33.0.2"
        )
    }
}

data class AppInfoProperties(
    val applicationId: String,
    val versionCode: Int?,
    val versionName: String?,
    val branch: String,
    val commitId: String,
    val properties: Properties,
    val storeFile: File,
)

data class MinifyProperties(
    val isMinifyEnabledInRelease: Boolean,
    val isMinifyEnabledInDebug: Boolean,
) {
    companion object {
        val DEFAULT = MinifyProperties(
            isMinifyEnabledInRelease = false,
            isMinifyEnabledInDebug = false,
        )
    }
}

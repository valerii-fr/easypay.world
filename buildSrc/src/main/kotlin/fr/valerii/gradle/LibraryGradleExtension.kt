package fr.valerii.gradle

import com.android.build.api.dsl.LibraryDefaultConfig
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion

object LibraryGradleExtension {

    fun LibraryExtension.applyDefaultConfig(
        namespace: String,
        sdkVersionProperties: SdkVersionProperties = SdkVersionProperties.DEFAULT,
        proguardFileGeneralSettings: ProguardFileGeneralProperties = ProguardFileGeneralProperties.DEFAULT,
        roomExportSchemeProperties: RoomExportSchemeProperties = RoomExportSchemeProperties.DEFAULT,
        minifyProperties: MinifyProperties = MinifyProperties.DEFAULT,
        desugaringEnabled: Boolean = false,
        taskInDefaultConfig: LibraryDefaultConfig.() -> Unit = {},
    ) {
        this.namespace = namespace
        compileSdk = sdkVersionProperties.compileSdk

        applyDefaultConfig(
            sdkVersionProperties,
            proguardFileGeneralSettings,
            roomExportSchemeProperties,
            taskInDefaultConfig
        )
        applyBuildTypes(minifyProperties, proguardFileGeneralSettings)
        applyCompileOptions(desugaringEnabled, sdkVersionProperties.javaVersion)
    }

    private fun LibraryExtension.applyDefaultConfig(
        sdkVersionProperties: SdkVersionProperties,
        proguardFileGeneralSettings: ProguardFileGeneralProperties,
        roomExportSchemeProperties: RoomExportSchemeProperties,
        taskInDefaultConfig: LibraryDefaultConfig.() -> Unit
    ) {
        defaultConfig {
            minSdk = sdkVersionProperties.minSdk
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            consumerProguardFiles(proguardFileGeneralSettings.consumerProguardFile)

            if (roomExportSchemeProperties.enabled
                && roomExportSchemeProperties.projectDirForRoomSchemaLocation != null
            ) {
                javaCompileOptions {
                    annotationProcessorOptions {
                        arguments += mapOf(
                            "room.schemaLocation"
                                    to "${roomExportSchemeProperties.projectDirForRoomSchemaLocation}/schemas"
                        )
                    }
                }
            }

            taskInDefaultConfig()
        }
    }

    private fun LibraryExtension.applyBuildTypes(
        minifyProperties: MinifyProperties,
        proguardFileGeneralSettings: ProguardFileGeneralProperties,
    ) {
        buildTypes {
            release {
                isMinifyEnabled = minifyProperties.isMinifyEnabledInRelease
                proguardFiles(
                    getDefaultProguardFile(proguardFileGeneralSettings.defaultProguardFile),
                    proguardFileGeneralSettings.additionalProguardFiles
                )
            }
            debug {
                isMinifyEnabled = minifyProperties.isMinifyEnabledInDebug
            }
        }
    }

    private fun LibraryExtension.applyCompileOptions(
        desugaringEnabled: Boolean,
        javaVersion: JavaVersion,
    ) {
        compileOptions {
            isCoreLibraryDesugaringEnabled = desugaringEnabled
            sourceCompatibility = javaVersion
            targetCompatibility = javaVersion
        }
    }

    fun LibraryExtension.applyComposeConfig(
        versionCompose: String
    ) {
        buildFeatures {
            compose = true
        }
        composeOptions {
            kotlinCompilerExtensionVersion = versionCompose
        }
    }
}

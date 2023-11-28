package fr.valerii.gradle

import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.JavaVersion

@Suppress("unused")
object AppGradleExtension {

    @Suppress("unused_parameter")
    fun BaseAppModuleExtension.applyDefaultConfig(
        namespace: String,
        appInfoProperties: AppInfoProperties? = null,
        sdkVersionProperties: SdkVersionProperties = SdkVersionProperties.DEFAULT,
        proguardFileGeneralSettings: ProguardFileGeneralProperties = ProguardFileGeneralProperties.DEFAULT,
        roomExportSchemeProperties: RoomExportSchemeProperties = RoomExportSchemeProperties.DEFAULT,
        minifyProperties: MinifyProperties = MinifyProperties.DEFAULT,
        desugaringEnabled: Boolean = false,
        taskInDefaultConfig: ApplicationDefaultConfig.() -> Unit = {},
    ) {
        this.namespace = namespace
        compileSdk = sdkVersionProperties.compileSdk
        buildToolsVersion = sdkVersionProperties.buildToolsVersion

//        applyBuildSigningConfigs(appInfoProperties)
//        applyDefaultConfig(appInfoProperties, sdkVersionProperties, roomExportSchemeProperties, taskInDefaultConfig)
        applyBuildTypesOptions(minifyProperties, proguardFileGeneralSettings)
        applyCompileOptions(desugaringEnabled, sdkVersionProperties.javaVersion)
        applyPackagingOptions()
        applyBundleOptions()
        applyLintOptions()
    }

    private fun BaseAppModuleExtension.applyBuildSigningConfigs(
        appInfoProperties: AppInfoProperties,
    ) {
        signingConfigs {
            create("release") {
                storeFile = appInfoProperties.storeFile
                storePassword = appInfoProperties.properties.getProperty("store.password")
                keyAlias = appInfoProperties.properties.getProperty("store.key-alias")
                keyPassword = appInfoProperties.properties.getProperty("store.key-password")
            }
        }
    }

    private fun BaseAppModuleExtension.applyBuildTypesOptions(
        minifyProperties: MinifyProperties,
        proguardFileGeneralSettings: ProguardFileGeneralProperties
    ) {
        buildTypes {
            release {
                this.isMinifyEnabled = minifyProperties.isMinifyEnabledInRelease
                proguardFiles(
                    getDefaultProguardFile(proguardFileGeneralSettings.defaultProguardFile),
                    proguardFileGeneralSettings.additionalProguardFiles
                )
                signingConfig = signingConfigs.findByName("release")
            }
            debug {
                applicationIdSuffix = ".debug"
                this.isMinifyEnabled = minifyProperties.isMinifyEnabledInDebug
                signingConfig = signingConfigs.findByName("release")
            }
        }
    }

    private fun BaseAppModuleExtension.applyDefaultConfig(
        appInfoProperties: AppInfoProperties,
        sdkVersionProperties: SdkVersionProperties,
        roomExportSchemeProperties: RoomExportSchemeProperties,
        taskInDefaultConfig: ApplicationDefaultConfig.() -> Unit
    ) {
        defaultConfig {
            applicationId = appInfoProperties.applicationId
            minSdk = sdkVersionProperties.minSdk
            targetSdk = sdkVersionProperties.compileSdk
            versionCode = appInfoProperties.versionCode
            versionName = appInfoProperties.versionName
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            signingConfig = signingConfigs.findByName("release")
            manifestPlaceholders["GIT_BRANCH"] = appInfoProperties.branch
            manifestPlaceholders["GIT_COMMIT"] = appInfoProperties.commitId
            buildConfigField("String", "PREFS_VERSION", "\"1\"")
            buildConfigField("String", "GIT_BRANCH", "\"${appInfoProperties.branch}\"")
            buildConfigField("String", "GIT_COMMIT", "\"${appInfoProperties.commitId}\"")

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

    private fun BaseAppModuleExtension.applyBundleOptions() {
        bundle {
            language {
                enableSplit = false
            }
        }
    }

    private fun BaseAppModuleExtension.applyLintOptions() {
        lint {
            disable.add("Instantiatable")
        }
    }

    private fun BaseAppModuleExtension.applyPackagingOptions() {
        packaging {
            resources.excludes.add("META-INF/DEPENDENCIES")
            resources.excludes.add("META-INF/INDEX.LIST")
            resources.excludes.add("META-INF/kotlinx_coroutines_core.version")
            resources.excludes.add("META-INF/LICENSE")
            resources.excludes.add("META-INF/LICENSE.txt")
            resources.excludes.add("META-INF/LICENSE.md")
            resources.excludes.add("META-INF/LICENSE-notice.md")
            resources.excludes.add("META-INF/license.txt")
            resources.excludes.add("META-INF/NOTICE")
            resources.excludes.add("META-INF/NOTICE.txt")
            resources.excludes.add("META-INF/notice.txt")
            resources.excludes.add("META-INF/ASL2.0")
            resources.excludes.add("META-INF/*.kotlin_module")
            resources.excludes.add("about.*")
            resources.excludes.add("logback.xml")
            resources.excludes.add("modeling32.png")
            resources.excludes.add("plugin.properties")
            resources.excludes.add("javamoney.properties")
        }
    }

    private fun BaseAppModuleExtension.applyCompileOptions(
        desugaringEnabled: Boolean,
        javaVersion: JavaVersion,
    ) {
        compileOptions {
            isCoreLibraryDesugaringEnabled = desugaringEnabled
            sourceCompatibility = javaVersion
            targetCompatibility = javaVersion
        }
    }

    fun BaseAppModuleExtension.applyComposeConfig(
        versionCompose: String
    ) {
        buildFeatures {
            compose = true
        }
        composeOptions {
            kotlinCompilerExtensionVersion = versionCompose
        }
    }

    inline fun <reified ValueT> com.android.build.api.dsl.VariantDimension.buildConfigField(
        name: String,
        value: ValueT
    ) {
        val resolvedValue = when (value) {
            is String -> "\"$value\"" // hate this
            else -> value
        }.toString()
        buildConfigField(ValueT::class.java.simpleName, name, resolvedValue)
    }
}

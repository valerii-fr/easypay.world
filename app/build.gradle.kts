import fr.valerii.gradle.AppGradleExtension.applyComposeConfig
import fr.valerii.gradle.GradleExtension.getGeneralKotlinConfigure
import fr.valerii.gradle.AppGradleExtension.buildConfigField

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(libs.plugins.androidApp.get().pluginId)
    id(libs.plugins.kotlinAndroid.get().pluginId)
    id(libs.plugins.kotlinKapt.get().pluginId)
    id(libs.plugins.hilt.get().pluginId)
}

android {
    namespace = "fr.valerii.aeon"
    compileSdk = 34
    buildFeatures.buildConfig = true
    defaultConfig {
        applicationId = "fr.valerii.aeon"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField(
            name = "BASE_URL",
            value = "https://easypay.world/"
        )
        buildConfigField(
            name = "APP_KEY",
            value = "12345"
        )
        buildConfigField(
            name = "API_V",
            value = "1"
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    applyComposeConfig(
        versionCompose = libs.versions.compose.get()
    )
    kotlinOptions(
        configure = getGeneralKotlinConfigure()
    )
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

hilt {
    enableAggregatingTask = true
}

dependencies {
    implementation(platform(libs.compose.bom))
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.bundles.compose)
    implementation(libs.hilt.navigation.compose) {
        exclude(group = "androidx.navigation", module = "navigation-compose")
    }
    implementation(libs.bundles.activity)
    implementation(libs.bundles.lifecycle)
    implementation(libs.dagger)
    kapt(libs.daggerCompiler)
    kapt(libs.hiltCompiler)
    implementation(libs.core)
    coreLibraryDesugaring(libs.desugar)
    implementation(libs.material)
    implementation(libs.kotlinJson)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.room)
    kapt(libs.roomCompiler)
    implementation(libs.composeAnnotation)
    implementation(libs.gson)
}

kapt {
    correctErrorTypes = true
}

androidComponents.onVariants {variant ->
    kotlin.sourceSets.findByName(variant.name)?.kotlin?.srcDirs(
        file("$buildDir/generated/ksp/${variant.name}/kotlin")
    )
}
